/*
 * Created on May 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.fabrikam123;



import java.rmi.RemoteException;

import javax.transaction.Status;
import javax.transaction.Transaction;

import org.apache.ws.transaction.participant.j2ee.TransactionManagerGlue;
import org.apache.ws.transaction.participant.j2ee.TransactionManagerGlueFactory;
import org.apache.ws.transaction.participant.standalone.TransactionManager;

import dummy.DummyXAResource;
import dummy.TxStatus;

/**
 * @author root
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ParticipantPortTypeImpl implements ParticipantPortType{

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#completionCommit()
	 */
	public void completionCommit() throws RemoteException {
		TransactionManager tm= TransactionManager.getInstance();
		tm.begin();
		
		tm.commit();
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#completionRollback()
	 */
	public void completionRollback() throws RemoteException {
	    TransactionManager tm= TransactionManager.getInstance();
		tm.begin();
		
		tm.rollback();
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#commit()
	 */
	public void commit() throws RemoteException {
		TransactionManagerGlue tmGlue= null;
		javax.transaction.TransactionManager tm= null;
		tmGlue= TransactionManagerGlueFactory.getInstance().getTransactionManagerGlue();
		tm= tmGlue.getTransactionManager();
		try {
			Transaction tx= tm.getTransaction();
			System.out.println(
				"[ParticipantPortTypeImpl] commit() " + TxStatus.getStatusName(tm.getStatus()));
			if (tm.getStatus() == Status.STATUS_ACTIVE) {
				tx.enlistResource(new DummyXAResource(false, false));
				System.out.println(
					"[InteropServiceSoapBindingImpl] "
						+ tx.toString()
						+ " tx.hashCode= "
						+ tx.hashCode());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			if (e instanceof RemoteException)
				throw (RemoteException)e;
			else
				throw new RemoteException(e.toString());
		}
	}
		
	

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#rollback()
	 */
	public void rollback() throws RemoteException {
		TransactionManagerGlue tmGlue= null;
		javax.transaction.TransactionManager tm= null;
		tmGlue= TransactionManagerGlueFactory.getInstance().getTransactionManagerGlue();
		tm= tmGlue.getTransactionManager();
		try {
			Transaction tx= tm.getTransaction();
			System.out.println(
				"[ParticipantPortTypeImpl] commit() " + TxStatus.getStatusName(tm.getStatus()));
			if (tm.getStatus() == Status.STATUS_ACTIVE) {
				tx.enlistResource(new DummyXAResource(true, false));
				System.out.println(
					"[InteropServiceSoapBindingImpl] "
						+ tx.toString()
						+ " tx.hashCode= "
						+ tx.hashCode());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			if (e instanceof RemoteException)
				throw (RemoteException)e;
			else
				throw new RemoteException(e.toString());
		}
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#phase2Rollback()
	 */
	public void phase2Rollback() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#readonly()
	 */
	public void readonly() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#volatileAndDurable()
	 */
	public void volatileAndDurable() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#earlyReadonly()
	 */
	public void earlyReadonly() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#earlyAborted()
	 */
	public void earlyAborted() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#replayAbort()
	 */
	public void replayAbort() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#replayCommit()
	 */
	public void replayCommit() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#retryPreparedCommit()
	 */
	public void retryPreparedCommit() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#retryPreparedAbort()
	 */
	public void retryPreparedAbort() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#retryCommit()
	 */
	public void retryCommit() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#retryReplay()
	 */
	public void retryReplay() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#preparedAfterTimeout()
	 */
	public void preparedAfterTimeout() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fabrikam123.ParticipantPortType#lostCommitted()
	 */
	public void lostCommitted() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
