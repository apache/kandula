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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.kandula.Constants;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.storage.StorageFactory;
import org.apache.kandula.utility.EndpointReferenceFactory;

/**
 * @author <a href="mailto:thilina@apache.org"> Thilina Gunarathne </a>
 */

public class RegistrationPortTypeRawXMLSkeleton {
	private OperationContext opContext;

	public void setOperationContext(OperationContext opContext) {
		this.opContext = opContext;
	}

	public OMElement registerOperation(OMElement request) throws AxisFault {

		String protocolIdentifier;
		EndpointReference participantEPR;
		String activityId;
		StorageFactory.getInstance().setConfigurationContext(
				opContext.getServiceContext().getConfigurationContext());
		/*
		 * Extracting data from the received message
		 */
		protocolIdentifier = request.getFirstChildWithName(
				new QName("ProtocolIdentifier")).getText();
		OMElement participantEPRElement = request
				.getFirstChildWithName(new QName("ParticipantProtocolService"));
		// Extracting the participant EPR
		participantEPR = EndpointReferenceFactory
				.endpointFromOM(participantEPRElement);

		OMElement header = opContext.getMessageContext(
				WSDLConstants.MESSAGE_LABEL_IN_VALUE).getEnvelope().getHeader();
		activityId = header.getFirstChildWithName(
				Constants.TRANSACTION_ID_PARAMETER).getText();
		/*
		 * Registering the participant for the activity for the given protocol
		 */
		try {
			Coordinator coordinator = new Coordinator();
			EndpointReference epr = coordinator.registerParticipant(activityId,
					protocolIdentifier, participantEPR);
			SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
			OMNamespace wsCoor = factory.createOMNamespace(Constants.WS_COOR,
					"wscoor");
			OMElement responseEle = factory.createOMElement("RegisterResponse",
					wsCoor);
			responseEle.addChild(toOM(epr));
			return responseEle;
		} catch (AbstractKandulaException e) {
			AxisFault fault = new AxisFault(e);
			fault.setFaultCode(e.getFaultCode());
			throw fault;
		}
	}

	/**
	 * Serializes an EndpointRefrence to OM Nodes
	 */
	private OMElement toOM(EndpointReference epr) {
		SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
		OMNamespace wsCoor = factory.createOMNamespace(
				org.apache.kandula.Constants.WS_COOR, "wscoor");
		OMElement coordinatorProtocolService = factory.createOMElement(
				"CoordinatorProtocolService", wsCoor);
		EndpointReferenceFactory.endpointToOM(epr, coordinatorProtocolService,
				factory);
		return coordinatorProtocolService;
	}
}