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
package interop;

import org.apache.kandula.Constants;
import org.apache.kandula.participant.KandulaResource;
import org.apache.kandula.participant.at.KandulaAtomicResource;
import org.apache.kandula.participant.at.Vote;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class VolatileAndDurableTestVolatileResource extends KandulaAtomicResource {

	/**
	 * 
	 */
	public VolatileAndDurableTestVolatileResource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public boolean commit() {
		System.out.println("Commited");
		return true;
	}

	public void rollback() {
		System.out.println("rollback");

	}

	public Vote prepare() {
		return Vote.READ_ONLY;
	}

	public String getProtocol() {
		return Constants.WS_AT_VOLATILE2PC;
	}

}