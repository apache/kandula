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
package org.apache.ws.transaction.participant.j2ee;


import javax.transaction.Status;
import javax.transaction.Transaction;

import org.apache.ws.transaction.wscoor.CoordinationContext;


class InterposedTransaction {
	protected CoordinationContext ctx;
	protected Transaction localTx;
	protected int status;

	protected InterposedTransaction() {
		status= Status.STATUS_ACTIVE;
	}

	public Transaction getLocalTransaction() {
		return localTx;
	}

	public CoordinationContext getCoordinationContext() {
		return ctx;
	}

	protected synchronized void forget() {
		TransactionBridge.getInstance().forget(ctx.getActivityId());
		done= true;
		notifyAll();
	}

	boolean done= false;
	boolean locked= false;

	protected synchronized void lock() {
		if (locked) {
			while (locked) {
				try {
					wait();
				}
				catch (InterruptedException ex) {
					//	ignore
				}
				if (done)
					throw new IllegalStateException();
			}
		}

		locked= true;
	}

	protected synchronized void unlock() {
		if (!locked)
			throw new IllegalStateException();
		locked= false;
		notify();
	}
}
