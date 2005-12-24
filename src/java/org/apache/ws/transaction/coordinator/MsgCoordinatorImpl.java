/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.ws.transaction.coordinator;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.ws.transaction.wscoor._CoordinationContext;

/**
 * @author Dasarath Weeratunge
 *  
 */
public abstract class MsgCoordinatorImpl implements MsgCoordinator {
	private String id;

	private _CoordinationContext ctx;

	private CoordinationService cs;

	public MsgCoordinatorImpl(CoordinationService cs) {
		UUIDGen gen = UUIDGenFactory.getUUIDGen();
		id = "uuid:" + gen.nextUUID();
		EndpointReference epr = cs.getRegistrationService();
		epr.setProperties(cs.getReferenceProperties(this, null));
		ctx = new _CoordinationContext(id, getCoordinationType(), epr);
	}

	public String getID() {
		return id;
	}

	public _CoordinationContext getCoordinationContext() {
		return ctx;
	}

	public CoordinationService getCoordinationService() {
		return cs;
	}
}