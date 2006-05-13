/*
 * Created on May 10, 2006
 *
 */
package bankone;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.geronimo.transaction.manager.NamedXAResource;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class BankOneDBMS implements NamedXAResource {
	private static BankOneDBMS instance;

	private boolean locked;

	private Xid tx;

	private double account[];

	private double cache[];

	private Map store;

	protected BankOneDBMS() {
		this.tx = null;
		this.cache = null;
		this.account = new double[2];
		this.account[0] = 0.0;
		this.account[1] = 0.0;
		locked = false;
		store = Collections.synchronizedMap(new HashMap());
	}

	public static BankOneDBMS getInstance() {
		if (instance == null)
			instance = new BankOneDBMS();
		return instance;
	}

	public double getBalance(int accountNo) {
		if (cache == null)
			return this.account[accountNo];
		return cache[accountNo];
	}

	public double debit(int accountNo, double amount) {
		if (cache == null)
			throw new IllegalStateException("transaction required.");
		return cache[accountNo] -= amount;
	}

	public double credit(int accountNo, double amount) {
		if (cache == null)
			throw new IllegalStateException("transaction required.");
		return cache[accountNo] += amount;
	}

	public void commit(Xid arg0, boolean arg1) throws XAException {
		System.out.println("[BankOneDBMS] commit");
		if (!locked)
			throw new XAException();
		double[] _cache = (double[]) store.get(arg0);
		this.account[0] = _cache[0];
		this.account[1] = _cache[1];
		locked = false;
		store.remove(arg0);
	}

	public void end(Xid arg0, int arg1) throws XAException {
		System.out.println("[BankOneDBMS] end");
		cache = null;
	}

	public void forget(Xid arg0) throws XAException {
		System.out.println("[BankOneDBMS] forget");
		store.remove(arg0);
	}

	public int getTransactionTimeout() throws XAException {
		return 0;
	}

	public boolean isSameRM(XAResource arg0) throws XAException {
		System.out.println("[BankOneDBMS] isSameRM");
		return arg0 == this;
	}

	public int prepare(Xid arg0) throws XAException {
		System.out.println("[BankOneDBMS] prepare");
		locked = true;
		if (tx.equals(arg0))
			return XAResource.XA_OK;
		locked = false;
		throw new XAException();
	}

	public Xid[] recover(int arg0) throws XAException {
		return null;
	}

	public void rollback(Xid arg0) throws XAException {
		System.out.println("[BankOneDBMS] rollback");
		if (tx.equals(arg0))
			locked = false;
		store.remove(arg0);
	}

	public boolean setTransactionTimeout(int arg0) throws XAException {
		return false;
	}

	public void start(Xid arg0, int arg1) throws XAException {
		System.out.println("[BankOneDBMS] start");
		if (locked)
			throw new XAException();
		this.tx = arg0;
		cache = (double[]) store.get(arg0);
		if (cache == null) {
			cache = new double[2];
			this.cache[0] = this.account[0];
			this.cache[1] = this.account[1];
			store.put(arg0, cache);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.transaction.manager.NamedXAResource#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return "BankOneDBMS";
	}

}