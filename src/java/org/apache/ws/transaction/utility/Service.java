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
package org.apache.ws.transaction.utility;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ServiceException;

import org.apache.axis.message.addressing.AddressingHeaders;
import org.apache.axis.message.addressing.Constants;
import org.apache.axis.message.addressing.EndpointReference;

public class Service extends org.apache.axis.client.Service {
	AddressingHeaders headers;

	public Service(EndpointReference epr) {
		headers= new AddressingHeaders(epr);
	}

	/* (non-Javadoc)
	 * @see javax.xml.rpc.Service#createCall()
	 */
	public Call createCall() throws ServiceException {
		Call call= super.createCall();
		call.setProperty(Constants.ENV_ADDRESSING_REQUEST_HEADERS, headers);
		return call;
	}

	/* (non-Javadoc)
	 * @see javax.xml.rpc.Service#createCall(javax.xml.namespace.QName, javax.xml.namespace.QName)
	 */
	public Call createCall(QName arg0, QName arg1) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see javax.xml.rpc.Service#createCall(javax.xml.namespace.QName, java.lang.String)
	 */
	public Call createCall(QName arg0, String arg1) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see javax.xml.rpc.Service#createCall(javax.xml.namespace.QName)
	 */
	public Call createCall(QName arg0) throws ServiceException {
		throw new UnsupportedOperationException();
	}

}
