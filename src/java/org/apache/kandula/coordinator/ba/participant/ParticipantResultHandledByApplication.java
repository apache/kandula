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

/**
 * This class represents a no-op result. Use it as return value from the
 * on(Message) result handlers if you want to handle the transition to the
 * next state yourself by calling the appropriate tell(Message) method.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 * 
 */

public final class ParticipantResultHandledByApplication extends ParticipantResult
		implements ParticipantCancelResult, ParticipantCompensateResult,
		ParticipantCloseResult, ParticipantCompleteResult {

	private ParticipantResultHandledByApplication() {
		super();
	}

	/**
	 * The one and only instance
	 */
	public static final ParticipantResultHandledByApplication instance = new ParticipantResultHandledByApplication();

}
