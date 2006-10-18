/*
 * Created on Dec 26, 2005
 *
 */
package org.apache.kandula.geronimo;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;

import org.apache.geronimo.transaction.manager.TransactionManagerImpl;
import org.apache.geronimo.transaction.manager.XidFactoryImpl;
import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.wscoor.Expires;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class Bridge {

	private static Bridge instance = new Bridge();

	private static final int TIMEOUT_S = 60 * 60;

	public static Bridge getInstance() {
		return instance;
	}

	private Map inM = new HashMap();

	TransactionManagerImpl tm;

	private Bridge() {
		try {
			tm = new TransactionManagerImpl(TIMEOUT_S, new XidFactoryImpl(),
					null, null);
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
}