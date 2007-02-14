package org.apache.kandula.coordinator.ba;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Status;

public class BAParticipantInformation {
	
	private String enlistmentId;

	private EndpointReference epr;

	private String protocol;

	private int status;
	
	boolean criticalParticipant;
	
	public BAParticipantInformation(EndpointReference epr, String protocol,
			String enlistmentId) {
		super();
		this.epr = epr;
		this.protocol = protocol;
		this.enlistmentId = enlistmentId;
		this.status = Status.BACoordinatorStatus.STATUS_ACTIVE;
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
	
	public boolean setCriticality(boolean criticality) {
		return criticalParticipant;
	}
	
	public boolean getCriticality() {
		return criticalParticipant;
	}
}
