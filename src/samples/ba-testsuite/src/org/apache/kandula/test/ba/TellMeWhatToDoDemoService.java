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
package org.apache.kandula.test.ba;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.axis.encoding.AnyContentType;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.AddressingHeaders;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.apache.axis.types.URI;
import org.apache.kandula.coordinator.CoordinationService;
import org.apache.kandula.coordinator.RegistrationStub;
import org.apache.kandula.coordinator.ba.ProtocolType;
import org.apache.kandula.coordinator.ba.State;
import org.apache.kandula.coordinator.ba.participant.BACoordinatorStub;
import org.apache.kandula.coordinator.ba.participant.BAwCCCoordinatorStub;
import org.apache.kandula.coordinator.ba.participant.BAwPCCoordinatorStub;
import org.apache.kandula.coordinator.ba.participant.ParticipantState;
import org.apache.kandula.wsba.NotificationType;
import org.apache.kandula.wsba.StateType;
import org.apache.kandula.wsba.StatusType;
import org.apache.kandula.wscoor.RegisterResponseType;
import org.apache.kandula.wscoor.RegisterType;

/**
 * This class implements a WS-BusinessActivity test participant proxy. This class shall be deployed
 * as a web service and can then be used as a "translator" between non-server applications and the
 * business activity coordinator.
 * 
 * As JUnit test cases cannot directly be deployed as web services with Axis 1.x, this class implements
 * a generic WS-BA participant that implements no business logic. A JUnit test case may use this proxy class
 * to register participants with an existing coordinator context and may subsequently manage those participants.
 * We expose all WS-BA coordinator methods through this class to the JUnit test case. 
 * 
 * This class also checks whether the coordinator successfully processed the commands, and return
 * the result to the caller. e.g. when the test case tells us to "Exit", we expect the coordinator to immediatly
 * reply with "Exited". If it does, we return "true", else we return "false" or throw up the RemoteException, if 
 * we got one. 
 * This of course requires threads on the server where this class is deployed to wait for coordinator responses! 
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class TellMeWhatToDoDemoService 
	implements TellMeWhatToDoDemoServicePortType 
{
	
	/**
	 * This is the limit (seconds) the doTest* operations wait for a response from the
	 * coordinator, if we expect just an acknownledgement.
	 * e.g. if we send "Exit", we expect the reply "Exited" within @see #COORDINATOR_RESPONSE_TIME__NODECISION
	 * seconds.
	 */
	public static final int COORDINATOR_RESPONSE_TIME__NODECISION = 10;

	/**
	 * This is the limit (seconds) the doTest* operations wait for a response from the
	 * coordinator, if the coordinator needs to wait for other participants or
	 * the initiator to decide on its reply.
	 * e.g. if we send "Completed", we wait up to @see #COORDINATOR_RESPONSE_TIME__DECISION seconds
	 * for the coordinator to reply with "Close" or "Cancel".
	 * (Otherwise the TellMeWhatToDoDemoService returns false on the doTestCompleted call, which
	 *  may direct a test case to fail).
	 */
	public static final int COORDINATOR_RESPONSE_TIME__DECISION = 10;

	/**
	 * The registry object
	 */
	private static final TestParticipantStorage PARTICIPANT_STORE = new TestParticipantStorage();
	
	/**
	 * 
	 * @author Hannes Erven, Georg Hicker (C) 2006
	 *
	 */
	private static class TestParticipantStorage {
		/**
		 * This map stores all registrations... key is the testparticipant ID, value is
		 * a @see #Test
		 */
		private final HashMap participantMap = new HashMap();
		
		/**
		 * Current participant ID - must not be used outside @see #getNextTParticipanID() 
		 */
		private int lastParticipantID = 1000;
		
		/**
		 * Increments @see #lastParticipantID by one, and returns the current value as string.
		 * @return
		 */
		public synchronized String getNextTParticipantID(){
			this.lastParticipantID++;
			
			return ""+this.lastParticipantID;
		}
		
		/**
		 * Puts an entry into the participant-map. 
		 * (is thread-safe).
		 * @param participantID The participant ID, @see #getNextTParticipantID()
		 * @param entry the entry
		 */
		public void putEntry(final String participantID, final TestParcipantStorageEntry entry){
			synchronized (this.participantMap) {
				System.out.println("Recorded entry: pID="+participantID+", entry="+entry);
				
				this.participantMap.put(participantID, entry);
			}
		}
		
		/**
		 * Fetches an entry from the participant map.
		 * (is thread-safe)
		 * @param participantID The ID 
		 * @return The entry.
		 */
		public TestParcipantStorageEntry getEntry(final String participantID){
			synchronized (this.participantMap) {
				return (TestParcipantStorageEntry) this.participantMap.get(participantID);
			}
		}
	}
	/**
	 * Hidden inner type for storing all information about a particular
	 * test participant. 
	 * 
	 * @author Hannes Erven, Georg Hicker (C) 2006
	 */
	private static class TestParcipantStorageEntry {
		private final EndpointReference protocolService;
		private final String participantReference; 
		private final ParticipantState state;
		private final QName protocol;
		private final Vector outstandingEvents = new Vector();
		private final Vector watchers = new Vector();
		
		/**
		 * This class is a small watch entry that is used to keep track
		 * of threads that watch for some messages to appear.
		 * 
		 * @author Hannes Erven, Georg Hicker (C) 2006
		 *
		 */
		private static class TestParticipantStorageEntryWatcher {
			private final QName[] watchFor;
			private QName appearedMessage = null;
			
			/**
			 * Returns the appeared message.
			 * @return
			 */
			public QName getAppearedMessage() {
				return this.appearedMessage;
			}

			/**
			 * Set the appeared message. Also notifies on this.
			 * @param xappearedMessage
			 */
			public void setAppearedMessage(final QName xappearedMessage) {
				synchronized (this) {
					if (this.appearedMessage != null)
						throw new IllegalArgumentException("There already appeared the message "+this.appearedMessage);

					this.appearedMessage = xappearedMessage;
					this.notifyAll();
				}
			}

			/**
			 * Build a new Watcher entry.
			 * @param xwatchFor The QNames to look for.
			 */
			public TestParticipantStorageEntryWatcher(final QName[] xwatchFor) {
				this.watchFor = xwatchFor;
			}

			/**
			 * Returns the QNames we watch out for.
			 * @return
			 */
			public QName[] getWatchFor() {
				return this.watchFor;
			}
		}
		
		/**
		 * A new testparticipant entry.
		 * 
		 * @param pprotocolService Protocol Service, returned from register()
		 * @param pparticipantReference Current participant reference
		 * @param pprotocol Protocol the participant has registered for
		 */
		public TestParcipantStorageEntry(
				final EndpointReference pprotocolService, 
				final String pparticipantReference, 
				final URI pprotocol
		) {
			
			this.protocolService = pprotocolService;
			this.participantReference = pparticipantReference;
			this.protocol = (new ProtocolType(pprotocol.toString()).getProtocol()) ;
			
			this.state = new ParticipantState(this.protocol);
		}
		
		/**
		 * The participant reference
		 * @return that
		 */
		public String getParticipantReference() {
			return this.participantReference;
		}
		
		/**
		 * The protocol
		 * @return that
		 */
		public QName getProtocol() {
			return this.protocol;
		}
		
		/**
		 * The coordinator's protocol services endpoint, complete with references
		 * @return that
		 */
		public EndpointReference getProtocolService() {
			return this.protocolService;
		}
		
		/**
		 * The participant's state object.
		 * @return that
		 */
		public ParticipantState getState() {
			return this.state;
		}
		
		/**
		 * Fetches a coordinator stub initialized with the given protocol services endpoint.
		 * @return that.
		 * @throws RemoteException Errors, Errors
		 */
		public BACoordinatorStub getBACoordinatorStub() throws RemoteException{
			try{
				if (ProtocolType.PROTOCOL_ID_CC.equals( this.protocol ))
					return new BAwCCCoordinatorStub(this.protocolService);
				else if (ProtocolType.PROTOCOL_ID_PC.equals( this.protocol ))
					return new BAwPCCoordinatorStub(this.protocolService);
				else
					throw new IllegalArgumentException("getBACoordinatorStub: unknown protocol "+this.protocol);
			}catch(Exception e){
				throw new RemoteException("getBAcoordinatorStub: "+e.getMessage());
			}
		}
		
		/**
		 * Adds an event to this participant's event queue.
		 * This method is thread-safe (synchronizes on @see #outstandingEvents )
		 * @param event The event.
		 */
		public void addEvent(final EventType event){
			synchronized (this.outstandingEvents) {
				this.outstandingEvents.add(event);
			}
		}
		
		/**
		 * Add a watcher. As soon as one of the given messages arrive, notify is called
		 * on the watcher entry and the "appearedMessage"-Field is set to the catched message.
		 * Callers may chose to wait on the returned object, but always supply a timeout and frequently
		 * check back on the appearedMessage-Field, for the case that notify() did not wake your thread
		 * properly.
		 * 
		 * @param watchFor A bunch of QNames (@see State) matching incoming messages
		 * @return A watcher object. Use this to wait for incoming messages and to check back which message
		 *  got caught.
		 */
		public TestParticipantStorageEntryWatcher addWatcher(final QName[] watchFor){
			if (watchFor == null || (! (watchFor.length>0)) ){
				throw new IllegalArgumentException("watchFor must contain at least one QName");
			}
			
			final TestParticipantStorageEntryWatcher w = new TestParticipantStorageEntryWatcher(watchFor);

			synchronized (this.watchers) {
				this.watchers.add(w);
			}
			return w;
		}
		/**
		 * Return and reset all outstanding events.
		 * This method is thread-safe (synchronizes on @see #outstandingEvents )
		 * @return @see #addEvent(EventType)
		 */
		public EventType[] getEventsAndClear(){
			synchronized (this.outstandingEvents) {
				final EventType[] ret = (EventType[]) this.outstandingEvents.toArray();
				this.outstandingEvents.clear();
				
				return ret;
			}
		}

		/**
		 * Iterate through all watchers of the current object, and notify
		 * any watcher who is looking for type. Remove notified watchers.
		 * @param type The incoming message type.
		 */
		public void checkForWatchersAndNotifyThem(final QName type) {
			synchronized (this.watchers) {
				for(int i=0; i<this.watchers.size(); i++){
					final TestParticipantStorageEntryWatcher w = (TestParticipantStorageEntryWatcher)
						this.watchers.get(i);
					
					synchronized (w) {
						for(int j=0; j<w.getWatchFor().length; j++){
							if (type.equals( w.getWatchFor()[j] )){
								// Hit!
								this.watchers.remove(i);
								i--;

								// SetAppeared also notifiesAll()
								w.setAppearedMessage(type);
								
								break;
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * The QName testparticipant ids are added with to endpointreferences.
	 */
	public static final QName QNAME_REFERENCE_PROPERTY = new QName("http://ws.apache.org/kandula/test/ba/", "testParticipantReference");
	
	/**
	 * Records a registerResponse
	 */
	private synchronized void registerParticipant(
			final String participantID,
			final URI protocol,
			final RegisterResponseType regresp
	){
		final TestParcipantStorageEntry tpse = new TestParcipantStorageEntry(
				new EndpointReference(regresp.getCoordinatorProtocolService()),
				participantID,
				protocol
		);
		PARTICIPANT_STORE.putEntry(participantID, tpse);
	}
	
	/**
	 * Get the participant associated with the current message. If no participant
	 * was found, returns a RemoteException.
	 * @return The participant storage entry, if found
	 * @throws RemoteException if no participant storage entry could be located
	 */
	private TestParcipantStorageEntry getTestParticipantStorageEntryFromMessage() throws RemoteException{
		try {
			final AddressingHeaders headers = 
				org.apache.kandula.utils.AddressingHeaders.getAddressingHeadersOfCurrentMessage();
			
			final String pID = headers.getReferenceProperties().get(QNAME_REFERENCE_PROPERTY).getValue();
			
			final TestParcipantStorageEntry tpse = PARTICIPANT_STORE.getEntry(pID);
			
			if (tpse == null){
				throw new RemoteException("");
			}
			return tpse;
			
		} catch (NullPointerException e) {
			throw new RemoteException("No addressing headers found, they were null, or there was no "+QNAME_REFERENCE_PROPERTY+" element in them");
		}
	}
	
	/**
	 * Get the participant associated with the given ID, throws a remoteex if null.
	 
	 * @return The participant storage entry, if found
	 * @throws RemoteException if no participant storage entry could be located
	 */
	private TestParcipantStorageEntry getTestParticipantStorageEntryByID(final String pID) throws RemoteException{
		final TestParcipantStorageEntry tpse = PARTICIPANT_STORE.getEntry(pID);
		
		if (tpse == null)
			throw new RemoteException("No participant is known by the testparticipantID "+pID);
		
		return tpse;
	}
	
	/**
	 * Registers with a WS-BA coordinator.
	 * ATTENTION: the given RegisterType is used a bit differently here, see its documentation! 
	 * 
	 * @param params Contains the request parameters. ATTENTION! instead of an EndpointReference of the participant,
	 *   this object must contain the endpoint reference of the context's registration service! 
	 */
	public TestRegisterResponseType doTestRegisterOperation(final RegisterType params) throws RemoteException {
		final RegistrationStub rs;
		
		if (params != null ) {
			try {
				// Try locating the given registration service
				
				rs = new RegistrationStub(new EndpointReference(params.getParticipantProtocolService()));
				
				// Prepare the local endpoint
				final RegisterType regparams = new RegisterType();
				final String curTPid = PARTICIPANT_STORE.getNextTParticipantID();		// This identifier is used to identify multiple "instances" of test participants
				
				final EndpointReference myEndpoint = new EndpointReference(CoordinationService.getInstance().getDemoService());
				final ReferencePropertiesType refProps = new ReferencePropertiesType();
				refProps.set_any(new MessageElement[]{ new MessageElement(QNAME_REFERENCE_PROPERTY, curTPid) });
				myEndpoint.setProperties(refProps);
				
				// 
				regparams.setParticipantProtocolService(myEndpoint);
				regparams.setProtocolIdentifier(params.getProtocolIdentifier());
				
				final RegisterResponseType rrt = rs.registerOperation(regparams);
				
				// It's done, we have registered... record the registration
				this.registerParticipant(curTPid, params.getProtocolIdentifier(), rrt);
				
				// Return ...
				final TestRegisterResponseType trrt = new TestRegisterResponseType(rrt, curTPid);
				return trrt;
			} catch (AxisFault e) {
				e.printStackTrace();
				throw e;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				throw new RemoteException("Exception in BaTestSuiteBAwPCTestParticipantImpl.doTestRegisterOperation: malformed URL", e);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw e;
			}
		} // End check if parameters empty
		throw new RemoteException("BaTestSuiteBAwPCTestParticipantImpl.doTestRegisterOperation: One of the parameters was null!");
	}
	
	
	/**
	 * Tests the coordinator's Exit method.
	 */
	public boolean doTestExitOperation(final TestExitRequestType exit) throws RemoteException {
		final TestParcipantStorageEntry tpse = getTestParticipantStorageEntryByID(exit.getTestParticipantReference());
		
		boolean messagePossible = tpse.getState().handleOutgoingMessage(State.MESSAGE_EXIT);
		
		if (messagePossible) {
			final BACoordinatorStub coord = tpse.getBACoordinatorStub();
			coord.exitOperation(exit.getExit());
			
			final QName appearedMessage = waitFor(tpse, new QName[]{ State.MESSAGE_EXITED }, COORDINATOR_RESPONSE_TIME__NODECISION);
			
			return (appearedMessage == null ? false : true );
		}
		throw State.GET_INVALID_STATE_SOAP_FAULT(tpse.getState().getCurrentState());
	}
	
	/**
	 * 
	 * @param tpse
	 * @param message_exited
	 * @return
	 */
	private QName waitFor(
			final TestParcipantStorageEntry tpse, 
			final QName[] messages,
			final int timeoutSeconds
	) {
		if (messages == null || messages.length==0)
			return null;
			
		final TestParcipantStorageEntry.TestParticipantStorageEntryWatcher tpsew =  tpse.addWatcher(messages);
		
		// Check back frequently
		int curIteration=0;
		while(tpsew.getAppearedMessage() == null){
			// Check if time out
//			if (curIteration>0 && curIteration>timeoutSeconds)
			if (curIteration>timeoutSeconds)
				break;

			// Wait ... 
			try{
				synchronized (tpsew) {
					tpsew.wait(1000);
				}
			}catch(InterruptedException x){
				// Ignore and continue
			}
			
			curIteration++;
			System.out.println("WaitFor in iteration "+curIteration);
		}
		System.out.println("WaitFor returned "+tpsew.getAppearedMessage()+" after "+curIteration+" iterations.");
		return tpsew.getAppearedMessage();
	}
	
	
	/**
	 * Handle incoming WS-BA coordinator message.
	 * @param type
	 * @throws RemoteException 
	 */
	private synchronized void handleIncomingWSBAmessage(final QName type, final AnyContentType param) throws RemoteException{
		final TestParcipantStorageEntry tpse = this.getTestParticipantStorageEntryFromMessage();
		
		if (State.MESSAGE_GETSTATUS.equals(type)){
			// Send back our state
			final StatusType st = new StatusType();
			st.setState(StateType.fromValue(tpse.getState().getCurrentState()));
			tpse.getBACoordinatorStub().statusOperation(st);
			
			final EventType_Message etm = new EventType_Message();
			etm.setMessageType(State.MESSAGE_GETSTATUS);
			etm.setNewState(st.getState());
			etm.setTimestamp(new Date());
			tpse.addEvent(etm);
		}else if (State.MESSAGE_STATUS.equals(type)){
			final StateType st = (StateType) param;
			System.out.println("*** State of "+tpse+" is: "+st);
			
			final EventType_Status ets = new EventType_Status();
			ets.setState(st);
			ets.setTimestamp(new Date());
			tpse.addEvent(ets);
		}else{
			// Check state against state map
			final EventType_Message etm = new EventType_Message();
			etm.setTimestamp(new Date());
			etm.setMessageType(type);

			try {
				tpse.getState().transistStateByMessage(type);
				
				etm.setNewState(StateType.fromValue( tpse.getState().getCurrentState() ));
				tpse.addEvent(etm);
			} catch (RemoteException e) {
				System.out.println("RM: Message "+type+" could not be accepted in state "+tpse.getState().getCurrentState()+": "+e);
				
				tpse.addEvent(etm);
				throw e;
			} catch(Exception e){
				System.out.println("EX: Message "+type+" could not be accepted in state "+tpse.getState().getCurrentState()+": "+e);
				e.printStackTrace();

				tpse.addEvent(etm);
				throw new RemoteException("Message "+type+" could not be accepted: "+e);
			}
			tpse.addEvent(etm);
		}

		/*
		 *  If we got as far as here, the message was accepted and all work has been done
		 *  Now, check if some threads have been waiting for this message and unblock them
		 */
		tpse.checkForWatchersAndNotifyThem(type);
	}
	
	/*
	 * Public test class methods are below
	 */

	/**
	 * Get the coordinator's state
	 */
	public boolean doTestGetStatusOperation(final TestGetStatusRequestType param) throws RemoteException {
		final TestParcipantStorageEntry tpse = getTestParticipantStorageEntryByID(param.getTestParticipantReference());
		
		final BACoordinatorStub coord = tpse.getBACoordinatorStub();
		coord.getStatusOperation(param.getExit());
		
		final QName appearedMessage = waitFor(tpse, new QName[]{ State.MESSAGE_STATUS }, COORDINATOR_RESPONSE_TIME__NODECISION);

		return (appearedMessage == null ? false : true );
	}
	
	/**
	 * NOT YET IMPLEMENTED
	 */
	public boolean doTestStatusOperation(final TestStatusRequestType param) throws RemoteException {
		// final TestParcipantStorageEntry tpse = 
		getTestParticipantStorageEntryByID(param.getTestParticipantReference());
		
		throw new RemoteException("Status not supported!");
	}
	
	/**
	 * Send "completed" to the coordinator. 
	 */
	public boolean doTestCompletedOperation(final TestCompletedRequestType param) throws RemoteException {
		final TestParcipantStorageEntry tpse = getTestParticipantStorageEntryByID(param.getTestParticipantReference());

		boolean messagePossible = tpse.getState().handleOutgoingMessage(State.MESSAGE_COMPLETED);
		
		if (messagePossible) {
			final BACoordinatorStub coord = tpse.getBACoordinatorStub();
			coord.completedOperation(param.getCompleted());
			
			final QName appearedMessage = waitFor(tpse, new QName[]{ State.MESSAGE_CLOSE, State.MESSAGE_COMPENSATE}, COORDINATOR_RESPONSE_TIME__DECISION);
	
			return (appearedMessage == null ? false : true );
		}
		throw State.GET_INVALID_STATE_SOAP_FAULT(tpse.getState().getCurrentState());
	}

	/**
	 * Send "fault" to the coordinator
	 */
	public boolean doTestFaultOperation(final TestFaultRequestType param) throws RemoteException {
		final TestParcipantStorageEntry tpse = getTestParticipantStorageEntryByID(param.getTestParticipantReference());
		
		boolean messagePossible = tpse.getState().handleOutgoingMessage(State.MESSAGE_FAULT);
		
		if (messagePossible) {
			final BACoordinatorStub coord = tpse.getBACoordinatorStub();
			coord.faultOperation(param.getFault());
			
			final QName appearedMessage = waitFor(tpse, new QName[]{ State.MESSAGE_FAULTED }, COORDINATOR_RESPONSE_TIME__NODECISION);
	
			return (appearedMessage == null ? false : true );
		}
		throw State.GET_INVALID_STATE_SOAP_FAULT(tpse.getState().getCurrentState());
	}

	/**
	 * Send compensated
	 */
	public boolean doTestCompensatedOperation(final TestCompensatedRequestType param) throws RemoteException {
		final TestParcipantStorageEntry tpse = getTestParticipantStorageEntryByID(param.getTestParticipantReference());

		boolean messagePossible = tpse.getState().handleOutgoingMessage(State.MESSAGE_COMPENSATED);
		
		if (messagePossible) {
			final BACoordinatorStub coord = tpse.getBACoordinatorStub();
			coord.compensatedOperation(param.getCompensated());
			
			final QName appearedMessage = waitFor(tpse, new QName[]{ }, COORDINATOR_RESPONSE_TIME__NODECISION);
	
			// We don't expect a message here...
			return (appearedMessage == null ? true : false );
		}
		throw State.GET_INVALID_STATE_SOAP_FAULT(tpse.getState().getCurrentState());
	}

	/**
	 * Send closed
	 */
	public boolean doTestClosedOperation(final TestClosedRequestType param) throws RemoteException {
		final TestParcipantStorageEntry tpse = getTestParticipantStorageEntryByID(param.getTestParticipantReference());
		boolean messagePossible = tpse.getState().handleOutgoingMessage(State.MESSAGE_CLOSED);
		
		if (messagePossible) {
			final BACoordinatorStub coord = tpse.getBACoordinatorStub();
			coord.closedOperation(param.getClosed());
			
			final QName appearedMessage = waitFor(tpse, new QName[]{ }, COORDINATOR_RESPONSE_TIME__NODECISION);
	
			return (appearedMessage == null ? true : false );
		}
		throw State.GET_INVALID_STATE_SOAP_FAULT(tpse.getState().getCurrentState());
	}

	/**
	 * Send canceled
	 */
	public boolean doTestCanceledOperation(final TestCanceledRequestType param) throws RemoteException {
		final TestParcipantStorageEntry tpse = getTestParticipantStorageEntryByID(param.getTestParticipantReference());
		boolean messagePossible = tpse.getState().handleOutgoingMessage(State.MESSAGE_CANCELED);
		
		if (messagePossible) {
			final BACoordinatorStub coord = tpse.getBACoordinatorStub();
			coord.canceledOperation(param.getCanceled());
			
			final QName appearedMessage = waitFor(tpse, new QName[]{ }, COORDINATOR_RESPONSE_TIME__NODECISION);
	
			return (appearedMessage == null ? true : false );
		}
		throw State.GET_INVALID_STATE_SOAP_FAULT(tpse.getState().getCurrentState());
	}
	
	/*
	 * Below follow methods for WS-BA participants
	 */
	
	/**
	 * Cancel is called
	 */
	public void cancelOperation(final NotificationType param) throws RemoteException {
		this.handleIncomingWSBAmessage(State.MESSAGE_CANCEL, param);
	}

	/**
	 * Close is called
	 */
	public void closeOperation(final NotificationType param) throws RemoteException {
		this.handleIncomingWSBAmessage(State.MESSAGE_CLOSE, param);
	}
	
	/**
	 * Compensate is called
	 */
	public void compensateOperation(final NotificationType param) throws RemoteException {
		this.handleIncomingWSBAmessage(State.MESSAGE_COMPENSATE, param);
	}

	/**
	 * Incoming complete message
	 */
	public void completeOperation(
			final NotificationType param
	) throws RemoteException {
		this.handleIncomingWSBAmessage(State.MESSAGE_COMPLETE, param);
	}

	/**
	 * Exited is called
	 */
	public void exitedOperation(final NotificationType param) throws RemoteException {
		this.handleIncomingWSBAmessage(State.MESSAGE_EXITED, param);
	}
	
	/**
	 * Faulted is called
	 */
	public void faultedOperation(final NotificationType param) throws RemoteException {
		this.handleIncomingWSBAmessage(State.MESSAGE_FAULTED, param);
	}

	/**
	 * GetStatus is called
	 */
	public void getStatusOperation(final NotificationType param) throws RemoteException {
		this.handleIncomingWSBAmessage(State.MESSAGE_GETSTATUS, param);
	}

	/**
	 * Status is called
	 */
	public void statusOperation(final StatusType param) throws RemoteException {
		this.handleIncomingWSBAmessage(State.MESSAGE_STATUS, param);
	}

	

	/*
	 * Helper methods
	 */


	/**
	 * This method returns any events that the given participant has encountered since
	 * the last query.
	 */
	public EventType[] getEventsOperation(
			final GetEventsRequestType param
	) throws RemoteException {
		// Try to fetch the participant
		final TestParcipantStorageEntry tpse = this.getTestParticipantStorageEntryByID(param.getTestParticipantReference());
		
		// Add outstanding events to the response, and clear them
		return tpse.getEventsAndClear();
	}


	/**
	 * Fetch the states of the given test participants.
	 */
	public ParticipantReferenceWithStateType[] getStatesOperation(
			final String[] request
	) throws RemoteException {
		final ParticipantReferenceWithStateType[] resp = 
			new ParticipantReferenceWithStateType[request.length];
	
		/*
		 *  Iterate through the requested participant ids
		 *  and fetch their respective states...
		 */
		for(int i=0; i<request.length; i++){
			final String reference = request[i];
			
			// Allow test cases to perform a faked registration
			if (reference == null){
				resp[i] = null;
				continue;
			}
			
			final TestParcipantStorageEntry tpse = getTestParticipantStorageEntryByID(
					reference
			);
			
			if (tpse == null)
				throw new RemoteException("There is no participant registered with reference "+reference.toString());
			
			resp[i] = new ParticipantReferenceWithStateType(
					reference,
					StateType.fromValue( tpse.getState().getCurrentState() )
			);
		}
		
		return resp;
	}
}
