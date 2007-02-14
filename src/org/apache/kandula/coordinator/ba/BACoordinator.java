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
package org.apache.kandula.coordinator.ba;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.Status.BACoordinatorStatus;
import org.apache.kandula.ba.BusinessActivityCallBack;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.impl.BAActivityContext;
import org.apache.kandula.coordinator.Registerable;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.InvalidStateException;
import org.apache.kandula.faults.KandulaGeneralException;
import org.apache.kandula.storage.StorageUtils;
import org.apache.kandula.utility.KandulaConfiguration;
import org.apache.kandula.wsba.BACoordinatorCompletionParticipantServiceStub;
import org.oasis_open.docs.ws_tx.wsba._2006._06.Cancel;
import org.oasis_open.docs.ws_tx.wsba._2006._06.Close;
import org.oasis_open.docs.ws_tx.wsba._2006._06.Compensate;
import org.oasis_open.docs.ws_tx.wsba._2006._06.Complete;
import org.oasis_open.docs.ws_tx.wsba._2006._06.Exited;
import org.oasis_open.docs.ws_tx.wsba._2006._06.Failed;
import org.oasis_open.docs.ws_tx.wsba._2006._06.NotificationType;

public class BACoordinator implements Registerable {

	private static final Log log = LogFactory.getLog(BACoordinator.class);

	public EndpointReference register(AbstractContext context, String protocol,
			EndpointReference participantEPR, String registrationID)
			throws AbstractKandulaException {
		BAActivityContext baContext = (BAActivityContext) context;
		baContext.lock();
		int coordinatorStatus = baContext.getStatus();
		if (coordinatorStatus == (BACoordinatorStatus.STATUS_CLOSING)) {
			baContext.unlock();
			throw new InvalidStateException("Coordinator is in closing status ");
		} else if (coordinatorStatus == (BACoordinatorStatus.STATUS_COMPENSATING)) {
			baContext.unlock();
			throw new InvalidStateException("Coordinator is in compensating status ");
		} else {
			EndpointReference epr = baContext.addParticipant(participantEPR, protocol,
					registrationID);
			baContext.unlock();
			return epr;
		}
	}

