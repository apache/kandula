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
package org.apache.kandula.utility;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.transport.http.SimpleHTTPServer;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class KandulaListener {

	private static KandulaListener instance = null;

	private ConfigurationContext responseConfigurationContext;

	protected static AxisOperation[] operations;

	private SimpleHTTPServer receiver = null;

	private boolean serverStarted = false;

	public int serverPort;

	private KandulaListener() throws IOException {
		KandulaConfiguration configuration = KandulaConfiguration
				.getInstance();
		responseConfigurationContext = ConfigurationContextFactory
				.createConfigurationContextFromFileSystem(
						configuration.getKandulaListenerRepository(),
						configuration.getKandulaListenerAxis2Xml());
		try {
			serverPort = Integer.parseInt(KandulaConfiguration
					.getInstance().getKadulaListenerPort());
		} catch (Exception e) {
			serverPort = 5059;
		}
		while (receiver == null) {

			receiver = new SimpleHTTPServer(responseConfigurationContext,
					serverPort);

		}
	}

	public static KandulaListener getInstance() throws IOException {
		if (instance == null) {
			instance = new KandulaListener();
		}
		return instance;
	}

	public void start() throws IOException {
		if (!serverStarted) {

			receiver.start();
			serverStarted = true;
			System.out.print("Server started on port " + serverPort + ".....");
		}

	}

	public void stop() {
		receiver.stop();
		serverStarted = false;
	}

	/**
	 * @param service
	 * @throws AxisFault
	 *             To add services with only one operation, which is the
	 *             frequent case in reponses
	 */
	public void addService(AxisService service) throws AxisFault {

		service.setClassLoader(Thread.currentThread().getContextClassLoader());
		HashMap allServices = responseConfigurationContext
				.getAxisConfiguration().getServices();

		if (allServices.get(service.getName()) == null) {

			responseConfigurationContext.getAxisConfiguration().addService(
					service);
			//TODO : check how to do this or this is neccessary anymore
			//			Utils.resolvePhases(receiver.getSystemContext()
			//					.getAxisConfiguration(), service);
		}

	}

	public String getHost() throws UnknownHostException {
		return "http://"
				+ InetAddress.getLocalHost().getHostAddress()
				+ ":"
				+ KandulaConfiguration.getInstance()
						.getKadulaListenerPortForEPR() + "/axis2/services/";
	}
}