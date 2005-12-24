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
public class CoordinatorEndpoint implements CoordinatorPortType {
	public static PortType PORT_TYPE=
		new PortType("http://schemas.xmlsoap.org/ws/2004/10/wsat", "CoordinatorPortType");


	/* (non-Javadoc)
	 * @see org.apache.ws.transaction.wsat.CoordinatorPortType#preparedOperation(org.apache.ws.transaction.wsat.Notification)
	 */
	public void preparedOperation(Notification parameters)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.ws.transaction.wsat.CoordinatorPortType#abortedOperation(org.apache.ws.transaction.wsat.Notification)
	 */
	public void abortedOperation(Notification parameters)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.ws.transaction.wsat.CoordinatorPortType#readOnlyOperation(org.apache.ws.transaction.wsat.Notification)
	 */
	public void readOnlyOperation(Notification parameters)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.ws.transaction.wsat.CoordinatorPortType#committedOperation(org.apache.ws.transaction.wsat.Notification)
	 */
	public void committedOperation(Notification parameters)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.ws.transaction.wsat.CoordinatorPortType#replayOperation(org.apache.ws.transaction.wsat.Notification)
	 */
	public void replayOperation(Notification parameters) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
