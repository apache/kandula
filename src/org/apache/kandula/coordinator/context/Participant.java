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

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 * 
 * Used to store the details about the participant & to store the runtime status
 * of Participants
 */
public class Participant {
    private EndpointReference epr;
    private String protocol;
    private int status;
       
    /**
     * @param epr
     * @param protocol
     * @param activityId
     */
    public Participant(EndpointReference epr, String protocol) {
        super();
        this.epr = epr;
        this.protocol = protocol;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    
}