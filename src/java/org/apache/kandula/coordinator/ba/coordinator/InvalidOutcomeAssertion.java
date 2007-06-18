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


/**
 * This exception should be thrown when creating a Business Activity Coordination
 * Context with an unknown Outcome assertion.
 * 
 * Supported outcome asserations are listed in the Coordinator implementation.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class InvalidOutcomeAssertion extends Exception {

	/**
	 * Generated UUID.
	 */
	private static final long serialVersionUID = -5173230826976571976L;
}
