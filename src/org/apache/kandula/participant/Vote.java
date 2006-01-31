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
package org.apache.kandula.participant;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class Vote {

	public static Vote READ_ONLY = new Vote("VoteReadOnly");

	public static Vote ABORT = new Vote("VoteAbort");

	public static Vote PREPARED = new Vote("VotePrepared");

	public static Vote NONE = new Vote("");

	private String state;

	public Vote(String outcome) {
		this.state = outcome;
	}

	public String getOutcome() {
		return state;
	}
}