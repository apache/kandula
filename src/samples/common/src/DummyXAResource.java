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
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class DummyXAResource implements XAResource {
	int timeout= 1000;
	boolean failOnPrepare= false;
	boolean readOnly= false;

	public DummyXAResource(boolean failOnPrepare, boolean readOnly) {
		if (this.readOnly = readOnly)
			this.failOnPrepare = false;
		else
			this.failOnPrepare= failOnPrepare;		
	}

	public void commit(Xid xid, boolean onePhase) throws XAException {
		System.out.println("[DummyXAResource] commit xid= "+xid);
	}

	public void end(Xid xid, int flags) throws XAException {
		System.out.println("[DummyXAResource] end xid= "+xid);		
	}

	public void forget(Xid xid) throws XAException {
		System.out.println("[DummyXAResource] forget xid= "+xid);		
	}

	public int getTransactionTimeout() throws XAException {
		System.out.println("[DummyXAResource] getTransactionTimeout");
		return timeout;
	}

	public boolean isSameRM(XAResource xares) throws XAException {
		System.out.println("[DummyXAResource] isSameRM= "+xares.equals(this));
		return xares.equals(this);
	}

	public int prepare(Xid xid) throws XAException {
		System.out.print("[DummyXAResource] prepare ");
		if (failOnPrepare) {
			System.out.println("throw new XAException(XAException.XAER_RMERR)");
			throw new XAException(XAException.XAER_RMERR);
		}
		else
			if (readOnly) {
				System.out.println("return XA_RDONLY");
				return XA_RDONLY;
			}
			else {
					System.out.println("return XAResource.XA_OK");
					return XAResource.XA_OK;
				}
	}

	public Xid[] recover(int flag) throws XAException {
		System.out.println("[DummyXAResource] recover");
		throw new XAException(XAException.XAER_RMFAIL);
	}

	public void rollback(Xid xid) throws XAException {
		System.out.println("[DummyXAResource] rollback xid= "+xid);
	}

	public boolean setTransactionTimeout(int seconds) throws XAException {
		System.out.println("[DummyXAResource] setTransactionTimeout= "+seconds);
		timeout= seconds;
		return true;
	}

	public void start(Xid xid, int flags) throws XAException {
		System.out.println("[DummyXAResource] start xid= "+xid);
	}
}