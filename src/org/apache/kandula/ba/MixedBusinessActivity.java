package org.apache.kandula.ba;

import java.util.ArrayList;

import org.apache.kandula.Constants;
import org.apache.kandula.context.impl.BAActivityContext;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.coordinator.ba.BACoordinator;
import org.apache.kandula.coordinator.ba.BAParticipantInformation;
import org.apache.kandula.faults.AbstractKandulaException;

public class MixedBusinessActivity extends BusinessActivity {
	
	private ArrayList closeParticipantsList;
	
	private ArrayList compensateParticipantsList;

	public MixedBusinessActivity(long expires ) throws AbstractKandulaException {
		closeParticipantsList = new ArrayList();
		compensateParticipantsList = new ArrayList();
		Coordinator coordinator = new Coordinator();
		context = (BAActivityContext)coordinator.createCoordinationContext(Constants.WS_BA_ATOMIC,
				expires);
	}
	
	public void addParticipantToCloseList(BAParticipantInformation participantInformation)
	{
		closeParticipantsList.add(participantInformation);
	}
	
	public void addParticipantToCompensateList(BAParticipantInformation participantInformation)
	{
		if (participantInformation!=null)
		compensateParticipantsList.add(participantInformation);
	}
	
	public void addParticipantToCloseList(String participantID)
	{
		BAParticipantInformation participantInformation = context.getParticipant(participantID);
		closeParticipantsList.add(participantInformation);
	}
	
	public void addParticipantToCompensateList(String participantID)
	{
		BAParticipantInformation participantInformation = context.getParticipant(participantID);
		compensateParticipantsList.add(participantInformation);
	}
	
	public void finalizeActivity() throws AbstractKandulaException
	{
		BACoordinator coordinator =  new BACoordinator();
		coordinator.finalizeMixedOutcomeActivity(context,closeParticipantsList,compensateParticipantsList);
	}
}
