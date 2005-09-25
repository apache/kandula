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
package org.apache.kandula.wscoor;

import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.AnyContentType;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.clientapi.MessageSender;
import org.apache.axis2.description.ParameterImpl;
import org.apache.axis2.description.ServiceDescription;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMNamespace;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.apache.axis2.receivers.RawXMLINOnlyMessageReceiver;
import org.apache.kandula.Constants;
import org.apache.kandula.utility.KandulaListener;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */

public class ActivationCoordinatorPortTypeRawXMLStub extends
        org.apache.axis2.clientapi.Stub {

    public static final String AXIS2_HOME = ".";

    protected static org.apache.axis2.description.OperationDescription[] operations;

    static {

        //creating the Service
        _service = new org.apache.axis2.description.ServiceDescription(
                new javax.xml.namespace.QName(
                        "http://schemas.xmlsoap.org/ws/2003/09/wscoor",
                        "ActivationCoordinatorPortType"));

        //creating the operations
        org.apache.axis2.description.OperationDescription operationDesc;
        operations = new org.apache.axis2.description.OperationDescription[1];

        operationDesc = new org.apache.axis2.description.OperationDescription();
        operationDesc.setName(new javax.xml.namespace.QName(
                "http://schemas.xmlsoap.org/ws/2003/09/wscoor",
                "CreateCoordinationContextOperation"));
        operations[0] = operationDesc;
        _service.addOperation(operationDesc);

    }

    /**
     * Constructor
     */
    public ActivationCoordinatorPortTypeRawXMLStub(String axis2Home,
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

    public void createCoordinationContextOperation(String coordinationType,
            String id) throws IOException {

        EndpointReference replyToEpr;

        org.apache.axis2.context.MessageContext messageContext = getMessageContext();
        messageContext.setProperty(AddressingConstants.WS_ADDRESSING_VERSION,
                AddressingConstants.Submission.WSA_NAMESPACE);
        org.apache.axis2.soap.SOAPEnvelope env = createSOAPEnvelope(coordinationType);
        messageContext.setEnvelope(env);

        replyToEpr = setupListener();
        AnyContentType refProperties = new AnyContentType();
        refProperties.addReferenceValue(new QName(
                "http://ws.apache.org/kandula", "id"), id);
        replyToEpr.setReferenceParameters(refProperties);

        MessageSender messageSender = new MessageSender(_serviceContext);
        messageSender.setReplyTo(replyToEpr);
        messageSender.setTo(this.toEPR);
        messageSender
                .setWsaAction(Constants.WS_COOR_CREATE_COORDINATIONCONTEXT);
        messageSender
                .setSenderTransport(org.apache.axis2.Constants.TRANSPORT_HTTP);
        messageSender.send(operations[0], messageContext);

    }

    private org.apache.axis2.soap.SOAPEnvelope createSOAPEnvelope(
            String coordinationType) {
        org.apache.axis2.soap.SOAPEnvelope env = super.createEnvelope();
        org.apache.axis2.soap.SOAPFactory factory = OMAbstractFactory
                .getSOAP12Factory();
        OMNamespace wsCoor = factory.createOMNamespace(Constants.WS_COOR,
                "wscoor");
        OMElement request = factory.createOMElement(
                "CreateCoordinationContext", wsCoor);
        OMElement coorType = factory
                .createOMElement("CoordinationType", wsCoor);
        coorType.setText(coordinationType);
        request.addChild(coorType);
        env.getBody().addChild(request);
        return env;
    }

    private EndpointReference setupListener() throws IOException {
        QName serviceName = new QName("ActivationRequesterPortType");
        QName operationName = new QName(Constants.WS_COOR,
                "createCoordinationContextResponseOperation");
        org.apache.axis2.description.OperationDescription responseOperationDesc;
        String className = ActivationRequesterPortTypeRawXMLSkeleton.class
                .getName();
        String mapping = Constants.WS_COOR_CREATE_COORDINATIONCONTEXT_RESPONSE;

        KandulaListener listener = KandulaListener.getInstance();
        ServiceDescription service = new ServiceDescription(serviceName);
        service.addParameter(new ParameterImpl(
                AbstractMessageReceiver.SERVICE_CLASS, className));
        service.setFileName(className);

        responseOperationDesc = new org.apache.axis2.description.OperationDescription();
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