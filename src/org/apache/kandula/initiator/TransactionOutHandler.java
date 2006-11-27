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

import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.kandula.Constants;
import org.apache.kandula.context.CoordinationContext;
import org.apache.kandula.faults.AbstractKandulaException;

public class TransactionOutHandler extends AbstractHandler {

	private static final long serialVersionUID = 4133392345837905499L;

	public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {

		InitiatorTransaction initiatorTransaction;
		try {
			String wsaAction = msgContext.getWSAAction();
			if ((wsaAction != Constants.WS_COOR_CREATE_COORDINATIONCONTEXT)
					&& (wsaAction != Constants.WS_COOR_REGISTER)
					&& (wsaAction != Constants.WS_AT_COMMIT)
					&& (wsaAction != Constants.WS_AT_ROLLBACK)) {
				initiatorTransaction = TransactionManager.getTransaction();
				SOAPHeader soapHeader = msgContext.getEnvelope().getHeader();
				CoordinationContext coorContext = initiatorTransaction
						.getCoordinationContext();
				soapHeader.addChild(coorContext.toOM());
			}
		} catch (AbstractKandulaException e) {
			throw new AxisFault(e);
		}
		return InvocationResponse.CONTINUE;
	}
}
