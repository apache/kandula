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
package org.apache.ws.transaction.coordinator;

import org.apache.ws.transaction.utility.EndpointReferenceFactory;
import org.apache.ws.transaction.wscoor.CoordinationContext;
import org.apache.ws.transaction.wscoor.RegistrationRPCEndpoint;


public abstract class CoordinatorImpl implements Coordinator {
	protected Identifier activityId;
	protected CoordinationContext coordinationContext;

	protected CoordinatorImpl(Identifier activityId, String coordinationType) {
		this.activityId= activityId;
		coordinationContext=
			new CoordinationContext(
				activityId,
				coordinationType,
				EndpointReferenceFactory.getInstance().getEndpointReference(
					RegistrationRPCEndpoint.PORT_TYPE,
					activityId.toReferencePropertiesType()));
	}

	public Identifier getActivityId() {
		return activityId;
	}

	public CoordinationContext getCoordinationContext() {
		return coordinationContext;
	}

	protected synchronized void done() {
		CoordinationService.getInstance().forget(activityId);
		done= true;
		notifyAll();
	}

	boolean done= false;
	boolean locked= false;

	protected synchronized void lock() {
		if (locked) {
			while (locked) {
				try {
					wait();
				}
				catch (InterruptedException ex) {
					//	ignore
				}
				if (done)
					throw new IllegalStateException();
			}
		}

		locked= true;
	}

	protected synchronized void unlock() {
		if (!locked)
			throw new IllegalStateException();
		locked= false;
		notify();
	}
}
