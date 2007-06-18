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

import java.rmi.RemoteException;
import javax.xml.namespace.QName;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.encoding.AnyContentType;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.kandula.coordinator.Callback;
import org.apache.kandula.coordinator.CallbackRegistry;
import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.coordinator.CoordinationService;
import org.apache.kandula.coordinator.ba.AbstractStateTransition;
import org.apache.kandula.coordinator.ba.State;
import org.apache.kandula.coordinator.ba.StateTransitionFault;
import org.apache.kandula.coordinator.ba.StateTransitionResend;
import org.apache.kandula.coordinator.ba.StateTransitionResendPreviousState;
import org.apache.kandula.coordinator.ba.StateTransitionState;
import org.apache.kandula.coordinator.ba.WrongMethodCallException;
import org.apache.kandula.wsba.StateType;
import org.apache.kandula.wsba.StatusType;

/**
 * Base class for all Participant implementations.
 * 
 * Here are all common participant features implemented. Subclasses provide implementations
 * of the various port types, but do not directly receive SOAP messages.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 * 
 */
public abstract class AbstractParticipant implements Callback {

	/**
	 * My state.
	 */
	protected final ParticipantState state;

	/**
	 * My Coordination Context.
	 */
	protected final CoordinationContext ctx;

	/**
	 * My local endpoint reference (for use by the coordinator)
	 */
	protected final EndpointReference localProtocolService;
	
	/**
	 * My coordinator's protocol endpoint reference (to contact the coordinator)
	 */
	protected final EndpointReference remoteProtocolService;

	/**
	 * My participant ID.
	 */
	protected final String myID; 

	/**
	 * Constructor to set the protocolType and the coordination context to participate in. It also
	 * registers the participant with the given coordination context.
	 * 
	 * @param protocolType The requested protocol type.
	 * @param coordinationContext The coordination context.
	 * @throws RemoteException Any exception that occurs while registering.
	 */
	protected AbstractParticipant(final CoordinationContext coordinationContext)
	throws RemoteException
	{
		this.ctx = coordinationContext;
		this.state = new ParticipantState(getProtocolType());

		// Register!
		final UUIDGen gen = UUIDGenFactory.getUUIDGen();
		this.myID = "uuid:" + gen.nextUUID();

		// Local initiator services endpoint
		this.localProtocolService = CoordinationService.getInstance().getParticipantService(this, 0l);

		// remote coordination services endpoint
		final EndpointReference coordinatorPort = this.ctx.register(
				getProtocolType().getNamespaceURI()+getProtocolType().getLocalPart(),
				this.localProtocolService
		);

		// Check if we got something back...
		if (coordinatorPort == null)
			throw new NullPointerException();

		this.remoteProtocolService = coordinatorPort;

		// Put ourselves into the callback registry
		CallbackRegistry.getInstance().registerCallback(this);
	}

	/**
	 * Getter for the current state
	 * 
	 * @return The current state.
	 */
	public final QName getState() {
		return this.state.getCurrentState();
	}

	/**
	 * Getter for the protocol type
	 * 
	 * @return the protocol type the participant registered for.
	 */
	public abstract QName getProtocolType();

	/**
	 * Return my participant ID.
	 * @return ..
	 */
	public final String getID() {
		return this.myID;
	}

	/**
	 * Gets this participant's local protocol service endpoint.
	 * @return ..
	 */
	public final EndpointReference getEndpointReference() {
		return this.localProtocolService;
	}

	/**
	 * Tell the coordinator that we have completed. Also sets this participant's state
	 * to @see State#STATE_COMPLETED .
	 */
	public final void tellCompleted(){
		this.tell(State.MESSAGE_COMPLETED);
	}

	/**
	 * Get a stub for the coordinator's protocol service.
	 *  
	 * @return ..
	 */
	protected abstract BACoordinatorStub getCoordinatorServiceStub();

