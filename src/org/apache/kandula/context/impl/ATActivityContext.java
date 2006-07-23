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

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.CoordinationContext;
import org.apache.kandula.coordinator.at.ATParticipantInformation;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.AlreadyRegisteredException;
import org.apache.kandula.faults.InvalidProtocolException;
import org.apache.kandula.utility.EndpointReferenceFactory;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ATActivityContext extends AbstractContext {

	private Method callBackMethod = null;

	// TODO : check whether there can be more than 1 initiator
	private EndpointReference completionParticipant;

	private Hashtable durableParticipantsTable;

	private EndpointReference parentEPR;

	private int preparingParticipantsCount = 0;

	private boolean subDurableRegistered = false;

	private boolean subOrdinate = false;

	private boolean subVolatileRegistered = false;

	private Hashtable volatileParticipantsTable;

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
		// context.setRegistrationService(EndpointReferenceFactory.getInstance()
		// .getRegistrationEndpoint());
		this.setStatus(Status.CoordinatorStatus.STATUS_ACTIVE);
		// TODO do we need the following
		volatileParticipantsTable = new Hashtable();
		durableParticipantsTable = new Hashtable();
		setCoordinationContext(context);
	}

	/**
	 * @param participantEPR
	 * @param protocol
	 * @return Coordinator protocol service.
	 * @throws AbstractKandulaException
	 */
	public EndpointReference addParticipant(EndpointReference participantEPR,
			String protocol) throws AbstractKandulaException {
		String enlistmentID = EndpointReferenceFactory
				.getRandomStringOf18Characters();
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

	public void removeParticipant(String enlistmentID) {
		// TODO: what to do if the participant is not found
		if (durableParticipantsTable.containsKey(enlistmentID)) {
			durableParticipantsTable.remove(enlistmentID);
		} else if (volatileParticipantsTable.containsKey(enlistmentID)) {
			volatileParticipantsTable.remove(enlistmentID);
		}
	}

	protected void addDurableParticipant(EndpointReference participantEPR,
			String enlistmentID) throws AlreadyRegisteredException {
		if (durableParticipantsTable.contains(participantEPR)) {
			throw new AlreadyRegisteredException();
		}
		ATParticipantInformation participant = new ATParticipantInformation(
				participantEPR, Constants.WS_AT_DURABLE2PC, enlistmentID);
		durableParticipantsTable.put(enlistmentID, participant);
	}

	protected void addVolatileParticipant(EndpointReference participantEPR,
			String enlistmentID) throws AbstractKandulaException {
		if (volatileParticipantsTable.contains(participantEPR))
			throw new AlreadyRegisteredException();
		ATParticipantInformation participant = new ATParticipantInformation(
				participantEPR, Constants.WS_AT_VOLATILE2PC, enlistmentID);
		volatileParticipantsTable.put(enlistmentID, participant);
	}

	public int getDurableParticipantCount() {
		return durableParticipantsTable.size();
	}

	public int getVolatileParticipantCount() {
		return volatileParticipantsTable.size();
	}

	public Iterator getAll2PCParticipants() {
		LinkedList list = new LinkedList(volatileParticipantsTable.values());
		list.addAll(durableParticipantsTable.values());
		return list.iterator();
	}

	/**
	 * @return the completion initiator epr
	 */
	public EndpointReference getCompletionParticipant() {
		return completionParticipant;
	}

	public ATParticipantInformation getParticipant(String enlistmentId) {
		if (volatileParticipantsTable.containsKey(enlistmentId)) {
			return (ATParticipantInformation) volatileParticipantsTable
					.get(enlistmentId);
		} else if (durableParticipantsTable.containsKey(enlistmentId)) {
			return (ATParticipantInformation) durableParticipantsTable
					.get(enlistmentId);
		} else {
			return null;
		}
	}

	public Iterator getRegistered2PCParticipants(String protocol) {
		if (protocol.equals(Constants.WS_AT_VOLATILE2PC)) {
			return volatileParticipantsTable.values().iterator();
		} else if (protocol.equals(Constants.WS_AT_DURABLE2PC)) {
			return durableParticipantsTable.values().iterator();
		}
		return null;
	}

	public synchronized void incrementPreparingParticipantCount() {
		preparingParticipantsCount++;

	}

	public synchronized void decrementPreparingParticipantCount() {
		preparingParticipantsCount--;
	}

	public String getCoordinationType() {
		return Constants.WS_AT;
	}

	public String getRegistrationProtocol() {
		return null;
	}

	/**
	 * @param callBackMethod
	 *            the method to call when all the participats have been
	 *            registered.
	 */
	public void setCallBackMethod(Method callBackMethod) {
		this.callBackMethod = callBackMethod;
	}

	/**
	 * @return Returns the callBackMethod.
	 */
	public Method getCallBackMethod() {
		return callBackMethod;
	}

	public boolean getSubDurableRegistered() {
		return subDurableRegistered;
	}

	public boolean getSubVolatileRegistered() {

		return subVolatileRegistered;
	}

	public synchronized boolean hasMorePreparing() {
		return (preparingParticipantsCount > 0);
	}

	public void setSubDurableRegistered(boolean value) {
		subDurableRegistered = value;
	}

	public void setSubVolatileRegistered(boolean value) {
		subVolatileRegistered = value;
	}
}
