/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.kandula.coordinator.at;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.soap.Name;

import org.apache.axis.AxisFault;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.AddressingHeaders;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.kandula.coordinator.CoordinationService;
import org.apache.kandula.coordinator.CoordinatorImpl;
import org.apache.kandula.coordinator.InvalidCoordinationProtocolException;
import org.apache.kandula.coordinator.TimedOutException;
import org.apache.kandula.wsat.Notification;

/**
 * @author Dasarath Weeratunge
 * @author Hannes Erven <hannes@erven.at>
 *  
 */
public class ATCoordinatorImpl extends CoordinatorImpl implements ATCoordinator {

	int status = AT2PCStatus.ACTIVE;

	Set preparedParticipants = new HashSet();

	List completionParticipants = new ArrayList();

	Map volatile2PCParticipants = new HashMap();

	Map durable2PCParticipants = new HashMap();

	public static int maxRetries = 10;

	public static final int RETRY_DELAY_MILLIS = 20 * 1000;

	public ATCoordinatorImpl() throws MalformedURIException {
		super(COORDINATION_TYPE_ID);
	}

	/**
	 * Register a new participant 
	 */
	public EndpointReference register(
			final String protocol,
			final EndpointReference participantProtocolService,
			final String matchcode
	) throws InvalidCoordinationProtocolException {

		if (!(this.status == AT2PCStatus.ACTIVE || this.status == AT2PCStatus.PREPARING_VOLATILE))
			throw new IllegalStateException();

		CoordinationService cs = CoordinationService.getInstance();
		String participantRef = null;
		EndpointReference epr = null;

		if (protocol.equals(PROTOCOL_ID_COMPLETION)) {
			if (participantProtocolService != null)
				this.completionParticipants.add(participantProtocolService);

			epr = cs.getCompletionCoordinatorService(this);
		} else {
			if (participantProtocolService == null)
				throw new IllegalArgumentException();

			UUIDGen gen = UUIDGenFactory.getUUIDGen();
			participantRef = "uuid:" + gen.nextUUID();

			if (protocol.equals(PROTOCOL_ID_VOLATILE_2PC))
				this.volatile2PCParticipants.put(participantRef,
					participantProtocolService);
			else if (protocol.equals(PROTOCOL_ID_DURABLE_2PC))
				this.durable2PCParticipants.put(participantRef,
					participantProtocolService);
			else
				throw new InvalidCoordinationProtocolException();

			epr = cs.getCoordinatorService(this, participantRef);
		}

		return epr;
	}

	/**
	 * Forget about this particular participant of the transaction. We only do
	 * this when that participant has acknowledged the final transaction
	 * outcome.
	 * 
	 * @param participantRef
	 *            The participant reference.
	 */
	public void forget2PC(String participantRef) {
		/*
		 * Check for which protocols the participants had registered and remove
		 */
		if (this.volatile2PCParticipants.remove(participantRef) == null)
			this.durable2PCParticipants.remove(participantRef);

		notifyAll();
	}

	public void rollback() {
		switch (this.status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			this.status = AT2PCStatus.ABORTING;
			terminate();
			return;

		case AT2PCStatus.COMMITTING:
		case AT2PCStatus.ABORTING:
		case AT2PCStatus.NONE:
		}
	}

	public void aborted(String participantRef) throws AxisFault {
		switch (this.status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			forget2PC(participantRef);
			rollback();
			return;

		case AT2PCStatus.COMMITTING:
			trigger(participantRef, INVALID_STATE_SOAP_FAULT());
			return;

		case AT2PCStatus.ABORTING:
			forget2PC(participantRef);
			return;

		case AT2PCStatus.NONE:
		}
	}

	public void readOnly(String participantRef) throws AxisFault {
		switch (this.status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			forget2PC(participantRef);
			return;

		case AT2PCStatus.COMMITTING:
			trigger(participantRef, INVALID_STATE_SOAP_FAULT());
			return;

		case AT2PCStatus.ABORTING:
			forget2PC(participantRef);
			return;

		case AT2PCStatus.NONE:
		}
	}

