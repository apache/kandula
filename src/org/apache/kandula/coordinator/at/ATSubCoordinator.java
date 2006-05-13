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
package org.apache.kandula.coordinator.at;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.impl.ATActivityContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.InvalidProtocolException;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ATSubCoordinator extends ATCoordinator {

	public EndpointReference addParticipant(AbstractContext context,
			String protocol, EndpointReference participantEPR)
			throws AbstractKandulaException {
		ATActivityContext atContext = (ATActivityContext) context;
		if (protocol.equals(Constants.WS_AT_DURABLE2PC)) {
			if (!atContext.getSubDurableRegistered()) {
				// have to register with the parent coordinator
				atContext.setSubDurableRegistered(true);
			}
			return atContext.addParticipant(participantEPR, protocol);
		}

		else if (protocol.equals(Constants.WS_AT_VOLATILE2PC)) {
			if (!atContext.getSubDurableRegistered()) {
				// have to register with the parent coordinator
				atContext.setSubDurableRegistered(true);
			}
			return atContext.addParticipant(participantEPR, protocol);
		} else
			throw new InvalidProtocolException();
	}

	public void commitOperation(String id) throws AbstractKandulaException {
		throw new InvalidProtocolException(
				"This activity is a Sub Ordinate activity. Completion Protocol not supported.");
	}

	public void rollbackOperation(String id) throws AbstractKandulaException {
		throw new InvalidProtocolException(
				"This activity is a Sub Ordinate activity. Completion Protocol not supported.");
	}

}