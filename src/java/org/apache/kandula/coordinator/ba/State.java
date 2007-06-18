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

package org.apache.kandula.coordinator.ba;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;

/**
 * Abstract Class for State handling.</br> </br> Participant must handle
 * ParticipantState</br> Coordinator must handle CoordinationState
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 * 
 */
public abstract class State {

	/**
	 * The protocol this state table is configured for.
	 */
	protected final QName protocolIdentifier;

	/**
	 * Default constructor. </br> Stores protocol identifier into final local
	 * variable for state transitions.
	 * 
	 * @param xprotocolIdentifier The protocol the state table will be configured for.
	 */
	public State(final QName xprotocolIdentifier) {
		if (!xprotocolIdentifier.equals(ProtocolType.PROTOCOL_ID_CC)
				|| !xprotocolIdentifier.equals(ProtocolType.PROTOCOL_ID_PC)) {
			this.protocolIdentifier = xprotocolIdentifier;
		} else
			throw new IllegalArgumentException("Protocoltype is unknown: "
					+ xprotocolIdentifier.toString());
	}

	/**
	 * Return an invalid state soap fault.
	 * as a troubleshooting help.
	 * @return The ready made exception.
	 */
	public static AxisFault GET_INVALID_STATE_SOAP_FAULT(){
		return  new AxisFault(
			new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor",
					"InvalidState"),
			"The message was invalid for the current state of the activity.",
			null, null);
	}
	
	/**
	 * Return an invalid state soap fault, but include the current state
	 * as a troubleshooting help.
	 * @param currentState The current state.
	 * @return The ready made exception.
	 */
	public static AxisFault GET_INVALID_STATE_SOAP_FAULT(final QName currentState) {
		return  new AxisFault(
				new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor",
						"InvalidState"),
				"The message was invalid for the current state of the activity."+
				" Current state was: "+currentState.toString(),
				null, null);
	}
	
	/**
	 * Return a new AxisFault, ready configured as wscoor:MessageUnknown invalid message fault.
	 * If axis is configured to include a stack trace, calling this as a method instead of providing a 
	 * static field will ensure the stack trace is correct. 
	 * @return wscoor:UnknownMessage
	 */
	public static AxisFault GET_INVALID_MESSAGE_SOAP_FAULT(){
		return new AxisFault(
			new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor",
					"MessageUnknown"),
			"The message was not known for the current state of the activity.",
			null, null);
	}
	
	/*
	 * All possible states.
	 */
	
	/**
	 * The WSBA:Active State.
	 */
	public static final QName STATE_ACTIVE = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Active");

	/**
	 * The WSBA:Cancelling State.
	 */
	public static final QName STATE_CANCELLING = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Canceling");

	/**
	 * The WSBA:Canceling-Active State.
	 */
	public static final QName STATE_CANCELLING_ACTIVE = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Canceling-Active");

	/**
	 * The WSBA:Canceling-Completing State.
	 */
	public static final QName STATE_CANCELLING_COMPLETING = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Canceling-Completing");

	/**
	 * The WSBA:Completing State.
	 */
	public static final QName STATE_COMPLETING = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Completing");

	/**
	 * The WSBA:Completed State.
	 */
	public static final QName STATE_COMPLETED = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Completed");

	/**
	 * The WSBA:Closing State.
	 */
	public static final QName STATE_CLOSING = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Closing");

	/**
	 * The WSBA:Compensating State.
	 */
	public static final QName STATE_COMPENSATING = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Compensating");

	/**
	 * The WSBA:Faulting State.
	 */
	public static final QName STATE_FAULTING = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Faulting");

	/**
	 * The WSBA:Faulting-Active State.
	 */
	public static final QName STATE_FAULTING_ACTIVE = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Faulting-Active");

	/**
	 * The WSBA:Faulting-Compensating State.
	 */
	public static final QName STATE_FAULTING_COMPENSATING = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Faulting-Compensating");

	/**
	 * The WSBA:Exiting State.
	 */
	public static final QName STATE_EXITING = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Exiting");

	/**
	 * The WSBA:Ended State.
	 */
	public static final QName STATE_ENDED = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Ended");

	/*
	 * All possible messages.
	 */
	/**
	 * The WSBA:Cancel Message.
	 */
	public static final QName MESSAGE_CANCEL = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Cancel");

	/**
	 * The WSBA:Canceled Message.
	 */
	public static final QName MESSAGE_CANCELED = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Canceled");

	/**
	 * The WSBA:Close Message.
	 */
	public static final QName MESSAGE_CLOSE = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Close");

	/**
	 * The WSBA:Closed Message.
	 */
	public static final QName MESSAGE_CLOSED = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Closed");

	/**
	 * The WSBA:Compensate Message.
	 */
	public static final QName MESSAGE_COMPENSATE = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Compensate");

	/**
	 * The WSBA:Compensated Message.
	 */
	public static final QName MESSAGE_COMPENSATED = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Compensated");

	/**
	 * The WSBA:Complete Message.
	 */
	public static final QName MESSAGE_COMPLETE = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Complete");

	/**
	 * The WSBA:Completed Message.
	 */
	public static final QName MESSAGE_COMPLETED = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Completed");

	/**
	 * The WSBA:Exit Message.
	 */
	public static final QName MESSAGE_EXIT = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Exit");

	/**
	 * The WSBA:Exited Message.
	 */
	public static final QName MESSAGE_EXITED = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Exited");

	/**
	 * The WSBA:Fault Message.
	 */
	public static final QName MESSAGE_FAULT = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Fault");

	/**
	 * The WSBA:Faulted Message.
	 */
	public static final QName MESSAGE_FAULTED = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Faulted");

	/**
	 * The WSBA:Status Message.
	 */
	public static final QName MESSAGE_STATUS = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}Status");

	/**
	 * The WSBA:GetStatus Message.
	 */
	public static final QName MESSAGE_GETSTATUS = QName
			.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba}GetStatus");

	/**
	 * The current state QName.
	 */
	protected QName currState = null;

	/**
	 * A list of the previous states.
	 */
	protected final Vector previousStates = new Vector();

	/**
	 * Fetch the state map.
	 * @return The <State, <Message, AbstractStateTransition>> map
	 */
	abstract protected Map getStates();

	/**
	 * Method for getting the StateTransition initiated by the received Message
	 * in the current ProtocolType.
	 * 
	 * This method never returns null.
	 * 
	 * @param message The incoming message type QName.
	 * 
	 * @return The state transition for the incoming message.
	 */
	private AbstractStateTransition getStateTransition(final QName message) {
		final HashMap temp = (HashMap) ((HashMap) getStates().get(
				this.protocolIdentifier)).get(this.currState);
		final AbstractStateTransition returnStateTransition = (AbstractStateTransition) temp
				.get(message);
		if (returnStateTransition != null)
			return returnStateTransition;
		return new StateTransitionIgnore();
	}

	/**
	 * Fetch the state table from the "other" side, that is: if we have the
	 * participant's states, returns the coordination's states; if we have the
	 * coordinations's states, returns the participant's states.
	 * 
	 * This method is used by
	 * 
	 * @see #getMessageForTransition(QName, QName) to determine which message makes the
	 *      peer transist from the previous state to the current state. This is
	 *      done when we receive a message that is consistent with the previous
	 *      state, but not the current one: then we need to resend our last
	 *      message, but since we didn't store it, we need to check what it was.
	 * @return Our counterpart's state table.
	 */
	protected abstract HashMap getOtherStates();

	/**
	 * Method for getting the Message to be received in order to transist from a
	 * given previous state to the current state in the current ProtocolType. In
	 * order to be able to correctly translate the last state (state before the
	 * current state) <code>lastState</code> the second last state (state
	 * before the last state) <code>secondLastState</code> has to be passed.
	 * 
	 * @param lastState
	 * @param secondLastState
	 * @return <code>null</code>, if it is not possible to transist with some
	 *         message from a given previous state to the current state
	 */
	public QName getMessageForTransition(QName lastState, final QName secondLastState) {
		this.translateState(lastState, secondLastState);
		final HashMap temp = (HashMap) ((HashMap) getOtherStates().get(
				this.protocolIdentifier)).get(lastState);

		final Set messages = temp.keySet();

		for (final Iterator it = messages.iterator(); it.hasNext();) {
			final QName message = (QName) it.next();
			final AbstractStateTransition stateTrans = (AbstractStateTransition) temp
					.get(message);

			if (stateTrans instanceof StateTransitionState) {
				final StateTransitionState possibleTrans = (StateTransitionState) stateTrans;
				try {
					if (possibleTrans.getState() == getCurrentState()) {
						return message;
					}
				} catch (WrongMethodCallException e) {
					// Can be ignored.
				}
			}
		}
		return null;
	}

	/**
	 * Get the currentState.
	 * 
	 * @return The current state.
	 */
	public QName getCurrentState() {
		return this.currState;
	}

	/**
	 * Get the result state.
	 * When the participant is in state ENDED,
	 * returns one of the following states that were passed:
	 * <ul>
	 *  <li>Exiting</li>
	 *  <li>Canceling</li>
	 *  <li>Closing</li>
	 *  <li>Compensating</li>
	 *  <li>Faulting</li>
	 * </ul>
	 * 
	 * If the participant is in COMPLETED, return COMPLETED.
	 * 
	 * Else, returns ACTIVE.
	 * 
	 * 
	 * @return Returns the state characterizing the participant's result best.
	 */
	public QName getResultState(){
		if (State.STATE_ENDED.equals(this.currState)){
			return this.getLastState();
		}
		if (false
				|| State.STATE_COMPLETED.equals(this.currState)
				|| State.STATE_CLOSING.equals(this.currState)
				|| State.STATE_COMPENSATING.equals(this.currState)
				|| State.STATE_FAULTING_COMPENSATING.equals(this.currState)
			){
			return State.STATE_COMPLETED;
		}
		return State.STATE_ACTIVE;
	}
	
	/**
	 * Setter for CurrentState
	 * 
	 * @param xcurrState Set the current state.
	 */
	protected synchronized void setCurrentState(final QName xcurrState) {
		if (xcurrState == null) {
			throw new NullPointerException("cannot set current state to 'null'");
		}
		if (xcurrState.equals(this.currState))
			return;

		if (this.currState != null)
			this.previousStates.add(this.currState);

		// DEBUG debugoutput
		System.out.println("*** State changed to " + xcurrState + " from "
				+ this.currState);

		this.currState = xcurrState;
	}

	/**
	 * Getter for the last known state before the current state.
	 * 
	 * @return Return the previous state; null, if there is none.
	 */
	public QName getLastState() {
		if (this.previousStates.size() > 0) {
			return (QName) this.previousStates.lastElement();
		}
		return null;
	}

	/**
	 * Getter for the state before the last state.
	 * 
	 * @return the second last state; null, if there is none.
	 */
	public QName getSecondLastState() {
		if (this.previousStates.size() >= 2) {
			return (QName) this.previousStates.get(this.previousStates.size()-2);
		}
		return null;
	}

	/**
	 * Revert the current state to the last known state.
	 */
	public void revertState() {
		if (this.previousStates.size() > 0) {
			this.currState = (QName) this.previousStates.lastElement();
			this.previousStates.remove(this.previousStates.size() - 1);
		} else {
			/*
			 * TODO GH throw Exception???
			 */
		}
	}

	/**
	 * Method for transiting the State as specified by the HashMap
	 * 
	 * @param message
	 *            The message received
	 * @return Method only returns an AbstractStateTransition in case of needed
	 *         resend of message of current state (state is unchanged)
	 * @throws AxisFault
	 *             thrown in case that the received Message is not allowed for
	 *             the current state or the message is not known for the current
	 *             state
	 * @throws WrongMethodCallException
	 *             thrown if a method on an instance of AbstactStateTransition
	 *             is called, that is not allowed for this instance
	 * @throws IllegalArgumentException
	 *             thrown if the stateTransition is of no known instance
	 */
	public synchronized AbstractStateTransition transistStateByMessage(
			final QName message) throws AxisFault, WrongMethodCallException {
		final AbstractStateTransition stateTransition = this
				.getStateTransition(message);
		if (stateTransition instanceof StateTransitionFault) {
			final StateTransitionFault stateTransitionFault = (StateTransitionFault) stateTransition;
			throw stateTransitionFault.getAxisFault();
		} else if (stateTransition instanceof StateTransitionResend) {
			final StateTransitionResend stateTransitionResend = (StateTransitionResend) stateTransition;
			return stateTransitionResend;
		} else if (stateTransition instanceof StateTransitionIgnore) {
			return null;
		} else if (stateTransition instanceof StateTransitionState) {
			final StateTransitionState stateTransitionState = (StateTransitionState) stateTransition;
			this.setCurrentState(stateTransitionState.getState());
			return stateTransitionState;
		} else if (stateTransition instanceof StateTransitionResendPreviousState) {
			final StateTransitionResendPreviousState stateTransitionResendPrevState = (StateTransitionResendPreviousState) stateTransition;
			return stateTransitionResendPrevState;
		} else {
			// stateTransition is of no known type
			throw new IllegalArgumentException(
					"The StateTransition is of an unknown instance:"
							+ stateTransition.getClass().getName());
		}
	}

	/**
	 * Tests if the passed message is allowed in the current state and if so
	 * resets the current state to the state after sending the message.
	 * 
	 * @param message
	 *            the message to test
	 * @return <code>true</code> if the message is possible in the current
	 *         state which is the last state when returned to calling method,
	 *         else <code>false</code>
	 */
	public synchronized boolean handleOutgoingMessage(final QName message) {
		final AbstractStateTransition possibleTrans = this
				.isMessagePossible(message);
		if (possibleTrans instanceof StateTransitionState) {
			final StateTransitionState transition = (StateTransitionState) possibleTrans;
			try {
				this.setCurrentState(
						this.reverseTranslateState(
								transition.getState(), 
								this.getCurrentState()
								)
						);
			} catch (WrongMethodCallException e) {
				// This Exception can not occur here since we ensure that it is
				// a StateTransitionState which implements this method without
				// throwing the error
			}
			return true;
		}
		return false;
	}

	/**
	 * Tests with the Hashmap of the other side (CoordinatorState in case of
	 * ParticipantState and viceversa) if the message can be receipt.
	 * 
	 * @param message
	 *            the message to test
	 * @return <code>true</code> if the message can be receipt by the other
	 *         side in the current state, else <code>false</code>
	 */
	protected AbstractStateTransition isMessagePossible(final QName message) {
		final HashMap otherSide = (HashMap) ((HashMap) getOtherStates().get(
				this.protocolIdentifier)).get(this.translateState(
				this.getCurrentState(), this.getLastState()));
		return (AbstractStateTransition) otherSide.get(message);
	}


	/**
	 * If the protocol is CoordinationCompletion and the state (<code>state</code>)
	 * is either <code>STATE_CANCELLING_ACTIVE</code> or
	 * <code>STATE_CANCELLING_COMPLETING</code> then the variable state has to
	 * be changed to reference <code>STATE_CANCELLING</code> since
	 * <code>ParticipantState</code> is called.

	 * @param state
	 * 			  The current state. 
	 * 
	 * @param prevState
	 *            The previous state.
	 *            
	 * @return The translated state.
	 * 
	 * @see org.apache.kandula.coordinator.ba.State#translateState(javax.xml.namespace.QName,
	 *      javax.xml.namespace.QName)
	 */
	protected abstract QName translateState(final QName state, final QName prevState);

	/**
	 * If the protocol is CoordinationCompletion and the state (<code>state</code>)
	 * is <code>STATE_CANCELLING</code> then the variable state has to be
	 * changed to reference <code>STATE_CANCELLING_ACTIVE</code> or
	 * <code>STATE_CANCELLING_COMPLETING</code> since this is
	 * <code>CoordinationState</code>.
	 * 
	 * @param state
	 * 			The current state.
	 *  
	 * @param prevState
	 * 			The previous state.
	 * 
	 * @return 
	 * 			The translated state.
	 * 
	 * @see org.apache.kandula.coordinator.ba.State#reverseTranslateState(javax.xml.namespace.QName,
	 *      javax.xml.namespace.QName)
	 */
	protected abstract QName reverseTranslateState(QName state, final QName prevState);
	
	/**
	 * Translate the given states to participant view.
	 * (In fact does this method no more that to simplify CANCELLING_ACTIVE
	 *  and CANCELLING_COMPLETING into CANCELLING.)
	 * 
	 * @param state The current state of the participant.
	 * @param prevState The previous state of the participant.
	 * @return The current state, translated for the participant's view.
	 */
	public static QName translateStateToParticipantView(final QName state, final QName prevState) {
		/*
		 * dirty hack to remove warning
		 */
		if (prevState != null) {
			prevState.toString();
		}
		
		final QName returnState;
		if (false
				|| state.equals(STATE_CANCELLING_ACTIVE) 
				|| state.equals(STATE_CANCELLING_COMPLETING)) {
			returnState = STATE_CANCELLING;
		}else{
			returnState = state;
		}
		return returnState;
	}
	

	/**
	 * Translate the given states to coordinator view view.
	 * (In fact does this method no more that to split CANCELLING into
	 *  CANCELLING_ACTIVE and CANCELLING_COMPLETING .)
	 *  
	 * @param protocol The current protocol type. 
	 * @param state The current state of the participant.
	 * @param prevState The previous state of the participant.
	 * @return The current state, translated for the coordinator's view.
	 */
	public static QName translateStateToCoordinatorView(
			final QName protocol,
			final QName state, 
			final QName prevState
	) {
		final QName returnState;
		
		if (ProtocolType.PROTOCOL_ID_CC.equals(protocol)
				&&	state.equals(STATE_CANCELLING)) {
			if (prevState.equals(STATE_ACTIVE)) {
				returnState = STATE_CANCELLING_ACTIVE;
			} else if (prevState.equals(STATE_COMPLETING)) {
				returnState = STATE_CANCELLING_COMPLETING;
			} else {
				throw new InternalError("no possible state");
			}
		}else{
			returnState = state;
		}
		
		return returnState;
	}
	
	
}
