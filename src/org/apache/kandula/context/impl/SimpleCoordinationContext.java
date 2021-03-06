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
package org.apache.kandula.context.impl;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Constants;
import org.apache.kandula.context.CoordinationContext;
import org.apache.kandula.utility.EndpointReferenceFactory;

public class SimpleCoordinationContext implements CoordinationContext {
	private String activityID;

	private OMElement contextElement = null;

	private String coordinationType;

	private long expires;

	private EndpointReference registrationServiceEpr;

	public SimpleCoordinationContext(OMElement contextElement) {
		super();
		this.contextElement = contextElement;
		activityID = contextElement.getFirstChildWithName(
				new QName(Constants.WS_COOR,"Identifier")).getText();
		coordinationType = contextElement.getFirstChildWithName(
				new QName(Constants.WS_COOR,"CoordinationType")).getText();
		OMElement registrationElement = contextElement
				.getFirstChildWithName(new QName(Constants.WS_COOR,"RegistrationService"));
		registrationServiceEpr = EndpointReferenceFactory
				.endpointFromOM(registrationElement);
	}

	public SimpleCoordinationContext(String activityID,
			String coordinationType, EndpointReference epr) {
		this.activityID = activityID;
		this.coordinationType = coordinationType;
		this.registrationServiceEpr = epr;
	}

	public String getActivityID() {
		return activityID;
	}

	public Object getCoordinationContextType() {
		return this;
	}

	public String getCoordinationType() {
		return coordinationType;
	}

	public long getExpires() {
		return expires;
	}

	public EndpointReference getRegistrationService() {
		return this.registrationServiceEpr;
	}

	public void setActivityID(String value) {
		this.activityID = value;

	}

	public void setCoordinationType(String value) {
		this.coordinationType = value;

	}

	public void setExpires(long value) {
		this.expires = value;
	}

	public void setRegistrationService(EndpointReference epr) {
		this.registrationServiceEpr = epr;
	}

	public OMElement toOM() {
		if (contextElement != null) {
			return contextElement;
		} else {
			SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
			OMNamespace wsCoor = factory.createOMNamespace(Constants.WS_COOR,
					"wscoor");
			OMElement contextElement = factory.createOMElement(
					"CoordinationContext", wsCoor);
			if (this.expires != 0) {
				OMElement expiresElement = factory.createOMElement("Expires",
						wsCoor);
				expiresElement.setText(Long.toString(this.expires));
				contextElement.addChild(expiresElement);
			}
			OMElement identifierElement = factory.createOMElement("Identifier",
					wsCoor);
			identifierElement.setText(this.activityID);
			contextElement.addChild(identifierElement);
			OMElement coorTypeElement = factory.createOMElement(
					"CoordinationType", wsCoor);
			coorTypeElement.setText(this.coordinationType);
			contextElement.addChild(coorTypeElement);
			OMElement registrationServiceElement = factory.createOMElement(
					"RegistrationService", wsCoor);
			EndpointReferenceFactory.endpointToOM(registrationServiceEpr,
					registrationServiceElement, factory);
			contextElement.addChild(registrationServiceElement);
			return contextElement;
		}
	}
}
