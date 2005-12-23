
import java.rmi.RemoteException;

import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.ws.transaction.participant.j2ee.TransactionManagerGlue;
import org.apache.ws.transaction.participant.j2ee.TransactionManagerGlueFactory;

public class InteropServiceSoapBindingImpl implements InteropService_PortType {
	TransactionManagerGlue tmGlue = null;

	TransactionManager tmj2ee = null;

	org.apache.ws.transaction.participant.standalone.TransactionManager tm = null;

	public InteropServiceSoapBindingImpl() {
		tmGlue = TransactionManagerGlueFactory.getInstance()
				.getTransactionManagerGlue();
		tmj2ee = tmGlue.getTransactionManager();
		tm = org.apache.ws.transaction.participant.standalone.TransactionManager
				.getInstance();
	}

	public java.lang.String completionCommit(java.lang.String coord)
			throws java.rmi.RemoteException {
		try {
			EndpointReference epr = new EndpointReference(coord);
			tm.begin(epr);
			tm.commit();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
		return null;
	}

	public java.lang.String completionRollback(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}
	

	public java.lang.String commit(java.lang.String part)
			throws java.rmi.RemoteException {
		try {
			Transaction tx = tmj2ee.getTransaction();
			System.out.println("[InteropServiceSoapBindingImpl] commit() "
					+ TxStatus.getStatusName(tmj2ee.getStatus()));
			if (tmj2ee.getStatus() == Status.STATUS_ACTIVE) {
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

	public java.lang.String rollback(java.lang.String part)
			throws java.rmi.RemoteException {
		try {
			Transaction tx = tmj2ee.getTransaction();
			System.out.println("[InteropServiceSoapBindingImpl] rollback() "
					+ TxStatus.getStatusName(tmj2ee.getStatus()));
			if (tmj2ee.getStatus() == Status.STATUS_ACTIVE) {
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

	public java.lang.String phase2Rollback(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String readonly(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String volatileAndDurable(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String earlyReadonly(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String earlyAborted(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String replayAbort(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String replayCommit(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String retryPreparedCommit(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String retryPreparedAbort(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String retryCommit(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String retryReplay(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String preparedAfterTimeout(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String lostCommitted(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String participantClosed(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String participantCompensated(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String participantCanceled(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String participantExit(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String participantFault(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String participantCompensationFault(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

	public java.lang.String invalidProtocol(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

}