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

import org.apache.kandula.demo.ba.holiday.DateSpec;
import org.apache.kandula.demo.ba.holiday.LocSpec;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public interface SearchDetailInterface {
	String getWho();
	String getWhenFrom();
	String getWhenTo();
	String getWhereFrom();
	String getWhereTo();
	String getCount();
	DateSpec getDateSpec();
	LocSpec getLocSpec();
}
