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
package org.apache.ws.transaction.participant.j2ee.handler;


import java.util.Iterator;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.ws.transaction.participant.j2ee.TransactionBridge;
import org.apache.ws.transaction.participant.j2ee.TransactionManagerGlueFactory;
import org.apache.ws.transaction.wscoor._CoordinationContext;


public class TransactionHandler extends BasicHandler {
	static TransactionManager tm= TransactionManagerGlueFactory.getInstance().getTransactionManagerGlue().getTransactionManager();
	static TransactionBridge bridge= TransactionBridge.getInstance();

	static private ThreadLocal threadInfo= new ThreadLocal();

	public void invoke(MessageContext msgContext) throws AxisFault {
		try {
			if (msgContext.isClient()) {
				Transaction localTx= tm.getTransaction();
				if (localTx != null) {
					SOAPHeader header= msgContext.getCurrentMessage().getSOAPEnvelope().getHeader();
					_CoordinationContext coordinationContext= bridge.exportJ2eeTransaction(localTx);
					coordinationContext.toSOAPHeaderElement(header);
				}
			}
			else {
				if (msgContext.getPastPivot()) {
					if (threadInfo.get() != null) {
						tm.suspend();
						threadInfo.set(null);
					}
				}
				else {
					SOAPHeader header= msgContext.getCurrentMessage().getSOAPEnvelope().getHeader();
					Iterator iter= header.getChildElements();
					while (iter.hasNext()) {
						SOAPElement e= (SOAPElement)iter.next();
						if (_CoordinationContext.is(e)) {
							_CoordinationContext coordinationContext=
								new _CoordinationContext(e);
							Transaction localTx=
								bridge.importWSTransaction(coordinationContext);
							tm.resume(localTx);
							threadInfo.set(localTx);
							return;
						}
					}
				}
			}
		}
		catch (Exception e) {
			throw AxisFault.makeFault(e);
		}
	}
}
