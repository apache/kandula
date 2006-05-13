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

import java.io.File;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.InOnlyAxisOperation;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.apache.axis2.receivers.RawXMLINOnlyMessageReceiver;
import org.apache.kandula.Constants;
import org.apache.kandula.utility.KandulaListener;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ParticipantServiceListener {

	private static ParticipantServiceListener instance = null;

	private EndpointReference epr = null;

	private ParticipantServiceListener() {
		super();
	}

	public static ParticipantServiceListener getInstance() {
		if (instance == null) {
			instance = new ParticipantServiceListener();
		}
		return instance;
	}

	public EndpointReference getEpr() throws IOException {
		if (epr == null) {
			this.epr = setupListener();
		}
		return this.epr;
	}

	private EndpointReference setupListener() throws IOException {
		String className = ParticipantPortTypeRawXMLSkeleton.class.getName();
		String serviceName = "ParticipantPortType";
		AxisService service = new AxisService(serviceName);
		service.addParameter(new Parameter(
				AbstractMessageReceiver.SERVICE_CLASS, className));
		service.setFileName((new File(className)).toURL());
		QName prepareOperationName = new QName(Constants.WS_COOR,
				"prepareOperation");
		AxisOperation prepareOperationDesc;
		String prepareMapping = Constants.WS_AT_PREPARE;
		prepareOperationDesc = new InOnlyAxisOperation();
		prepareOperationDesc.setName(prepareOperationName);
		prepareOperationDesc
				.setMessageReceiver(new RawXMLINOnlyMessageReceiver());
		// Adding the WSA Action mapping to the operation
		service.mapActionToOperation(prepareMapping, prepareOperationDesc);
		service.addOperation(prepareOperationDesc);

		QName commitOperationName = new QName(Constants.WS_COOR,
				"commitOperation");
		AxisOperation commitOperationDesc;
		String commitMapping = Constants.WS_AT_COMMIT;
		commitOperationDesc = new InOnlyAxisOperation();
		commitOperationDesc.setName(commitOperationName);
		commitOperationDesc
				.setMessageReceiver(new RawXMLINOnlyMessageReceiver());
		// Adding the WSA Action mapping to the operation
		service.mapActionToOperation(commitMapping, commitOperationDesc);
		service.addOperation(commitOperationDesc);

		QName rollbackOperationName = new QName(Constants.WS_COOR,
				"rollbackOperation");
		AxisOperation rollbackOperationDesc;
		String rollbackMapping = Constants.WS_AT_ROLLBACK;
		rollbackOperationDesc = new InOnlyAxisOperation();
		rollbackOperationDesc.setName(rollbackOperationName);
		rollbackOperationDesc
				.setMessageReceiver(new RawXMLINOnlyMessageReceiver());
		// Adding the WSA Action mapping to the operation
		service.mapActionToOperation(rollbackMapping, rollbackOperationDesc);
		service.addOperation(rollbackOperationDesc);

		KandulaListener listener = KandulaListener.getInstance();
		listener.addService(service);
		listener.start();
		return new EndpointReference(listener.getHost() + serviceName);
	}
}