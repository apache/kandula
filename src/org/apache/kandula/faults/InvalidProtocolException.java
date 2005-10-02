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
package org.apache.kandula.faults;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class InvalidProtocolException extends AbstractKandulaException {

    /**
     *  
     */
    public InvalidProtocolException() {
        super();
    }

    /**
     * @param arg0
     */
    public InvalidProtocolException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public InvalidProtocolException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public InvalidProtocolException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.kandula.faults.KandulaFault#getFaultCode()
     */
    public String getFaultCode() {
        return "Sender";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.kandula.faults.KandulaFault#getFaultSubcode()
     */
    public String getFaultSubcode() {
        return "wscoor:InvalidProtocol";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.kandula.faults.KandulaFault#getFaultReason()
     */
    public String getFaultReason() {
        return "The protocol is invalid or is not supported by the coordinator.";
    }
}