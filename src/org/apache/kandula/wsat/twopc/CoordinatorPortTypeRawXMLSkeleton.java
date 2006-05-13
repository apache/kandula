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

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.kandula.Constants;
import org.apache.kandula.coordinator.at.ATCoordinator;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.participant.Vote;
import org.apache.kandula.storage.StorageFactory;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class CoordinatorPortTypeRawXMLSkeleton {
	private OperationContext opContext;

	public void setOperationContext(OperationContext opContext) {
		this.opContext = opContext;
	}

	/**
	 * @param requestElement
	 * @throws AbstractKandulaException
	 */
	public OMElement preparedOperation(OMElement requestElement)
			throws AxisFault {
		StorageFactory.getInstance().setConfigurationContext(
				opContext.getServiceContext().getConfigurationContext());
		OMElement header = opContext.getMessageContext(
				WSDLConstants.MESSAGE_LABEL_IN_VALUE).getEnvelope().getHeader();
		String activityId = header.getFirstChildWithName(
				Constants.TRANSACTION_ID_PARAMETER).getText();
		String enlistmentId = header.getFirstChildWithName(
				Constants.ENLISTMENT_ID_PARAMETER).getText();
		ATCoordinator coordinator = new ATCoordinator();
		try {
			coordinator.countVote(activityId, Vote.PREPARED, enlistmentId);
		} catch (AbstractKandulaException e) {
			AxisFault fault = new AxisFault(e);
			fault.setFaultCode(e.getFaultCode());
			throw fault;
		}
		return null;
	}

	/**
	 * @param requestElement
	 * @throws AbstractKandulaException
	 */
	public OMElement abortedOperation(OMElement requestElement)
			throws AxisFault {
		StorageFactory.getInstance().setConfigurationContext(
				opContext.getServiceContext().getConfigurationContext());
		OMElement header = opContext.getMessageContext(
				WSDLConstants.MESSAGE_LABEL_IN_VALUE).getEnvelope().getHeader();
		String activityId = header.getFirstChildWithName(
				Constants.TRANSACTION_ID_PARAMETER).getText();
		String enlistmentId = header.getFirstChildWithName(
				Constants.ENLISTMENT_ID_PARAMETER).getText();
		ATCoordinator coordinator = new ATCoordinator();
		try {
			coordinator.abortedOperation(activityId, enlistmentId);
		} catch (AbstractKandulaException e) {
			AxisFault fault = new AxisFault(e);
			fault.setFaultCode(e.getFaultCode());
			throw fault;
		}
		return null;
	}

	/**
	 * @param requestElement
	 * @throws AbstractKandulaException
	 */
	public OMElement readOnlyOperation(OMElement requestElement)
			throws AxisFault {
		StorageFactory.getInstance().setConfigurationContext(
				opContext.getServiceContext().getConfigurationContext());
		OMElement header = opContext.getMessageContext(
				WSDLConstants.MESSAGE_LABEL_IN_VALUE).getEnvelope().getHeader();
		String activityId = header.getFirstChildWithName(
				Constants.TRANSACTION_ID_PARAMETER).getText();
		String enlistmentId = header.getFirstChildWithName(
				Constants.ENLISTMENT_ID_PARAMETER).getText();
		ATCoordinator coordinator = new ATCoordinator();
		try {
			coordinator.countVote(activityId, Vote.READ_ONLY, enlistmentId);
		} catch (AbstractKandulaException e) {
			AxisFault fault = new AxisFault(e);
			fault.setFaultCode(e.getFaultCode());
			throw fault;
		}
		return null;
	}

	/**
	 * @param requestElement
	 * @throws AbstractKandulaException
	 */
	public OMElement committedOperation(OMElement requestElement)
			throws AxisFault {
		StorageFactory.getInstance().setConfigurationContext(
				opContext.getServiceContext().getConfigurationContext());
		OMElement header = opContext.getMessageContext(
				WSDLConstants.MESSAGE_LABEL_IN_VALUE).getEnvelope().getHeader();
		String activityId = header.getFirstChildWithName(
				Constants.TRANSACTION_ID_PARAMETER).getText();
		String enlistmentId = header.getFirstChildWithName(
				Constants.ENLISTMENT_ID_PARAMETER).getText();
		ATCoordinator coordinator = new ATCoordinator();
		try {
			coordinator.countParticipantOutcome(activityId, enlistmentId);
		} catch (AbstractKandulaException e) {
			AxisFault fault = new AxisFault(e);
			fault.setFaultCode(e.getFaultCode());
			throw fault;
		}
		return null;
	}

	/**
	 * @param requestElement
	 * @throws AbstractKandulaException
	 */
	public OMElement replayOperation(OMElement requestElement) throws AxisFault {
		StorageFactory.getInstance().setConfigurationContext(
				opContext.getServiceContext().getConfigurationContext());
		System.out.println("Visited Replay operation");
		return null;
	}

}