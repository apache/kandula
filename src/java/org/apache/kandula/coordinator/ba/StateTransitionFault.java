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
 * This class represents faulted state transitions, used when a participant's message
 * cannot be received in its current state.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class StateTransitionFault extends AbstractStateTransition {

	/**
	 * Default constructor. Throw back the given exception.
	 * @param axisFault The fault to throw back.
	 */
	public StateTransitionFault(final AxisFault axisFault) {
		super(null, axisFault);
	}

	/**
	 * Return the target state. (Not applicable!)
	 * 
	 * @throws WrongMethodCallException Exception, if this method is not applicable to this state transition.
	 * @return The state.
	 */	
	public QName getState() throws WrongMethodCallException {
		throw new WrongMethodCallException("Calling getState is not allowed on a StateTransitionFault");
	}

}
