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

import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.context.CoordinationContext;
import org.apache.kandula.context.impl.ADBCoordinationContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.KandulaGeneralException;
import org.apache.kandula.utility.EndpointReferenceFactory;
import org.apache.kandula.wsat.completion.CompletionCoordinatorPortTypeRawXMLStub;
import org.apache.kandula.wsat.completion.CompletionInitiatorServiceListener;
import org.apache.kandula.wscoor.ActivationServiceStub;
import org.apache.kandula.wscoor.RegistrationServiceStub;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CreateCoordinationContext;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CreateCoordinationContextResponse;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CreateCoordinationContextResponseType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CreateCoordinationContextType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.Register;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.RegisterResponse;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.RegisterType;

public class TransactionManager {
	
	private static ThreadLocal threadInfo = new ThreadLocal();
	private ConfigurationContext configurationContext;

	public TransactionManager(String axis2Home, String axis2Xml)
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
	}

	/**
	 * @throws Exception
	 */
	public void begin(String coordinatorAddress) throws Exception {

		if (threadInfo.get()!=null)
		{
			throw new IllegalStateException();
		}
		InitiatorContext initiatorTransaction = new InitiatorContext(Constants.WS_AT,coordinatorAddress);
		CoordinationContext coordinationContext = createTransaction(initiatorTransaction);
		initiatorTransaction.setCoordinationContext(coordinationContext);
		threadInfo.set(initiatorTransaction);
	}



	public void commit() throws Exception {
		InitiatorContext initiatorTransaction = getTransaction();
		CompletionCallback completionCallback = new CompletionCallback(initiatorTransaction);
		// Register for completion
		EndpointReference coordinationEPR = registerForCompletion(initiatorTransaction,completionCallback);
		initiatorTransaction.setCoordinationEPR(coordinationEPR);		

		CompletionCoordinatorPortTypeRawXMLStub stub = new CompletionCoordinatorPortTypeRawXMLStub(
				configurationContext, coordinationEPR);
		stub.commitOperation();
		while (!completionCallback.isComplete())
			Thread.sleep(10);
			
		if ((completionCallback.getResult() == Status.CoordinatorStatus.STATUS_ABORTING)) {
			forgetTransaction();
			throw new Exception("Aborted");
		}
		forgetTransaction();
	}

	public void rollback() throws Exception {
		InitiatorContext initiatorTransaction = getTransaction();		
		// Register for completion
		CompletionCallback completionCallback = new CompletionCallback(initiatorTransaction);
		// Register for completion
		EndpointReference coordinationEPR = registerForCompletion(initiatorTransaction,completionCallback);
		initiatorTransaction.setCoordinationEPR(coordinationEPR);	
		CompletionCoordinatorPortTypeRawXMLStub stub = new CompletionCoordinatorPortTypeRawXMLStub(
				configurationContext, coordinationEPR);
		stub.rollbackOperation();
		while (!completionCallback.isComplete())
			Thread.sleep(10);
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

	private static InitiatorContext getTransaction()
			throws AbstractKandulaException {
		Object key = threadInfo.get();
		if (key!= null)
		{
			return (InitiatorContext)key;
		}
		return null;
	}

	public static void forgetTransaction() {
		threadInfo.set(null);
	}
	
	private EndpointReference registerForCompletion(InitiatorContext initiatorTransaction, CompletionCallback completionCallback) throws AxisFault, IOException, MalformedURIException, RemoteException {
		RegistrationServiceStub registrationCoordinator = new RegistrationServiceStub(
				configurationContext,null);
		registrationCoordinator._getServiceClient().setTargetEPR(initiatorTransaction
				.getCoordinationContext().getRegistrationService());
		//setup the listener
		CompletionInitiatorServiceListener listener = new CompletionInitiatorServiceListener();
		EndpointReference registrationRequeterPortEPR =listener.getEpr(completionCallback);
		
		Register register = new Register();
		RegisterType registerType = new RegisterType();
		registerType.setProtocolIdentifier(new URI(Constants.WS_AT_COMPLETION));
		registerType.setParticipantProtocolService(EndpointReferenceFactory.getADBEPRTypeFromEPR(registrationRequeterPortEPR));
		register.setRegister(registerType);
		//Actual WS call for registeration
		RegisterResponse registerResponse = registrationCoordinator
				.RegisterOperation(register);
		EndpointReference coordinationEPR = EndpointReferenceFactory
				.getEPR(registerResponse.getRegisterResponse()
						.getCoordinatorProtocolService());
		return coordinationEPR;
	}

	private CoordinationContext createTransaction(InitiatorContext initiatorTransaction) throws AxisFault, MalformedURIException, RemoteException {
		ActivationServiceStub activationCoordinator = new ActivationServiceStub(
				configurationContext,initiatorTransaction.getActivationEPR());
		CreateCoordinationContext context = new CreateCoordinationContext();
		CreateCoordinationContextType createCoordinationContextType = new CreateCoordinationContextType();
		createCoordinationContextType.setCoordinationType(new URI(initiatorTransaction.getCoordinationType()));
		context.setCreateCoordinationContext(createCoordinationContextType);
		CreateCoordinationContextResponse response = activationCoordinator.CreateCoordinationContextOperation(context);
		CreateCoordinationContextResponseType createCoordinationContextResponse = response.getCreateCoordinationContextResponse();
		CoordinationContext coordinationContext = new ADBCoordinationContext(createCoordinationContextResponse.getCoordinationContext());
		return coordinationContext;
	}
}
