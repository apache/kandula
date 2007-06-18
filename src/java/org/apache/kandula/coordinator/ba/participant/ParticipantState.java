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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.kandula.coordinator.ba.ProtocolType;
import org.apache.kandula.coordinator.ba.State;
import org.apache.kandula.coordinator.ba.StateTransitionFault;
import org.apache.kandula.coordinator.ba.StateTransitionIgnore;
import org.apache.kandula.coordinator.ba.StateTransitionResend;
import org.apache.kandula.coordinator.ba.StateTransitionResendPreviousState;
import org.apache.kandula.coordinator.ba.StateTransitionState;
import org.apache.kandula.coordinator.ba.coordinator.CoordinationState;

/**
 * Implementation of State with the Hashmap set for the different states of a
 * pariticipant.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 * 
 */
public class ParticipantState extends State {
	
	/**
	 * The state map.
	 */
	protected final static HashMap states = new HashMap();

	static {
		{
			/*
			 * Initializing the HashMap for the ParticipantCompletion - Protocol
			 */
			final HashMap pc = new HashMap();
			
			{
				// Messages in state ACTIVE
				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCEL, new StateTransitionState(
						STATE_CANCELLING));
				hm.put(MESSAGE_CLOSE, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATE, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXITED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULTED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				pc.put(STATE_ACTIVE, hm);
			}
			{
				// Messages in state CANCELLING
				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCEL, new StateTransitionIgnore());
				hm.put(MESSAGE_CLOSE, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATE, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXITED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULTED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				pc.put(STATE_CANCELLING, hm);
			}
			{
				// Messages in state COMPLETED
				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCEL, new StateTransitionResend(
						MESSAGE_COMPLETED));
				hm.put(MESSAGE_CLOSE, new StateTransitionState(
						STATE_CLOSING));
				hm.put(MESSAGE_COMPENSATE, new StateTransitionState(
						STATE_COMPENSATING));
				hm.put(MESSAGE_EXITED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULTED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				pc.put(STATE_COMPLETED, hm);
			}
			{
				// Messages in state CLOSING
				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCEL, new StateTransitionIgnore());
				hm.put(MESSAGE_CLOSE, new StateTransitionIgnore());
				hm.put(MESSAGE_COMPENSATE, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXITED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULTED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				pc.put(STATE_CLOSING, hm);
			}
			{
				// Messages in state COMPENSATING
				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCEL, new StateTransitionIgnore());
				hm.put(MESSAGE_CLOSE, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATE, new StateTransitionIgnore());
				hm.put(MESSAGE_EXITED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULTED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				pc.put(STATE_COMPENSATING, hm);
			}
			{
				// Messages in state FAULTING_ACTIVE
				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCEL, new StateTransitionResend(
						MESSAGE_FAULT));
				hm.put(MESSAGE_CLOSE, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATE, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXITED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULTED, new StateTransitionState(
						STATE_ENDED));
				pc.put(STATE_FAULTING_ACTIVE, hm);
			}
			{
				// Messages in state FAULTING_COMPENSATING
				
				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCEL, new StateTransitionIgnore());
				hm.put(MESSAGE_CLOSE, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATE, new StateTransitionResend(
						MESSAGE_FAULT));
				hm.put(MESSAGE_EXITED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULTED, new StateTransitionState(
						STATE_ENDED));
				pc.put(STATE_FAULTING_COMPENSATING, hm);
			}
			{
				// Messages in state EXITING
				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCEL, new StateTransitionResend(
						MESSAGE_EXIT));
				hm.put(MESSAGE_CLOSE, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATE, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXITED, new StateTransitionState(
						STATE_ENDED));
				hm.put(MESSAGE_FAULTED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				pc.put(STATE_EXITING, hm);
			}
			{
				// Messages in state ENDED
				
				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCEL,
						new StateTransitionResendPreviousState());
				hm.put(MESSAGE_CLOSE,
						new StateTransitionResendPreviousState());
				hm.put(MESSAGE_COMPENSATE,
						new StateTransitionResendPreviousState());
				hm.put(MESSAGE_EXITED, new StateTransitionIgnore());
				hm.put(MESSAGE_FAULTED, new StateTransitionIgnore());
				pc.put(STATE_ENDED, hm);
			} // end initialize pc
			states.put(ProtocolType.PROTOCOL_ID_PC, pc);
			
			{
				/*
				 * Initializing the HashMap for the CoordinatorCompletion - Protocol
				 */
				final HashMap cc = new HashMap();
				
				{
					// Messages in state ACTIVE
					
					final HashMap hm = new HashMap();
					hm.put(MESSAGE_CANCEL, new StateTransitionState(
							STATE_CANCELLING));
					hm.put(MESSAGE_CLOSE, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_COMPENSATE, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_COMPLETE, new StateTransitionState(
							STATE_COMPLETING));
					hm.put(MESSAGE_EXITED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_FAULTED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					cc.put(STATE_ACTIVE, hm);
				}
				{
					// Messages in state CANCELLING
					final HashMap hm = new HashMap();
					hm.put(MESSAGE_CANCEL, new StateTransitionIgnore());
					hm.put(MESSAGE_CLOSE, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_COMPENSATE, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_COMPLETE, new StateTransitionIgnore());
					hm.put(MESSAGE_EXITED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_FAULTED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					cc.put(STATE_CANCELLING, hm);
				}
				{
					// Messages in state COMPLETING
					final HashMap hm = new HashMap();
					hm.put(MESSAGE_CANCEL, new StateTransitionState(
							STATE_CANCELLING));
					hm.put(MESSAGE_CLOSE, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_COMPENSATE, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_COMPLETE, new StateTransitionIgnore());
					hm.put(MESSAGE_EXITED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_FAULTED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					cc.put(STATE_COMPLETING, hm);
				}
				{
					// Messages in state COMPLETED
					final HashMap hm = new HashMap();
					hm.put(MESSAGE_CANCEL, new StateTransitionResend(
							MESSAGE_COMPLETED));
					hm.put(MESSAGE_CLOSE, new StateTransitionState(
							STATE_CLOSING));
					hm.put(MESSAGE_COMPENSATE, new StateTransitionState(
							STATE_COMPENSATING));
					hm.put(MESSAGE_COMPLETE, new StateTransitionResend(
							MESSAGE_COMPLETED));
					hm.put(MESSAGE_EXITED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_FAULTED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					cc.put(STATE_COMPLETED, hm);
				}
				{
					// Messages in state CLOSING
					final HashMap hm = new HashMap();
					hm.put(MESSAGE_CANCEL, new StateTransitionIgnore());
					hm.put(MESSAGE_CLOSE, new StateTransitionIgnore());
					hm.put(MESSAGE_COMPENSATE, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_COMPLETE, new StateTransitionIgnore());
					hm.put(MESSAGE_EXITED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_FAULTED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					cc.put(STATE_CLOSING, hm);
				}
				{
					// Messages in state COMPENSATING
					final HashMap hm = new HashMap();
					hm.put(MESSAGE_CANCEL, new StateTransitionIgnore());
					hm.put(MESSAGE_CLOSE, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_COMPENSATE, new StateTransitionIgnore());
					hm.put(MESSAGE_COMPLETE, new StateTransitionIgnore());
					hm.put(MESSAGE_EXITED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_FAULTED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					cc.put(STATE_COMPENSATING, hm);
				}
				{
					// Messages in state FAULTING_ACTIVE
					final HashMap hm = new HashMap();
					hm.put(MESSAGE_CANCEL, new StateTransitionResend(
							MESSAGE_FAULT));
					hm.put(MESSAGE_CLOSE, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_COMPENSATE, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_COMPLETE, new StateTransitionResend(
							MESSAGE_FAULT));
					hm.put(MESSAGE_EXITED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_FAULTED, new StateTransitionState(
							STATE_ENDED));
					cc.put(STATE_FAULTING_ACTIVE, hm);
				}
				{
					// Messages in state FAULTING_COMPENSATING
					final HashMap hm = new HashMap();
					hm.put(MESSAGE_CANCEL, new StateTransitionIgnore());
					hm.put(MESSAGE_CLOSE, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_COMPENSATE, new StateTransitionResend(
							MESSAGE_FAULT));
					hm.put(MESSAGE_COMPLETE, new StateTransitionIgnore());
					hm.put(MESSAGE_EXITED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_FAULTED, new StateTransitionState(
							STATE_ENDED));
					cc.put(STATE_FAULTING_COMPENSATING, hm);
				}
				{
					// Messages in state EXITING
					final HashMap hm = new HashMap();
					hm.put(MESSAGE_CANCEL, new StateTransitionResend(
							MESSAGE_EXIT));
					hm.put(MESSAGE_CLOSE, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_COMPENSATE, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					hm.put(MESSAGE_COMPLETE, new StateTransitionResend(
							MESSAGE_EXIT));
					hm.put(MESSAGE_EXITED, new StateTransitionState(
							STATE_ENDED));
					hm.put(MESSAGE_FAULTED, new StateTransitionFault(
							GET_INVALID_STATE_SOAP_FAULT()));
					cc.put(STATE_EXITING, hm);
				}
				{
					// Messages in state ENDED
					final HashMap hm = new HashMap();
					hm.put(MESSAGE_CANCEL,
							new StateTransitionResendPreviousState());
					hm.put(MESSAGE_CLOSE,
							new StateTransitionResendPreviousState());
					hm.put(MESSAGE_COMPENSATE,
							new StateTransitionResendPreviousState());
					hm.put(MESSAGE_COMPLETE, new StateTransitionIgnore());
					hm.put(MESSAGE_EXITED, new StateTransitionIgnore());
					hm.put(MESSAGE_FAULTED, new StateTransitionIgnore());
					cc.put(STATE_ENDED, hm);
				}
				
				states.put(ProtocolType.PROTOCOL_ID_CC, cc);
			} // end initialize Coordinator Completion
		} // end initialize message/state hash maps 
	} // end static 
	
	/**
	 * Default constructor.</br> Calls super constructor with
	 * protocolIdentifier to be able to follow statetransitions of chosen
	 * protocol. Sets the current state to active.
	 * 
	 * @param protocolIdentifierX The protocol this state table will be configured for.
	 */
	public ParticipantState(final QName protocolIdentifierX) {
		super(protocolIdentifierX);
		setCurrentState(STATE_ACTIVE);
	}
	
	/**
	 * Return the current state table.
	 * @return The state table.
	 */
	protected Map getStates() {
		return states;
	}
	
	/**
	 * Return the global state table.
	 * @return The global state table.
	 */
	public static HashMap states() {
		return (HashMap)states.clone();
	}
	
	/**
	 * Return the peer's global state table.
	 * 
	 * @see org.apache.kandula.coordinator.ba.State#getOtherStates()
	 */
	protected HashMap getOtherStates() {
		return CoordinationState.states();
	}
	
	/**
	 * If the protocol is CoordinationCompletion and the state (<code>state</code>)
	 * is <code>STATE_CANCELLING</code> then the variable prevState has to be
	 * changed to reference <code>STATE_CANCELLING_ACTIVE</code> or
	 * <code>STATE_CANCELLING_COMPLETING</code> since
	 * <code>CoordinationState</code> is called.
	 * 
	 * @see org.apache.kandula.coordinator.ba.State#translateState(javax.xml.namespace.QName,
	 *      javax.xml.namespace.QName)
	 */
	protected QName translateState(final QName state, final QName prevState) {
		return State.translateStateToCoordinatorView(this.protocolIdentifier, state, prevState);
	}

	/**
	 * If the protocol is CoordinationCompletion and the state (<code>state</code>)
	 * is either <code>STATE_CANCELLING_ACTIVE</code> or
	 * <code>STATE_CANCELLING_COMPLETING</code> then the variable state has to
	 * be changed to reference <code>STATE_CANCELLING</code> since this is
	 * <code>ParticipantState</code>.
	 * 
	 * @param prevState
	 *            is not necessary for this method
	 * @see org.apache.kandula.coordinator.ba.State#reverseTranslateState(javax.xml.namespace.QName,
	 *      javax.xml.namespace.QName)
	 */
	protected QName reverseTranslateState(final QName state, final QName prevState) {
		return State.translateStateToParticipantView(state, prevState);
	}
}
