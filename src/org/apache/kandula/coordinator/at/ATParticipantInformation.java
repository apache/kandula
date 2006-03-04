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
package org.apache.kandula.coordinator.at;

import org.apache.axis2.addressing.EndpointReference;
/**
 *  * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
/**
 *  <p/>
 *         Used to store the details about the participant & to store the
 *         runtime status of Participants in the coordinator.
 */

public class ATParticipantInformation {

	private EndpointReference epr;

	private String protocol;

	private int status;

	private String enlistmentId;

	/**
	 * @param epr
	 * @param protocol
	 * @param enlistmentId
	 */
	public ATParticipantInformation(EndpointReference epr, String protocol,
			String enlistmentId) {
		super();
		this.epr = epr;
		this.protocol = protocol;
		this.enlistmentId = enlistmentId;
	}

	/**
	 * @return Returns the enlistmentId.
	 */
	public String getEnlistmentId() {
		return enlistmentId;
	}

	/**
	 * @return Returns the epr.
	 */
	public EndpointReference getEpr() {
		return epr;
	}

	/**
	 * @param epr -
	 *            The epr to set.
	 */
	public void setEpr(EndpointReference epr) {
		this.epr = epr;
	}

	/**
	 * @return Returns the status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status -
	 *            The status to set.
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return Returns the protocol.
	 */
	public String getProtocol() {
		return protocol;
	}
}