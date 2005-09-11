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
package org.apache.kandula.typemapping;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.AnyContentType;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.addressing.AddressingConstants.Final;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMNamespace;
import org.apache.axis2.soap.SOAPFactory;
import org.apache.kandula.Constants;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class SimpleCoordinationContext implements CoordinationContext {
    private String activityID;

    private String coordinationType;

    private long expires;

    private EndpointReference registrationServiceEpr;

    private OMElement contextElement = null;

    public SimpleCoordinationContext() {
        super();

    }

    public SimpleCoordinationContext(OMElement contextElement) {
        super();
        this.contextElement = contextElement;
        activityID = contextElement.getFirstChildWithName(
                new QName("Identifier")).getText();
        coordinationType = contextElement.getFirstChildWithName(
                new QName("CoordinationType")).getText();
        OMElement registrationElement =  contextElement.getFirstChildWithName(
                new QName("RegistrationService"));
        registrationServiceEpr = new EndpointReference(registrationElement
                .getFirstChildWithName(new QName("Address")).getText());
        AnyContentType referenceProperties = new AnyContentType();
        OMElement referencePropertiesElement = registrationElement
                .getFirstChildWithName(new QName("ReferenceProperties"));
        Iterator propertyIter = referencePropertiesElement.getChildElements();
        while (propertyIter.hasNext()) {
            OMElement element = (OMElement) propertyIter.next();
            referenceProperties.addReferenceValue(element.getQName(), element
                    .getText());
        }
        registrationServiceEpr.setReferenceProperties(referenceProperties);
    }

    public String getActivityID() {
        return activityID;
    }

    public String getCoordinationType() {
        return coordinationType;
    }

    public EndpointReference getRegistrationService() {
        return this.registrationServiceEpr;
    }

    public long getExpires() {
        return expires;
    }

    public void setActivityID(String value) {
        this.activityID = value;

    }

    public void setCoordinationType(String value) {
        this.coordinationType = value;

    }

    public void setRegistrationService(EndpointReference epr) {
        this.registrationServiceEpr = epr;
    }

    public void setExpires(long value) {
        this.expires = value;
    }

    public Object getCoordinationContextType() {
        return this;
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
            OMNamespace wsAddressing = factory.createOMNamespace(
                    AddressingConstants.Submission.WSA_NAMESPACE,
                    AddressingConstants.WSA_DEFAULT_PRFIX);
            OMElement addressElement = factory.createOMElement("Address",
                    wsAddressing);
            addressElement.setText(registrationServiceEpr.getAddress());
            registrationServiceElement.addChild(addressElement);
            AnyContentType referenceValues = registrationServiceEpr
                    .getReferenceProperties();
            if (referenceValues != null) {
                OMElement refPropertyElement = factory.createOMElement(
                        "ReferenceProperties", wsAddressing);
                registrationServiceElement.addChild(refPropertyElement);
                Iterator iterator = referenceValues.getKeys();
                while (iterator.hasNext()) {
                    QName key = (QName) iterator.next();
                    String value = referenceValues.getReferenceValue(key);
                    OMElement omElement = factory.createOMElement(key,
                            refPropertyElement);
                    refPropertyElement.addChild(omElement);
                    if (Final.WSA_NAMESPACE.equals(wsAddressing)) {
                        omElement.addAttribute(
                                Final.WSA_IS_REFERENCE_PARAMETER_ATTRIBUTE,
                                Final.WSA_TYPE_ATTRIBUTE_VALUE, wsAddressing);
                    }
                    omElement.setText(value);
                }
            }
            contextElement.addChild(registrationServiceElement);
            return contextElement;
        }
    }
}