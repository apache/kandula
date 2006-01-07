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

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.transaction.RollbackException;
import javax.xml.soap.Name;

import org.apache.axis.AxisFault;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.ws.transaction.coordinator.Callback;
import org.apache.ws.transaction.coordinator.CoordinationContext;
import org.apache.ws.transaction.coordinator.CoordinationService;
import org.apache.ws.transaction.coordinator.TimedOutException;
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
		EndpointReference epr = CoordinationService.getInstance().getCompletionInitiatorService(
			callback, timeout);
		callback.setEndpointReference(epr);
		eprOfCompletionCoordinator = ctx.register(
			ATCoordinator.PROTOCOL_ID_COMPLETION, epr);
		canInitiateCompletion = true;
	}

	private class CompletionInitiatorCallback implements
			CompletionInitiatorPortType, Callback {
		
		private String id;

		private EndpointReference epr;

		public CompletionInitiatorCallback() {
			id = "uuid:" + UUIDGenFactory.getUUIDGen().nextUUID();
		}

		public synchronized void committedOperation(Notification parameters) {
			committed = true;
			notify();
		}

		public synchronized void abortedOperation(Notification parameters) {
			aborted = true;
			notify();
		}

		public synchronized void timeout() {
			timedOut = true;
			notify();
		}

		public synchronized void onFault(Name code) {
			notify();
		}

		public String getID() {
			return id;
		}

		public void setEndpointReference(EndpointReference epr) {
			this.epr = epr;
		}

		public EndpointReference getEndpointReference() {
			return epr;
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
					getCompletionCoordinatorStub().rollbackOperation(null);
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

	private CompletionCoordinatorStub getCompletionCoordinatorStub()
			throws AxisFault, MalformedURLException {
		return new CompletionCoordinatorStub(callback,
				eprOfCompletionCoordinator);
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
					getCompletionCoordinatorStub().commitOperation(null);
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