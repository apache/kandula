package org.apache.kandula.ba;


import org.apache.kandula.Constants;
import org.apache.kandula.context.impl.BAActivityContext;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.coordinator.ba.BACoordinator;
import org.apache.kandula.faults.AbstractKandulaException;

public class AtomicBusinessActivity extends BusinessActivity{
	
	public AtomicBusinessActivity(long expires ) throws AbstractKandulaException {
		Coordinator coordinator = new Coordinator();
		context = (BAActivityContext)coordinator.createCoordinationContext(Constants.WS_BA_ATOMIC,
				expires);
	}
	
	public void closeActivity() throws AbstractKandulaException
	{
		BACoordinator atomicBACoordinator = new BACoordinator();
		atomicBACoordinator.closeAllOperation(context);
	}
	
	public void compensateActivity() throws AbstractKandulaException
	{
		BACoordinator atomicBACoordinator = new BACoordinator();
		atomicBACoordinator.compensateAllOperation(context);
	}
}
