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

package org.apache.kandula.coordinator.ba.coordinator;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.kandula.wscoor.CreateCoordinationContextTypeCurrentContext;

/**
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class BAwithMixedOutcomeCoordinator extends BACoordinator {

	/**
	 * Create a new mixed outcome BA Coordinator.
	 * 
	 * @param superContext The super context, if any
	 * @throws MalformedURIException Invalid URI.
	 * @throws InvalidOutcomeAssertion Invalid Outcome Type
	 */
	public BAwithMixedOutcomeCoordinator(
			final CreateCoordinationContextTypeCurrentContext superContext
	) throws MalformedURIException, InvalidOutcomeAssertion {
		super(BACoordinator.COORDINATION_TYPE__MIXED, superContext);
	}

	/**
	 * Check if additional participants are allowed to register. With mixed
	 * outcome, this is always true.
	 * @return true
	 */
	boolean isRegistrationPossible() {
		return true;
	}

}
