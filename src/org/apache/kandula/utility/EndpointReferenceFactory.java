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

import org.apache.axis2.addressing.AnyContentType;
import org.apache.axis2.addressing.EndpointReference;

import javax.xml.namespace.QName;
import java.util.Properties;

/**
 * @author Dasarath Weeratunge
 * @author <a href="mailto:thilina@apache.org"> Thilina Gunarathne </a>
 */

public class EndpointReferenceFactory {
    static final String PROPERTY_FILE = "endpoints.conf";

    static final String PROTOCOL_PROPERTY = "protocol";

    static final String HOST_PROPERTY = "host";

    static final String PORT_PROPERTY = "port";

    private static EndpointReferenceFactory instance = null;

    Properties properties = null;

    String location = null;

    private EndpointReferenceFactory() {
//        InputStream in = getClass().getClassLoader().getResourceAsStream(
//                PROPERTY_FILE);
//        properties = new Properties();
//        try {
//            properties.load(in);
//            in.close();
//            String host = properties.getProperty(HOST_PROPERTY);
//            if (host == null)
//                host = InetAddress.getLocalHost().getHostAddress();
//            location = properties.getProperty(PROTOCOL_PROPERTY) + "://" + host
//                    + ":" + properties.getProperty(PORT_PROPERTY);
//        } catch (Exception e) {
//            if (e instanceof RuntimeException)
//                throw (RuntimeException) e;
//            else
//                throw new RuntimeException(e);
//        }
    }

    public static EndpointReferenceFactory getInstance() {
        if (instance == null)
            instance = new EndpointReferenceFactory();
        return instance;
    }

    public EndpointReference getRegistrationEndpoint(String id) {
        //TODO set this somehow reading the conf file
        EndpointReference epr = new EndpointReference(
                "http://localhost:8082/axis/services/RegistrationCoordinator");
        AnyContentType refProperties = new AnyContentType();
        refProperties.addReferenceValue(
                new QName("http://ws.apache.org/Kandula", "id"), id);
        epr.setReferenceProperties(refProperties);
        return epr;
    }

    public EndpointReference getCompletionParticipantEndpoint(String id) {
        //TODO set this somehow reading the conf file
        EndpointReference epr = new EndpointReference(
                "http://localhost:5059/axis/services/CompletionParticipant");
        AnyContentType refProperties = new AnyContentType();
        refProperties.addReferenceValue(
                new QName("http://ws.apache.org/Kandula", "id"), id);
        epr.setReferenceProperties(refProperties);
        return epr;
    }

    public EndpointReference getCompletionEndpoint(String id) {
        //TODO set this somehow reading the conf file
        EndpointReference epr = new EndpointReference(
                "http://localhost:8082/axis/services/RegistrationCoordinator");
        AnyContentType refProperties = new AnyContentType();
        refProperties.addReferenceValue(
                new QName("http://ws.apache.org/Kandula", "id"), id);
        epr.setReferenceProperties(refProperties);
        return epr;
    }

    public EndpointReference get2PCEndpoint(String id) {
        //TODO set this somehow reading the conf file
        EndpointReference epr = new EndpointReference(
                "http://localhost:8082/axis/services/RegistrationCoordinator");
        AnyContentType refProperties = new AnyContentType();
        refProperties.addReferenceValue(
                new QName("http://ws.apache.org/Kandula", "id"), id);
        epr.setReferenceProperties(refProperties);
        return epr;
    }
    //	public EndpointReferenceTypeImpl
    // getEndpointReference(PortReferenceTypeImpl portType,
    // ReferencePropertiesType referenceProperties) {
    //		try {
    //			EndpointReferenceTypeImpl endpointReference = new
    // EndpointReferenceTypeImpl(SchemaTypeImpl.);
    //			endpointReference.setPortTypesetPortType(portType);
    //			endpointReference.setProperties(referenceProperties);
    //			return endpointReference;
    //		}
    //		catch (Exception e) {
    //			if (e instanceof RuntimeException)
    //				throw (RuntimeException)e;
    //			else
    //				throw new RuntimeException(e);
    //		}
    //	}
}