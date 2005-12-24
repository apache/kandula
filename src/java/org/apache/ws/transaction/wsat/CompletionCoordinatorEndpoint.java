/*
 * Created on Dec 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.ws.transaction.wsat;

import java.rmi.RemoteException;

import org.apache.axis.message.addressing.PortType;

/**
 * @author Dasarath Weeratunge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CompletionCoordinatorEndpoint implements
		CompletionCoordinatorPortType {
	public static PortType PORT_TYPE=
		new PortType("http://schemas.xmlsoap.org/ws/2004/10/wsat", "CompletionCoordinatorPortType");


	/* (non-Javadoc)
	 * @see org.apache.ws.transaction.wsat.CompletionCoordinatorPortType#commitOperation(org.apache.ws.transaction.wsat.Notification)
	 */
	public void commitOperation(Notification parameters) throws RemoteException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.ws.transaction.wsat.CompletionCoordinatorPortType#rollbackOperation(org.apache.ws.transaction.wsat.Notification)
	 */
	public void rollbackOperation(Notification parameters)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

}
