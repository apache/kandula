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
package org.apache.kandula.coordinator.ba.participant;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.coordinator.ba.ProtocolType;

/**
 * Implementation of a Participant registered for BAwPC
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 * 
 */
public abstract class BAwPCParticipant extends AbstractParticipant {

	/**
	 * Create a new BAwPCParticipant. The participant automatically registers
	 * for the "BusinessActivity with Participant Completion" protocol.
	 * 
	 * @param coordinationContext The coordination context to register with.
	 * 
	 * @throws RemoteException If there were problems with the registration, any exception
	 * from the registration service is propageted.
	 */
	public BAwPCParticipant(final CoordinationContext coordinationContext) 
	throws RemoteException
	{
		super(coordinationContext);
	}
	
	/**
	 * Create a new BAwPCParticipant. The participant automatically registers
	 * for the "BusinessActivity with Participant Completion" protocol.
	 * 
	 * @param coordinationContext The coordination context to register with.
	 * 
	 * @throws RemoteException If there were problems with the registration, any exception
	 * from the registration service is propageted.
	 */
	public BAwPCParticipant(final org.apache.kandula.wscoor.CoordinationContext coordinationContext) 
	throws RemoteException{
		super(new CoordinationContext(coordinationContext));
	}


	/**
	 * Fetches a stub to access the coordinator's protocol service.
	 * @return ..
	 * 
	 */
	protected final BACoordinatorStub getCoordinatorServiceStub(){
		try {
			return new BAwPCCoordinatorStub(this.remoteProtocolService);
		} catch (AxisFault e) {
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
		
	/**
	 * This participant's protocol type.
	 * @return @see ProtocolType#PROTOCOL_ID_PC 
	 */
	public final QName getProtocolType(){
		return ProtocolType.PROTOCOL_ID_PC;
	}
	
	/**
	 * This is the event handler dispatcher. It is called on any state transition and forwards events
	 * to the following event handlers:
	 * <ul>
	 *  <li>onClose (if the incoming message was Close)
	 *  <li>onCompensate (if the incoming message was Compensate)
	 *  <li>onCancel (if the incoming message was Cancel)
	 *  <li>onFinished (if the target state was Exited)
	 * 	<li>onStateChanged (all events)
	 * </ul>
	 * @param previousState The previous state.
	 * @param currentState The current state.
	 * @param coordinatorInitiated If the state transition was initiated by the coordinator.
	 */
	final protected void onStateChangedInternal(
			final QName previousState, 
			final QName currentState, 
			final boolean coordinatorInitiated
	) {
		super.onStateChangedInternal(previousState, currentState, coordinatorInitiated);
	}
	
}
