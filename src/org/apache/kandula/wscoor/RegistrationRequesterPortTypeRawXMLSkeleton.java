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
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.kandula.Constants;
import org.apache.kandula.context.impl.ATParticipantContext;
import org.apache.kandula.initiator.InitiatorTransaction;
import org.apache.kandula.storage.StorageFactory;
import org.apache.kandula.utility.EndpointReferenceFactory;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */

public class RegistrationRequesterPortTypeRawXMLSkeleton {

	public OMElement registerResponseOperation(OMElement responseElement)
			throws AxisFault {

		OMElement response = responseElement.getFirstElement();
		if ("CoordinatorProtocolService".equals(response.getLocalName())) {
			OMElement header = MessageContext.getCurrentMessageContext().getEnvelope()
					.getHeader();
			String requesterID = header.getFirstChildWithName(
					Constants.REQUESTER_ID_PARAMETER).getText();
			EndpointReference coordinatorService = EndpointReferenceFactory
					.endpointFromOM(response.getFirstElement());
			InitiatorTransaction initiatorTransaction;
			initiatorTransaction = (InitiatorTransaction) StorageFactory
					.getInstance().getInitiatorStore().get(requesterID);
			if (initiatorTransaction == null) {
				ATParticipantContext context = (ATParticipantContext) StorageFactory
						.getInstance().getStore().get(requesterID);
				context.setCoordinationEPR(coordinatorService);
			} else {
				initiatorTransaction.setCoordinationEPR(coordinatorService);
			}
		}
		return null;
	}
}
