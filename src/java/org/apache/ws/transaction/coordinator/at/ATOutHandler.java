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
package org.apache.ws.transaction.coordinator.at;

import javax.xml.soap.SOAPHeader;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.ws.transaction.coordinator.at.AT;
import org.apache.ws.transaction.coordinator.at.ATManager;

/**
 * @author Dasarath Weeratunge
 */
public class ATOutHandler extends BasicHandler {
	ATManager tm = ATManager.getInstance();

	public void invoke(MessageContext msgContext) throws AxisFault {
		AT tx = tm.getTransaction();
		if (tx != null) {
			try {
				SOAPHeader header = msgContext.getCurrentMessage().getSOAPEnvelope().getHeader();
				tx.getCoordinationContex().toSOAPHeaderElement(header);
			} catch (Exception e) {
				throw AxisFault.makeFault(e);
			}
		}
	}
}