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

import java.net.URL;
import java.rmi.RemoteException;

import org.apache.axis.types.URI;
import org.apache.ws.transaction.utility.Service;

/**
 * @author Dasarath Weeratunge
 * 
 * Created on Jun 14, 2004
 */
public class ActivationRPCPort {
	ActivationPortTypeRPC stub;

	public ActivationRPCPort(org.apache.axis.message.addressing.EndpointReference epr) {
		try {
			stub= new ActivationPortTypeRPCBindingStub(new URL(epr.getAddress().toString()), new Service(epr));
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e.toString());
		}
	}

	public _CoordinationContext createCoordinationContext(String coordinationType) throws RemoteException {
		try {
			CreateCoordinationContextType parameters= new CreateCoordinationContextType();
			parameters.setCoordinationType(new URI(coordinationType));
			CreateCoordinationContextResponseType response= stub.createCoordinationContextOperation(parameters);
			return new _CoordinationContext(response.getCoordinationContext());
		}
		catch (URI.MalformedURIException e) {
			throw new IllegalArgumentException(e + "???" + coordinationType);
		}
	}
}
