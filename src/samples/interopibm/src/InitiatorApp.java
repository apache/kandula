
import java.net.URL;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.ws.transaction.coordinator.ActivationStub;
import org.apache.ws.transaction.coordinator.CoordinationContext;
import org.apache.ws.transaction.coordinator.at.ATCoordinator;
import org.apache.ws.transaction.coordinator.at.CompletionCoordinatorStub;
import org.apache.ws.transaction.utility.TCPSnifferHelper;

/*
 * Created on Dec 29, 2005
 *
 */

/**
 * @author Dasarath Weeratunge
 *  
 */
public class InitiatorApp {
	
	private final String IBM_INTEROP_SERVICE = "http://wsi.alphaworks.ibm.com:8080/wstx/services/InteropService";
	
	private final String KANDULA_INTEROP_SERVICE = "http://localhost:8081/axis/services/InteropService";

	private final String KANDULA_ACTIVATION_SERVICE = "http://localhost:8081/axis/services/Activation";
	
	private String eprOfInteropService = KANDULA_INTEROP_SERVICE;

	private InteropService_PortType getInteropService() throws Exception {
		return new InteropServiceServiceLocator().getInteropService(new URL(
				TCPSnifferHelper.redirect(eprOfInteropService)));
	}

	public void testCompletionCommit() throws Exception {
		getInteropService().completionCommit(KANDULA_ACTIVATION_SERVICE);
	}

	public void testCompletionRollback() throws Exception {
		getInteropService().completionRollback(KANDULA_ACTIVATION_SERVICE);
	}

	public void testCommit() throws Exception {
		begin();
		getInteropService().commit(null);
		commit();
	}

	public void testRollback() throws Exception {
		begin();
		getInteropService().rollback(null);
		rollback();
	}

	public void testPhase2Rollback() throws Exception {
		begin();
		getInteropService().phase2Rollback(null);
		commit();
	}

	public void testReadonly() throws Exception {
		begin();
		getInteropService().readonly(null);
		commit();
	}

	public void testVolatileAndDurable() throws Exception {
		begin();
		getInteropService().volatileAndDurable(null);
		commit();
	}

	public void testEarlyReadonly() throws Exception {
		begin();
		getInteropService().earlyReadonly(null);
		commit();
	}

	public void testEarlyAborted() throws Exception {
		begin();
		getInteropService().earlyAborted(null);
	}

	public void testReplayAbort() throws Exception {
		begin();
		getInteropService().replayAbort(null);
		commit();
	}

	public void testReplayCommit() throws Exception {
		begin();
		getInteropService().replayCommit(null);
		commit();
	}

	public void testRetryPreparedCommit() throws Exception {
		begin();
		getInteropService().retryPreparedCommit(null);
		commit();
	}

	public void testRetryPreparedAbort() throws Exception {
		begin();
		getInteropService().retryPreparedAbort(null);
		commit();
	}

	public void testRetryCommit() throws Exception {
		begin();
		getInteropService().retryCommit(null);
		commit();
	}

	public void testRetryReplay() throws Exception {
		begin();
		getInteropService().retryReplay(null);
		commit();
	}

	public void testPreparedAfterTimeout() throws Exception {
		begin();
		getInteropService().preparedAfterTimeout(null);
		commit();
	}

	public void testLostCommitted() throws Exception {
		begin();
		getInteropService().lostCommitted(null);
		commit();
	}

	public static void main(String[] args) throws Exception {
		new InitiatorApp().testPreparedAfterTimeout();
	}

	EndpointReference cps;

	CoordinationContext ctx;

	public void begin() throws Exception {
		ActivationStub stub = new ActivationStub(new EndpointReference(
				"http://localhost:8081/axis/services/Activation"));
		ctx = stub.createCoordinationContext(ATCoordinator.COORDINATION_TYPE_ID);
		cps = ctx.register(ATCoordinator.PROTOCOL_ID_COMPLETION,
			new EndpointReference(
					"http://localhost:8081/axis/services/CompletionInitiator"));
		SetCoordCtxHandler.setCtx(ctx);
	}

	public void commit() throws Exception {
		SetCoordCtxHandler.setCtx(null);
		new CompletionCoordinatorStub(cps).commitOperation(null);
	}

	public void rollback() throws Exception {
		SetCoordCtxHandler.setCtx(null);
		new CompletionCoordinatorStub(cps).rollbackOperation(null);
	}
}