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

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.util.TextExtractor;
import org.apache.axis.types.URI;
import org.apache.ws.transaction.coordinator.Identifier;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class _CoordinationContext {
	static final Name NAME_COORDINATION_CONTEXT=
		new PrefixedQName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "CoordinationContext", "wscoor");
	public static final Name QNAME_IDENTIFIER=
		new PrefixedQName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "Identifier", "wscoor");
	static final Name NAME_COORDINATION_TYPE=
		new PrefixedQName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "CoordinationType", "wscoor");
	static final Name NAME_REGISTRATION_SERVICE=
		new PrefixedQName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "RegistrationService", "wscoor");
	static final Name NAME_ADDRESS=
		new PrefixedQName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "Address", "wsa");
	static final Name NAME_REFERENCE_PROPERTIES=
		new PrefixedQName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "ReferenceProperties", "wsa");

	CoordinationContext ctx;

	public _CoordinationContext(CoordinationContext ctx) {
		this.ctx= ctx;
	}

	public _CoordinationContext(
		String id,
		String coordinationType,
		EndpointReference registrationServiceEndpoint) {
		ctx= new CoordinationContext();
		ctx.setIdentifier(new CoordinationContextTypeIdentifier(id));
		ctx.setRegistrationService(registrationServiceEndpoint);
		try {
			ctx.setCoordinationType(new URI(coordinationType));
		}
		catch (URI.MalformedURIException e) {
			throw new IllegalArgumentException(e.toString() + "???" + coordinationType);
		}
	}

	public _CoordinationContext(SOAPElement el) {
		if (!is(el))
			throw new IllegalArgumentException();
		ctx= new CoordinationContext();
		System.out.println("[_CoordinationContext] 1");
		try {			
			NodeList list=
				el.getElementsByTagNameNS(QNAME_IDENTIFIER.getURI(), QNAME_IDENTIFIER.getLocalName());
			System.out.println("[_CoordinationContext] 1.1");
			Element node= (Element)list.item(0);
			System.out.println("[_CoordinationContext] 1.2");
			String s = TextExtractor.getText(node);
			System.out.println("[_CoordinationContext] 1.3");
			ctx.setIdentifier(new CoordinationContextTypeIdentifier(s));

			System.out.println("[_CoordinationContext] 2");
			list=
				el.getElementsByTagNameNS(
					NAME_COORDINATION_TYPE.getURI(),
					NAME_COORDINATION_TYPE.getLocalName());
			node= (Element)list.item(0);
			ctx.setCoordinationType(new URI(TextExtractor.getText(node)));

			System.out.println("[_CoordinationContext] 3");
			list=
				el.getElementsByTagNameNS(
					NAME_REGISTRATION_SERVICE.getURI(),
					NAME_REGISTRATION_SERVICE.getLocalName());
			node= (Element)list.item(0);

			System.out.println("[_CoordinationContext] 4");
			EndpointReference epr= new EndpointReference(node);
			System.out.println("[_CoordinationContext] 5");
			ctx.setRegistrationService(epr);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e.toString());
		}
	}

	public static boolean is(SOAPElement e) {
		return e.getElementName().equals(NAME_COORDINATION_CONTEXT);
	}

	public CoordinationContext toCoordinationContext() {
		return ctx;
	}

	public void toSOAPHeaderElement(SOAPHeader header) {
		try {
			SOAPHeaderElement e= header.addHeaderElement(NAME_COORDINATION_CONTEXT);
			e.addChildElement(QNAME_IDENTIFIER).addTextNode(ctx.getIdentifier().toString());
			e.addChildElement(NAME_COORDINATION_TYPE).addTextNode(ctx.getCoordinationType().toString());
			SOAPElement e1= e.addChildElement(NAME_REGISTRATION_SERVICE);
			org.apache.axis.message.addressing.EndpointReferenceType registrationEndpoint=
				ctx.getRegistrationService();
			e1.addChildElement(NAME_ADDRESS).addTextNode(registrationEndpoint.getAddress().toString());
			SOAPElement e3= e1.addChildElement(NAME_REFERENCE_PROPERTIES);
			MessageElement[] _referenceProperties= registrationEndpoint.getProperties().get_any();
			for (int ii= 0; ii < _referenceProperties.length; ii++)
				e3.addChildElement(_referenceProperties[ii].getElementName()).addTextNode(
					_referenceProperties[ii].getValue());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public EndpointReference register(
		String protocol,
		EndpointReference participantEndpoint)
		throws RemoteException {
		RegistrationRPCPort port= new RegistrationRPCPort(new EndpointReference(ctx.getRegistrationService()));
		return port.register(protocol, participantEndpoint);
	}

	public Identifier getActivityId() {
		return new Identifier(ctx.getIdentifier().toString());
	}
}
