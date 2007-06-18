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
import javax.xml.soap.Name;

import org.apache.axis.AxisFault;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.kandula.coordinator.ActivationStub;
import org.apache.kandula.coordinator.Callback;
import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.coordinator.CoordinationService;
import org.apache.kandula.coordinator.TimedOutException;
import org.apache.kandula.coordinator.ba.coordinator.BACoordinator;
import org.apache.kandula.wscoor.CreateCoordinationContextResponseType;

import org.apache.kandula.wsbai.CoordinatorInitiatorProtocolPortType_AllProtocols;
import org.apache.kandula.wsbai.BAParticipantReferenceType;
import org.apache.kandula.wsbai.BAParticipantType;
import org.apache.kandula.wsbai.GetCoordCtxWCodeReqType;
import org.apache.kandula.wsbai.ListParticipantsReqType;

/**
 * This class represents the client proxy for calls to the coordinator by the
 * business activity enabled application.
 * 
 * It is responsible for handling all communication requirements between a kandula coordinator
 * and the application. Since WS-BusinessActivity does not include specifications for this protocol,
 * it is mandatory that both the application and the coordination service use compatible Kandula
 * versions. @see org.apache.kandula.coordinator.ba.coordinator.BACoordinator#PROTOCOL_ID_INITIATOR
 * 
 * This abstract class provides methods common to all business activity contexts, such as preparing
 * registration slots for new participants as well as querying the participant list and handling
 * coordinator-completion subscribers up to the COMPLETED state.
 * 
 * Specific subclasse, @see org.apache.kandula.coordinator.ba.initiator.AtomicCoordinatorIProxy 
 * and @see org.apache.kandula.coordinator.ba.initiator.MixedCoordinatorIProxy provide methods
 * specific contexts either with the atomic or mixed outcome assertions.
 * 
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 * 
 */
public abstract class AbstractCoordinatorIProxy implements Callback {

	/**
	 * The coordinator's service endpoint location, complete with reference
	 * properties.
	 */
	protected final EndpointReference coordinatorPort;

	/**
	 * The local endpoint reference as given to the coordinator.
	 */
	protected final EndpointReference initiatorPort;

	/**
	 * The initiator's participant access GUID as provided by the coordinator.
	 */
	protected final String myID;

	/**
	 * The coordination context object that this object manages.
	 */
	protected final CoordinationContext coordinationContext;

	/**
	 * Retrieves a web service stub for the coordinator's initiator service.
	 * @return The web service stub.
	 */
	protected abstract CoordinatorInitiatorProtocolPortType_AllProtocols getCoordinatorStub();
	
	/**
	 * Tries to register a new initiator for the given coordination context.
	 * 
	 * Supported Outcome Types: Atomic and Mixed
	 * 
	 * @param ctx
	 *        The coordination context to register with
	 * @return The initiator object
	 * @throws AxisFault 
	 * @throws RemoteException 
	 * @throws ServiceException 
	 */
	public static AbstractCoordinatorIProxy newInstance(final CoordinationContext ctx)
			throws AxisFault, RemoteException, ServiceException {
		
		if (BACoordinator.COORDINATION_TYPE__ATOMIC.equals( ctx.getCoordinationType().toString() )) {
			return new AtomicCoordinatorIProxy(ctx);
		
		}else if (BACoordinator.COORDINATION_TYPE__MIXED.equals( ctx.getCoordinationType().toString() )) {
			return new MixedCoordinatorIProxy(ctx);
		
		}else{
			throw new IllegalArgumentException("Unsupported context type: "+ctx.getCoordinationType());
		}
	}

	/**
	 * Contact a WS-Coordination Service to have a context with the specified coordinationType
	 * created.
	 * @param coordinationType The WS-Coordination coordination type to request.
	 * @return The created CoordinationContext object.
	 * @throws MalformedURLException Configuration Error in kandula.properties
	 * @throws MalformedURIException Configuration Error in kandula.properties
	 * @throws RemoteException Error from the Activation Service
	 * @throws AxisFault Error from the Activation Service
	 */
	protected static CoordinationContext createCoordinationContext(final String coordinationType) throws AxisFault, RemoteException, MalformedURIException, MalformedURLException{
		/*
		 * The endpoint of the activation service to use.
		 */
		final EndpointReference activationServiceEPR = CoordinationService
				.getInstance().getActivationCoordinatorService();

		/*
		 * Contact the activiation service and create a new coordination context
		 */
		final CoordinationContext ctx;
		ctx = new ActivationStub(activationServiceEPR)
				.createCoordinationContext(coordinationType);

		return ctx;
	}
	
