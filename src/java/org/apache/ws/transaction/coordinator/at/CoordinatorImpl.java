/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.ws.transaction.coordinator.at;

import java.rmi.RemoteException;

import org.apache.ws.transaction.coordinator.CallbackRegistry;
import org.apache.ws.transaction.wsat.CoordinatorPortType;
import org.apache.ws.transaction.wsat.Notification;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class CoordinatorImpl implements CoordinatorPortType {

	public void preparedOperation(Notification params) throws RemoteException {
		ATCoordinator c = (ATCoordinator) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		c.preparedOperation(params);
	}

	public void abortedOperation(Notification params) throws RemoteException {
		ATCoordinator c = (ATCoordinator) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		c.abortedOperation(params);
	}

	public void readOnlyOperation(Notification params) throws RemoteException {
		ATCoordinator c = (ATCoordinator) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		c.readOnlyOperation(params);
	}

	public void committedOperation(Notification params) throws RemoteException {
		ATCoordinator c = (ATCoordinator) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		c.committedOperation(params);
	}

	public void replayOperation(Notification params) throws RemoteException {
		ATCoordinator c = (ATCoordinator) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		c.replayOperation(params);
	}
}