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

import org.apache.axis.message.addressing.EndpointReference;

/**
 * Placeholder for the InitiatorProxy class - requires the protocol
 * between initiator and coordinator to be defined first. 
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class InitiatorProxy {
	
	/**
	 * The participant ID requests from the genuine initiator carry with them.
	 */
	protected final String myID;
	
	/**
	 * The endpoint where the initiator can be reached.
	 */
	protected final EndpointReference myEndpoint;
	
	/**
	 * Constructor for a new Initiator - every business activity should have one :-)
	 * @param id The initiator ID (used to authenticate requests from them)
	 * @param epr The EPR the initiator can be reached at.
	 */
	public InitiatorProxy(final String id, final EndpointReference epr){
		if (id == null || id.length()==0)
			throw new IllegalArgumentException("Sorry, Initator ID must not be null or empty");
		
		if (epr == null)
			throw new IllegalArgumentException("Sorry, Initiator EPR must not be null");
		
		this.myID = id;
		this.myEndpoint = epr;
	}

}
