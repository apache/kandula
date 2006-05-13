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
import org.apache.axis2.context.OperationContext;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.storage.StorageFactory;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */

public class ActivationPortTypeRawXMLSkeleton {
	private OperationContext opContext;

	public void setOperationContext(OperationContext opContext) {
		this.opContext = opContext;
	}

	/**
	 * @param requestElement
	 * @throws AbstractKandulaException
	 */
	public OMElement createCoordinationContextOperation(OMElement requestElement)
			throws AxisFault {
		AbstractContext context;
		StorageFactory.getInstance().setConfigurationContext(
				opContext.getServiceContext().getConfigurationContext());
		/*
		 * Extracting data from the incoming message
		 */
		String coordinationType = requestElement.getFirstChildWithName(
				new QName("CoordinationType")).getText();
		OMElement expiresElement = requestElement
				.getFirstChildWithName(new QName("Expires"));
		String expires = null;
		long expiresL = 0;
		if (expiresElement != null) {
			expires = expiresElement.getText();
			if ((expires != null) && (expires.equals(""))) {
				expiresL = Long.parseLong(expires);
			}
		}

		/*
		 * Creating the Coordination Context
		 */
		try {
			Coordinator coordinator = new Coordinator();
			context = coordinator.createCoordinationContext(coordinationType,
					expiresL);
			SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
			OMNamespace wsCoor = factory.createOMNamespace(Constants.WS_COOR,
					"wscoor");
			OMElement responseEle = factory.createOMElement(
					"CreateCoordinationContextResponse", wsCoor);
			responseEle.addChild(context.getCoordinationContext().toOM());
			return responseEle;
		} catch (AbstractKandulaException e) {
			AxisFault fault = new AxisFault(e);
			fault.setFaultCode(e.getFaultCode());
			throw fault;
		}

	}

}