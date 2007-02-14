 package org.apache.kandula.context.impl;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.ba.BusinessActivityCallBack;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.CoordinationContext;
import org.apache.kandula.coordinator.ba.BAParticipantInformation;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.AlreadyRegisteredException;
import org.apache.kandula.faults.InvalidProtocolException;
import org.apache.kandula.utility.EndpointReferenceFactory;

public class BAActivityContext extends AbstractContext {

	private String coordinationType;
	private Hashtable participantCompletionParticipantsTable;
	private Hashtable coordinatorCompletionParticipantsTable;
	private int completingParticipantCount = 0;
	private BusinessActivityCallBack callBack;

	public BAActivityContext() {
		}
	
	public BAActivityContext(String coorType) {
		super(coorType);
		this.coordinationType = coorType;
		this.setStatus(Status.CoordinatorStatus.STATUS_ACTIVE);
		participantCompletionParticipantsTable = new Hashtable();
		coordinatorCompletionParticipantsTable = new Hashtable();
	}
	
	public BAActivityContext(CoordinationContext context) {
		this.setStatus(Status.CoordinatorStatus.STATUS_ACTIVE);
		// TODO do we need the following
		participantCompletionParticipantsTable = new Hashtable();
		coordinatorCompletionParticipantsTable = new Hashtable();
		setCoordinationContext(context);
	}
	
	/**
	 * @param participantEPR
	 * @param protocol
	 * @return Coordinator protocol service.
	 * @throws AbstractKandulaException
	 */
	public EndpointReference addParticipant(EndpointReference participantEPR, String protocol,
			String enlistmentID) throws AbstractKandulaException {
		if (enlistmentID == null) {
			enlistmentID = EndpointReferenceFactory.getRandomStringOf18Characters();
		}
		if (Constants.WS_BA_CC.equals(protocol)) {
			addCoordinatorCompletionParticipants(participantEPR, enlistmentID);
			if (this.coordinationType.equals(Constants.WS_BA_ATOMIC)) {
				return EndpointReferenceFactory.getInstance().getAtomicOutcomeCCCoordinatorEndpoint(
						activityID, enlistmentID);
			} else if (this.coordinationType.equals(Constants.WS_BA_MIXED)) {
				return EndpointReferenceFactory.getInstance().getMixedOutcomeCCCoordinatorEndpoint(
						activityID, enlistmentID);
			}
		} else if (Constants.WS_BA_PC.equals(protocol)) {
			addParticipantCompletionParticipants(participantEPR, enlistmentID);
			incrementCompletingParticipantCount();
			if (this.coordinationType.equals(Constants.WS_BA_ATOMIC)) {
				return EndpointReferenceFactory.getInstance().getAtomicOutcomePCCoordinatorEndpoint(
						activityID, enlistmentID);
			} else if (this.coordinationType.equals(Constants.WS_BA_MIXED)) {
				return EndpointReferenceFactory.getInstance().getMixedOutcomePCCoordinatorEndpoint(
						activityID, enlistmentID);
			}
		} else {
			throw new InvalidProtocolException();
		}
		return null;
	}

	public void removeParticipant(String enlistmentID) {
		// TODO: what to do if the participant is not found
		if (participantCompletionParticipantsTable.containsKey(enlistmentID)) {
			participantCompletionParticipantsTable.remove(enlistmentID);
		} else if (coordinatorCompletionParticipantsTable.containsKey(enlistmentID)) {
			coordinatorCompletionParticipantsTable.remove(enlistmentID);
		}
	}

	protected void addParticipantCompletionParticipants(EndpointReference participantEPR,
			String enlistmentID) throws AlreadyRegisteredException {
		if (participantCompletionParticipantsTable.contains(participantEPR)) {
			throw new AlreadyRegisteredException();
		}
		BAParticipantInformation participant = new BAParticipantInformation(
				participantEPR, Constants.WS_BA_PC, enlistmentID);
		participantCompletionParticipantsTable.put(enlistmentID, participant);
	}

	protected void addCoordinatorCompletionParticipants(EndpointReference participantEPR,
			String enlistmentID) throws AbstractKandulaException {
		if (coordinatorCompletionParticipantsTable.contains(participantEPR))
			throw new AlreadyRegisteredException();
		BAParticipantInformation participant = new BAParticipantInformation(
				participantEPR, Constants.WS_BA_CC, enlistmentID);
		coordinatorCompletionParticipantsTable.put(enlistmentID, participant);
	}

	public int getcoordinatorCompletionParticipantsCount() {
		return coordinatorCompletionParticipantsTable.size();
	}

	public int getparticipantCompletionParticipantCount() {
		return participantCompletionParticipantsTable.size();
	}

	public Iterator getAllParticipants() {
		LinkedList list = new LinkedList(participantCompletionParticipantsTable.values());
		list.addAll(coordinatorCompletionParticipantsTable.values());
		return list.iterator();
	}

	
	public BAParticipantInformation getParticipant(String enlistmentId) {
		if (participantCompletionParticipantsTable.containsKey(enlistmentId)) {
			return (BAParticipantInformation) participantCompletionParticipantsTable
					.get(enlistmentId);
		} else if (coordinatorCompletionParticipantsTable.containsKey(enlistmentId)) {
			return (BAParticipantInformation) coordinatorCompletionParticipantsTable
					.get(enlistmentId);
		} else {
			return null;
		}
	}
	public String getCoordinationType() {
		return this.coordinationType;
	}
	public String getRegistrationProtocol() {
		return null;
	}
	
	public Hashtable getCoordinatorCompletionParticipants(){
		return coordinatorCompletionParticipantsTable;
	}

	public Hashtable getparticipantCompletionParticipants() {
		return participantCompletionParticipantsTable;
	}
	
	public synchronized void incrementCompletingParticipantCount() {
		completingParticipantCount++;
	}

	public synchronized void incrementCompletingParticipantCount(int number) {
		completingParticipantCount+=number;
	}
	
	public synchronized void decrementCompletingParticipantCount() {
		completingParticipantCount--;
	}
	
	public synchronized boolean hasMoreCompleting() {
		return (completingParticipantCount > 0);
	}

	public BusinessActivityCallBack getCallBack() {
		return callBack;
	}

	public void setCallBack(BusinessActivityCallBack callBack) {
		this.callBack = callBack;
	}
}
