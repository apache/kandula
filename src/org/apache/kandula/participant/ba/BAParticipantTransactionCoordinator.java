package org.apache.kandula.participant.ba;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.impl.ParticipantContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.InvalidStateException;
import org.apache.kandula.storage.StorageUtils;
import org.apache.kandula.wsba.BACoordinatorCompletionParticipantServiceStub;
import org.apache.kandula.wsba.BAParticipantCompletionCoordinatorServiceStub;

public class BAParticipantTransactionCoordinator {

	private static final Log log = LogFactory.getLog(BAParticipantTransactionCoordinator.class);

	public void complete(ParticipantContext context) throws AbstractKandulaException {
		BAParticipantTransactionSentCoordinator sentCoordinator;
		switch (context.getStatus()) {
		case (Status.BAParticipantStatus.STATUS_ACTIVE):
			KandulaBusinessActivityResource resource = (KandulaBusinessActivityResource) context
					.getResource();
			context.setStatus(Status.BAParticipantStatus.STATUS_COMPLETING);
			resource.complete();
			sentCoordinator = new BAParticipantTransactionSentCoordinator();
			sentCoordinator.completed(context);
			break;
		case (Status.BAParticipantStatus.STATUS_COMPLETED):
			sentCoordinator = new BAParticipantTransactionSentCoordinator();
			sentCoordinator.completed(context);
			break;
		case (Status.BAParticipantStatus.STATUS_WORK_DONE):
			context.setStatus(Status.BAParticipantStatus.STATUS_COMPLETING);
			sentCoordinator = new BAParticipantTransactionSentCoordinator();
			sentCoordinator.completed(context);
			break;
		case (Status.BAParticipantStatus.STATUS_CANCELLING):
		case (Status.BAParticipantStatus.STATUS_COMPLETING):
		case (Status.BAParticipantStatus.STATUS_CLOSING):
		case (Status.BAParticipantStatus.STATUS_COMPENSATING):
		case (Status.BAParticipantStatus.STATUS_FAULTING_COMPENSATING):
		case (Status.BAParticipantStatus.STATUS_ENDED):
			break;

		case (Status.BAParticipantStatus.STATUS_FAULTING_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_FAULTING_COMPLETED):
			// TODO resend fault
			// try{
			// CCPStub = new BACoordinatorCompletionParticipantServiceStub();
			// Failed failed= new Failed();
			// CCPStub.FailedOperation(failed);
			// context.setStatus(Status.BAParticipantStatus.STATUS_FAULTING_ACTIVE_COMPLETED);
			// }catch(Exception e){
			//					
			// }
			break;

		case (Status.BAParticipantStatus.STATUS_EXITING):
			sentCoordinator = new BAParticipantTransactionSentCoordinator();
			sentCoordinator.exit(context);
			break;
		}
	}

