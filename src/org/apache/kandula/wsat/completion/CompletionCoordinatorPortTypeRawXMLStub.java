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
package org.apache.kandula.wsat.completion;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.description.OutOnlyOperationDescription;
import org.apache.kandula.Constants;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.wsat.AbstractATNotifierStub;
/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class CompletionCoordinatorPortTypeRawXMLStub extends
        AbstractATNotifierStub {
    public static final String AXIS2_HOME = ".";

    static {

        //creating the Service
        _service = new org.apache.axis2.description.ServiceDescription(
                new javax.xml.namespace.QName(Constants.WS_AT,
                        "CompletionCoordinatorPortType"));

        //creating the operations
        org.apache.axis2.description.OperationDescription operation;
        operations = new org.apache.axis2.description.OperationDescription[2];

        operation = new OutOnlyOperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "commitOperation"));
        operations[0] = operation;
        _service.addOperation(operation);

        operation = new OutOnlyOperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "rollbackOperation"));
        operations[1] = operation;
        _service.addOperation(operation);
    }

    /**
     * Constructor
     * 
     * @throws AxisFault
     */
    public CompletionCoordinatorPortTypeRawXMLStub(String axis2Home,
            EndpointReference targetEndpoint) throws AxisFault {
        this.toEPR = targetEndpoint;
        //creating the configuration
        _configurationContext = new org.apache.axis2.context.ConfigurationContextFactory()
                .buildClientConfigurationContext(axis2Home);
        _configurationContext.getAxisConfiguration().addService(_service);
        _serviceContext = _service.getParent().getServiceGroupContext(
                _configurationContext).getServiceContext(
                _service.getName().getLocalPart());
    }

    public void commitOperation() throws AbstractKandulaException {
        //TODO must send reply to epr
        this.notify("Commit", Constants.WS_AT_COMMIT, 0, null);

    }

    public void rollbackOperation() throws AbstractKandulaException {
        //TODO must send reply to EPR
        this.notify("Rollback", Constants.WS_AT_ROLLBACK, 1, null);

    }

}