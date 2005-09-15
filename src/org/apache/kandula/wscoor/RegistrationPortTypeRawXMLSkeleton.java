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
package org.apache.kandula.wscoor;

import org.apache.axis2.addressing.AnyContentType;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMNamespace;
import org.apache.axis2.soap.SOAPFactory;
import org.apache.kandula.KandulaException;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.utility.KandulaUtils;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author <a href="mailto:thilina@apache.org"> Thilina Gunarathne </a>
 */

public class RegistrationPortTypeRawXMLSkeleton {
    private MessageContext msgContext;

    public void init(MessageContext msgContext) {
        this.msgContext = msgContext;
    }

    public OMElement RegisterOperation(OMElement request)
            throws KandulaException {

        String protocolIdentifier = request.getFirstChildWithName(
                new QName("ProtocolIdentifier")).getText();
        OMElement participantEPRElement = request
                .getFirstChildWithName(new QName("ParticipantProtocolService"));
        EndpointReference participantEPR = new EndpointReference(
                participantEPRElement.getFirstChildWithName(
                        new QName("Address")).getText());
        AnyContentType referenceProperties = new AnyContentType();
        OMElement referencePropertiesElement = participantEPRElement
                .getFirstChildWithName(new QName("ReferenceProperties"));
        Iterator propertyIter = referencePropertiesElement.getChildElements();
        while (propertyIter.hasNext()) {
            OMElement element = (OMElement) propertyIter.next();
            referenceProperties.addReferenceValue(element.getQName(), element
                    .getText());
        }
        participantEPR.setReferenceProperties(referenceProperties);

        //have to extract the reference parameter "id". Axis2 does not support
        ArrayList list = msgContext.getMessageInformationHeaders()
                .getReferenceParameters();

        Coordinator coordinator = new Coordinator();
        EndpointReference epr = coordinator.registerParticipant(
                Coordinator.ACTIVITY_ID, protocolIdentifier, participantEPR);
        System.out.println("visited registration skeleton");
        return toOM(epr);
    }

    private OMElement toOM(EndpointReference epr) {
        SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
        OMNamespace wsCoor = factory.createOMNamespace(
                org.apache.kandula.Constants.WS_COOR, "wscoor");
        OMElement protocolService = factory.createOMElement(
                "CoordinatorProtocolService", wsCoor);
        OMElement coordinatorProtocolService = factory.createOMElement(
                "CoordinatorProtocolService", wsCoor);
        KandulaUtils.endpointToOM(epr, coordinatorProtocolService, factory);
        protocolService.addChild(coordinatorProtocolService);
        return protocolService;
    }
}