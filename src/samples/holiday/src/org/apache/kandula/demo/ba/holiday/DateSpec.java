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
package org.apache.kandula.demo.ba.holiday;

import java.util.Date;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class DateSpec {

    private final Date from, to;

    public DateSpec(Date pFrom, Date pTo) {
    	this.from = pFrom;
    	this.to = pTo;
    }
    
	public DateSpec(org.apache.kandula.demo.ba.holiday.car.DateSpec date) {
		this.from = date.getFrom();
		this.to = date.getTo();
	}
	
	public DateSpec(org.apache.kandula.demo.ba.holiday.room.DateSpec date) {
		this.from = date.getFrom();
		this.to = date.getTo();
	}

	/**
	 * @return Returns the from.
	 */
	public Date getFrom() {
		return from;
	}

	/**
	 * @return Returns the to.
	 */
	public Date getTo() {
		return to;
	}
	
	public org.apache.kandula.demo.ba.holiday.car.DateSpec getCarDateSpec() {
		return new org.apache.kandula.demo.ba.holiday.car.DateSpec(getFrom(), getTo());
	}
	
	public org.apache.kandula.demo.ba.holiday.room.DateSpec getRoomDateSpec() {
		return new org.apache.kandula.demo.ba.holiday.room.DateSpec(getFrom(), getTo());
	}
}
