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
package org.apache.kandula.geronimo;

import java.util.Iterator;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.kandula.coordinator.CoordinationContext;

public class TxHandler extends BasicHandler {
	private Bridge bridge = Bridge.getInstance();

	private TransactionManager tm = bridge.getTM();

	static private ThreadLocal threadInfo = new ThreadLocal();

	public void invoke(MessageContext mc) throws AxisFault {
		try {
			if (mc.getCurrentMessage() == null)
				return;

			if (mc.isClient() && !mc.getPastPivot()) {
//				Transaction tx = tm.getTransaction();
//				if (tx == null)
//					return;
//
//				SOAPHeader header = mc.getCurrentMessage().getSOAPEnvelope().getHeader();
//				CoordinationContext ctx = bridge.mediate(tx);
//				ctx.toSOAPHeaderElement(header);
			} else {
				if (mc.getPastPivot()) {
					if (threadInfo.get() == null)
						return;

					tm.suspend();
					threadInfo.set(null);
				} else {
					SOAPHeader header = mc.getCurrentMessage().getSOAPEnvelope().getHeader();
					Iterator iter = header.getChildElements();
					while (iter.hasNext()) {
						SOAPElement e = (SOAPElement) iter.next();
						if (CoordinationContext.is(e)) {
							CoordinationContext ctx = new CoordinationContext(e);
							Transaction tx = bridge.mediate(ctx);
							tm.resume(tx);
							threadInfo.set(tx);
							return;
						}
					}
				}
			}
		} catch (Exception e) {
			throw AxisFault.makeFault(e);
		}
	}
}