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
package org.apache.kandula.coordinator.context;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.KandulaException;
import org.apache.kandula.coordinator.context.at.ATActivityContext;
import org.apache.kandula.typemapping.CoordinationContext;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ContextFactory {
    private static ContextFactory instance = new ContextFactory();

    public static ContextFactory getInstance() {
        return instance;
    }

    public ActivityContext createActivity(String protocol)
            throws KandulaException {
        if (org.apache.kandula.Constants.WS_AT.equalsIgnoreCase(protocol)) {
            return new ATActivityContext();
        } else {
            throw new KandulaException(new IllegalArgumentException(
                    "Unsupported Protocol Type"));
        }
    }

    public ActivityContext createActivity(String protocol,
            EndpointReference activationEPR) throws KandulaException {
        if (org.apache.kandula.Constants.WS_AT.equalsIgnoreCase(protocol)) {
            return new ATActivityContext(activationEPR);
        } else {
            throw new KandulaException(new IllegalArgumentException(
                    "Unsupported Protocol Type"));
        }
    }

    public ActivityContext createActivity(CoordinationContext context)
            throws KandulaException {
        if (org.apache.kandula.Constants.WS_AT.equalsIgnoreCase(context
                .getCoordinationType())) {
            return new ATActivityContext(context);
        } else {
            throw new KandulaException(new IllegalArgumentException(
                    "Unsupported Protocol Type"));
        }
    }
}