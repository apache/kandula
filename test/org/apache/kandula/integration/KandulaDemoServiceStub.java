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
package org.apache.kandula.integration;

import java.io.IOException;

import org.apache.axiom.om.OMAbstractFactory;
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
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.OutOnlyAxisOperation;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */

public class KandulaDemoServiceStub extends org.apache.axis2.client.Stub {

	public static final String AXIS2_HOME = ".";

	// private AxisService service;
	private ConfigurationContext configurationContext;

	private ServiceContext serviceContext;

	private EndpointReference toEPR;

	private static org.apache.axis2.description.AxisOperation[] operations;

	static {

		// creating the Service
		_service = new AxisService("KandulaDemoService");

		// creating the operations
		AxisOperation operationDesc;
		operations = new org.apache.axis2.description.AxisOperation[1];

		operationDesc = new OutOnlyAxisOperation();
		operationDesc.setName(new javax.xml.namespace.QName("creditOperation"));
		operations[0] = operationDesc;
		_service.addOperation(operationDesc);

	}

	/**
	 * Constructor
	 */
	public KandulaDemoServiceStub(String axis2Home,
			EndpointReference targetEndpoint) throws java.lang.Exception {
		this.toEPR = targetEndpoint;
		// creating the configuration
		configurationContext = ConfigurationContextFactory
				.createConfigurationContextFromFileSystem(axis2Home, axis2Home
						+ "/axis2.xml");
		configurationContext.getAxisConfiguration().addService(_service);
		ServiceGroupContext sgc = new ServiceGroupContext(
				this.configurationContext, (AxisServiceGroup) _service
						.getParent());
		this.serviceContext = new ServiceContext(_service, sgc);

	}

	public void creditOperation() throws IOException, AxisFault{

		Options options = new Options();
		MessageContext messageContext = new MessageContext();
		messageContext.setProperty(AddressingConstants.WS_ADDRESSING_VERSION,
				AddressingConstants.Submission.WSA_NAMESPACE);
		SOAPEnvelope env = createSOAPEnvelope();
		messageContext.setEnvelope(env);

		// _service.engageModule("addressing");

		options.setAction("creditOperation");
		options.setTo(this.toEPR);

		// messageSender
		// .setSenderTransport(org.apache.axis2.Constants.TRANSPORT_HTTP);
		OperationClient client = operations[0].createClient(serviceContext,
				options);
		client.addMessageContext(messageContext);
		client.execute(true);

	}

	private SOAPEnvelope createSOAPEnvelope() {

		SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
		SOAPEnvelope env = factory.getDefaultEnvelope();
		return env;
	}

}