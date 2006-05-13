/**
 * BankOneSoapBindingImpl.java
 */

package bankone;

import java.rmi.RemoteException;

import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.apache.kandula.geronimo.Bridge;

public class BankOneSoapBindingImpl implements BankOne {

	TransactionManager tm = Bridge.getInstance().getTM();

	public double getBalance(int accountNo) throws RemoteException {
		System.out.println("[BankOne] getBalance(" + accountNo + ")");
		try {
			if (tm.getStatus() == Status.STATUS_NO_TRANSACTION)
				return BankOneDBMS.getInstance().getBalance(accountNo);
			BankOneDBMS conn = getConnection();
			double balance = conn.getBalance(accountNo);
			closeConnection(conn);
			return balance;
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
	}

	public double debit(int accountNo, double amount) throws RemoteException {
		System.out.println("[BankOne] debit(" + accountNo + ", " + amount + ")");
		try {
			BankOneDBMS conn = getConnection();
			double balance = conn.debit(accountNo, amount);
			closeConnection(conn);
			return balance;
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
	}

	public double credit(int accountNo, double amount) throws RemoteException {
		System.out.println("[BankOne] credit(" + accountNo + ", " + amount
				+ ")");
		try {
			BankOneDBMS conn = getConnection();
			double balance = conn.credit(accountNo, amount);
			closeConnection(conn);
			return balance;
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
	}

	private BankOneDBMS getConnection() throws IllegalStateException,
			RollbackException, SystemException {
		BankOneDBMS conn = BankOneDBMS.getInstance();
		tm.getTransaction().enlistResource(conn);
		return conn;
	}

	private void closeConnection(BankOneDBMS conn)
			throws IllegalStateException, SystemException {
		tm.getTransaction().delistResource(conn, XAResource.TMSUSPEND);
	}
}