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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.engine.DependencyManager;
import org.apache.kandula.Constants;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class RegistrationPortTypeRawXMLAsyncMessageReceiver extends
		org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver {

	public void invokeBusinessLogic(
			org.apache.axis2.context.MessageContext msgContext,
			org.apache.axis2.context.MessageContext newMsgContext)
			throws org.apache.axis2.AxisFault {

		try {

			// get the implementation class for the Web Service
			Object obj = getTheImplementationObject(msgContext);

			//Inject the Message Context if it is asked for
			DependencyManager.configureBusinessLogicProvider(obj, msgContext.getOperationContext());

			RegistrationPortTypeRawXMLSkeleton skel = (RegistrationPortTypeRawXMLSkeleton) obj;
			//Out Envelop
			SOAPEnvelope envelope = null;
			//Find the operation that has been set by the Dispatch phase.
			AxisOperation op = msgContext.getOperationContext()
					.getAxisOperation();

			String methodName;
			if (op.getName() != null
					& (methodName = op.getName().getLocalPart()) != null) {
				if (("registerOperation").equals(methodName)) {
					OMElement response = null;

					//doc style
					response = skel
							.registerOperation((OMElement) msgContext
									.getEnvelope().getBody().getFirstElement()
									.detach());

					//Create a default envelop
					envelope = getSOAPFactory(msgContext).getDefaultEnvelope();
					//Create a Omelement of the result if a result exist

					envelope.getBody().setFirstChild(response);
				}
				newMsgContext.setEnvelope(envelope);
				newMsgContext.setWSAAction(Constants.WS_COOR_REGISTER_RESPONSE);
				newMsgContext.setRelationships(null);
			}
		} catch (Exception e) {
			throw AxisFault.makeFault(e);
		}

	}

}