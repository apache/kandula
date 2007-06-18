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

import org.apache.kandula.coordinator.Callback;
import org.apache.kandula.coordinator.CallbackRegistry;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.coordinator.ba.State;
import org.apache.kandula.wsba.BusinessAgreementWithCoordinatorCompletionCoordinatorPortType;
import org.apache.kandula.wsba.BusinessAgreementWithParticipantCompletionCoordinatorPortType;
import org.apache.kandula.wsba.ExceptionType;
import org.apache.kandula.wsba.NotificationType;
import org.apache.kandula.wsba.StatusType;

/**
 * This is the class that represents the Business Activity protocol web service
 * to the client.
 * Internally, on an incoming message, it checks whether that message can be correlated
 * to an existing business activity, and forwards it to the BACoordinator object
 * managing that activity.
 * 
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 * 
 *
 */
public class BACoordinatorService implements
		BusinessAgreementWithCoordinatorCompletionCoordinatorPortType,
		BusinessAgreementWithParticipantCompletionCoordinatorPortType {

	/**
	 * Retrieves the BACoordinator associated with the coordinator context attached to
	 * the current message.
	 * If no context could be associated, an exception is thrown. This method never returns null.
	 * @return The BACoordinator.
	 * @throws RemoteException
	 */
	public static BACoordinator getBACoordinatorOrThrowFault() throws RemoteException{
		final Callback c = CallbackRegistry.getInstance().correlateMessage(
				CallbackRegistry.CALLBACK_REF, false
		);
			
		if (c == null)
			throw Coordinator.CONTEXT_REFUSED_SOAP_FAULT();
			
		if (! (c instanceof BACoordinator) || c == null)
			throw Coordinator.CONTEXT_REFUSED_SOAP_FAULT();
		
		return (BACoordinator) c;
	}
	
	/**
	 * Retrieves the Participant who sent the current message.
	 * @param context The current transaction's context.
	 * @return The participant, if found
	 * @throws RemoteException if no participant could be found
	 */
	protected static AbstractCoordParticipant getBAParticipantOrThrowFault(final BACoordinator context) throws RemoteException {
		final AbstractCoordParticipant p = (AbstractCoordParticipant) CallbackRegistry.getInstance().correlateMessage(
				Coordinator.PARTICIPANT_REF, false
		);

		// The message could not be correlated to a registered participant
		if (p == null){
			throw Coordinator.CONTEXT_REFUSED_SOAP_FAULT() ;
		}

		// The message contained a reference to a participant and a context,
		// but the participant is not registered in the context
		if (! context.containsParticipant(p)){
			throw Coordinator.CONTEXT_REFUSED_SOAP_FAULT();
		}
		
		return p;
	}

	/*
	 * Below are the web service's methods 
	 */
	
	/**
	 * Completed.
	 * @param parameters Any parameters. 
	 * @throws RemoteException Any fault thrown while processing the incoming message.
	 */
	public void completedOperation(final NotificationType parameters)
			throws RemoteException {
		handleIncomingMessage(State.MESSAGE_COMPLETED, parameters);
	}

	/**
	 * Fault.
	 * @param parameters Any parameters. 
	 * @throws RemoteException Any fault thrown while processing the incoming message.
	 */
	public void faultOperation(final ExceptionType parameters) throws RemoteException {
		handleIncomingMessage(State.MESSAGE_FAULT, parameters);
	}

	/**
	 * Compensated.
	 * @param parameters Any parameters. 
	 * @throws RemoteException Any fault thrown while processing the incoming message.
	 */
	public void compensatedOperation(final NotificationType parameters)
			throws RemoteException {
		handleIncomingMessage(State.MESSAGE_COMPENSATED, parameters);
	}

	/**
	 * Closed
	 * @param parameters Any parameters. 
	 * @throws RemoteException Any fault thrown while processing the incoming message.
	 */
	public void closedOperation(final NotificationType parameters)
			throws RemoteException {
		handleIncomingMessage(State.MESSAGE_CLOSED, parameters);
	}

	/**
	 * Canceled
	 * @param parameters Any parameters. 
	 * @throws RemoteException Any fault thrown while processing the incoming message.
	 */
	public void canceledOperation(final NotificationType parameters)
			throws RemoteException {
		handleIncomingMessage(State.MESSAGE_CANCELED, parameters);
	}

	/**
	 * Exit
	 * @param parameters Any parameters. 
	 * @throws RemoteException Any fault thrown while processing the incoming message.
	 */
	public void exitOperation(final NotificationType parameters)
			throws RemoteException {
		handleIncomingMessage(State.MESSAGE_EXIT, parameters);
	}

	/**
	 * GetStatus
	 * @param parameters Any parameters. 
	 * @throws RemoteException Any fault thrown while processing the incoming message.
	 */
	public void getStatusOperation(final NotificationType parameters)
			throws RemoteException {
		handleIncomingMessage(State.MESSAGE_GETSTATUS, parameters);
	}

	/**
	 * Status
	 * @param parameters Any parameters. 
	 * @throws RemoteException Any fault thrown while processing the incoming message.
	 */
	public void statusOperation(final StatusType parameters) throws RemoteException {
		handleIncomingMessage(State.MESSAGE_STATUS, parameters);
	}

	/*
	 * End Web Service Methods
	 */
	
	
	/**
	 * Handle an incoming messages. That is, fetch the coordination context associated 
	 * with the given coordination context references; fetch the participant associated
	 * with the given participant references;
	 * and pass further message processing to the coordinator-side participant object.
	 * @param messageType The message's type. @see CoordinationStates
	 * @param params The message's parameters.
	 * @throws RemoteException If context or participant could not be created, or message processing
	 *   by the participant was not successfull, we throw an error back to the calling party.
	 */
	protected void handleIncomingMessage(final QName messageType, final NotificationType params) throws RemoteException{

		final BACoordinator coord = getBACoordinatorOrThrowFault();
		final AbstractCoordParticipant part = getBAParticipantOrThrowFault(coord);

		System.out.println("+++ CoordIncoming: "+messageType+" ("+params+"+ in TX "+coord+" from participant "+part);
	
		if (messageType.equals(State.MESSAGE_GETSTATUS)){
			// Reply with the status
			
			part.tellStatus();
		}else{
			part.handleIncomingMessage(messageType, params);
		}
	}
	/**
	 * Handle an incoming messages. That is, fetch the coordination context associated 
	 * with the given coordination context references; fetch the participant associated
	 * with the given participant references;
	 * and pass further message processing to the coordinator-side participant object.
	 * @param messageType The message's type. @see CoordinationStates
	 * @param params The message's parameters.
	 * @throws RemoteException If context or participant could not be created, or message processing
	 *   by the participant was not successfull, we throw an error back to the calling party.
	 */
	protected void handleIncomingMessage(final QName messageType, final ExceptionType params) throws RemoteException{

		final BACoordinator coord = getBACoordinatorOrThrowFault();
		final AbstractCoordParticipant part = getBAParticipantOrThrowFault(coord);

		System.out.println("+++ CoordIncoming: "+messageType+" ("+params+"+ in TX "+coord+" from participant "+part);
		part.handleIncomingMessage(messageType, params);		
	}
	/**
	 * Handle an incoming messages. That is, fetch the coordination context associated 
	 * with the given coordination context references; fetch the participant associated
	 * with the given participant references;
	 * and pass further message processing to the coordinator-side participant object.
	 * @param messageType The message's type. @see CoordinationStates
	 * @param params The message's parameters.
	 * @throws RemoteException If context or participant could not be created, or message processing
	 *   by the participant was not successfull, we throw an error back to the calling party.
	 */
	protected void handleIncomingMessage(final QName messageType, final StatusType params) throws RemoteException{

		final BACoordinator coord = getBACoordinatorOrThrowFault();
		final AbstractCoordParticipant part = getBAParticipantOrThrowFault(coord);

		System.out.println("+++ CoordIncoming: "+messageType+" ("+params+"+ in TX "+coord+" from participant "+part);
		part.handleIncomingMessage(messageType, params);
	}
}
