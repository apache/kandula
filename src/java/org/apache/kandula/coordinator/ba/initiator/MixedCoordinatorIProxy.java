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

import org.apache.kandula.wsbai.CoordinatorInitiatorProtocolPortType_AllProtocols;
import org.apache.kandula.wsbai.CoordinatorInitiatorProtocolPortType_Mixed;
import org.apache.kandula.wsbai.CoordinatorInitiatorProtocol_ServiceLocator;
import org.apache.kandula.wsbai.BAParticipantReferenceType;
import org.apache.kandula.wsbai.BAParticipantType;

/**
 * This class provides methods for managing business activity contexts with a MixedOutcome
 * assertion.
 * 
 * Mixed outcome means that participants are handled independently. It is hence perfectly legal
 * to direct one participant to close, one to compensate and one to cancel.
 * 
 * The coordinator always checks if the message you intend to send is allowed in the current state of the
 * participant, and will silently ignore any commands do not comply with this rule.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class MixedCoordinatorIProxy extends AbstractCoordinatorIProxy {

	/**
	 * The mixed initiator service location
	 */
	protected final CoordinatorInitiatorProtocolPortType_Mixed coordinatorStubMixed;


	/**
	 * Try to register as initiator in the given coordination context.
	 * 
	 * @param ctx
	 * @throws AxisFault
	 * @throws RemoteException
	 * @throws ServiceException
	 */
	protected MixedCoordinatorIProxy(final CoordinationContext ctx) throws AxisFault,
			RemoteException, ServiceException {

		super(ctx);
		
		try {
			this.coordinatorStubMixed = CoordinatorInitiatorProtocol_ServiceLocator
			.getMixedStub(this.coordinationContext,
					this.coordinatorPort);
		} catch (MalformedURLException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * complete given participants in the transaction
	 * 
	 * @param participantsToComplete
	 *            the participants to be completed
	 * @return an array of all participants in the transaction and their
	 *         associated states
	 * @throws RemoteException
	 */
	public BAParticipantType[] completeParticipants(
			BAParticipantReferenceType[] participantsToComplete
	) throws RemoteException {
		
		final BAParticipantType[] participantList;
		try {
			participantList = this.coordinatorStubMixed.completeParticipants(participantsToComplete);
		} catch (RemoteException e) {
			throw e;
		}
		return participantList;
	}


	/**
	 * cancel given participants in the transaction with mixed outcome
	 * 
	 * @param participantsToCancel
	 *            the participants to be cancelled
	 * @return an array of all participants in the transaction and their
	 *         associated states
	 * @throws RemoteException
	 */
	public BAParticipantType[] cancelParticipants(
			BAParticipantReferenceType[] participantsToCancel
	) throws RemoteException {

		final BAParticipantType[] participantList;
		try {
			participantList = this.coordinatorStubMixed.cancelParticipants(participantsToCancel);
		} catch (RemoteException e) {
			throw e;
		}
		return participantList;
	}

	/**
	 * close given participants in the transaction with mixed outcome
	 * 
	 * @param participantsToClose
	 *            the participants to be closed
	 * @return an array of all participants in the transaction and their
	 *         associated states
	 * @throws RemoteException
	 */
	public BAParticipantType[] closeParticipants(
			BAParticipantReferenceType[] participantsToClose
		) throws RemoteException {

		final BAParticipantType[] participantList;
		try {
			participantList = this.coordinatorStubMixed.closeParticipants(participantsToClose);
		} catch (RemoteException e) {
			throw e;
		}
		return participantList;
	}

	/**
	 * compensate given participants in the transaction with mixed outcome
	 * 
	 * @param participantsToCompensate
	 *            the participants to be compensated
	 * @return an array of all participants in the transaction and their
	 *         associated states
	 * @throws RemoteException
	 */
	public BAParticipantType[] compensateParticipants(
			BAParticipantReferenceType[] participantsToCompensate
	) throws RemoteException {

		final BAParticipantType[] participantList;
		try {
			participantList = this.coordinatorStubMixed.compensateParticipants(participantsToCompensate);
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
		return this.coordinatorStubMixed;
	}

	/**
	 * Contacts the configured (see kandula.properties) activation service to create a new
	 * mixed outcome coordination context, registers itself as initiator there and returns
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
	public static MixedCoordinatorIProxy createNewContext_WithWSBAI()
			throws AxisFault, RemoteException, ServiceException, MalformedURIException, MalformedURLException {

		final CoordinationContext cc = AbstractCoordinatorIProxy.createCoordinationContext(
				BACoordinator.COORDINATION_TYPE__MIXED
		);

		return new MixedCoordinatorIProxy(cc);
	}

}
