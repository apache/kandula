/*
 * Created on Dec 27, 2005
 *
 */
package org.apache.ws.transaction.j2ee;

import java.rmi.RemoteException;

import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.geronimo.transaction.manager.NamedXAResource;
import org.apache.geronimo.transaction.manager.TransactionManagerImpl;
import org.apache.ws.transaction.coordinator.CoordinationContext;
import org.apache.ws.transaction.coordinator.ParticipantService;
import org.apache.ws.transaction.coordinator.at.ATCoordinator;
import org.apache.ws.transaction.coordinator.at.CoordinatorStub;
import org.apache.ws.transaction.wsat.Notification;
import org.apache.ws.transaction.wsat.ParticipantPortType;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class Mediator implements ParticipantPortType, NamedXAResource {

	private int timeout = Integer.MAX_VALUE;

	private String id;

	private Transaction tx;

	private EndpointReference c;

	private Bridge bridge = Bridge.getInstance();

	private TransactionManagerImpl tm = (TransactionManagerImpl) bridge.getTM();

	public Mediator(Transaction tx, CoordinationContext ctx)
			throws RemoteException {
		id = ctx.getIdentifier().toString();
		this.tx = tx;
		EndpointReference epr = ParticipantService.getInstance().getParticipantService(
			this);
		c = ctx.register(ATCoordinator.PROTOCOL_ID_DURABLE_2PC, epr);
		try {
			tx.enlistResource(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void commit(Xid arg0, boolean arg1) throws XAException {
		if (bridge.isMapped(id))
			try {
				new CoordinatorStub(c).committedOperation(null);
			} catch (Exception e) {
			}
	}

	public void end(Xid arg0, int arg1) throws XAException {
	}

	public void forget(Xid arg0) throws XAException {
	}

	public String getName() {
		return getClass().getName();
	}

	public int getTransactionTimeout() throws XAException {
		return timeout;
	}

	public boolean isSameRM(XAResource arg0) throws XAException {
		return this == arg0;
	}

	public int prepare(Xid arg0) throws XAException {
		if (bridge.isMapped(id)) {
			try {
				new CoordinatorStub(c).abortedOperation(null);
			} catch (Exception e) {
			}
			throw new XAException();
		} else
			return XAResource.XA_RDONLY;
	}

	public Xid[] recover(int arg0) throws XAException {
		return null;
	}

	public void rollback(Xid arg0) throws XAException {
		if (bridge.isMapped(id))
			try {
				new CoordinatorStub(c).abortedOperation(null);
			} catch (Exception e) {
			}
	}

	public boolean setTransactionTimeout(int arg0) throws XAException {
		timeout = arg0;
		return false;
	}

	public void start(Xid arg0, int arg1) throws XAException {
	}

	public synchronized void commitOperation(Notification parameters)
			throws RemoteException {
		done();
		try {
			tm.commit(tx, false);
			new CoordinatorStub(c).committedOperation(null);
		} catch (RemoteException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}

	private void done() {
		ParticipantService.getInstance().forget(this);
	}

	public synchronized void prepareOperation(Notification parameters)
			throws RemoteException {
		try {
			bridge.forget(id);
			CoordinatorStub p = new CoordinatorStub(c);
			int status = tx.getStatus();
			switch (status) {
			case Status.STATUS_ACTIVE:
				try {
					if (tm.prepare(tx) == XAResource.XA_RDONLY) {
						done();
						p.readOnlyOperation(null);
					} else
						p.preparedOperation(null);
					return;
				} catch (XAException e) {
					done();
					p.abortedOperation(null);
					return;
				}

			case Status.STATUS_COMMITTED:
			case Status.STATUS_COMMITTING:
				p.committedOperation(null);
				return;

			case Status.STATUS_MARKED_ROLLBACK:
				done();
				p.abortedOperation(null);
				tx.rollback();
				return;

			case Status.STATUS_ROLLEDBACK:
			case Status.STATUS_ROLLING_BACK:
				done();
				p.abortedOperation(null);
				return;

			case Status.STATUS_PREPARED:
				p.preparedOperation(null);
				return;

			case Status.STATUS_PREPARING:
				return;

			case Status.STATUS_NO_TRANSACTION:
			case Status.STATUS_UNKNOWN:
				done();
				p.abortedOperation(null);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}

	public synchronized void rollbackOperation(Notification parameters)
			throws RemoteException {
		bridge.forget(id);
		done();
		try {
			tx.rollback();
			new CoordinatorStub(c).abortedOperation(null);
		} catch (RemoteException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}

}