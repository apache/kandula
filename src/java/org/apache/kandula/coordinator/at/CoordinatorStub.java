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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.axis.AxisFault;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.kandula.coordinator.Callback;
import org.apache.kandula.coordinator.CoordinationService;
import org.apache.kandula.utils.AddressingHeaders;
import org.apache.kandula.utils.Service;
import org.apache.kandula.utils.TCPSnifferHelper;
import org.apache.kandula.wsat.CoordinatorBindingStub;

/**
 * @author Dasarath Weeratunge
 * 
 * Created on Jun 14, 2004
 */
public class CoordinatorStub extends CoordinatorBindingStub {

	public CoordinatorStub(Callback participant, EndpointReference epr)
			throws AxisFault, MalformedURLException {
		super(new URL(TCPSnifferHelper.redirect(epr.getAddress().toString())),
				new Service());
		CoordinationService cs = CoordinationService.getInstance();
		AddressingHeaders headers = new AddressingHeaders(epr, participant.getEndpointReference());
		headers.setFaultTo(cs.getFaultDispatcherService(participant));
		((Service) service).setAddressingHeaders(headers);
	}

}