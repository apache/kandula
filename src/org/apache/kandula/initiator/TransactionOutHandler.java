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
package org.apache.kandula.initiator;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.MessageInformationHeaders;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.axis2.soap.SOAPHeader;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.coordination.CoordinationContext;
import org.apache.kandula.faults.AbstractKandulaException;

public class TransactionOutHandler extends AbstractHandler {
	
	public void invoke(MessageContext msgContext) throws AxisFault {
		
		AbstractContext context;
		try {
			String wsaAction = msgContext.getWSAAction();
			if ((wsaAction != Constants.WS_COOR_CREATE_COORDINATIONCONTEXT)
					&& (wsaAction != Constants.WS_COOR_REGISTER)) {
				context = TransactionManager.getTransaction();
				MessageInformationHeaders messageInformationHeaders = msgContext
				.getMessageInformationHeaders();
				SOAPHeader soapHeader = msgContext.getEnvelope().getHeader();
				CoordinationContext coorContext = context
				.getCoordinationContext();
				soapHeader.addChild(coorContext.toOM());
			}
		} catch (AbstractKandulaException e) {
			throw new AxisFault(e);
		} 
	}
}

