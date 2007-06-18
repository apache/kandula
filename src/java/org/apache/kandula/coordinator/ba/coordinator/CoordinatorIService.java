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
import org.apache.kandula.wscoor.CreateCoordinationContextResponseType;

import org.apache.kandula.wsbai.CoordinatorInitiatorProtocolPortType_AllProtocols;
import org.apache.kandula.wsbai.BAParticipantReferenceType;
import org.apache.kandula.wsbai.BAParticipantType;
import org.apache.kandula.wsbai.GetCoordCtxWCodeReqType;
import org.apache.kandula.wsbai.ListParticipantsReqType;


/**
 * This class handles requests sent from the initiator to the coordinator. It receives
 * the messages, correlates them to a running business activity and either fetches the requested
 * information from the business activity coordinator or forwards commands to it.
 * 
 * To avoid inconsistencies, access to BACoordinator's fields is always synchronized on the 
 * BACoordinator object, even if the intended access is read-only.  
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
 
public abstract class CoordinatorIService 
implements CoordinatorInitiatorProtocolPortType_AllProtocols 
{

	/**
	 * Return a coordination context response, containing a registration service that
	 * is tagged with the given matchcode.
	 * throws faults if called with an empty or in-use matchcode.
	 * 
	 * Synchronization is handled by the BACoordinator.
	 * @param withMatchcode The matchcode request.
	 * @return The Coordination Context, tagged with the match code.
	 * @throws RemoteException 
	 */
	public CreateCoordinationContextResponseType getCoordinationContextWithMatchcode(final GetCoordCtxWCodeReqType withMatchcode) throws RemoteException {
		final BACoordinator context = BACoordinatorService.getBACoordinatorOrThrowFault();

		context.ensureRequestOriginatesFromInitiatorOrThrowFault();

		return new CreateCoordinationContextResponseType(
				context.registerMatchcode(withMatchcode.getMatchcode()),
				null
		);
	}


	/**
	 * List the current participants of the coordination context, complete by matchcode
	 * and with their current states.
	 * @param req The Request. 
	 * 
	 * @return An unsorted array of the current participants, complete with state and matchcode.
	 * @throws RemoteException 
	 */
	public BAParticipantType[] listParticipants(
			final ListParticipantsReqType req
	) throws RemoteException {
		// to avoid warning
		if (req != null) {
			req.toString();
		}
		
		final BACoordinator context = BACoordinatorService.getBACoordinatorOrThrowFault();
		context.ensureRequestOriginatesFromInitiatorOrThrowFault();

		synchronized (context) {
			return context.buildParticipantList();
		}
	}


	/**
	 * Tells the coordinator to complete the selected participants. This is valid
	 * both in transaction with the "mixed outcome" and the "atomic outcome" assertions.
	 * 
	 * This method may be called as many times as the initiator wishes. If a 
	 * selected participant is in a state where it is not allowed to complete,
	 * it is silently ignored.
	 * Hence, the initiator must check the return for the participants' current
	 * states to ensure its request was satisfactorily fulfilled.
	 * @param participantsToComplete References to the participants that shall be completed.
	 * 
	 * @return A list of the current participants and their states.
	 * @throws RemoteException 
	 */
	public BAParticipantType[] completeParticipants(
			final BAParticipantReferenceType[] participantsToComplete
	) throws RemoteException {
		return notifyParticipants(null, participantsToComplete, State.MESSAGE_COMPLETE, false, true);
	}
	
	/**
	 * Send some message to the given participants.
	 * @param checkOutcomeType If supplied, check if the outcome type is equal to checkOutcomeType 
	 * @param participants References to the participants that shall be notified.
	 * @param message The message to send.
	 * @param notifyPC Notify PC participants?
	 * @param notifyCC Notify CC participants?
	 * @return Updated list of participant information.
	 * @throws RemoteException 
	 */
	protected BAParticipantType[] notifyParticipants(
			final String checkOutcomeType,
			final BAParticipantReferenceType[] participants,
			final QName message,
			final boolean notifyPC,
			final boolean notifyCC
	) throws RemoteException{
		final BACoordinator context = BACoordinatorService.getBACoordinatorOrThrowFault();
		context.ensureRequestOriginatesFromInitiatorOrThrowFault();

		/*
		 * Synchronize on the context to ensure that only one thread
		 * sends out messages and performs state changes
		 */ 
		synchronized(context){
			
			if (checkOutcomeType != null &&
					(! checkOutcomeType.equals(context.getOutcomeType() ) )
			){
				throw new RemoteException(checkOutcomeType+" initiator method called, but the given context's outcome type is: "+context.getOutcomeType());
			}
			
			
			if (participants != null && participants.length>0){
				for(int i=0; i<participants.length; i++){
					// Skip empty slots
					if (participants[i] == null)
						continue;
					
					// Fetch the current match code
					final String matchCode = participants[i].getParticipantMatchcode();
					
					// Check if the participant is known
					final AbstractCoordParticipant part =
						context.getParticipantByMatchcode(matchCode);
					
					if (part == null)
					{
						// No participant found - ignore
						continue ;
					}
					
					try{
						/*
						 *  TODO WSBA When sending notifications, we perhaps have to
						 *  record the target state if the participant has not
						 *  yet progressed far enough to receive the requested
						 *  message.
						 */
						
						// Participant found - check if PC
						if (part instanceof BAwPCCoordParticipant && notifyPC){
							part.tell(message);
						}
						// Participant found - check if CC
						if (part instanceof BAwCCCoordParticipant && notifyCC){
							part.tell(message);
						}
					}catch(Exception e){
						// ignore any fault thrown by the participant
						e.printStackTrace();
					}
				} // end iterate through the requested match codes		
			} // end check if at least one participant was requested to complet
			
			return context.buildParticipantList();
		} // end synchronized
	}
}
