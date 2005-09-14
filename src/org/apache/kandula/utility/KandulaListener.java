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

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.ParameterImpl;
import org.apache.axis2.description.ServiceDescription;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.apache.axis2.receivers.RawXMLINOnlyMessageReceiver;
import org.apache.axis2.transport.http.SimpleHTTPServer;
import org.apache.axis2.util.Utils;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class KandulaListener {

    private static KandulaListener instance = null;

    private ConfigurationContext responseConfigurationContext;

    protected static org.apache.axis2.description.OperationDescription[] operations;

    private SimpleHTTPServer receiver = null;

    private boolean serverStarted = false;

    public static final int SERVER_PORT = 5059;

    private KandulaListener() throws IOException {
        responseConfigurationContext = new org.apache.axis2.context.ConfigurationContextFactory()
                .buildClientConfigurationContext(".");
        receiver = new SimpleHTTPServer(responseConfigurationContext,
                SERVER_PORT);

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
            System.out.print("Server started on port " + SERVER_PORT + ".....");
        }

    }
    
    public void stop()
    {
        receiver.stop();
        serverStarted = false;
    }

    /**
     * @param serviceName
     * @param operationName
     * @throws AxisFault
     *             To add services with only one operation, which is the
     *             frequent case in reponses
     */
    public void addService(QName serviceName, QName operationName,
            String className) throws AxisFault {
        ServiceDescription service = new ServiceDescription(serviceName);
        service.addParameter(new ParameterImpl(
                AbstractMessageReceiver.SERVICE_CLASS, className));
        service.setFileName(className);
        org.apache.axis2.description.OperationDescription responseOperationDesc;
        operations = new org.apache.axis2.description.OperationDescription[1];

        responseOperationDesc = new org.apache.axis2.description.OperationDescription();
        responseOperationDesc.setName(operationName);
        responseOperationDesc
                .setMessageReceiver(new RawXMLINOnlyMessageReceiver());
        operations[0] = responseOperationDesc;
        service.addOperation(responseOperationDesc);
        service.setClassLoader(Thread.currentThread().getContextClassLoader());

        responseConfigurationContext.getAxisConfiguration().addService(service);
        responseConfigurationContext.createServiceContext(serviceName);
        Utils.resolvePhases(receiver.getSystemContext().getAxisConfiguration(),
                service);
    }

    public String getHost() throws UnknownHostException {
        return "http://"+InetAddress.getLocalHost().getHostAddress() + ":" + 5060
                + "/axis2/services/";
    }
}