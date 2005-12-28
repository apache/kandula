/*
 * Created on Dec 26, 2005
 *
 */
package org.apache.ws.transaction.j2ee;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;

import org.apache.geronimo.transaction.manager.TransactionManagerImpl;
import org.apache.ws.transaction.coordinator.CoordinationContext;
import org.apache.ws.transaction.wscoor.Expires;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class Bridge {

	private static Bridge instance = new Bridge();

	private static final int TIMEOUT = 60 * 60;

	public static Bridge getInstance() {
		return instance;
	}

	private Map inM = new HashMap();

	TransactionManagerImpl tm;

	private Bridge() {
		try {
			tm = new TransactionManagerImpl(TIMEOUT, null, null);
		} catch (XAException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public TransactionManager getTM() {
		return tm;
	}

	public synchronized Transaction mediate(CoordinationContext ctx)
			throws RemoteException {
		String id = ctx.getIdentifier().toString();
		Transaction tx = (Transaction) inM.get(id);
		if (tx != null)
			return tx;
		Expires expires = ctx.getExpires();
		long timeout = 0;
		if (expires != null)
			timeout = expires.get_value().longValue();

		try {
			tm.begin(timeout);
			tx = tm.suspend();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		new Mediator(tx, ctx);

		inM.put(id, tx);
		return tx;
	}

	public synchronized CoordinationContext mediate(Transaction tx) {
		return null;

	}

	public void forget(String id) {
		inM.remove(id);
	}

	public boolean isMapped(String id) {
		return inM.containsKey(id);
	}
}