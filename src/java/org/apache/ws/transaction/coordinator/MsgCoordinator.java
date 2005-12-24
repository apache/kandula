/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.ws.transaction.coordinator;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.ws.transaction.wscoor._CoordinationContext;

/**
 * @author Dasarath Weeratunge
 *  
 */
public interface MsgCoordinator {

	String getID();

	_CoordinationContext getCoordinationContext();

	void register(String prot, EndpointReference pps, EndpointReference res)
			throws InvalidCoordinationProtocolException;

	String getCoordinationType();

	CoordinationService getCoordinationService();

}