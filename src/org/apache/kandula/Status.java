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
}
