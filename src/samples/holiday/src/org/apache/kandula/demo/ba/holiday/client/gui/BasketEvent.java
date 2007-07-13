/*
 * Copyright 2007 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *  @author Hannes Erven, Georg Hicker
 */

package org.apache.kandula.demo.ba.holiday.client.gui;

import java.util.EventObject;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class BasketEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4531091733626998392L;
	private final BasketEntry[] bE;
	private final EventType type;
	/**
	 * @param source
	 */
	public BasketEvent(Object source, BasketEntry[] pBE, EventType pType) {
		super(source);
		this.bE = pBE;
		this.type = pType;
	}
	
	public EventType getType() {
		return this.type;
	}
	
	/**
	 * @return Returns the bE.
	 */
	public BasketEntry[] getBE() {
		return this.bE;
	}

	/**
	 * Enumeration for basket event types
	 */
	public static final class EventType {

		private EventType(String s) {
			this.typeString = s;
		}

		/**
		 * Cancel type.
		 */
		public static final EventType CANCEL = new EventType("CANCEL");

		/**
		 * Complete type.
		 */
		public static final EventType COMPLETE = new EventType("COMPLETE");

		/**
		 * Compensate type.
		 */
		public static final EventType COMPENSATE = new EventType("COMPENSATE");

		/**
		 * Close type.
		 */
		public static final EventType CLOSE = new EventType("CLOSE");

		/**
		 * Refresh type.
		 */
		public static final EventType REFRESH = new EventType("REFRESH");

		/**
		 * Converts the type to a string.
		 *
		 * @return the string
		 */
		public String toString() {
			return this.typeString;
		}

		private String typeString;
	}


}
