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
package org.apache.kandula.wsat;

import java.io.IOException;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.clientapi.MessageSender;
import org.apache.axis2.clientapi.Stub;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMNamespace;
import org.apache.kandula.Constants;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public abstract class AbstractATNotifierStub extends Stub {
    protected static org.apache.axis2.description.OperationDescription[] operations;
    public AbstractATNotifierStub() {
        super();
    }
    protected void notify(String localName, String action, int opIndex,
            EndpointReference replyToEPR) throws IOException {
        org.apache.axis2.context.MessageContext messageContext = getMessageContext();
        org.apache.axis2.soap.SOAPEnvelope env = super.createEnvelope();
        org.apache.axis2.soap.SOAPFactory factory = OMAbstractFactory
                .getSOAP12Factory();
        OMNamespace wsAT = factory.createOMNamespace(Constants.WS_AT, "wsat");
        OMElement request = factory.createOMElement(localName, wsAT);
        env.getBody().addChild(request);
        messageContext.setEnvelope(env);

        MessageSender messageSender = new MessageSender(_serviceContext);
        messageSender.setTo(this.toEPR);
        if (replyToEPR != null) {
            messageSender.setReplyTo(replyToEPR);
        }
        messageSender.setWsaAction(action);
        messageSender
                .setSenderTransport(org.apache.axis2.Constants.TRANSPORT_HTTP);
        messageSender.send(operations[opIndex], messageContext);
    }
}
