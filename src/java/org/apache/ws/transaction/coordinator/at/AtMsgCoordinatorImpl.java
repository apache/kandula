/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.ws.transaction.coordinator.at;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.ws.transaction.coordinator.CoordinationService;
import org.apache.ws.transaction.coordinator.InvalidCoordinationProtocolException;
import org.apache.ws.transaction.coordinator.MsgCoordinatorImpl;
import org.apache.ws.transaction.wsat.CompletionInitiatorPort;
import org.apache.ws.transaction.wsat.ParticipantPort;
import org.apache.ws.transaction.wscoor.RegistrationRequesterPort;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class AtMsgCoordinatorImpl extends MsgCoordinatorImpl implements
		AtMsgCoordinator {

	int status = At2PCStatus.NONE;

	private static final int COMPLETION = 0;

	private static final int VOLATILE_2PC = 1;

	private static final int DURABLE_2PC = 2;

	Map participants[] = new Map[3];

	Map prepared = Collections.synchronizedMap(new HashMap());

	public static final int MAX_ITERS = 3;

	public static final int ITER_DELAY = 15 * 1000;

	public static final int RESPONSE_DELAY = 5 * 1000;

	protected AtMsgCoordinatorImpl(CoordinationService cs) {
		super(cs);
		status = At2PCStatus.ACTIVE;
		for (int i = 0; i < 3; i++)
			participants[i] = Collections.synchronizedMap(new HashMap());
	}

	public String getCoordinationType() {
		return COORDINATION_TYPE;
	}

	public void register(String prot, EndpointReference pps,
			EndpointReference res) throws InvalidCoordinationProtocolException {
		UUIDGen gen = UUIDGenFactory.getUUIDGen();
		CoordinationService cs = getCoordinationService();
		String ref = null;
		EndpointReference epr = null;
		if (prot.equals(PROTOCOL_ID_COMPLETION))
			if (status == At2PCStatus.ACTIVE) {
				ref = gen.nextUUID();
				participants[COMPLETION].put(ref, pps);
				epr = cs.getCompletionCoordinatorService();
			} else
				return;
		else {
			if (prot.equals(PROTOCOL_ID_VOLATILE_2PC))
				if (status == At2PCStatus.ACTIVE
						|| status == At2PCStatus.PREPARING_VOLATILE) {
					ref = gen.nextUUID();
					participants[VOLATILE_2PC].put(ref, pps);
				} else
					return;
			else if (prot.equals(PROTOCOL_ID_DURABLE_2PC))
				if (status == At2PCStatus.ACTIVE) {
					ref = gen.nextUUID();
					participants[DURABLE_2PC].put(ref, pps);
				} else
					return;
			else
				throw new InvalidCoordinationProtocolException();
			epr = cs.getCoordinatorService();
			epr.setProperties(cs.getReferenceProperties(this, ref));
		}

		try {
			new RegistrationRequesterPort(res).registerResponseOperation(epr);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void forget2PC(String ref) {
		if (participants[VOLATILE_2PC].get(ref) != null)
			participants[VOLATILE_2PC].remove(ref);
		else
			participants[DURABLE_2PC].remove(ref);
	}

	public void rollback() {
		switch (status) {
		case At2PCStatus.ACTIVE:
		case At2PCStatus.PREPARING_VOLATILE:
		case At2PCStatus.PREPARING_DURABLE:
			status = At2PCStatus.ABORTING;
			break;

		case At2PCStatus.COMMITTING:
		case At2PCStatus.ABORTING:
		case At2PCStatus.ENDED:
			return;

		}

		terminate();
	}

	public void aborted(String ref) {
		switch (status) {
		case At2PCStatus.ACTIVE:
		case At2PCStatus.PREPARING_VOLATILE:
		case At2PCStatus.PREPARING_DURABLE:
			forget2PC(ref);
			rollback();
			return;

		case At2PCStatus.COMMITTING:
			throw new IllegalStateException();

		case At2PCStatus.ABORTING:
			forget2PC(ref);
			return;

		case At2PCStatus.ENDED:
			return;

		}

	}

	public void readOnly(String ref) {
		switch (status) {
		case At2PCStatus.ACTIVE:
		case At2PCStatus.PREPARING_VOLATILE:
		case At2PCStatus.PREPARING_DURABLE:
			forget2PC(ref);
			return;

		case At2PCStatus.COMMITTING:
			throw new IllegalStateException();

		case At2PCStatus.ABORTING:
			forget2PC(ref);
			return;

		case At2PCStatus.ENDED:
			return;

		}
	}

	public void replay(String ref) {
		switch (status) {
		case At2PCStatus.ACTIVE:
			rollback();
			return;

		case At2PCStatus.PREPARING_VOLATILE:
		case At2PCStatus.PREPARING_DURABLE:
			status = At2PCStatus.ABORTING;
			return;

		case At2PCStatus.COMMITTING:
			EndpointReference epr = get2PCEndpointReference(ref);
			try {
				new ParticipantPort(epr).commitOperation();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;

		case At2PCStatus.ABORTING:
			epr = get2PCEndpointReference(ref);
			try {
				new ParticipantPort(epr).rollbackOperation();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			forget2PC(ref);
			return;

		case At2PCStatus.ENDED:
			return;

		}
	}

	public void prepared(String ref) {
		switch (status) {
		case At2PCStatus.ACTIVE:
			rollback();
			throw new IllegalStateException();

		case At2PCStatus.PREPARING_VOLATILE:
		case At2PCStatus.PREPARING_DURABLE:
			prepared.put(ref, ref);
			return;

		case At2PCStatus.COMMITTING:
			EndpointReference epr = get2PCEndpointReference(ref);
			try {
				new ParticipantPort(epr).commitOperation();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;

		case At2PCStatus.ABORTING:
			epr = get2PCEndpointReference(ref);
			try {
				new ParticipantPort(epr).rollbackOperation();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			forget2PC(ref);
			return;

		case At2PCStatus.ENDED:
			return;

		}
	}

	public void committed(String ref) {
		switch (status) {
		case At2PCStatus.ACTIVE:
			rollback();
			throw new IllegalStateException();

		case At2PCStatus.PREPARING_VOLATILE:
		case At2PCStatus.PREPARING_DURABLE:
			status = At2PCStatus.ABORTING;
			throw new IllegalStateException();

		case At2PCStatus.COMMITTING:
			forget2PC(ref);
			return;

		case At2PCStatus.ABORTING:
			throw new IllegalStateException();

		case At2PCStatus.ENDED:
			return;

		}

	}

	private EndpointReference get2PCEndpointReference(String ref) {
		EndpointReference epr = (EndpointReference) participants[VOLATILE_2PC]
				.get(ref);
		if (epr == null)
			epr = (EndpointReference) participants[DURABLE_2PC].get(ref);
		return epr;
	}

	private boolean prepare(int prot) {
		int iters = 0;
		while (iters < MAX_ITERS) {
			if (iters > 0)
				pause(ITER_DELAY - RESPONSE_DELAY);

			synchronized (participants[prot]) {
				Iterator iter = participants[prot].values().iterator();
				while (iter.hasNext()) {
					if (status == At2PCStatus.ABORTING)
						return false;
					try {
						new ParticipantPort((EndpointReference) iter.next())
								.prepareOperation();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

			pause(RESPONSE_DELAY);

			boolean allPrepared = true;
			synchronized (participants[prot]) {
				Iterator iter = participants[prot].keySet().iterator();
				while (iter.hasNext())
					if (!prepared.containsKey(iter.next())) {
						allPrepared = false;
						break;
					}
			}

			if (allPrepared)
				break;

			iters++;
		}

		return iters < MAX_ITERS;
	}

	private boolean prepare() {
		prepared.clear();
		status = At2PCStatus.PREPARING_VOLATILE;
		if (!prepare(VOLATILE_2PC))
			return false;

		prepared.clear();
		status = At2PCStatus.PREPARING_DURABLE;
		return prepare(DURABLE_2PC);
	}

	public void commit() {
		switch (status) {
		case At2PCStatus.ACTIVE:
			break;

		case At2PCStatus.PREPARING_VOLATILE:
		case At2PCStatus.PREPARING_DURABLE:
		case At2PCStatus.COMMITTING:
		case At2PCStatus.ABORTING:
		case At2PCStatus.ENDED:
			return;
		}

		if (!prepare()) {
			rollback();
			return;
		}

		status = At2PCStatus.COMMITTING;
		terminate();
	}

	private void pause(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void terminate() {
		int iters = 0;
		while (iters < MAX_ITERS
				&& !(participants[VOLATILE_2PC].isEmpty() && participants[DURABLE_2PC]
						.isEmpty())) {
			if (iters > 0)
				pause(ITER_DELAY - RESPONSE_DELAY);
			iters++;

			for (int prot = VOLATILE_2PC; prot == VOLATILE_2PC
					|| prot == DURABLE_2PC; prot = prot == VOLATILE_2PC ? DURABLE_2PC
					: -1) {
				synchronized (participants[prot]) {
					Iterator iter = participants[prot].values().iterator();
					while (iter.hasNext())
						try {
							ParticipantPort p = new ParticipantPort(
									(EndpointReference) iter.next());
							if (status == At2PCStatus.ABORTING)
								p.rollbackOperation();
							else
								p.commitOperation();
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
			}

			pause(RESPONSE_DELAY);
		}

		if (participants[VOLATILE_2PC].isEmpty()
				&& participants[DURABLE_2PC].isEmpty()) {
			synchronized (participants[COMPLETION]) {
				Iterator iter = participants[COMPLETION].values().iterator();
				while (iter.hasNext())
					try {
						CompletionInitiatorPort p = new CompletionInitiatorPort(
								(EndpointReference) iter.next());
						if (status == At2PCStatus.ABORTING)
							p.abortedOperation();
						else
							p.committedOperation();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
		}

		status = At2PCStatus.ENDED;
	}
}