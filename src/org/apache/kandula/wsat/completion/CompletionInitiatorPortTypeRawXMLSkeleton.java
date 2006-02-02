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

import org.apache.axis2.context.MessageContext;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.storage.StorageFactory;
import org.apache.ws.commons.om.OMElement;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */

public class CompletionInitiatorPortTypeRawXMLSkeleton {
	private MessageContext msgContext;

	public void init(MessageContext context) {
		this.msgContext = context;
	}

	public OMElement committedOperation(OMElement requestElement) {
		StorageFactory.getInstance().setConfigurationContext(
				msgContext.getServiceContext().getConfigurationContext());
		OMElement header = msgContext.getEnvelope().getHeader();
		String requesterID = header.getFirstChildWithName(
				Constants.REQUESTER_ID_PARAMETER).getText();
		AbstractContext context = (AbstractContext) StorageFactory
				.getInstance().getInitiatorStore().get(requesterID);
		context.setStatus(Status.ParticipantStatus.STATUS_COMMITED);
		return null;
	}

	public OMElement abortedOperation(OMElement requestElement) {
		StorageFactory.getInstance().setConfigurationContext(
				msgContext.getServiceContext().getConfigurationContext());
		OMElement header = msgContext.getEnvelope().getHeader();
		String requesterID = header.getFirstChildWithName(
				Constants.REQUESTER_ID_PARAMETER).getText();
		AbstractContext context = (AbstractContext) StorageFactory
				.getInstance().getInitiatorStore().get(requesterID);
		context.setStatus(Status.ParticipantStatus.STATUS_ABORTED);
		return null;
	}
}