	/**
	 * See Coordinator View of BusinessAgreement with Coordinator Completion
	 * 
	 * @param context
	 * @param enlishmentID
	 * @throws AbstractKandulaException
	 */
	public void completeOperation(BAActivityContext baContext) throws AbstractKandulaException {
		baContext.lock();
		switch (baContext.getStatus()) {
		case BACoordinatorStatus.STATUS_ACTIVE:
		case BACoordinatorStatus.STATUS_COMPLETING:
			baContext.unlock();
			baContext.setStatus(BACoordinatorStatus.STATUS_COMPLETING);
			Hashtable coordinatorCompletionParticipants = baContext
					.getCoordinatorCompletionParticipants();
			Iterator participantIterator = coordinatorCompletionParticipants.values().iterator();
			baContext.incrementCompletingParticipantCount(coordinatorCompletionParticipants.size());
			BACoordinatorCompletionParticipantServiceStub participantServiceStub;
			try {
				participantServiceStub = new BACoordinatorCompletionParticipantServiceStub(
						KandulaConfiguration.getInstance()
								.getCoordinatorAxis2ConfigurationContext(), null);
			} catch (Exception e1) {
				log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
						+ " : completeOperation : " + e1);
				throw new KandulaGeneralException(e1);
			}
			while (participantIterator.hasNext()) {
				Object object = (participantIterator.next());
				BAParticipantInformation participant = (BAParticipantInformation) object;
				try {
					participantServiceStub._getServiceClient().getOptions().setTo(
							participant.getEpr());
					Complete completeParam = new Complete();
					completeParam.setComplete(new NotificationType());
					participant.setStatus(BACoordinatorStatus.STATUS_COMPLETING);
					participantServiceStub.CompleteOperation(completeParam);
				} catch (Exception e) {
					log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
							+ " : completeOperation :" + participant.getEnlistmentId() + " : " + e);
					throw new KandulaGeneralException(e);
				}
			}
			break;
		case BACoordinatorStatus.STATUS_CANCELLING:
		case BACoordinatorStatus.STATUS_COMPLETED:
		case BACoordinatorStatus.STATUS_CLOSING:
		case BACoordinatorStatus.STATUS_COMPENSATING:
		case BACoordinatorStatus.STATUS_FAULTING:
		case BACoordinatorStatus.STATUS_EXITING:
		case BACoordinatorStatus.STATUS_ENDED:
			baContext.unlock();
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : completeOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		default:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : completeOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state, Defaulting");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		}
	}

	protected void closeOperation(BAActivityContext baContext, String enlistmentID)
			throws AbstractKandulaException {
		BAParticipantInformation baParticipantInformation = baContext.getParticipant(enlistmentID);
		BACoordinatorCompletionParticipantServiceStub participantServiceStub;
		switch (baParticipantInformation.getStatus()) {
		case BACoordinatorStatus.STATUS_ACTIVE:
		case BACoordinatorStatus.STATUS_CANCELLING:
		case BACoordinatorStatus.STATUS_COMPENSATING:
		case BACoordinatorStatus.STATUS_FAULTING:
		case BACoordinatorStatus.STATUS_EXITING:
		case BACoordinatorStatus.STATUS_ENDED:
		case BACoordinatorStatus.STATUS_COMPLETING:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : closeOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		case BACoordinatorStatus.STATUS_COMPLETED:
		case BACoordinatorStatus.STATUS_CLOSING:
			try {
				participantServiceStub = new BACoordinatorCompletionParticipantServiceStub(
						KandulaConfiguration.getInstance()
								.getCoordinatorAxis2ConfigurationContext(), null);
				participantServiceStub._getServiceClient().getOptions().setTo(
						baParticipantInformation.getEpr());
				Close closeParam = new Close();
				closeParam.setClose(new NotificationType());
				baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_CLOSING);
				participantServiceStub.CloseOperation(closeParam);
			} catch (Exception e) {
				log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
						+ " : close Operation :" + baParticipantInformation + " : " + e);
				throw new KandulaGeneralException(e);
			}
			break;
			default:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : closeOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state, Defaulting");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		}
	}
	

	protected void compensateOperation(BAActivityContext baContext, String enlistmentID)
			throws AbstractKandulaException {
		BAParticipantInformation baParticipantInformation = baContext.getParticipant(enlistmentID);
		BACoordinatorCompletionParticipantServiceStub participantServiceStub;
		switch (baParticipantInformation.getStatus()) {
		case BACoordinatorStatus.STATUS_ACTIVE:
		case BACoordinatorStatus.STATUS_CANCELLING:
		case BACoordinatorStatus.STATUS_CLOSING:
		case BACoordinatorStatus.STATUS_FAULTING:
		case BACoordinatorStatus.STATUS_EXITING:
		case BACoordinatorStatus.STATUS_ENDED:
		case BACoordinatorStatus.STATUS_COMPLETING:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : compensateOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		case BACoordinatorStatus.STATUS_COMPLETED:
		case BACoordinatorStatus.STATUS_COMPENSATING:
			try {
				participantServiceStub = new BACoordinatorCompletionParticipantServiceStub(
						KandulaConfiguration.getInstance()
								.getCoordinatorAxis2ConfigurationContext(), null);
				participantServiceStub._getServiceClient().getOptions().setTo(
						baParticipantInformation.getEpr());
				Compensate compensate = new Compensate();
				compensate.setCompensate(new NotificationType());
				baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_COMPENSATING);
				participantServiceStub.CompensateOperation(compensate);
			} catch (Exception e) {
				log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
						+ " : compensateOperation :" + baParticipantInformation + " : " + e);
				throw new KandulaGeneralException(e);
			}
			break;
			default:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : compensateOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state, Defaulting");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		}
	}
	
	public void compensatedOperation(BAActivityContext baContext, String enlistmentID) throws AbstractKandulaException {
		BAParticipantInformation baPaticipantInformation = baContext.getParticipant(enlistmentID);
		switch (baPaticipantInformation.getStatus()) {
		case BACoordinatorStatus.STATUS_ACTIVE:
		case BACoordinatorStatus.STATUS_COMPLETED:
		case BACoordinatorStatus.STATUS_CLOSING:
		case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
		case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
		case BACoordinatorStatus.STATUS_EXITING:
		case BACoordinatorStatus.STATUS_CANCELLING:
		case BACoordinatorStatus.STATUS_CANCELLING_ACTIVE:
		case BACoordinatorStatus.STATUS_CANCELLING_COMPLETING:
		case BACoordinatorStatus.STATUS_COMPLETING:
		case BACoordinatorStatus.STATUS_FAULTING_COMPLETING:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : compensatedOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");

		case BACoordinatorStatus.STATUS_COMPENSATING:
			baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_ENDED);
			baContext.removeParticipant(baPaticipantInformation.getEnlistmentId());
			if ((baContext.getcoordinatorCompletionParticipantsCount() == 0)
					&& (baContext.getparticipantCompletionParticipantCount() == 0)) {
				baContext.setStatus(BACoordinatorStatus.STATUS_ENDED);
				StorageUtils.forgetContext(baContext.getCoordinationContext().getActivityID());
			}
			break;
		case BACoordinatorStatus.STATUS_ENDED:
			break;
		default:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : compensatedOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state, Defaulting");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		}
	}

	/**
	 * Send the wsba:cancel message to the given participant
	 * 
	 * @param baContext
	 * @param cancelingParticipant
	 * @throws AbstractKandulaException
	 */
	public void cancelOperation(BAActivityContext baContext,
			BAParticipantInformation cancelingParticipant) throws AbstractKandulaException {
		baContext.lock();
		switch (baContext.getStatus()) {
		case BACoordinatorStatus.STATUS_COMPLETED:
		case BACoordinatorStatus.STATUS_CLOSING:
		case BACoordinatorStatus.STATUS_COMPENSATING:
		case BACoordinatorStatus.STATUS_FAULTING:
		case BACoordinatorStatus.STATUS_EXITING:
		case BACoordinatorStatus.STATUS_ENDED:
			baContext.unlock();
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : cancelOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		case BACoordinatorStatus.STATUS_ACTIVE:
		case BACoordinatorStatus.STATUS_CANCELLING:
			baContext.unlock();
			cancelingParticipant.setStatus(BACoordinatorStatus.STATUS_CANCELLING_ACTIVE);
			try {
				sendCancelMessage(cancelingParticipant);
			} catch (Exception e) {
				log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
						+ " : cancelOperation :" + cancelingParticipant + " : " + e);
				throw new KandulaGeneralException(e);
			}
			break;
		case BACoordinatorStatus.STATUS_COMPLETING:
			baContext.unlock();
			cancelingParticipant.setStatus(BACoordinatorStatus.STATUS_CANCELLING_COMPLETING);
			try {
				sendCancelMessage(cancelingParticipant);
			} catch (Exception e) {
				log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
						+ " : cancelOperation :" + cancelingParticipant + " : " + e);
				throw new KandulaGeneralException(e);
			}
			break;
		default:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : cancelOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state, Defaulting");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		}

	}

	private void sendCancelMessage(BAParticipantInformation participantInformation)
			throws AbstractKandulaException, RemoteException {
		BACoordinatorCompletionParticipantServiceStub participantServiceStub;
		participantServiceStub = new BACoordinatorCompletionParticipantServiceStub(
				KandulaConfiguration.getInstance().getCoordinatorAxis2ConfigurationContext(), null);
		participantServiceStub._getServiceClient().getOptions().setTo(
				participantInformation.getEpr());
		Cancel cancel = new Cancel();
		cancel.setCancel(new NotificationType());
		participantServiceStub.CancelOperation(cancel);
	}

	public void canceledOperation(BAActivityContext baContext, String enlistmentID)
			throws AbstractKandulaException {
		BAParticipantInformation cancelingParticipant = baContext.getParticipant(enlistmentID);
		switch (cancelingParticipant.getStatus()) {
		case BACoordinatorStatus.STATUS_ACTIVE:
		case BACoordinatorStatus.STATUS_COMPLETED:
		case BACoordinatorStatus.STATUS_COMPLETING:
		case BACoordinatorStatus.STATUS_CLOSING:
		case BACoordinatorStatus.STATUS_COMPENSATING:
		case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
		case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
		case BACoordinatorStatus.STATUS_FAULTING_COMPLETING:
		case BACoordinatorStatus.STATUS_EXITING:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : canceledOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		case BACoordinatorStatus.STATUS_ENDED:
			break;
		case BACoordinatorStatus.STATUS_CANCELLING:
		case BACoordinatorStatus.STATUS_CANCELLING_ACTIVE:
			cancelingParticipant.setStatus(BACoordinatorStatus.STATUS_ENDED);
			baContext.removeParticipant(enlistmentID);
		break;
		case BACoordinatorStatus.STATUS_CANCELLING_COMPLETING:
			baContext.decrementCompletingParticipantCount();
			cancelingParticipant.setStatus(BACoordinatorStatus.STATUS_ENDED);
			baContext.removeParticipant(enlistmentID);
			break;
		default:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : canceledOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state, Defaulting");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		}
	}
	

	public void exitOperation(BAActivityContext baContext, String enlistmentID)
			throws AbstractKandulaException {
		BAParticipantInformation cancelingParticipant = baContext.getParticipant(enlistmentID);
		switch (cancelingParticipant.getStatus()) {
		case BACoordinatorStatus.STATUS_COMPLETED:
		case BACoordinatorStatus.STATUS_CLOSING:
		case BACoordinatorStatus.STATUS_COMPENSATING:
		case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
		case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
		case BACoordinatorStatus.STATUS_FAULTING_COMPLETING:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : exitOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		case BACoordinatorStatus.STATUS_EXITING:
			break;
		case BACoordinatorStatus.STATUS_ENDED:
			//resend exited
			break;
		case BACoordinatorStatus.STATUS_ACTIVE:
		case BACoordinatorStatus.STATUS_CANCELLING_ACTIVE:
		case BACoordinatorStatus.STATUS_CANCELLING_COMPLETING:
		case BACoordinatorStatus.STATUS_COMPLETING:
		case BACoordinatorStatus.STATUS_CANCELLING:
			cancelingParticipant.setStatus(BACoordinatorStatus.STATUS_EXITING);
			exited(baContext,cancelingParticipant);
			baContext.removeParticipant(enlistmentID);
			
		break;
		default:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : exitOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state, Defaulting");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		}
	}
	
	protected void exited(BAActivityContext baContext, BAParticipantInformation baParticipantInformation) throws AbstractKandulaException {
		BACoordinatorCompletionParticipantServiceStub participantServiceStub;
		switch (baParticipantInformation.getStatus()) {
		case BACoordinatorStatus.STATUS_ACTIVE:
		case BACoordinatorStatus.STATUS_CANCELLING:
		case BACoordinatorStatus.STATUS_COMPENSATING:
		case BACoordinatorStatus.STATUS_FAULTING:
		case BACoordinatorStatus.STATUS_COMPLETED:
		case BACoordinatorStatus.STATUS_CLOSING:
		case BACoordinatorStatus.STATUS_COMPLETING:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : exited Operation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");

		case BACoordinatorStatus.STATUS_EXITING:
		case BACoordinatorStatus.STATUS_ENDED:
			try {
				participantServiceStub = new BACoordinatorCompletionParticipantServiceStub(
						KandulaConfiguration.getInstance()
								.getCoordinatorAxis2ConfigurationContext(), null);
				participantServiceStub._getServiceClient().getOptions().setTo(
						baParticipantInformation.getEpr());
				Exited exited = new Exited();
				exited.setExited(new NotificationType());
				baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_ENDED);
				participantServiceStub.ExitedOperation(exited);
			} catch (Exception e) {
				log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
						+ " : exited Operation :" + baParticipantInformation + " : " + e);
				throw new KandulaGeneralException(e);
			}
			break;
			default:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : exited operation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state, Defaulting");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		}
	}


	public void closedOperation(BAActivityContext baContext, String enlistmentID)
			throws AbstractKandulaException {
		BAParticipantInformation baPaticipantInformation = baContext.getParticipant(enlistmentID);
		switch (baPaticipantInformation.getStatus()) {
		case BACoordinatorStatus.STATUS_ACTIVE:
		case BACoordinatorStatus.STATUS_COMPLETED:
		case BACoordinatorStatus.STATUS_COMPENSATING:
		case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
		case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
		case BACoordinatorStatus.STATUS_EXITING:
		case BACoordinatorStatus.STATUS_CANCELLING_ACTIVE:
		case BACoordinatorStatus.STATUS_CANCELLING_COMPLETING:
		case BACoordinatorStatus.STATUS_COMPLETING:
		case BACoordinatorStatus.STATUS_FAULTING_COMPLETING:
		case BACoordinatorStatus.STATUS_CANCELLING:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : closedOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		case BACoordinatorStatus.STATUS_ENDED:
			break;
		case BACoordinatorStatus.STATUS_CLOSING:
			baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_ENDED);
			baContext.removeParticipant(enlistmentID);
			if ((baContext.getcoordinatorCompletionParticipantsCount() == 0)
					&& (baContext.getparticipantCompletionParticipantCount() == 0)) {
				baContext.setStatus(BACoordinatorStatus.STATUS_ENDED);
				StorageUtils.forgetContext(baContext.getCoordinationContext().getActivityID());
			}
			break;
		}
	}

	// 3. Start - Fault
	public void faultOperation(BAActivityContext baContext, String enlistmentID)
			throws AbstractKandulaException {
		BAParticipantInformation cancelingParticipant = baContext.getParticipant(enlistmentID);
		switch (cancelingParticipant.getStatus()) {
		case BACoordinatorStatus.STATUS_COMPLETED:
		case BACoordinatorStatus.STATUS_CLOSING:
		case BACoordinatorStatus.STATUS_EXITING:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : faultOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		
		case BACoordinatorStatus.STATUS_ENDED:
			faultedOperation(baContext,cancelingParticipant);
			break;
		case BACoordinatorStatus.STATUS_COMPENSATING:
			cancelingParticipant.setStatus(BACoordinatorStatus.STATUS_FAULTING_COMPENSATING);
			faultedOperation(baContext,cancelingParticipant);
			break;
		case BACoordinatorStatus.STATUS_ACTIVE:
		case BACoordinatorStatus.STATUS_CANCELLING_ACTIVE:
		case BACoordinatorStatus.STATUS_CANCELLING_COMPLETING:
		case BACoordinatorStatus.STATUS_COMPLETING:
		case BACoordinatorStatus.STATUS_CANCELLING:
			cancelingParticipant.setStatus(BACoordinatorStatus.STATUS_FAULTING_ACTIVE);
			faultedOperation(baContext,cancelingParticipant);
		break;
		case BACoordinatorStatus.STATUS_FAULTING:
		case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
		case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
		case BACoordinatorStatus.STATUS_FAULTING_COMPLETING:
			break;
		default:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : faultOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state, Defaulting");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		}
	}

	protected void faultedOperation(BAActivityContext baContext,BAParticipantInformation baParticipantInformation) throws AbstractKandulaException {
		BACoordinatorCompletionParticipantServiceStub participantServiceStub;
		switch (baParticipantInformation.getStatus()) {
		case BACoordinatorStatus.STATUS_ACTIVE:
		case BACoordinatorStatus.STATUS_CANCELLING:
		case BACoordinatorStatus.STATUS_COMPLETING:
		case BACoordinatorStatus.STATUS_COMPLETED:
		case BACoordinatorStatus.STATUS_CLOSING:
		case BACoordinatorStatus.STATUS_COMPENSATING:
		case BACoordinatorStatus.STATUS_EXITING:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : faultedOperation : Coordinator is in" + baContext.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException("Coordinator is in" + baContext.getStatus() + "state");
		case BACoordinatorStatus.STATUS_ENDED:
		case BACoordinatorStatus.STATUS_FAULTING:
		case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
		case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
		case BACoordinatorStatus.STATUS_FAULTING_COMPLETING:
		case BACoordinatorStatus.STATUS_FAULTING_COMPLETED:
			try {
				participantServiceStub = new BACoordinatorCompletionParticipantServiceStub(
						KandulaConfiguration.getInstance()
								.getCoordinatorAxis2ConfigurationContext(), null);
				participantServiceStub._getServiceClient().getOptions().setTo(
						baParticipantInformation.getEpr());
				Failed failed = new Failed();
				failed.setFailed(new NotificationType());
				baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_ENDED);
				baContext.removeParticipant(baParticipantInformation.getEnlistmentId());
				participantServiceStub.FailedOperation(failed);
			} catch (Exception e) {
				log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
						+ " : faultedOperation :" + baParticipantInformation + " : " + e);
				throw new KandulaGeneralException(e);
			}
			break;
			default:
			log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
					+ " : faultedOperation : Coordinator is in" + baParticipantInformation.getStatus()
					+ " (invalid) state, Defaulting");
			throw new InvalidStateException("Coordinator is in" + baParticipantInformation.getStatus() + "state");
		}
	}

	/**
	 * If the coordinator received a Completed message - then it checks whether
	 * all the participants have finished. This is done by checking whether
	 * allparticipantcount is equal to the failed + completed
	 * participants.(Since cancelled and exited participant are removed from the
	 * list). If all have finished If all are completed - send close to all If
	 * even one is faulted - send compensate for all
	 * 
	 * @param baContext
	 * @param enlistmentID
	 * @throws AbstractKandulaException
	 */
	public void completedOperation(final BAActivityContext baContext, String enlistmentID) throws AbstractKandulaException {
		if (baContext == null) {
			throw new IllegalStateException("No Activity Found for this Activity ID");
		}
		BAParticipantInformation baParticipantInformation = baContext.getParticipant(enlistmentID);
	
		if (baParticipantInformation.getProtocol().equals(Constants.WS_BA_PC)) {
			switch (baParticipantInformation.getStatus()) {
			case BACoordinatorStatus.STATUS_COMPLETED:
			case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
			case BACoordinatorStatus.STATUS_ENDED:
				break;
			case BACoordinatorStatus.STATUS_ACTIVE:
			case BACoordinatorStatus.STATUS_CANCELLING:
				baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_COMPLETED);
				baContext.decrementCompletingParticipantCount();
				if (!baContext.hasMoreCompleting()) {
					baContext.setStatus(BACoordinatorStatus.STATUS_COMPLETED);
					log.debug("WS_BA : " + baContext.getCoordinationContext().getActivityID()
							+ " : " + "Decision to close.");
					BusinessActivityCallBack callBack = baContext.getCallBack();
					callBack.onComplete();
				}
				break;
			case BACoordinatorStatus.STATUS_CLOSING:
				closeOperation(baContext, baParticipantInformation.getEnlistmentId());
				break;
			case BACoordinatorStatus.STATUS_COMPENSATING:
				compensateOperation(baContext, baParticipantInformation.getEnlistmentId());
				break;
			case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
			case BACoordinatorStatus.STATUS_EXITING:
				log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
						+ " : atCompleted : Coordinator is in" + baContext.getStatus()
						+ " (invalid) state");
				throw new InvalidStateException("Coordinator is in" + baContext.getStatus()
						+ "state");
			default:
				log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
						+ " : atCompleted : Coordinator is in" + baContext.getStatus()
						+ " (invalid) state, Defaulting");
				throw new InvalidStateException("Coordinator is in" + baContext.getStatus()
						+ "state");
			}
		} else if (baParticipantInformation.getProtocol().equals(Constants.WS_BA_CC)) {
			switch (baParticipantInformation.getStatus()) {
			case BACoordinatorStatus.STATUS_COMPLETED:
			case BACoordinatorStatus.STATUS_FAULTING:
			case BACoordinatorStatus.STATUS_ENDED:
				break;
	
			case BACoordinatorStatus.STATUS_CANCELLING_COMPLETING:
			case BACoordinatorStatus.STATUS_COMPLETING:
				baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_COMPLETED);
				baContext.decrementCompletingParticipantCount();
				if (!baContext.hasMoreCompleting()) {
					baContext.setStatus(BACoordinatorStatus.STATUS_COMPLETED);
					log.debug("WS_BA : " + baContext.getCoordinationContext().getActivityID()
							+ " : " + "Decision to close.");
					Runnable threadedTask = new Runnable() {
						public void run() {
							BusinessActivityCallBack callBack = baContext.getCallBack();
							callBack.onComplete();
						}
					};
					Thread thread = new Thread(threadedTask);
					thread.start();
				}
				break;
			case BACoordinatorStatus.STATUS_CLOSING:
				closeOperation(baContext, baParticipantInformation.getEnlistmentId());
				break;
			case BACoordinatorStatus.STATUS_COMPENSATING:
				compensateOperation(baContext, baParticipantInformation.getEnlistmentId());
				break;
			case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
			case BACoordinatorStatus.STATUS_FAULTING_COMPLETING:
			case BACoordinatorStatus.STATUS_EXITING:
			case BACoordinatorStatus.STATUS_ACTIVE:
			case BACoordinatorStatus.STATUS_CANCELLING_ACTIVE:
				log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
						+ " : atCompleted : Coordinator is in" + baContext.getStatus()
						+ " (invalid) state");
				throw new InvalidStateException("Coordinator is in" + baContext.getStatus()
						+ "state");
			default:
				log.fatal("WS_BA : " + baContext.getCoordinationContext().getActivityID()
						+ " : atCompleted : Coordinator is in" + baContext.getStatus()
						+ " (invalid) state, Defaulting");
				throw new InvalidStateException("Coordinator is in" + baContext.getStatus()
						+ "state");
	
			}
		}
	}
	/**
	 * After all the participants have completed sucessfully send close to all
	 * 
	 * @param baContext
	 * @throws AbstractKandulaException
	 */
	public void closeAllOperation(BAActivityContext baContext) throws AbstractKandulaException {
		Iterator allParticipants = baContext.getAllParticipants();
		baContext.setStatus(Status.BACoordinatorStatus.STATUS_CLOSING);
		while (allParticipants.hasNext()) {
			BAParticipantInformation participantInformation = (BAParticipantInformation) allParticipants
					.next();
			String enlistmentId = participantInformation.getEnlistmentId();
			closeOperation(baContext, enlistmentId);
		}
	}

	// After all the cparticipants have finished and if a even one faulted
	// occured send compensate to all
	public void compensateAllOperation(BAActivityContext baContext) throws AbstractKandulaException {
		baContext.lock();
		Iterator allparticipants = baContext.getAllParticipants();
		baContext.setStatus(Status.BACoordinatorStatus.STATUS_COMPENSATING);
		while (allparticipants.hasNext()) {
			BAParticipantInformation pInformation = (BAParticipantInformation) allparticipants
					.next();
			String nextEnlistmentId = pInformation.getEnlistmentId();
			compensateOperation(baContext, nextEnlistmentId);
		}
	}
	
	public void finalizeMixedOutcomeActivity(BAActivityContext context, ArrayList closeParticipantsList,ArrayList compensateParticipantsList) throws AbstractKandulaException
	{
		Iterator iterator1 = compensateParticipantsList.iterator();
		while (iterator1.hasNext()) {
			BAParticipantInformation participantInformation = (BAParticipantInformation) iterator1.next();
		    compensateOperation(context,participantInformation.getEnlistmentId());
		}
		Iterator iterator = closeParticipantsList.iterator();
		while (iterator.hasNext()) {
			BAParticipantInformation participantInformation = (BAParticipantInformation) iterator.next();
		    closeOperation(context,participantInformation.getEnlistmentId());
		}
	}
}
