/*
 * Copyright  2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.kandula.context;

import java.util.HashMap;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Status;
import org.apache.kandula.utility.EndpointReferenceFactory;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public abstract class AbstractContext {

	private final HashMap propertyBag;

	protected String activityID;

	private int status = Status.CoordinatorStatus.STATUS_NONE;

	private boolean locked = false;

	protected CoordinationContext coordinationContext = null;

	public static String REQUESTER_ID = "requesterID";

	public static String COORDINATION_EPR = "coordinationEPR";

	public static String ACTIVATION_EPR = "activationEPR";

	protected AbstractContext() {
		propertyBag = new HashMap();
	}

	public AbstractContext(String coordinationType) {
		propertyBag = new HashMap();
		activityID = "urn:"+EndpointReferenceFactory.getRandomStringOf18Characters();
		EndpointReference registrationEpr = EndpointReferenceFactory
				.getInstance().getRegistrationEndpoint(activityID);
		coordinationContext = CoordinationContext.Factory.newContext(
				activityID, coordinationType, registrationEpr);
	}

	public final CoordinationContext getCoordinationContext() {
		return coordinationContext;
	}

	public final void setCoordinationContext(CoordinationContext context) {
		this.coordinationContext = context;
	}

	public final int getStatus() {
		return status;
	}

	// we can use a publisher-subscriber in the future to notify listeners abt
	// state changes.
	public final void setStatus(int value) {
		status = value;
	}

	public final synchronized void lock() {
		if (locked) {
			while (locked) {
				try {
					wait();
				} catch (InterruptedException ex) {
					//	ignore
				}
				if (status == Status.CoordinatorStatus.STATUS_NONE)
					throw new IllegalStateException();
			}
		}

		locked = true;
	}

	public final synchronized void unlock() {
		if (!locked)
			throw new IllegalStateException();
		locked = false;
		notify();
	}

	public final void setProperty(Object key, Object value) {
		propertyBag.put(key, value);

	}

	public final Object getProperty(Object key) {
		return propertyBag.get(key);
	}

	public abstract String getCoordinationType();

}