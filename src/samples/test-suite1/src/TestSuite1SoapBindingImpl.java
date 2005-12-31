
import java.lang.reflect.Field;
import java.rmi.RemoteException;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import javax.xml.rpc.ServiceException;

import org.apache.geronimo.transaction.manager.NamedXAResource;
import org.apache.ws.transaction.j2ee.Bridge;
import org.apache.ws.transaction.utility.TransactionManagerImpl;

public class TestSuite1SoapBindingImpl implements TestSuite1PortType {
	TransactionManagerImpl wsTm = TransactionManagerImpl.getInstance();

	TransactionManager tm = Bridge.getInstance().getTM();

	public void testReadonlyCommit() throws RemoteException {
		try {
			TestSuite1PortType p = new TestSuite1PortTypeServiceLocator().getTestSuite1();
			wsTm.begin();
			p.justReturnOperation();
			wsTm.commit();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void testReadonlyRollback() throws RemoteException {
		try {
			TestSuite1PortType p = new TestSuite1PortTypeServiceLocator().getTestSuite1();
			wsTm.begin();
			p.justReturnOperation();
			wsTm.rollback();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void testRollback() throws RemoteException {
		try {
			TestSuite1PortType p = new TestSuite1PortTypeServiceLocator().getTestSuite1();
			wsTm.begin();
			p.enlistXA_OKOnPrepareResourceOperation();
			wsTm.rollback();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void testPrepareCommit() throws RemoteException {
		try {
			TestSuite1PortType p = new TestSuite1PortTypeServiceLocator().getTestSuite1();
			wsTm.begin();
			p.enlistXA_OKOnPrepareResourceOperation();
			wsTm.commit();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void testPrepareRollback() throws RemoteException {
		try {
			TestSuite1PortType p = new TestSuite1PortTypeServiceLocator().getTestSuite1();
			wsTm.begin();
			p.enlistXA_OKOnPrepareResourceOperation();
			p.enlistXAExceptionOnPrepareResourceOperation();
			wsTm.commit();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void testEarlyCommit() throws RemoteException {
		try {
			TestSuite1PortType p = new TestSuite1PortTypeServiceLocator().getTestSuite1();
			wsTm.begin();
			p.commitTransactionOperation();
			wsTm.commit();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

	}

	public void testEarlyRollback() throws RemoteException {
		try {
			TestSuite1PortType p = new TestSuite1PortTypeServiceLocator().getTestSuite1();
			wsTm.begin();
			p.rollbackTransactionOperation();
			wsTm.commit();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void testMarkedRollbackCommit() throws RemoteException {
		try {
			TestSuite1PortType p = new TestSuite1PortTypeServiceLocator().getTestSuite1();
			wsTm.begin();
			p.markTransactionForRollbackOperation();
			wsTm.commit();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

	}

	public void testMarkedRollbackRollback() throws RemoteException {
		try {
			TestSuite1PortType p = new TestSuite1PortTypeServiceLocator().getTestSuite1();
			wsTm.begin();
			p.markTransactionForRollbackOperation();
			wsTm.rollback();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

	}

	public void testCommitFailure() throws RemoteException {
		try {
			TestSuite1PortType p = new TestSuite1PortTypeServiceLocator().getTestSuite1();
			wsTm.begin();
			p.enlistXAExceptionOnCommitRollbackResourceOperation();
			wsTm.commit();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void testRollbackFailure() throws RemoteException {
		try {
			TestSuite1PortType p = new TestSuite1PortTypeServiceLocator().getTestSuite1();
			wsTm.begin();
			p.enlistXAExceptionOnCommitRollbackResourceOperation();
			wsTm.rollback();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void justReturnOperation() throws RemoteException {
		try {
			System.out.println("[TestSuite1SoapBindingImpl] "
					+ getStatusName(tm.getStatus()));
		} catch (SystemException e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}

	public void enlistXA_OKOnPrepareResourceOperation() throws RemoteException {
		try {
			System.out.println("[TestSuite1SoapBindingImpl] "
					+ getStatusName(tm.getStatus()));
			tm.getTransaction().enlistResource(new XA_OKOnPrepareXAResource());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}

	public void enlistXAExceptionOnPrepareResourceOperation()
			throws RemoteException {
		try {
			System.out.println("[TestSuite1SoapBindingImpl] "
					+ getStatusName(tm.getStatus()));
			tm.getTransaction().enlistResource(
				new XAExceptionOnPrepareXAResource());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}

	public void markTransactionForRollbackOperation() throws RemoteException {
		try {
			System.out.println("[TestSuite1SoapBindingImpl] "
					+ getStatusName(tm.getStatus()));
			tm.setRollbackOnly();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}

	}

	public void commitTransactionOperation() throws RemoteException {
		try {
			System.out.println("[TestSuite1SoapBindingImpl] "
					+ getStatusName(tm.getStatus()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}

		try {
			tm.commit();
		} catch (Exception e) {
			System.out.println("[TestSuite1SoapBindingImpl] "
					+ e.getClass().getName() + " on commit.");
		}
	}

	public void rollbackTransactionOperation() throws RemoteException {
		try {
			System.out.println("[TestSuite1SoapBindingImpl] "
					+ getStatusName(tm.getStatus()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}

		try {
			tm.rollback();
		} catch (Exception e) {
			System.out.println("[TestSuite1SoapBindingImpl] "
					+ e.getClass().getName() + " on rollback.");
		}
	}

	public void enlistXAExceptionOnCommitRollbackResourceOperation()
			throws RemoteException {
		try {
			System.out.println("[TestSuite1SoapBindingImpl] "
					+ getStatusName(tm.getStatus()));
			tm.getTransaction().enlistResource(
				new XAExceptionOnCommitRollbackXAResource());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}

	private class XAExceptionOnCommitRollbackXAResource extends
			XA_OKOnPrepareXAResource {

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#commit(javax.transaction.xa.Xid,
		 *      boolean)
		 */
		public void commit(Xid arg0, boolean arg1) throws XAException {
			throw new XAException();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#rollback(javax.transaction.xa.Xid)
		 */
		public void rollback(Xid arg0) throws XAException {
			throw new XAException();
		}
	}

	private class XAExceptionOnPrepareXAResource extends XAResourceImpl {

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#prepare(javax.transaction.xa.Xid)
		 */
		public int prepare(Xid arg0) throws XAException {
			// TODO Auto-generated method stub
			throw new XAException();
		}
	}

	private class XA_OKOnPrepareXAResource extends XAResourceImpl {

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#prepare(javax.transaction.xa.Xid)
		 */
		public int prepare(Xid arg0) throws XAException {
			// TODO Auto-generated method stub
			return XAResource.XA_OK;
		}
	}

	private class XAResourceImpl implements NamedXAResource {
		private int timeout = Integer.MAX_VALUE;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.geronimo.transaction.manager.NamedXAResource#getName()
		 */
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#commit(javax.transaction.xa.Xid,
		 *      boolean)
		 */
		public void commit(Xid arg0, boolean arg1) throws XAException {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#end(javax.transaction.xa.Xid,
		 *      int)
		 */
		public void end(Xid arg0, int arg1) throws XAException {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#forget(javax.transaction.xa.Xid)
		 */
		public void forget(Xid arg0) throws XAException {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#getTransactionTimeout()
		 */
		public int getTransactionTimeout() throws XAException {
			return timeout;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#isSameRM(javax.transaction.xa.XAResource)
		 */
		public boolean isSameRM(XAResource arg0) throws XAException {
			return this == arg0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#prepare(javax.transaction.xa.Xid)
		 */
		public int prepare(Xid arg0) throws XAException {
			// TODO Auto-generated method stub
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#recover(int)
		 */
		public Xid[] recover(int arg0) throws XAException {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#rollback(javax.transaction.xa.Xid)
		 */
		public void rollback(Xid arg0) throws XAException {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#setTransactionTimeout(int)
		 */
		public boolean setTransactionTimeout(int arg0) throws XAException {
			timeout = arg0;
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.transaction.xa.XAResource#start(javax.transaction.xa.Xid,
		 *      int)
		 */
		public void start(Xid arg0, int arg1) throws XAException {
			// TODO Auto-generated method stub

		}

	}

	private static Field[] flds = Status.class.getDeclaredFields();

	public static String getStatusName(int status) {
		String statusName = null;

		try {
			for (int i = 0; i < flds.length; i++) {
				if (flds[i].getInt(null) == status)
					statusName = flds[i].getName();
			}
		} catch (Exception e) {
			statusName = "invalid status value!";
		}
		return statusName;
	}

}