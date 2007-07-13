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

import java.util.Calendar;

import javax.xml.namespace.QName;

import org.apache.kandula.wsbai.BAParticipantType;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class BasketEntry {

	private final TableOfferEntry tOE;
	private BAParticipantType participant;
	private final String matchcode;
	private final String bookingReference;
	private final Calendar compensateUntil;
	
	public BasketEntry(
			final String pMatchcode, 
			final BAParticipantType pParticipant, 
			final TableOfferEntry offer,
			final String pBookingReference,
			final Calendar pCompensateUntil
	) {
		this.tOE = offer;
		this.participant = pParticipant;
		this.matchcode = pMatchcode;
		this.bookingReference = pBookingReference;
		this.compensateUntil = pCompensateUntil;
	}

	public TableOfferEntry getTOE() {
		return this.tOE;
	}

	/**
	 * @return Returns the participant's state.
	 */
	public QName getState() {
		if (this.participant == null) {
			return new QName("");
		}
		return this.participant.getStatus().getState().getValue();
	}
	
	/**
	 * @return Returns the participant.
	 */
	public BAParticipantType getParticipant() {
		return this.participant;
	}
	
	/**
	 * @param participant The participant to set.
	 */
	public void setParticipant(BAParticipantType pParticipant) {
		this.participant = pParticipant;
	}

	/**
	 * @return Returns the matchcode.
	 */
	public String getMatchcode() {
		return this.matchcode;
	}

	/**
	 * @return Returns the bookingReference.
	 */
	public String getBookingReference() {
		return this.bookingReference;
	}
	
	/**
	 * The latest point in time at which compensate will be accepted.
	 * @return ..
	 */
	public Calendar getCompensateUntil() {
		return this.compensateUntil;
	}
}
