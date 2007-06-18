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

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;

/**
 * This state transition is used when the participant needs to resend a possibly lost
 * notification to the coordinator, due to a duplicate incoming message.
 * In contrast to @see {@link StateTransitionResend} the participant needs to look
 * two states back to determine which message to send.
 * 
 * The typical use case is a duplicate incoming "CANCEL" message when already in state "ENDED":
 * the participant must resend its "CANCELED" message.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class StateTransitionResendPreviousState extends AbstractStateTransition {

	/**
	 * Create a new State Transition with the info to Resend Message of previous
	 * state.
	 */
	public StateTransitionResendPreviousState() {
		super(null, null);
	}
	
	/**
	 * Throws a WrongMethodCallException
	 * @see AbstractStateTransition#getAxisFault()
	 */
	public AxisFault getAxisFault() throws WrongMethodCallException {
		throw new WrongMethodCallException(
				"Calling getAxisFault is not allowed on a StateTransitionResendPreviousState");
	}

	/**
	 * Throws a WrongMethodCallException
	 * @see AbstractStateTransition#getState()
	 */
	public QName getState() throws WrongMethodCallException {
		throw new WrongMethodCallException(
				"Calling getState is not allowed on a StateTransitionResendPreviousState");
	}
}
