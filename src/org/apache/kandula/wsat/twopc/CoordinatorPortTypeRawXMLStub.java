package org.apache.kandula.wsat.twopc;

import java.io.IOException;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.clientapi.MessageSender;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMNamespace;
import org.apache.kandula.Constants;
import org.apache.kandula.wsat.AbstractATNotifierStub;

public class CoordinatorPortTypeRawXMLStub extends AbstractATNotifierStub {
    public static final String AXIS2_HOME = ".";

    static {

        //creating the Service
        _service = new org.apache.axis2.description.ServiceDescription(
                new javax.xml.namespace.QName(Constants.WS_AT,
                        "CoordinatorPortType"));

        //creating the operations
        org.apache.axis2.description.OperationDescription operation;
        operations = new org.apache.axis2.description.OperationDescription[5];

        operation = new org.apache.axis2.description.OperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "PreparedOperation"));
        operations[0] = operation;
        _service.addOperation(operation);

        operation = new org.apache.axis2.description.OperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "AbortedOperation"));
        operations[1] = operation;
        _service.addOperation(operation);
        operation = new org.apache.axis2.description.OperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "ReadOnlyOperation"));
        operations[2] = operation;
        _service.addOperation(operation);

        operation = new org.apache.axis2.description.OperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "CommittedOperation"));
        operations[3] = operation;
        _service.addOperation(operation);
        operation = new org.apache.axis2.description.OperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "ReplayOperation"));
        operations[4] = operation;
        _service.addOperation(operation);
    }

    /**
     * Constructor
     */
    public CoordinatorPortTypeRawXMLStub(String axis2Home,
            EndpointReference targetEndpoint) throws java.lang.Exception {
        this.toEPR = targetEndpoint;
        //creating the configuration
        _configurationContext = new org.apache.axis2.context.ConfigurationContextFactory()
                .buildClientConfigurationContext(axis2Home);
        _configurationContext.getAxisConfiguration().addService(_service);
        _serviceContext = _service.getParent().getServiceGroupContext(
                _configurationContext).getServiceContext(
                _service.getName().getLocalPart());
    }

    public void preparedOperation() throws IOException {
        // must send reply to epr
        this.notify("Prepared", Constants.WS_AT_PREPARED, 0, null);
    }

    public void abortedOperation() throws IOException {
        this.notify("Aborted", Constants.WS_AT_ABORTED, 1, null);
    }

    public void readOnlyOperation() throws IOException {
        this.notify("ReadOnly", Constants.WS_AT_READONLY, 2, null);

    }

    public void committedOperation() throws IOException {
        this.notify("Committed", Constants.WS_AT_COMMITTED, 3, null);

    }

    public void replayOperation() throws IOException {
        //must send reply to epr
        this.notify("Replay", Constants.WS_AT_REPLAY, 4, null);
    }

}