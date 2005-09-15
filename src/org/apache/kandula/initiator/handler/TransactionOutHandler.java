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
package org.apache.kandula.initiator.handler;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.MessageInformationHeaders;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.axis2.soap.SOAPHeader;
import org.apache.kandula.context.ActivityContext;
import org.apache.kandula.context.coordination.CoordinationContext;
import org.apache.kandula.storage.StorageFactory;


public class TransactionOutHandler extends AbstractHandler {
    private ThreadLocal threadInfo = new ThreadLocal();

    public void invoke(MessageContext msgContext) throws AxisFault {
        Object key = threadInfo.get();
        if (null != key) {
            ActivityContext context = StorageFactory.getInstance().getStore()
                    .getContext(key);
            if (context == null) {
                throw new AxisFault("IllegalState");
            }

            MessageInformationHeaders messageInformationHeaders =
                    msgContext.getMessageInformationHeaders();
            SOAPHeader soapHeader = msgContext.getEnvelope().getHeader();
            CoordinationContext coorContext = context.getCoordinationContext();
            soapHeader.addChild(coorContext.toOM());
        }
    }
}

