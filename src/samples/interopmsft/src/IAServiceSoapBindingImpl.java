
import java.net.URL;
import java.rmi.RemoteException;

import org.apache.kandula.coordinator.at.TransactionManagerImpl;

/**
 * IAServiceSoapBindingImpl.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.3 Oct 05, 2005
 * (05:23:37 EDT) WSDL2Java emitter.
 */

public class IAServiceSoapBindingImpl implements IAService_PortType {
	TransactionManagerImpl wstm = TransactionManagerImpl.getInstance();

	public final String TRANSACTIONS_SERVICE_INDIGO = "http://131.107.72.15/Transactions_Service_Indigo/TransactionalService.svc";

	//public final String TRANSACTIONS_SERVICE = TRANSACTIONS_SERVICE_INDIGO;
	
//	public final String TRANSACTIONS_SERVICE = "http://localhost:8081/axis/services/CustomTransactionBinding_ITransactionalService";
	
	public final String TRANSACTIONS_SERVICE = "http://localhost:8082/Transactions_Service_Indigo/TransactionalService.svc";

	public void testCommit() throws RemoteException {
		try {
			ITransactionalService svc = new TransactionalServiceLocator().getCustomTransactionBinding_ITransactionalService(new URL(
					TRANSACTIONS_SERVICE));
			wstm.begin();
			System.out.println("[IAServiceSoapBindingImpl] test");
			svc.commit();
			wstm.commit();
		} catch (Exception e) {
			if (e instanceof RemoteException)
				throw (RemoteException) e;
			else
				throw new RemoteException(e.getMessage());
		}
	}

	public void testRollback() throws java.rmi.RemoteException {
	}

	public void testAsyncRollback() throws java.rmi.RemoteException {
	}

}