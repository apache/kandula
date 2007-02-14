package org.apache.kandula.participant.at;

import org.apache.kandula.context.impl.ParticipantContext;
import org.apache.kandula.participant.KandulaResource;

public abstract class KandulaAtomicResource implements KandulaResource{

	protected ParticipantContext context=null;
	
	public void init(ParticipantContext context)
	{
		this.context = context;
	}
	public abstract boolean commit();

	public abstract void rollback();

	public abstract Vote prepare();
}