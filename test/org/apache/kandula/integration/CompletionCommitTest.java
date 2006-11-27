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

package org.apache.kandula.integration;

/**
 * @author <a href="mailto:thilina@opensource.lk">Thilina Gunarathne </a>
 */

import java.io.File;

import junit.framework.TestCase;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.SimpleHTTPServer;
import org.apache.kandula.Constants;
import org.apache.kandula.initiator.TransactionManager;

public class CompletionCommitTest extends TestCase {

	private String repository = "target/testing-repository";

	private KandulaDemoServiceStub stub;

	private SimpleHTTPServer server;

	public CompletionCommitTest() throws Exception {
		super(CompletionCommitTest.class.getName());
		stub = new KandulaDemoServiceStub(
				"target/initiator-repository",
				new EndpointReference(
						"http://localhost:8081/axis2/services/KandulaDemoService"));
	}

	public CompletionCommitTest(String testName) throws Exception {
		super(testName);
		stub = new KandulaDemoServiceStub(
				"target/initiator-repository",
				new EndpointReference(
						"http://localhost:8081/axis2/services/KandulaDemoService"));
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

	public void testEchoXMLASync() throws Exception {
		TransactionManager tm = new TransactionManager(
				Constants.WS_AT,
				new EndpointReference(
						"http://localhost:8081/axis2/services/ActivationCoordinator"),
				"target/initiator-repository",
				"target/initiator-repository/axis2.xml");
		tm.begin(false);
		 
		 stub.creditOperation();
		 tm.commit();
		Thread.sleep(5000);
		
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