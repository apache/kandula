/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package org.apache.kandula.storage;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.coordinator.Coordinator;

public class StorageUtils {

	private static final Log log = LogFactory.getLog(StorageUtils.class);

	public static AbstractContext getContext(MessageContext messageContext, String id) {
		log.info("Kandula : Retrieved Context " + id + " from the store.");
		Store store = getStore(messageContext.getServiceContext().getConfigurationContext());
		return (AbstractContext) store.get(id);
	}

	public static AbstractContext getContext(String id) {
		log.info("Kandula : Retrieved Context " + id + " from the store.");
		Store store = getStore(MessageContext.getCurrentMessageContext().getServiceContext()
				.getConfigurationContext());
		return (AbstractContext) store.get(id);
	}

	public static void putContext(AbstractContext context) {
		log.info("Kandula : Stored Context " + context.getCoordinationContext().getActivityID()
				+ " on the store.");
		Store store = getStore(MessageContext.getCurrentMessageContext().getServiceContext()
				.getConfigurationContext());
		store.put(context.getCoordinationContext().getActivityID(), context);
	}

	public static void putContext(AbstractContext context, String id, MessageContext messageContext) {
		log.info("Kandula : Stored Context " + context.getCoordinationContext().getActivityID()
				+ " on the store.");
		Store store = getStore(messageContext.getServiceContext().getConfigurationContext());
		store.put(id, context);
	}

	public static void forgetContext(String id) {
		log.info("Kandula : Forgot Context " + id	+ " from the store.");
		Store store = getStore(MessageContext.getCurrentMessageContext().getServiceContext()
				.getConfigurationContext());
		store.forget(id);
	}

	public static Store getStore(ConfigurationContext configurationContext) {
		if (configurationContext == null)
			return null;
		Store store;
		Object storeObect = configurationContext.getProperty(Constants.KANDULA_STORE);
		if (storeObect == null) {
			store = Coordinator.store;
			// store = new SimpleStore();
			configurationContext.setProperty(Constants.KANDULA_STORE, store);
		} else {
			store = (Store) storeObect;
		}
		return store;
	}
}
