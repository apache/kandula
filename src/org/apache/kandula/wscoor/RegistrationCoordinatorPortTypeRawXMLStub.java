package org.apache.kandula.wscoor;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.AnyContentType;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.clientapi.MessageSender;
import org.apache.axis2.description.InOnlyOperationDescription;
import org.apache.axis2.description.OutInOperationDescription;
import org.apache.axis2.description.ParameterImpl;
import org.apache.axis2.description.ServiceDescription;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMNamespace;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.apache.axis2.receivers.RawXMLINOnlyMessageReceiver;
import org.apache.kandula.Constants;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.KandulaGeneralException;
import org.apache.kandula.utility.KandulaListener;
import org.apache.kandula.utility.KandulaUtils;

public class RegistrationCoordinatorPortTypeRawXMLStub extends
        org.apache.axis2.clientapi.Stub {
    public static final String AXIS2_HOME = ".";

    protected static org.apache.axis2.description.OperationDescription[] _operations;

    static {

        //creating the Service
        _service = new org.apache.axis2.description.ServiceDescription(
                new javax.xml.namespace.QName(
                        "http://schemas.xmlsoap.org/ws/2003/09/wscoor",
                        "RegistrationCoordinatorPortType"));

        //creating the operations
        org.apache.axis2.description.OperationDescription __operation;
        _operations = new org.apache.axis2.description.OperationDescription[1];

        __operation = new OutInOperationDescription();
        __operation.setName(new javax.xml.namespace.QName(
                "http://schemas.xmlsoap.org/ws/2003/09/wscoor",
                "RegisterOperation"));
        _operations[0] = __operation;
        _service.addOperation(__operation);

    }

    /**
     * Constructor
     */
    public RegistrationCoordinatorPortTypeRawXMLStub(String axis2Home,
            EndpointReference targetEndpoint) throws AbstractKandulaException {
        this.toEPR = targetEndpoint;
        try {
            //creating the configuration
            _configurationContext = new org.apache.axis2.context.ConfigurationContextFactory()
                    .buildClientConfigurationContext(axis2Home);

            _configurationContext.getAxisConfiguration().addService(_service);
        } catch (AxisFault e1) {
            throw new KandulaGeneralException(e1);
        }

        _serviceContext = _service.getParent().getServiceGroupContext(
                _configurationContext).getServiceContext(
                _service.getName().getLocalPart());
    }

    public void registerOperation(String protocolType, EndpointReference epr,
            String id) throws IOException {

        EndpointReference replyToEpr;

        org.apache.axis2.context.MessageContext messageContext = getMessageContext();
        org.apache.axis2.soap.SOAPEnvelope env = createSOAPEnvelope(
                protocolType, epr);
        messageContext.setEnvelope(env);

        replyToEpr = setupListener();
        AnyContentType refParameters = new AnyContentType();
        refParameters.addReferenceValue(Constants.REQUESTER_ID_PARAMETER, id);
        replyToEpr.setReferenceParameters(refParameters);

        MessageSender messageSender = new MessageSender(_serviceContext);
        messageSender.setReplyTo(replyToEpr);
        messageSender.setTo(this.toEPR);
        messageSender.setWsaAction(Constants.WS_COOR_REGISTER);

        messageSender
                .setSenderTransport(org.apache.axis2.Constants.TRANSPORT_HTTP);
        messageSender.send(_operations[0], messageContext);
    }

    private org.apache.axis2.soap.SOAPEnvelope createSOAPEnvelope(
            String protocolType, EndpointReference epr) {
        org.apache.axis2.soap.SOAPEnvelope env = super.createEnvelope();
        org.apache.axis2.soap.SOAPFactory factory = OMAbstractFactory
                .getSOAP12Factory();
        OMNamespace wsCoor = factory.createOMNamespace(Constants.WS_COOR,
                "wscoor");
        OMElement request = factory.createOMElement("Register", wsCoor);
        OMElement protocolTypeElement = factory.createOMElement(
                "ProtocolIdentifier", wsCoor);
        protocolTypeElement.setText(protocolType);
        request.addChild(protocolTypeElement);

        OMElement protocolService = factory.createOMElement(
                "ParticipantProtocolService", wsCoor);
        KandulaUtils.endpointToOM(epr, protocolService, factory);
        request.addChild(protocolService);
        env.getBody().addChild(request);
        return env;
    }

    private EndpointReference setupListener() throws IOException {
        QName serviceName = new QName("RegistrationRequesterPortType");
        QName operationName = new QName(Constants.WS_COOR,
                "registerResponseOperation");
        org.apache.axis2.description.OperationDescription responseOperationDesc;
        String className = RegistrationRequesterPortTypeRawXMLSkeleton.class
                .getName();
        String mapping = Constants.WS_COOR_REGISTER_RESPONSE;

        KandulaListener listener = KandulaListener.getInstance();
        ServiceDescription service = new ServiceDescription(serviceName);
        service.addParameter(new ParameterImpl(
                AbstractMessageReceiver.SERVICE_CLASS, className));
        service.setFileName(className);

        responseOperationDesc = new InOnlyOperationDescription();
        responseOperationDesc.setName(operationName);
        responseOperationDesc
                .setMessageReceiver(new RawXMLINOnlyMessageReceiver());

        // Adding the WSA Action mapping to the operation
        service.addMapping(mapping, responseOperationDesc);
        service.addOperation(responseOperationDesc);
        listener.addService(service);
        listener.start();
        return new EndpointReference(listener.getHost()
                + serviceName.getLocalPart());
    }
}