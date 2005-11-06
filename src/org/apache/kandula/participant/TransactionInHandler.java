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
package org.apache.kandula.participant;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.soap.SOAPHeader;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.at.ATParticipantContext;
import org.apache.kandula.context.coordination.CoordinationContext;
import org.apache.kandula.context.coordination.SimpleCoordinationContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.storage.StorageFactory;
import org.apache.kandula.storage.Store;
import org.apache.kandula.utility.EndpointReferenceFactory;
import org.apache.kandula.utility.KandulaUtils;
import org.apache.kandula.wscoor.RegistrationCoordinatorPortTypeRawXMLStub;

public class TransactionInHandler extends AbstractHandler {
    private ThreadLocal threadInfo = new ThreadLocal();

    public void invoke(MessageContext msgContext) throws AxisFault {
        KandulaResource resource;
        StorageFactory.getInstance().setConfigurationContext(msgContext.getServiceContext().getConfigurationContext());
        ATParticipantContext context = new ATParticipantContext();
        SOAPHeader header = msgContext.getEnvelope().getHeader();
        OMElement coordinationElement = header.getFirstChildWithName(new QName(
                Constants.WS_COOR, "CoordinationContext"));
        if (coordinationElement == null) {
            throw new AxisFault(
                    "Transaction Handler engaged.. No Coordination Context found");
        }
        CoordinationContext coorContext = new SimpleCoordinationContext(
                coordinationElement);
        context.setCoordinationContext(coorContext);

        // TODO : See whether we can allow the user to set the resource when the
        // business logic receives the message
        String resourceFile = (String) msgContext.getParameter(
                Constants.KANDULA_RESOURCE).getValue();
        String participantRepository = EndpointReferenceFactory.getInstance().getPariticipantRepository();
        System.out.println(participantRepository);
        try {
            resource = (KandulaResource) Class.forName(resourceFile)
                    .newInstance();
        } catch (Exception e) {
            throw new AxisFault(e);
        }
        context.setResource(resource);

        String id = KandulaUtils.getRandomStringOf18Characters();
        Store store = StorageFactory.getInstance().getStore();
        context.setProperty(AbstractContext.REQUESTER_ID, id);
        store.put(id, context);
        ParticipantTransactionManager txManager = new ParticipantTransactionManager();
        try {
            RegistrationCoordinatorPortTypeRawXMLStub stub = new RegistrationCoordinatorPortTypeRawXMLStub(
                    participantRepository, coorContext.getRegistrationService());
            EndpointReference participantProtocolService = EndpointReferenceFactory
                    .getInstance().get2PCParticipantEndpoint(id);
            stub.registerOperation(resource.getProtocol(),
                    participantProtocolService, id);
        } catch (IOException e) {
            throw new AxisFault(e);
        } catch (AbstractKandulaException e) {
            AxisFault e1 = new AxisFault(e);
            e1.setFaultCode(e.getFaultCode());
            throw e1;
        }

    }
}