	/**
	 * Try to register as initiator in the given coordination context.
	 * 
	 * @param ctx
	 * @throws AxisFault
	 * @throws RemoteException
	 * @throws ServiceException
	 */
	protected AbstractCoordinatorIProxy(
			final CoordinationContext ctx 
	) throws AxisFault,
			RemoteException {

		this.coordinationContext = ctx;
		final UUIDGen gen = UUIDGenFactory.getUUIDGen();
		this.myID = "uuid:" + gen.nextUUID();

		// Local initiator services endpoint
		this.initiatorPort = CoordinationService.getInstance()
				.getInitiatorService(this, 0);

		// remote coordination services endpoint
		this.coordinatorPort = ctx.register(
				BACoordinator.PROTOCOL_ID_INITIATOR, this.initiatorPort);

		// Check if we got something back...
		if (this.coordinatorPort == null)
			throw new NullPointerException("CoordinatorPort has not been provided, but is required!");

		// OK, everything seems to be good
	}

	/**
	 * This initiator's ID.
	 * @return My ID.
	 */
	public String getID() {
		return this.myID;
	}

	/**
	 * The context's coordination type (mixed or atomic)
	 * @return The coordination type's URI
	 */
	public org.apache.axis.types.URI getCoordinationType() {
		return this.coordinationContext.getCoordinationType();
	}

	/**
	 * Checks if a given coordinationType is equal to the one of the Initiator
	 * chosen protocol.
	 * 
	 * @param coordinationTypeX
	 * @return <code>true</code> if equal 
	 */
	public boolean testCoordinationTypeEquals(final String coordinationTypeX) {
		return coordinationTypeX.equals( this.getCoordinationType().toString() );
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

			ctxResponseType = this.getCoordinatorStub().getCoordinationContextWithMatchcode(
						matchCodeReq
//						participantMatchcode
			);
		} catch (RemoteException e) {
			throw e;
		}

		return new CoordinationContext(ctxResponseType.getCoordinationContext());
	}

	/**
	 * List the participants taking part in the transaction
	 * 
	 * @return an array of all participants in the transaction and their
	 *         associated states
	 * @throws RemoteException
	 */
	public BAParticipantType[] listParticipants() throws RemoteException {
		final BAParticipantType[] participantList;
		final ListParticipantsReqType req = new ListParticipantsReqType();
		try {
			participantList = this.getCoordinatorStub().listParticipants(req);
		} catch (RemoteException e) {
			throw e;
		}
		return participantList;
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
			participantList = this.getCoordinatorStub().completeParticipants(participantsToComplete);
		} catch (RemoteException e) {
			throw e;
		}
		return participantList;
	}


	/*
	 * === Callback methods ===
	 * @see org.apache.kandula.coordinator.Callback#onFault(javax.xml.soap.Name)
	 */
	
	
	/** Callback interface fault handler
	 * @see org.apache.kandula.coordinator.Callback#onFault(javax.xml.soap.Name)
	 */
	public void onFault(final Name code) {
		code.toString();
		// TODO WSBA Handle faults
	}

	/** Callback interface timeout handler
	 * @see org.apache.kandula.coordinator.Callback#timeout()
	 */
	public void timeout() throws TimedOutException {
		// TODO WSBA Handle timeouts
		if (false)
			throw new TimedOutException();
	}

	/** Callback interface method -- unused 
	 * @see org.apache.kandula.coordinator.Callback#getEndpointReference()
	 */
	public EndpointReference getEndpointReference() {
		return null;
	}

	/**
	 * Provides an array with matchcodes of the currently enrolled participants. This is a
	 * convenience method for calls to the mixed outcome coordinator proxy that expects
	 * arrays of this type for naming the participants that are subjected to some call.
	 * 
	 * @return Unused?
	 * @throws RemoteException 
	 */
	public BAParticipantReferenceType[] getXAllParticipantReferences() throws RemoteException{
		final BAParticipantType[] ps = this.listParticipants();
		
		if (ps == null || ps.length == 0)
			return new BAParticipantReferenceType[0];
		
		final BAParticipantReferenceType[] prs = new BAParticipantReferenceType[ps.length];
		for(int i=0; i<ps.length; i++)
			prs[i] = new BAParticipantReferenceType(ps[i].getParticipantMatchcode().getParticipantMatchcode());
		
		return prs;
	}
}
