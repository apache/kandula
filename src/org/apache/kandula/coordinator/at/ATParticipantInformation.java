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
 * <p/> Used to store the details about the participant & to store the runtime
 * status of Participants in the coordinator.
 */
public class ATParticipantInformation {

	private String enlistmentId;

	private EndpointReference epr;

	private String protocol;

	private int status;

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
	 * @return Returns the Id which this participant is registered with the
	 *         coordinator
	 */
	public String getEnlistmentId() {
		return enlistmentId;
	}

	/**
	 * @return Returns the epr of the participant
	 */
	public EndpointReference getEpr() {
		return epr;
	}

	/**
	 * @return Returns the protocol of the participant (Eg: WS-AT, WS-BA)
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @return Returns the status of the participant
	 */
	public int getStatus() {
		return status;
	}

	public void setEpr(EndpointReference epr) {
		this.epr = epr;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}