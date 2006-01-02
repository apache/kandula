
import java.rmi.RemoteException;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.ws.transaction.coordinator.at.AT2PCStatus;
import org.apache.ws.transaction.coordinator.at.AbstractParticipant;
import org.apache.ws.transaction.coordinator.at.TransactionImpl;
import org.apache.ws.transaction.coordinator.at.TransactionManagerImpl;

/**
 * InteropServiceSoapBindingImpl.java
 *  
 */

public class InteropServiceSoapBindingImpl implements InteropService_PortType {

	TransactionManagerImpl tm = TransactionManagerImpl.getInstance();

	public java.lang.String commit(java.lang.String part)
			throws java.rmi.RemoteException {

		tm.getTransaction().enlistParticipant(true, new XA_OKParticipant());

		return null;
	}

	public java.lang.String rollback(java.lang.String part)
			throws java.rmi.RemoteException {

		tm.getTransaction().enlistParticipant(true, new XA_OKParticipant());

		return null;
	}

	private class XA_OKParticipant extends AbstractParticipant {
		int status = AT2PCStatus.ACTIVE;

		protected int prepare() throws XAException {
			status = AT2PCStatus.PREPARED;
			return XAResource.XA_OK;
		}

		protected void commit() throws XAException {
			if (status == AT2PCStatus.PREPARED)
				status = AT2PCStatus.NONE;
			else
				throw new XAException();
		}

		protected void rollback() throws XAException {
			if (status == AT2PCStatus.ACTIVE || status == AT2PCStatus.PREPARED)
				status = AT2PCStatus.NONE;
			else
				throw new XAException();
		}

		protected void forget() {
			status = AT2PCStatus.NONE;
		}

		protected int getStatus() {
			return status;
		}
	}

	private class XAExceptionParticipant extends XA_OKParticipant {

		protected int prepare() throws XAException {
			throw new XAException();
		}
	}

	public java.lang.String phase2Rollback(java.lang.String part)
			throws java.rmi.RemoteException {

		TransactionImpl tx = tm.getTransaction();

		tx.enlistParticipant(false, new XA_OKParticipant());
		tx.enlistParticipant(true, new XAExceptionParticipant());

		return null;
	}

	private class ReadonlyParticipant extends XA_OKParticipant {

		protected int prepare() throws XAException {
			return XAResource.XA_RDONLY;
		}
	}

	public java.lang.String readonly(java.lang.String part)
			throws java.rmi.RemoteException {

		TransactionImpl tx = tm.getTransaction();

		tx.enlistParticipant(true, new ReadonlyParticipant());
		tx.enlistParticipant(true, new XA_OKParticipant());

		return null;
	}

	public java.lang.String volatileAndDurable(java.lang.String part)
			throws java.rmi.RemoteException {

		final TransactionImpl tx = tm.getTransaction();

		tx.enlistParticipant(false, new XA_OKParticipant() {

			protected int prepare() throws XAException {
				try {

					tx.enlistParticipant(true, new XA_OKParticipant());

				} catch (RemoteException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}

				return XAResource.XA_RDONLY;
			}

		});

		return null;
	}

	private class NotificationSenderParticipant extends XA_OKParticipant {

		public void sendReadonly() throws RemoteException {
			getCoordinator().readOnlyOperation(null);
			status = AT2PCStatus.NONE;
		}

		public void sendAborted() throws RemoteException {
			getCoordinator().abortedOperation(null);
			status = AT2PCStatus.NONE;
		}

	}

	public java.lang.String earlyReadonly(java.lang.String part)
			throws java.rmi.RemoteException {

		TransactionImpl tx = tm.getTransaction();

		NotificationSenderParticipant foo = new NotificationSenderParticipant();
		tx.enlistParticipant(false, foo);
		tx.enlistParticipant(true, new XA_OKParticipant());
		foo.sendReadonly();

		return null;
	}

	public java.lang.String earlyAborted(java.lang.String part)
			throws java.rmi.RemoteException {

		TransactionImpl tx = tm.getTransaction();

		NotificationSenderParticipant foo = new NotificationSenderParticipant();
		tx.enlistParticipant(false, foo);
		tx.enlistParticipant(true, new XA_OKParticipant());
		foo.sendAborted();

		return null;
	}

	public java.lang.String replayAbort(java.lang.String part)
			throws java.rmi.RemoteException {

		tm.getTransaction().enlistParticipant(true, new XA_OKParticipant() {
			protected int prepare() throws XAException {

				try {
					getCoordinator().replayOperation(null);
				} catch (RemoteException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}

				return XAResource.XA_OK;

			}
		});

		return null;
	}

	public java.lang.String replayCommit(java.lang.String part)
			throws java.rmi.RemoteException {

		tm.getTransaction().enlistParticipant(true, new XA_OKParticipant() {
			protected void commit() throws XAException {

				try {
					getCoordinator().replayOperation(null);
				} catch (RemoteException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		});

		return null;
	}

	public java.lang.String retryPreparedCommit(java.lang.String part)
			throws java.rmi.RemoteException {

		tm.getTransaction().enlistParticipant(true, new XA_OKParticipant());

		return null;
	}

	public java.lang.String retryPreparedAbort(java.lang.String part)
			throws java.rmi.RemoteException {

		tm.getTransaction().enlistParticipant(true, new XA_OKParticipant());

		return null;
	}

	public java.lang.String retryCommit(java.lang.String part)
			throws java.rmi.RemoteException {

		tm.getTransaction().enlistParticipant(true, new XA_OKParticipant());

		return null;
	}

	public java.lang.String retryReplay(java.lang.String part)
			throws java.rmi.RemoteException {

		tm.getTransaction().enlistParticipant(true, new XA_OKParticipant());

		return null;
	}

	public java.lang.String preparedAfterTimeout(java.lang.String part)
			throws java.rmi.RemoteException {

		TransactionImpl tx = tm.getTransaction();

		tx.enlistParticipant(false, new XA_OKParticipant());
		tx.enlistParticipant(true, new XA_OKParticipant());

		return null;
	}

	public java.lang.String lostCommitted(java.lang.String part)
			throws java.rmi.RemoteException {

		tm.getTransaction().enlistParticipant(true, new XA_OKParticipant());

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

	public java.lang.String completionCommit(java.lang.String part)
			throws java.rmi.RemoteException {

		try {
			tm.begin(new EndpointReference(part));
		} catch (MalformedURIException e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}

		tm.commit();

		return null;
	}

	public java.lang.String completionRollback(java.lang.String part)
			throws java.rmi.RemoteException {

		try {
			tm.begin(new EndpointReference(part));
		} catch (MalformedURIException e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}

		tm.rollback();

		return null;
	}

	public java.lang.String invalidProtocol(java.lang.String part)
			throws java.rmi.RemoteException {
		return null;
	}

}