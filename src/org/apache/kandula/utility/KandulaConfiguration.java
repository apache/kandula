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

import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.KandulaGeneralException;

public class KandulaConfiguration {
	static final String PROPERTY_FILE = "endpoints.conf";

	// static final String PROTOCOL_PROPERTY = "protocol";

	static final String HOST_PROPERTY = "host";

	static final String PORT_PROPERTY = "port";

	static final String TCPMON_ENABLE = "tcpmon_enable";

	static final String PARTICIPANT_REPO = "PARTICIPANT_REPOSITORY";

	static final String PARTICIPANT_AXIS2_CONF = "PARTICIPANT_AXIS2_CONF";

	static final String KANDULA_LISTENER_REPO = "KANDULA_LISTENER_REPOSITORY";

	static final String KANDULA_LISTENER_AXIS2XML = "KANDULA_LISTENER_AXIS2XML";

	static final String LISTENER_PORT = "KANDULA_LISTENER_PORT";

	static final String COORDINATOR_AXIS2XML = "COORDINATOR_AXIS2XML";

	static final String COORDINATOR_REPOSITORY = "COORDINATOR_REPOSITORY";

	private static KandulaConfiguration instance = null;

	Properties properties = null;

	String location = null;

	String participantRepository = null;

	String participantAxis2Xml = null;

	String kandulaListenerRepository = null;

	String kandulaListenerAxis2Xml = null;

	String kandulaListenerPort = null;

	String coordinatorRepo;

	String coordinatorAxis2Conf;

	String debug = "false";

	private KandulaConfiguration() {

		String port = null;

		String host = null;
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				PROPERTY_FILE);
		properties = new Properties();
		try {
			properties.load(in);
			in.close();
			host = properties.getProperty(HOST_PROPERTY);
			port = properties.getProperty(PORT_PROPERTY);
			participantRepository = properties.getProperty(PARTICIPANT_REPO);
			// if (participantRepository == null) {
			// participantRepository = ".";
			// }

			if (properties.getProperty("tcpmon_enable").equals("true")) {
				debug = "true";
			}

			participantAxis2Xml = properties
					.getProperty(PARTICIPANT_AXIS2_CONF);
			// if (participantAxis2Xml == null) {
			// participantAxis2Xml = "axis2.xml";
			// }

			kandulaListenerRepository = properties
					.getProperty(KANDULA_LISTENER_REPO);
			// if (kandulaListenerRepository == null) {
			// kandulaListenerRepository = ".";
			// }
			kandulaListenerAxis2Xml = properties
					.getProperty(KANDULA_LISTENER_AXIS2XML);
			// if (kandulaListenerAxis2Xml == null) {
			// kandulaListenerRepository += "/axis2.xml";
			// }

			coordinatorAxis2Conf = properties.getProperty(COORDINATOR_AXIS2XML);
			// if (coordinatorAxis2Conf == null) {
			// coordinatorAxis2Conf = "axis2.xml";
			// }

			coordinatorRepo = properties.getProperty(COORDINATOR_REPOSITORY);
			// if (coordinatorRepo == null) {
			// coordinatorAxis2Conf = ".";
			// }

			kandulaListenerPort = properties.getProperty(LISTENER_PORT);
			if (kandulaListenerPort == null) {
				kandulaListenerPort = "5050";
			}

			if (port == null) {
				port = "8080";
			}

			if (host == null) {
				host = InetAddress.getLocalHost().getHostAddress();
			}

			location = "http://" + host + ":" + port;
			System.out.println(location);
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException(e);
		}

	}

	public static KandulaConfiguration getInstance() {
		if (instance == null)
			instance = new KandulaConfiguration();
		return instance;
	}

	
	/**
	 * @return a ConfigurationContext according to the coordinator Axis2.xml & repository configured.
	 * @throws AbstractKandulaException
	 */
	public ConfigurationContext getPariticipantAxis2ConfigurationContext()
			throws AbstractKandulaException {
		try {
			if (coordinatorAxis2Conf != null && coordinatorAxis2Conf != "")
			{
				return ConfigurationContextFactory
						.createConfigurationContextFromFileSystem(
								participantRepository, participantAxis2Xml);
			}
			
		} catch (DeploymentException e) {
			throw new KandulaGeneralException(e);
		} catch (AxisFault e1) {
			throw new KandulaGeneralException(e1);
		}
		return null;
	}

	public String getParticipantRepository() {
		return participantRepository;
	}

	public String getParticipantAxis2Conf() {
		return participantAxis2Xml;
	}

	public ConfigurationContext getCoordinatorAxis2ConfigurationContext()
			throws AbstractKandulaException {
		try {
			if (coordinatorAxis2Conf != null && coordinatorAxis2Conf != "")
			{
				return ConfigurationContextFactory
						.createConfigurationContextFromFileSystem(
								coordinatorRepo, coordinatorAxis2Conf);
			}
		} catch (DeploymentException e) {
			throw new KandulaGeneralException(e);
		} catch (AxisFault e1) {
			throw new KandulaGeneralException(e1);
		}
		return null;
	}

	public String getCoordinatorRepo() {
		return coordinatorRepo;
	}

	public String getCoordinatorAxis2Conf() {
		return coordinatorAxis2Conf;
	}

	public String getKadulaListenerPort() {
		return kandulaListenerPort;
	}

	public String getKadulaListenerPortForEPR() {
		if (debug.equals("true"))
			return (Integer.parseInt(kandulaListenerPort) + 1) + "";
		else
			return kandulaListenerPort;
	}

	/**
	 * @return Returns the kandulaListenerRepository.
	 */
	public String getKandulaListenerRepository() {
		return kandulaListenerRepository;
	}

	public String getKandulaListenerAxis2Xml() {
		return kandulaListenerAxis2Xml;
	}

	public String getLocationForEPR() {
		return location;
	}
	// public static EndpointReferenceFactory getInstance() {
	// if (instance == null)
	// instance = new EndpointReferenceFactory();
	// return instance;
	// }
}
