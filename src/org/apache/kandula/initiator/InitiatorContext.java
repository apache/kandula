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
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.CoordinationContext;

public class InitiatorContext extends AbstractContext {

	private String activationEPR;

	private EndpointReference coordinationEPR;

//	private String requesterID;

	private String coordinationType;

	private CoordinationContext coordinationContext;

	private int status = Status.CoordinatorStatus.STATUS_NONE;

	public InitiatorContext(String coordinationType,
			String activationEPR) {
		this.activationEPR = activationEPR;
		this.coordinationType = coordinationType;
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

//	public String getRequesterID() {
//		return requesterID;
//	}
//
//	public void setRequesterID(String requesterID) {
//		this.requesterID = requesterID;
//	}

	public String getCoordinationType() {
		return coordinationType;
	}

	public String getActivationEPR() {
		return activationEPR;
	}

	public void setActivationEPR(String activationEPR) {
		this.activationEPR = activationEPR;
	}
}