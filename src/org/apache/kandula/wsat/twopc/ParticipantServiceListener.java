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
package org.apache.kandula.wsat.twopc;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.description.InOnlyOperationDescription;
import org.apache.axis2.description.ParameterImpl;
import org.apache.axis2.description.ServiceDescription;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.apache.axis2.receivers.RawXMLINOnlyMessageReceiver;
import org.apache.kandula.Constants;
import org.apache.kandula.utility.KandulaListener;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ParticipantServiceListener {

    private static ParticipantServiceListener instance = null;

    private EndpointReference epr = null;

    private ParticipantServiceListener() {
        super();
    }

    public static ParticipantServiceListener getInstance() {
        if (instance == null) {
            instance = new ParticipantServiceListener();
        }
        return instance;
    }

    public EndpointReference getEpr() throws IOException {
        if (epr == null) {
            this.epr = setupListener();
        }
        return this.epr;
    }

    private EndpointReference setupListener() throws IOException {
        QName serviceName = new QName("ParticipantPortType");
        String className = ParticipantPortTypeRawXMLSkeleton.class.getName();
        ServiceDescription service = new ServiceDescription(serviceName);
        service.addParameter(new ParameterImpl(
                AbstractMessageReceiver.SERVICE_CLASS, className));
        service.setFileName(className);

        QName prepareOperationName = new QName(Constants.WS_COOR,
                "prepareOperation");
        org.apache.axis2.description.OperationDescription prepareOperationDesc;
        String prepareMapping = Constants.WS_AT_PREPARE;
        prepareOperationDesc = new InOnlyOperationDescription();
        prepareOperationDesc.setName(prepareOperationName);
        prepareOperationDesc
                .setMessageReceiver(new RawXMLINOnlyMessageReceiver());
        // Adding the WSA Action mapping to the operation
        service.addMapping(prepareMapping, prepareOperationDesc);
        service.addOperation(prepareOperationDesc);

        QName commitOperationName = new QName(Constants.WS_COOR,
                "commitOperation");
        org.apache.axis2.description.OperationDescription commitOperationDesc;
        String commitMapping = Constants.WS_AT_COMMIT;
        commitOperationDesc = new InOnlyOperationDescription();
        commitOperationDesc.setName(commitOperationName);
        commitOperationDesc
                .setMessageReceiver(new RawXMLINOnlyMessageReceiver());
        // Adding the WSA Action mapping to the operation
        service.addMapping(commitMapping, commitOperationDesc);
        service.addOperation(commitOperationDesc);

        QName rollbackOperationName = new QName(Constants.WS_COOR,
                "rollbackOperation");
        org.apache.axis2.description.OperationDescription rollbackOperationDesc;
        String rollbackMapping = Constants.WS_AT_ROLLBACK;
        rollbackOperationDesc = new InOnlyOperationDescription();
        rollbackOperationDesc.setName(rollbackOperationName);
        rollbackOperationDesc
                .setMessageReceiver(new RawXMLINOnlyMessageReceiver());
        // Adding the WSA Action mapping to the operation
        service.addMapping(rollbackMapping, rollbackOperationDesc);
        service.addOperation(rollbackOperationDesc);

        KandulaListener listener = KandulaListener.getInstance();
        listener.addService(service);
        listener.start();
        return new EndpointReference(listener.getHost()
                + serviceName.getLocalPart());
    }
}