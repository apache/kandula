
import java.rmi.RemoteException;

import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.ws.transaction.participant.j2ee.TransactionManagerGlue;
import org.apache.ws.transaction.participant.j2ee.TransactionManagerGlueFactory;

public class InteropServiceSoapBindingImpl implements InteropService_PortType {
	TransactionManagerGlue tmGlue = null;

	TransactionManager tm = null;

	public InteropServiceSoapBindingImpl() {
		tmGlue = TransactionManagerGlueFactory.getInstance()
				.getTransactionManagerGlue();
		tm = tmGlue.getTransactionManager();
	}

	public java.lang.Object completionCommit(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object completionRollback(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object commit(java.lang.Object part)
			throws java.rmi.RemoteException {
		try {
			Transaction tx = tm.getTransaction();
			System.out.println("[InteropServiceSoapBindingImpl] commit() "
					+ TxStatus.getStatusName(tm.getStatus()));
			if (tm.getStatus() == Status.STATUS_ACTIVE) {
				tx.enlistResource(new DummyXAResource(false, false));
				System.out.println("[InteropServiceSoapBindingImpl] "
						+ tx.toString() + " tx.hashCode= " + tx.hashCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof RemoteException)
				throw (RemoteException) e;
			else
				throw new RemoteException(e.toString());
		}

		return null;
	}

	public java.lang.Object rollback(java.lang.Object part)
			throws java.rmi.RemoteException {
		try {
			Transaction tx = tm.getTransaction();
			System.out.println("[InteropServiceSoapBindingImpl] rollback() "
					+ TxStatus.getStatusName(tm.getStatus()));
			if (tm.getStatus() == Status.STATUS_ACTIVE) {
				tx.enlistResource(new DummyXAResource(true, false));
				System.out.println("[InteropServiceSoapBindingImpl] "
						+ tx.toString() + " tx.hashCode= " + tx.hashCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof RemoteException)
				throw (RemoteException) e;
			else
				throw new RemoteException(e.toString());
		}

		return null;
	}

	public java.lang.Object phase2Rollback(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object readonly(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object volatileAndDurable(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object earlyReadonly(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object earlyAborted(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object replayAbort(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object replayCommit(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object retryPreparedCommit(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object retryPreparedAbort(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object retryCommit(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object retryReplay(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object preparedAfterTimeout(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object lostCommitted(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object participantClosed(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object participantCompensated(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object participantCanceled(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object participantExit(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object participantFault(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object participantCompensationFault(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.Object invalidProtocol(java.lang.Object part)
			throws java.rmi.RemoteException {
		return null;
	}

}