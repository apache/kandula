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
package org.apache.ws.transaction.utility;

import java.rmi.RemoteException;

import javax.transaction.RollbackException;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.ws.transaction.coordinator.CoordinationContext;
import org.apache.ws.transaction.coordinator.ParticipantService;
import org.apache.ws.transaction.coordinator.TimedOutException;
import org.apache.ws.transaction.coordinator.at.ATCoordinator;
import org.apache.ws.transaction.coordinator.at.CompletionCoordinatorStub;
import org.apache.ws.transaction.wsat.CompletionInitiatorPortType;
import org.apache.ws.transaction.wsat.Notification;
import org.apache.ws.transaction.wscoor.Expires;

/**
 * @author Dasarath Weeratunge
 */
public class TransactionImpl {
	private CoordinationContext ctx;

	private EndpointReference epr;

	private CompletionInitiatorCallback callback;

	private boolean aborted = false;

	private boolean committed = false;

	private boolean timedOut = false;

	public TransactionImpl(CoordinationContext ctx) throws RemoteException {
		this.ctx = ctx;
		long timeout = 0;
		Expires ex = ctx.getExpires();
		if (ex != null)
			timeout = ex.get_value().longValue();
		callback = new CompletionInitiatorCallback();
		epr = ctx.register(ATCoordinator.PROTOCOL_ID_COMPLETION,
			ParticipantService.getInstance().getCompletionInitiatorService(
				callback, timeout));
	}

	private class CompletionInitiatorCallback implements
			CompletionInitiatorPortType, Callback {
		public synchronized void committedOperation(Notification parameters)
				throws RemoteException {
			committed = true;
			notify();
		}

		public synchronized void abortedOperation(Notification parameters)
				throws RemoteException {
			aborted = true;
			notify();
		}

		public synchronized void timeout() {
			timedOut = true;
			notify();
		}
	}

	public CoordinationContext getCoordinationContex() {
		return ctx;
	}

	public void rollback() throws RemoteException {
		try {
			synchronized (callback) {
				if (aborted)
					return;
				if (committed)
					throw new IllegalStateException();
				new CompletionCoordinatorStub(epr).rollbackOperation(null);
				callback.wait();
			}
			if (timedOut)
				throw new TimedOutException();
			if (!aborted)
				throw new RollbackException();
		} catch (RemoteException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void commit() throws RemoteException {
		try {
			synchronized (callback) {
				if (committed)
					return;
				if (aborted)
					throw new IllegalStateException();
				new CompletionCoordinatorStub(epr).commitOperation(null);
				callback.wait();
			}
			if (timedOut)
				throw new TimedOutException();
			if (!committed)
				throw new RollbackException();
		} catch (RemoteException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}