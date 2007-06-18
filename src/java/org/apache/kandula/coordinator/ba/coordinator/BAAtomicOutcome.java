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

package org.apache.kandula.coordinator.ba.coordinator;

import javax.xml.namespace.QName;

/**
 * This class is a container for atomic business activity outcomes.
 * 
 * After instantiating it, the result is undecided. At any given point,
 * the BACoordinator may call either @see #decideToClose() or @see #decideToCancelOrCompensate() ,
 * which sets the final outcome.
 * After that, no more methods may be called.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class BAAtomicOutcome {
	/**
	 * The result QName for coordination contexts that are not yet decided.
	 */
	public static final QName RESULT_UNDECIDED = new QName(BACoordinator.PROTOCOL_ID_INITIATOR, "AtomicOutcome_Undecided");
	
	/**
	 * The result QName for coordination contexts that are decided to close.
	 */
	public static final QName RESULT_CLOSE = new QName(BACoordinator.PROTOCOL_ID_INITIATOR, "AtomicOutcome_Close");
	
	/**
	 * The result QName for coordination contexts that are decided to cancel or compensate.
	 */
	public static final QName RESULT_COMPENSATE = new QName(BACoordinator.PROTOCOL_ID_INITIATOR, "AtomicOutcome_Compensate");


	/**
	 * The private field where the result qname is stored.
	 */
	private QName myOutcome = RESULT_UNDECIDED;
	
	/**
	 * Set the final outcome to CLOSE.
	 * If isDecided() is already true, this method will throw an IllegalStateException. 
	 *
	 */
	public void decideToClose(){
		synchronized(this){
			if (isDecided()){
				throw new IllegalStateException("Sorry, this outcome has already been decided: "+this.myOutcome);
			}

			this.myOutcome = RESULT_CLOSE;
		}
	}

	/**
	 * Set the final outcome to CANCEL or COMPENSATE.
	 * If isDecided() is already true, this method will throw an IllegalStateException. 
	 *
	 */
	public void decideToCancelOrCompensate(){
		synchronized(this){
			if (isDecided()){
				throw new IllegalStateException("Sorry, this outcome has already been decided: "+this.myOutcome);
			}
			this.myOutcome = RESULT_COMPENSATE;
		}
	}

	/**
	 * Query if the outcome has alredy been decided.
	 * @return true, if the outcome has already been decided; false, if not.
	 *
	 */
	public boolean isDecided(){
		if (RESULT_UNDECIDED.equals( this.myOutcome )){
			return false;
		}
		
		return true;
	}

	/**
	 * Query if the outcome has alredy been decided to successfully close the transaction. 
	 * @return true, if the outcome has already been decided to close; false, if not.
	 *
	 */
	public boolean isDecidedToClose(){
		return RESULT_CLOSE.equals( this.myOutcome );
	}

	/**
	 * Query if the outcome has alredy been decided to roll back (cancel or compensate) the transaction. 
	 * @return true, if the outcome has already been decided to cancel or compensate; false, if not.
	 *
	 */
	public boolean isDecidedToCancelOrCompensate(){
		return RESULT_COMPENSATE.equals( this.myOutcome );
	}

	/**
	 * Query the outcome decision.
	 * @return null, if undecided; or one of @see #RESULT_CLOSE and @see #RESULT_COMPENSATE , if
	 * already decided.
	 *
	 */
	public QName getOutcomeDecision(){
		if (this.isDecided())
			return this.myOutcome;
			
		return null;
	}

	/**
	 * Query the current outcome.
	 * @return @see #RESULT_UNDECIDED if undecided, or one of @see #RESULT_CLOSE and @see #RESULT_COMPENSATE , if
	 * already decided.
	 *
	 */
	public QName getOutcome(){
		return this.myOutcome;
	}
}
