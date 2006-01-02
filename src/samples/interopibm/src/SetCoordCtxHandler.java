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

import javax.xml.soap.SOAPHeader;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.ws.transaction.coordinator.CoordinationContext;

/**
 * @author Dasarath Weeratunge
 */
public class SetCoordCtxHandler extends BasicHandler {
	private static CoordinationContext ctx;

	public static void setCtx(CoordinationContext ctx) {
		SetCoordCtxHandler.ctx = ctx;
	}

	public void invoke(MessageContext msgContext) throws AxisFault {
		if (ctx != null) {
			try {
				SOAPHeader header = msgContext.getCurrentMessage().getSOAPEnvelope().getHeader();
				ctx.toSOAPHeaderElement(header);
			} catch (Exception e) {
				throw AxisFault.makeFault(e);
			}
		}
	}
}