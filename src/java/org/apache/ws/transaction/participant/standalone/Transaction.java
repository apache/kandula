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
package org.apache.ws.transaction.participant.standalone;

import java.rmi.RemoteException;

import org.apache.ws.transaction.utility.EndpointReferenceFactory;
import org.apache.ws.transaction.wsat.CompletionRPCPort;
import org.apache.ws.transaction.wsat.ParticipantRPCEndpoint;
import org.apache.ws.transaction.wscoor._CoordinationContext;

/**
 * @author Dasarath Weeratunge
 */
public class Transaction {
	private static String COMPLETION_PROTOCOL = "http://schemas.xmlsoap.org/ws/2003/09/wsat#Completion";

	private _CoordinationContext ctx;

	private CompletionRPCPort compPort;

	public Transaction(_CoordinationContext ctx) throws RemoteException {
		this.ctx = ctx;
		// FIXME: 
		compPort = new CompletionRPCPort(ctx.register(COMPLETION_PROTOCOL,
				EndpointReferenceFactory.getInstance().getEndpointReference(
						ParticipantRPCEndpoint.PORT_TYPE, null)));
	}

	public _CoordinationContext getCoordinationContex() {
		return ctx;
	}

	public void rollback() throws RemoteException {
		compPort.rollback();
	}

	public void commit() throws RemoteException {
		compPort.commit();
	}
}