/*
 * Created on Dec 24, 2005
 *
 */
package org.apache.ws.transaction.utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axis.MessageContext;
import org.apache.axis.message.addressing.AddressingHeaders;
import org.apache.axis.message.addressing.Constants;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class CallbackRegistry {
	public static final QName CALLBACK_REF = new QName(
			"http://ws.apache.org/kandula", "CallbackRef");

	public static final QName COORDINATOR_REF = new QName(
			"http://ws.apache.org/kandula", "CoordinatorRef");

	private static final CallbackRegistry instance = new CallbackRegistry();

	private Map callbacks = new HashMap();

	protected CallbackRegistry() {
	}

	public static CallbackRegistry getInstance() {
		return instance;
	}

	public synchronized void registerCallback(String ref, Object callback) {
		callbacks.put(ref, callback);
	}

	public synchronized Object correlateMessage(QName q, boolean terminal) {
		String s = getRef(q);
		if (s == null)
			return null;
		else
			return callbacks.get(s);
	}

	private String getRef(QName q) {
		AddressingHeaders header = (AddressingHeaders) MessageContext.getCurrentContext().getProperty(
			Constants.ENV_ADDRESSING_REQUEST_HEADERS);
		try {
			return header.getReferenceProperties().get(q).getValue();
		} catch (NullPointerException e) {
			return null;
		}
	}

	public synchronized void remove(Object callback) {
		Set s = new HashSet(callbacks.keySet());
		Iterator iter = s.iterator();
		while (iter.hasNext()) {
			Object key = iter.next();
			if (callbacks.get(key) == callback)
				callbacks.remove(key);
		}
	}
}