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
package org.apache.kandula.initiator;

import java.io.IOException;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.ContextFactory;
import org.apache.kandula.context.at.ATActivityContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.InvalidStateException;
import org.apache.kandula.storage.StorageFactory;
import org.apache.kandula.storage.Store;
import org.apache.kandula.utility.EndpointReferenceFactory;
import org.apache.kandula.wsat.completion.CompletionCoordinatorPortTypeRawXMLStub;
import org.apache.kandula.wscoor.ActivationCoordinatorPortTypeRawXMLStub;
import org.apache.kandula.wscoor.RegistrationCoordinatorPortTypeRawXMLStub;

/**
 * @author Dasarath Weeratunge
 * @author <a href="mailto:thilina@apache.org"> Thilina Gunarathne </a>
 */

public class TransactionManager {

    private ThreadLocal threadInfo;

    //till we get reply to reference properties correctly in Axis 2 Addressing
    public static String tempID;

    public TransactionManager(String coordinationType,
                              EndpointReference coordinatorEPR) throws AbstractKandulaException {
        threadInfo = new ThreadLocal();
        //ATInitiatorTransaction transaction = new ATInitiatorTransaction(coordinatorEPR);
        AbstractContext context = ContextFactory.getInstance().createActivity(
                coordinationType, coordinatorEPR);
        if (threadInfo.get() != null)
            throw new IllegalStateException();
        threadInfo.set(context.getProperty(ATActivityContext.REQUESTER_ID));
        //TODO remove this when we get replyTo reference properties correctly
        tempID = (String) context.getProperty(ATActivityContext.REQUESTER_ID);
        Store store = StorageFactory.getInstance().getStore();
        store.put(context.getProperty(ATActivityContext.REQUESTER_ID),
                context);
    }

    /**
     * @throws Exception
     */
    public void begin() throws Exception {
        AbstractContext context = getTransaction();
        String id = (String) context
                .getProperty(ATActivityContext.REQUESTER_ID);
        ActivationCoordinatorPortTypeRawXMLStub activationCoordinator = new ActivationCoordinatorPortTypeRawXMLStub(
                ".", (EndpointReference) context
                .getProperty(ATActivityContext.ACTIVATION_EPR));
        activationCoordinator.createCoordinationContextOperation(
                org.apache.kandula.Constants.WS_AT, id);
        while (context.getCoordinationContext() == null) {
            //allow other threads to execute
            Thread.sleep(10);
        }
        RegistrationCoordinatorPortTypeRawXMLStub registrationCoordinator = new RegistrationCoordinatorPortTypeRawXMLStub(
                ".", context.getCoordinationContext().getRegistrationService());
        EndpointReference registrationRequeterPortEPR = EndpointReferenceFactory
                .getInstance().getCompletionParticipantEndpoint(id);
        registrationCoordinator.registerOperation(
                org.apache.kandula.Constants.WS_AT_COMPLETION,
                registrationRequeterPortEPR, id);
        while (context.getProperty(ATActivityContext.COORDINATION_EPR)==null) {
            Thread.sleep(10);
        }
    }

    public void commit() throws AbstractKandulaException, IOException {
        AbstractContext context = getTransaction();
        EndpointReference coordinationEPR = (EndpointReference)context.getProperty(ATActivityContext.COORDINATION_EPR);
        CompletionCoordinatorPortTypeRawXMLStub stub = new CompletionCoordinatorPortTypeRawXMLStub(".",coordinationEPR);
        stub.commitOperation();
    }

    public void rollback() throws AbstractKandulaException, IOException {
        AbstractContext context = getTransaction();
        EndpointReference coordinationEPR = (EndpointReference)context.getProperty(ATActivityContext.COORDINATION_EPR);
        CompletionCoordinatorPortTypeRawXMLStub stub = new CompletionCoordinatorPortTypeRawXMLStub(".",coordinationEPR);
        stub.rollbackOperation();
    }

    //	public Transaction suspend() {
    //		Transaction tx= getTransaction();
    //		forget();
    //		return tx;
    //	}
    //
    //	public void resume(Transaction tx) {
    //		if (threadInfo.get() != null)
    //			throw new IllegalStateException();
    //		else
    //			threadInfo.set(tx);
    //	}
    //
    //	public void forget() {
    //		threadInfo.set(null);
    //	}

    private AbstractContext getTransaction() throws AbstractKandulaException {
        Object key = threadInfo.get();
        AbstractContext context = (AbstractContext) StorageFactory.getInstance().getStore()
                .get(key);
        if (context == null) {
            throw new InvalidStateException("No Activity Found");
        }
        return context;
    }
}