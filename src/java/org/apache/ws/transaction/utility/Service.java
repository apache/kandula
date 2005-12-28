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
package org.apache.ws.transaction.utility;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ServiceException;

import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.AddressingHeaders;
import org.apache.axis.message.addressing.Constants;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.MessageID;
import org.apache.axis.message.addressing.RelatesTo;
import org.apache.axis.message.addressing.RelationshipTypeValues;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;

public class Service extends org.apache.axis.client.Service {
	AddressingHeaders headers;

	Object callback;

	public Service(EndpointReference epr) {
		headers = new AddressingHeaders(epr);
	}
	
	public Service(EndpointReference epr, EndpointReference replyTo) {
		headers = new AddressingHeaders(epr);
		headers.setReplyTo(replyTo);
	}
	
	public void setCallback(Object callback) {
		this.callback = callback;
	}
	
	public void setRelatesTo(MessageID id){
		List l = new ArrayList(1);
		l.add(new RelatesTo(id, RelationshipTypeValues.RESPONSE));
		headers.setRelatesTo(l);
	}
	
	public Call createCall() throws ServiceException {
		Call call = super.createCall();
		if (callback != null) {
			try {
				MessageID id = new MessageID(new URI("uuid:"
						+ UUIDGenFactory.getUUIDGen().nextUUID()));
				headers.setMessageID(id);
				String ref = id.toString();
				CallbackRegistry.getInstance().registerCallback(ref, callback);
				MessageElement e = new MessageElement(
						CallbackRegistry.CALLBACK_REF, ref);
				headers.getReplyTo().getProperties().add(e);
			} catch (MalformedURIException e) {
				throw new ServiceException(e.getMessage());
			}
		}
		call.setProperty(Constants.ENV_ADDRESSING_REQUEST_HEADERS, headers);
		return call;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.rpc.Service#createCall(javax.xml.namespace.QName,
	 *      javax.xml.namespace.QName)
	 */
	public Call createCall(QName arg0, QName arg1) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.rpc.Service#createCall(javax.xml.namespace.QName,
	 *      java.lang.String)
	 */
	public Call createCall(QName arg0, String arg1) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.rpc.Service#createCall(javax.xml.namespace.QName)
	 */
	public Call createCall(QName arg0) throws ServiceException {
		throw new UnsupportedOperationException();
	}

}