package org.apache.kandula.ba;

import java.util.Iterator;

import org.apache.kandula.Status;
import org.apache.kandula.context.impl.BAActivityContext;
import org.apache.kandula.coordinator.ba.BACoordinator;
import org.apache.kandula.coordinator.ba.BAParticipantInformation;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.KandulaGeneralException;

public class BusinessActivity {

	protected BAActivityContext context;

	public void complete() throws AbstractKandulaException {
		BACoordinator coordinator =  new BACoordinator();
		coordinator.completeOperation(context);
		while (context.getStatus()!=Status.BACoordinatorStatus.STATUS_ENDED){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new KandulaGeneralException(e);
			}
		}
	}

	public void cancelParticipant(String participantID) throws AbstractKandulaException {
		BAParticipantInformation participantInformation;
		participantInformation = context.getParticipant(participantID);
		if (participantInformation==null)
		{
			//participant registration might be still on the wire.. So let's wait an retry..
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				throw new KandulaGeneralException(e);
			}
			participantInformation = context.getParticipant(participantID);
			if (participantInformation==null)
			{
				throw new KandulaGeneralException("No registered participant found by the ID :"+participantID);
			}	
		}
		BACoordinator coordinator =  new BACoordinator();
		coordinator.cancelOperation(context,participantInformation);
	}

	public BAActivityContext getContext() {
		return (BAActivityContext)context;
	}

	public BusinessActivityCallBack getCallBack() {
		return context.getCallBack();
	}

	public void setCallBack(BusinessActivityCallBack callBack) {
		callBack.setBusinessActivity(this);
		this.context.setCallBack(callBack);
	}

	public int getStatus() {
		return context.getStatus();
	}

	public Iterator getAllParticipants() {
		return context.getAllParticipants();
	}

	public BAParticipantInformation getParticipant(String participantID) {
		
		return context.getParticipant(participantID);
	}

	public void addParticipantActivity(AtomicBusinessActivity businessActivity) {
		//TODO
	}

}
