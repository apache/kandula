/*
 * Created on Dec 30, 2005
 *
 */
package org.apache.ws.transaction.coordinator.at;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.ws.transaction.coordinator.CoordinationContext;
import org.apache.ws.transaction.coordinator.Coordinator;
import org.apache.ws.transaction.coordinator.ParticipantService;
import org.apache.ws.transaction.coordinator.TimedOutException;
import org.apache.ws.transaction.utility.Callback;
import org.apache.ws.transaction.wsat.CoordinatorPortType;
import org.apache.ws.transaction.wsat.Notification;
import org.apache.ws.transaction.wsat.ParticipantPortType;
import org.apache.ws.transaction.wscoor.Expires;

/**
 * @author Dasarath Weeratunge
 *  
 */
public abstract class AbstractParticipant implements ParticipantPortType,
		Callback {

	private static Timer timer = new Timer();

	public static final int RETRY_DELAY_MILLIS = 10 * 1000;

	private EndpointReference eprOfCoordinator;

	protected abstract int prepare() throws XAException;

	protected abstract void commit() throws XAException;

	protected abstract void rollback() throws XAException;

	protected abstract void forget();

	protected abstract int getStatus();

	protected void register(boolean durable, CoordinationContext ctx)
			throws RemoteException {
		long timeout = 0;
		Expires ex = ctx.getExpires();
		if (ex != null)
			timeout = ex.get_value().longValue();
		EndpointReference epr = ParticipantService.getInstance().getParticipantService(
			this, timeout);
		eprOfCoordinator = ctx.register(
			durable ? ATCoordinator.PROTOCOL_ID_DURABLE_2PC
					: ATCoordinator.PROTOCOL_ID_VOLATILE_2PC, epr);
	}

	protected CoordinatorPortType getCoordinator() {
		try {
			return new CoordinatorStub(eprOfCoordinator);
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
				if (prepare() == XAResource.XA_RDONLY) {
					forget();
					p.readOnlyOperation(null);
				} else {
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
									// identify wscoor:InvalidState Soap fault and stop
									e.printStackTrace();								
								}
							}
						}
					}, RETRY_DELAY_MILLIS, RETRY_DELAY_MILLIS);
				}
			} catch (XAException e) {
				forget();
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
				rollback();
				forget();
				getCoordinator().abortedOperation(null);
			} catch (XAException e) {
				e.printStackTrace();
			}
			return;

		case AT2PCStatus.PREPARED:
			try {
				commit();
				forget();
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
				rollback();
				forget();
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
			rollback();
			forget();
			getCoordinator().abortedOperation(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new TimedOutException();
	}
}