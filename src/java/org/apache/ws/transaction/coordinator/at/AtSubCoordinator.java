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

import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class AtSubCoordinator extends AtCoordinatorImpl implements XAResource {
	Transaction localTx;

	public AtSubCoordinator(XidImpl xid, Transaction localTx) {
		//	FIXME - XSDDateTime impl required
		super(xid);
		this.localTx = localTx;
		try {
			localTx.enlistResource(this);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//	we need to overide these methods
	//	there is no reason why this functionality should be provided here
	//	we might as well throw a java.lang.UnsupportedOperationException instead

	public void commit() {
		try {
			localTx.commit();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void rollback() {
		try {
			localTx.rollback();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void commit(Xid xid, boolean onePhase) throws XAException {
		try {
			//	otherwise defaults to the overidden method
			super.commit();
		}
		catch (Exception e) {
			throwAsXAException(e);
		}
	}

	public void end(Xid xid, int flags) throws XAException {
		//	FIXME
	}
	
	public void forget(Xid xid) throws XAException {
		//	FIXME
	}

	public int getTransactionTimeout() throws XAException {
		//	FIXME
		return 0;
	}

	public boolean isSameRM(javax.transaction.xa.XAResource xares) throws XAException {
		return this == xares;
	}

	public int prepare(Xid xid) throws XAException {
		int status = prepare();
		if (status == Status.STATUS_COMMITTED)
			return XAResource.XA_RDONLY;
		else
			if (status == Status.STATUS_PREPARED)
				return XAResource.XA_OK;
			else
				throw new XAException(XAException.XA_RBROLLBACK);
	}

	public Xid[] recover(int flag) throws XAException {
		//	FIXME
		return null;
	}

	public void rollback(Xid xid) throws XAException {
		try {
			super.rollback();
		}
		catch (Exception e) {
			throwAsXAException(e);
		}
	}

	public boolean setTransactionTimeout(int seconds) throws XAException {
		//	FIXME
		return false;
	}

	public void start(Xid xid, int flags) throws XAException {
		//	FIXME
	}

	private void throwAsXAException(Exception e) throws XAException {
		e.printStackTrace();
		throw new XAException(XAException.XAER_RMERR);
	}
}
