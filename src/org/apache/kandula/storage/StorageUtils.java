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
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;

public class StorageUtils {
	public static AbstractContext getContext(String id) {
		Store store = getStore(MessageContext.getCurrentMessageContext().getServiceContext()
				.getConfigurationContext());
		return (AbstractContext) store.get(id);
	}

	public static void putContext(AbstractContext context) {
		Store store = getStore(MessageContext.getCurrentMessageContext().getServiceContext()
				.getConfigurationContext());
		store.put(context.getCoordinationContext().getActivityID(), context);
	}

	public static void putContext(AbstractContext context, String id, MessageContext messageContext) {
		Store store = getStore(messageContext.getServiceContext().getConfigurationContext());
		store.put(id, context);
	}

	public static Store getStore(ConfigurationContext configurationContext) {
		if (configurationContext == null)
			return null;
		Store store;
		Object storeObect = configurationContext.getProperty(Constants.KANDULA_STORE);
		if (storeObect == null) {
			store = new SimpleStore();
			configurationContext.setProperty(Constants.KANDULA_STORE, store);
		} else {
			store = (Store) storeObect;
		}
		return store;
	}
}
