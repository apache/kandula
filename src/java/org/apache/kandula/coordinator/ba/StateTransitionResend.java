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
 * A state transition that shall only resend an already sent message to the peer. No further
 * action is required.
 * This transition is typically used when a duplicate message from a participant is received
 * and the coordinator shall resend its most recent (possibly lost) message again.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class StateTransitionResend extends AbstractStateTransition {

	/**
	 * The message to send 
	 */
	private final QName messageToResend;

	/**
	 * Create a new State Transition with Message Resend
	 * @param pMessageToResend 
	 */
	public StateTransitionResend(final QName pMessageToResend) {
		super(null, null);

		this.messageToResend = pMessageToResend;
	}


	/**
	 * Throws a WrongMethodCallException
	 * @see AbstractStateTransition#getAxisFault()
	 */
	public AxisFault getAxisFault() throws WrongMethodCallException{
		throw new WrongMethodCallException(
		"Calling getAxisFault is not allowed on a StateTransitionState");
	}

	/**
	 * Throws a WrongMethodCallException
	 * @see AbstractStateTransition#getState()
	 */
	public QName getState() throws WrongMethodCallException {
		throw new WrongMethodCallException(
		"Calling getState is not allowed on a StateTransitionState");
	}

	/**
	 * The message to resend.
	 * @return The message to resend. @see State
	 */
	public QName getMessageToResend() {
		return this.messageToResend;
	}
}