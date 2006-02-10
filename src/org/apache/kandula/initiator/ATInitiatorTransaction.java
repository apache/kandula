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
package org.apache.kandula.initiator;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.context.CoordinationContext;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ATInitiatorTransaction {

	private EndpointReference coordinationEPR;

	private CoordinationContext coordinationContext;

	public ATInitiatorTransaction(EndpointReference coordinationEPR) {
		this.coordinationEPR = coordinationEPR;
	}

	/**
	 * @return Returns the coordinationEPR.
	 */
	public EndpointReference getCoordinationEPR() {
		return coordinationEPR;
	}

	/**
	 * @param coordinationEPR
	 *            The coordinationEPR to set.
	 */
	public void setCoordinationEPR(EndpointReference coordinationEPR) {
		this.coordinationEPR = coordinationEPR;
	}

	/**
	 * @return Returns the coordinationContext.
	 */
	public CoordinationContext getCoordinationContext() {
		return coordinationContext;
	}

	/**
	 * @param coordinationContext
	 *            The coordinationContext to set.
	 */
	public void setCoordinationContext(CoordinationContext coordinationContext) {
		this.coordinationContext = coordinationContext;
	}
}