	public void close(ParticipantContext context) throws AbstractKandulaException {

		switch (context.getStatus()) {
		case (Status.BAParticipantStatus.STATUS_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_CANCELLING):
		case (Status.BAParticipantStatus.STATUS_COMPLETING):
		case (Status.BAParticipantStatus.STATUS_COMPENSATING):
		case (Status.BAParticipantStatus.STATUS_FAULTING_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_FAULTING_COMPLETED):
		case (Status.BAParticipantStatus.STATUS_FAULTING_COMPENSATING):
		case (Status.BAParticipantStatus.STATUS_EXITING):
			log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
					+ " : completeOperation :" + context.getID() + " : " + " Coordinator is in"
					+ context.getStatus() + " (invalid) state");
			throw new InvalidStateException();

		case (Status.BAParticipantStatus.STATUS_COMPLETED):
			KandulaBusinessActivityResource resource = (KandulaBusinessActivityResource) context
					.getResource();

			context.setStatus(Status.BAParticipantStatus.STATUS_CLOSING);
			resource.close();
			BAParticipantTransactionSentCoordinator sentCoordinator = new BAParticipantTransactionSentCoordinator();
			sentCoordinator.closed(context);
			break;

		case (Status.BAParticipantStatus.STATUS_ENDED):
			BAParticipantTransactionSentCoordinator sentCoordinator1 = new BAParticipantTransactionSentCoordinator();
			sentCoordinator1.closed(context);
			break;
		case (Status.BAParticipantStatus.STATUS_CLOSING):
			break;
		}

	}

	public void cancel(ParticipantContext context) throws AbstractKandulaException {
		BAParticipantTransactionSentCoordinator coordinator;
		switch (context.getStatus()) {
		case (Status.BAParticipantStatus.STATUS_CANCELLING):
		case (Status.BAParticipantStatus.STATUS_CLOSING):
		case (Status.BAParticipantStatus.STATUS_COMPENSATING):
		case (Status.BAParticipantStatus.STATUS_FAULTING_COMPENSATING):
			break;

		case (Status.BAParticipantStatus.STATUS_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_COMPLETING):
		case (Status.BAParticipantStatus.STATUS_WORK_DONE):
			KandulaBusinessActivityResource resource = (KandulaBusinessActivityResource) context
					.getResource();
			context.setStatus(Status.BAParticipantStatus.STATUS_CANCELLING);
			resource.cancel();
			BAParticipantTransactionSentCoordinator sentCoordinator = new BAParticipantTransactionSentCoordinator();
			sentCoordinator.canceled(context);
			break;

		case (Status.BAParticipantStatus.STATUS_COMPLETED):
			coordinator = new BAParticipantTransactionSentCoordinator();
			coordinator.completed(context);
			break;
		case (Status.BAParticipantStatus.STATUS_FAULTING_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_FAULTING_COMPLETED):
			// resend fault
			break;

		case (Status.BAParticipantStatus.STATUS_EXITING):
			coordinator = new BAParticipantTransactionSentCoordinator();
			coordinator.exit(context);
			break;
		case (Status.BAParticipantStatus.STATUS_ENDED):
			coordinator = new BAParticipantTransactionSentCoordinator();
			coordinator.canceled(context);
			break;
		}
	}

	public void exited(ParticipantContext context) throws AbstractKandulaException {

		BACoordinatorCompletionParticipantServiceStub CCPStub;
		switch (context.getStatus()) {
		case (Status.BAParticipantStatus.STATUS_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_CANCELLING):
		case (Status.BAParticipantStatus.STATUS_COMPLETING):
		case (Status.BAParticipantStatus.STATUS_COMPLETED):
		case (Status.BAParticipantStatus.STATUS_CLOSING):
		case (Status.BAParticipantStatus.STATUS_COMPENSATING):
		case (Status.BAParticipantStatus.STATUS_FAULTING_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_FAULTING_COMPLETED):
		case (Status.BAParticipantStatus.STATUS_FAULTING_COMPENSATING):
			log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID() + " : exited :"
					+ context.getID() + " : " + " Coordinator is in" + context.getStatus()
					+ " (invalid) state");
			throw new InvalidStateException();
		case (Status.BAParticipantStatus.STATUS_EXITING):
		case (Status.BAParticipantStatus.STATUS_ENDED):
			context.setStatus(Status.BAParticipantStatus.STATUS_ENDED);
			StorageUtils.forgetContext(context.getID());
			break;
		}

	}

	public void compensate(ParticipantContext context) throws AbstractKandulaException {
		BAParticipantTransactionSentCoordinator sentCoordinator;
		switch (context.getStatus()) {
		case (Status.BAParticipantStatus.STATUS_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_CANCELLING):
		case (Status.BAParticipantStatus.STATUS_COMPLETING):
		case (Status.BAParticipantStatus.STATUS_CLOSING):
		case (Status.BAParticipantStatus.STATUS_FAULTING_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_FAULTING_COMPLETED):
		case (Status.BAParticipantStatus.STATUS_EXITING):
			throw new InvalidStateException();

		case (Status.BAParticipantStatus.STATUS_COMPLETED):
			KandulaBusinessActivityResource resource = (KandulaBusinessActivityResource) context
					.getResource();
			context.setStatus(Status.BAParticipantStatus.STATUS_COMPENSATING);
			resource.compensate();
			sentCoordinator = new BAParticipantTransactionSentCoordinator();
			sentCoordinator.compensated(context);
		case (Status.BAParticipantStatus.STATUS_COMPENSATING):
			break;
		case (Status.BAParticipantStatus.STATUS_FAULTING_COMPENSATING):
			//resend fault
			break;
		case (Status.BAParticipantStatus.STATUS_ENDED):
			sentCoordinator = new BAParticipantTransactionSentCoordinator();
			sentCoordinator.compensated(context);
			break;
		}
	}

	public void Faulted(ParticipantContext context) throws AbstractKandulaException {
		BACoordinatorCompletionParticipantServiceStub CCPStub;
		switch (context.getStatus()) {
		case (Status.BAParticipantStatus.STATUS_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_CANCELLING):
		case (Status.BAParticipantStatus.STATUS_COMPLETING):
		case (Status.BAParticipantStatus.STATUS_COMPLETED):
		case (Status.BAParticipantStatus.STATUS_CLOSING):
		case (Status.BAParticipantStatus.STATUS_COMPENSATING):
		case (Status.BAParticipantStatus.STATUS_EXITING):
			log.fatal("WS_BA : " + context.getCoordinationContext().getActivityID()
					+ " : Faulted :" + context.getID() + " : " + " Coordinator is in"
					+ context.getStatus() + " (invalid) state");
			throw new InvalidStateException();
		case (Status.BAParticipantStatus.STATUS_FAULTING_ACTIVE):
		case (Status.BAParticipantStatus.STATUS_FAULTING_COMPLETED):
		case (Status.BAParticipantStatus.STATUS_FAULTING_COMPENSATING):
			context.setStatus(Status.BAParticipantStatus.STATUS_ENDED);
			StorageUtils.forgetContext(context.getID());
			break;
		case (Status.BAParticipantStatus.STATUS_ENDED):
			break;
		}
	}
}
