/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *  
 */
package org.apache.ws.transaction.coordinator.at;

import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.ws.transaction.coordinator.ActivationStub;
import org.apache.ws.transaction.coordinator.CoordinationContext;
import org.apache.ws.transaction.coordinator.CoordinationService;

public class ATManager {
	private static ATManager instance = new ATManager();

	private static ThreadLocal threadInfo = new ThreadLocal();

	public static ATManager getInstance() {
		return instance;
	}

	private ATManager() {
	}

	public void begin() throws RemoteException {
		begin(CoordinationService.getInstance().getActivationService());
	}

	public void begin(EndpointReference epr) throws RemoteException {
		if (threadInfo.get() != null)
			throw new IllegalStateException();
		CoordinationContext ctx;
		try {
			ctx = new ActivationStub(epr).createCoordinationContext(ATCoordinator.COORDINATION_TYPE_ID);
			threadInfo.set(new AT(ctx));
		} catch (RemoteException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void commit() throws RemoteException {
		AT tx = getTransaction();
		if (tx == null)
			throw new IllegalStateException();
		forget();
		tx.commit();
	}

	public void rollback() throws RemoteException {
		AT tx = getTransaction();
		if (tx == null)
			throw new IllegalStateException();
		forget();
		tx.rollback();
	}

	public AT suspend() {
		AT tx = getTransaction();
		forget();
		return tx;
	}

	public void resume(AT tx) {
		if (threadInfo.get() != null)
			throw new IllegalStateException();
		else
			threadInfo.set(tx);
	}

	public void forget() {
		threadInfo.set(null);
	}

	public AT getTransaction() {
		return (AT) threadInfo.get();
	}
}