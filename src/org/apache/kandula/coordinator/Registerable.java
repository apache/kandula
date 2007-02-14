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
package org.apache.kandula.coordinator;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.coordinator.at.ATCoordinator;
import org.apache.kandula.coordinator.ba.BACoordinator;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.InvalidProtocolException;

public interface Registerable {
	public static final class Factory {
		public static Registerable newRegisterable(String coordinationType)
				throws InvalidProtocolException {
			if (Constants.WS_AT.equals(coordinationType)) {
				return new ATCoordinator();
			} else if ((Constants.WS_BA_ATOMIC.equals(coordinationType))
					|| (Constants.WS_BA_MIXED.equals(coordinationType))) {
				return new BACoordinator();
			} else {
				throw new InvalidProtocolException(
						"Unsupported Coordination Type");
			}
		}

		private Factory() {
		}
	}

	public EndpointReference register(AbstractContext context, String protocol,
			EndpointReference participantEPR, String enlistmentID) throws AbstractKandulaException;
}