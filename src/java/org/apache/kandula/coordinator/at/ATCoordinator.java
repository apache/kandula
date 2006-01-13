/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *  
 */
package org.apache.kandula.coordinator.at;

import javax.xml.namespace.QName;

import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.wsat.CompletionCoordinatorPortType;
import org.apache.kandula.wsat.CoordinatorPortType;

public interface ATCoordinator extends Coordinator, CoordinatorPortType,
		CompletionCoordinatorPortType {

	final QName PARTICIPANT_REF = new QName("http://ws.apache.org/kandula",
			"ParticipantRef");

	final String COORDINATION_TYPE_ID = "http://schemas.xmlsoap.org/ws/2004/10/wsat";

	final String PROTOCOL_ID_COMPLETION = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Completion";

	final String PROTOCOL_ID_VOLATILE_2PC = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Volatile2PC";

	final String PROTOCOL_ID_DURABLE_2PC = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Durable2PC";

}