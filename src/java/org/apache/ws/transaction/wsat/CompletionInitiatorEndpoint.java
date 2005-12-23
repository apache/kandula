package org.apache.ws.transaction.wsat;

import org.apache.axis.message.addressing.PortType;

// if anyone feels that he/she needs to work with this file then
// you should be looking into using Kandula2/Axis2-- at least for now,
// we ignore these messages and assume we can do so safely

public class CompletionInitiatorEndpoint implements
		org.apache.ws.transaction.wsat.CompletionInitiatorPortType {
	public static PortType PORT_TYPE=
		new PortType("http://schemas.xmlsoap.org/ws/2004/10/wsat", "CompletionInitiatorPortType");

	public void committedOperation(
			org.apache.ws.transaction.wsat.Notification parameters)
			throws java.rmi.RemoteException {
		System.out.println("[CompletionInitiatorEndpoint] committedOperation");
	}

	public void abortedOperation(
			org.apache.ws.transaction.wsat.Notification parameters)
			throws java.rmi.RemoteException {
		System.out.println("[CompletionInitiatorEndpoint] abortedOperation");
	}
}