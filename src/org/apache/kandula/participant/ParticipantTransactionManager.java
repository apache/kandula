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
package org.apache.kandula.participant;

import org.apache.kandula.Status;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.at.ATParticipantContext;
import org.apache.kandula.faults.InvalidStateException;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ParticipantTransactionManager {
    public Vote prepare(AbstractContext context) throws InvalidStateException {
        ATParticipantContext atContext = (ATParticipantContext) context;
        atContext.lock();
        switch (context.getStatus()) {
        case (Status.CoordinatorStatus.STATUS_NONE):
            //TODO send aborted
            atContext.unlock();
            return Vote.ABORT;
        case (Status.CoordinatorStatus.STATUS_PREPARING):
        case (Status.CoordinatorStatus.STATUS_PREPARED):
        case (Status.CoordinatorStatus.STATUS_COMMITTING):
            //Ignore the message
            atContext.unlock();
            return Vote.NONE;
        case (Status.CoordinatorStatus.STATUS_PREPARED_SUCCESS):
            atContext.unlock();
            return Vote.PREPARED;
        case (Status.CoordinatorStatus.STATUS_ACTIVE):
            atContext.setStatus(Status.CoordinatorStatus.STATUS_PREPARING);
            KandulaResource resource = atContext.getResource();
            return resource.prepare();
        default:
            context.unlock();
            throw new InvalidStateException();
        }
    }
}