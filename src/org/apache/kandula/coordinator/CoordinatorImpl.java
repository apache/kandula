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
package org.apache.kandula.coordinator;

import org.apache.kandula.KandulaException;
import org.apache.kandula.coordinator.at.ATCoordinator;
import org.apache.kandula.coordinator.at.ATCoordinatorImpl;
import org.apache.kandula.coordinator.context.ActivityContext;
import org.apache.kandula.coordinator.context.ContextFactory;
import org.apache.kandula.storage.StorageFactory;
import org.apache.kandula.storage.Store;
import org.apache.kandula.typemapping.CoordinationContext;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 * 
 * This class implements the operations defined in WS-Coordination specification
 */

public class CoordinatorImpl implements Coordinator {
    Store store;

    public CoordinatorImpl() {
        StorageFactory storageFactory = StorageFactory.getInstance();
        store = storageFactory.getStore();
    }

    /**
     * @param Coordination
     *            Type
     * @return the Coordination Context created
     * 
     * Initiators can use this to Create new Distributed transactions.This will
     * take in the Coordination Type and will create an instance of the
     * reapective ActivityContext. The Coordination Context created by this can
     * be used to convey the information about the transaction between
     * initiator,Participants and coordinator
     * @throws KandulaException
     */
    public ActivityContext createCoordinationContext(String coordinationType, long expires) throws KandulaException {
        ContextFactory factory = ContextFactory.getInstance();
        ActivityContext context = factory
                .createActivity(coordinationType);
        context.getCoordinationContext().setExpires(expires);
        store.putContext(context.getCoordinationContext().getActivityID(), context);
        return context;
    }

    /**
     * @param Coordinationcontext
     * @return the interposed Coordination Context created
     * 
     * Participants decided to use this Coordinator as a interposed
     * sub-coordinator.The newly created CoordinationContext will contain the
     * same ActivityIdentifier & Protocol type. Registration EPR of the earlier
     * CoordinationContext will be replaced by the RegistrationEPR of this
     * Coordinator.
     */
    public ActivityContext createCoordinationContext(CoordinationContext coorContext) throws KandulaException{
        ContextFactory factory = ContextFactory.getInstance();
        ActivityContext context = factory
                .createActivity(coorContext);
        store.putContext(context.getCoordinationContext().getActivityID(), context);
        return context;
    }
    
    /**
     * @param coordinationProtocol
     * @param participantEPR
     * @param Activity-id
     * @return Should return the particular Coordiators End Point Reference
     * 
     * This method provides the functional logic for participants to register
     * for a particular transaction activity which was created by a initiator.
     * Registration request will be forwarded to repective protocol coordinators.
     * @throws KandulaException
     */
    public String registerParticipant(String id, String protocol, String participantEPR) throws KandulaException {

        ActivityContext context = getCoordinationContext(id);
        if (context == null) {
            throw new IllegalStateException(
                    "No Activity Found for this Activity ID");
        }
        //TODO use "switch case" when BA is present
        ATCoordinator atCoordinator = new ATCoordinatorImpl();
        return atCoordinator.register(context,protocol,participantEPR);
    }

    private ActivityContext getCoordinationContext(String id) {
        return store.getContext(id);
    }
}