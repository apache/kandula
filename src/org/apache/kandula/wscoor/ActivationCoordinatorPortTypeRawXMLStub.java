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

import java.io.File;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.context.ServiceGroupContext;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.InOnlyAxisOperation;
import org.apache.axis2.description.OutInAxisOperation;
import org.apache.axis2.description.OutOnlyAxisOperation;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.apache.axis2.receivers.RawXMLINOnlyMessageReceiver;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.CoordinationContext;
import org.apache.kandula.context.impl.ATActivityContext;
import org.apache.kandula.faults.KandulaGeneralException;
import org.apache.kandula.utility.EndpointReferenceFactory;
import org.apache.kandula.utility.KandulaListener;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */

public class ActivationCoordinatorPortTypeRawXMLStub extends
		org.apache.axis2.client.Stub {

	public static final String AXIS2_HOME = ".";

	protected AxisService service;

	protected ConfigurationContext configurationContext;

	protected ServiceContext serviceContext;

	protected EndpointReference toEPR;

	protected AxisOperation operation;

	/**
	 * Constructor
	 */
	public ActivationCoordinatorPortTypeRawXMLStub(String axis2Home,
			String axis2Xml, EndpointReference targetEndpoint)
			throws java.lang.Exception {
		this.toEPR = targetEndpoint;
		service = new AxisService("ActivationCoordinatorPortType");
		try {
			configurationContext = ConfigurationContextFactory
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
	}

	public void createCoordinationContextOperation(AbstractContext context, boolean async) throws IOException, KandulaGeneralException {

		EndpointReference replyToEpr;
		MessageContext messageContext = new MessageContext();
		Options options = new Options();
		messageContext.setProperty(AddressingConstants.WS_ADDRESSING_VERSION,
				AddressingConstants.Final.WSA_NAMESPACE);
		SOAPEnvelope env = createSOAPEnvelope(context.getCoordinationType());
		messageContext.setEnvelope(env);
		options.setTo(this.toEPR);
		options.setAction(Constants.WS_COOR_CREATE_COORDINATIONCONTEXT);

		if (async) {
			operation = new OutOnlyAxisOperation();
			operation.setName(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/ws/2003/09/wscoor","CreateCoordinationContextOperation"));
			service.addOperation(operation);
			replyToEpr = setupListener();
			EndpointReferenceFactory.addReferenceProperty(replyToEpr,
					Constants.REQUESTER_ID_PARAMETER, (String) context
							.getProperty(AbstractContext.REQUESTER_ID));
			options.setReplyTo(replyToEpr);
			OperationClient client = operation.createClient(serviceContext,
					options);
			client.addMessageContext(messageContext);
			client.execute(false);
		} else {
			operation = new OutInAxisOperation();
			operation.setName(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/ws/2003/09/wscoor","CreateCoordinationContextOperation"));
			service.addOperation(operation);
			OperationClient client = operation.createClient(serviceContext,
					options);
			client.addMessageContext(messageContext);
			client.execute(true);
			MessageContext msgContext = client
					.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			OMElement response = msgContext.getEnvelope().getBody()
					.getFirstChildWithName(new QName(Constants.WS_COOR,"CreateCoordinationContextResponse"));
			OMElement contextElement = response.getFirstChildWithName(new QName(Constants.WS_COOR,"CoordinationContext"));
			if (contextElement!=null) {
				CoordinationContext coordinationContext = CoordinationContext.Factory.newContext(contextElement);
				context.setCoordinationContext(coordinationContext);
			}
			else 
			{
				throw new KandulaGeneralException("CoordinationContext was not found in the CreareCoordinationContextResponse Message");
			}
		}

	}

	private SOAPEnvelope createSOAPEnvelope(String coordinationType) {
		SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
		SOAPEnvelope env = factory.getDefaultEnvelope();
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
		String serviceName = "ActivationRequesterPortType";
		QName operationName = new QName(Constants.WS_COOR,
				"createCoordinationContextResponseOperation");
		org.apache.axis2.description.AxisOperation responseOperationDesc;
		String className = ActivationRequesterPortTypeRawXMLSkeleton.class
				.getName();
		String mapping = Constants.WS_COOR_CREATE_COORDINATIONCONTEXT_RESPONSE;

		KandulaListener listener = KandulaListener.getInstance();
		AxisService service = new AxisService(serviceName);
		service.addParameter(new Parameter(
				AbstractMessageReceiver.SERVICE_CLASS, className));
		service.setFileName((new File(className)).toURL());

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