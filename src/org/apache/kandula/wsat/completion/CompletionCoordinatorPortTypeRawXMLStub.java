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
package org.apache.kandula.wsat.completion;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.OutOnlyAxisOperation;
import org.apache.kandula.Constants;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.wsat.AbstractATNotifierStub;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class CompletionCoordinatorPortTypeRawXMLStub extends
		AbstractATNotifierStub {
	public static final String AXIS2_HOME = ".";

	/**
	 * Constructor
	 * 
	 * @throws AxisFault
	 */
	public CompletionCoordinatorPortTypeRawXMLStub(String axis2Home,
			String axis2Xml, EndpointReference targetEndpoint)
			throws AbstractKandulaException {
		super(axis2Home, axis2Xml, new AxisService(
				"CompletionCoordinatorPortType"));
		this.toEPR = targetEndpoint;

		//creating the operations
		AxisOperation operation;
		operations = new AxisOperation[2];

		operation = new OutOnlyAxisOperation();
		operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
				"commitOperation"));
		operations[0] = operation;
		service.addOperation(operation);

		operation = new OutOnlyAxisOperation();
		operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
				"rollbackOperation"));
		operations[1] = operation;
		service.addOperation(operation);
	}

	public void commitOperation() throws AbstractKandulaException {
		//TODO must send reply to epr
		this.notify("Commit", Constants.WS_AT_COMMIT, 0, null);

	}

	public void rollbackOperation() throws AbstractKandulaException {
		//TODO must send reply to EPR
		this.notify("Rollback", Constants.WS_AT_ROLLBACK, 1, null);

	}

}