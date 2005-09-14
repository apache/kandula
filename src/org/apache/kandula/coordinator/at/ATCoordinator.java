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
import org.apache.kandula.KandulaException;
import org.apache.kandula.Status;
import org.apache.kandula.Status.CoordinatorStatus;
import org.apache.kandula.coordinator.CoordinatorUtils;
import org.apache.kandula.coordinator.Registerable;
import org.apache.kandula.context.ActivityContext;
import org.apache.kandula.context.at.ATActivityContext;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ATCoordinator implements Registerable {

    public EndpointReference register(ActivityContext context, String protocol,
            EndpointReference participantEPR) throws KandulaException {
        context.lock();
        switch (context.getStatus()) {
        case CoordinatorStatus.STATUS_PREPARING_DURABLE:
            context.unlock();
            this.abort(context);
            throw new IllegalStateException(
                    "Coordinator is in preparing state - Durable ");
        case CoordinatorStatus.STATUS_PREPARED_SUCCESS:
            context.unlock();
            throw new IllegalStateException(
                    "Coordinator is in prepared success state");
        case CoordinatorStatus.STATUS_COMMITTING:
            context.unlock();
            throw new IllegalStateException(
                    "Coordinator is in committing state");
        case CoordinatorStatus.STATUS_ABORTING:
            throw new IllegalStateException("Coordinator is in Aborting state");
        case CoordinatorStatus.STATUS_ACTIVE:
        case CoordinatorStatus.STATUS_PREPARING_VOLATILE:
            return addParticipant(context,protocol, participantEPR);
        case CoordinatorStatus.STATUS_NONE:
        default:
            context.unlock();
            throw new IllegalStateException();
        }
    }

    /**
     * @param Activity
     *            Id taken from the Request
     * @return should be a notification This wraps the Commit operation defined
     *         in Ws-AtomicTransaction specification.
     */
    public String commitOperation(String id) throws IllegalAccessException {
        ActivityContext context = CoordinatorUtils.getActivityContext(id);
        // if store throws a Exception capture it
        if (context == null) {
            throw new IllegalStateException(
                    "No Activity Found for this Activity ID");
        }

        /*
         * Check for states Do we need to lock the activity
         */
        context.lock();
        switch (context.getStatus()) {
        case CoordinatorStatus.STATUS_NONE:
        case CoordinatorStatus.STATUS_ABORTING:
            context.unlock();
            return "Aborted";
        case CoordinatorStatus.STATUS_PREPARING_DURABLE:
        case CoordinatorStatus.STATUS_PREPARING_VOLATILE:
        case CoordinatorStatus.STATUS_PREPARED_SUCCESS:
            //If prepared success Ignore this message
            context.unlock();
            return null;
        case CoordinatorStatus.STATUS_COMMITTING:
            context.unlock();
            return "Committed";
        case Status.CoordinatorStatus.STATUS_ACTIVE:
            int result;
            result = volatilePrepare(context);

            if (result == Status.CoordinatorStatus.STATUS_ABORTING) {
                context.lock();
                context.setStatus(Status.CoordinatorStatus.STATUS_ABORTING);
                context.unlock();
                abort(context);
            }

            result = commit(context);
            return null;
        default:
            context.unlock();
            return null;
        }

    }

    public String rollbackOperation(String id) throws IllegalAccessException {
        ActivityContext context = CoordinatorUtils.getActivityContext(id);
        // if store throws a Exception capture it
        if (context == null) {
            throw new IllegalStateException(
                    "No Activity Found for this Activity ID");
        }
        /*
         * Check for states Do we need to lock the activity
         */
        context.lock();
        switch (context.getStatus()) {
        case CoordinatorStatus.STATUS_NONE:
        case CoordinatorStatus.STATUS_ABORTING:
            context.unlock();
            return "Aborted";
        case CoordinatorStatus.STATUS_PREPARING_DURABLE:
        case CoordinatorStatus.STATUS_PREPARING_VOLATILE:
        case CoordinatorStatus.STATUS_PREPARED_SUCCESS:
            //If prepared success Ignore this message
            context.unlock();
            return null;
        case CoordinatorStatus.STATUS_COMMITTING:
            context.unlock();
            return "Committed";
        case Status.CoordinatorStatus.STATUS_ACTIVE:
            context.setStatus(Status.CoordinatorStatus.STATUS_ABORTING);
            context.unlock();
            int result = abort(context);
            //                if (result ==fdsfsfd)
            //                {
            //                    throw new Exception
            //                }

            return null;
        default:
            context.unlock();
            return null;
        }
    }

    /**
     * @param context
     * @return the status of the Activity after the volatile preparation
     * @see This methode issues the oneway prepare() message. Does not wait till
     *      partipants responds. Used in 2PC after user commits as well as in
     *      subordinate scenerio, when parent issues volatile prepare(). One can
     *      check if there are any more participants to be responded by checking
     *      the hasMorePreparing() methode of the context.
     */
    public int volatilePrepare(ActivityContext context) {
        ATActivityContext atContext = (ATActivityContext) context;
        Iterator volatilePartipantIterator = atContext
                .getRegisteredParticipants(Constants.WS_AT_VOLATILE2PC);
        if (volatilePartipantIterator.hasNext()) {
            atContext
                    .setStatus(Status.CoordinatorStatus.STATUS_PREPARING_VOLATILE);
            atContext.unlock();
            while (volatilePartipantIterator.hasNext()) {
                atContext.countPreparing();
                // participantPortType port
                // port.prepare
            }
        }
        return atContext.getStatus();
    }

    /**
     * @param context
     * @return the status of the Activity after the Durable preparation
     * @see This methode issues the oneway prepare() message. Does not wait till
     *      partipants responds. Used in 2PC after user commits as well as in
     *      subordinate scenerio, when parent issues Durable prepare(). One can
     *      check if there are any more participants to be responded by checking
     *      the hasMorePreparing() methode of the context.
     */
    public int durablePrepare(ActivityContext context) {
        ATActivityContext atContext = (ATActivityContext) context;
        Iterator durablePartipantIterator = atContext
                .getRegisteredParticipants(Constants.WS_AT_DURABLE2PC);
        if (durablePartipantIterator.hasNext()) {
            // wait till all the Volatile prepare()'s are done
            while (atContext.hasMorePreparing()) {
                if (atContext.getStatus() == Status.CoordinatorStatus.STATUS_ABORTING)
                    return Status.CoordinatorStatus.STATUS_ABORTING;
            }
            atContext.lock();
            atContext
                    .setStatus(Status.CoordinatorStatus.STATUS_PREPARING_DURABLE);
            atContext.unlock();
            while (durablePartipantIterator.hasNext()) {
                atContext.countPreparing();
                //port.prepare
            }
        }
        return atContext.getStatus();
    }

    /**
     * @param context
     * @return the status of the Activity
     * @see This will send the commit() messages to all the participants
     *      registered for the Transaction Must check whether all the
     *      participants have replied to the prepare()
     */
    public int commit(ActivityContext context) {
        // check whether all participants are prepared
        ATActivityContext atContext = (ATActivityContext) context;
        while (atContext.hasMorePreparing()) {
            if (atContext.getStatus() == Status.CoordinatorStatus.STATUS_ABORTING)
                return Status.CoordinatorStatus.STATUS_ABORTING;
        }
        atContext.lock();
        atContext.setStatus(Status.CoordinatorStatus.STATUS_COMMITTING);
        atContext.unlock();
        Iterator participants = atContext.getAllParticipants();

        while (participants.hasNext()) {
            //port.commit(participant)
        }
        return Status.CoordinatorStatus.STATUS_COMMITTING;
    }

    /**
     * @param context
     * @return the status of the Activity
     * @see This will send the rollback() messages to all the participants
     *      registered for the Transaction Do not have to check whether all the
     *      participants have replied to the prepare()
     */
    public int abort(ActivityContext context) {
        // check whether all participants are prepared
        context.lock();
        context.setStatus(Status.CoordinatorStatus.STATUS_ABORTING);
        context.unlock();
        Iterator participants = context.getAllParticipants();

        while (participants.hasNext()) {
            //port.rollback(participant)
        }
        return Status.CoordinatorStatus.STATUS_ABORTING;
    }

    public EndpointReference addParticipant(ActivityContext context, String protocol,
            EndpointReference participantEPR) throws KandulaException {
        ATActivityContext atContext = (ATActivityContext) context;
        if (protocol.equals(Constants.WS_AT_DURABLE2PC))
            return atContext.addParticipant(participantEPR, protocol);
        else if (protocol.equals(Constants.WS_AT_VOLATILE2PC))
            return atContext.addParticipant(participantEPR, protocol);
        else if (protocol.equals(Constants.WS_AT_COMPLETION))
            return atContext.addParticipant(participantEPR, protocol);
        else
            throw new KandulaException(new IllegalArgumentException(
                    "Unsupported Protocol"));
    }
}