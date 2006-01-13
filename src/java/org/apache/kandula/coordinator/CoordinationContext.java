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

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.message.addressing.util.TextExtractor;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.kandula.wscoor.CoordinationContextType;
import org.apache.kandula.wscoor.CoordinationContextTypeIdentifier;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CoordinationContext extends CoordinationContextType {
	private static final Name COORDINATION_CONTEXT = new PrefixedQName(
			"http://schemas.xmlsoap.org/ws/2004/10/wscoor",
			"CoordinationContext", "wscoor");

	private static final Name IDENTIFIER = new PrefixedQName(
			"http://schemas.xmlsoap.org/ws/2004/10/wscoor", "Identifier",
			"wscoor");

	private static final Name COORDINATION_TYPE = new PrefixedQName(
			"http://schemas.xmlsoap.org/ws/2004/10/wscoor", "CoordinationType",
			"wscoor");

	private static final Name REGISTRATION_SERVICE = new PrefixedQName(
			"http://schemas.xmlsoap.org/ws/2004/10/wscoor",
			"RegistrationService", "wscoor");

	private static final Name ADDRESS = new PrefixedQName(
			"http://schemas.xmlsoap.org/ws/2004/08/addressing", "Address",
			"wsa");

	private static final Name REFERENCE_PROPERTIES = new PrefixedQName(
			"http://schemas.xmlsoap.org/ws/2004/08/addressing",
			"ReferenceProperties", "wsa");

	public CoordinationContext(CoordinationContextType c) {
		this.set_any(c.get_any());
		this.setCoordinationType(c.getCoordinationType());
		this.setExpires(c.getExpires());
		this.setIdentifier(c.getIdentifier());
		this.setRegistrationService(c.getRegistrationService());
	}

	public CoordinationContext(String id, String coordinationType,
			EndpointReference epr) throws MalformedURIException {
		setIdentifier(new CoordinationContextTypeIdentifier(id));
		setRegistrationService(epr);
		setCoordinationType(new URI(coordinationType));
	}

	public CoordinationContext(SOAPElement el) {
		try {
			NodeList list = el.getElementsByTagNameNS(IDENTIFIER.getURI(),
				IDENTIFIER.getLocalName());
			Element node = (Element) list.item(0);
			String s = TextExtractor.getText(node);
			setIdentifier(new CoordinationContextTypeIdentifier(s));

			list = el.getElementsByTagNameNS(COORDINATION_TYPE.getURI(),
				COORDINATION_TYPE.getLocalName());
			node = (Element) list.item(0);
			setCoordinationType(new URI(TextExtractor.getText(node)));

			list = el.getElementsByTagNameNS(REGISTRATION_SERVICE.getURI(),
				REGISTRATION_SERVICE.getLocalName());
			node = (Element) list.item(0);

			EndpointReference epr = new EndpointReference(node);
			setRegistrationService(epr);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.toString());
		}
	}

	public static boolean is(SOAPElement e) {
		return e.getElementName().equals(COORDINATION_CONTEXT);
	}

	public void toSOAPHeaderElement(SOAPHeader header) {
		try {
			SOAPHeaderElement e = header.addHeaderElement(COORDINATION_CONTEXT);
			e.addChildElement(IDENTIFIER).addTextNode(
				getIdentifier().toString());
			e.addChildElement(COORDINATION_TYPE).addTextNode(
				getCoordinationType().toString());
			SOAPElement e1 = e.addChildElement(REGISTRATION_SERVICE);
			EndpointReferenceType epr = getRegistrationService();
			e1.addChildElement(ADDRESS).addTextNode(epr.getAddress().toString());
			SOAPElement e3 = e1.addChildElement(REFERENCE_PROPERTIES);
			MessageElement[] e4 = epr.getProperties().get_any();
			for (int ii = 0; ii < e4.length; ii++)
				e3.addChildElement(e4[ii].getElementName()).addTextNode(
					e4[ii].getValue());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public EndpointReference register(String p, EndpointReference epr)
			throws RemoteException {
		try {
			RegistrationStub stub = new RegistrationStub(new EndpointReference(
					getRegistrationService()));
			return stub.registerOperation(p, epr);
		} catch (RemoteException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}