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

import java.lang.reflect.Method;
import java.util.Iterator;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.Status.CoordinatorStatus;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.impl.ATActivityContext;
import org.apache.kandula.coordinator.Registerable;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.InvalidStateException;
import org.apache.kandula.faults.KandulaGeneralException;
import org.apache.kandula.participant.Vote;
import org.apache.kandula.wsat.completion.CompletionInitiatorPortTypeRawXMLStub;
import org.apache.kandula.wsat.twopc.ParticipantPortTypeRawXMLStub;

public class ATCoordinator implements Registerable {

	public ATCoordinator() {
	}

	/**
	 * Registration Protocol logic for WS-Atomic Trasaction.
	 * 
	 * @see org.apache.kandula.coordinator.Registerable#register(org.apache.kandula.context.AbstractContext,
	 *      java.lang.String, org.apache.axis2.addressing.EndpointReference)
	 */
	public EndpointReference register(AbstractContext context, String protocol,
			EndpointReference participantEPR) throws AbstractKandulaException {
		ATActivityContext atContext = (ATActivityContext) context;
		atContext.lock();
		switch (atContext.getStatus()) {
		case CoordinatorStatus.STATUS_PREPARING_DURABLE:
			atContext.unlock();
			try {
				this.abortActivity(atContext);
			} catch (Exception e) {
				throw new InvalidStateException(e);
			}
			throw new InvalidStateException(
					"Coordinator is in preparing state - Durable ");
		case CoordinatorStatus.STATUS_PREPARED_SUCCESS:
			atContext.unlock();
			throw new InvalidStateException(
					"Coordinator is in prepared success state");
		case CoordinatorStatus.STATUS_COMMITTING:
			atContext.unlock();
			throw new InvalidStateException(
					"Coordinator is in committing state");
		case CoordinatorStatus.STATUS_ABORTING:
			atContext.unlock();
			throw new InvalidStateException("Coordinator is in Aborting state");
		case CoordinatorStatus.STATUS_ACTIVE:
		case CoordinatorStatus.STATUS_PREPARING_VOLATILE:
			EndpointReference epr = atContext.addParticipant(participantEPR,
					protocol);
			atContext.unlock();
			return epr;
		case CoordinatorStatus.STATUS_NONE:
		default:
			atContext.unlock();
			throw new InvalidStateException();
		}
	}

	/**
	 * Business logic for Commit operation. Completion protocol defined in
	 * Ws-AtomicTransaction specification. Initiates the 2PC protocol.
	 * 
	 * Completion participant decides to commit the transaction. First Send
	 * Prepare messages to Volatile participants,then to Durable participants
	 * registered for this transaction. If all votes as prepared or read only
	 * then issue Commit messages to all participants. Abort the transaction if
	 * atleast one participant respond as aborted.
	 * 
	 * @throws Exception
	 */
	public void commitOperation(ATActivityContext atContext) throws AbstractKandulaException {
		CompletionInitiatorPortTypeRawXMLStub stub;
		
		if (atContext == null) {
			throw new IllegalStateException(
					"No Activity Found for this Activity ID");
		}

		/*
		 * Check for states TODO Do we actually need to lock the activity
		 */
		atContext.lock();
		switch (atContext.getStatus()) {
		case CoordinatorStatus.STATUS_NONE:
		case CoordinatorStatus.STATUS_ABORTING:
			atContext.unlock();
			stub = new CompletionInitiatorPortTypeRawXMLStub(atContext
					.getCompletionParticipant());
			stub.abortedOperation();
			break;
		case CoordinatorStatus.STATUS_PREPARING_DURABLE:
		case CoordinatorStatus.STATUS_PREPARING_VOLATILE:
		case CoordinatorStatus.STATUS_PREPARED_SUCCESS:
			// If prepared success Ignore this message
			atContext.unlock();
			break;
		case CoordinatorStatus.STATUS_COMMITTING:
			atContext.unlock();
			stub = new CompletionInitiatorPortTypeRawXMLStub(atContext
					.getCompletionParticipant());
			stub.committedOperation();
			break;
		case Status.CoordinatorStatus.STATUS_ACTIVE:

			if (atContext.getVolatileParticipantCount() > 0) {
				atContext
						.setStatus(Status.CoordinatorStatus.STATUS_PREPARING_VOLATILE);
				atContext.unlock();
				volatilePrepare(atContext);
			} else if (atContext.getDurableParticipantCount() > 0) {
				atContext
						.setStatus(Status.CoordinatorStatus.STATUS_PREPARING_DURABLE);
				atContext.unlock();
				durablePrepare(atContext);
			} else {
				atContext.setStatus(Status.CoordinatorStatus.STATUS_COMMITTING);
				atContext.unlock();
				commitActivity(atContext);
			}

			break;
		default:
			atContext.unlock();
			break;
		}
	}

