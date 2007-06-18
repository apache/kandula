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
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.kandula.coordinator.ba.State;
import org.apache.kandula.wscoor.CreateCoordinationContextTypeCurrentContext;


/**
 * This class represents an Business Activity coordinator for a context
 * with Atomic Outcome assertion.
 * 
 * Atomic outcome means that all participants will be directed to either
 *  - close, or
 *  - cancel/compensate
 *
 * depending on the progress the participants made. If the transaction's state
 * does not require the coordinator to automatically choose one of these
 * options, the initiator must decide which outcome they desire.
 * 
 * The following rules apply to business activities with atomic outcome:
 * <ul>
 * <li>participants who 'exit' do not affect the progress of other participants and are discarded.
 * <li>participants who 'fault' require all other participants to cancel or compensate, depending
 * 	on their progress.
 * <li>participants who 'cancel' while active or completing also require all other participants
 * 	to cancel or compensate, again depending on their progress.
 * <li>only if all participants progress to the 'completed' state may the initiator decide on the
 * 	way to go.
 * </ul>
 *  
 * This class overrides the handleTransition method of BACoordinator and adds handling for
 * participants who reach one of the 'completed', 'exiting' or 'faulting' states and hence
 * require action on the other participants.
 * 
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class BAwithAtomicOutcomeCoordinator extends BACoordinator {

	/**
	 * The object containing the outcome decision. 
	 */
	private final BAAtomicOutcome myOutcome = new BAAtomicOutcome();
	
	/**
	 * Default constructor, called by the activation service. Prepares a new
	 * empty business activity context.
	 * @param superContext The super context, null if there is no super context.
	 *  
	 * @throws MalformedURIException Indicates a severe programming error in the @see BACoordinator#COORDINATION_TYPE__ATOMIC constant.
	 * @throws InvalidOutcomeAssertion Indicates a severe programming error in the @see BACoordinator#COORDINATION_TYPE__ATOMIC constant.
	 */
	public BAwithAtomicOutcomeCoordinator(
			final CreateCoordinationContextTypeCurrentContext superContext
	)
		throws MalformedURIException, InvalidOutcomeAssertion
	{
		super(BACoordinator.COORDINATION_TYPE__ATOMIC, superContext);
	}

	/**
	 * This method adds handling logic for participants that reach one of the
	 * 'completed' or 'faulting' states and hence
	 * may require action on the other participants.
	 * 
	 * This method uses the following process logic:
	 * <ul>
	 * <li>faulting: decide to cancel or compensate all participants
	 * <li>completed: check if the outcome has already been decided. If yes, compensate; if no, check if all participants
	 * 	are in state 'completed' and wait for the initiator to decide.
	 * </ul> 
	 * 
	 * 
	 * @see org.apache.kandula.coordinator.ba.coordinator.BACoordinator#handleStateTransition(org.apache.kandula.coordinator.ba.coordinator.AbstractCoordParticipant, javax.xml.namespace.QName)
	 */
	public void handleStateTransition(
			final AbstractCoordParticipant participant, 
			final QName newState
	) throws RemoteException {
		
		synchronized(this){
//			if (false
//					|| State.STATE_FAULTING.equals(newState)
//					|| State.STATE_FAULTING_ACTIVE.equals(newState)
//			){
				// new State: Faulting! decide to cancel/compensate if not already done
				// remark: it is impossible that at this point it has already been decided to close, since
				// fault is no valid message when closing.
//				
				// TODO BA Perhaps it is better to support this as an optional feature.
				
//				if (! this.myOutcome.isDecided()){
//					System.out.println("Auto-deciding "+this.getID()+" to cancel/compensate due to participant fault ");
//					this.myOutcome.decideToCancelOrCompensate();
//				}
				
//				executeDecision();
//			}else 
			if (false
					|| State.STATE_COMPLETED.equals(newState)
			){
				// If it has already been decided to abort the transaction,
				// and a participant reports completed, automatically
				// compensate them.
				if (this.myOutcome.isDecidedToCancelOrCompensate()){
					participant.tellCompensate();
				}
			}
			
			super.handleStateTransition(participant, newState);
		}
	}

	/**
	 * Execute a given decision.
	 * This means, if decision is
	 * <ul>
	 * <li>undecided: throw illegalstateexception
	 * <li>close: tell all participants who are in state completed to close.
	 * <li>cancel or compensate: tell all participants who are in active or completing to cancel,
	 *  participants who are in completed to compensate and ignore all others.
	 * </ul>
	 *
	 */
	protected void executeDecision() {
		if (! this.myOutcome.isDecided())
			throw new IllegalStateException("No decision to execute: it's still undecided!");

		synchronized (this) {
			final Collection participants = this.getParticipants();
			final Iterator it = participants.iterator();

			while(it.hasNext()){
				final AbstractCoordParticipant part = (AbstractCoordParticipant) it.next();

				if (this.myOutcome.isDecidedToClose()){
					if (State.STATE_COMPLETED.equals( part.getCurrentState() ) ){
						// We expect all participants to be in this state...
						
						try{
							part.tellClose();
						}catch(Exception e){
							e.printStackTrace();
							// Ignore exceptions
						}
					}
					//	end Close Outcome 
				}else if (this.myOutcome.isDecidedToCancelOrCompensate()){
					final QName cState = part.getCurrentState();
					
					try{
					if (false 
							|| State.STATE_ACTIVE.equals( cState )
							|| State.STATE_COMPLETING.equals( cState )
					){
						part.tellCancel();
					}else if (false
							|| State.STATE_COMPLETED.equals( cState )
					){
						part.tellCompensate();
					}
					}catch(Exception e){
						e.printStackTrace();
						// ignore exception
					}
					
					// end cancel/compensate outcome
				}else{
					throw new IllegalArgumentException("Sorry, unknown decided outcome: "+this.myOutcome.getOutcome());
				}
			}
		}
	}

	/**
	 * Check if the current state of the transaction allows for additional
	 * @see #myOutcome
	 * @return true, if registration is possible.
	 *         false, if no more registrations are accepted.
	 * 
	 */
	boolean isRegistrationPossible() {
		return (! this.myOutcome.isDecided());
	}

	/**
	 * Return the outcome object.
	 * @return The outcome container. 
	 */
	public BAAtomicOutcome getOutcomeDecision() {
		return this.myOutcome;
	}

	
}
