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
		return callbacks.get(getRef(q));
	}

	private String getRef(QName q) {
		AddressingHeaders header = (AddressingHeaders) MessageContext.getCurrentContext().getProperty(
			Constants.ENV_ADDRESSING_REQUEST_HEADERS);
		return header.getReferenceProperties().get(q).getValue();
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