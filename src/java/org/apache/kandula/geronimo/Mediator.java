/*
 * Created on Dec 27, 2005
 *
 */
package org.apache.kandula.geronimo;

import java.rmi.RemoteException;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.geronimo.transaction.manager.NamedXAResource;
import org.apache.geronimo.transaction.manager.TransactionManagerImpl;
import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.coordinator.at.AT2PCStatus;
import org.apache.kandula.coordinator.at.AbstractParticipant;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class Mediator extends AbstractParticipant implements NamedXAResource {

	private int timeout = Integer.MAX_VALUE;

	private String id;

	private Transaction tx;

	private static Bridge bridge = Bridge.getInstance();

	private static TransactionManagerImpl tm = (TransactionManagerImpl) bridge.getTM();

	private boolean bridged = true;

	public Mediator(Transaction tx, CoordinationContext ctx)
			throws RemoteException {
		register(true, ctx);
		id = ctx.getIdentifier().toString();
		this.tx = tx;
		try {
			tx.enlistResource(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void commit(Xid arg0, boolean arg1) throws XAException {
		if (bridged) {
			forget();
			try {
				getCoordinator().committedOperation(null);
			} catch (Exception e) {
			}
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

	public boolean isSameRM(XAResource rm) throws XAException {
		return this == rm;
	}

	public int prepare(Xid arg0) throws XAException {
		if (bridged) {
			forget();
			try {
				getCoordinator().abortedOperation(null);
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
		if (bridged) {
			forget();
			try {
				getCoordinator().abortedOperation(null);
			} catch (Exception e) {
			}
		}
	}

	public boolean setTransactionTimeout(int timeout) throws XAException {
		this.timeout = timeout;
		return true;
	}

	public void start(Xid arg0, int arg1) throws XAException {
	}

	public int prepare() throws XAException {
		return tm.prepare(tx);
	}

	public void commit() throws XAException {
		tm.commit(tx, false);
	}

	public void rollback() throws XAException {
		tm.rollback(tx);
	}

	public void forget() {
		if (bridged) {
			bridge.forget(id);
			bridged = false;
		}
	}

	public int getStatus() {
		try {
			if (this.tx == null)
				return Status.STATUS_NO_TRANSACTION;
			
			switch (tx.getStatus()) {
			case Status.STATUS_ACTIVE:
			case Status.STATUS_MARKED_ROLLBACK:
				return AT2PCStatus.ACTIVE;

			case Status.STATUS_PREPARING:
				return AT2PCStatus.PREPARING;

			case Status.STATUS_ROLLING_BACK:
			case Status.STATUS_ROLLEDBACK:
				return AT2PCStatus.ABORTING;

			case Status.STATUS_PREPARED:
				return AT2PCStatus.PREPARED;

			case Status.STATUS_COMMITTING:
			case Status.STATUS_COMMITTED:
				return AT2PCStatus.COMMITTING;

			case Status.STATUS_NO_TRANSACTION:
				return AT2PCStatus.NONE;

			case Status.STATUS_UNKNOWN:
			default:
				throw new RuntimeException();
			}
		} catch (SystemException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}