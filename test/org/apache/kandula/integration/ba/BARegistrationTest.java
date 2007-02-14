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

package org.apache.kandula.integration.ba;

import java.io.File;

import junit.framework.TestCase;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.SimpleHTTPServer;
import org.apache.kandula.Constants;
import org.apache.kandula.ba.AtomicBusinessActivity;
import org.apache.kandula.coordinator.Coordinator;

public class BARegistrationTest extends TestCase {

	private String repository = "target/testing-repository";

	private SimpleHTTPServer server;

	public BARegistrationTest() throws Exception {
		super(BARegistrationTest.class.getName());
	}

	public BARegistrationTest(String testName) throws Exception {
		super(testName);
	}

	protected void setUp() throws Exception {
		File file = new File(repository);
		File configFile = new File(repository + "/axis2.xml");
		if (!file.exists()) {
			throw new Exception("repository directory "
					+ file.getAbsolutePath() + " does not exists");
		}
		ConfigurationContext er = ConfigurationContextFactory
				.createConfigurationContextFromFileSystem(file
						.getAbsolutePath(), configFile.getAbsolutePath());

		server = new SimpleHTTPServer(er, 8081);

		try {
			server.start();
			System.out.print("Server started on port " + 8081 + ".....");
		} finally {

		}
	}

	protected void tearDown() throws Exception {
		server.stop();
	}

	public void testRegisteration() throws Exception {
		AtomicBusinessActivity businessActivity = new AtomicBusinessActivity(1000);
		Coordinator coordinator = new Coordinator();
		EndpointReference epr= coordinator.registerParticipant(businessActivity.getContext(),Constants.WS_BA_CC,new EndpointReference("http://ws.apache.org/axis2"),null);
		assertNotNull(epr);
	}
	

//	public void testEchoXMLSync() throws Exception {
//		// TransactionManager tm = new TransactionManager(
//		// Constants.WS_AT,
//		// new EndpointReference(
//		// "http://localhost:8081/axis2/services/ActivationCoordinator"));
//		// tm.begin("target/initiator-repository",
//		// "target/initiator-repository/axis2.xml", false);
//		// try {
//		// stub.creditOperation();
//		// } catch (Exception e) {
//		// tm.rollback();
//		// }
//		// tm.commit();
//	}
}