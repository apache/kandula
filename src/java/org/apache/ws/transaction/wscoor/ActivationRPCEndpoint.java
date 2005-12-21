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
package org.apache.ws.transaction.wscoor;

import java.rmi.RemoteException;

import org.apache.axis.message.addressing.PortType;
import org.apache.ws.transaction.coordinator.CoordinationService;
import org.apache.ws.transaction.coordinator.Coordinator;


public class ActivationRPCEndpoint implements ActivationPortTypeRPC {
	public static PortType PORT_TYPE= new PortType("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "ActivationPortTypeRPC");
	
	public CreateCoordinationContextResponseType createCoordinationContextOperation(CreateCoordinationContextType parameters)
		throws RemoteException {
		try {
			String coordinationType= parameters.getCoordinationType().toString();
			Coordinator coordinator= CoordinationService.getInstance().coordinate(coordinationType);
			CreateCoordinationContextResponseType response= new CreateCoordinationContextResponseType();
			response.setCoordinationContext(
				coordinator.getCoordinationContext().toCoordinationContext());
			return response;
		}
		catch (Exception e) {
			if (e instanceof RemoteException)
				throw (RemoteException)e;
			else
				throw new RemoteException(e.toString());
		}
	}
}
