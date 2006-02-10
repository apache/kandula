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
package org.apache.kandula.wsat.twopc;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.participant.ParticipantTransactionCoordinator;
import org.apache.kandula.storage.StorageFactory;
import org.apache.kandula.storage.Store;
import org.apache.ws.commons.om.OMElement;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */

public class ParticipantPortTypeRawXMLSkeleton {
	private MessageContext msgContext;

	public void init(MessageContext context) {
		this.msgContext = context;
	}

	public OMElement prepareOperation(OMElement requestEle) throws AxisFault {
		StorageFactory.getInstance().setConfigurationContext(
				msgContext.getServiceContext().getConfigurationContext());
		OMElement header = msgContext.getEnvelope().getHeader();
		String requesterID = header.getFirstChildWithName(
				Constants.REQUESTER_ID_PARAMETER).getText();
		Store store = StorageFactory.getInstance().getStore();
		AbstractContext context = (AbstractContext) store.get(requesterID);
		ParticipantTransactionCoordinator txManager = new ParticipantTransactionCoordinator();
		try {
			txManager.prepare(context);
		} catch (AbstractKandulaException e) {
			AxisFault fault = new AxisFault(e);
			fault.setFaultCode(e.getFaultCode());
			throw fault;
		}
		return null;

	}

	public OMElement commitOperation(OMElement requestEle) throws AxisFault {
		StorageFactory.getInstance().setConfigurationContext(
				msgContext.getServiceContext().getConfigurationContext());
		OMElement header = msgContext.getEnvelope().getHeader();
		String requesterID = header.getFirstChildWithName(
				Constants.REQUESTER_ID_PARAMETER).getText();
		Store store = StorageFactory.getInstance().getStore();
		AbstractContext context = (AbstractContext) store.get(requesterID);
		ParticipantTransactionCoordinator txManager = new ParticipantTransactionCoordinator();
		try {
			txManager.commit(context);
		} catch (AbstractKandulaException e) {
			AxisFault fault = new AxisFault(e);
			fault.setFaultCode(e.getFaultCode());
			throw fault;
		}
		return null;
	}

	public OMElement rollbackOperation(OMElement requestEle) throws AxisFault {
		StorageFactory.getInstance().setConfigurationContext(
				msgContext.getServiceContext().getConfigurationContext());
		OMElement header = msgContext.getEnvelope().getHeader();
		String requesterID = header.getFirstChildWithName(
				Constants.REQUESTER_ID_PARAMETER).getText();
		Store store = StorageFactory.getInstance().getStore();
		AbstractContext context = (AbstractContext) store.get(requesterID);
		ParticipantTransactionCoordinator txManager = new ParticipantTransactionCoordinator();
		try {
			txManager.rollback(context);
		} catch (AbstractKandulaException e) {
			AxisFault fault = new AxisFault(e);
			fault.setFaultCode(e.getFaultCode());
			throw fault;
		}
		return null;
	}
}