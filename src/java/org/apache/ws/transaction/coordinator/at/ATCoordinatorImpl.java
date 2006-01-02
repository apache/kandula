/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.ws.transaction.coordinator.at;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.AddressingHeaders;
import org.apache.axis.message.addressing.Constants;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.ws.transaction.coordinator.CoordinationService;
import org.apache.ws.transaction.coordinator.CoordinatorImpl;
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

	public EndpointReference register(String prot, EndpointReference pps)
			throws AxisFault {
		if (!(status == AT2PCStatus.ACTIVE || status == AT2PCStatus.PREPARING_VOLATILE))
			throw new IllegalStateException();
		CoordinationService cs = CoordinationService.getInstance();
		String ref = null;
		EndpointReference epr = null;
		if (prot.equals(PROTOCOL_ID_COMPLETION)) {
			if (pps != null)
				completionParticipants.add(pps);
			epr = cs.getCompletionCoordinatorService(this);
		} else {
			if (pps == null)
				throw new IllegalArgumentException();
			UUIDGen gen = UUIDGenFactory.getUUIDGen();
			ref = "uuid:" + gen.nextUUID();
			if (prot.equals(PROTOCOL_ID_VOLATILE_2PC))
				volatile2PCParticipants.put(ref, pps);
			else if (prot.equals(PROTOCOL_ID_DURABLE_2PC))
				durable2PCParticipants.put(ref, pps);
			else
				throw INVALID_PROTOCOL_SOAP_FAULT;
			epr = cs.getCoordinatorService(this, ref);
		}
		return epr;
	}

	public void forget2PC(String ref) {
		if (volatile2PCParticipants.remove(ref) == null)
			durable2PCParticipants.remove(ref);
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

	public void aborted(String ref) throws AxisFault {
		switch (status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			forget2PC(ref);
			rollback();
			return;

		case AT2PCStatus.COMMITTING:
			throw INVALID_STATE_SOAP_FAULT;

		case AT2PCStatus.ABORTING:
			forget2PC(ref);
			return;

		case AT2PCStatus.NONE:
		}
	}

	public void readOnly(String ref) throws AxisFault {
		switch (status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			forget2PC(ref);
			return;

		case AT2PCStatus.COMMITTING:
			throw INVALID_STATE_SOAP_FAULT;

		case AT2PCStatus.ABORTING:
			forget2PC(ref);
			return;

		case AT2PCStatus.NONE:
		}
	}

	public void replay(String ref) throws AxisFault {
		switch (status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			rollback();
			return;

		case AT2PCStatus.COMMITTING:
			EndpointReference epr = getEprOf2PCParticipant(ref);
			if (epr != null)
				try {
					new ParticipantStub(epr).commitOperation(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			return;

		case AT2PCStatus.ABORTING:
			epr = getEprOf2PCParticipant(ref);
			if (epr != null)
				try {
					new ParticipantStub(epr).rollbackOperation(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			return;

		case AT2PCStatus.NONE:
			if (volatile2PCParticipants.containsKey(ref))
				throw INVALID_STATE_SOAP_FAULT;
			epr = (EndpointReference) durable2PCParticipants.get(ref);
			if (epr != null)
				try {
					new ParticipantStub(epr).rollbackOperation(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	public void prepared(String ref) throws AxisFault {
		switch (status) {
		case AT2PCStatus.ACTIVE:
			rollback();
			throw INVALID_STATE_SOAP_FAULT;

		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			preparedParticipants.add(ref);
			return;

		case AT2PCStatus.COMMITTING:
			EndpointReference epr = getEprOf2PCParticipant(ref);
			if (epr != null)
				try {
					new ParticipantStub(epr).commitOperation(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			return;

		case AT2PCStatus.ABORTING:
			if (volatile2PCParticipants.remove(ref) != null) 
				throw INVALID_STATE_SOAP_FAULT;
			epr = (EndpointReference) durable2PCParticipants.remove(ref);
			if (epr != null) {
				try {
					new ParticipantStub(epr).rollbackOperation(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return;

		case AT2PCStatus.NONE:
			if (volatile2PCParticipants.containsKey(ref))
				throw INVALID_STATE_SOAP_FAULT;
			epr = (EndpointReference) durable2PCParticipants.get(ref);
			if (epr != null)
				try {
					new ParticipantStub(epr).rollbackOperation(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	public void committed(String ref) throws AxisFault {
		switch (status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			rollback();
			throw INVALID_STATE_SOAP_FAULT;

		case AT2PCStatus.COMMITTING:
			forget2PC(ref);
			return;

		case AT2PCStatus.ABORTING:
			throw INVALID_STATE_SOAP_FAULT;

		case AT2PCStatus.NONE:
		}
	}

	private EndpointReference getEprOf2PCParticipant(String ref) {
		EndpointReference epr = (EndpointReference) volatile2PCParticipants.get(ref);
		if (epr != null)
			return epr;
		return (EndpointReference) durable2PCParticipants.get(ref);
	}

	private boolean prepare(Map participants) {
		int iters = 0;
		int status_old = status;

		while (iters < maxRetries) {
			if (iters++ > 0)
				pause(RETRY_DELAY_MILLIS - RESPONSE_DELAY_MILLIS);

			Iterator iter = participants.values().iterator();
			while (iter.hasNext()) {
				if (status == AT2PCStatus.ABORTING)
					return false;
				try {
					new ParticipantStub((EndpointReference) iter.next()).prepareOperation(null);
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
				Iterator iter = participants.values().iterator();
				while (iter.hasNext())
					try {
						ParticipantStub p = new ParticipantStub(
								(EndpointReference) iter.next());
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

	private AddressingHeaders getAddressingHeaders() {
		return (AddressingHeaders) MessageContext.getCurrentContext().getProperty(
			Constants.ENV_ADDRESSING_REQUEST_HEADERS);
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
}