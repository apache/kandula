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
package org.apache.kandula.coordinator.ba.participant;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.AnyContentType;
import org.apache.axis.message.MessageElement;
import org.apache.kandula.coordinator.CallbackRegistry;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.coordinator.ba.State;
import org.apache.kandula.coordinator.ba.WrongMethodCallException;
import org.apache.kandula.wsba.BusinessAgreementWithCoordinatorCompletionParticipantPortType;
import org.apache.kandula.wsba.BusinessAgreementWithParticipantCompletionParticipantPortType;
import org.apache.kandula.wsba.NotificationType;
import org.apache.kandula.wsba.StatusType;

/**
 * This class represents the receiving end of the participant side web services.
 * 
 * All SOAP requests are forwareded to one of the methods of this class, which then
 * <li>tries to correlate the request to an existing transaction participant,
 * <li>checks if the message is allowed in the current participant state and
 * <li>finally invokes the appropriate method on the participant.
 * </ul>
 * 
 * If one of those actions fails, an exception is thrown and sent to the caller's
 * fault handler.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class ParticipantService 
	implements BusinessAgreementWithParticipantCompletionParticipantPortType,
	BusinessAgreementWithCoordinatorCompletionParticipantPortType
{

	/**
	 * Handle an incoming message. That is, identify the participant the message is directed to,
	 * check if the message is allowed in the current state of that participant, and then 
	 * turn control over to the participant.
	 * 
	 * @param type Message Type
	 * @param param Any message parameters
	 * @throws WrongMethodCallException 
	 */
	private void handleIncomingMessage(final QName type, final AnyContentType param) throws RemoteException{
		//
		final AbstractParticipant participant = getBAParticipantOrThrowFault();
		
		final StringBuffer msg = new StringBuffer();
		msg.append("** PartServe incoming "+type+" for "+participant+": \n");

		if (param != null){
			final MessageElement[] es = param.get_any();
			if (es != null){
				for(int i=0; i<es.length; i++)
					msg.append("\t"+es[i].toString()+"\n");
			}
		}		
		System.out.println(msg);
		
		
		participant.handleIncomingMessage(type, param);
	}
	
	/**
	 * Retrieves the Participant who sent the current message.
	 * @return The participant, if found
	 * @throws RemoteException if no participant could be found
	 */
	protected AbstractParticipant getBAParticipantOrThrowFault() throws RemoteException {
		final AbstractParticipant p = (AbstractParticipant) CallbackRegistry.getInstance().correlateMessage(
				CallbackRegistry.CALLBACK_REF, false
		);

		// The message could not be correlated to a registered participant
		if (p == null){
			throw Coordinator.CONTEXT_REFUSED_SOAP_FAULT() ;
		}
		
		return p;
	}

	/**
	 * Handles an incoming COMPLETE message. 
	 * @param parameters Unused.
	 * @throws RemoteException Any fault that occured.
	 */
	public void completeOperation(NotificationType parameters) throws RemoteException {
		handleIncomingMessage(State.MESSAGE_COMPLETE, parameters);
	}

	/**
	 * Handles an incoming CLOSE message.
	 * @param parameters Unused.
	 * @throws RemoteException Any fault that occured.
	 */
	public void closeOperation(final NotificationType parameters) throws RemoteException {
		handleIncomingMessage(State.MESSAGE_CLOSE, parameters);
	}

	/**
	 * Handles an incoming CANCEL message. 
	 * @param parameters Unused.
	 * @throws RemoteException Any fault that occured.
	 */
	public void cancelOperation(final NotificationType parameters) throws RemoteException {
		handleIncomingMessage(State.MESSAGE_CANCEL, parameters);
	}

	/**
	 * Handles an incoming COMPENSATE message.
	 * @param parameters Unused.
	 * @throws RemoteException Any fault that occured.
	 */
	public void compensateOperation(final NotificationType parameters) throws RemoteException {
		handleIncomingMessage(State.MESSAGE_COMPENSATE, parameters);
	}

	/**
	 * Handles an incoming FAULTED message.
	 * @param parameters Unused.
	 * @throws RemoteException Any fault that occured.
	 */
	public void faultedOperation(final NotificationType parameters) throws RemoteException {
		handleIncomingMessage(State.MESSAGE_FAULTED, parameters);
	}

	/**
	 * Handles an incoming EXITED message.
	 * @param parameters Unused.
	 * @throws RemoteException Any fault that occured.
	 */
	public void exitedOperation(final NotificationType parameters) throws RemoteException {
		handleIncomingMessage(State.MESSAGE_EXITED, parameters);
	}

	/**
	 * Handles an incoming GETSTATUS message.
	 * @param parameters Unused.
	 * @throws RemoteException Any fault that occured.
	 */
	public void getStatusOperation(NotificationType parameters) throws RemoteException {
		handleIncomingMessage(State.MESSAGE_GETSTATUS, parameters);
	}

	/**
	 * 	Handles an incoming STATUS message.
	 * @param parameters The current state.
	 * @throws RemoteException Any fault that occured.
	 */
	public void statusOperation(StatusType parameters) throws RemoteException {
		handleIncomingMessage(State.MESSAGE_STATUS, parameters);
	}
}
