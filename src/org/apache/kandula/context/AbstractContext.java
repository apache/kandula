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
package org.apache.kandula.context;

import java.util.HashMap;

import javax.xml.namespace.QName;

import org.apache.axis2.addressing.AnyContentType;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Status.CoordinatorStatus;
import org.apache.kandula.context.coordination.CoordinationContext;
import org.apache.kandula.utility.EndpointReferenceFactory;
import org.apache.kandula.utility.KandulaUtils;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public abstract class AbstractContext {

    private HashMap propertyBag;

    protected String activityID;

    private int status = CoordinatorStatus.STATUS_NONE;

    private boolean locked = false;

    protected CoordinationContext coordinationContext = null;

    protected AbstractContext() {
        propertyBag = new HashMap();
    }

    public AbstractContext(String coordinationType) {
        propertyBag = new HashMap();
        activityID = KandulaUtils.getRandomStringOf18Characters();
        EndpointReference registrationEpr = EndpointReferenceFactory
                .getInstance().getRegistrationEndpoint(activityID);
        AnyContentType referenceProp = new AnyContentType();
        referenceProp.addReferenceValue(new QName(
                "http://webservice.apache.org/~thilina", "myapp", "ID"),
                activityID);
        registrationEpr.setReferenceProperties(referenceProp);
        coordinationContext = CoordinationContext.Factory.newContext(
                activityID, coordinationType, registrationEpr);
    }

    public CoordinationContext getCoordinationContext() {
        return coordinationContext;
    }

    public void setCoordinationContext(CoordinationContext context) {
        this.coordinationContext = context;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int value) {
        status = value;
    }

    public synchronized void lock() {
        if (locked) {
            while (locked) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    //	ignore
                }
                if (status == CoordinatorStatus.STATUS_NONE)
                    throw new IllegalStateException();
            }
        }

        locked = true;
    }

    public synchronized void unlock() {
        if (!locked)
            throw new IllegalStateException();
        locked = false;
        notify();
    }

    public void setProperty(Object key, Object value) {
        propertyBag.put(key, value);

    }

    public Object getProperty(Object key) {
        return propertyBag.get(key);
    }
    public abstract String getCoordinationType();
}