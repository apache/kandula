/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.ws.transaction.coordinator;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.ws.transaction.utility.Callback;
import org.apache.ws.transaction.wscoor.RegistrationPortTypeRPC;

/**
 * @author Dasarath Weeratunge
 *  
 */
public interface Coordinator extends RegistrationPortTypeRPC, Callback {

	AxisFault ALREADY_REGISTERED_SOAP_FAULT = new AxisFault(
			new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor",
					"AlreadyRegistered"),
			"The participant has already registered for the same protocol.",
			null, null);

	AxisFault INVALID_PROTOCOL_SOAP_FAULT = new AxisFault(new QName(
			"http://schemas.xmlsoap.org/ws/2004/10/wscoor", "InvalidProtocol"),
			"The protocol is invalid or is not supported by the coordinator.",
			null, null);

	AxisFault INVALID_STATE_SOAP_FAULT = new AxisFault(new QName(
			"http://schemas.xmlsoap.org/ws/2004/10/wscoor", "InvalidState"),
			"The message was invalid for the current state of the activity.",
			null, null);

	String getID();

	CoordinationContext getCoordinationContext();

}