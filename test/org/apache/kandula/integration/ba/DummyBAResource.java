/*
 * Copyright  2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.kandula.integration.ba;

import org.apache.kandula.Constants;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.participant.ba.KandulaBusinessActivityResource;

public class DummyBAResource extends KandulaBusinessActivityResource {

	/**
	 * 
	 */
	public DummyBAResource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getProtocol() {
		return Constants.WS_BA_CC;
	}

	public boolean compensate() {
		System.out.println("Compensated");
		return true;
	}

	public void complete() throws  AbstractKandulaException{
		System.out.println("Complete");
		return;
	}

	public void close() {
		System.out.println("Close");
	}

	public void cancel() {
		System.out.println("Cancel");
	}

}