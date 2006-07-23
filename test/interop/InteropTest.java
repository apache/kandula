/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package interop;

/**
 * @author <a href="mailto:thilina@opensource.lk">Thilina Gunarathne </a>
 */

import junit.framework.TestCase;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Constants;
import org.apache.kandula.initiator.TransactionManager;

public class InteropTest extends TestCase {

	EndpointReference coordinator = new EndpointReference(
			"http://localhost:8085/axis2/services/ActivationCoordinator");

	String axis2Repo = "target/initiator-repository";

	String axis2XML = "target/initiator-repository/axis2.xml";

	TestServiceStub stub = new TestServiceStub("target/initiator-repository",
			new EndpointReference(
					"http://localhost:8085/axis2/services/TestService"));

	public InteropTest() throws Exception {
		super(InteropTest.class.getName());
	}

	public InteropTest(String testName) throws Exception {
		super(testName);
	}

	public void testCompletionCommit() throws Exception {
		TransactionManager tm = new TransactionManager(Constants.WS_AT,
				coordinator, axis2Repo, axis2XML);
		tm.begin(true);
		tm.commit();
	}

	public void testCompletionRollback() throws Exception {
		TransactionManager tm = new TransactionManager(Constants.WS_AT,
				coordinator, axis2Repo, axis2XML);
		tm.begin(true);
		tm.rollback();
	}

	public void testCommit() throws Exception {
		TransactionManager tm = new TransactionManager(Constants.WS_AT,
				coordinator, axis2Repo, axis2XML);
		tm.begin(true);
		stub.commitOperation();
		tm.commit();
	}

	public void testRollback() throws Exception {
		TransactionManager tm = new TransactionManager(Constants.WS_AT,
				coordinator, axis2Repo, axis2XML);
		tm.begin(true);
		stub.rollbackOperation();
		tm.rollback();
	}

	public void testPhase2Rollback() throws Exception {
		TransactionManager tm = new TransactionManager(Constants.WS_AT,
				coordinator, axis2Repo, axis2XML);
		tm.begin(true);
		stub.phase2RollbackOperation();

		boolean done = false;
		try {
			tm.commit();
		} catch (Exception e) {
			done = true;
		}
		assertTrue(done);
	}

	public void testReadonly() throws Exception {
		TransactionManager tm = new TransactionManager(Constants.WS_AT,
				coordinator, axis2Repo, axis2XML);
		tm.begin(true);
		stub.readonlyOperation();
		tm.commit();
	}

	public void testVolatileAndDurable() throws Exception {
		TransactionManager tm = new TransactionManager(Constants.WS_AT,
				coordinator, axis2Repo, axis2XML);
		tm.begin(true);
		stub.readonlyOperation();
		tm.commit();
	}
	public void testEarlyAborted() throws Exception {
		TransactionManager tm = new TransactionManager(Constants.WS_AT,
				coordinator, axis2Repo, axis2XML);
		tm.begin(true);
		stub.earlyAbortedOperation();
	}
}