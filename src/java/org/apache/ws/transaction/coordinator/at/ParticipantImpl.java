/*
 * Created on Dec 25, 2005
 *
 */
package org.apache.ws.transaction.coordinator.at;

import java.rmi.RemoteException;

import org.apache.ws.transaction.utility.CallbackRegistry;
import org.apache.ws.transaction.wsat.Notification;
import org.apache.ws.transaction.wsat.ParticipantPortType;

/**
 * @author Dasarath Weeratunge
 *
 */
public class ParticipantImpl implements ParticipantPortType {

	public void prepareOperation(Notification parameters)
			throws RemoteException {
		ParticipantPortType callback = (ParticipantPortType) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		if (callback != null)
			callback.prepareOperation(parameters);
	}

	public void commitOperation(Notification parameters) throws RemoteException {
		ParticipantPortType callback = (ParticipantPortType) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		if (callback != null)
			callback.commitOperation(parameters);
	}

	public void rollbackOperation(Notification parameters)
			throws RemoteException {
		ParticipantPortType callback = (ParticipantPortType) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		if (callback != null)
			callback.rollbackOperation(parameters);
	}

}
