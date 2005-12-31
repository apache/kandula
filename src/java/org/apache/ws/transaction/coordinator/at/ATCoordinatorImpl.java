/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.ws.transaction.coordinator.at;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.apache.ws.transaction.coordinator.InvalidCoordinationProtocolException;
import org.apache.ws.transaction.coordinator.TimedOutException;
import org.apache.ws.transaction.wsat.Notification;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class ATCoordinatorImpl extends CoordinatorImpl implements ATCoordinator {

	int status = AT2PCStatus.NONE;

	private static final int VOLATILE = 0;

	private static final int DURABLE = 1;

	Map participants2PC[] = new Map[2];

	Set prepared = Collections.synchronizedSet(new HashSet());

	List participantsComp = Collections.synchronizedList(new ArrayList());

	public static final int MAX_RETRIES = 3;

	public static final int RETRY_DELAY_MILLIS = 15 * 1000;

	public static final int RESPONSE_DELAY_MILLIS = 3 * 1000;

	public ATCoordinatorImpl() throws MalformedURIException {
		super(COORDINATION_TYPE_ID);
		status = AT2PCStatus.ACTIVE;
		for (int i = 0; i < 2; i++)
			participants2PC[i] = Collections.synchronizedMap(new HashMap());
	}

	public EndpointReference register(String prot, EndpointReference pps)
			throws InvalidCoordinationProtocolException {
		if (!(status == AT2PCStatus.ACTIVE || status == AT2PCStatus.PREPARING_VOLATILE))
			throw new IllegalStateException();
		CoordinationService cs = CoordinationService.getInstance();
		String ref = null;
		EndpointReference epr = null;
		if (prot.equals(PROTOCOL_ID_COMPLETION)) {
			if (pps != null)
				participantsComp.add(pps);
			epr = cs.getCompletionCoordinatorService(this);
		} else {
			if (pps == null)
				throw new IllegalArgumentException();
			UUIDGen gen = UUIDGenFactory.getUUIDGen();
			ref = "uuid:" + gen.nextUUID();
			if (prot.equals(PROTOCOL_ID_VOLATILE_2PC))
				participants2PC[VOLATILE].put(ref, pps);
			else if (prot.equals(PROTOCOL_ID_DURABLE_2PC))
				participants2PC[DURABLE].put(ref, pps);
			else
				throw new InvalidCoordinationProtocolException();
			epr = cs.getCoordinatorService(this, ref);
		}
		return epr;
	}

	public void forget2PC(String ref) {
		if (participants2PC[VOLATILE].remove(ref) == null)
			participants2PC[DURABLE].remove(ref);
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

	public void aborted(String ref) {
		switch (status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			forget2PC(ref);
			rollback();
			return;

		case AT2PCStatus.COMMITTING:
			throw new IllegalStateException();

		case AT2PCStatus.ABORTING:
			forget2PC(ref);
			return;

		case AT2PCStatus.NONE:
		}
	}

	public void readOnly(String ref) {
		switch (status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			forget2PC(ref);
			return;

		case AT2PCStatus.COMMITTING:
			throw new IllegalStateException();

		case AT2PCStatus.ABORTING:
			forget2PC(ref);
			return;

		case AT2PCStatus.NONE:
		}
	}

	public void replay(String ref) {
		switch (status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			rollback();
			return;

		case AT2PCStatus.COMMITTING:
			EndpointReference epr = getEpr(ref);
			try {
				new ParticipantStub(epr).commitOperation(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;

		case AT2PCStatus.ABORTING:
			epr = getEpr(ref);
			try {
				new ParticipantStub(epr).rollbackOperation(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;

		case AT2PCStatus.NONE:
			epr = (EndpointReference) participants2PC[VOLATILE].get(ref);
			if (epr != null)
				throw new IllegalStateException();
			epr = (EndpointReference) participants2PC[DURABLE].get(ref);
			try {
				new ParticipantStub(epr).rollbackOperation(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void prepared(String ref) {
		switch (status) {
		case AT2PCStatus.ACTIVE:
			rollback();
			throw new IllegalStateException();

		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			prepared.add(ref);
			return;

		case AT2PCStatus.COMMITTING:
			EndpointReference epr = getEpr(ref);
			try {
				new ParticipantStub(epr).commitOperation(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;

		case AT2PCStatus.ABORTING:
			epr = getEpr(ref);
			try {
				new ParticipantStub(epr).rollbackOperation(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			forget2PC(ref);
			return;

		case AT2PCStatus.NONE:
			epr = (EndpointReference) participants2PC[VOLATILE].get(ref);
			if (epr != null)
				throw new IllegalStateException();
			epr = (EndpointReference) participants2PC[DURABLE].get(ref);
			try {
				new ParticipantStub(epr).rollbackOperation(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void committed(String ref) {
		switch (status) {
		case AT2PCStatus.ACTIVE:
		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
			rollback();
			throw new IllegalStateException();

		case AT2PCStatus.COMMITTING:
			forget2PC(ref);
			return;

		case AT2PCStatus.ABORTING:
			throw new IllegalStateException();

		case AT2PCStatus.NONE:
		}
	}

	private EndpointReference getEpr(String ref) {
		EndpointReference epr = (EndpointReference) participants2PC[VOLATILE].get(ref);
		if (epr == null)
			epr = (EndpointReference) participants2PC[DURABLE].get(ref);
		return epr;
	}

	private boolean prepare(int prot) {
		int iters = 0;
		int status_old = status;

		while (iters < MAX_RETRIES) {
			if (iters++ > 0)
				pause(RETRY_DELAY_MILLIS - RESPONSE_DELAY_MILLIS);

			Iterator iter = participants2PC[prot].values().iterator();
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

			if (prepared.containsAll(participants2PC[prot].keySet()))
				return status == status_old;
		}

		return false;
	}

	private boolean prepare() {
		status = AT2PCStatus.PREPARING_VOLATILE;
		if (!prepare(VOLATILE))
			return false;

		status = AT2PCStatus.PREPARING_DURABLE;
		return prepare(DURABLE);
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

	private void terminate() {
		int iters = 0;
		while (iters < MAX_RETRIES
				&& !(participants2PC[VOLATILE].isEmpty() && participants2PC[DURABLE].isEmpty())) {
			if (iters++ > 0)
				pause(RETRY_DELAY_MILLIS - RESPONSE_DELAY_MILLIS);

			for (int prot = VOLATILE; prot == VOLATILE || prot == DURABLE; prot = prot == VOLATILE ? DURABLE
					: -1) {
				Iterator iter = participants2PC[prot].values().iterator();
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
			}

			pause(RESPONSE_DELAY_MILLIS);
		}

		if (participants2PC[VOLATILE].isEmpty()
				&& participants2PC[DURABLE].isEmpty()) {
			Iterator iter = participantsComp.iterator();
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

	private synchronized String getParticipantRef() {
		AddressingHeaders header = (AddressingHeaders) MessageContext.getCurrentContext().getProperty(
			Constants.ENV_ADDRESSING_REQUEST_HEADERS);
		MessageElement e = header.getReferenceProperties().get(PARTICIPANT_REF);
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

	public synchronized void timeout() {
		System.out.println("[ATCoordinatorImpl] timeout " + AT2PCStatus.getStatusName(status));
		if (status == AT2PCStatus.NONE)
			return;
		rollback();
		throw new TimedOutException();		
	}
}