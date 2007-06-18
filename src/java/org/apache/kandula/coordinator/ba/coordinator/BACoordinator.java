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

package org.apache.kandula.coordinator.ba.coordinator;


import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;

import org.apache.axis.AxisFault;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.kandula.coordinator.CallbackRegistry;
import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.coordinator.CoordinationService;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.coordinator.CoordinatorImpl;
import org.apache.kandula.coordinator.InvalidCoordinationProtocolException;
import org.apache.kandula.coordinator.TimedOutException;
import org.apache.kandula.coordinator.ba.ProtocolType;
import org.apache.kandula.coordinator.ba.State;
import org.apache.kandula.wsba.StateType;
import org.apache.kandula.wsba.StatusType;
import org.apache.kandula.wscoor.CreateCoordinationContextTypeCurrentContext;

import org.apache.kandula.wsbai.BAParticipantReferenceType;
import org.apache.kandula.wsbai.BAParticipantType;

/**
 * Base class for all WS-BusinessActivity coordinators.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public abstract class BACoordinator extends CoordinatorImpl {
	/**
	 * The QName used to identify the BA with Coordinator Completion protocol.
	 * TODO WSBA Change this to the final initiator protocol identifier
	 */
	public final static String PROTOCOL_ID_INITIATOR = "http://big.tuwien.ac.at/ws-ba--extensions/protocol/initiator";
	
	/**
	 * The QName used to identify the Business Activity "Atomic" Outcome
	 */
	public final static String COORDINATION_TYPE__ATOMIC = "http://schemas.xmlsoap.org/ws/2004/10/wsba/AtomicOutcome";
	
	/**
	 * The QName used to identify the Business Activity "Mixed" Outcome
	 */
	public final static String COORDINATION_TYPE__MIXED = "http://schemas.xmlsoap.org/ws/2004/10/wsba/MixedOutcome";
	
	/**
	 * This QName is used to pass around the initiator's uuid.
	 */
	public static final QName INITIATOR_REF = new QName(
			"http://ws.apache.org/kandula", "InitiatorRef"
	);
	
	/**
	 * This QName identifies an error that is thrown when attempting to register
	 * a given matchcode twice in a context
	 */
	public static final QName DUPLICATE_MATCHCODE_FAULT = new QName(
			PROTOCOL_ID_INITIATOR, "duplicateMatchcodeFault"
	);
	
	/**
	 * Return a new AxisFault instance with its faultCode set to
	 * the  @see #DUPLICATE_MATCHCODE_FAULT QName.
	 * @param matchcode The matchcode that was requested twice.
	 * @return AxisFault
	 */
	public AxisFault DUPLICATE_MATCHCODE_SOAP_FAULT(final String matchcode){
		return new AxisFault(
				DUPLICATE_MATCHCODE_FAULT,
				"Attempted to register a matchcode that was already in use: "+matchcode,
				"", null
		);
	}
	
	/*
	 * = = = = = = = = = = = = = = =
	 */
	
	/**
	 * This field stores the chosen outcome Type (mixed/atomic).
	 */
	private final String outcomeType ;
	
	/**
	 * The participants map contains all registered participants, accessible by
	 * their Participant UUID.
	 * Access to this map should be synchronized for any write access and for methods
	 * that use iterators or enumerations derived from the map. The map is expected
	 * to only extend, it never loses entries.
	 */
	private final Map participants = new HashMap();
	
	/**
	 * This map contains a matchcode --> participant association. matchcodes
	 * are inserted with a null value as soon as the initiator requests 
	 * a registration service endpoint for a matchcode.
	 * Synchronize access to this map!! 
	 */
	private final Map participantMatchcodes = new HashMap();
	
	/**
	 * A reference to the initiator, if they already registered.
	 */
	private InitiatorProxy initiator = null;
	
	/**
	 * The default constructor. Use this to create a new Business Activity Coordinator.
	 * If superContext is given, this coordinator will register itself as a CoordinatorCompletion
	 * participant in the supercontext and will provide the initiator with all messages and state
	 * changes related to the supercontext. See the WSBAI specification for more detail.
	 *  
	 * @param pOutcomeType The outcome type for this coordination context.
	 * @param superContext The super context, if this is a subcontext
	 * @throws MalformedURIException Exception if the OutcomeType could not be parsed
	 * @throws InvalidOutcomeAssertion  Exception if the OutcomeType could be parsed, but not accepted
	 */
	protected BACoordinator(
			final String pOutcomeType,
			final CreateCoordinationContextTypeCurrentContext superContext
	) 
		throws MalformedURIException, InvalidOutcomeAssertion
	{		
		super(pOutcomeType);
		
		if (superContext != null){
			// Check the super context 
			if (false
					|| COORDINATION_TYPE__ATOMIC.equals( superContext.getCoordinationType().toString() ) 
					|| COORDINATION_TYPE__MIXED.equals( superContext.getCoordinationType().toString() )
			){
				// Super's coordination type is OK
			}else{
				throw new IllegalArgumentException("The Kandula Business Activity coordination service currently can only create subordinate coordination contexts to a WS-BA parent context.");
			}
			
			// TODO HE set super context and act appropriately...
			
			
		} // end supercontext checks
		
		if (COORDINATION_TYPE__ATOMIC.equals(pOutcomeType)){
			this.outcomeType = pOutcomeType;
		}else if (COORDINATION_TYPE__MIXED.equals(pOutcomeType)){
			this.outcomeType = pOutcomeType;
		}else {
			throw new InvalidOutcomeAssertion();
		}
	}
	
	
	/**
	 * Check if a particular match code has already been asked for.
	 * @param matchcode The match code to check.
	 * @return true, if the match code is available; false, if not.
	 */
	protected boolean isMatchCodeAvailable(final String matchcode){
		return (! this.participantMatchcodes.containsKey(matchcode));
	}
	
	/**
	 * Activate a particular matchcode. A matchcode is used by the initiator to identify
	 * the transaction's participants.
	 * By default, we only allow registrations for protocols that include a valid matchcode
	 * that is activated, but not yet taken.
	 * This method returns an registrationService endpoint that is already tagged with
	 * the requested matchcode, if it was available.
	 * If the requested matchcode has already been taken, an invalidargumentexception is thrown.
	 *  
	 * @param matchcode The match code to register and activate
	 * @return Endpoint to this context's registration service, tagged with the match code.
	 * @throws AxisFault A ready-made fault, if the match code is already reserved or taken.
	 */
	public CoordinationContext registerMatchcode(final String matchcode) throws AxisFault{
		synchronized (this.participantMatchcodes) {
			if (matchcode == null || matchcode.length()==0 )
				throw new IllegalArgumentException("The matchcode must not be null or emtpy!");
			
			if (! this.isMatchCodeAvailable(matchcode)){
				throw DUPLICATE_MATCHCODE_SOAP_FAULT(matchcode);
			}
			
			this.participantMatchcodes.put(matchcode, null);
			
			return this.getCoordinationContext(matchcode);
		} // end synchronized participantMatchcodes
	}
	/**
	 * Register a participant for one of the BAwCC or BAwPC protocols and
	 * return the protocol service's endpoint reference.
	 * 
	 * @param protocol The requested protocol.
	 * @param participantEndpoint The participant's endpoint.
	 * @param matchcode The match code the participant asked for.
	 * @return The endpoint reference of the coordinator's protocol service, tagged with the
	 *  participant and transaction identifiers.
	 *  
	 * @throws InvalidCoordinationProtocolException 
	 */
	public EndpointReference register(
			final String protocol, 
			final EndpointReference participantEndpoint,
			final String matchcode
	) 
	throws InvalidCoordinationProtocolException {
		
		// Check, whether a "valid" callback was supplied
		if (participantEndpoint == null)
			throw new IllegalArgumentException("Register: Participant Endpoint must not be null");
		
		
		// Calculate the new participant's GUID
		// The kandula coordination service
		final CoordinationService cs = CoordinationService.getInstance();
		
		final UUIDGen gen = UUIDGenFactory.getUUIDGen();
		final String newRef = "uuid:" + gen.nextUUID();
		
		final EndpointReference  epr;
		
		try{
			// Check whether a valid protocol was supplied and register
			if (protocol.equals(PROTOCOL_ID_INITIATOR)){
				// OK, register as initiator; synchronize and set the initiator fields

				synchronized(this){				
					if (this.initiator != null)
						throw new IllegalStateException("Sorry, there already is an initiator registered!");
					
					this.initiator = new InitiatorProxy(newRef, participantEndpoint);
					epr = cs.getInitiatorCoordinatorService(this, newRef);
				} // end synchronized this
			}else{
				// Other protocols
				final AbstractCoordParticipant newParticipant;
				
				/*
				 * While registration is in progress, lock the match code table
				 * 
				 */
				synchronized(this.participantMatchcodes){
					if (! this.participantMatchcodes.containsKey(matchcode)){
						throw new IllegalArgumentException("Sorry, the matchcode "+matchcode+" has not been activated.");
					}
					if (this.participantMatchcodes.get(matchcode) != null){
						final IllegalArgumentException x = new IllegalArgumentException("Sorry, the matchcode "+matchcode+" has already been taken.");
						throw x;
					}
	
					if (! this.isRegistrationPossible() ){
						final AxisFault af = CONTEXT_REFUSED_SOAP_FAULT();
						af.addFaultDetailString("The context exists, but work has progressed so far that no more registrations are accepted.");
						throw af;
					}
					
					if (protocol.equals(ProtocolType.PROTOCOL_ID_CC.getNamespaceURI()+ProtocolType.PROTOCOL_ID_CC.getLocalPart())){
						// OK, register for Coordinator Completion 
						
						newParticipant = new BAwCCCoordParticipant(newRef, participantEndpoint, this, matchcode);
					}else if (protocol.equals(ProtocolType.PROTOCOL_ID_PC.getNamespaceURI()+ProtocolType.PROTOCOL_ID_PC.getLocalPart())){
						
						// OK, register for Participant Completion
						
						newParticipant = new BAwPCCoordParticipant(newRef, participantEndpoint, this, matchcode);
					}else{
						throw new InvalidCoordinationProtocolException();
					}
					
					// Register participant object
					this.participantMatchcodes.put(matchcode, newParticipant);
				} // End synchronized (this.participantMatchcode)

				/*
				 * Synchronize on both this (used by buildParticipantList) and this.participants
				 * (used by registration) to lock participant lists before adding
				 */
				synchronized(this){
					synchronized (this.participants) {
						this.participants.put(newRef, newParticipant);
					}
				}
				epr = cs.getCoordinatorService(this, newRef, protocol);
				CallbackRegistry.getInstance().registerCallback(newParticipant);
			} // End protocol switch 
		}
		catch(AxisFault f){
			throw new IllegalArgumentException("Sorry, could not instantiate a participant: "+f);
		}
		return epr;
	}
	
	/**
	 * Check if enrolling a new participant is allowed in the current state
	 * of the transaction.
	 * @return false, if registration requests shall be rejected.
	 */
	abstract boolean isRegistrationPossible();


	/**
	 * Registers an incoming fault.
	 * @param code The fault code.
	 */
	public void onFault(Name code) {
		// TODO WSBA Handle faults
		
		System.out.println("[BACoordinator] onFault: " + code);
	}
	
	/**
	 * The transaction context timed out. Notify all participants 
	 * and get outta here!
	 * @throws TimedOutException ???
	 */
	public void timeout() throws TimedOutException {
		// TODO WSBA Handle timeouts
		
		if (false)
			throw new TimedOutException();
	}
	
	/**
	 * This coordinator's endpoint reference.
	 * (Seems to be an unused method, since there is always an UnsupportedOperationException thrown)
	 * @return ERROR !
	 */
	public EndpointReference getEndpointReference() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns this Coordinator's coordination type.
	 * 
	 * @return One of COORDINATION_TYPE__ATOMIC and COORDINATION_TYPE__MIXED
	 * @see #COORDINATION_TYPE__ATOMIC
	 * @see #COORDINATION_TYPE__MIXED
	 */
	public String getOutcomeType() {
		return this.outcomeType;
	}
	
	/**
	 * Check, if the given participant is a registered participant in this context.
	 * @param participant The participant.
	 * @return true, if the participant is registered in the current transaction; false, if not.
	 */
	public boolean containsParticipant(final AbstractCoordParticipant participant){
	
		// Doesn't need synchronization since de-registration is not possible
		
		return this.participants.containsValue(participant);
	}
	
	/**
	 * This method is used by participant objects to signal state changes to the coordination context.
	 * It is synchronized to avoid any problems that may arise when multiple participants change
	 * states at the same time.
	 *
	 * This method contains any process logic that does not need business logic and hence may be 
	 * implemented in the coordinator, e.g. automatically
	 * acknowledging state changes as ACTIVE->EXIT->EXITING->EXITED->ENDED .
	 * 
	 * Any state change will also be automatically be forwarded to the initiator. For those state
	 * changes where the coordinator does not have sufficient knowledge of the initiator's goals to
	 * direct the participant to the next state, it will wait for the initiator to issue new commands.
	 * 
	 * @param participant The participant that changed states
	 * @param newState The state after the transition (= current state of the participant)
	 * @throws RemoteException Exception while handling the transition. 
	 */
	public void handleStateTransition(
			final AbstractCoordParticipant participant,
			final QName newState
	) throws RemoteException {
		// TODO WSBA Forward any state change to the initiator

		// Ensure we may change participant states
		// (this should be the case anyways)
		synchronized(this){
			if (State.STATE_EXITING.equals(newState)){
				// Participant is in state EXITING, send EXITED and set state to ENDED.
				participant.tellExited();
			}else if (false 
					|| State.STATE_FAULTING.equals(newState)
					|| State.STATE_FAULTING_ACTIVE.equals(newState)
					|| State.STATE_FAULTING_COMPENSATING.equals(newState)
			){
				participant.tellFaulted();
			}else{
				System.out.println("State transition to "+newState+" must be handled by initiator!");
			}

			/*
			 *  As soon as the ENDED state is reached, we no longer accept
			 *  messages from the client and hence remove them from the
			 *  callback registry. (This also aborts the timeout timertask.)
			 *  We dont remove them from the participants map, so the initiator
			 *  can still see the participant and their state.
			 */
			if (State.STATE_ENDED.equals(newState)){
				CallbackRegistry.getInstance().remove(participant);
			}
		}
	}
	
	
	/**
	 * Return a participant list. This method is synchronized on "this"
	 * and "this.participants" to ensure maximum consistency.
	 * 
	 * @return An array of BAParticipantType objects.
	 */
	public BAParticipantType[] buildParticipantList() {
		final BAParticipantType[] result ;
		
		synchronized (this){
			synchronized (this.participants) {
				result = new BAParticipantType[this.participants.size()];
				
				final Iterator it = this.participants.values().iterator();
				int count   = 0;
				
				// circle through the participants...
				while(it.hasNext()){
					final AbstractCoordParticipant participant = (AbstractCoordParticipant) it.next();
					
					final URI proto;
					{
						URI tmpProto = null;
						try{
							tmpProto = new org.apache.axis.types.URI(
									participant.getProtocolIdentifier().getNamespaceURI()+
									participant.getProtocolIdentifier().getLocalPart()
							);
						}
						catch(MalformedURIException x){
							// forget it
						}
						proto = tmpProto;
					}
					
					final StateType resultState = StateType.fromValue(participant.getResultState());
					
					// Fill all fields...
					result[count] = new BAParticipantType(
							new BAParticipantReferenceType(participant.getMatchcode()),
							new StatusType(StateType.fromValue(participant.getCurrentState()), null),
							new StatusType(resultState, null),
							proto
					);
					
					count++;
				}
			} // end synchronized this.participants
		} // end synchronized this
		return result;
	}
	
	/**
	 * Fetches the participant associated to the given matchcode in the current context.
	 * Returns null if either the matchcode was not registered in the context, or 
	 * the matchcode is valid but no participant has yet registered for it.
	 * @param matchCode
	 * @return The participant that registered for the requested matchcode, or null
	 * 	if the matchcode is either unused or no participant registered for it.
	 */
	public AbstractCoordParticipant getParticipantByMatchcode(final String matchCode){
		return (AbstractCoordParticipant) this.participantMatchcodes.get(matchCode);
	}
	
	/**
	 * Returns the collection of participants who are registered in the transaction. 
	 * @return The participant collection.
	 */
	protected final Collection getParticipants(){
		return this.participants.values();
	}
	
	/**
	 * Checks if the current request has all properties a request from the genuine
	 * initiator must have. At this time, we check that the @see 
	 * @throws RemoteException
	 */
	public void ensureRequestOriginatesFromInitiatorOrThrowFault() throws RemoteException{
		final String initiatorRef = CallbackRegistry.getRef(BACoordinator.INITIATOR_REF);
		
		if (initiatorRef != null
				&& initiatorRef.equals(this.initiator.myID) 
		){
			// OK
		}else{
			throw Coordinator.CONTEXT_REFUSED_SOAP_FAULT();
		}
	}

}
