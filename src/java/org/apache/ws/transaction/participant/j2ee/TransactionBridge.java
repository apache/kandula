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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transaction;

import org.apache.ws.transaction.coordinator.Identifier;
import org.apache.ws.transaction.coordinator.UnknownActivityException;
import org.apache.ws.transaction.wscoor.CoordinationContext;


public class TransactionBridge {
	static TransactionBridge instance= new TransactionBridge();

	public TransactionBridge() {}

	public static TransactionBridge getInstance() {
		return instance;
	}

	public CoordinationContext exportJ2eeTransaction(Transaction localTx) {
		InterposedTransaction tx= (InterposedTransaction)byLocalTx.get(localTx);
		if (tx != null)
			return tx.getCoordinationContext();
		tx= new ExportedTransaction(localTx);
		byLocalTx.put(localTx, tx);
		CoordinationContext ctx= tx.getCoordinationContext();
		byCtx.put(ctx.getActivityId(), tx);
		return ctx;
	}

	public Transaction importWSTransaction(CoordinationContext ctx) {
		InterposedTransaction tx= (InterposedTransaction)byCtx.get(ctx.getActivityId());
		if (tx != null)
			return tx.getLocalTransaction();
		tx= new ImportedTransaction(ctx);
		byCtx.put(ctx.getActivityId(), tx);
		Transaction localTx= tx.getLocalTransaction();
		byLocalTx.put(localTx, tx);
		return localTx;
	}

	public ImportedTransaction getImportedTransaction(Identifier activityId) throws UnknownActivityException {
		Object obj= byCtx.get(activityId);
		if (obj == null)
			throw new UnknownActivityException(activityId);
		if (obj instanceof ImportedTransaction)
			return (ImportedTransaction)obj;
		throw new IllegalArgumentException(
			"activity id " + activityId + " does not correspond to an imported transaction.");
	}

	//	TODO: when should we forget an imported transaction???
	public void forget(Identifier activityId) throws UnknownActivityException {
		InterposedTransaction tx= (InterposedTransaction)byCtx.remove(activityId);
		if (tx == null)
			throw new UnknownActivityException(activityId);
		byLocalTx.remove(tx.getLocalTransaction());
	}

	Map byLocalTx= Collections.synchronizedMap(new HashMap());
	Map byCtx= Collections.synchronizedMap(new HashMap());
}