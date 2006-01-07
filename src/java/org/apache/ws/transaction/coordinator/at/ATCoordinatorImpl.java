/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.ws.transaction.coordinator.at;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
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
import org.apache.ws.transaction.coordinator.CoordinationService;
import org.apache.ws.transaction.coordinator.CoordinatorImpl;
import org.apache.ws.transaction.coordinator.InvalidCoordinationProtocolException;
import org.apache.ws.transaction.coordinator.TimedOutException;
import org.apache.ws.transaction.wsat.Notification;

/**
 * @author Dasarath Weeratunge
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

	public static final int RESPONSE_DELAY_MILLIS = 3 * 1000;

	public ATCoordinatorImpl() throws MalformedURIException {
		super(COORDINATION_TYPE_ID);
	}

	public EndpointReference register(String protocol,
			EndpointReference participantProtocolService)
			throws InvalidCoordinationProtocolException {

		if (!(status == AT2PCStatus.ACTIVE || status == AT2PCStatus.PREPARING_VOLATILE))
			throw new IllegalStateException();

		CoordinationService cs = CoordinationService.getInstance();
		String participantRef = null;
		EndpointReference epr = null;

		if (protocol.equals(PROTOCOL_ID_COMPLETION)) {
			if (participantProtocolService != null)
				completionParticipants.add(participantProtocolService);

			epr = cs.getCompletionCoordinatorService(this);
		} else {
			if (participantProtocolService == null)
				throw new IllegalArgumentException();

			UUIDGen gen = UUIDGenFactory.getUUIDGen();
			participantRef = "uuid:" + gen.nextUUID();

			if (protocol.equals(PROTOCOL_ID_VOLATILE_2PC))
				volatile2PCParticipants.put(participantRef,
					participantProtocolService);
			else if (protocol.equals(PROTOCOL_ID_DURABLE_2PC))
				durable2PCParticipants.put(participantRef,
					participantProtocolService);
			else
				throw new InvalidCoordinationProtocolException();

			epr = cs.getCoordinatorService(this, participantRef);
		}

		return epr;
	}

	public void forget2PC(String participantRef) {
		if (volatile2PCParticipants.remove(participantRef) == null)
			durable2PCParticipants.remove(participantRef);
	}

	public void rollback() {
		switch (status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			status = AT2PCStatus.ABORTING;
			terminate();
			return;

		case AT2PCStatus.COMMITTING:
		case AT2PCStatus.ABORTING:
		case AT2PCStatus.NONE:
		}
	}

	public void aborted(String participantRef) throws AxisFault {
		switch (status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			forget2PC(participantRef);
			rollback();
			return;

		case AT2PCStatus.COMMITTING:
			throwFault(participantRef, INVALID_STATE_SOAP_FAULT);
			return;

		case AT2PCStatus.ABORTING:
			forget2PC(participantRef);
			return;

		case AT2PCStatus.NONE:
		}
	}

	public void readOnly(String participantRef) throws AxisFault {
		switch (status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			forget2PC(participantRef);
			return;

		case AT2PCStatus.COMMITTING:
			throwFault(participantRef, INVALID_STATE_SOAP_FAULT);
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
		switch (status) {
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
				throwFault(participantRef, INVALID_STATE_SOAP_FAULT);
			else {
				epr = (EndpointReference) durable2PCParticipants.get(participantRef);
				if (epr == null)
					epr = getReplyToEpr();
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

	public void prepared(String participantRef) throws AxisFault {
		switch (status) {
		case AT2PCStatus.ACTIVE:
			try {
				throwFault(participantRef, INVALID_STATE_SOAP_FAULT);
			} finally {
				rollback();
			}
			return;

		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			preparedParticipants.add(participantRef);
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
				throwFault(participantRef, INVALID_STATE_SOAP_FAULT);
			else {
				epr = (EndpointReference) durable2PCParticipants.remove(participantRef);
				if (epr == null)
					epr = getReplyToEpr();
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
				throwFault(participantRef, INVALID_STATE_SOAP_FAULT);
			else {
				epr = (EndpointReference) durable2PCParticipants.get(participantRef);
				if (epr == null)
					epr = getReplyToEpr();
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
		switch (status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			try {
				throwFault(participantRef, INVALID_STATE_SOAP_FAULT);
			} finally {
				rollback();
			}
			return;

		case AT2PCStatus.COMMITTING:
			forget2PC(participantRef);
			return;

		case AT2PCStatus.ABORTING:
			throwFault(participantRef, INVALID_STATE_SOAP_FAULT);
			return;

		case AT2PCStatus.NONE:
		}
	}

	private boolean prepare(Map participants) {
		int iters = 0;
		int status_old = status;

		while (iters < maxRetries) {
			if (iters++ > 0)
				pause(RETRY_DELAY_MILLIS - RESPONSE_DELAY_MILLIS);

			Iterator iter = participants.keySet().iterator();
			while (iter.hasNext()) {
				if (status == AT2PCStatus.ABORTING)
					return false;
				try {
					String participantRef = (String) iter.next();
					getParticipantStub(participantRef,
						(EndpointReference) participants.get(participantRef)).prepareOperation(
						null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			pause(RESPONSE_DELAY_MILLIS);

			if (preparedParticipants.containsAll(participants.keySet()))
				return status == status_old;
		}

		return false;
	}

	private boolean prepare() {
		status = AT2PCStatus.PREPARING_VOLATILE;
		if (!prepare(volatile2PCParticipants))
			return false;

		status = AT2PCStatus.PREPARING_DURABLE;
		return prepare(durable2PCParticipants);
	}

	public void commit() {
		switch (status) {
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

		status = AT2PCStatus.COMMITTING;
		terminate();
	}

	private void pause(long millis) {
		try {
			wait(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean noParticipantsToTerminate() {
		return volatile2PCParticipants.isEmpty()
				&& durable2PCParticipants.isEmpty();
	}

	private void terminate() {
		int iters = 0;
		while (iters < maxRetries && !noParticipantsToTerminate()) {

			if (iters++ > 0)
				pause(RETRY_DELAY_MILLIS - RESPONSE_DELAY_MILLIS);

			Map participants = volatile2PCParticipants;
			while (true) {
				Iterator iter = participants.keySet().iterator();
				while (iter.hasNext())
					try {
						String participantRef = (String) iter.next();
						ParticipantStub p = getParticipantStub(
							participantRef,
							(EndpointReference) participants.get(participantRef));
						if (status == AT2PCStatus.ABORTING)
							p.rollbackOperation(null);
						else
							p.commitOperation(null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				if (participants == volatile2PCParticipants)
					participants = durable2PCParticipants;
				else
					break;
			}

			pause(RESPONSE_DELAY_MILLIS);
		}

		if (noParticipantsToTerminate()) {

			Iterator iter = completionParticipants.iterator();
			while (iter.hasNext())
				try {
					CompletionInitiatorStub p = new CompletionInitiatorStub(
							(EndpointReference) iter.next());
					if (status == AT2PCStatus.ABORTING)
						p.abortedOperation(null);
					else
						p.committedOperation(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

		status = AT2PCStatus.NONE;
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
		EndpointReference epr = getFaultToEpr();
		if (epr != null)
			return epr;
		return getEprToRespond(participantRef);
	}

	private EndpointReference getEprToRespond(String participantRef) {
		EndpointReference epr = (EndpointReference) volatile2PCParticipants.get(participantRef);
		if (epr != null)
			return epr;
		epr = (EndpointReference) durable2PCParticipants.get(participantRef);
		if (epr != null)
			return epr;
		return getReplyToEpr();
	}

	private String getParticipantRef() {
		AddressingHeaders headers = getAddressingHeaders();
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
				+ AT2PCStatus.getStatusName(status));

		if (status != AT2PCStatus.NONE) {
			maxRetries = 3;
			rollback();
			throw new TimedOutException();
		}
	}

	private void throwFault(String participantRef, AxisFault fault)
			throws AxisFault {
		throwFault(getEprToSendFault(participantRef), fault);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ws.transaction.coordinator.Callback#onFault(javax.xml.soap.Name)
	 */
	public synchronized void onFault(Name code) {
		// TODO Auto-generated method stub

	}

	public EndpointReference getEndpointReference() {
		throw new UnsupportedOperationException();
	}

}