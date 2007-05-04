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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.CoordinationContext;
import org.apache.kandula.context.impl.ParticipantContext;
import org.apache.kandula.context.impl.SimpleCoordinationContext;
import org.apache.kandula.participant.ba.ParticipantOutHandler;
import org.apache.kandula.storage.StorageUtils;

public class TransactionInHandler extends AbstractHandler {
	/**
	 * Field log
	 */
	private static final Log log = LogFactory.getLog(ParticipantOutHandler.class);


	private static final long serialVersionUID = 2098581248112968550L;

	public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {
		KandulaResource resource;
		String wsaAction = msgContext.getWSAAction();
		if (!(Constants.WS_COOR_CREATE_COORDINATIONCONTEXT.equals(wsaAction))
                        && !(Constants.WS_COOR_REGISTER.equals(wsaAction))
                        && !(Constants.WS_AT_COMMIT.equals(wsaAction)) && !(Constants.WS_AT_ROLLBACK.equals(wsaAction))){
			ParticipantContext context = new ParticipantContext();
			SOAPHeader header = msgContext.getEnvelope().getHeader();
			OMElement coordinationElement = header
					.getFirstChildWithName(new QName(Constants.WS_COOR,
							"CoordinationContext"));
			if (coordinationElement == null) {
				throw new AxisFault(
						"Transaction Handler engaged.. No Coordination Context found");
			}
			CoordinationContext coorContext = new SimpleCoordinationContext(
					coordinationElement);
			context.setCoordinationContext(coorContext);

			StorageUtils.putContext(context,context.getID(),msgContext);
			msgContext.setProperty(AbstractContext.REQUESTER_ID,context.getID());
			msgContext.getOperationContext().setProperty(AbstractContext.REQUESTER_ID,context.getID());
			Parameter resourceFile =  msgContext.getParameter(Constants.KANDULA_RESOURCE);
			
			//Resource not given. Registration delayed to the business logic
			if (resourceFile != null) {
				try {
					resource = (KandulaResource) Class.forName((String) resourceFile.getValue())
							.newInstance();
					context.setResource(resource);
				} catch (Exception e) {
					log.fatal("TransactionInHandler: Activity ID :"+context.getCoordinationContext().getActivityID()+" : "+e);
					throw AxisFault.makeFault(e);
				}
                try{
				ParticipantUtility.registerParticipant(context,msgContext);
                }catch (Exception e ){
                    System.out.println(e);
                }
			}
		}
		return InvocationResponse.CONTINUE;
	}
}
