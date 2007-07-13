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

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class LocSpec {

	private final String from, to;
	private final String location;
	
	public LocSpec(final String pFrom, final String pTo) {
		this.from = pFrom;
		this.to = pTo;
		this.location = null;
	}
	
	public LocSpec(final String pLocation) {
		this.location = pLocation;
		this.from = null;
		this.to = null;
	}
	
	public LocSpec(org.apache.kandula.demo.ba.holiday.car.LocSpec loc) {
		this.from = loc.getFrom().toString();
		this.to = loc.getTo().toString();
		this.location = null;
	}
	
	public LocSpec(org.apache.kandula.demo.ba.holiday.room.LocSpec loc) {
		this.from = null;
		this.to = null;
		this.location = loc.getLocation().toString();
	}

	/**
	 * @return Returns the from.
	 */
	public String getFrom() {
		if (from != null)
			return this.from;
		return getLocation();
	}

	/**
	 * @return Returns the location.
	 */
	public String getLocation() {
		if (location != null)
			return location;
		return getFrom();
	}

	/**
	 * @return Returns the to.
	 */
	public String getTo() {
		if (to != null)
			return to;
		return getLocation();
	}
	
	public org.apache.kandula.demo.ba.holiday.car.LocSpec getCarLocSpec() {
		return new org.apache.kandula.demo.ba.holiday.car.LocSpec(getFrom(), getTo());
	}
	
	public org.apache.kandula.demo.ba.holiday.room.LocSpec getRoomLocSpec() {
		return new org.apache.kandula.demo.ba.holiday.room.LocSpec(getLocation());
	}
}
