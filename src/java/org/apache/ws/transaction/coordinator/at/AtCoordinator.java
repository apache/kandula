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
package org.apache.ws.transaction.coordinator.at;

import javax.transaction.xa.Xid;

import org.apache.ws.transaction.coordinator.Coordinator;



public interface AtCoordinator extends Coordinator {
	final String COORDINATION_TYPE= "http://schemas.xmlsoap.org/ws/2004/10/wsat";
	
	void commit();
	void rollback();
	void readOnly(Xid branch);
	void aborted(Xid branch);
	void replay(Xid branch);
}