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
package org.apache.ws.transaction.coordinator;

import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.ws.transaction.wscoor.ActivationPortTypeRPC;
import org.apache.ws.transaction.wscoor.CreateCoordinationContextResponseType;
import org.apache.ws.transaction.wscoor.CreateCoordinationContextType;

public class ActivationImpl implements ActivationPortTypeRPC {

	public CreateCoordinationContextResponseType createCoordinationContextOperation(
			CreateCoordinationContextType parameters) throws RemoteException {

		String t = parameters.getCoordinationType().toString();
		CoordinationContext ctx;
		try {
			ctx = CoordinationService.getInstance().createCoordinationContext(t);
		} catch (MalformedURIException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (UnsupportedCoordinationTypeException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		CreateCoordinationContextResponseType r = new CreateCoordinationContextResponseType();
		r.setCoordinationContext(ctx);
		return r;

	}
}