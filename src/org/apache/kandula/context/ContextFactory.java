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
package org.apache.kandula.context;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.context.impl.ATActivityContext;
import org.apache.kandula.faults.InvalidProtocolException;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ContextFactory {
	private static ContextFactory instance = new ContextFactory();

	public static ContextFactory getInstance() {
		return instance;
	}

	public AbstractContext createActivity(String protocol)
			throws InvalidProtocolException {
		if (org.apache.kandula.Constants.WS_AT.equalsIgnoreCase(protocol)) {
			return new ATActivityContext();
		} else {
			throw new InvalidProtocolException("Unsupported Protocol Type");
		}
	}

	/**
	 * To be used by the initaitor TxManager to create the initial context
	 * @param protocol
	 * @param activationEPR
	 * @return
	 * @throws InvalidProtocolException
	 */
	public AbstractContext createActivity(String protocol,
			EndpointReference activationEPR) throws InvalidProtocolException {
		if (org.apache.kandula.Constants.WS_AT.equalsIgnoreCase(protocol)) {
			return new ATActivityContext(activationEPR);
		} else {
			throw new InvalidProtocolException("Unsupported Protocol Type");
		}
	}

	public AbstractContext createActivity(CoordinationContext context)
			throws InvalidProtocolException {
		if (org.apache.kandula.Constants.WS_AT.equalsIgnoreCase(context
				.getCoordinationType())) {
			return new ATActivityContext(context);
		} else {
			throw new InvalidProtocolException("Unsupported Protocol Type");
		}
	}
}