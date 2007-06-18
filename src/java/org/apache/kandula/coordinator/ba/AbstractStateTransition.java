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
 * This class is the super class for all state transitions. It is extended by the
 * various StateTransition classes.
 * 
 * This type is used as a container to communicate actions related to incoming 
 * messages. When a participant sends a WS-BA message, the participant's current
 * state and the message type are looked up in the protocol state table which returns the
 * appropriate StateTransition.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public abstract class AbstractStateTransition {
	
	private final QName state;
	private final AxisFault axisFault;
	
	/**
	 * A new state transition.
	 * 
	 * @param pState The target state of this transition, if applicable.
	 * @param pAxisFault An exception to throw back to the client.
	 */
	public AbstractStateTransition(final QName pState, final AxisFault pAxisFault) {
		this.state = pState;
		this.axisFault = pAxisFault;
	}

	/**
	 * @return Returns the axisFault.
	 * @throws WrongMethodCallException Exception, if this method is not applicable to this state transition.
	 */
	public AxisFault getAxisFault() throws WrongMethodCallException {
		// This is just a placeholder method, so we need to declare an exception for subclasses to throw them
		if (false)
			throw new WrongMethodCallException();
		
		return this.axisFault;
	}

	/**
	 * @return Returns the state.
	 * @throws WrongMethodCallException Exception, if this method is not applicable to this state transition.
	 */
	public QName getState() throws WrongMethodCallException {
		// This is just a placeholder method, so we need to declare an exception for subclasses to throw them
		if (false)
			throw new WrongMethodCallException();

		return this.state;
	}

}
