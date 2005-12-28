package org.apache.ws.transaction.coordinator.at;

import org.apache.ws.transaction.utility.CallbackRegistry;
import org.apache.ws.transaction.wsat.CompletionInitiatorPortType;

public class CompletionInitiatorImpl implements CompletionInitiatorPortType {
	public void committedOperation(
			org.apache.ws.transaction.wsat.Notification parameters)
			throws java.rmi.RemoteException {
		CompletionInitiatorPortType callback = (CompletionInitiatorPortType) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		if (callback != null)
			callback.committedOperation(parameters);
	}

	public void abortedOperation(
			org.apache.ws.transaction.wsat.Notification parameters)
			throws java.rmi.RemoteException {
		CompletionInitiatorPortType callback = (CompletionInitiatorPortType) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		if (callback != null)
			callback.abortedOperation(parameters);
	}
}