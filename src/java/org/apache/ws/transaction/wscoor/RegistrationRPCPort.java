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

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.types.URI;
import org.apache.ws.transaction.utility.Service;

/**
 * @author Dasarath Weeratunge
 * 
 * Created on Jun 14, 2004
 * 
 */
public class RegistrationRPCPort {
	RegistrationPortTypeRPC stub;

	public RegistrationRPCPort(org.apache.axis.message.addressing.EndpointReference epr) {
		try {
			String url = epr.getAddress().toString();
			// FIXME: 
			// the following line was added for interop testing against IBM endpoint  
			url= url.replaceAll("wsi\\.alphaworks\\.ibm\\.com:8080", "localhost:8082");
			System.out.print(url);
			stub= new RegistrationPortTypeRPCBindingStub(new URL(url), new Service(epr));
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e.toString());
		}
	}

	public EndpointReference register(
		String protocol,
		EndpointReference participant)
		throws RemoteException {
		try {			
			RegisterType parameters= new RegisterType();
			parameters.setProtocolIdentifier(new URI(protocol));
			parameters.setParticipantProtocolService(participant);			
			RegisterResponseType response= stub.registerOperation(parameters);
			return new EndpointReference(response.getCoordinatorProtocolService());
		}
		catch (URI.MalformedURIException e) {
			throw new IllegalArgumentException(e + "???" + protocol);
		}
	}
}
