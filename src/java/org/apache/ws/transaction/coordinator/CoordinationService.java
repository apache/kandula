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
package org.apache.ws.transaction.coordinator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transaction;

import org.apache.ws.transaction.coordinator.at.AtCoordinator;
import org.apache.ws.transaction.coordinator.at.AtCoordinatorImpl;
import org.apache.ws.transaction.coordinator.at.AtSubCoordinator;
import org.apache.ws.transaction.coordinator.at.XidImpl;
import org.apache.ws.transaction.wscoor.CoordinationContext;


public class CoordinationService {
	Map coordinators= Collections.synchronizedMap(new HashMap());

	static CoordinationService instance= new CoordinationService();

	public CoordinationService() {}

	public static CoordinationService getInstance() {
		return instance;
	}

	public Coordinator coordinate(String coordinationType) throws UnknownCoordinationTypeException {
		if (!AtCoordinator.COORDINATION_TYPE.equals(coordinationType))
			throw new UnknownCoordinationTypeException(coordinationType);
		Coordinator coordinator= new AtCoordinatorImpl(new XidImpl());
		coordinators.put(coordinator.getActivityId(), coordinator);
		return coordinator;
	}

	public Coordinator interpose(CoordinationContext ctx) {
		throw new UnsupportedOperationException();
	}

	public Coordinator interpose(Transaction localTx) {
		Coordinator coordinator= new AtSubCoordinator(new XidImpl(), localTx);
		coordinators.put(coordinator.getActivityId(), coordinator);
		return coordinator;
	}

	public Coordinator getCoordinator(Identifier activityId) throws UnknownActivityException {
		Coordinator coordinator= (Coordinator)coordinators.get(activityId);
		if (coordinator == null)
			throw new UnknownActivityException(activityId);
		return coordinator;
	}

	public void forget(Identifier activityId) throws UnknownActivityException {
		Coordinator coordinator= (Coordinator)coordinators.remove(activityId);
		if (coordinator == null)
			throw new UnknownActivityException(activityId);
	}
}
