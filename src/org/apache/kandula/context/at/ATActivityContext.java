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
package org.apache.kandula.context.at;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.coordination.CoordinationContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.AlreadyRegisteredException;
import org.apache.kandula.faults.InvalidProtocolException;
import org.apache.kandula.participant.Participant;
import org.apache.kandula.utility.EPRHandlingUtils;
import org.apache.kandula.utility.EndpointReferenceFactory;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ATActivityContext extends AbstractContext {

	private int preparingParticipantsCount = 0;

	private boolean subOrdinate = false;

	private Hashtable volatileParticipantsTable;

	private Hashtable durableParticipantsTable;

	//TODO : check whether there can be more than 1 initiator
	private EndpointReference completionParticipant;

	private boolean subVolatileRegistered = false;

	private boolean subDurableRegistered = false;

	private EndpointReference parentEPR;

	/**
	 * Used when creating new activities
	 */
	public ATActivityContext() {
		super(Constants.WS_AT);
		this.setStatus(Status.CoordinatorStatus.STATUS_ACTIVE);
		volatileParticipantsTable = new Hashtable();
		durableParticipantsTable = new Hashtable();
	}

	/**
	 * @param context
	 *            To be used when coordinator is used as a sub ordinate to an
	 *            another cooordinator
	 */
	public ATActivityContext(CoordinationContext context) {
		subOrdinate = true;
		parentEPR = context.getRegistrationService();
		//        context.setRegistrationService(EndpointReferenceFactory.getInstance()
		//                .getRegistrationEndpoint());
		this.setStatus(Status.CoordinatorStatus.STATUS_ACTIVE);
		volatileParticipantsTable = new Hashtable();
		durableParticipantsTable = new Hashtable();
		setCoordinationContext(context);
	}

	/**
	 * @param id
	 *            To be used when using as the requester
	 */
	public ATActivityContext(EndpointReference activationEPR) {
		super();
		this.setProperty(REQUESTER_ID, EPRHandlingUtils
				.getRandomStringOf18Characters());
		this.setProperty(ACTIVATION_EPR, activationEPR);
	}

	/**
	 * @param participantEPR
	 * @param protocol
	 * @return Coordinator protocol service.
	 * @throws AbstractKandulaException
	 */
	public EndpointReference addParticipant(EndpointReference participantEPR,
			String protocol) throws AbstractKandulaException {
		String enlistmentID = EPRHandlingUtils.getRandomStringOf18Characters();
		if (Constants.WS_AT_VOLATILE2PC.equals(protocol)) {
			addVolatileParticipant(participantEPR, enlistmentID);
			return EndpointReferenceFactory.getInstance()
					.get2PCCoordinatorEndpoint(activityID, enlistmentID);
		} else if (Constants.WS_AT_DURABLE2PC.equals(protocol)) {
			addDurableParticipant(participantEPR, enlistmentID);
			return EndpointReferenceFactory.getInstance()
					.get2PCCoordinatorEndpoint(activityID, enlistmentID);
		} else if (Constants.WS_AT_COMPLETION.equals(protocol)) {
			completionParticipant = participantEPR;
			return EndpointReferenceFactory.getInstance()
					.getCompletionEndpoint(this.activityID);
		} else {
			throw new InvalidProtocolException();
		}
	}

	public void addVolatileParticipant(EndpointReference participantEPR,
			String enlistmentID) throws AbstractKandulaException {
		if (volatileParticipantsTable.contains(participantEPR))
			throw new AlreadyRegisteredException();
		Participant participant = new Participant(participantEPR,
				Constants.WS_AT_VOLATILE2PC, enlistmentID);
		volatileParticipantsTable.put(enlistmentID, participant);
	}

	public void addDurableParticipant(EndpointReference participantEPR,
			String enlistmentID) throws AlreadyRegisteredException {
		if (durableParticipantsTable.contains(participantEPR)) {
			throw new AlreadyRegisteredException();
		}
		Participant participant = new Participant(participantEPR,
				Constants.WS_AT_DURABLE2PC, enlistmentID);
		durableParticipantsTable.put(enlistmentID, participant);
	}

	public Iterator getRegistered2PCParticipants(String protocol) {
		if (protocol.equals(Constants.WS_AT_VOLATILE2PC)) {
			return volatileParticipantsTable.values().iterator();
		} else if (protocol.equals(Constants.WS_AT_DURABLE2PC)) {
			return durableParticipantsTable.values().iterator();
		}
		return null;
	}

	public Iterator getAll2PCParticipants() {
		LinkedList list = new LinkedList(volatileParticipantsTable.values());
		list.addAll(durableParticipantsTable.values());
		return list.iterator();
	}

	public Participant getParticipant(String enlistmentId) {
		if (volatileParticipantsTable.containsKey(enlistmentId)) {
			return (Participant) volatileParticipantsTable.get(enlistmentId);
		} else if (durableParticipantsTable.containsKey(enlistmentId)) {
			return (Participant) durableParticipantsTable.get(enlistmentId);
		} else {
			return null;
		}
	}

	/**
	 * @return the completion initiator epr
	 */
	public EndpointReference getCompletionParticipant() {
		return completionParticipant;
	}

	public void countPreparing() {
		preparingParticipantsCount++;

	}

	public void decrementPreparing() {
		preparingParticipantsCount--;
	}

	public boolean hasMorePreparing() {
		return (preparingParticipantsCount > 0);
	}

	public boolean getSubVolatileRegistered() {

		return subVolatileRegistered;
	}

	public boolean getSubDurableRegistered() {
		return subDurableRegistered;
	}

	public void setSubVolatileRegistered(boolean value) {
		subVolatileRegistered = value;
	}

	public void setSubDurableRegistered(boolean value) {
		subDurableRegistered = value;
	}

	//    public void prepared(Participant participant)
	//    {
	//        if (participant.getStatus()==Status.ParticipantStatus.STATUS_ABORTED)
	//        {
	//            //throw new
	//        }
	//    }

	public String getCoordinationType() {
		return Constants.WS_AT;
	}

}