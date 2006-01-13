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
package org.apache.kandula.coordinator;

import java.rmi.RemoteException;

import org.apache.kandula.wscoor.ActivationPortTypeRPC;
import org.apache.kandula.wscoor.CreateCoordinationContextResponseType;
import org.apache.kandula.wscoor.CreateCoordinationContextType;

public class ActivationImpl implements ActivationPortTypeRPC {

	public CreateCoordinationContextResponseType createCoordinationContextOperation(
			CreateCoordinationContextType params) throws RemoteException {
		return CoordinationService.getInstance().createCoordinationContextOperation(
			params);
	}
}