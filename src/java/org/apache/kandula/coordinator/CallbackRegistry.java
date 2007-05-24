/*
 * Created on Dec 24, 2005
 *
 */
package org.apache.kandula.coordinator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.AddressingHeaders;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class CallbackRegistry {

	public static final QName CALLBACK_REF = new QName(
			"http://ws.apache.org/kandula", "CallbackRef"
	);

	public static final QName CALLBACK_REF_PARTICIPANT = new QName(
			"http://ws.apache.org/kandula", "ParticipantRef"
	);

	// FIXME _ALL_ Make default callback timeout configurable
	public static final int DEFAULT_TIMEOUT_MILLIS = 20 * 1000;

	private static Timer timer = new Timer();

	private static final CallbackRegistry instance = new CallbackRegistry();

	/**
	 * This map maintains the CallbackID-->Callback association.
	 */
	private Map callbacks = new HashMap();
	
	/**
	 * This map maintains the CallbackID-->Timertask association.
	 *
	 */
	private Map timertasks = new HashMap();

	
	protected CallbackRegistry() {
		// Empty constructor
	}

	public static CallbackRegistry getInstance() {
		return instance;
	}

	public synchronized void registerCallback(Callback callback) {
		this.callbacks.put(callback.getID(), callback);
	}

	public synchronized void registerCallback(
			final Callback callback,
			final long timeout
	) {
		final String callbackID = callback.getID();
		
		final long myTimeout; 
		if (timeout == 0)
			myTimeout = DEFAULT_TIMEOUT_MILLIS;
		else
			myTimeout = timeout;

		System.out.println("[CallbackRegistry] registerCallback: timeout= "
				+ timeout + " callbacks.size= " + callbacks.size());

		final TimerTask task = new TimerTask() {

			public void run() {
				final Object removed = CallbackRegistry.this.callbacks.remove(callbackID);
				try {
					// If nothing was removed, don't invoke any event handlers 
					if (removed == null)
						System.out.println("CallbackRegistry: nothing found to remove for callback "+callbackID+" after timeout");
					else
						((Callback) removed).timeout();
						
				} catch (TimedOutException e) {
					//	e.printStackTrace();
				}
			}
		};
		timer.schedule(task, myTimeout);
		
		this.callbacks.put (callbackID, callback);
		this.timertasks.put(callbackID, task);
	}

	public synchronized Callback correlateMessage(final QName q, final boolean terminal) {
		String s = getRef(q);
		if (s == null)
			return null;

		return (Callback) this.callbacks.get(s);
	}

	/**
	 * Fetch an addressing header's value.
	 * @param q The QName of the wanted header.
	 * @return The header's value.
	 */
	public static String getRef(final QName q) {
		AddressingHeaders headers = org.apache.kandula.utils.AddressingHeaders.getAddressingHeadersOfCurrentMessage();
		try {
			return headers.getReferenceProperties().get(q).getValue();
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Remove a callback and cancel the associated timetask.
	 * 
	 * @param callback The callback to remove.
	 */
	public synchronized void remove(final Callback callback) {
		System.out.println("CallbackRegistry removed "+callback);

		final TimerTask task = (TimerTask) this.timertasks.remove(callback.getID());
		this.callbacks.remove(callback.getID());

		if (task != null)
			task.cancel();
	}
}
