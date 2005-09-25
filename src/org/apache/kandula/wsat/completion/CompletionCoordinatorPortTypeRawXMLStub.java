package org.apache.kandula.wsat.completion;

import java.io.IOException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.clientapi.MessageSender;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMNamespace;
import org.apache.kandula.Constants;
import org.apache.kandula.wsat.AbstractATNotifierStub;

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

        operation = new org.apache.axis2.description.OperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "commitOperation"));
        operations[0] = operation;
        _service.addOperation(operation);

        operation = new org.apache.axis2.description.OperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "rollbackOperation"));
        operations[1] = operation;
        _service.addOperation(operation);
    }

    /**
     * Constructor
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

    public void commitOperation() throws IOException {
        //TODO must send reply to epr
        this.notify("Commit", Constants.WS_AT_COMMIT, 0, null);

    }

    public void rollbackOperation() throws IOException {
        //TODO must send reply to EPR
        this.notify("Rollback", Constants.WS_AT_ROLLBACK, 1, null);

    }

}