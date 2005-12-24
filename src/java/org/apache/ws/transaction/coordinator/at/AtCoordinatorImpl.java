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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Status;
import javax.transaction.xa.Xid;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.types.URI;
import org.apache.ws.transaction.coordinator.CoordinatorImpl;
import org.apache.ws.transaction.coordinator.UnknownCoordinationProtocolException;
import org.apache.ws.transaction.coordinator.UnknownCoordinationTypeException;
import org.apache.ws.transaction.utility.EndpointReferenceFactory;
import org.apache.ws.transaction.wsat.CompletionRPCEndpoint;
import org.apache.ws.transaction.wsat.CoordinatorRPCEndpoint;
import org.apache.ws.transaction.wsat.ParticipantRPCPort;
import org.apache.ws.transaction.wsat.Vote;

public class AtCoordinatorImpl extends CoordinatorImpl implements AtCoordinator {

	int status = Status.STATUS_NO_TRANSACTION;

	private List participants = Collections.synchronizedList(new ArrayList());

	private XidImpl xid;

	public AtCoordinatorImpl(XidImpl xid) {
		super(xid.toActivityId(), COORDINATION_TYPE);
		this.xid = xid;
		status = Status.STATUS_ACTIVE;
	}

	public int getStatus() {
		return status;
	}

	public EndpointReference registerParticipant(URI protocol,
			EndpointReference participant)
			throws UnknownCoordinationTypeException,
			UnknownCoordinationProtocolException {
		if (status != Status.STATUS_ACTIVE)
			throw new IllegalStateException();
		//	according to the specification we should continue to enlist
		//	durable 2PC participants during the prepare phase of volatile
		// participants
		//	however we ignore this for now

		String temp = protocol.toString();

		if (temp.equals(PROTOCOL_ID_COMPLETION))
			return EndpointReferenceFactory.getInstance().getEndpointReference(
					CompletionRPCEndpoint.PORT_TYPE,
					xid.toReferencePropertiesType());
		else {
			XidImpl branch = xid.newBranch();
			if (temp.equals(PROTOCOL_ID_VOLATILE_2PC))
				participants.add(new RegisteredParticipant(branch, true,
						participant));
			else if (temp.equals(PROTOCOL_ID_DURABLE_2PC))
				participants.add(new RegisteredParticipant(branch, false,
						participant));
			else
				throw new UnknownCoordinationProtocolException(protocol
						.toString());

			return EndpointReferenceFactory.getInstance().getEndpointReference(
					CoordinatorRPCEndpoint.PORT_TYPE,
					branch.toReferencePropertiesType());
		}

	}

	public void commit() {
		switch (status) {
		case Status.STATUS_ACTIVE:
			prepare();
		//	fall through
		case Status.STATUS_PREPARED:
			break;
		default:
			throw new IllegalStateException();
		}

		lock();
		if (status == Status.STATUS_PREPARED)
			status = Status.STATUS_COMMITTING;
		unlock();

		RemoteException e = null;
		if (status == Status.STATUS_MARKED_ROLLBACK)
			rollback();
		else {
			Iterator iter = participants.iterator();
			while (iter.hasNext()) {
				RegisteredParticipant participant = (RegisteredParticipant) iter
						.next();
				//	if a participant abort we set the transaction to rollback,
				// hence
				//	we will not be here. so we do not have to check for aborted
				// case below
				if (!participant.readOnly)
					try {
						participant.commit();
					} catch (RemoteException _e) {
						_e.printStackTrace();
						//	FIXME ???
						e = _e;
					}
			}
			status = Status.STATUS_COMMITTED;
			done();
			if (e != null)
				//	only the last exception caught is thrown; however if there
				// was at least one
				//	it will be thrown to upper layers
				throw new RuntimeException(e);
		}
	}

	public void rollback() {
		lock();

		switch (status) {
		case Status.STATUS_ACTIVE:
		case Status.STATUS_MARKED_ROLLBACK:
		case Status.STATUS_PREPARED:
			break;
		default:
			throw new IllegalStateException();
		}
		status = Status.STATUS_ROLLING_BACK;

		unlock();

		RemoteException e = null;
		Iterator iter = participants.iterator();
		while (iter.hasNext()) {
			RegisteredParticipant participant = (RegisteredParticipant) iter
					.next();
			if (!(participant.readOnly || participant.aborted))
				try {
					participant.rollback();
				} catch (RemoteException _e) {
					_e.printStackTrace();
					//	FIXME ???
					e = _e;
				}
		}

		status = Status.STATUS_ROLLEDBACK;
		done();

		if (e != null)
			throw new RuntimeException(e);
	}

