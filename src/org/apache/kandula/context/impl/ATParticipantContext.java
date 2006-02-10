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
package org.apache.kandula.context.impl;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.participant.KandulaResource;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ATParticipantContext extends AbstractContext {
	KandulaResource resource;

	public ATParticipantContext() {
		this.setStatus(Status.CoordinatorStatus.STATUS_ACTIVE);
	}

	/**
	 * @return Returns the resource.
	 */
	public KandulaResource getResource() {
		return resource;
	}

	/**
	 * @param resource
	 *            The resource to set.
	 */
	public void setResource(KandulaResource resource) {
		this.resource = resource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.kandula.context.AbstractContext#getCoordinationType()
	 */
	public String getCoordinationType() {
		return Constants.WS_AT;
	}

	public EndpointReference getCoordinationEPR() {
		return (EndpointReference) getProperty(ATActivityContext.COORDINATION_EPR);
	}

}