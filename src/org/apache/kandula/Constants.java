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
package org.apache.kandula;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
abstract public interface Constants {

    //WS-Coordination URI's

    public static final String WS_COOR = "http://schemas.xmlsoap.org/ws/2004/10/wscoor";

    public static final String WS_COOR_CREATE_COORDINATIONCONTEXT = "http://schemas.xmlsoap.org/ws/2004/10/wscoor/CreateCoordinationContext";

    public static final String WS_COOR_CREATE_COORDINATIONCONTEXT_RESPONSE = "http://schemas.xmlsoap.org/ws/2004/10/wscoor/CreateCoordinationContextResponse";

    public static final String WS_COOR_REGISTER = "http://schemas.xmlsoap.org/ws/2004/10/wscoor/Register";

    public static final String WS_COOR_REGISTER_RESPONSE = "http://schemas.xmlsoap.org/ws/2004/10/wscoor/RegisterResponse";

    //WS-AT URI's

    public static final String WS_AT = "http://schemas.xmlsoap.org/ws/2004/10/wsat";

    public static final String WS_AT_COMPLETION = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Completion";

    public static final String WS_AT_COMMIT = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Commit";

    public static final String WS_AT_COMMITTED = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Committed";

    public static final String WS_AT_ROLLBACK = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Rollback";

    public static final String WS_AT_ABORTED = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Aborted";

    public static final String WS_AT_DURABLE2PC = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Durable2PC";

    public static final String WS_AT_VOLATILE2PC = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Volatile2PC";

    public static final String WS_AT_PREPARE = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Prepare";

    public static final String WS_AT_PREPARED = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Preapared";

    public static final String WS_AT_REPLAY = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Replay";

    public static final String WS_AT_READONLY = "http://schemas.xmlsoap.org/ws/2004/10/wsat/ReadOnly";

    public static final String SUB_VOLATILE_REGISTERED = "registered for volatile 2PC";

    public static final String SUB_DURABLE_REGISTERED = "registered for durable 2PC";

    //WS-BA URI's

    public static final String WS_BA = "http://schemas.xmlsoap.org/ws/2004/10/wsba";

}