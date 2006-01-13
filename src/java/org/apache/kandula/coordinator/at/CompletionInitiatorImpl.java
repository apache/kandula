package org.apache.kandula.coordinator.at;

import org.apache.kandula.coordinator.CallbackRegistry;
import org.apache.kandula.wsat.CompletionInitiatorPortType;

public class CompletionInitiatorImpl implements CompletionInitiatorPortType {
	public void committedOperation(
			org.apache.kandula.wsat.Notification parameters)
			throws java.rmi.RemoteException {
		CompletionInitiatorPortType callback = (CompletionInitiatorPortType) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		if (callback != null)
			callback.committedOperation(parameters);
	}

	public void abortedOperation(
			org.apache.kandula.wsat.Notification parameters)
			throws java.rmi.RemoteException {
		CompletionInitiatorPortType callback = (CompletionInitiatorPortType) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		if (callback != null)
			callback.abortedOperation(parameters);
	}
}