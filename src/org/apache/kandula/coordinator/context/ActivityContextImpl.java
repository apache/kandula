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

import java.util.HashMap;

import org.apache.kandula.Status.CoordinatorStatus;
import org.apache.kandula.coordinator.CoordinatorUtils;
import org.apache.kandula.typemapping.CoordinationContext;
import org.apache.kandula.typemapping.EndPointReference;
import org.apache.kandula.utility.EndpointReferenceFactory;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.SchemaTypeLoaderImpl;
import org.xmlsoap.schemas.ws.x2003.x09.wscoor.impl.CoordinationContextTypeImpl;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public abstract class ActivityContextImpl implements ActivityContext {

    private HashMap propertyBag;
    
    private String activityID;
    
    private int status = CoordinatorStatus.STATUS_NONE;

    private boolean locked = false;
    
    protected CoordinationContext coordinationContext;
    
    protected ActivityContextImpl(){    
    }
    
    public ActivityContextImpl(String coordinationType) {
        activityID = CoordinatorUtils.getRandomStringOf18Characters();
        coordinationContext = CoordinationContext.Factory.newInstance();
        coordinationContext.setActivityID(activityID);
        coordinationContext.setRegistrationService(EndpointReferenceFactory.getInstance().getRegistrationEndpoint());
        coordinationContext.setCoordinationType(coordinationType);
    }
 
    public CoordinationContext getCoordinationContext() {
        return coordinationContext;
    }

    protected void setCoordinationContext(CoordinationContext context)
    {
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
}
