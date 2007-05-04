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
import org.apache.kandula.context.impl.ParticipantContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.NoActivityException;
import org.apache.kandula.participant.ba.BAParticipantTransactionCoordinator;
import org.apache.kandula.storage.StorageUtils;

/**
 * BACoordinatorCompletionParticipantServiceSkeleton java skeleton for the
 * axisService
 */
public class BACoordinatorCompletionParticipantServiceSkeleton {

	private static final Log log = LogFactory
			.getLog(BACoordinatorCompletionParticipantServiceSkeleton.class);

	public void CloseOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Close param1)
			throws AxisFault {
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		OMElement firstChildWithName = header
				.getFirstChildWithName(Constants.REQUESTER_ID_PARAMETER);
		if (firstChildWithName != null) {
			String requesterID = firstChildWithName.getText();
			AbstractContext context2 = StorageUtils.getContext(requesterID);
			try {
			if (context2!=null)
			{
			ParticipantContext context = (ParticipantContext) context2;
			BAParticipantTransactionCoordinator participantCoordinator = new BAParticipantTransactionCoordinator();
				participantCoordinator.close(context);
			}else {
				log.fatal("WS_BA : message "+ MessageContext.getCurrentMessageContext().getMessageID()
						+ " : CompensatedOperation : Business Activity Not Found");
				throw new NoActivityException("CompensateOperation : Business Activity Not Found");
			}
			} catch (AbstractKandulaException e) {
				log.fatal("WS_BA : Message ID : " + MessageContext.getCurrentMessageContext().getMessageID()
						+ " : close Operation :" + e);
				throw AxisFault.makeFault(e);
			}
		} else {
			AxisFault e = new AxisFault(
					"Requester ID header is not found in the message. Please check whether ws-addressing is enabled in the coordinator.");
			log.fatal("WS_BA : Message ID "
					+ MessageContext.getCurrentMessageContext().getMessageID()
					+ " : CloseOperation :" + e);
			throw e;
		}
	}

	public void CompleteOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Complete param4)
			throws AxisFault {
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		OMElement firstChildWithName = header
				.getFirstChildWithName(Constants.REQUESTER_ID_PARAMETER);
		if (firstChildWithName != null) {
			String requesterID = firstChildWithName.getText();
			AbstractContext context2 = StorageUtils.getContext(requesterID);
			if (context2 != null) {
				ParticipantContext context = (ParticipantContext) context2;
				BAParticipantTransactionCoordinator participantCoordinator = new BAParticipantTransactionCoordinator();
				try {
					participantCoordinator.complete(context);
				} catch (AbstractKandulaException e) {
					log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
							+ " : Complete Operation :" + e);
					throw AxisFault.makeFault(e);
				}
			} else {
				log.fatal("WS_BA : Message ID "
						+ MessageContext.getCurrentMessageContext().getMessageID()
						+ " : CompleteOperation : Participant Context Not Found.");
				throw new AxisFault("CompleteOperation : Participant Context Not Found.");
			}
		} else {
			AxisFault e = new AxisFault(
					"Requester ID header is not found in the message. Please check whether ws-addressing is enabled in the coordinator.");
			log.fatal("WS_BA : Message ID "
					+ MessageContext.getCurrentMessageContext().getMessageID()
					+ " : Complete Operation :" + e);
			throw e;
		}
	}

	public void CancelOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Cancel param3)
			throws AxisFault {
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		OMElement firstChildWithName = header
				.getFirstChildWithName(Constants.REQUESTER_ID_PARAMETER);
		if (firstChildWithName != null) {
			String requesterID = firstChildWithName.getText();
			AbstractContext context2 = StorageUtils.getContext(requesterID);
			if (context2 != null) {
				ParticipantContext context = (ParticipantContext) context2;
				BAParticipantTransactionCoordinator participantCoordinator = new BAParticipantTransactionCoordinator();
				try {
					participantCoordinator.cancel(context);
				} catch (AbstractKandulaException e) {
					log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
							+ " : CancelOperation :" + e);
					throw AxisFault.makeFault(e);
				}
			} else
			// participant context migt have been already removed by an earlier
			// cancel
			{
				log.info("WS_BA : Message ID "
						+ MessageContext.getCurrentMessageContext().getMessageID()
						+ " : CancelOperation : Participant Context Not Found.");
			}
		} else {
			AxisFault e = new AxisFault(
					"Requester ID header is not found in the message. Please check whether ws-addressing is enabled in the coordinator.");
			log.fatal("WS_BA : Message ID "
					+ MessageContext.getCurrentMessageContext().getMessageID()
					+ " : CancelOperation :" + e);
			throw e;
		}
	}

	public void ExitedOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Exited param8)
			throws AxisFault
	{
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		OMElement firstChildWithName = header
				.getFirstChildWithName(Constants.REQUESTER_ID_PARAMETER);
		if (firstChildWithName != null) {
			String requesterID = firstChildWithName.getText();
			AbstractContext context2 = StorageUtils.getContext(requesterID);
			if (context2 != null) {
				ParticipantContext context = (ParticipantContext) context2;
				BAParticipantTransactionCoordinator participantCoordinator = new BAParticipantTransactionCoordinator();
				try {
					participantCoordinator.exited(context);
				} catch (AbstractKandulaException e) {
					log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
							+ " : ExitedOperation :" + e);
					throw AxisFault.makeFault(e);
				}
			} else
			// participant context migt have been already removed by an earlier
			// exited
			{
				log.info("WS_BA : Message ID "
						+ MessageContext.getCurrentMessageContext().getMessageID()
						+ " : ExitedOperation : Participant Context Not Found.");
			}
		} else {
			AxisFault e = new AxisFault(
					"Requester ID header is not found in the message. Please check whether ws-addressing is enabled in the coordinator.");
			log.fatal("WS_BA : Message ID "
					+ MessageContext.getCurrentMessageContext().getMessageID()
					+ " : ExitedOperation :" + e);
			throw e;
		}
	}
	
	public void CompensateOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Compensate param2) throws AxisFault

	{
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		OMElement firstChildWithName = header
				.getFirstChildWithName(Constants.REQUESTER_ID_PARAMETER);
		if (firstChildWithName != null) {
			String requesterID = firstChildWithName.getText();
			AbstractContext context2 = StorageUtils.getContext(requesterID);
			try {
				if (context2 != null) {
					ParticipantContext context = (ParticipantContext) context2;
					BAParticipantTransactionCoordinator participantCoordinator = new BAParticipantTransactionCoordinator();
					participantCoordinator.compensate(context);
				} else {
					log.fatal("WS_BA : message "+ MessageContext.getCurrentMessageContext().getMessageID()
							+ " : CompensatedOperation : Business Activity Not Found");
					throw new NoActivityException("CompensateOperation : Business Activity Not Found");
				}
			} catch (AbstractKandulaException e) {
				log.fatal("WS_BA : Message ID : " + MessageContext.getCurrentMessageContext().getMessageID()
						+ " : CompensateOperation :" + e);
				throw AxisFault.makeFault(e);
			}
		} else {
			AxisFault e = new AxisFault(
					"Requester ID header is not found in the message. Please check whether ws-addressing is enabled in the coordinator.");
			log.fatal("WS_BA : Message ID "
					+ MessageContext.getCurrentMessageContext().getMessageID()
					+ " : CompensateOperation :" + e);
			throw e;
		}
	}

	public void FailedOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Failed param0) throws AxisFault

	{
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		OMElement firstChildWithName = header
				.getFirstChildWithName(Constants.REQUESTER_ID_PARAMETER);
		if (firstChildWithName != null) {
			String requesterID = firstChildWithName.getText();
			AbstractContext context2 = StorageUtils.getContext(requesterID);
			if (context2 != null) {
				ParticipantContext context = (ParticipantContext) context2;
				BAParticipantTransactionCoordinator participantCoordinator = new BAParticipantTransactionCoordinator();
				try {
					participantCoordinator.Faulted(context);
				} catch (AbstractKandulaException e) {
					log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
							+ " : FailedOperation :" + e);
					throw AxisFault.makeFault(e);
				}
			} else
			// participant context migt have been already removed by an earlier
			// exited
			{
				log.info("WS_BA : Message ID "
						+ MessageContext.getCurrentMessageContext().getMessageID()
						+ " : FailedOperation : Participant Context Not Found.");
			}
		} else {
			AxisFault e = new AxisFault(
					"Requester ID header is not found in the message. Please check whether ws-addressing is enabled in the coordinator.");
			log.fatal("WS_BA : Message ID "
					+ MessageContext.getCurrentMessageContext().getMessageID()
					+ " : FailedOperation :" + e);
			throw e;
		}
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param5
	 * 
	 */
	public void GetStatusOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.GetStatus param5) {
		// Todo fill this with the necessary business logic
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param6
	 * 
	 */
	public void NotCompleted(org.oasis_open.docs.ws_tx.wsba._2006._06.NotCompleted param6)

	{
		// Todo fill this with the necessary business logic

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
}
