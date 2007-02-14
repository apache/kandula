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
package org.apache.kandula.wsba;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.impl.BAActivityContext;
import org.apache.kandula.coordinator.ba.BACoordinator;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.NoActivityException;
import org.apache.kandula.storage.StorageUtils;

/**
 * BACoordinatorCompletionCoordinatorServiceSkeleton java skeleton for the
 * axisService
 */
public class BACoordinatorCompletionCoordinatorServiceSkeleton {

	private static final Log log = LogFactory
			.getLog(BACoordinatorCompletionCoordinatorServiceSkeleton.class);

	public void CompensatedOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Compensated param0)
			throws AxisFault {
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		String activityId = header.getFirstChildWithName(Constants.TRANSACTION_ID_PARAMETER)
				.getText();
		String enlistmentId = header.getFirstChildWithName(Constants.ENLISTMENT_ID_PARAMETER)
				.getText();
		try {
			AbstractContext context = StorageUtils.getContext(activityId);
			if (context != null) {
				BAActivityContext baContext = (BAActivityContext) context;
				BACoordinator atomicBACoordinator = new BACoordinator();
				atomicBACoordinator.compensatedOperation(baContext, enlistmentId);
			} else {
				log.fatal("WS_BA : message "
						+ MessageContext.getCurrentMessageContext().getMessageID()
						+ " : CompensatedOperation : Business Activity Not Found");
				throw new NoActivityException("CompensatedOperation : Business Activity Not Found");
			}
		} catch (AbstractKandulaException e) {
			log.fatal("WS_BA : message " + MessageContext.getCurrentMessageContext().getMessageID()
					+ " : CompensatedOperation :" + e);
			throw new AxisFault(e);
		}
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param1
	 * @throws AxisFault
	 * 
	 */
	public void ClosedOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Closed param1)
			throws AxisFault {
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		String activityId = header.getFirstChildWithName(Constants.TRANSACTION_ID_PARAMETER)
				.getText();
		String enlistmentId = header.getFirstChildWithName(Constants.ENLISTMENT_ID_PARAMETER)
				.getText();
		try {
			AbstractContext context = StorageUtils.getContext(activityId);
			if (context != null) {
				BAActivityContext baContext = (BAActivityContext) context;
				BACoordinator atomicBACoordinator = new BACoordinator();
				atomicBACoordinator.closedOperation(baContext, enlistmentId);
			} else {
				log.fatal("WS_BA : message "
						+ MessageContext.getCurrentMessageContext().getMessageID()
						+ " : ClosedOperation : Business Activity Not Found");
				throw new NoActivityException("ClosedOperation : Business Activity Not Found");
			}
		} catch (AbstractKandulaException e) {
			log.fatal("WS_BA : message " + MessageContext.getCurrentMessageContext().getMessageID()
					+ " : ClosedOperation :" + e);
			throw new AxisFault(e);
		}
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param2
	 * @throws AxisFault
	 * 
	 */
	public void ExitOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Exit param2)
			throws AxisFault

	{
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		String activityId = header.getFirstChildWithName(Constants.TRANSACTION_ID_PARAMETER)
				.getText();
		String enlistmentId = header.getFirstChildWithName(Constants.ENLISTMENT_ID_PARAMETER)
				.getText();
		try {
			AbstractContext context = StorageUtils.getContext(activityId);
			if (context != null) {
				BAActivityContext baContext = (BAActivityContext) context;
				BACoordinator atomicBACoordinator = new BACoordinator();
				atomicBACoordinator.exitOperation(baContext, enlistmentId);
			} else {
				log.fatal("WS_BA : message "
						+ MessageContext.getCurrentMessageContext().getMessageID()
						+ " : ExitOperation : Business Activity Not Found");
				throw new NoActivityException("ClosedOperation : Business Activity Not Found");
			}
		} catch (AbstractKandulaException e) {
			log.fatal("WS_BA : message " + MessageContext.getCurrentMessageContext().getMessageID()
					+ " : ExitOperation :" + e);
			throw new AxisFault(e);
		}
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param3
	 * @throws AxisFault
	 * 
	 */
	public void CompletedOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Completed param3)
			throws AxisFault {
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		String activityId = header.getFirstChildWithName(Constants.TRANSACTION_ID_PARAMETER)
				.getText();
		String enlistmentId = header.getFirstChildWithName(Constants.ENLISTMENT_ID_PARAMETER)
				.getText();
		try {
			AbstractContext context = StorageUtils.getContext(activityId);
			if (context != null) {
				BAActivityContext baContext = (BAActivityContext) context;
				BACoordinator atomicBACoordinator = new BACoordinator();
				atomicBACoordinator.completedOperation(baContext, enlistmentId);
			} else {
				log.fatal("WS_BA : message "
						+ MessageContext.getCurrentMessageContext().getMessageID()
						+ " : CompletedOperation : Business Activity Not Found");
				throw new NoActivityException("ClosedOperation : Business Activity Not Found");
			}
		} catch (AbstractKandulaException e) {
			log.fatal("WS_BA : message " + MessageContext.getCurrentMessageContext().getMessageID()
					+ " : CompletedOperation :" + e);
			throw new AxisFault(e);
		}
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param4
	 * 
	 */
	public void CannotComplete(org.oasis_open.docs.ws_tx.wsba._2006._06.CannotComplete param4)

	{
		// Todo fill this with the necessary business logic

	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param5
	 * 
	 */
	public void GetStatusOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.GetStatus param5)

	{
		// Todo fill this with the necessary business logic

	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param6
	 * @throws AxisFault
	 * 
	 */
	public void FailOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Fail param6)
			throws AxisFault

	{
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		String activityId = header.getFirstChildWithName(Constants.TRANSACTION_ID_PARAMETER)
				.getText();
		String enlistmentId = header.getFirstChildWithName(Constants.ENLISTMENT_ID_PARAMETER)
				.getText();
		try {
			AbstractContext context = StorageUtils.getContext(activityId);
			if (context != null) {
				BAActivityContext baContext = (BAActivityContext) context;
				BACoordinator atomicBACoordinator = new BACoordinator();
				atomicBACoordinator.faultOperation(baContext, enlistmentId);
			} else {
				log.fatal("WS_BA : message "
						+ MessageContext.getCurrentMessageContext().getMessageID()
						+ " : FailOperation : Business Activity Not Found");
				throw new NoActivityException("ClosedOperation : Business Activity Not Found");
			}
		} catch (AbstractKandulaException e) {
			log.fatal("WS_BA : message " + MessageContext.getCurrentMessageContext().getMessageID()
					+ " : FailOperation :" + e);
			throw new AxisFault(e);
		}

	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param7
	 * 
	 */
	public void StatusOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Status param7)

	{
		// Todo fill this with the necessary business logic

	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param8
	 * @throws AxisFault
	 * 
	 */
	public void CanceledOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Canceled param8)
			throws AxisFault

	{
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		String activityId = header.getFirstChildWithName(Constants.TRANSACTION_ID_PARAMETER)
				.getText();
		String enlistmentId = header.getFirstChildWithName(Constants.ENLISTMENT_ID_PARAMETER)
				.getText();
		try {
			AbstractContext context = StorageUtils.getContext(activityId);
			if (context != null) {
				BAActivityContext baContext = (BAActivityContext) context;
				BACoordinator atomicBACoordinator = new BACoordinator();
				atomicBACoordinator.canceledOperation(baContext, enlistmentId);
			}
		} catch (AbstractKandulaException e) {
			log.fatal("WS_BA : message " + MessageContext.getCurrentMessageContext().getMessageID()
					+ " : CanceledOperation :" + e);
			throw new AxisFault(e);
		}
	}

}
