package org.apache.kandula.participant.ba;

import javax.xml.namespace.QName;

import org.apache.axis2.context.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.impl.BAActivityContext;
import org.apache.kandula.context.impl.ParticipantContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.InvalidStateException;
import org.apache.kandula.faults.KandulaGeneralException;
import org.apache.kandula.storage.StorageUtils;
import org.apache.kandula.utility.KandulaConfiguration;
import org.apache.kandula.wsba.BACoordinatorCompletionCoordinatorServiceStub;
import org.apache.kandula.wsba.BACoordinatorCompletionParticipantServiceStub;
import org.apache.kandula.wsba.BAParticipantCompletionCoordinatorServiceStub;
import org.apache.kandula.wsba.BAParticipantCompletionParticipantServiceStub;
import org.oasis_open.docs.ws_tx.wsba._2006._06.Canceled;
import org.oasis_open.docs.ws_tx.wsba._2006._06.Closed;
import org.oasis_open.docs.ws_tx.wsba._2006._06.Compensated;
import org.oasis_open.docs.ws_tx.wsba._2006._06.Completed;
import org.oasis_open.docs.ws_tx.wsba._2006._06.ExceptionType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.Exit;
import org.oasis_open.docs.ws_tx.wsba._2006._06.Fail;
import org.oasis_open.docs.ws_tx.wsba._2006._06.NotificationType;

public class BAParticipantTransactionSentCoordinator {

	private static final Log log = LogFactory.getLog(BAParticipantTransactionSentCoordinator.class);

