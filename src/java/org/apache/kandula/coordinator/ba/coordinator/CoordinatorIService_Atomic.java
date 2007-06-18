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

import javax.xml.namespace.QName;

import org.apache.kandula.coordinator.ba.State;

import org.apache.kandula.wsbai.CoordinatorInitiatorProtocolPortType_Atomic;
import org.apache.kandula.wsbai.BAParticipantReferenceType;
import org.apache.kandula.wsbai.BAParticipantType;


/**
 * This class handles requests sent from the initiator to the coordinator. It receives
 * the messages, correlates them to a running business activity and either fetches the requested
 * information from the business activity coordinator or forwards commands to it.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */

public class CoordinatorIService_Atomic 
extends CoordinatorIService
implements CoordinatorInitiatorProtocolPortType_Atomic
{
	
	/**
	 * Tells the coordinator to cancel or compensate all participants. This is only valid
	 * in transactions with the "atomic outcome" assertion.
	 * 
	 * Once called, the coordinator will issue appropriate commands so that eventually
	 * all participants (depending on their state when this method was called) either
	 * 	 - canceled (and ended),
	 *   - compensated (and ended), or
	 *   - faulted (and ended).
	 *   
	 * If a participant reports a fault, it is the initiator's responsibility to take
	 * appropriate action.
	 *   
	 * When calling this method, all registered participants must be in one of the following states:
	 * <ul>
	 * <li>ACTIVE (will be cancelled)
	 * <li>COMPLETED (will be compensated)
	 * <li>COMPLETING (will be compensated)
	 * <li>EXITING (will be ignored)
	 * <li>ENDED (will be ignored)
	 * </ul> 
	 * 
	 * 
	 * The initiator may in each transaction issue one call to either @see #cancelOrCompensateAllParticipants()
	 * or @see #closeAllParticipants() , not both. 
	 * 
	 * @return A list of the current participants and their states.
	 * @throws RemoteException
	 */
	public BAParticipantType[] cancelOrCompensateAllParticipants() throws RemoteException {
		final BAwithAtomicOutcomeCoordinator context = 
			(BAwithAtomicOutcomeCoordinator) BACoordinatorService.getBACoordinatorOrThrowFault();		
		
		context.ensureRequestOriginatesFromInitiatorOrThrowFault();
		
		synchronized (context) {
			final BAParticipantType[] participantsToCancelOrCompensate = context.buildParticipantList();
			
			if (participantsToCancelOrCompensate != null && participantsToCancelOrCompensate.length>0){
				for(int i=0; i<participantsToCancelOrCompensate.length; i++){
					// Fetch the current match code
					final String matchCode = participantsToCancelOrCompensate[i].getParticipantMatchcode().getParticipantMatchcode();
					
					// Check if the participant is known
					final AbstractCoordParticipant part =
						context.getParticipantByMatchcode(matchCode);
					
					if (part == null){
						// No participant found - ignore
						continue ;
					}
					
					final QName pstate = part.getCurrentState();
					if (false
							|| pstate.equals(State.STATE_ACTIVE)
							|| pstate.equals(State.STATE_COMPLETING)
					){
						// State is good, proceed with cancel						
					}else if ( false
							|| pstate.equals(State.STATE_COMPLETED)
					){
						// State is good, compensate
					}else if (false
							|| pstate.equals(State.STATE_EXITING)
							|| pstate.equals(State.STATE_ENDED)
					){
						// state is good, ignore
						
					}else{
						// state is bad, throw back 
						throw State.GET_INVALID_STATE_SOAP_FAULT(pstate);
					}
				} // end iterate through the requested match codes and check participant states
			} // end check if there are any participants
			
			// Up until now, everything was alright
			// So proceed with setting the decision, even if there were no participants
			context.getOutcomeDecision().decideToCancelOrCompensate();
			context.executeDecision();
			
			return context.buildParticipantList();
		}
	}
	
	/**
	 * Tells the coordinator to close all participants. This is only valid
	 * in transactions with the "atomic outcome" assertion.
	 * 
	 * Once called, the coordinator will issue appropriate commands so that eventually
	 * all participants either
	 *   - closed (and ended), or
	 *   - faulted (and ended).
	 *   
	 * If a participant reports a fault, it is the initiator's responsibility to take
	 * appropriate action.
	 *   
	 * 
	 * The initiator may in each transaction issue one call to either @see #cancelOrCompensateAllParticipants()
	 * or @see #closeAllParticipants() , not both. 
	 * 
	 * @return A list of the current participants and their states.
	 * @throws RemoteException
	 */
	public BAParticipantType[] closeAllParticipants() throws RemoteException {
		final BAwithAtomicOutcomeCoordinator context = 
			(BAwithAtomicOutcomeCoordinator) BACoordinatorService.getBACoordinatorOrThrowFault();		

		context.ensureRequestOriginatesFromInitiatorOrThrowFault();
		
		synchronized(context){
			final BAParticipantType[] participantsToClose = context.buildParticipantList();
			
			
			/*
			 * First step: compile a participant list and check, for each participant, whether
			 * it is in a state where a decision to close or compensate is allowed.
			 * 
			 * The call to closeAllParticipants is only allowed if ALL registered participants
			 * are in one of the following states:
			 * <ul>
			 * 	<li>COMPLETED</li>
			 * <li>EXITING</li>
			 * <li>ENDED (after Exiting)</li>
			 * </ul>
			 */
			final BAParticipantReferenceType[] participantsToCloseByMatchcode = new BAParticipantReferenceType[participantsToClose.length];
			for(int i=0; i<participantsToClose.length; i++){
				final BAParticipantType participant = participantsToClose[i]; 
				
				final QName pstate  = participant.getStatus().getState().getValue();
				
				if (pstate == null){
					// The state is null if a matchcode was activated, but no participant has registered for it
					continue;
				}
				
				if (false
						|| pstate.equals(State.STATE_COMPLETED)
				){
					// The state is OK, send the message
					participantsToCloseByMatchcode[i]= participant.getParticipantMatchcode();
					
				}else if (false
						|| pstate.equals(State.STATE_EXITING)
						|| pstate.equals(State.STATE_ENDED)
				){
					// The state is OK, but ignore this participant
					continue ;
					
				}else{
					// Illegal state!
					
					throw State.GET_INVALID_STATE_SOAP_FAULT(pstate);
				}
			}
			
			context.getOutcomeDecision().decideToClose();
			context.executeDecision();
			
			return context.buildParticipantList();
		} // end synchronized on context
	}
	
}
