/*
 * Copyright 2007 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *  @author Hannes Erven, Georg Hicker
 */
package org.apache.kandula.coordinator.ba.coordinator;

import java.rmi.RemoteException;

import org.apache.kandula.coordinator.ba.State;

import org.apache.kandula.wsbai.CoordinatorInitiatorProtocolPortType_Mixed;
import org.apache.kandula.wsbai.BAParticipantReferenceType;
import org.apache.kandula.wsbai.BAParticipantType;


/**
 * This class handles requests sent from the initiator to the coordinator. It receives
 * the messages, correlates them to a running business activity and either fetches the requested
 * information from the business activity coordinator or forwards commands to it.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */

public class CoordinatorIService_Mixed 
extends CoordinatorIService
implements CoordinatorInitiatorProtocolPortType_Mixed
{
	
	/*
	 * 
	 * Methods for managing coordination contexts with 
	 * 
	 *   MIXED OUTCOME
	 * 
	 * 
	 */
	
	/**
	 * Tells the coordinator to cancel the selected participants. This is only
	 * valid in transaction with the "mixed outcome" assertion.
	 * 
	 * This method may be called as many times as the initiator wishes. If a 
	 * selected participant is in a state where it is not allowed to cancel,
	 * it is silently ignored.
	 * Hence, the initiator must check the return for the participants' current
	 * states.
	 * 
	 * @param participantsToCancel References to the participants that shall be cancelled. 
	 * 
	 * @return A list of the current participants and their states.
	 * @throws RemoteException 
	 */
	public BAParticipantType[] cancelParticipants(
			final BAParticipantReferenceType[] participantsToCancel
	) throws RemoteException {
		return this.notifyParticipants(
				BACoordinator.COORDINATION_TYPE__MIXED,
				participantsToCancel,
				State.MESSAGE_CANCEL,
				true,
				true
		);
	}
	
	/**
	 * Tells the coordinator to close the selected participants. This is only
	 * valid in transaction with the "mixed outcome" assertion.
	 * 
	 * This method may be called as many times as the initiator wishes. If a 
	 * selected participant is in a state where it is not allowed to close,
	 * it is silently ignored.
	 * Hence, the initiator must check the return for the participants' current
	 * states.
	 * @param participantsToClose References to the participants that shall be closed. 
	 * 
	 * @return A list of the current participants and their states.
	 * @throws RemoteException 
	 */
	public BAParticipantType[] closeParticipants(
			final BAParticipantReferenceType[] participantsToClose
	) throws RemoteException {
		return this.notifyParticipants(
				BACoordinator.COORDINATION_TYPE__MIXED,
				participantsToClose, State.MESSAGE_CLOSE, true, true
		);
	}
	
	/**
	 * Tells the coordinator to compensate the selected participants. This is only
	 * valid in transaction with the "mixed outcome" assertion.
	 * 
	 * This method may be called as many times as the initiator wishes. If a 
	 * selected participant is in a state where it is not allowed to compensate,
	 * it is silently ignored.
	 * Hence, the initiator must check the return for the participants' current
	 * states.
	 * @param participantsToCompensate References to the participants that shall be compensated.
	 * 
	 * @return A list of the current participants and their states.
	 * @throws RemoteException 
	 */
	
	public BAParticipantType[] compensateParticipants(
			final BAParticipantReferenceType[] participantsToCompensate
	) throws RemoteException {
		return this.notifyParticipants(
				BACoordinator.COORDINATION_TYPE__MIXED,
				participantsToCompensate, State.MESSAGE_COMPENSATE, true, true
		);
	}
}
