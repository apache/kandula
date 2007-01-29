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

public abstract interface Status {
	interface CoordinatorStatus {

		static final int STATUS_ABORTING = 9;

		static final int STATUS_ACTIVE = 0;

		static final int STATUS_COMMITTING = 8;

		static final int STATUS_NONE = 6;

		static final int STATUS_PREPARED = 14;

		static final int STATUS_PREPARED_SUCCESS = 13;

		static final int STATUS_PREPARING = 10;

		static final int STATUS_PREPARING_DURABLE = 11;

		static final int STATUS_PREPARING_VOLATILE = 12;

		static final int STATUS_READ_ONLY = 23;

	}

	// TODO remove the inner interfaces..
	interface ParticipantStatus {

		static final int STATUS_ABORTED = 22;

		static final int STATUS_COMMITED = 24;

		static final int STATUS_DURABLE_PREPARING = 21;

		static final int STATUS_READ_ONLY = 23;

		static final int STATUS_VOLATILE_PREPARING = 20;

	}
	interface BACoordinatorStatus {

		static final int STATUS_CLOSING = 30;

		static final int STATUS_COMPENSATING = 31;
		
		static final int STATUS_ACTIVE = 32;
		
		static final int STATUS_CANCELLING = 33;
		
		static final int STATUS_COMPLETED = 34;
		
		static final int STATUS_FAULTING_COMPENSATING = 35;
		
		static final int STATUS_FAULTING_ACTIVE = 36;
		
		static final int STATUS_EXITING = 37;
		
		static final int STATUS_ENDED = 38;
			
		static final int STATUS_CANCELLING_ACTIVE = 39;
		
		static final int STATUS_FAULTING = 40;
		
		static final int STATUS_COMPLETING = 41;
		
		static final int STATUS_CANCELING_COMPLETING = 42;
		
		static final int STATUS_FAULTING_COMPLETED = 43;
	}
	
	interface BAParticipantStatus {

			static final int STATUS_CLOSING = 44;

			static final int STATUS_COMPENSATING = 45;
			
			static final int STATUS_ACTIVE = 46;
			
			static final int STATUS_CANCELLING = 47;
			
			static final int STATUS_COMPLETED = 48;
			
			static final int STATUS_FAULTING_COMPENSATING = 49;
			
			static final int STATUS_FAULTING_ACTIVE_COMPLETED = 50;
			
			static final int STATUS_EXITING = 51;
			
			static final int STATUS_ENDED = 52;
				
			static final int STATUS_CANCELLING_ACTIVE = 53;
			
			static final int STATUS_COMPLETING = 55;
			
			static final int STATUS_FAULTING = 56;
	}
}
