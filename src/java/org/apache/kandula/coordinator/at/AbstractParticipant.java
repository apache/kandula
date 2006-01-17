/*
 * Created on Dec 30, 2005
 *
 */
package org.apache.kandula.coordinator.at;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.xml.soap.Name;

import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.kandula.coordinator.Callback;
import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.coordinator.CoordinationService;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.coordinator.TimedOutException;
import org.apache.kandula.wsat.CoordinatorPortType;
import org.apache.kandula.wsat.Notification;
import org.apache.kandula.wsat.ParticipantPortType;
import org.apache.kandula.wscoor.Expires;

/**
 * @author Dasarath Weeratunge
 *  
 */
public abstract class AbstractParticipant implements ParticipantPortType,
		Callback {

	private String id;

	private EndpointReference epr;

	private static Timer timer = new Timer();

	public static final int RETRY_DELAY_MILLIS = 3 * 1000;

	private EndpointReference eprOfCoordinator;

	protected abstract int prepare() throws XAException;

	protected abstract void commit() throws XAException;

	protected abstract void rollback() throws XAException;

	protected abstract void forget();

	protected abstract int getStatus();

	protected AbstractParticipant() {
		id = "uuid:" + UUIDGenFactory.getUUIDGen().nextUUID();
	}

	public String getID() {
		return id;
	}

	protected void register(boolean durable, CoordinationContext ctx)
			throws RemoteException {
		long timeout = 0;
		Expires ex = ctx.getExpires();
		if (ex != null)
			timeout = ex.get_value().longValue();
		epr = CoordinationService.getInstance().getParticipantService(this,
			timeout);
		eprOfCoordinator = ctx.register(
			durable ? ATCoordinator.PROTOCOL_ID_DURABLE_2PC
					: ATCoordinator.PROTOCOL_ID_VOLATILE_2PC, epr);
	}

	protected CoordinatorPortType getCoordinator() {
		try {
			return new CoordinatorStub(this, eprOfCoordinator);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public synchronized void prepareOperation(Notification parameters)
			throws RemoteException {

		switch (getStatus()) {
		case AT2PCStatus.NONE:
			getCoordinator().abortedOperation(null);
			return;

		case AT2PCStatus.ACTIVE:
			final CoordinatorPortType p = getCoordinator();
			try {
				forget();
				if (prepare() == XAResource.XA_RDONLY)
					p.readOnlyOperation(null);
				else {
					p.preparedOperation(null);
					timer.schedule(new TimerTask() {
						public void run() {
							switch (getStatus()) {
							case AT2PCStatus.NONE:
							case AT2PCStatus.ACTIVE:
							case AT2PCStatus.PREPARING:
							case AT2PCStatus.ABORTING:
							case AT2PCStatus.COMMITTING:
								cancel();
								return;

							case AT2PCStatus.PREPARED:
								try {
									p.preparedOperation(null);
								} catch (RemoteException e) {
									// TODO:
									// identify wscoor:InvalidState Soap fault
									// and stop
									e.printStackTrace();
								}
							}
						}
					}, RETRY_DELAY_MILLIS, RETRY_DELAY_MILLIS);
				}
			} catch (XAException e) {
				p.abortedOperation(null);
			}
			return;

		case AT2PCStatus.PREPARING:
			return;

		case AT2PCStatus.PREPARED:
			getCoordinator().preparedOperation(null);
			return;

		case AT2PCStatus.ABORTING:
			forget();
			getCoordinator().abortedOperation(null);
			return;

		case AT2PCStatus.COMMITTING:
		}
	}

	public synchronized void commitOperation(Notification parameters)
			throws RemoteException {
		switch (getStatus()) {
		case AT2PCStatus.NONE:
			getCoordinator().committedOperation(null);
			return;

		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING:
			try {
				forget();
				rollback();				
				getCoordinator().abortedOperation(null);
			} catch (XAException e) {
				e.printStackTrace();
			}
			return;

		case AT2PCStatus.PREPARED:
			try {
				commit();
				getCoordinator().committedOperation(null);
			} catch (XAException e) {
				e.printStackTrace();
			}
			return;

		case AT2PCStatus.ABORTING:
			throw Coordinator.INVALID_STATE_SOAP_FAULT;

		case AT2PCStatus.COMMITTING:
		}
	}

	public synchronized void rollbackOperation(Notification parameters)
			throws RemoteException {
		switch (getStatus()) {
		case AT2PCStatus.NONE:
			getCoordinator().abortedOperation(null);
			return;

		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING:
		case AT2PCStatus.PREPARED:
			try {
				forget();
				rollback();			
				getCoordinator().abortedOperation(null);
			} catch (XAException e) {
				e.printStackTrace();
			}
			return;

		case AT2PCStatus.ABORTING:
			forget();
			getCoordinator().abortedOperation(null);
			return;

		case AT2PCStatus.COMMITTING:
			throw Coordinator.INVALID_STATE_SOAP_FAULT;
		}
	}

	public synchronized void timeout() throws TimedOutException {
		System.out.println("[AbstractParticipant] timeout "
				+ AT2PCStatus.getStatusName(getStatus()));

		if (getStatus() == AT2PCStatus.NONE)
			return;
		try {
			forget();
			rollback();		
			getCoordinator().abortedOperation(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new TimedOutException();
	}

	public synchronized void onFault(Name code) {
		System.out.println("[AbstractParticipant] onFault: " + code);

		// FIXME:
		try {
			forget();
			rollback();
		} catch (XAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public EndpointReference getEndpointReference() {
		return epr;
	}
}