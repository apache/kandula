/*
 * Copyright  2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
import java.rmi.RemoteException;

import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.ws.transaction.participant.j2ee.TransactionManagerGlue;
import org.apache.ws.transaction.participant.j2ee.TransactionManagerGlueFactory;

public class InteropServiceSoapBindingImpl implements InteropPortTypeRPC {
	TransactionManagerGlue tmGlue= null;
	TransactionManager tm= null;

	public InteropServiceSoapBindingImpl() {
		tmGlue= TransactionManagerGlueFactory.getInstance().getTransactionManagerGlue();
		tm= tmGlue.getTransactionManager();
	}

	public void commit() throws java.rmi.RemoteException {
		try {
			Transaction tx= tm.getTransaction();
			System.out.println(
				"[InteropServiceSoapBindingImpl] commit() " + TxStatus.getStatusName(tm.getStatus()));
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

	public void rollback() throws java.rmi.RemoteException {
		try {
			Transaction tx= tm.getTransaction();
			System.out.println(
				"[InteropServiceSoapBindingImpl] rollback() " + TxStatus.getStatusName(tm.getStatus()));
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
}
