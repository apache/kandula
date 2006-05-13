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

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public interface Constants {
	
	// WS-Coordination URI's
	
	static final String WS_COOR = "http://schemas.xmlsoap.org/ws/2004/10/wscoor";
	
	static final String WS_COOR_CREATE_COORDINATIONCONTEXT = "http://schemas.xmlsoap.org/ws/2004/10/wscoor/CreateCoordinationContext";
	
	static final String WS_COOR_CREATE_COORDINATIONCONTEXT_RESPONSE = "http://schemas.xmlsoap.org/ws/2004/10/wscoor/CreateCoordinationContextResponse";
	
	static final String WS_COOR_REGISTER = "http://schemas.xmlsoap.org/ws/2004/10/wscoor/Register";
	
	static final String WS_COOR_REGISTER_RESPONSE = "http://schemas.xmlsoap.org/ws/2004/10/wscoor/RegisterResponse";
	
	// WS-AT URI's
	
	static final String WS_AT = "http://schemas.xmlsoap.org/ws/2004/10/wsat";
	
	static final String WS_AT_COMPLETION = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Completion";
	
	static final String WS_AT_COMMIT = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Commit";
	
	static final String WS_AT_COMMITTED = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Committed";
	
	static final String WS_AT_ROLLBACK = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Rollback";
	
	static final String WS_AT_ABORTED = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Aborted";
	
	static final String WS_AT_DURABLE2PC = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Durable2PC";
	
	static final String WS_AT_VOLATILE2PC = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Volatile2PC";
	
	static final String WS_AT_PREPARE = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Prepare";
	
	static final String WS_AT_PREPARED = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Prepared";
	
	static final String WS_AT_REPLAY = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Replay";
	
	static final String WS_AT_READONLY = "http://schemas.xmlsoap.org/ws/2004/10/wsat/ReadOnly";
	
	static final String SUB_VOLATILE_REGISTERED = "registered for volatile 2PC";
	
	static final String SUB_DURABLE_REGISTERED = "registered for durable 2PC";
	
	// WS-BA URI's
	
	static final String WS_BA = "http://schemas.xmlsoap.org/ws/2004/10/wsba";
	
	// Kandula Specific
	// Constants----------------------------------------------------------------------------------------------------
	static String KANDULA_URI = "http://ws.apache.org/kandula";
	
	static String KANDULA_RESOURCE = "KandulaResource";
	
	static String KANDULA_PRE = "kand";
	
	static String KANDULA_STORE = "KandulaStore";
	
	// For the coordinator to identify seperate distributed
	// activities(transactions)
	// Common to all the parties participating in a single distributed tx.
	static final QName TRANSACTION_ID_PARAMETER = new QName(KANDULA_URI,
			"TransactionID", KANDULA_PRE);
	
	// Used by the Initiator Transaction Manager & participant TM to track the seperate transactions
	static final QName REQUESTER_ID_PARAMETER = new QName(KANDULA_URI,
			"RequesterID", KANDULA_PRE);
	
	// For the coordinator to identify each and every registered participant
	// whithing a transaction
	// This + Tx_ID will be unique for a participant
	static final QName ENLISTMENT_ID_PARAMETER = new QName(KANDULA_URI,
			"EnlistmentID", KANDULA_PRE);
}