	/**
	 * Business logic for Rollback operation. Completion protocol defined in
	 * Ws-AtomicTransaction specification.
	 * 
	 * Completion participant decides to Rollback (abort) the transaction. Send
	 * Rollback message all participants registered for this transaction.
	 * 
	 * @throws Exception
	 */
	public void rollbackOperation(ATActivityContext atContext) throws AbstractKandulaException {
		CompletionInitiatorPortTypeRawXMLStub stub;
		/*
		 * Check for states TODO Do we need to lock the activity
		 */
		atContext.lock();
		switch (atContext.getStatus()) {
		case CoordinatorStatus.STATUS_NONE:
		case CoordinatorStatus.STATUS_ABORTING:
			atContext.unlock();
			stub = new CompletionInitiatorPortTypeRawXMLStub(atContext
					.getCompletionParticipant());
			stub.abortedOperation();
			break;
		case CoordinatorStatus.STATUS_PREPARING_DURABLE:
		case CoordinatorStatus.STATUS_PREPARING_VOLATILE:
		case CoordinatorStatus.STATUS_PREPARED_SUCCESS:
			// If prepared success Ignoring
			atContext.unlock();
			break;
		case CoordinatorStatus.STATUS_COMMITTING:
			atContext.unlock();
			stub = new CompletionInitiatorPortTypeRawXMLStub(atContext
					.getCompletionParticipant());
			stub.committedOperation();
			break;
		case Status.CoordinatorStatus.STATUS_ACTIVE:
			atContext.setStatus(Status.CoordinatorStatus.STATUS_ABORTING);
			atContext.unlock();
			abortActivity(atContext);
			break;
		default:
			atContext.unlock();
			break;
		}
	}

	/**
	 * Business logic for Prepare and ReadOnly operations. Participant responses
	 * for the prepare phase of two Phase Commit protocol defined in
	 * Ws-AtomicTransaction specification.
	 * 
	 * Participant guaranties that he can go ahead with the transaction
	 * successfuly or he already finished it succesfuly.
	 * 
	 * @param activityID
	 * @param vote
	 * @param enlistmentID
	 * @throws AbstractKandulaException
	 */
	// TODO seperate these TWO and check states for each case
	public void countVote(ATActivityContext atContext, Vote vote, String enlistmentID)
			throws AbstractKandulaException {
		ATParticipantInformation participant = atContext
				.getParticipant(enlistmentID);
		if (Vote.PREPARED.equals(vote)) {
			participant.setStatus(Status.CoordinatorStatus.STATUS_PREPARED);
		} else if (Vote.READ_ONLY.equals(vote)) {
			participant.setStatus(Status.CoordinatorStatus.STATUS_READ_ONLY);
		}
		/*
		 * There can be a two invocations of the callback methode due to race
		 * conditions at decrement preparing and count preparing
		 */
		synchronized (atContext) {
			atContext.decrementPreparingParticipantCount();
			if (!atContext.hasMorePreparing()) {
				atContext.lock();
				if (!(atContext.getStatus() == Status.CoordinatorStatus.STATUS_ABORTING)) {
					atContext.unlock();
					Method method = atContext.getCallBackMethod();
					try {
						method.invoke(this, new Object[] { atContext });
					} catch (Exception e) {
						throw new KandulaGeneralException(
								"Internal Server Error", e);
					}
				} else {
					atContext.unlock();
				}
			}
		}

	}

