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
public class CompletionInitiatorServiceListener {

	private static CompletionInitiatorServiceListener instance = null;

	private EndpointReference epr = null;

	private CompletionInitiatorServiceListener() {
		super();
	}

	public static CompletionInitiatorServiceListener getInstance() {
		if (instance == null) {
			instance = new CompletionInitiatorServiceListener();
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
		String className = CompletionInitiatorPortTypeRawXMLSkeleton.class
				.getName();
		String serviceName = "CompletionInitiatorPortType";
		AxisService service = new AxisService(serviceName);
		service.addParameter(new Parameter(
				AbstractMessageReceiver.SERVICE_CLASS, className));
		service.setFileName((new File(className)).toURL());

		QName committedOperationName = new QName(Constants.WS_COOR,
				"committedOperation");
		AxisOperation committedOperationDesc;
		String committedMapping = Constants.WS_AT_COMMITTED;
		committedOperationDesc = new InOnlyAxisOperation();
		committedOperationDesc.setName(committedOperationName);
		committedOperationDesc
				.setMessageReceiver(new RawXMLINOnlyMessageReceiver());
		// Adding the WSA Action mapping to the operation
		service.mapActionToOperation(committedMapping, committedOperationDesc);
		service.addOperation(committedOperationDesc);

		QName abortedOperationName = new QName(Constants.WS_COOR,
				"abortedOperation");
		AxisOperation abortedOperationDesc;
		String abortedMapping = Constants.WS_AT_ABORTED;
		abortedOperationDesc = new InOnlyAxisOperation();
		abortedOperationDesc.setName(abortedOperationName);
		abortedOperationDesc
				.setMessageReceiver(new RawXMLINOnlyMessageReceiver());
		// Adding the WSA Action mapping to the operation
		service.mapActionToOperation(abortedMapping, abortedOperationDesc);
		service.addOperation(abortedOperationDesc);

		KandulaListener listener = KandulaListener.getInstance();
		listener.addService(service);
		listener.start();
		return new EndpointReference(listener.getHost() + serviceName);
	}
}