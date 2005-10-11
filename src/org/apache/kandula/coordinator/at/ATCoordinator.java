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

import java.util.Iterator;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.Status.CoordinatorStatus;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.Participant;
import org.apache.kandula.context.at.ATActivityContext;
import org.apache.kandula.coordinator.Registerable;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.InvalidStateException;
import org.apache.kandula.participant.Vote;
import org.apache.kandula.storage.StorageFactory;
import org.apache.kandula.storage.Store;
import org.apache.kandula.wsat.completion.CompletionInitiatorPortTypeRawXMLStub;
import org.apache.kandula.wsat.twopc.ParticipantPortTypeRawXMLStub;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ATCoordinator implements Registerable {

    private Store store;

    public ATCoordinator() {
        StorageFactory storageFactory = StorageFactory.getInstance();
        store = storageFactory.getStore();
    }

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
            atContext.unlock();
            return atContext.addParticipant(participantEPR, protocol);
        case CoordinatorStatus.STATUS_NONE:
        default:
            atContext.unlock();
            throw new InvalidStateException();
        }
    }

    /**
     * should send be a notification This wraps the Commit operation defined in
     * Ws-AtomicTransaction specification.
     * 
     * @throws Exception
     */
    public void commitOperation(String id) throws AbstractKandulaException {
        CompletionInitiatorPortTypeRawXMLStub stub;
        ATActivityContext atContext = (ATActivityContext) store.get(id);

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
            stub = new CompletionInitiatorPortTypeRawXMLStub(".", atContext
                    .getCompletionParticipant());
            stub.abortedOperation();
            break;
        case CoordinatorStatus.STATUS_PREPARING_DURABLE:
        case CoordinatorStatus.STATUS_PREPARING_VOLATILE:
        case CoordinatorStatus.STATUS_PREPARED_SUCCESS:
            //If prepared success Ignore this message
            atContext.unlock();
            break;
        case CoordinatorStatus.STATUS_COMMITTING:
            atContext.unlock();
            stub = new CompletionInitiatorPortTypeRawXMLStub(".", atContext
                    .getCompletionParticipant());
            stub.committedOperation();
            break;
        case Status.CoordinatorStatus.STATUS_ACTIVE:
            atContext.setStatus(Status.CoordinatorStatus.STATUS_PREPARING);
            atContext.unlock();
            volatilePrepare(atContext);
            // wait till all the Volatile prepare()'s are done
            while (atContext.hasMorePreparing()) {
                if (atContext.getStatus() == Status.CoordinatorStatus.STATUS_ABORTING) {
                    abortActivity(atContext);
                    stub = new CompletionInitiatorPortTypeRawXMLStub(".",
                            atContext.getCompletionParticipant());
                    stub.abortedOperation();
                    return;
                }
            }
            durablePrepare(atContext);
            //wait till all the Durable prepare()'s are done
            while (atContext.hasMorePreparing()) {
                if (atContext.getStatus() == Status.CoordinatorStatus.STATUS_ABORTING)
                    abortActivity(atContext);

                return;
            }
            commitActivity(atContext);
            break;
        default:
            atContext.unlock();
            break;
        }

    }

    public void rollbackOperation(String id) throws Exception {
        CompletionInitiatorPortTypeRawXMLStub stub;
        ATActivityContext atContext = (ATActivityContext) store.get(id);

        // if store throws a Exception capture it
        if (atContext == null) {
            throw new IllegalStateException(
                    "No Activity Found for this Activity ID");
        }
        /*
         * Check for states TODO Do we need to lock the activity
         */
        atContext.lock();
        switch (atContext.getStatus()) {
        case CoordinatorStatus.STATUS_NONE:
        case CoordinatorStatus.STATUS_ABORTING:
            atContext.unlock();
            stub = new CompletionInitiatorPortTypeRawXMLStub(".", atContext
                    .getCompletionParticipant());
            stub.abortedOperation();
            break;
        case CoordinatorStatus.STATUS_PREPARING_DURABLE:
        case CoordinatorStatus.STATUS_PREPARING_VOLATILE:
        case CoordinatorStatus.STATUS_PREPARED_SUCCESS:
            //If prepared success Ignoring
            atContext.unlock();
            break;
        case CoordinatorStatus.STATUS_COMMITTING:
            atContext.unlock();
            stub = new CompletionInitiatorPortTypeRawXMLStub(".", atContext
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
     * @param context
     * @throws Exception
     * @see This methode issues the oneway prepare() message. Does not wait till
     *      partipants responds. Used in 2PC after user commits as well as in
     *      subordinate scenerio, when parent issues volatile prepare(). One can
     *      check if there are any more participants to be responded by checking
     *      the hasMorePreparing() methode of the context.
     */
    public void volatilePrepare(AbstractContext context)
            throws AbstractKandulaException {
        ParticipantPortTypeRawXMLStub stub = new ParticipantPortTypeRawXMLStub(
                ".");
        ATActivityContext atContext = (ATActivityContext) context;
        Iterator volatilePartipantIterator = atContext
                .getRegistered2PCParticipants(Constants.WS_AT_VOLATILE2PC);
        if (volatilePartipantIterator.hasNext()) {
            atContext.lock();
            atContext
                    .setStatus(Status.CoordinatorStatus.STATUS_PREPARING_VOLATILE);
            atContext.unlock();
            while (volatilePartipantIterator.hasNext()) {
                atContext.countPreparing();
                stub.prepareOperation(((Participant) volatilePartipantIterator
                        .next()).getEpr());
            }
        }
    }

    public void countVote(String activityID, Vote vote, String enlistmentID)
            throws AbstractKandulaException {
        ATActivityContext context = (ATActivityContext) store.get(activityID);
        Participant participant = context.getParticipant(enlistmentID);

        if (Vote.PREPARED.equals(vote)) {
            participant.setStatus(Status.CoordinatorStatus.STATUS_PREPARED);
        } else if (Vote.READ_ONLY.equals(vote)) {
            participant.setStatus(Status.CoordinatorStatus.STATUS_READ_ONLY);
        } else if (Vote.ABORT.equals(vote)) {
            participant.setStatus(Status.ParticipantStatus.STATUS_ABORTED);
            abortActivity(context);
        }
        context.decrementPreparing();
    }

    /**
     * @param context
     * @throws Exception
     * @see This methode issues the oneway prepare() message. Does not wait till
     *      partipants responds. Used in 2PC after user commits as well as in
     *      subordinate scenerio, when parent issues Durable prepare(). One can
     *      check if there are any more participants to be responded by checking
     *      the hasMorePreparing() methode of the context.
     */
    public void durablePrepare(AbstractContext context)
            throws AbstractKandulaException {
        ParticipantPortTypeRawXMLStub stub = new ParticipantPortTypeRawXMLStub(
                ".");
        ATActivityContext atContext = (ATActivityContext) context;
        Iterator durablePartipantIterator = atContext
                .getRegistered2PCParticipants(Constants.WS_AT_DURABLE2PC);
        if (durablePartipantIterator.hasNext()) {

            atContext.lock();
            atContext
                    .setStatus(Status.CoordinatorStatus.STATUS_PREPARING_DURABLE);
            atContext.unlock();
            while (durablePartipantIterator.hasNext()) {
                atContext.countPreparing();
                stub.prepareOperation(((Participant) durablePartipantIterator
                        .next()).getEpr());
            }
        }
    }

    /**
     * @param context
     * @throws Exception
     * @see This will send the commit() messages to all the participants
     *      registered for the Transaction Must check whether all the
     *      participants have replied to the prepare()
     */
    private void commitActivity(AbstractContext context)
            throws AbstractKandulaException {
        // check whether all participants have prepared
        ParticipantPortTypeRawXMLStub stub = new ParticipantPortTypeRawXMLStub(
                ".");
        ATActivityContext atContext = (ATActivityContext) context;

        atContext.lock();
        atContext.setStatus(Status.CoordinatorStatus.STATUS_COMMITTING);
        atContext.unlock();
        Iterator participants = atContext.getAll2PCParticipants();
        while (participants.hasNext()) {
            stub.commitOperation(((Participant) participants.next()).getEpr());
        }
        CompletionInitiatorPortTypeRawXMLStub completionStub = new CompletionInitiatorPortTypeRawXMLStub(
                ".", atContext.getCompletionParticipant());
        completionStub.committedOperation();
    }

    /**
     * @param context
     * @throws Exception
     * @see This will send the rollback() messages to all the participants
     *      registered for the Transaction Do not have to check whether all the
     *      participants have replied to the prepare()
     */
    private void abortActivity(AbstractContext context)
            throws AbstractKandulaException {
        ParticipantPortTypeRawXMLStub stub = new ParticipantPortTypeRawXMLStub(
                ".");
        ATActivityContext atContext = (ATActivityContext) context;
        atContext.lock();
        atContext.setStatus(Status.CoordinatorStatus.STATUS_ABORTING);
        atContext.unlock();
        Iterator participants = atContext.getAll2PCParticipants();

        while (participants.hasNext()) {
            stub
                    .rollbackOperation(((Participant) participants.next())
                            .getEpr());
        }
        CompletionInitiatorPortTypeRawXMLStub completionStub = new CompletionInitiatorPortTypeRawXMLStub(
                ".", atContext.getCompletionParticipant());
        completionStub.abortedOperation();
    }

}