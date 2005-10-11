package org.apache.kandula.wsat.completion;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.description.OutOnlyOperationDescription;
import org.apache.kandula.Constants;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.KandulaGeneralException;
import org.apache.kandula.wsat.AbstractATNotifierStub;

public class CompletionInitiatorPortTypeRawXMLStub extends
        AbstractATNotifierStub {
    public static final String AXIS2_HOME = ".";

    static {

        //creating the Service
        _service = new org.apache.axis2.description.ServiceDescription(
                new javax.xml.namespace.QName(Constants.WS_AT,
                        "CompletionInitiatorPortType"));

        //creating the operations
        org.apache.axis2.description.OperationDescription operation;
        operations = new org.apache.axis2.description.OperationDescription[2];

        operation = new OutOnlyOperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "committedOperation"));
        operations[0] = operation;
        _service.addOperation(operation);

        operation = new OutOnlyOperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "abortedOperation"));
        operations[1] = operation;
        _service.addOperation(operation);
    }

    /**
     * Constructor
     * 
     * @throws AbstractKandulaException
     */
    public CompletionInitiatorPortTypeRawXMLStub(String axis2Home,
            EndpointReference targetEndpoint) throws AbstractKandulaException {
        this.toEPR = targetEndpoint;
        try {
            //creating the configuration
            _configurationContext = new org.apache.axis2.context.ConfigurationContextFactory()
                    .buildClientConfigurationContext(axis2Home);
            _configurationContext.getAxisConfiguration().addService(_service);
        } catch (DeploymentException e) {
            throw new KandulaGeneralException(e);
        } catch (AxisFault e1) {
            throw new KandulaGeneralException(e1);
        }
        _serviceContext = _service.getParent().getServiceGroupContext(
                _configurationContext).getServiceContext(
                _service.getName().getLocalPart());
    }

    public void committedOperation() throws AbstractKandulaException {
        this.notify("Committed", Constants.WS_AT_COMMITTED, 0, null);
    }

    public void abortedOperation() throws AbstractKandulaException {
        this.notify("Aborted", Constants.WS_AT_ABORTED, 1, null);
    }

}