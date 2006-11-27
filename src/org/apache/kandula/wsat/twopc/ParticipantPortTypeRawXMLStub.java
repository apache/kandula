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
package org.apache.kandula.wsat.twopc;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.OutOnlyAxisOperation;
import org.apache.kandula.Constants;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.utility.KandulaConfiguration;
import org.apache.kandula.wsat.AbstractATNotifierStub;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ParticipantPortTypeRawXMLStub extends AbstractATNotifierStub {
	public static final String AXIS2_HOME = ".";

	/**
	 * Constructor
	 * 
	 * @throws AbstractKandulaException
	 */
	public ParticipantPortTypeRawXMLStub() throws AbstractKandulaException {
		super(KandulaConfiguration.getInstance()
				.getCoordinatorAxis2ConfigurationContext());

		// creating the operations
		AxisOperation operation;
		operations = new AxisOperation[3];

		operation = new OutOnlyAxisOperation();
		operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
				"prepareOperation"));
		operations[0] = operation;
		service.addOperation(operation);

		operation = new OutOnlyAxisOperation();
		operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
				"commitOperation"));
		operations[1] = operation;
		service.addOperation(operation);

		operation = new OutOnlyAxisOperation();
		operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
				"rollbackOperation"));
		operations[2] = operation;
		service.addOperation(operation);
	}

	public void prepareOperation(EndpointReference targetEndpoint)
			throws AbstractKandulaException {
		this.toEPR = targetEndpoint;
		this.notify("Prepare", Constants.WS_AT_PREPARE, 0, null);

	}

	public void commitOperation(EndpointReference targetEndpoint)
			throws AbstractKandulaException {
		this.toEPR = targetEndpoint;
		this.notify("Commit", Constants.WS_AT_COMMIT, 1, null);

	}

	public void rollbackOperation(EndpointReference targetEndpoint)
			throws AbstractKandulaException {
		this.toEPR = targetEndpoint;
		this.notify("Rollback", Constants.WS_AT_ROLLBACK, 2, null);
	}

}