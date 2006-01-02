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
package org.apache.ws.transaction.coordinator.at;

import java.rmi.RemoteException;

import javax.transaction.RollbackException;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.ws.transaction.coordinator.CoordinationContext;
import org.apache.ws.transaction.coordinator.ParticipantService;
import org.apache.ws.transaction.coordinator.TimedOutException;
import org.apache.ws.transaction.utility.Callback;
import org.apache.ws.transaction.wsat.CompletionInitiatorPortType;
import org.apache.ws.transaction.wsat.Notification;
import org.apache.ws.transaction.wscoor.Expires;

/**
 * @author Dasarath Weeratunge
 */
public class TransactionImpl {

	private CoordinationContext ctx;

	private EndpointReference eprOfCompletionCoordinator;

	private CompletionInitiatorCallback callback;

	private boolean aborted = false;

	private boolean committed = false;

	private boolean timedOut = false;

	private boolean canInitiateCompletion = false;

	protected TransactionImpl(CoordinationContext ctx) {
		this.ctx = ctx;
	}

	private void register() throws RemoteException {
		long timeout = 0;
		Expires ex = ctx.getExpires();
		if (ex != null)
			timeout = ex.get_value().longValue();
		callback = new CompletionInitiatorCallback();
		eprOfCompletionCoordinator = ctx.register(
			ATCoordinator.PROTOCOL_ID_COMPLETION,
			ParticipantService.getInstance().getCompletionInitiatorService(
				callback, timeout));
		canInitiateCompletion = true;
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

	protected CoordinationContext getCoordinationContex() {
		return ctx;
	}

	public void enlistParticipant(boolean durable,
			AbstractParticipant participant) throws RemoteException {

		TransactionManagerImpl tm = TransactionManagerImpl.getInstance();
		TransactionImpl tx = tm.suspend();

		participant.register(durable, ctx);

		tm.resume(tx);
	}

	public void rollback() throws RemoteException {
		TransactionManagerImpl tm = TransactionManagerImpl.getInstance();
		TransactionImpl tx = tm.suspend();

		if (!canInitiateCompletion)
			register();

		try {
			synchronized (callback) {
				if (!aborted) {
					if (committed)
						throw new IllegalStateException();
					new CompletionCoordinatorStub(eprOfCompletionCoordinator).rollbackOperation(null);
					callback.wait();
				}
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
		} finally {
			tm.resume(tx);
		}
	}

	public void commit() throws RemoteException {
		TransactionManagerImpl tm = TransactionManagerImpl.getInstance();
		TransactionImpl tx = tm.suspend();

		if (!canInitiateCompletion)
			register();

		try {
			synchronized (callback) {
				if (!committed) {
					if (aborted)
						throw new IllegalStateException();
					new CompletionCoordinatorStub(eprOfCompletionCoordinator).commitOperation(null);
					callback.wait();
				}
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
		} finally {
			tm.resume(tx);
		}
	}
}