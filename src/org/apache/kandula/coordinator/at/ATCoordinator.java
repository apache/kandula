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
import org.apache.kandula.KandulaException;
import org.apache.kandula.coordinator.context.ActivityContext;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public interface ATCoordinator {

    public abstract EndpointReference register(ActivityContext context, String protocol,
            EndpointReference participantEPR) throws KandulaException;

    /**
     * @param Activity
     *            Id taken from the Request
     * @return should be a notification. 
     * This wraps the Commit operation defined
     *         in Ws-AtomicTransaction specification.
     */
    public abstract String commitOperation(String id)  throws IllegalAccessException ;

    /**
     * @param Activity
     *            Id taken from the Request
     * @return should be a notification.
     * This wraps the rollback operation defined
     *         in Ws-AtomicTransaction specification.
     */
    public abstract String rollbackOperation(String id)  throws IllegalAccessException ;

    /**
     * @param context
     * @return the status of the Activity after the volatile preparation
     * @see This methode issues the oneway prepare() message. Does not wait till
     *      partipants responds. Used in 2PC after user commits as well as in
     *      subordinate scenerio, when parent issues volatile prepare(). One can
     *      check if there are any more participants to be responded by checking
     *      the hasMorePreparing() methode of the context.
     */
    public abstract int volatilePrepare(ActivityContext context);

    /**
     * @param context
     * @return the status of the Activity after the Durable preparation
     * @see This methode issues the oneway prepare() message. Does not wait till
     *      partipants responds. Used in 2PC after user commits as well as in
     *      subordinate scenerio, when parent issues Durable prepare(). One can
     *      check if there are any more participants to be responded by checking
     *      the hasMorePreparing() methode of the context.
     */
    public abstract int durablePrepare(ActivityContext context);

    /**
     * @param context
     * @return the status of the Activity
     * @see This will send the commit() messages to all the participants
     *      registered for the Transaction Must check whether all the
     *      participants have replied to the prepare()
     */
    public abstract int commit(ActivityContext context);

    /**
     * @param context
     * @return the status of the Activity
     * @see This will send the rollback() messages to all the participants
     *      registered for the Transaction Do not have to check whether all the
     *      participants have replied to the prepare()
     */
    public abstract int abort(ActivityContext context);
    
    public EndpointReference addParticipant(ActivityContext context, String protocol,
            EndpointReference participantEPR) throws KandulaException;
}