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

/**
 * @author Dasarath Weeratunge
 *
 */
import javax.transaction.Synchronization;
import javax.transaction.Transaction;

import org.apache.ws.transaction.coordinator.CoordinationService;


public class ExportedTransaction extends InterposedTransaction implements Synchronization {
	public ExportedTransaction(Transaction localTx) {
		super.localTx= localTx;
		ctx= CoordinationService.getInstance().interpose(localTx).getCoordinationContext();
		try {
			localTx.registerSynchronization(this);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void beforeCompletion() {}

	public void afterCompletion(int status) {
		this.status= status;
		forget();
	}
}