/*
 * Copyright  2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.kandula.initiator;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.InvalidStateException;
import org.apache.kandula.faults.KandulaGeneralException;
import org.apache.kandula.storage.StorageFactory;
import org.apache.kandula.storage.Store;
import org.apache.kandula.utility.EndpointReferenceFactory;
import org.apache.kandula.wsat.completion.CompletionCoordinatorPortTypeRawXMLStub;
import org.apache.kandula.wscoor.ActivationCoordinatorPortTypeRawXMLStub;
import org.apache.kandula.wscoor.RegistrationCoordinatorPortTypeRawXMLStub;

/**
 * @author Dasarath Weeratunge
 * @author <a href="mailto:thilina@apache.org"> Thilina Gunarathne </a>
 */

public class TransactionManager {

	private static ThreadLocal threadInfo;

	private ConfigurationContext configurationContext;

	public TransactionManager(String coordinationType,
			EndpointReference coordinatorEPR, String axis2Home, String axis2Xml)
			throws AbstractKandulaException {

		try {
			configurationContext = ConfigurationContextFactory
					.createConfigurationContextFromFileSystem(axis2Home,
							axis2Xml);
		} catch (DeploymentException e) {
			throw new KandulaGeneralException(e);
		} catch (AxisFault e1) {
			throw new KandulaGeneralException(e1);
		}

		threadInfo = new ThreadLocal();
		InitiatorTransaction initiatorTransaction = new InitiatorTransaction(
				coordinationType, coordinatorEPR);
		if (threadInfo.get() != null)
			throw new IllegalStateException();
		threadInfo.set(initiatorTransaction.getRequesterID());
		Store store = StorageFactory.getInstance().getInitiatorStore();
		store.put(initiatorTransaction.getRequesterID(), initiatorTransaction);
	}

	public void begin() throws Exception {
		begin(false);
	}

	/**
	 * @throws Exception
	 */
	public void begin(boolean async) throws Exception {

		InitiatorTransaction initiatorTransaction = getTransaction();
		String id = initiatorTransaction.getRequesterID();
		ActivationCoordinatorPortTypeRawXMLStub activationCoordinator = new ActivationCoordinatorPortTypeRawXMLStub(
				configurationContext, initiatorTransaction.getActivationEPR());
		activationCoordinator.createCoordinationContextOperation(
				initiatorTransaction, async);
		while (async & initiatorTransaction.getCoordinationContext() == null) {
			// allow other threads to execute
			Thread.sleep(10);
		}
		RegistrationCoordinatorPortTypeRawXMLStub registrationCoordinator = new RegistrationCoordinatorPortTypeRawXMLStub(
				configurationContext, initiatorTransaction
						.getCoordinationContext().getRegistrationService());
		// TODO make this unaware of the protocol
		EndpointReference registrationRequeterPortEPR = EndpointReferenceFactory
				.getInstance().getCompletionInitiatorEndpoint(id);
		registrationCoordinator.registerOperation(Constants.WS_AT_COMPLETION,
				initiatorTransaction.getRequesterID(),
				registrationRequeterPortEPR, async);
		while (async & initiatorTransaction.getCoordinationEPR() == null) {
			Thread.sleep(10);
		}
	}

	public void commit() throws Exception {
		InitiatorTransaction initiatorTransaction = getTransaction();
		EndpointReference coordinationEPR = initiatorTransaction
				.getCoordinationEPR();
		CompletionCoordinatorPortTypeRawXMLStub stub = new CompletionCoordinatorPortTypeRawXMLStub(
				configurationContext, coordinationEPR);
		stub.commitOperation();
		while ((initiatorTransaction.getStatus() != Status.CoordinatorStatus.STATUS_COMMITTING)
				& (initiatorTransaction.getStatus() != Status.CoordinatorStatus.STATUS_ABORTING)) {
			Thread.sleep(10);
		}
		if ((initiatorTransaction.getStatus() == Status.CoordinatorStatus.STATUS_ABORTING)) {
			throw new Exception("Aborted");
		}
		forgetTransaction();
	}

	public void rollback() throws Exception {
		InitiatorTransaction initiatorTransaction = getTransaction();
		EndpointReference coordinationEPR = initiatorTransaction
				.getCoordinationEPR();
		CompletionCoordinatorPortTypeRawXMLStub stub = new CompletionCoordinatorPortTypeRawXMLStub(
				configurationContext, coordinationEPR);
		stub.rollbackOperation();
		forgetTransaction();
	}

	// public Transaction suspend() {
	// Transaction tx= getTransaction();
	// forget();
	// return tx;
	// }
	//
	// public void resume(Transaction tx) {
	// if (threadInfo.get() != null)
	// throw new IllegalStateException();
	// else
	// threadInfo.set(tx);
	// }
	//
	// public void forget() {
	// threadInfo.set(null);
	// }

	public static InitiatorTransaction getTransaction()
			throws AbstractKandulaException {
		Object key = threadInfo.get();
		InitiatorTransaction context = (InitiatorTransaction) StorageFactory
				.getInstance().getInitiatorStore().get(key);
		if (context == null) {
			throw new InvalidStateException("No Activity Found");
		}
		return context;
	}

	public static void forgetTransaction() {
		threadInfo.set(null);
	}
}
