/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.ws.transaction.coordinator.at;

import java.rmi.RemoteException;

import org.apache.ws.transaction.coordinator.CallbackRegistry;
import org.apache.ws.transaction.wsat.CompletionCoordinatorPortType;
import org.apache.ws.transaction.wsat.Notification;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class CompletionCoordinatorImpl implements CompletionCoordinatorPortType {

	public void commitOperation(Notification params) throws RemoteException {
		ATCoordinator c = (ATCoordinator) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		c.commitOperation(params);
	}

	public void rollbackOperation(Notification params) throws RemoteException {
		ATCoordinator c = (ATCoordinator) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		c.rollbackOperation(params);
	}

}