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
package org.apache.ws.transaction.participant.standalone;

import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.ws.transaction.utility.EndpointReferenceFactory;
import org.apache.ws.transaction.wscoor.ActivationRPCEndpoint;
import org.apache.ws.transaction.wscoor.ActivationRPCPort;
import org.apache.ws.transaction.wscoor._CoordinationContext;


public class TransactionManager {
	private static String COORDINATION_TYPE= "http://schemas.xmlsoap.org/ws/2004/10/wsat";

	private static TransactionManager instance= new TransactionManager();
	private static ThreadLocal threadInfo= new ThreadLocal();

	public static TransactionManager getInstance() {
		return instance;
	}

	private TransactionManager() {}

	public void begin() throws RemoteException {
		begin(
			EndpointReferenceFactory.getInstance().getEndpointReference(
				ActivationRPCEndpoint.PORT_TYPE,
				null));
	}

	public void begin(EndpointReference coord) throws RemoteException {
		if (threadInfo.get() != null)
			throw new IllegalStateException();
		ActivationRPCPort act= new ActivationRPCPort(coord);
		_CoordinationContext ctx= act.createCoordinationContext(COORDINATION_TYPE);
		threadInfo.set(new Transaction(ctx));
	}

	public void commit() throws RemoteException {
		Transaction tx= getTransaction();
		if (tx == null)
			throw new IllegalStateException();
		forget();
		tx.commit();
	}

	public void rollback() throws RemoteException {
		Transaction tx= getTransaction();
		if (tx == null)
			throw new IllegalStateException();
		forget();
		tx.rollback();
	}

	public Transaction suspend() {
		Transaction tx= getTransaction();
		forget();
		return tx;
	}

	public void resume(Transaction tx) {
		if (threadInfo.get() != null)
			throw new IllegalStateException();
		else
			threadInfo.set(tx);
	}

	public void forget() {
		threadInfo.set(null);
	}

	public Transaction getTransaction() {
		return (Transaction)threadInfo.get();
	}
}
