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

import java.util.Iterator;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.ws.transaction.coordinator.CoordinationContext;

/**
 * @author Dasarath Weeratunge
 */
public class TxHandler extends BasicHandler {
	TransactionManagerImpl tm = TransactionManagerImpl.getInstance();

	public void invoke(MessageContext mc) throws AxisFault {
		if (mc.isClient()) {
			TransactionImpl tx = tm.getTransaction();
			if (tx != null) {
				try {
					SOAPHeader header = mc.getCurrentMessage().getSOAPEnvelope().getHeader();
					tx.getCoordinationContex().toSOAPHeaderElement(header);
				} catch (Exception e) {
					throw AxisFault.makeFault(e);
				}
			}
		}
		else {
			if (mc.getPastPivot())
				tm.forget();
			else {
				try {
				SOAPHeader header = mc.getCurrentMessage().getSOAPEnvelope().getHeader();
				Iterator iter = header.getChildElements();
				while (iter.hasNext()) {
					SOAPElement e = (SOAPElement) iter.next();
					if (CoordinationContext.is(e)) {
						CoordinationContext ctx = new CoordinationContext(e);
						tm.resume(new TransactionImpl(ctx));
						return;
					}
				}
				} catch (SOAPException e){
					e.printStackTrace();
					throw new AxisFault();
				}
			}

		}
	}
}