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
			"http://ws.apache.org/kandula", "CallbackRef");

	// FIXME:
	public static final int DEFAULT_TIMEOUT_MILLIS = 20 * 1000;

	private static Timer timer = new Timer();

	private static final CallbackRegistry instance = new CallbackRegistry();

	private Map callbacks = new HashMap();

	protected CallbackRegistry() {
	}

	public static CallbackRegistry getInstance() {
		return instance;
	}

	public synchronized void registerCallback(Callback callback) {
		callbacks.put(callback.getID(), callback);
	}

	public synchronized void registerCallback(final Callback callback,
			long timeout) {
		callbacks.put(callback.getID(), callback);

		if (timeout == 0)
			timeout = DEFAULT_TIMEOUT_MILLIS;

		System.out.println("[CallbackRegistry] registerCallback: timeout= "
				+ timeout + " callbacks.size= " + callbacks.size());

		timer.schedule(new TimerTask() {
			public void run() {
				callbacks.remove(callback.getID());
				try {
					callback.timeout();
				} catch (TimedOutException e) {
					//	e.printStackTrace();
				}
			}
		}, timeout);
	}

	public synchronized Callback correlateMessage(QName q, boolean terminal) {
		String s = getRef(q);
		if (s == null)
			return null;
		else
			return (Callback) callbacks.get(s);
	}

	private String getRef(QName q) {
		AddressingHeaders headers = org.apache.kandula.utils.AddressingHeaders.getAddressingHeadersOfCurrentMessage();
		try {
			return headers.getReferenceProperties().get(q).getValue();
		} catch (NullPointerException e) {
			return null;
		}
	}

	public synchronized void remove(Callback callback) {
		callbacks.remove(callback.getID());
	}
}