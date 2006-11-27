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
import org.apache.axis2.context.MessageContext;
import org.apache.kandula.Constants;
import org.apache.kandula.coordinator.at.ATCoordinator;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.storage.StorageFactory;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */

public class CompletionCoordinatorPortTypeRawXMLSkeleton {
	/**
	 * @param requestElement
	 * @throws AxisFault
	 */
	public void commitOperation(OMElement requestElement) throws AxisFault {
		String activityId;
		// log.info("Visited Commit operation");
		StorageFactory.getInstance().setConfigurationContext(
				MessageContext.getCurrentMessageContext().getServiceContext().getConfigurationContext());
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		activityId = header.getFirstChildWithName(
				Constants.TRANSACTION_ID_PARAMETER).getText();
		try {
			ATCoordinator coordinator = new ATCoordinator();
			coordinator.commitOperation(activityId);
		} catch (AbstractKandulaException e) {
			e.printStackTrace();
			AxisFault fault = new AxisFault(e);
			fault.setFaultCode(e.getFaultCode());
			throw fault;
		}
	}

	public void rollbackOperation(OMElement requestElement)
			throws AxisFault {

		String activityId;
		StorageFactory.getInstance().setConfigurationContext(
				MessageContext.getCurrentMessageContext().getServiceContext().getConfigurationContext());
		// log.info("Visited rollback operation");
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		activityId = header.getFirstChildWithName(
				Constants.TRANSACTION_ID_PARAMETER).getText();
		try {
			ATCoordinator coordinator = new ATCoordinator();
			coordinator.rollbackOperation(activityId);
		} catch (AbstractKandulaException e) {
			AxisFault fault = new AxisFault(e);
			fault.setFaultCode(e.getFaultCode());
			throw fault;
		}
	}

}