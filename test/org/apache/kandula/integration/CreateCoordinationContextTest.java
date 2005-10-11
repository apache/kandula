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

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.SimpleHTTPServer;
import org.apache.kandula.Constants;
import org.apache.kandula.initiator.TransactionManager;

public class CreateCoordinationContextTest extends TestCase {

    private String repository = "test-resources/testing-repository";

    private SimpleHTTPServer server;

    private boolean finish = false;

    public CreateCoordinationContextTest() {
        super(CreateCoordinationContextTest.class.getName());
    }

    public CreateCoordinationContextTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        ConfigurationContextFactory erfac = new ConfigurationContextFactory();
        File file = new File(repository);
        if (!file.exists()) {
            throw new Exception("repository directory "
                    + file.getAbsolutePath() + " does not exists");
        }
        ConfigurationContext er = erfac.buildConfigurationContext(file
                .getAbsolutePath());

        server = new SimpleHTTPServer(er, 8081);

        try {
            server.start();
            System.out.print("Server started on port " + 8081 + ".....");
        } finally {

        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            throw new AxisFault("Thread interuptted", e1);
        }

    }

    protected void tearDown() throws Exception {
        server.stop();
    }

    public void testEchoXMLSync() throws Exception {
        TransactionManager tm = new TransactionManager(
                Constants.WS_AT,
                new EndpointReference(
                        "http://localhost:8082/axis/services/ActivationCoordinator"));

        tm.begin();
    }
}