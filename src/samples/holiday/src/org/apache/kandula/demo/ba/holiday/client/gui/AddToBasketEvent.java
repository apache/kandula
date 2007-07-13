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
public class AddToBasketEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7540974467608056084L;
	
	private final TableOfferEntry[] tOE;
	/**
	 * 
	 */
	public AddToBasketEvent(Object source, TableOfferEntry pTOE) {
		super(source);
		this.tOE = new TableOfferEntry[]{pTOE};
	}
	
	public AddToBasketEvent(Object source, TableOfferEntry[] pTOE) {
		super(source);
		this.tOE = pTOE;
	}
	
	/**
	 * @return Returns the tOE.
	 */
	public TableOfferEntry[] getTOE() {
		return this.tOE;
	}

}