	/**
	 * Requests the state the coordinator has on its records for this participant.
	 */
	public final void requestStatusFromCoordinator(){
		try{
			this.getCoordinatorServiceStub().getStatusOperation(null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Handle an incoming message:
	 * <li>If type is @see State#MESSAGE_GETSTATUS , invokes status on the coordinator.
	 * <li>else, tries to perform a state transition according to the message type given.
	 * 
	 * This method is called from the @see ParticipantService
	 * 
	 * @param type The type of the received message. (@see State)
	 * @param param Any parameters of the message (currently unused)
	 * @throws RemoteException A protocol exception occured.
	 * @throws WrongMethodCallException  An internal error occured. (@see WrongMethodCallException)
	 */
	final protected void handleIncomingMessage(
			final QName type, 
			final AnyContentType param
	) throws RemoteException{

		{
			// Suppress compiler rants about param not being read
			if (false)
				param.get_any();
		}

		if (State.MESSAGE_GETSTATUS.equals(type)){
			tellStatus();
			return ;
		}

		if (State.MESSAGE_STATUS.equals(type)){
			incomingStatusResponse(type);
			return ;
		}

		// No request for state, we should do some work

		final AbstractStateTransition ast;
		try{
			ast = this.state.transistStateByMessage(type);

			// transistStateByMessage could return null (StateTransitionIgnore)
			if (ast != null) {
				if (ast instanceof StateTransitionResend){
					final StateTransitionResend str = (StateTransitionResend) ast;
					tellAgain(str.getMessageToResend());
				}else if (ast instanceof StateTransitionFault){
					final StateTransitionResend stf = (StateTransitionResend) ast;
					throw stf.getAxisFault();
				}else if (ast instanceof StateTransitionResendPreviousState){
					final QName messageToResend = this.state.getMessageForTransition(this.state.getLastState(), this.state.getSecondLastState());
					tellAgain(messageToResend);
				}else if (ast instanceof StateTransitionState){
					// The participant state changed, invoke event handler
					this.onStateChangedInternal(this.state.getLastState(), this.state.getCurrentState(), true);
				}else{
					/*
					 *  there are other state transition types, but we only got work
					 *  to do with those four
					 */
				}
			}

		}catch(WrongMethodCallException e){
			// This indicates an internal programming error
			e.printStackTrace();
			throw State.GET_INVALID_STATE_SOAP_FAULT() ;
		}

	}

	/**
	 * This method is called when the coordinator sends a STATUS message. By default we don't
	 * need that information, so it is just printed to stdout... Override this method
	 * if you need a custom implementation for it.
	 * @param coordinatorState The state the coordinator reported.
	 * @param type The state the coordinator is in.
	 */
	protected final void incomingStatusResponse(final QName coordinatorState) {
		System.out.println("*** Got a STATUS reply: coordinator's state is "+coordinatorState);
	}

	/**
	 * Send a message of type 'type' to the coordinator. Attention: this is of course only
	 * possible with message types that do not require parameters (e.g. you will get an
	 * exception when you try to tell status). This method also checks if the message is allowed in 
	 * the current state and resets the state if possible.
	 * 
	 * Normally, one should use the tellXXX()-methods directly, as they also set this participants' state
	 * accordingly. 
	 * 
	 * @param type The message type to send.
	 * @throws RemoteException Any exception the coordinator returns
	 */
	protected final void tell(final QName type){
		if (this.state.handleOutgoingMessage(type)) {
			this.tellAgain(type);
			this.onStateChangedInternal(this.state.getLastState(), this.state.getCurrentState(), false);
		} else {
			throw new RuntimeException(
					"Sorry, you cannot tell the coordinator " + type
					+ " in state " + this.getState().toString(),
					State.GET_INVALID_STATE_SOAP_FAULT());
		}
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
	 * If the event handler returns some other field from the interface than HANDLED_BY_APPLICATION, the participant
	 * will automatically report the new state to the coordinator.
	 * 
	 * @param previousState The previous state.
	 * @param currentState The current state.
	 * @param coordinatorInitiated If the state transition was initiated by the coordinator.
	 * 
	 * This method is overridden in BAwPC / BAwCC partipant and declared final
	 */
	protected void onStateChangedInternal(
			final QName previousState,
			final QName currentState, 
			final boolean coordinatorInitiated
	) {
		if (coordinatorInitiated){
			if (State.STATE_CLOSING.equals(currentState)) {
				final ParticipantCloseResult r = onClose();
				
				// Implicit null check ;-)
				if (r.equals(ParticipantCloseResult.CLOSED)){
					this.tellClosed();
				}
			}

			if (State.STATE_COMPENSATING.equals(currentState)){
				final ParticipantCompensateResult r = onCompensate();

				// Implicit null check ;-)
				if (r.equals(ParticipantCompensateResult.COMPENSATED)){
					this.tellCompensated();
				}
				if (r.equals(ParticipantCompensateResult.FAULTED)){
					this.tellFault();
				}
			}

			if (false 
					|| State.STATE_CANCELLING.equals(currentState)
					|| State.STATE_CANCELLING_ACTIVE.equals(currentState)
					|| State.STATE_CANCELLING_COMPLETING.equals(currentState)
			){
				final ParticipantCancelResult r = onCancel();
				
				// Implicit null check
				if (r.equals(ParticipantCancelResult.CANCELED)){
					this.tellCanceled();
				}
			}

		}
		
		onStateChanged(previousState, currentState, coordinatorInitiated);

		// We reached the end of our lifespan. Remove from the current participant list, and have the GC collect us.
		if (State.STATE_ENDED.equals(currentState)){
			CallbackRegistry.getInstance().remove(this);
			onFinish();
		}
	}
	
	/**
	 * This event handler is called when the transaction has completed in either way
	 * and the participant is no more needed.
	 */
	protected abstract void onFinish();

	/**
	 * This event handler is called when the participant received the Cancel
	 * command from the coordinator.
	 * @return The result to report to the coordinator.
	 */
	protected abstract ParticipantCancelResult onCancel();

	/**
	 * This event handler is called when the participant received the Compensate
	 * command from the coordinator.
	 * @return The result to report to the coordinator.
	 */
	protected abstract ParticipantCompensateResult onCompensate();

	/**
	 * This event handler is called when the participant received the Close
	 * command from the coordinator.
	 * @return The result to report to the coordinator.
	 */
	protected abstract ParticipantCloseResult onClose();

	/**
	 * Resend a message of type 'type' to the coordinator. Attention: this is of course only
	 * possible with message types that do not require parameters (e.g. you will get an
	 * exception when you try to tellAgain status).
	 * 
	 * Normally, one should use the tellXXX()-methods directly, as they also set this participants' state
	 * accordingly. However, there are situations where we need to resend a message, and this is
	 * what this method is handy for.  
	 * 
	 * @param type The message type to send.
	 * @throws RemoteException Any exception the coordinator returns
	 */
	private final void tellAgain(final QName type) {
		final BACoordinatorStub stub = getCoordinatorServiceStub();

		try{
			if (State.MESSAGE_CANCELED.equals(type)){
				stub.canceledOperation(null);
	
			}else if (State.MESSAGE_CLOSED.equals(type)){
				stub.closedOperation(null);
	
			}else if (State.MESSAGE_COMPENSATED.equals(type)){
				stub.compensatedOperation(null);
	
			}else if (State.MESSAGE_COMPLETED.equals(type)){
				stub.completedOperation(null);
	
			}else if (State.MESSAGE_EXIT.equals(type)){
				stub.exitOperation(null);
	
			}else if (State.MESSAGE_FAULT.equals(type)){
				stub.faultOperation(null);
	
			}else{
				throw new IllegalArgumentException("Sorry, you cannot tell the coordinator "+type);
			}
		}catch(final RemoteException e){
			// Throw away the error and continue as normal...
			// TODO WSBA Report the exception to the application?
		}
	}

	/**
	 * Tells the coordinator the current state of this participant. (Invokes statusOperation on
	 * @see #getCoordinatorServiceStub() )
	 * @throws RemoteException Any exception from the coordinator.
	 */
	public final void tellStatus() throws RemoteException {
		final StatusType st = new StatusType();
		st.setState(StateType.fromValue(this.state.getCurrentState()));

		getCoordinatorServiceStub().statusOperation(st);
	}

	/**
	 * Tell the coordinator that we exit. Also sets this participant's state
	 * to @see State#STATE_EXITING .
	 */
	public final void tellExit(){
		this.tell(State.MESSAGE_EXIT);
	}

	/**
	 * Tell the coordinator that we fault. Also sets this participant's state
	 * to @see State#STATE_FAULT .
	 */
	public final void tellFault(){
		this.tell(State.MESSAGE_FAULT);
	}

	/**
	 * Tell the coordinator that we successfully cancelled and sets
	 * the current state to @see State#STATE_ENDED .
	 */
	public final void tellCanceled(){
		this.tell(State.MESSAGE_CANCELED);
	}

	/**
	 * Tell the coordinator that we successfully closed and sets
	 * the current state to @see State#STATE_ENDED .
	 */
	public final void tellClosed(){
		this.tell(State.MESSAGE_CLOSED);
	}

	/**
	 * Tell the coordinator that we successfully compensated and sets
	 * the current state to @see State#STATE_ENDED .
	 */
	public final void tellCompensated(){
		this.tell(State.MESSAGE_COMPENSATED);
	}

	/**
	 * This event handler is called when the participant's state changed. Child classes
	 * shall override it to catch the events, or use the onClose/onCompensate/onCancel handler methods.
	 * 
	 * @param previousState The previous state.
	 * @param currentState The current state.
	 * @param coordinatorInitiated 
	 * 		true, if the state change was 
	 * 		false, if the state change was due to other reasons (e.g. outgoing message)
	 */
	protected void onStateChanged(
			final QName previousState, 
			final QName currentState, 
			final boolean coordinatorInitiated
	){
		// "Fix" compiler warnings about unused parameters
		if (previousState == null || currentState == null || coordinatorInitiated){
			// do nothing
		}

		// Override this method if you want to use it
	}
}

