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
package org.apache.kandula.wsat.completion;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.initiator.InitiatorTransaction;
import org.apache.kandula.storage.StorageFactory;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */

public class CompletionInitiatorPortTypeRawXMLSkeleton {
	private OperationContext opContext;

	public void setOperationContext(OperationContext opContext) {
		this.opContext = opContext;
	}

	public void committedOperation(OMElement requestElement)
			throws AxisFault {
		StorageFactory.getInstance().setConfigurationContext(
				opContext.getServiceContext().getConfigurationContext());
		OMElement header = opContext.getMessageContext(
				WSDLConstants.MESSAGE_LABEL_IN_VALUE).getEnvelope().getHeader();
		String requesterID = header.getFirstChildWithName(
				Constants.REQUESTER_ID_PARAMETER).getText();
		InitiatorTransaction transaction = (InitiatorTransaction) StorageFactory
				.getInstance().getInitiatorStore().get(requesterID);
		transaction.setStatus(Status.CoordinatorStatus.STATUS_COMMITTING);
	}

	public void abortedOperation(OMElement requestElement)
			throws AxisFault {
		StorageFactory.getInstance().setConfigurationContext(
				opContext.getServiceContext().getConfigurationContext());
		OMElement header = opContext.getMessageContext(
				WSDLConstants.MESSAGE_LABEL_IN_VALUE).getEnvelope().getHeader();
		String requesterID = header.getFirstChildWithName(
				Constants.REQUESTER_ID_PARAMETER).getText();
		InitiatorTransaction transaction = (InitiatorTransaction) StorageFactory
				.getInstance().getInitiatorStore().get(requesterID);
		transaction.setStatus(Status.CoordinatorStatus.STATUS_ABORTING);
	}
}