package org.apache.kandula.participant.ba;

import org.apache.kandula.context.impl.ParticipantContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.participant.KandulaResource;

public abstract class KandulaBusinessActivityResource implements KandulaResource{

	protected ParticipantContext context= null;
	
	public void init(ParticipantContext context)
	{
		this.context =context;
	}
	public abstract boolean compensate();
	
	public abstract void complete() throws AbstractKandulaException;

    public abstract void close();
    
    public abstract void cancel();
    
	public void exit() throws AbstractKandulaException
	{
		BAParticipantTransactionSentCoordinator coordinator = new BAParticipantTransactionSentCoordinator();
		coordinator.exit(context);
	}

}