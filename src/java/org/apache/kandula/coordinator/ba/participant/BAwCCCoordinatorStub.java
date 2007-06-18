/*
 * Copyright 2007 The Apache Software Foundation.
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
 *  @author Hannes Erven, Georg Hicker
 */
package org.apache.kandula.coordinator.ba.participant;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.axis.AxisFault;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.kandula.utils.AddressingHeaders;
import org.apache.kandula.utils.Service;
import org.apache.kandula.utils.TCPSnifferHelper;
import org.apache.kandula.wsba.BusinessAgreementWithParticipantCompletionCoordinatorPortTypeBindingStub;

/**
 * Convenience Wrapper around
 * BusinessAgreementWithParticipantCompletionCoordinatorPortTypeBindingStub
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 * 
 */
public class BAwCCCoordinatorStub extends
		BusinessAgreementWithParticipantCompletionCoordinatorPortTypeBindingStub
		implements BACoordinatorStub {
	/**
	 * The peer's epr's addressing headers.
	 */
	final AddressingHeaders headers;

	/**
	 * Constructor.
	 * @param epr The peer's endpoint reference.
	 * @throws AxisFault Fault.
	 * @throws MalformedURLException Fault.
	 */
	public BAwCCCoordinatorStub(final EndpointReference epr) throws AxisFault,
			MalformedURLException {
		super(new URL(TCPSnifferHelper.redirect(epr.getAddress().toString())),
				new Service());
		this.headers = new AddressingHeaders(epr, null);
		((Service) this.service).setAddressingHeaders(this.headers);
	}

	/**
	 * The epr's addressing headers.
	 * @return ..
	 */
	public AddressingHeaders getAddressingHeaders() {
		return this.headers;
	}

}
