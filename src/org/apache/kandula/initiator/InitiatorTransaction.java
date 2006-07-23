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
package org.apache.kandula.initiator;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Status;
import org.apache.kandula.context.CoordinationContext;
import org.apache.kandula.utility.EndpointReferenceFactory;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class InitiatorTransaction {

	private EndpointReference activationEPR;

	private EndpointReference coordinationEPR;

	private String requesterID;

	private String coordinationType;

	private CoordinationContext coordinationContext;

	private int status = Status.CoordinatorStatus.STATUS_NONE;

	public InitiatorTransaction(String coordinationType,
			EndpointReference activationEPR) {
		this.activationEPR = activationEPR;
		this.coordinationType = coordinationType;
		requesterID = EndpointReferenceFactory.getRandomStringOf18Characters();
	}

	/**
	 * @return Returns the coordinationEPR.
	 */
	public EndpointReference getCoordinationEPR() {
		return coordinationEPR;
	}

	/**
	 * @param coordinationEPR
	 *            The coordinationEPR to set.
	 */
	public void setCoordinationEPR(EndpointReference coordinationEPR) {
		this.coordinationEPR = coordinationEPR;
	}

	/**
	 * @return Returns the coordinationContext.
	 */
	public CoordinationContext getCoordinationContext() {
		return coordinationContext;
	}

	/**
	 * @param coordinationContext
	 *            The coordinationContext to set.
	 */
	public void setCoordinationContext(CoordinationContext coordinationContext) {
		this.coordinationContext = coordinationContext;
	}

	public final int getStatus() {
		return status;
	}

	public final void setStatus(int value) {
		status = value;
	}

	public String getRequesterID() {
		return requesterID;
	}

	public void setRequesterID(String requesterID) {
		this.requesterID = requesterID;
	}

	public String getCoordinationType() {
		return coordinationType;
	}

	public EndpointReference getActivationEPR() {
		return activationEPR;
	}

	public void setActivationEPR(EndpointReference activationEPR) {
		this.activationEPR = activationEPR;
	}
}