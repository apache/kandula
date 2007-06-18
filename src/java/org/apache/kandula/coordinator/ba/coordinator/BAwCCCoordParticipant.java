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

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.kandula.coordinator.ba.ProtocolType;
import org.apache.kandula.coordinator.ba.State;


/**
 * A server-side WS-BA transaction particpant, registered for the
 * CoordinatorCompletion Protocol.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class BAwCCCoordParticipant extends AbstractCoordParticipant {

	private final BAwCCParticipantProxy myPCProxy;
	
	/**
	 * The default constructor for the new participant.
	 * 
	 * @param participantID The participant's ID.
	 * @param participantEPR The participant's protocol service endpoint reference.
	 * @param coordinationContext The coordination context the participant is registered in.
	 * @param matchcode The matchcode the participant was given by the initiator.
	 * @throws AxisFault Any fault while initializing the participant object.
	 */
	public BAwCCCoordParticipant(
			final String participantID, 
			final EndpointReference participantEPR,
			final BACoordinator coordinationContext, 
			final String matchcode
			
	) throws AxisFault {
		super(participantID, participantEPR, coordinationContext, matchcode);
		
		try {
			this.myPCProxy = new BAwCCParticipantProxy(participantID, participantEPR);
		} catch (MalformedURLException e) {
			throw new AxisFault("ERROR!!! "+e);
		}
	}

	/**
	 * As this is a CC participant, always returns CC.
	 * @return This particpant's protocol identifier.
	 * @see ProtocolType#PROTOCOL_ID_CC
	 */
	public QName getProtocolIdentifier() {
		return ProtocolType.PROTOCOL_ID_CC;
	}

	/*
	 * Methods for the PC participant
	 */
	
	/**
	 * Tell the participant to complete
	 * @throws java.rmi.RemoteException A fault from the participant.
	 */
	public void tellComplete() throws RemoteException {
		this.tell(State.MESSAGE_COMPLETE);
	}
	
	/**
	 * Return the participant proxy
	 * @return The participant proxy.
	 */
	public AbstractCoordParticipantProxy getParticipantProxy() {
		return this.myPCProxy;
	}
	
	/**
	 * Overrides @see AbstractCoordParticipant#tellAgain(QName) , as the BAwCC participant
	 * needs an additional message: @see State#MESSAGE_COMPLETED
	 * @param message The message type to send.
	 * @throws java.rmi.RemoteException A fault from the participant.
	 */
	protected void tellAgain(final QName message) throws RemoteException {
		if (State.MESSAGE_COMPLETE.equals(message)){
			this.myPCProxy.completeOperation(null);
		}else{
			super.tellAgain(message);
		}
	}

}