	/**
	 * Business logic for Abort operation. Participant response for the prepare
	 * phase of two Phase Commit protocol defined in Ws-AtomicTransaction
	 * specification.
	 * 
	 * Participant aborts the transaction. This cause the whole transactio to be
	 * aborted.
	 * 
	 * @param activityID
	 * @param enlistmentID
	 * @throws AbstractKandulaException
	 */
	public void abortedOperation(ATActivityContext atContext, String enlistmentID)
			throws AbstractKandulaException {
		synchronized (atContext) {
			atContext.lock();
			switch (atContext.getStatus()) {
			case CoordinatorStatus.STATUS_NONE:
				atContext.unlock();
				break;
			case CoordinatorStatus.STATUS_ABORTING:
				atContext.unlock();
				atContext.removeParticipant(enlistmentID);
				break;
			case CoordinatorStatus.STATUS_PREPARING_DURABLE:
			case CoordinatorStatus.STATUS_PREPARING_VOLATILE:
			case Status.CoordinatorStatus.STATUS_ACTIVE:
				atContext.unlock();
				atContext.removeParticipant(enlistmentID);
				abortActivity(atContext);
				break;
			case CoordinatorStatus.STATUS_PREPARED_SUCCESS:
			case CoordinatorStatus.STATUS_COMMITTING:
				// Invalid state
				atContext.unlock();
				break;
			default:
				atContext.unlock();
				break;
			}
		}
	}

	/**
	 * Business logic for commited operation. Participant notifies the
	 * succesfull completion of Commit phase of two Phase Commit protocol
	 * defined in Ws-AtomicTransaction specification.
	 * 
	 * Forget the participant, since he is done.
	 * 
	 * @param activityID
	 * @param enlistmentID
	 * @throws AbstractKandulaException
	 */
	public void countParticipantOutcome(ATActivityContext atContext, String enlistmentID)
			throws AbstractKandulaException {
		atContext.removeParticipant(enlistmentID);
	}

	/**
	 * This will send the rollback() messages to all the participants registered
	 * for the Transaction Do not have to check whether all the participants
	 * have replied to the prepare()
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void abortActivity(AbstractContext context)
			throws AbstractKandulaException {
		ParticipantPortTypeRawXMLStub stub = new ParticipantPortTypeRawXMLStub();
		ATActivityContext atContext = (ATActivityContext) context;
		atContext.lock();
		atContext.setStatus(Status.CoordinatorStatus.STATUS_ABORTING);
		atContext.unlock();
		Iterator participants = atContext.getAll2PCParticipants();

		while (participants.hasNext()) {
			stub.rollbackOperation(((ATParticipantInformation) participants
					.next()).getEpr());
		}
		CompletionInitiatorPortTypeRawXMLStub completionStub = new CompletionInitiatorPortTypeRawXMLStub(
				atContext.getCompletionParticipant());
		completionStub.abortedOperation();
	}

	/**
	 * This will send the commit() messages to all the participants registered
	 * for the Transaction Must check whether all the participants have replied
	 * to the prepare()
	 * 
	 * @param context
	 * @throws Exception
	 * 
	 */
	public void commitActivity(AbstractContext context)
			throws AbstractKandulaException {

		ATActivityContext atContext = (ATActivityContext) context;

		atContext.lock();
		atContext.setStatus(Status.CoordinatorStatus.STATUS_COMMITTING);
		atContext.unlock();
		Iterator participants = atContext.getAll2PCParticipants();
		if (participants.hasNext()) {
			// check whether all participants have prepared
			ParticipantPortTypeRawXMLStub stub = new ParticipantPortTypeRawXMLStub();
			while (participants.hasNext()) {
				ATParticipantInformation participant = (ATParticipantInformation) participants
						.next();
				if (!(Status.CoordinatorStatus.STATUS_READ_ONLY == participant
						.getStatus())) {
					stub.commitOperation(participant.getEpr());
				}
			}
		}
		CompletionInitiatorPortTypeRawXMLStub completionStub = new CompletionInitiatorPortTypeRawXMLStub(
				atContext.getCompletionParticipant());
		completionStub.committedOperation();
	}

