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

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.SimpleHTTPServer;
import org.apache.kandula.Constants;
import org.apache.kandula.ba.MixedBusinessActivity;
import org.apache.kandula.integration.KandulaDemoServiceKandulaDemoServiceSOAP11Port_httpStub;
import org.apache.kandula.integration.xsd.CreditOperation;
import org.apache.kandula.integration.xsd.DebitOperation;

public class BAParticipantRegistrationTest extends TestCase {

	private String repository = "target/testing-repository";

	private KandulaDemoServiceKandulaDemoServiceSOAP11Port_httpStub stub;

	private SimpleHTTPServer server;

	public BAParticipantRegistrationTest() throws Exception {
		this(BAParticipantRegistrationTest.class.getName());
	}

	public BAParticipantRegistrationTest(String testName) throws Exception {
		super(testName);
		ConfigurationContext configurationContext = ConfigurationContextFactory
				.createConfigurationContextFromFileSystem("target/initiator-repository",
						"target/initiator-repository" + "/axis2.xml");

		stub = new KandulaDemoServiceKandulaDemoServiceSOAP11Port_httpStub(configurationContext,
				"http://localhost:8081/axis2/services/KandulaBADemoService");
	}

	protected void setUp() throws Exception {
		File file = new File(repository);
		File configFile = new File(repository + "/axis2.xml");
		if (!file.exists()) {
			throw new Exception("repository directory " + file.getAbsolutePath()
					+ " does not exists");
		}
		ConfigurationContext er = ConfigurationContextFactory
				.createConfigurationContextFromFileSystem(file.getAbsolutePath(), configFile
						.getAbsolutePath());

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

	public void testRegistration() throws Exception {
		MixedBusinessActivity businessActivity = null;
		businessActivity = new MixedBusinessActivity(0);
		stub._getServiceClient().getOptions().setProperty(
				Constants.Configuration.TRANSACTION_CONTEXT, businessActivity.getContext());
		stub._getServiceClient().getOptions().setProperty(
				Constants.Configuration.PARTICIPANT_IDENTIFIER, "creditingBank");
		stub.creditOperation(new CreditOperation());
		try{
		stub._getServiceClient().getOptions().setProperty(
				Constants.Configuration.PARTICIPANT_IDENTIFIER, "debitingBank");
		stub.debitOperation(new DebitOperation());
		}catch (Exception e){
			
		}
		assertNotNull(businessActivity.getParticipant("debitingBank"));
		assertNotNull(businessActivity.getParticipant("creditingBank"));
//		businessActivity.cancelParticipant("debitingBank");
//		Thread.sleep(1000);
		businessActivity.setCallBack(new DemoServiceActivityMixedCallBack());
		businessActivity.complete();
	}
/*	public void testAtomicOutcome() throws Exception {
		AtomicBusinessActivity businessActivity = null;
		businessActivity = new AtomicBusinessActivity( 0);
		stub._getServiceClient().getOptions().setProperty(
				Constants.Configuration.TRANSACTION_CONTEXT, businessActivity.getContext());
		stub._getServiceClient().getOptions().setProperty(
				Constants.Configuration.PARTICIPANT_IDENTIFIER, "creditingBank");
		stub.creditOperation(new CreditOperation());
		try{
		stub._getServiceClient().getOptions().setProperty(
				Constants.Configuration.PARTICIPANT_IDENTIFIER, "debitingBank");
		stub.debitOperation(new DebitOperation());
		}catch (Exception e){
			
		}
		assertNotNull(businessActivity.getParticipant("debitingBank"));
		assertNotNull(businessActivity.getParticipant("creditingBank"));
//		businessActivity.cancelParticipant("debitingBank");
		Thread.sleep(1000);
		businessActivity.setCallBack(new DemoServiceActivityCallBack());
		businessActivity.complete();
	}*/
/*	public void testCancelParticipant() throws Exception {
		AtomicBusinessActivity businessActivity = null;
		businessActivity = new AtomicBusinessActivity( 0);
		stub._getServiceClient().getOptions().setProperty(
				Constants.Configuration.TRANSACTION_CONTEXT, businessActivity.getContext());
		stub._getServiceClient().getOptions().setProperty(
				Constants.Configuration.PARTICIPANT_IDENTIFIER, "creditingBank");
		stub.creditOperation(new CreditOperation());
		try{
		stub._getServiceClient().getOptions().setProperty(
				Constants.Configuration.PARTICIPANT_IDENTIFIER, "debitingBank");
		stub.debitOperation(new DebitOperation());
		}catch (Exception e){
			
		}
		assertNotNull(businessActivity.getParticipant("debitingBank"));
		assertNotNull(businessActivity.getParticipant("creditingBank"));
		businessActivity.cancelParticipant("debitingBank");
		assertNull(businessActivity.getParticipant("debitingBank"));
		Thread.sleep(1000);
		businessActivity.setCallBack(new DemoServiceActivityCallBack());
		businessActivity.complete();
	}*/
}