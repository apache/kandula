/*
 * Copyright 2007 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *  @author Hannes Erven, Georg Hicker
 */
package org.apache.kandula.coordinator.ba.initiator;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.coordinator.ba.coordinator.BACoordinator;
import org.apache.kandula.wscoor.CreateCoordinationContextResponseType;

import org.apache.kandula.wsbai.CoordinatorInitiatorProtocolPortType_AllProtocols;
import org.apache.kandula.wsbai.CoordinatorInitiatorProtocolPortType_Atomic;
import org.apache.kandula.wsbai.CoordinatorInitiatorProtocol_ServiceLocator;
import org.apache.kandula.wsbai.BAParticipantType;
import org.apache.kandula.wsbai.GetCoordCtxWCodeReqType;

/**
 * This class provides methods for managing business activity contexts with an AtomicOutcome
 * assertion.
 * 
 * Atomic outcome means that there is a single point at which the initiator application must decide
 * whether to successfully close the transaction or to "roll back", hence cancel or compensate all participants.
 * The coordinator will afterwards reject new participant's registration requests. 
 * 
 * The final outcome decision can only be made once per coordination context, and may not be changed afterwards.
 * 
 * "cancelOrCompensateAllParticipants" tells the coordinator to direct all participants depending on their
 * current state to either CANCEL or COMPENSATE. Participants who EXITed before are ignored, as well as those
 * who FAULTed. It is always (while the transaction is undecided) allowed to decide to cancelOrCompensate.
 * 
 * "closeAllParticipants" tells the coordinator to direct all participants to CLOSE, while ignoring
 * those who already EXITed the transaction. It is only allowed to decide to closeAll when all
 * participants are in one of the COMPLETED, EXITING or ENDED states. 
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class AtomicCoordinatorIProxy extends AbstractCoordinatorIProxy {

	/**
	 * The stub object, used to make calls to the coordinator
	 */
	protected final CoordinatorInitiatorProtocolPortType_Atomic coordinatorStubAtomic;

	/**
	 * Try to register as initiator in the given coordination context.
	 * 
	 * @param ctx
	 * @throws AxisFault
	 * @throws RemoteException
	 * @throws ServiceException
	 */
	protected AtomicCoordinatorIProxy(final CoordinationContext ctx) throws AxisFault,
			RemoteException, ServiceException {
		super(ctx);

		try {
			this.coordinatorStubAtomic = CoordinatorInitiatorProtocol_ServiceLocator
							.getAtomicStub(this.coordinationContext,
									this.coordinatorPort);
		} catch (MalformedURLException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Return a coordination context, containing a registration service that is
	 * tagged with the given matchcode.
	 * 
	 * @param participantMatchcode
	 *            The matchcode for the participant (ATTENTION: it has to be
	 *            unique)
	 * @return a coordination context
	 * @throws RemoteException
	 */
	public CoordinationContext getCoordinationContextForParticipant(
			final String participantMatchcode) throws RemoteException {

		final CreateCoordinationContextResponseType ctxResponseType;

		try {
			final GetCoordCtxWCodeReqType matchCodeReq =
				new GetCoordCtxWCodeReqType();
			
			matchCodeReq.setMatchcode(
					participantMatchcode
			);

			ctxResponseType = this.coordinatorStubAtomic.getCoordinationContextWithMatchcode(
						matchCodeReq
//						participantMatchcode
			);
		} catch (RemoteException e) {
			throw e;
		}

		return new CoordinationContext(ctxResponseType.getCoordinationContext());
	}

	/**
	 * cancel or compensate all participants of the transaction with atomic
	 * outcome
	 * 
	 * @return an array of all participants in the transaction and their
	 *         associated states
	 * @throws RemoteException
	 */
	public BAParticipantType[] cancelOrCompensateAllParticipants() throws RemoteException {

		final BAParticipantType[] participantList;
		try {
			participantList = this.coordinatorStubAtomic.cancelOrCompensateAllParticipants();
		} catch (RemoteException e) {
			throw e;
		}
		return participantList;
	}

	/**
	 * close all participants of the transaction with atomic outcome
	 * 
	 * @return an array of all participants in the transaction and their
	 *         associated states
	 * @throws RemoteException
	 */
	public BAParticipantType[] closeAllParticipants() throws RemoteException {

		final BAParticipantType[] participantList;
		try {
			participantList = this.coordinatorStubAtomic.closeAllParticipants();
		} catch (RemoteException e) {
			throw e;
		}
		return participantList;
	}

	/**
	 * Return the coordinator stub.
	 * @return The stub.
	 */

	protected CoordinatorInitiatorProtocolPortType_AllProtocols getCoordinatorStub() {
		return this.coordinatorStubAtomic;
	}
	
	/**
	 * Contacts the configured (see kandula.properties) activation service to create a new
	 * atomic outcome coordination context, registers itself as initiator there and returns
	 * the Kandula WSBAI management object for the new transaction.
	 * 
	 * 
	 * @return The initiator object.
	 * @throws AxisFault A fault from Axis.
	 * @throws RemoteException A fault from the Activation Service.
	 * @throws ServiceException A fault from Axis.
	 * @throws MalformedURLException Configuration error.
	 * @throws MalformedURIException Configuration error.
	 */
	public static AtomicCoordinatorIProxy createNewContext_WithWSBAI()
			throws AxisFault, RemoteException, ServiceException, MalformedURIException, MalformedURLException {

		final CoordinationContext cc = AbstractCoordinatorIProxy.createCoordinationContext(
				BACoordinator.COORDINATION_TYPE__ATOMIC
		);

		return new AtomicCoordinatorIProxy(cc);
	}

}
