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

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.OutOnlyAxisOperation;
import org.apache.kandula.Constants;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.utility.KandulaConfiguration;
import org.apache.kandula.wsat.AbstractATNotifierStub;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class CompletionInitiatorPortTypeRawXMLStub extends
		AbstractATNotifierStub {
	public static final String AXIS2_HOME = ".";

	/**
	 * Constructor
	 * 
	 * @throws AbstractKandulaException
	 */
	public CompletionInitiatorPortTypeRawXMLStub(
			EndpointReference targetEndpoint) throws AbstractKandulaException {
		super(KandulaConfiguration.getInstance().getCoordinatorRepo(),
				KandulaConfiguration.getInstance()
						.getCoordinatorAxis2Conf(), new AxisService(
						"CompletionInitiatorPortType"));
		this.toEPR = targetEndpoint;

		//creating the operations
		AxisOperation operation;
		operations = new AxisOperation[2];

		operation = new OutOnlyAxisOperation();
		operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
				"committedOperation"));
		operations[0] = operation;
		service.addOperation(operation);

		operation = new OutOnlyAxisOperation();
		operation.setName(new javax.xml.namespace.QName(Constants.WS_AT,
				"abortedOperation"));
		operations[1] = operation;
		service.addOperation(operation);

	}

	public void committedOperation() throws AbstractKandulaException {
		this.notify("Committed", Constants.WS_AT_COMMITTED, 0, null);
	}

	public void abortedOperation() throws AbstractKandulaException {
		this.notify("Aborted", Constants.WS_AT_ABORTED, 1, null);
	}

}