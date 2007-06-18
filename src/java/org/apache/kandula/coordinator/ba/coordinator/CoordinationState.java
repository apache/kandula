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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.kandula.coordinator.ba.ProtocolType;
import org.apache.kandula.coordinator.ba.State;
import org.apache.kandula.coordinator.ba.StateTransitionFault;
import org.apache.kandula.coordinator.ba.StateTransitionIgnore;
import org.apache.kandula.coordinator.ba.StateTransitionResend;
import org.apache.kandula.coordinator.ba.StateTransitionState;
import org.apache.kandula.coordinator.ba.participant.ParticipantState;

/**
 * Implementation of State with the Hashmap set for the different states of a
 * Coordinator.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 * 
 */
public class CoordinationState extends State {
	
	/**
	 * The state table.
	 */
	protected final static HashMap states = new HashMap();


	static {
		/*
		 * Initializing the HashMap for the ParticipantCompletion - Protocol
		 */
		{
			// HashMap for ParticipantCompletion
			final HashMap pc = new HashMap();			
			{
				// Messages in state ACTIVE
				
				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionState(
						STATE_EXITING));
				hm.put(MESSAGE_FAULT, new StateTransitionState(
						STATE_FAULTING_ACTIVE));
				hm.put(MESSAGE_COMPLETED, new StateTransitionState(
						STATE_COMPLETED));
				pc.put(STATE_ACTIVE, hm);
			}
			{
				// Messages in state CANCELLING

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionState(
						STATE_ENDED));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionState(
						STATE_EXITING));
				hm.put(MESSAGE_FAULT, new StateTransitionState(
						STATE_FAULTING_ACTIVE));
				hm.put(MESSAGE_COMPLETED, new StateTransitionState(
						STATE_COMPLETED));
				pc.put(STATE_CANCELLING, hm);
			}			
			{
				// Messages in state COMPLETED

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPLETED, new StateTransitionIgnore());
				pc.put(STATE_COMPLETED, hm);
			}
			{
				// Messages in state CLOSING

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionState(
						STATE_ENDED));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPLETED, new StateTransitionResend(
						MESSAGE_CLOSE));
				pc.put(STATE_CLOSING, hm);
			}
			{
				// Messages in state COMPENSATING

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionState(
						STATE_ENDED));
				hm.put(MESSAGE_EXIT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULT, new StateTransitionState(
						STATE_FAULTING_COMPENSATING));
				hm.put(MESSAGE_COMPLETED, new StateTransitionResend(
						MESSAGE_COMPENSATE));
				pc.put(STATE_COMPENSATING, hm);
			}
			{
				// Messages in state FAULTING_ACTIVE

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULT, new StateTransitionIgnore());
				hm.put(MESSAGE_COMPLETED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				pc.put(STATE_FAULTING_ACTIVE, hm);
			}
			
			{
				// Messages in state FAULTING_COMPENSATING

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULT, new StateTransitionIgnore());
				hm.put(MESSAGE_COMPLETED, new StateTransitionIgnore());
				pc.put(STATE_FAULTING_COMPENSATING, hm);
			}
			{
				// Messages in state EXITING

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionIgnore());
				hm.put(MESSAGE_FAULT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPLETED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				pc.put(STATE_EXITING, hm);
			}
			{
				// Messages in state ENDED

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionIgnore());
				hm.put(MESSAGE_CLOSED, new StateTransitionIgnore());
				hm.put(MESSAGE_COMPENSATED, new StateTransitionIgnore());
				hm.put(MESSAGE_EXIT, new StateTransitionResend(MESSAGE_EXITED));
				hm.put(MESSAGE_FAULT, new StateTransitionResend(MESSAGE_FAULTED));
				hm.put(MESSAGE_COMPLETED, new StateTransitionIgnore());
				pc.put(STATE_ENDED, hm);
			}
			
			states.put(ProtocolType.PROTOCOL_ID_PC, pc);
		}
		
		{
			/*
			 * Initializing the HashMap for the CoordinatorCompletion - Protocol
			 */
			final HashMap cc = new HashMap();
						
			{
				// Messages in state ACTIVE

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionState(
						STATE_EXITING));
				hm.put(MESSAGE_FAULT, new StateTransitionState(
						STATE_FAULTING_ACTIVE));
				hm.put(MESSAGE_COMPLETED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				cc.put(STATE_ACTIVE, hm);
			}
			{
				// Messages in state CANCELLING_ACTIVE

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionState(
						STATE_ENDED));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionState(
						STATE_EXITING));
				hm.put(MESSAGE_FAULT, new StateTransitionState(
						STATE_FAULTING_ACTIVE));
				hm.put(MESSAGE_COMPLETED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				cc.put(STATE_CANCELLING_ACTIVE, hm);
			}
			{
				// Messages in state CANCELLING_COMPLETING

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionState(
						STATE_ENDED));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionState(
						STATE_EXITING));
				hm.put(MESSAGE_FAULT, new StateTransitionState(
						STATE_FAULTING_ACTIVE));
				hm.put(MESSAGE_COMPLETED, new StateTransitionState(
						STATE_COMPLETED));
				cc.put(STATE_CANCELLING_COMPLETING, hm);
			}
			{
				// Messages in state COMPLETED

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPLETED, new StateTransitionIgnore());
				cc.put(STATE_COMPLETED, hm);
			}
			{
				// Messages in state COMPLETING

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionState(
						STATE_EXITING));
				hm.put(MESSAGE_FAULT, new StateTransitionState(
						STATE_FAULTING_ACTIVE));
				hm.put(MESSAGE_COMPLETED, new StateTransitionState(
						STATE_COMPLETED));
				cc.put(STATE_COMPLETING, hm);
			}
			{
				// Messages in state CLOSING

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionState(
						STATE_ENDED));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPLETED, new StateTransitionResend(
						MESSAGE_CLOSE));
				cc.put(STATE_CLOSING, hm);
			}
			{
				// Messages in state COMPENSATING

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionState(
						STATE_ENDED));
				hm.put(MESSAGE_EXIT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULT, new StateTransitionState(
						STATE_FAULTING_COMPENSATING));
				hm.put(MESSAGE_COMPLETED, new StateTransitionResend(
						MESSAGE_COMPENSATE));
				cc.put(STATE_COMPENSATING, hm);
			}
			{
				// Messages in state FAULTING ACTIVE

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULT, new StateTransitionIgnore());
				hm.put(MESSAGE_COMPLETED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				cc.put(STATE_FAULTING_ACTIVE, hm);
			}
			{
				// Messages in state FAULTING_COMPENSATING

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_FAULT, new StateTransitionIgnore());
				hm.put(MESSAGE_COMPLETED, new StateTransitionIgnore());
				cc.put(STATE_FAULTING_COMPENSATING, hm);
			}
			{
				// Messages in state EXITING

				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_CLOSED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPENSATED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_EXIT, new StateTransitionIgnore());
				hm.put(MESSAGE_FAULT, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				hm.put(MESSAGE_COMPLETED, new StateTransitionFault(
						GET_INVALID_STATE_SOAP_FAULT()));
				cc.put(STATE_EXITING, hm);
			}
			{
				final HashMap hm = new HashMap();
				hm.put(MESSAGE_CANCELED, new StateTransitionIgnore());
				hm.put(MESSAGE_CLOSED, new StateTransitionIgnore());
				hm.put(MESSAGE_COMPENSATED, new StateTransitionIgnore());
				hm.put(MESSAGE_EXIT, new StateTransitionResend(MESSAGE_EXITED));
				hm.put(MESSAGE_FAULT, new StateTransitionResend(MESSAGE_FAULTED));
				hm.put(MESSAGE_COMPLETED, new StateTransitionIgnore());
				cc.put(STATE_ENDED, hm);
			}
			states.put(ProtocolType.PROTOCOL_ID_CC, cc);
		}
	}
	
	/**
	 * Default constructor.</br> Calls super constructor with
	 * protocolIdentifier to be able to follow statetransitions of chosen
	 * protocol. Sets the current state to active.
	 * 
	 * @param protocolIdentifierX The protocol this state table will be used with.
	 */
	public CoordinationState(QName protocolIdentifierX) {
		super(protocolIdentifierX);
		setCurrentState(STATE_ACTIVE);
	}
	
	/**
	 * Fetch the state table.
	 * @return The state table.
	 */
	protected Map getStates() {
		return states;
	}
	
	/**
	 * Fetch the global state table.
	 * @return The global state table.
	 */
	public static HashMap states() {
		return (HashMap)states.clone();
	}
	
	/**
	 * Return the peer's state table.
	 * 
	 * @see org.apache.kandula.coordinator.ba.State#getOtherStates()
	 */
	protected HashMap getOtherStates() {
		return ParticipantState.states();
	}

	/**
	 * If the protocol is CoordinationCompletion and the state (<code>state</code>)
	 * is either <code>STATE_CANCELLING_ACTIVE</code> or
	 * <code>STATE_CANCELLING_COMPLETING</code> then the variable state has to
	 * be changed to reference <code>STATE_CANCELLING</code> since
	 * <code>ParticipantState</code> is called.
	 * 
	 * @param prevState
	 *            is not necessary for this method
	 * @see org.apache.kandula.coordinator.ba.State#translateState(javax.xml.namespace.QName,
	 *      javax.xml.namespace.QName)
	 */
	protected QName translateState(final QName state, final QName prevState) {
		return State.translateStateToParticipantView(state, prevState);
	}

	/**
	 * If the protocol is CoordinationCompletion and the state (<code>state</code>)
	 * is <code>STATE_CANCELLING</code> then the variable state has to be
	 * changed to reference <code>STATE_CANCELLING_ACTIVE</code> or
	 * <code>STATE_CANCELLING_COMPLETING</code> since this is
	 * <code>CoordinationState</code>.
	 * 
	 * @see org.apache.kandula.coordinator.ba.State#reverseTranslateState(javax.xml.namespace.QName,
	 *      javax.xml.namespace.QName)
	 */
	protected QName reverseTranslateState(QName state, final QName prevState) {
		return State.translateStateToCoordinatorView(this.protocolIdentifier, state, prevState);
	}
}
