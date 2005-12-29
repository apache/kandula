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
import org.apache.ws.transaction.wsat.Notification;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class ATCoordinatorImpl extends CoordinatorImpl implements ATCoordinator {

	int status = AT2PCStatus.NONE;

	private static final int VOLATILE_2PC = 0;

	private static final int DURABLE_2PC = 1;

	Map participants2PC[] = new Map[2];

	Set prepared = Collections.synchronizedSet(new HashSet());

	List participantsComp = Collections.synchronizedList(new ArrayList());

	public static final int MAX_ITERS = 3;

	public static final int ITER_DELAY = 15 * 1000;

	public static final int RESPONSE_DELAY = 3 * 1000;

	public ATCoordinatorImpl() throws MalformedURIException {
		super(COORDINATION_TYPE_ID);
		status = AT2PCStatus.ACTIVE;
		for (int i = 0; i < 2; i++)
			participants2PC[i] = Collections.synchronizedMap(new HashMap());
	}

	public EndpointReference register(String prot,
			EndpointReference pps) throws InvalidCoordinationProtocolException {
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
				participants2PC[VOLATILE_2PC].put(ref, pps);
			else if (prot.equals(PROTOCOL_ID_DURABLE_2PC))
				participants2PC[DURABLE_2PC].put(ref, pps);
			else
				throw new InvalidCoordinationProtocolException();
			epr = cs.getCoordinatorService(this, ref);
		}
		return epr;
	}

	public void forget2PC(String ref) {
		if (participants2PC[VOLATILE_2PC].remove(ref) == null)
			participants2PC[DURABLE_2PC].remove(ref);
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
		case AT2PCStatus.ENDED:
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

		case AT2PCStatus.ENDED:
			return;
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

		case AT2PCStatus.ENDED:
			return;
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
			EndpointReference epr = get2PCEndpointReference(ref);
			try {
				new ParticipantStub(epr).commitOperation(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;

		case AT2PCStatus.ABORTING:
			epr = get2PCEndpointReference(ref);
			try {
				new ParticipantStub(epr).rollbackOperation(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;

		case AT2PCStatus.ENDED:
			return;
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
			EndpointReference epr = get2PCEndpointReference(ref);
			try {
				new ParticipantStub(epr).commitOperation(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;

		case AT2PCStatus.ABORTING:
			epr = get2PCEndpointReference(ref);
			try {
				new ParticipantStub(epr).rollbackOperation(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			forget2PC(ref);
			return;

		case AT2PCStatus.ENDED:
			return;
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

		case AT2PCStatus.ENDED:
			return;
		}
	}

	private EndpointReference get2PCEndpointReference(String ref) {
		EndpointReference epr = (EndpointReference) participants2PC[VOLATILE_2PC].get(ref);
		if (epr == null)
			epr = (EndpointReference) participants2PC[DURABLE_2PC].get(ref);
		return epr;
	}

	private boolean prepare(int prot) {
		int iters = 0;
		int status_old = status;

		while (iters < MAX_ITERS) {
			if (iters++ > 0)
				pause(ITER_DELAY - RESPONSE_DELAY);

			Iterator iter = participants2PC[prot].values().iterator();
			while (iter.hasNext()) {
				if (status == AT2PCStatus.ABORTING)
					return false;
				try {
					new ParticipantStub((EndpointReference) iter.next()).prepareOperation(null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			pause(RESPONSE_DELAY);

			if (prepared.containsAll(participants2PC[prot].keySet()))
				return status == status_old;
		}

		return false;
	}

	private boolean prepare() {
		status = AT2PCStatus.PREPARING_VOLATILE;
		if (!prepare(VOLATILE_2PC))
			return false;

		status = AT2PCStatus.PREPARING_DURABLE;
		return prepare(DURABLE_2PC);
	}

	public void commit() {
		switch (status) {
		case AT2PCStatus.ACTIVE:
			break;

		case AT2PCStatus.PREPARING_VOLATILE:
		case AT2PCStatus.PREPARING_DURABLE:
		case AT2PCStatus.COMMITTING:
		case AT2PCStatus.ABORTING:
		case AT2PCStatus.ENDED:
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
		while (iters < MAX_ITERS
				&& !(participants2PC[VOLATILE_2PC].isEmpty() && participants2PC[DURABLE_2PC].isEmpty())) {
			if (iters++ > 0)
				pause(ITER_DELAY - RESPONSE_DELAY);

			for (int prot = VOLATILE_2PC; prot == VOLATILE_2PC
					|| prot == DURABLE_2PC; prot = prot == VOLATILE_2PC ? DURABLE_2PC
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
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

			}

			pause(RESPONSE_DELAY);
		}

		if (participants2PC[VOLATILE_2PC].isEmpty()
				&& participants2PC[DURABLE_2PC].isEmpty()) {
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		status = AT2PCStatus.ENDED;
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
		rollback();
	}
}