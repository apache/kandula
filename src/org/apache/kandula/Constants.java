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

public class Constants {

	// WS-Coordination URI's

	public static final String WS_COOR = "http://docs.oasis-open.org/ws-tx/wscoor/2006/06";

	public static final String WS_COOR_CREATE_COORDINATIONCONTEXT = WS_COOR+ "/CreateCoordinationContext";

	public static final String WS_COOR_CREATE_COORDINATIONCONTEXT_RESPONSE = WS_COOR+ "/CreateCoordinationContextResponse";

	public static final String WS_COOR_REGISTER = WS_COOR+ "/Register";

	public static final String WS_COOR_REGISTER_RESPONSE = WS_COOR+ "/RegisterResponse";

	// WS-AT URI's

	public static final String WS_AT = "http://docs.oasis-open.org/ws-tx/wsat/2006/03";

	public static final String WS_AT_COMPLETION = WS_AT+ "/Completion";

	public static final String WS_AT_COMMIT = WS_AT+ "/Commit";

	public static final String WS_AT_COMMITTED = WS_AT+ "/Committed";

	public static final String WS_AT_ROLLBACK = WS_AT+ "/Rollback";

	public static final String WS_AT_ABORTED = WS_AT+ "/Aborted";

	public static final String WS_AT_DURABLE2PC = WS_AT+ "/Durable2PC";

	public static final String WS_AT_VOLATILE2PC = WS_AT+ "/Volatile2PC";

	public static final String WS_AT_PREPARE = WS_AT+ "/Prepare";

	public static final String WS_AT_PREPARED = WS_AT+ "/Prepared";

	public static final String WS_AT_REPLAY = WS_AT+ "/Replay";

	public static final String WS_AT_READONLY = WS_AT+ "/ReadOnly";

	public static final String SUB_VOLATILE_REGISTERED = "registered for volatile 2PC";

	public static final String SUB_DURABLE_REGISTERED = "registered for durable 2PC";

	// WS-BA URI's

	public static final String WS_BA = "http://docs.oasis-open.org/ws-tx/wsba/2006/03";
	
	public static final String WS_BA_ATOMIC = WS_BA+ "/AtomicOutcome";
	
	public static final String WS_BA_MIXED = WS_BA+"/MixedOutcome";
	
	//bims
	
	public static final String WS_BA_PC = WS_BA+ "/ParticipantCompletion";

	public static final String WS_BA_CC = WS_BA+ "/CoordinatorCompletion";

	// Kandula Specific
	// Constants-------------------------------------------------------------
	
	public static final  String KANDULA_URI = "http://ws.apache.org/kandula";

	public static final  String KANDULA_RESOURCE = "KandulaResource";

	public static final  String KANDULA_PRE = "kand";

	public static final  String KANDULA_STORE = "KandulaStore";

	// For the coordinator to identify seperate distributed
	// activities(transactions)
	// Common to all the parties participating in a single distributed tx.
	public static final QName TRANSACTION_ID_PARAMETER = new QName(KANDULA_URI,
			"TransactionID", KANDULA_PRE);

	// Used by the Initiator Transaction Manager & participant TM to track the
	// seperate transactions
	public static final QName REQUESTER_ID_PARAMETER = new QName(KANDULA_URI,
			"RequesterID", KANDULA_PRE);


	public static final QName PARTICIPANT_ID_PARAMETER = new QName(KANDULA_URI,
			Configuration.PARTICIPANT_IDENTIFIER, KANDULA_PRE);
	
	// For the coordinator to identify each and every registered participant
	// whithing a transaction
	// This + Tx_ID will be unique for a participant
	public static final QName ENLISTMENT_ID_PARAMETER = new QName(KANDULA_URI,
			"EnlistmentID", KANDULA_PRE);
	//bims
	//For the coordinator to identify seperate business
	// activities
	// Common to all the parties participating in a single distributed tx.
	public static final QName BA_ID_PARAMETER = new QName(KANDULA_URI,
			"BusinessActivityID", KANDULA_PRE);
	
	public static interface Configuration {
		
		public static final String TRANSACTION_CONTEXT = "TransactionContext";

		public static final  String PARTICIPANT_IDENTIFIER = "ParticipantID";
	}
}
