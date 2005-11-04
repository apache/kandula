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
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.transport.http.SimpleHTTPServer;
import org.apache.axis2.util.Utils;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class KandulaListener {

    private static KandulaListener instance = null;

    private ConfigurationContext responseConfigurationContext;

    protected static AxisOperation[] operations;

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

    public void stop() {
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
    public void addService(AxisService service) throws AxisFault {
        AxisOperation responseOperationDesc;

        service.setClassLoader(Thread.currentThread().getContextClassLoader());
        HashMap allServices = responseConfigurationContext
                .getAxisConfiguration().getServices();

        if (allServices.get(service.getName().getLocalPart()) == null) {

            responseConfigurationContext.getAxisConfiguration().addService(
                    service);
            Utils.resolvePhases(receiver.getSystemContext()
                    .getAxisConfiguration(), service);
        }

    }

    public String getHost() throws UnknownHostException {
        return "http://" + InetAddress.getLocalHost().getHostAddress() + ":"
                + (SERVER_PORT + 1) + "/axis2/services/";
    }
}