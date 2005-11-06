/*
 * Copyright 2004 The Apache Software Foundation.
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
 */
package org.apache.kandula.utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.apache.axis2.addressing.AnyContentType;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Constants;
import org.apache.kandula.wsat.completion.CompletionInitiatorServiceListener;

/**
 * @author Dasarath Weeratunge
 * @author <a href="mailto:thilina@apache.org"> Thilina Gunarathne </a>
 */

public class EndpointReferenceFactory {
	static final String PROPERTY_FILE = "endpoints.conf";

	//  static final String PROTOCOL_PROPERTY = "protocol";

	static final String HOST_PROPERTY = "host";

	static final String PORT_PROPERTY = "port";

	static final String TCPMON_ENABLE = "tcpmon_enable";
	
	static final String REPO = "PARTICIPANT_REPOSITORY";

   static final String LISTENER_PORT = "KANDULA_LISTENER_PORT";

    private static EndpointReferenceFactory instance = null;

	Properties properties = null;

	String location = null;
	
	String participantRepository =null;

    String kandulaListenerPort=null;

    private EndpointReferenceFactory() {

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
			participantRepository = properties.getProperty(REPO);

            if (participantRepository ==null)
			{
				participantRepository = ".";
			}

            kandulaListenerPort = properties.getProperty(LISTENER_PORT);

             if (kandulaListenerPort ==null)
			{
				kandulaListenerPort = "5059";
			}

            if (port == null) {
				port = "8080";
			}
			if (host == null) {
				host = InetAddress.getLocalHost().getHostAddress();
			}
			

			location = "http://" + host + ":"+port;
            System.out.println(location);
        } catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException(e);
		}

	}

	public static EndpointReferenceFactory getInstance() {
		if (instance == null)
			instance = new EndpointReferenceFactory();
		return instance;
	}

	public EndpointReference getRegistrationEndpoint(String id) {

		EndpointReference epr = new EndpointReference(location
				+ "/axis2/services/RegistrationCoordinator");
		AnyContentType refParameters = new AnyContentType();
		refParameters.addReferenceValue(Constants.TRANSACTION_ID_PARAMETER, id);
		epr.setReferenceParameters(refParameters);
		return epr;
	}

	public EndpointReference getCompletionParticipantEndpoint(String id)
			throws IOException {
		CompletionInitiatorServiceListener serviceListener = CompletionInitiatorServiceListener
				.getInstance();
		EndpointReference epr = serviceListener.getEpr();
		AnyContentType refParameters = new AnyContentType();
		refParameters.addReferenceValue(Constants.REQUESTER_ID_PARAMETER, id);
		epr.setReferenceParameters(refParameters);
		return epr;
	}

	public EndpointReference getCompletionEndpoint(String id) {

		EndpointReference epr = new EndpointReference(location
				+ "/axis2/services/CompletionCoordinator");
		AnyContentType refParameters = new AnyContentType();
		refParameters.addReferenceValue(Constants.TRANSACTION_ID_PARAMETER, id);
		epr.setReferenceParameters(refParameters);
		return epr;
	}

	public EndpointReference get2PCCoordinatorEndpoint(String activityId,
			String enlistmentId) {

		EndpointReference epr = new EndpointReference(location
				+ "/axis2/services/AtomicTransactionCoordinator");
		AnyContentType refParameters = new AnyContentType();
		refParameters.addReferenceValue(Constants.TRANSACTION_ID_PARAMETER,
				activityId);
		refParameters.addReferenceValue(Constants.ENLISTMENT_ID_PARAMETER,
				enlistmentId);
		epr.setReferenceParameters(refParameters);
		return epr;
	}

	public EndpointReference get2PCParticipantEndpoint(String id) {

		EndpointReference epr = new EndpointReference(location
				+ "/axis2/services/AtomicTransactionParticipant");
		AnyContentType refParameters = new AnyContentType();
		refParameters.addReferenceValue(Constants.REQUESTER_ID_PARAMETER, id);
		epr.setReferenceParameters(refParameters);
		return epr;
	}
	public String getPariticipantRepository()
	{
		return participantRepository;
	}

    public String getKadulaListenerPort()
    {
         return kandulaListenerPort;
    }
}