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

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.kandula.coordinator.ba.ProtocolType;


/**
 * A server-side WS-BA transaction particpant, registered for the
 * ParticipantCompletion Protocol.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class BAwPCCoordParticipant extends AbstractCoordParticipant {

	private final BAwPCParticipantProxy myPCProxy;
	
	/**
	 * Build a coordinator side PC participant object and proxy.
	 * 
	 * @param participantID The participant's ID.
	 * @param participantEPR The participant's EPR.
	 * @param coordinationContext The coordination context the participant is enrolled in.
	 * @param matchcode The participant's matchcode in the context.
	 * @throws AxisFault Any fault from within.
	 */
	public BAwPCCoordParticipant(
			final String participantID, 
			final EndpointReference participantEPR,
			final BACoordinator coordinationContext, 
			final String matchcode
	) throws AxisFault {
		super(participantID, participantEPR, coordinationContext, matchcode);
		
		try{
			this.myPCProxy = new BAwPCParticipantProxy(participantID, participantEPR);
		}catch(MalformedURLException x){
			throw new AxisFault("ERROR: "+x);
		}
	}
	
	/**
	 * As this is a PC participant, always returns PC.
	 * @see ProtocolType#PROTOCOL_ID_PC
	 * @return The PC protocol's QName
	 */
	public QName getProtocolIdentifier() {
		return ProtocolType.PROTOCOL_ID_PC;
	}

	/**
	 * Return the participant proxy
	 * @return The participant proxy.
	 */
	public AbstractCoordParticipantProxy getParticipantProxy() {
		return this.myPCProxy;
	}

	/**
	 * Methods for the PC participant - already implemented in @see org.apache.kandula.coordinator.ba.coordinator.AbstractCoordParticipant
	 */
	

}
