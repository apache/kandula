/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.kandula.coordinator;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.kandula.wscoor.RegistrationPortTypeRPC;

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

	AxisFault INVALID_PARAMETERS_SOAP_FAULT = new AxisFault(
			new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor",
					"InvalidParameters"),
			"The message contained invalid parameters and could not be processed.",
			null, null);

	CoordinationContext getCoordinationContext();

}