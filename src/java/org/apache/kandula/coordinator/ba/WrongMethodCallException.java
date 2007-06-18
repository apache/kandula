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

package org.apache.kandula.coordinator.ba;

/**
 * This exception signals that something went wrong while performing a state transition.
 * This may happen, when incorrect code queries fields on @see org.apache.kandula.coordinator.ba.AbstractStateTransition
 * which are not available in that subclass.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class WrongMethodCallException extends Exception {

	/**
	 * Generated SerialVersionUID
	 */
	private static final long serialVersionUID = -8654599203638284959L;

	/**
	 * 
	 */
	public WrongMethodCallException() {
		super();
	}

	/**
	 * @param message
	 */
	public WrongMethodCallException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public WrongMethodCallException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public WrongMethodCallException(String message, Throwable cause) {
		super(message, cause);
	}

}
