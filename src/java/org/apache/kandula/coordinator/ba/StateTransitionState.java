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
 * This is the most common state transition: the participant simply changes its state
 * due to an incoming message.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class StateTransitionState extends AbstractStateTransition {

	/**
	 * Default constructor.
	 * @param state The state the participant has now reached.
	 */
	public StateTransitionState(final QName state) {
		super(state, null);
	}

	/**
	 * @throws WrongMethodCallException Exception, if this method is not applicable to this state transition.
	 * @see org.apache.kandula.coordinator.ba.AbstractStateTransition#getAxisFault()
	 */
	public AxisFault getAxisFault() throws WrongMethodCallException{
		throw new WrongMethodCallException("Calling getAxisFault is not allowed on a StateTransitionState");
	}

}
