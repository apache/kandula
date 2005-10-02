package org.apache.kandula.wsat.twopc;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.kandula.Constants;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.KandulaGeneralException;
import org.apache.kandula.wsat.AbstractATNotifierStub;

public class ParticipantPortTypeRawXMLStub extends AbstractATNotifierStub {
    public static final String AXIS2_HOME = ".";

    static {

        //creating the Service
        _service = new org.apache.axis2.description.ServiceDescription(
                new javax.xml.namespace.QName(Constants.WS_AT,
                        "ParticipantPortType"));

        //creating the operations
        org.apache.axis2.description.OperationDescription operation;
        operations = new org.apache.axis2.description.OperationDescription[3];

        operation = new org.apache.axis2.description.OperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "prepareOperation"));
        operations[0] = operation;
        _service.addOperation(operation);

        operation = new org.apache.axis2.description.OperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "committOperation"));
        operations[1] = operation;
        _service.addOperation(operation);

        operation = new org.apache.axis2.description.OperationDescription();
        operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
                "rollbackOperation"));
        operations[2] = operation;
        _service.addOperation(operation);
    }

    /**
     * Constructor
     */
    public ParticipantPortTypeRawXMLStub(String axis2Home)
            throws AbstractKandulaException {

        //creating the configuration
        try {
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

    public void prepareOperation(EndpointReference targetEndpoint)
            throws AbstractKandulaException {
        //TODO must send reply TO epr
        this.toEPR = targetEndpoint;
        this.notify("Prepare", Constants.WS_AT_PREPARE, 0, null);

    }

    public void commitOperation(EndpointReference targetEndpoint)
            throws AbstractKandulaException {
        //TODO must send reply to epr
        this.toEPR = targetEndpoint;
        this.notify("Commit", Constants.WS_AT_COMMIT, 1, null);

    }

    public void rollbackOperation(EndpointReference targetEndpoint)
            throws AbstractKandulaException {
        //TODO must send reply to epr
        this.toEPR = targetEndpoint;
        this.notify("Rollback", Constants.WS_AT_ROLLBACK, 2, null);
    }

}