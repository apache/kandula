/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.ws.transaction.coordinator;

import org.apache.ws.transaction.wscoor.RegistrationPortTypeRPC;

/**
 * @author Dasarath Weeratunge
 *  
 */
public interface Coordinator extends RegistrationPortTypeRPC {
	String getID();

	CoordinationContext getCoordinationContext();
}