	protected int prepare() {
		System.out.println("\nAAAAAAAAAAA1");
		lock();

		switch (status) {
		case Status.STATUS_ACTIVE:
			break;
		case Status.STATUS_MARKED_ROLLBACK:
		case Status.STATUS_PREPARED:
			return status;
		default:
			throw new IllegalStateException();
		}
		status = Status.STATUS_PREPARING;

		unlock();
		System.out.println("\nAAAAAAAAAAA2");
		boolean readOnly = true;
		Iterator iter = participants.iterator();
		while (iter.hasNext() && status == Status.STATUS_PREPARING) {
			System.out.println("\nAAAAAAAAAAA3");
			RegisteredParticipant participant = (RegisteredParticipant) iter
					.next();
			if (!participant._volatile || participant.readOnly)
				continue;
			Vote vote = null;
			try {
				vote = participant.prepare();
			} catch (RemoteException e) {
				e.printStackTrace();
				//	do we need lock/unlock here???
				status = Status.STATUS_MARKED_ROLLBACK;
				return status;
			}
			if (vote.equals(Vote.VoteReadOnly))
				participant.readOnly();
			else if (readOnly && vote.equals(Vote.VoteCommit))
				readOnly = false;
			else if (vote.equals(Vote.VoteRollback)) {
				participant.abort();
				status = Status.STATUS_MARKED_ROLLBACK;
				return status;
			}
		}
		System.out.println("\nAAAAAAAAAAA4");
		iter = participants.iterator();
		while (iter.hasNext() && status == Status.STATUS_PREPARING) {
			System.out.println("\nAAAAAAAAAAA5");
			RegisteredParticipant participant = (RegisteredParticipant) iter
					.next();
			if (participant._volatile || participant.readOnly)
				continue;
			Vote vote = null;
			try {
				vote = participant.prepare();
			} catch (RemoteException e) {
				e.printStackTrace();
				status = Status.STATUS_MARKED_ROLLBACK;
				return status;
			}
			if (vote.equals(Vote.VoteReadOnly))
				participant.readOnly();
			else if (readOnly && vote.equals(Vote.VoteCommit))
				readOnly = false;
			else if (vote.equals(Vote.VoteRollback)) {
				participant.abort();
				status = Status.STATUS_MARKED_ROLLBACK;
				return status;
			}

		}
		System.out.println("\nAAAAAAAAAAA6");
		lock();
		if (status == Status.STATUS_PREPARING)
			status = readOnly ? Status.STATUS_COMMITTED
					: Status.STATUS_PREPARED;
		unlock();
		System.out.println("\nAAAAAAAAAAA7");
		return status;
	}

	public void readOnly(Xid branch) {
		lock();
		try {
			switch (status) {
			case Status.STATUS_COMMITTED:
			case Status.STATUS_ROLLEDBACK:
				//	no harm done
				return;
			case Status.STATUS_UNKNOWN:
			case Status.STATUS_NO_TRANSACTION:
				throw new IllegalStateException();
			default:
				getParticipant(branch).readOnly();
			}
		} finally {
			unlock();
		}
	}

	public void aborted(Xid branch) {
		lock();
		try {
			switch (status) {
			case Status.STATUS_ROLLEDBACK:
				return;
			case Status.STATUS_ACTIVE:
			case Status.STATUS_PREPARING:
			case Status.STATUS_PREPARED:
				status = Status.STATUS_MARKED_ROLLBACK;
			//	fall through
			case Status.STATUS_MARKED_ROLLBACK:
			case Status.STATUS_ROLLING_BACK:
				//	the participant may get a rollback in the latter case
				break;
			default:
				throw new IllegalStateException();
			}

			getParticipant(branch).abort();
		} finally {
			unlock();
		}
	}

	public void replay(Xid branch) {
		//	FIXME ???
	}

	private RegisteredParticipant getParticipant(Xid branch) {
		Iterator iter = participants.iterator();
		while (iter.hasNext()) {
			RegisteredParticipant participant = (RegisteredParticipant) iter
					.next();
			if (branch.equals(participant.branch))
				return participant;
		}
		//	FIXME
		return null;
	}
}

class RegisteredParticipant {
	public boolean _volatile;

	public boolean readOnly;

	public boolean aborted;

	public XidImpl branch;

	EndpointReference epr;

	ParticipantRPCPort port;

	public RegisteredParticipant(XidImpl branch, boolean _volatile,
			EndpointReference epr) {
		this.branch = branch;
		this._volatile = _volatile;
		this.epr = epr;
		aborted = false;
		readOnly = false;
	}

	public void abort() {
		aborted = true;
	}

	public void readOnly() {
		readOnly = true;
	}

	public Vote prepare() throws RemoteException {
		port = new ParticipantRPCPort(epr);
		return port.prepare();
		//	keep the stub for commit or rollback
	}

	public void commit() throws RemoteException {
		//	we do not have to check and see whether the stub is null since
		//	prepare is always called before commit
		port.commit();
		//	stub is no longer required
		port = null;
	}

	public void rollback() throws RemoteException {
		if (port == null)
			port = new ParticipantRPCPort(epr);
		port.rollback();
		port = null;
	}
}