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

import java.util.Iterator;

import org.apache.kandula.KandulaException;
import org.apache.kandula.typemapping.CoordinationContext;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public interface ActivityContext {
    
    public abstract String addParticipant(String participantEPR, String protocol) throws KandulaException;
    
    public abstract Iterator getRegisteredParticipants(String protocol);
    
    public abstract Iterator getAllParticipants();

    public abstract int getStatus();

    public abstract void setStatus(int value);

    public abstract void lock();

    public abstract void unlock();
    
    public abstract void setProperty(Object key, Object value);
    
    public abstract Object getProperty(Object key);
    
    public abstract CoordinationContext getCoordinationContext();
    
}