	public void completed(ParticipantContext context) throws AbstractKandulaException {

		context.lock();
		switch (context.getStatus()) {
		case (Status.BAParticipantStatus.STATUS_COMPLETING):
		case (Status.BAParticipantStatus.STATUS_COMPLETED):
			context.unlock();
			try {
				BAParticipantCompletionCoordinatorServiceStub serviceStub = new BAParticipantCompletionCoordinatorServiceStub(
						MessageContext.getCurrentMessageContext().getConfigurationContext(), null);
				serviceStub._getServiceClient().getOptions().setTo(context.getCoordinationEPR());
				Completed completed = new Completed();
				completed.setCompleted(new NotificationType());
				context.setStatus(Status.BAParticipantStatus.STATUS_COMPLETED);
				serviceStub.CompletedOperation(completed);
			} catch (Exception e) {
				log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
						+ " : completed :" + context.getID() + " : " + e);
				throw new KandulaGeneralException(e);
			}
			break;
		case (Status.BAParticipantStatus.STATUS_CANCELLING):
		case (Status.BAParticipantStatus.STATUS_COMPENSATING):
		case (Status.BAParticipantStatus.STATUS_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_CLOSING):
		case (Status.BAParticipantStatus.STATUS_FAULTING):
		case (Status.BAParticipantStatus.STATUS_EXITING):
		case (Status.BAParticipantStatus.STATUS_ENDED):
			log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
					+ " : completed :" + context.getID() + " : " + " Participant is in"
					+ context.getStatus() + " (invalid) state");
			throw new InvalidStateException(" Participant is in" + context.getStatus()
					+ " (invalid) state");
		}
	}

	public void closed(ParticipantContext context) throws AbstractKandulaException {
		BACoordinatorCompletionCoordinatorServiceStub coordinatorStub;
		switch (context.getStatus()) {
		case (Status.BAParticipantStatus.STATUS_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_CANCELLING):
		case (Status.BAParticipantStatus.STATUS_COMPLETING):
		case (Status.BAParticipantStatus.STATUS_COMPLETED):
		case (Status.BAParticipantStatus.STATUS_COMPENSATING):
		case (Status.BAParticipantStatus.STATUS_FAULTING):
		case (Status.BAParticipantStatus.STATUS_EXITING):
			log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID() + " : closed :"
					+ context.getID() + " : " + " Participant is in" + context.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException(" Participant is in" + context.getStatus()
					+ " (invalid) state");
		case (Status.BAParticipantStatus.STATUS_CLOSING):
		case (Status.BAParticipantStatus.STATUS_ENDED):
			try {
				coordinatorStub = new BACoordinatorCompletionCoordinatorServiceStub(MessageContext
						.getCurrentMessageContext().getConfigurationContext(), null);
				coordinatorStub._getServiceClient().getOptions()
						.setTo(context.getCoordinationEPR());
				Closed closed = new Closed();
				closed.setClosed(new NotificationType());
				coordinatorStub.ClosedOperation(closed);
				context.setStatus(Status.BAParticipantStatus.STATUS_ENDED);
				StorageUtils.forgetContext(context.getID());
			} catch (Exception e) {
				log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
						+ " : closed :" + context.getID() + " : " + e);
				throw new KandulaGeneralException(e);
			}
			break;
		}
	}

	public void canceled(ParticipantContext context) throws AbstractKandulaException {
		switch (context.getStatus()) {
		case (Status.BAParticipantStatus.STATUS_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_COMPLETING):
		case (Status.BAParticipantStatus.STATUS_COMPLETED):
		case (Status.BAParticipantStatus.STATUS_CLOSING):
		case (Status.BAParticipantStatus.STATUS_COMPENSATING):
		case (Status.BAParticipantStatus.STATUS_FAULTING):
		case (Status.BAParticipantStatus.STATUS_EXITING):
			log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
					+ " : canceled :" + context.getID() + " : " + " Participant is in"
					+ context.getStatus() + " (invalid) state");
			throw new InvalidStateException(" Participant is in" + context.getStatus()
					+ " (invalid) state");

		case (Status.BAParticipantStatus.STATUS_CANCELLING):
		case (Status.BAParticipantStatus.STATUS_ENDED):
			try {
				BACoordinatorCompletionCoordinatorServiceStub coordinatorServiceStub;
				coordinatorServiceStub = new BACoordinatorCompletionCoordinatorServiceStub(
						MessageContext.getCurrentMessageContext().getConfigurationContext(), null);
				coordinatorServiceStub._getServiceClient().getOptions().setTo(
						context.getCoordinationEPR());
				Canceled canceled = new Canceled();
				canceled.setCanceled(new NotificationType());
				coordinatorServiceStub.CanceledOperation(canceled);
				context.setStatus(Status.BAParticipantStatus.STATUS_ENDED);
				StorageUtils.forgetContext(context.getID());
			} catch (Exception e) {
				log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
						+ " : canceled :" + context.getID() + " : " + e);
				throw new KandulaGeneralException(e);
			}
			break;
		}
	}

	public void exit(ParticipantContext context) throws AbstractKandulaException {
		BACoordinatorCompletionCoordinatorServiceStub coordinatorStub;
		switch (context.getStatus()) {
		case (Status.BAParticipantStatus.STATUS_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_COMPLETING):
		case (Status.BAParticipantStatus.STATUS_EXITING):
			try {
				coordinatorStub = new BACoordinatorCompletionCoordinatorServiceStub(MessageContext
						.getCurrentMessageContext().getConfigurationContext(), null);
				coordinatorStub._getServiceClient().getOptions()
						.setTo(context.getCoordinationEPR());
				Exit exit = new Exit();
				exit.setExit(new NotificationType());
				context.setStatus(Status.BAParticipantStatus.STATUS_EXITING);
				coordinatorStub.ExitOperation(exit);
			} catch (Exception e) {
				log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
						+ " : closed :" + context.getID() + " : " + e);
				throw new KandulaGeneralException(e);
			}
			break;
		case (Status.BAParticipantStatus.STATUS_CANCELLING):
		case (Status.BAParticipantStatus.STATUS_COMPLETED):
		case (Status.BAParticipantStatus.STATUS_CLOSING):
		case (Status.BAParticipantStatus.STATUS_COMPENSATING):
		case (Status.BAParticipantStatus.STATUS_FAULTING):
		case (Status.BAParticipantStatus.STATUS_ENDED):
			log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID() + " : exit :"
					+ context.getID() + " : " + " Participant is in" + context.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException(" Participant is in" + context.getStatus()
					+ " (invalid) state");
		}
	}

	public void compensated(ParticipantContext context) throws AbstractKandulaException {
		BACoordinatorCompletionCoordinatorServiceStub coordinatorStub;
		switch (context.getStatus()) {
		case (Status.BAParticipantStatus.STATUS_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_CANCELLING):
		case (Status.BAParticipantStatus.STATUS_COMPLETING):
		case (Status.BAParticipantStatus.STATUS_COMPLETED):
		case (Status.BAParticipantStatus.STATUS_CLOSING):
		case (Status.BAParticipantStatus.STATUS_FAULTING):
		case (Status.BAParticipantStatus.STATUS_EXITING):
			log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
					+ " : compensated :" + context.getID() + " : " + " Participant is in"
					+ context.getStatus() + " (invalid) state");
			throw new InvalidStateException(" Participant is in" + context.getStatus()
					+ " (invalid) state");
		case (Status.BAParticipantStatus.STATUS_COMPENSATING):
		case (Status.BAParticipantStatus.STATUS_ENDED):
			try {
				coordinatorStub = new BACoordinatorCompletionCoordinatorServiceStub(MessageContext
						.getCurrentMessageContext().getConfigurationContext(), null);
				coordinatorStub._getServiceClient().getOptions()
						.setTo(context.getCoordinationEPR());
				Compensated compensated = new Compensated();
				compensated.setCompensated(new NotificationType());
				context.setStatus(Status.BAParticipantStatus.STATUS_ENDED);
				coordinatorStub.CompensatedOperation(compensated);
			} catch (Exception e) {
				log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
						+ " : compensated :" + context.getID() + " : " + e);
				throw new KandulaGeneralException(e);
			}
			break;
		}
	}

	public void fault(ParticipantContext context) throws AbstractKandulaException {
		switch (context.getStatus()) {
		case (Status.BAParticipantStatus.STATUS_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_COMPLETING):
			context.setStatus(Status.BAParticipantStatus.STATUS_FAULTING_ACTIVE);
			sendFaultMessage(context);
			break;
		case (Status.BAParticipantStatus.STATUS_CANCELLING):
		case (Status.BAParticipantStatus.STATUS_COMPLETED):
		case (Status.BAParticipantStatus.STATUS_CLOSING):
		case (Status.BAParticipantStatus.STATUS_EXITING):
		case (Status.BAParticipantStatus.STATUS_ENDED):
			log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID() + " : Fault :"
					+ context.getID() + " : " + " Participant is in" + context.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException(" Participant is in" + context.getStatus()
					+ " (invalid) state");
		case (Status.BAParticipantStatus.STATUS_COMPENSATING):
			context.setStatus(Status.BAParticipantStatus.STATUS_FAULTING_COMPENSATING);
			sendFaultMessage(context);
			break;
		case (Status.BAParticipantStatus.STATUS_FAULTING):
			context.setStatus(Status.BAParticipantStatus.STATUS_FAULTING);
			sendFaultMessage(context);
			break;
		}
	}

	private void sendFaultMessage(ParticipantContext context) throws AbstractKandulaException {
		BACoordinatorCompletionCoordinatorServiceStub coordinatorStub;
		try {
			coordinatorStub = new BACoordinatorCompletionCoordinatorServiceStub(
					KandulaConfiguration.getInstance().getPariticipantAxis2ConfigurationContext(),
					null);
			coordinatorStub._getServiceClient().getOptions().setTo(context.getCoordinationEPR());
			Fail fail = new Fail();
			ExceptionType exceptionType = new ExceptionType();
			exceptionType.setExceptionIdentifier(new QName("Bimalee"));
			fail.setFail(exceptionType);
			context.setStatus(Status.BAParticipantStatus.STATUS_ENDED);
			coordinatorStub.FailOperation(fail);
		} catch (Exception e) {
			log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
					+ " : compensated :" + context.getID() + " : " + e);
			throw new KandulaGeneralException(e);
		}
	}
}
