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

package org.apache.kandula.demo.ba.holiday.room;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.soap.Name;

import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.coordinator.TimedOutException;
import org.apache.kandula.coordinator.ba.State;
import org.apache.kandula.coordinator.ba.participant.BAwCCParticipant;
import org.apache.kandula.coordinator.ba.participant.ParticipantCancelResult;
import org.apache.kandula.coordinator.ba.participant.ParticipantCloseResult;
import org.apache.kandula.coordinator.ba.participant.ParticipantCompensateResult;
import org.apache.kandula.coordinator.ba.participant.ParticipantCompleteResult;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class RentARoomParticipant extends BAwCCParticipant {

	/**
	 * The next booking number.
	 */
	private static long nextBookingNumer = 9999;

	private final RentARoomService myService;
	
	private final RentARoomBookingRequest myRequest;
	
	private final RoomOfferType myOffer;
	
	/**
	 * The booking number for this participant.
	 */
	private final long myBookingNumber;

	private final RentARoomBookingResponse myResponse;
	
	/**
	 * A timer that Exits the participant if its compensate time times out
	 * while active.
	 */
	private final Timer timer = new Timer();

	/**
	 * @param coordinationContext
	 * @throws RemoteException
	 */
	public RentARoomParticipant(RentARoomService pService, RentARoomBookingRequest request, RoomOfferType offer)
			throws RemoteException {
		super(new CoordinationContext( request.getTransactionalContext().getCoordinationContext()));
		
		final Calendar compensateUntil = Calendar.getInstance();
		compensateUntil.add(Calendar.SECOND, 90);
				
		this.myService = pService;
		this.myRequest = request;
		this.myOffer = offer;
		
		this.myBookingNumber = nextBookingNumer++;
		
		if (myOffer.getOfferExpires() != null 
				&& Calendar.getInstance().after(myOffer.getOfferExpires())) {
			this.myResponse = new RentARoomBookingResponse(
					"RoomTEST-"+getBookingNumber(), 
					"TEST booking of Room NOK", 
					Calendar.getInstance(), 
					"Offer expired"
			);
			this.tellExit();
		} else {
			this.myResponse = new RentARoomBookingResponse(
					"RoomTEST-"+getBookingNumber(), 
					"TEST booking of Room OK", 
					compensateUntil, 
					"Active"
			);
			
			final TimerTask exitIfNotCompleted = new TimerTask(){
				public void run() {
					final RentARoomParticipant me = RentARoomParticipant.this;
					System.out.println("Room "+me.myBookingNumber+": CompensateUntil reached while ACTIVE, exiting...");
					if (State.STATE_ACTIVE.equals( me.getState() )){
						me.tellExit();
					}
				}
			};
			this.timer.schedule(exitIfNotCompleted, compensateUntil.getTime());
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.kandula.coordinator.ba.participant.BAwCCParticipant#onComplete()
	 */
	protected ParticipantCompleteResult onComplete() {
		if (this.myOffer.getOfferExpires() != null 
				&& Calendar.getInstance().after(this.myOffer.getOfferExpires())) {
			System.out.println("*** rentaroom booking "+getBookingNumber()+" tells exit because offer expired");

			this.tellExit();
			return ParticipantCompleteResult.HANDLED_BY_APPLICATION;
		} else {
			System.out.println("*** rentaroom booking "+getBookingNumber()+" completed");

			return ParticipantCompleteResult.COMPLETED;
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.kandula.coordinator.ba.participant.AbstractParticipant#onCancel()
	 */
	protected ParticipantCancelResult onCancel() {
		System.out.println("*** rentaroom booking "+getBookingNumber()+" canceled");

		return ParticipantCancelResult.CANCELED;
	}

	/* (non-Javadoc)
	 * @see org.apache.kandula.coordinator.ba.participant.AbstractParticipant#onClose()
	 */
	protected ParticipantCloseResult onClose() {
		System.out.println("*** rentaroom booking "+getBookingNumber()+" closed");

		return ParticipantCloseResult.CLOSED;
	}

	/* (non-Javadoc)
	 * @see org.apache.kandula.coordinator.ba.participant.AbstractParticipant#onCompensate()
	 */
	protected ParticipantCompensateResult onCompensate() {
		// Check if the booking may be compensated
		if (this.myResponse.getCompensateUntil()==null
				|| Calendar.getInstance().before(this.myResponse.getCompensateUntil())
		){
			// YES!
			System.out.println("*** rentaroom booking "+this.myBookingNumber+" compensated");

			return ParticipantCompensateResult.COMPENSATED;
		}
		// NO!
		System.out.println("*** rentaroom booking "+this.myBookingNumber+" NOT compensated, too late! Faulting...");

		// Sorry, its too late -- manual intervention required.
		return ParticipantCompensateResult.FAULTED;
	}

	/* (non-Javadoc)
	 * @see org.apache.kandula.coordinator.ba.participant.AbstractParticipant#onFinish()
	 */
	protected void onFinish() {
		System.out.println("*** rentaroom booking "+getBookingNumber()+" finished, disposing");

	}

	/* (non-Javadoc)
	 * @see org.apache.kandula.coordinator.Callback#onFault(javax.xml.soap.Name)
	 */
	public void onFault(Name code) {
		System.out.println("*** rentaroom booking "+getBookingNumber()+" Fault: "+code);
	}

	/* (non-Javadoc)
	 * @see org.apache.kandula.coordinator.Callback#timeout()
	 */
	public void timeout() throws TimedOutException {
		System.out.println("*** rentaroom booking "+this.myBookingNumber+" Timeout!");
	}

	/**
	 * Return the booking response object so the calling web service 
	 * may return it to the customer. At this point, the constructor will
	 * already have registered the participant with the coordination context
	 * and reported Completed.
	 * @return The booking response
	 */
	public RentARoomBookingResponse getBookingResponse() {

		// Access the field so it is read at least one time...
		if (this.myRequest == null)
			throw new NullPointerException();
		
		return this.myResponse;
	}

	/**
	 * The current booking number.
	 * @return ..
	 */
	public long getBookingNumber(){
		return this.myBookingNumber;
	}
}
