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
 * This class represents state transitions where no action is needed. This is typically the case
 * when a duplicate message from a participant is received.
 *  
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class StateTransitionIgnore extends AbstractStateTransition {

	/**
	 * Default constructor. As nothing should be done, there are
	 * no arguments needed. 
	 */
	public StateTransitionIgnore() {
		super(null, null);
	}

	/**
	 * Not applicable.
	 * @see org.apache.kandula.coordinator.ba.AbstractStateTransition#getAxisFault()
	 * @throws WrongMethodCallException Exception, if this method is not applicable to this state transition.
	 */
	public AxisFault getAxisFault() throws WrongMethodCallException{
		throw new WrongMethodCallException("Calling getAxisFault is not allowed on a StateTransitionState");
	}

	/**
	 * Not applicable.
	 * @throws WrongMethodCallException Exception, if this method is not applicable to this state transition.
	 * @see org.apache.kandula.coordinator.ba.AbstractStateTransition#getState()
	 */
	public QName getState() throws WrongMethodCallException {
		throw new WrongMethodCallException("Calling getState is not allowed on a StateTransitionFault");
	}

}
