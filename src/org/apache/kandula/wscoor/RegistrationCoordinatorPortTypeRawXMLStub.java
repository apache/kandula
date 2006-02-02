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

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.context.ServiceGroupContext;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.InOnlyAxisOperation;
import org.apache.axis2.description.OutOnlyAxisOperation;
import org.apache.axis2.description.ParameterImpl;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.apache.axis2.receivers.RawXMLINOnlyMessageReceiver;
import org.apache.kandula.Constants;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.KandulaGeneralException;
import org.apache.kandula.utility.EPRHandlingUtils;
import org.apache.kandula.utility.KandulaListener;
import org.apache.ws.commons.om.OMAbstractFactory;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.om.OMNamespace;
import org.apache.ws.commons.soap.SOAPEnvelope;
import org.apache.ws.commons.soap.SOAPFactory;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class RegistrationCoordinatorPortTypeRawXMLStub extends
		org.apache.axis2.client.Stub {
	public static final String AXIS2_HOME = ".";

	protected static org.apache.axis2.description.AxisOperation[] _operations;

	protected AxisService service;

	protected ConfigurationContext configurationContext;

	private ServiceContext serviceContext;

	private EndpointReference toEPR;

	private AxisOperation operation;

	/**
	 * Constructor
	 */
	public RegistrationCoordinatorPortTypeRawXMLStub(String axis2Home,
			String axis2Xml, EndpointReference targetEndpoint)
			throws AbstractKandulaException {
		this.toEPR = targetEndpoint;
		service = new AxisService("RegistrationCoordinatorPortType");
		try {
			configurationContext = new org.apache.axis2.context.ConfigurationContextFactory()
					.createConfigurationContextFromFileSystem(axis2Home,
							axis2Xml);
			configurationContext.getAxisConfiguration().addService(service);
		} catch (DeploymentException e) {
			throw new KandulaGeneralException(e);
		} catch (AxisFault e1) {
			throw new KandulaGeneralException(e1);
		}
		ServiceGroupContext sgc = new ServiceGroupContext(
				this.configurationContext, (AxisServiceGroup) this.service
						.getParent());
		this.serviceContext = new ServiceContext(service, sgc);

		operation = new OutOnlyAxisOperation();
		operation.setName(new javax.xml.namespace.QName(
				"http://schemas.xmlsoap.org/ws/2003/09/wscoor",
				"RegisterOperation"));
		service.addOperation(operation);

	}

	public void registerOperation(String protocolType, EndpointReference epr,
			String id) throws IOException {
		EndpointReference replyToEpr;
		MessageContext messageContext = new MessageContext();
		Options options = new Options();
		messageContext.setProperty(AddressingConstants.WS_ADDRESSING_VERSION,
				AddressingConstants.Submission.WSA_NAMESPACE);
		SOAPEnvelope env = createSOAPEnvelope(
				protocolType, epr);
		messageContext.setEnvelope(env);
		replyToEpr = setupListener();
		EPRHandlingUtils.addReferenceProperty(replyToEpr,
				Constants.REQUESTER_ID_PARAMETER, id);
		options.setReplyTo(replyToEpr);
		options.setTo(this.toEPR);
		options.setAction(Constants.WS_COOR_REGISTER);
		OperationClient client = operation
				.createClient(serviceContext, options);
		client.addMessageContext(messageContext);
		client.execute(false);

	}

	private SOAPEnvelope createSOAPEnvelope(
			String protocolType, EndpointReference epr) {

		SOAPFactory factory = OMAbstractFactory
				.getSOAP12Factory();
		SOAPEnvelope env = factory.getDefaultEnvelope();
		OMNamespace wsCoor = factory.createOMNamespace(Constants.WS_COOR,
				"wscoor");
		OMElement request = factory.createOMElement("Register", wsCoor);
		OMElement protocolTypeElement = factory.createOMElement(
				"ProtocolIdentifier", wsCoor);
		protocolTypeElement.setText(protocolType);
		request.addChild(protocolTypeElement);

		OMElement protocolService = factory.createOMElement(
				"ParticipantProtocolService", wsCoor);
		EPRHandlingUtils.endpointToOM(epr, protocolService, factory);
		request.addChild(protocolService);
		env.getBody().addChild(request);
		return env;
	}

	private EndpointReference setupListener() throws IOException {
		String serviceName = "RegistrationRequesterPortType";
		QName operationName = new QName(Constants.WS_COOR,
				"registerResponseOperation");
		AxisOperation responseOperationDesc;
		String className = RegistrationRequesterPortTypeRawXMLSkeleton.class
				.getName();
		String mapping = Constants.WS_COOR_REGISTER_RESPONSE;

		KandulaListener listener = KandulaListener.getInstance();
		AxisService service = new AxisService(serviceName);
		service.addParameter(new ParameterImpl(
				AbstractMessageReceiver.SERVICE_CLASS, className));
		service.setFileName(className);

		responseOperationDesc = new InOnlyAxisOperation();
		responseOperationDesc.setName(operationName);
		responseOperationDesc
				.setMessageReceiver(new RawXMLINOnlyMessageReceiver());

		// Adding the WSA Action mapping to the operation
		service.mapActionToOperation(mapping, responseOperationDesc);
		service.addOperation(responseOperationDesc);
		listener.addService(service);
		listener.start();
		return new EndpointReference(listener.getHost() + serviceName);
	}
}