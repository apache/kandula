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
package org.apache.ws.transaction.wsat;

import java.rmi.RemoteException;

import org.apache.axis.MessageContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.AddressingHeaders;
import org.apache.axis.message.addressing.Constants;
import org.apache.axis.message.addressing.PortType;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.apache.ws.transaction.coordinator.CoordinationService;
import org.apache.ws.transaction.coordinator.Identifier;
import org.apache.ws.transaction.coordinator.at.AtCoordinator;
import org.apache.ws.transaction.coordinator.at.XidImpl;

public class CompletionRPCEndpoint implements CompletionPortTypeRPC {
	public static PortType PORT_TYPE=
		new PortType("http://schemas.xmlsoap.org/ws/2004/10/wsat", "CompletionPortTypeRPC");

	public Notification commitOperation(Notification parameters) throws java.rmi.RemoteException {
		Identifier activityId= getXid().toActivityId();
		try {
			AtCoordinator coordinator=
				(AtCoordinator)CoordinationService.getInstance().getCoordinator(activityId);
			coordinator.commit();
			return null;
		}
		catch (Exception e) {
			if (e instanceof RemoteException)
				throw (RemoteException)e;
			else
				throw new RemoteException(e.toString());

		}
	}

	public Notification rollbackOperation(Notification parameters) throws java.rmi.RemoteException {
		Identifier activityId= getXid().toActivityId();
		try {
			AtCoordinator coordinator=
				(AtCoordinator)CoordinationService.getInstance().getCoordinator(activityId);
			coordinator.rollback();
			return null;
		}
		catch (Exception e) {
			if (e instanceof RemoteException)
				throw (RemoteException)e;
			else
				throw new RemoteException(e.toString());
		}
	}

	private static XidImpl getXid() throws RemoteException {
		AddressingHeaders header=
			(AddressingHeaders)MessageContext.getCurrentContext().getProperty(
				Constants.ENV_ADDRESSING_REQUEST_HEADERS);
		ReferencePropertiesType refprop= header.getReferenceProperties();
		MessageElement e= refprop.get(XidImpl.QNAME_XIDIMPL);
		if (e != null)
			return new XidImpl(e);
		else
			throw new RemoteException();
	}
}