	private ParticipantStub getParticipantStub(String participantRef,
			EndpointReference epr) throws AxisFault, MalformedURLException {
		return new ParticipantStub(this, participantRef, epr);
	}

	public void replay(String participantRef) throws AxisFault {
		switch (this.status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			rollback();
			return;

		case AT2PCStatus.COMMITTING:
			EndpointReference epr = getEprToRespond(participantRef);
			if (epr != null)
				try {
					getParticipantStub(participantRef, epr).commitOperation(
						null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			return;

		case AT2PCStatus.ABORTING:
			epr = getEprToRespond(participantRef);
			if (epr != null)
				try {
					getParticipantStub(participantRef, epr).rollbackOperation(
						null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			return;

		case AT2PCStatus.NONE:
			if (volatile2PCParticipants.containsKey(participantRef))
				trigger(participantRef, INVALID_STATE_SOAP_FAULT());

			else {
				epr = (EndpointReference) this.durable2PCParticipants.get(participantRef);
				if (epr == null)
					epr = org.apache.kandula.utils.AddressingHeaders.getReplyToOfCurrentMessage();
				if (epr != null)
					try {
						getParticipantStub(participantRef, epr).rollbackOperation(
							null);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
	}

	/**
	 * Handles calls to the Coordinator's "prepared" method. The given
	 * participant is marked as being prepared.
	 * 
	 * @param participantRef
	 *            The participant to be marked.
	 * @throws AxisFault
	 *             Some fault, e.g. INVALID_STATE
	 */
	public void prepared(String participantRef) throws AxisFault {
		switch (this.status) {
		case AT2PCStatus.ACTIVE:
			try {
				trigger(participantRef, INVALID_STATE_SOAP_FAULT());
			} finally {
				rollback();
			}
			return;

		/*
		 * If we are currently in the PREPARE phase, mark the participant as
		 * prepared and notify the waiting thread. (wait() is called in
		 * prepare(Map) )
		 */
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			this.preparedParticipants.add(participantRef);
			notifyAll();
			return;

		case AT2PCStatus.COMMITTING:
			EndpointReference epr = getEprToRespond(participantRef);
			if (epr != null)
				try {
					getParticipantStub(participantRef, epr).commitOperation(
						null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			return;

		case AT2PCStatus.ABORTING:
			if (volatile2PCParticipants.remove(participantRef) != null)
				trigger(participantRef, INVALID_STATE_SOAP_FAULT());

			else {
				epr = (EndpointReference) this.durable2PCParticipants.remove(participantRef);
				if (epr == null)
					epr = org.apache.kandula.utils.AddressingHeaders.getReplyToOfCurrentMessage();
				if (epr != null) {
					try {
						getParticipantStub(participantRef, epr).rollbackOperation(
							null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return;

		case AT2PCStatus.NONE:
			if (volatile2PCParticipants.containsKey(participantRef))
				trigger(participantRef, INVALID_STATE_SOAP_FAULT());

			else {
				epr = (EndpointReference) this.durable2PCParticipants.get(participantRef);
				if (epr == null)
					epr = org.apache.kandula.utils.AddressingHeaders.getReplyToOfCurrentMessage();
				if (epr != null)
					try {
						getParticipantStub(participantRef, epr).rollbackOperation(
							null);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
	}

	public void committed(String participantRef) throws AxisFault {
		switch (this.status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			try {
				trigger(participantRef, INVALID_STATE_SOAP_FAULT());
			} finally {
				rollback();
			}
			return;

		case AT2PCStatus.COMMITTING:
			forget2PC(participantRef);
			return;

		case AT2PCStatus.ABORTING:
			trigger(participantRef, INVALID_STATE_SOAP_FAULT());
			return;

		case AT2PCStatus.NONE:
		}
	}

	/**
	 * Send a "prepare" message to all participants in the keySet of
	 * "participants". The messages are resent up to "maxRetries" times; between
	 * each retry, up to RETRY_DELAY_MILLIS milliseconds are waited.
	 * 
	 * @param participants
	 *            The map which contains the participants in its keySet.
	 * @return true, if all participants are prepared. false, if there are
	 *         unprepared participants remaining after trying maxRetries times.
	 */
	private boolean prepare(Map participants) {
		/*
		 * Check if there are any participants in this map
		 */
		if (participants.size() == 0)
			/*
			 * "No participants" means OK
			 */
			return true;

		int iters = 0; // iteration count
		int status_old = this.status; // State when beginning to prepare

		/*
		 * Send the "prepare" message to all unprepared participants. Retry up
		 * to maxRetries times.
		 */
		while (iters < maxRetries) {
			Iterator iter = participants.keySet().iterator();
			while (iter.hasNext()) {
				if (this.status == AT2PCStatus.ABORTING)
					return false;
				try {
					/*
					 * Call the participant's "prepare" method
					 */
					String participantRef = (String) iter.next();
					getParticipantStub(participantRef,
						(EndpointReference) participants.get(participantRef)).prepareOperation(
						null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			/*
			 * Wait for arriving messages, max. RETRY_DELAY_MILLIS milliseconds
			 * for each retry.
			 */
			long startTime = (new Date()).getTime();
			long curTime;

			/*
			 * For each retry: wait for incoming messages. If unprepared
			 * participants remain, wait for the remaining time up until
			 * RETRY_DELAY_MILLIS is reached or another message arrives.
			 */
			while ((curTime = (new Date().getTime())) < startTime
					+ RETRY_DELAY_MILLIS) {
				/*
				 * Wait for incoming messages. notifyAll() is called in
				 * prepared(String).
				 */
				try {
					wait(startTime - curTime + RETRY_DELAY_MILLIS);
				} catch (Exception e) {
					// No exception handling needed for wait();
				}

				/*
				 * Are all participants prepared?
				 */
				if (this.preparedParticipants.containsAll(participants.keySet()))
					// Yes! - Return true, if the transaction state did not
					// change in the mean time.
					return this.status == status_old;
			}
		}

		/*
		 * After trying so hard and sending maxRetries messages, there is still
		 * at least one unprepared participant.
		 */
		return false;
	}

	private boolean prepare() {
		this.status = AT2PCStatus.PREPARING_VOLATILE;
		if (! prepare(this.volatile2PCParticipants))
			return false;

		this.status = AT2PCStatus.PREPARING_DURABLE;
		return prepare(this.durable2PCParticipants);
	}

	public void commit() {
		switch (this.status) {
		case AT2PCStatus.ACTIVE:
			break;

		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
		case AT2PCStatus.COMMITTING:
		case AT2PCStatus.ABORTING:
		case AT2PCStatus.NONE:
			return;
		}

		if (!prepare()) {
			rollback();
			return;
		}

		this.status = AT2PCStatus.COMMITTING;
		terminate();
	}

	private boolean noParticipantsToTerminate() {
		return this.volatile2PCParticipants.isEmpty()
				&& this.durable2PCParticipants.isEmpty();
	}

	/**
	 * Handles transaction teardown and communicate the final state to
	 * participants. Each participant is notified up to maxRetries times.
	 * Between each message, we will wait up to RETRY_DELAY_MILLIS milliseconds.
	 */
	private void terminate() {
		int iters = 0;

		/*
		 * This is the main loop. While there are participants who did not yet
		 * acknowledge our message, (re)send our message to them.
		 */
		waitForAllParticipantsToComplete: while (iters < maxRetries
				&& !noParticipantsToTerminate()) {
			/*
			 * Participant set to operate on. For each retry, send our message
			 * to volatile peers first and then to durable participants.
			 */
			Map participants = this.volatile2PCParticipants;
			while (true) {
				Iterator iter = participants.keySet().iterator();

				while (iter.hasNext()) {
					try {
						/*
						 * Get the participant's protocol service and send our
						 * final state message.
						 */
						String participantRef = (String) iter.next();
						ParticipantStub p = getParticipantStub(
							participantRef,
							(EndpointReference) participants.get(participantRef));

						if (this.status == AT2PCStatus.ABORTING)
							p.rollbackOperation(null);
						else
							p.commitOperation(null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				/*
				 * After all volatile participants are notified, continue with
				 * durable participants. After that, wait for incoming messages.
				 */
				if (participants == this.volatile2PCParticipants)
					participants = this.durable2PCParticipants;
				else
					break;
			}

			/*
			 * Messages to all remaining participants are out. Wait up until
			 * RETRY_DELAY_MILLIS milliseconds for replies. On each incoming
			 * reply, forget2PC() will call notify so we can check if all
			 * participants have acknownledged the transaction outcome and can
			 * thus continue. If there are remaining peers after an incoming
			 * message, return to wait and sleep for the rest of the
			 * RETRY_DELAY_MILLIS period.
			 */
			try {
				long startTime = (new Date()).getTime();
				long curTime;

				while ((curTime = (new Date().getTime())) < startTime
						+ RETRY_DELAY_MILLIS) {
					/*
					 * Wait for incoming acknowledgements. notify() is called in
					 * forget2PC(String).
					 */
					wait(startTime - curTime + RETRY_DELAY_MILLIS);

					if (noParticipantsToTerminate())
						break waitForAllParticipantsToComplete;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/*
		 * All particpants have acknowledged the final transaction state. Notify
		 * all peers who have subscribed for the completion protocol and forget
		 * about our litte transaction. ;-)
		 */
		if (noParticipantsToTerminate()) {
			/*
			 * TODO WSAT shouldn't this message also be acknowledged, at least if
			 * there was an exception (e.g. timeout) caught?
			 */
			Iterator iter = this.completionParticipants.iterator();
			while (iter.hasNext())
				try {
					CompletionInitiatorStub p = new CompletionInitiatorStub(
							(EndpointReference) iter.next());
					if (this.status == AT2PCStatus.ABORTING)
						p.abortedOperation(null);
					else
						p.committedOperation(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		this.status = AT2PCStatus.NONE;
	}

	public synchronized void preparedOperation(Notification parameters)
			throws RemoteException {
		prepared(getParticipantRef());
	}

	public synchronized void abortedOperation(Notification parameters)
			throws RemoteException {
		aborted(getParticipantRef());
	}

	public synchronized void readOnlyOperation(Notification parameters)
			throws RemoteException {
		readOnly(getParticipantRef());
	}

	public synchronized void committedOperation(Notification parameters)
			throws RemoteException {
		committed(getParticipantRef());
	}

	public synchronized void replayOperation(Notification parameters)
			throws RemoteException {
		replay(getParticipantRef());
	}

	private EndpointReference getEprToSendFault(String participantRef) {
		EndpointReference epr = org.apache.kandula.utils.AddressingHeaders.getFaultToOfCurrentMessage();
		if (epr != null)
			return epr;
		return getEprToRespond(participantRef);
	}

	private EndpointReference getEprToRespond(String participantRef) {
		EndpointReference epr = (EndpointReference) this.volatile2PCParticipants.get(participantRef);
		if (epr != null)
			return epr;
		epr = (EndpointReference) this.durable2PCParticipants.get(participantRef);
		if (epr != null)
			return epr;
		return org.apache.kandula.utils.AddressingHeaders.getReplyToOfCurrentMessage();
	}

	private String getParticipantRef() {
		AddressingHeaders headers = org.apache.kandula.utils.AddressingHeaders.getAddressingHeadersOfCurrentMessage();
		MessageElement e = headers.getReferenceProperties().get(PARTICIPANT_REF);
		return e.getValue();
	}

	public synchronized void commitOperation(Notification parameters)
			throws RemoteException {
		commit();
	}

	public synchronized void rollbackOperation(Notification parameters)
			throws RemoteException {
		rollback();
	}

	public synchronized void timeout() throws TimedOutException {
		System.out.println("[ATCoordinatorImpl] timeout "
				+ AT2PCStatus.getStatusName(this.status));

		if (this.status != AT2PCStatus.NONE) {
			maxRetries = 8;
			rollback();
			throw new TimedOutException();
		}
	}

	private void trigger(String participantRef, AxisFault fault)
			throws AxisFault {
		trigger(getEprToSendFault(participantRef), fault);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ws.transaction.coordinator.Callback#onFault(javax.xml.soap.Name)
	 */
	public synchronized void onFault(Name code) {
		// TODO WSAT Auto-generated method stub

	}

	public EndpointReference getEndpointReference() {
		throw new UnsupportedOperationException();
	}

}