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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.apache.kandula.demo.ba.holiday.DateSpec;
import org.apache.kandula.demo.ba.holiday.LocSpec;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public abstract class TableOfferEntry {
	private final String bookingReference;
	private final String specification;
	private final Date when_from, when_to;
	private final String where_from, where_to;
	private final BigDecimal price;
	private final String[] payments;
	private final Calendar expires;
	
	public TableOfferEntry(final String pBookingReference,
			final String pSpecification, final String where,
			final DateSpec dateSpec, final BigDecimal pPrice,
			final String[] pPayments,final Calendar pExpires
	) {
		this.bookingReference = pBookingReference;
		this.specification = pSpecification;
		this.when_from = dateSpec.getFrom();
		this.when_to = dateSpec.getTo();
		this.where_from = where;
		this.where_to = "";
		this.price = pPrice;
		this.payments = pPayments;
		this.expires = pExpires;
	}

	public TableOfferEntry(final String pBookingReference,
			final String pSpecification, final LocSpec locSpec,
			final DateSpec dateSpec, final BigDecimal pPrice,
			final String[] pPayments, final Calendar pExpires
	) {
		this.bookingReference = pBookingReference;
		this.specification = pSpecification;
		this.when_from = dateSpec.getFrom();
		this.when_to = dateSpec.getTo();
		this.where_from = locSpec.getFrom();
		this.where_to = locSpec.getTo();
		this.price = pPrice;
		this.payments = pPayments;
		this.expires = pExpires;
	}

	/**
	 * @return Returns the bookingReference.
	 */
	public String getBookingReference() {
		return this.bookingReference;
	}

	/**
	 * @return Returns the when_from.
	 */
	public Date getWhen_from() {
		return this.when_from;
	}

	/**
	 * @return Returns the payments.
	 */
	public String[] getPayments() {
		return this.payments;
	}

	/**
	 * @return Returns the specification.
	 */
	public String getSpecification() {
		return this.specification;
	}

	/**
	 * @return Returns the when_to.
	 */
	public Date getWhen_to() {
		return this.when_to;
	}

	/**
	 * @return Returns the where_from.
	 */
	public String getWhere_from() {
		return where_from;
	}

	/**
	 * @return Returns the where_to.
	 */
	public String getWhere_to() {
		return where_to;
	}

	public Calendar getExpires(){
		return this.expires;
	}

	/**
	 * THis offer's price.
	 * @return The price.
	 */
	public BigDecimal getPrice() {
		return this.price;
	}
}
