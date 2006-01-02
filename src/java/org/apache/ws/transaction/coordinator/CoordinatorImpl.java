/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.ws.transaction.coordinator;

import java.rmi.RemoteException;

import org.apache.axis.AxisFault;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.ws.transaction.wscoor.RegisterResponseType;
import org.apache.ws.transaction.wscoor.RegisterType;

/**
 * @author Dasarath Weeratunge
 *  
 */
public abstract class CoordinatorImpl implements Coordinator {
	private String id;

	private CoordinationContext ctx;

	public CoordinatorImpl(String coordinationType)
			throws MalformedURIException {
		UUIDGen gen = UUIDGenFactory.getUUIDGen();
		id = "uuid:" + gen.nextUUID();
		CoordinationService cs = CoordinationService.getInstance();
		EndpointReference epr = cs.getRegistrationService(this);
		ctx = new CoordinationContext(id, coordinationType, epr);
	}

	public String getID() {
		return id;
	}

	public CoordinationContext getCoordinationContext() {
		return ctx;
	}

	public abstract EndpointReference register(String prot,
			EndpointReference pps) throws AxisFault;

	public synchronized RegisterResponseType registerOperation(
			RegisterType params) throws RemoteException {
		EndpointReference epr = null;
		epr = register(params.getProtocolIdentifier().toString(),
			new EndpointReference(params.getParticipantProtocolService()));
		RegisterResponseType r = new RegisterResponseType();
		r.setCoordinatorProtocolService(epr);
		return r;
	}

}