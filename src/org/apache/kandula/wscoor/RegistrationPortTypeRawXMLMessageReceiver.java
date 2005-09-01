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
/**
 * @author <a href="mailto:thilina@apache.org"> Thilina Gunarathne </a>
 */
import org.apache.axis2.om.OMElement;
import org.apache.axis2.receivers.ServerCallback;

/**
 * Auto generated message receiver
 */

public class RegistrationPortTypeRawXMLMessageReceiver extends
        org.apache.axis2.receivers.AbstractInOutAsyncMessageReceiver {

    public void invokeBusinessLogic(
            org.apache.axis2.context.MessageContext msgContext,
            org.apache.axis2.context.MessageContext newMsgContext,
            ServerCallback callback) throws org.apache.axis2.AxisFault {

        try {
            Object obj = getTheImplementationObject(msgContext);

            RegistrationPortTypeRawXMLSkeleton skel = (RegistrationPortTypeRawXMLSkeleton) obj;
            //Out Envelop
            org.apache.axis2.soap.SOAPEnvelope envelope = null;
            //Find the operation that has been set by the Dispatch phase.
            org.apache.axis2.description.OperationDescription op = msgContext
                    .getOperationContext().getAxisOperation();
            if (op == null) {
                throw new org.apache.axis2.AxisFault(
                        "Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
            }

            String methodName;
            if (op.getName() != null
                    & (methodName = op.getName().getLocalPart()) != null) {

                if (methodName.equals("RegisterOperation")) {

                    OMElement response = null;

                    //doc style
                    response = skel
                            .RegisterOperation((org.apache.axis2.om.OMElement) msgContext
                                    .getEnvelope().getBody().getFirstChild()
                                    .detach());

                    //Create a default envelop
                    envelope = getSOAPFactory().getDefaultEnvelope();
                    //Create a Omelement of the result if a result exist

                    envelope.getBody().setFirstChild(response);

                }

                newMsgContext.setEnvelope(envelope);
                callback.handleResult(newMsgContext);
            }

        } catch (Exception e) {
            callback.handleFault(org.apache.axis2.AxisFault.makeFault(e));
        }

    }

}