	/**
	 * This method issues the oneway prepare() message. Does not wait till
	 * partipants responds. Used in 2PC after user commits as well as in
	 * subordinate scenerio, when parent issues volatile prepare(). One can
	 * check if there are any more participants to be responded by checking the
	 * hasMorePreparing() methode of the context.
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void volatilePrepare(AbstractContext context)
			throws AbstractKandulaException {
		ParticipantPortTypeRawXMLStub stub = new ParticipantPortTypeRawXMLStub();
		ATActivityContext atContext = (ATActivityContext) context;
		Iterator volatilePartipantIterator = atContext
				.getRegistered2PCParticipants(Constants.WS_AT_VOLATILE2PC);
		synchronized (atContext) {
			if (volatilePartipantIterator.hasNext()) {
				while (volatilePartipantIterator.hasNext()) {
					atContext.incrementPreparingParticipantCount();
					stub
							.prepareOperation(((ATParticipantInformation) volatilePartipantIterator
									.next()).getEpr());
				}
			}
		}

		try {
			Method method = ATCoordinator.class.getMethod("durablePrepare",
					new Class[] { AbstractContext.class });
			atContext.setCallBackMethod(method);
		} catch (Exception e) {
			throw new KandulaGeneralException("Internal Kandula Server Error ",
					e);
		}
	}

	/**
	 * This method issues the oneway prepare() message. Does not wait till
	 * partipants responds. Used in 2PC after user commits as well as in
	 * subordinate scenerio, when parent issues Durable prepare(). One can check
	 * if there are any more participants to be responded by checking the
	 * hasMorePreparing() methode of the context.
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void durablePrepare(AbstractContext context)
			throws AbstractKandulaException {
		ParticipantPortTypeRawXMLStub stub = new ParticipantPortTypeRawXMLStub();
		ATActivityContext atContext = (ATActivityContext) context;
		Iterator durablePartipantIterator = atContext
				.getRegistered2PCParticipants(Constants.WS_AT_DURABLE2PC);

		synchronized (atContext) {
			if (durablePartipantIterator.hasNext()) {
				atContext.lock();
				atContext
						.setStatus(Status.CoordinatorStatus.STATUS_PREPARING_DURABLE);
				atContext.unlock();
				while (durablePartipantIterator.hasNext()) {
					atContext.incrementPreparingParticipantCount();
					stub
							.prepareOperation(((ATParticipantInformation) durablePartipantIterator
									.next()).getEpr());
				}
			}

			try {
				Method method = ATCoordinator.class.getMethod("commitActivity",
						new Class[] { AbstractContext.class });
				atContext.setCallBackMethod(method);
			} catch (Exception e) {
				throw new KandulaGeneralException(
						"Internal Kandula Server Error ", e);
			}
		}
	}

	public void timeout(AbstractContext context){
		ATActivityContext atContext = (ATActivityContext) context;
		atContext.lock();
		switch (atContext.getStatus()) {

		case CoordinatorStatus.STATUS_ABORTING:
		case CoordinatorStatus.STATUS_COMMITTING:
		case CoordinatorStatus.STATUS_PREPARED_SUCCESS:
			atContext.unlock();
			break;
		case CoordinatorStatus.STATUS_ACTIVE:
		case CoordinatorStatus.STATUS_PREPARING_VOLATILE:
		case CoordinatorStatus.STATUS_PREPARING_DURABLE:
			try {
					abortActivity(context);
				} catch (AbstractKandulaException e) {
					e.printStackTrace();
				}
			break;
		default:
			atContext.unlock();
		}
	}
}
