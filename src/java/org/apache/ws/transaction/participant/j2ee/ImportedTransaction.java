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

import javax.resource.spi.XATerminator;
import javax.transaction.Status;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.ws.transaction.coordinator.at.XidImpl;
import org.apache.ws.transaction.utility.EndpointReferenceFactory;
import org.apache.ws.transaction.wsat.ParticipantRPCEndpoint;
import org.apache.ws.transaction.wsat.Vote;
import org.apache.ws.transaction.wscoor.CoordinationContext;

public class ImportedTransaction extends InterposedTransaction {
	static final String DURABLE_2PC_PROTOCOL= "http://schemas.xmlsoap.org/ws/2003/09/wsat#Durable2PC";
	static XATerminator xaTerminator=
		TransactionManagerGlueFactory.getInstance().getTransactionManagerGlue().getXATerminator();
	EndpointReference coordinator;
	Xid xid= new XidImpl();

	public ImportedTransaction(CoordinationContext ctx) {
		super.ctx= ctx;
		try {
			coordinator=
				ctx.register(
					DURABLE_2PC_PROTOCOL,
					EndpointReferenceFactory.getInstance().getEndpointReference(
						ParticipantRPCEndpoint.PORT_TYPE,
						ctx.getActivityId().toReferencePropertiesType()));
			TransactionImporter importer=
				TransactionManagerGlueFactory
					.getInstance()
					.getTransactionManagerGlue()
					.getTransactionImporter();
			// TODO: implement timeout
			localTx= importer.importExternalTransaction(xid, Integer.MAX_VALUE);
		}
		catch (Exception e) {
			if (localTx != null) {
				try {
					xaTerminator.forget(xid);
				}
				catch (Exception _e) {
					_e.printStackTrace();
				}
			}
			throw new RuntimeException(e);
		}
	}

	public Vote prepare() {
		lock();
		try {
			switch (status) {
				case Status.STATUS_ACTIVE :
					break;
				case Status.STATUS_PREPARED :
					return Vote.VoteCommit;
				case Status.STATUS_COMMITTED :
					return Vote.VoteReadOnly;
				case Status.STATUS_ROLLEDBACK :
					return Vote.VoteRollback;
				default :
					throw new IllegalStateException();
			}
			status= Status.STATUS_PREPARING;
			try {
				if (xaTerminator.prepare(xid) == XAResource.XA_RDONLY) {
					status= Status.STATUS_COMMITTED;
					return Vote.VoteReadOnly;
				}
				status= Status.STATUS_PREPARED;
				return Vote.VoteCommit;
			}
			catch (XAException e) {
				//	TODO: do we need to do a rollback for jotm ???
				status= Status.STATUS_ROLLEDBACK;
				return Vote.VoteRollback;
			}
		}
		finally {
			unlock();
		}
	}

	public void commit() throws XAException {
		lock();
		try {
			switch (status) {
				case Status.STATUS_PREPARED :
					break;
				case Status.STATUS_COMMITTED :
					return;
				default :
					throw new IllegalStateException();
			}
			status= Status.STATUS_COMMITTING;
			xaTerminator.commit(xid, false);
			status= Status.STATUS_COMMITTED;
		}
		finally {
			unlock();
		}
	}

	public void rollback() throws XAException {
		lock();
		try {
			switch (status) {
				case Status.STATUS_ACTIVE :
				case Status.STATUS_PREPARED :
					break;
				case Status.STATUS_ROLLEDBACK :
					return;
				default :
					throw new IllegalStateException();
			}
			status= Status.STATUS_ROLLING_BACK;
			xaTerminator.rollback(xid);
			status= Status.STATUS_ROLLEDBACK;
		}
		finally {
			unlock();
		}
	}
}
