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
public abstract class Coordinator implements RegistrationPortTypeRPC, Callback {

	public final static QName PARTICIPANT_REF = new QName(
			"http://ws.apache.org/kandula", "ParticipantRef"
	);

	public static AxisFault ALREADY_REGISTERED_SOAP_FAULT(){
		return  new AxisFault(
			new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor",
					"AlreadyRegistered"),
			"The participant has already registered for the same protocol.",
			null, null);
	}

	public static AxisFault INVALID_PROTOCOL_SOAP_FAULT(){ 
		return new AxisFault(new QName(
			"http://schemas.xmlsoap.org/ws/2004/10/wscoor", "InvalidProtocol"),
			"The protocol is invalid or is not supported by the coordinator.",
			null, null);
	}

	public static AxisFault INVALID_STATE_SOAP_FAULT(){
		return  new AxisFault(new QName(
			"http://schemas.xmlsoap.org/ws/2004/10/wscoor", "InvalidState"),
			"The message was invalid for the current state of the activity.",
			null, null);
	}
	public static AxisFault INVALID_STATE_SOAP_FAULT(final String faultDetail){
		final AxisFault af = new AxisFault(new QName(
			"http://schemas.xmlsoap.org/ws/2004/10/wscoor", "InvalidState"),
			"The message was invalid for the current state of the activity.",
			null, null);
		
		af.setFaultDetailString(faultDetail);
		
		return af;
	}
	
	public static AxisFault CONTEXT_REFUSED_SOAP_FAULT(){ 
		return new AxisFault(new QName(
			"http://schemas.xmlsoap.org/ws/2004/10/wscoor", "ContextRefused"),
			"The coordination context that was provided could not be accepted.",
			null, null);
	}
	
	public static AxisFault INVALID_PARAMETERS_SOAP_FAULT(){
		return new AxisFault(
			new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor",
					"InvalidParameters"),
			"The message contained invalid parameters and could not be processed.",
			null, null);
	}
	public static AxisFault INVALID_PARAMETERS_SOAP_FAULT(final String faultDetail){
		final AxisFault af = new AxisFault(
			new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor",
					"InvalidParameters"),
			"The message contained invalid parameters and could not be processed.",
			null, null);
		
		af.setFaultDetailString(faultDetail);
		
		return af;
	}
	
	abstract CoordinationContext getCoordinationContext();
}
