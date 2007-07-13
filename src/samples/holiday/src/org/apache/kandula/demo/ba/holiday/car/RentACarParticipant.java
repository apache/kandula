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
package org.apache.kandula.demo.ba.holiday.car;

import java.rmi.RemoteException;
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;

import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.coordinator.ba.participant.BAwPCParticipant;
import org.apache.kandula.coordinator.ba.participant.ParticipantCancelResult;
import org.apache.kandula.coordinator.ba.participant.ParticipantCloseResult;
import org.apache.kandula.coordinator.ba.participant.ParticipantCompensateResult;

/**
 * This class models a Rent-A-Car WS-BA participant. The participant is instantiated
 * when a customer issues a booking request. The participant first registers itself 
 * with the given coordination context and then books the wanted car, if possible.
 * 
 * If the booking could not be carried out, the participant Exits from the business activity.
 * 
 * If the booking was successfully completed, the participant reports Completed to the
 * transaction and waits for further orders.
 * 
 * Note that this class extends the BAwPCParticipant class, which is the hook into the
 * kandula client web services. The RentACarParticipant's onClose/onCompensate/onFault/onCancel methods
 * are automatically invoked when the cooresponding events occur. 
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class RentACarParticipant extends BAwPCParticipant {

	/**
	 * The next booking number.
	 */
	private static long nextBookingNumer = 1007;

	/**
	 * The request for the booking.
	 */
	private final RentACarBookingRequest myRequest;
	
	/**
	 * The booking response.
	 */
	private final RentACarBookingResponse myResponse;
	
	/**
	 * The booking number for this participant.
	 */
	private final long myBookingNumber;

	/**
	 * My car offer.
	 */
	private final CarOfferType myCarOffer;
	
	/**
	 * The service that manages this participant.
	 */
	private final RentACarService service;
	
	/**
	 * Default constructor for the RentACarParticipant. Takes a booking request,
	 * registers itself with the coordination context, completes the booking
	 * and reports completed.
	 * If any of these steps fail, the corresponding RemoteException is thrown.
	 * @param theService The managing service.
	 *  
	 * @param params The booking request.
	 * @throws RemoteException Any error that occurs.
	 */
	public RentACarParticipant(
			final RentACarService theService,
			final RentACarBookingRequest params,
			final CarOfferType carOffer
	) throws RemoteException {
		super(new CoordinationContext( params.getTransactionalContext().getCoordinationContext() ));

		final Calendar compensateUntil = Calendar.getInstance();
		compensateUntil.add(Calendar.SECOND, 90);
		
		this.service = theService;
		this.myBookingNumber = nextBookingNumer++;
		this.myCarOffer = carOffer;

		this.myRequest = params;

		
		if (carOffer.getOfferExpires() != null
				&& Calendar.getInstance().after(carOffer.getOfferExpires())
		){
			// The offer had expired
			this.myResponse = new RentACarBookingResponse(
					"TEST-"+this.myBookingNumber, 
					"TEST booking NOK", 
					Calendar.getInstance(), 
					"Offer expired"
			);
			this.tellExit();
		}else{
			this.myResponse = new RentACarBookingResponse(
					"TEST-"+this.myBookingNumber, 
					"TEST booking OK", 
					compensateUntil, 
					"Completed"
			);
			this.tellCompleted();
		}
	}

	/**
	 * Event handler for faults.
	 * @param code The error code.
	 */
	public void onFault(final Name code) {
		System.out.println("*** rentacar booking "+this.myBookingNumber+" Fault: "+code);
	}

	/**
	 * Event handler for timeouts.
	 */
	public void timeout() {
		System.out.println("*** rentacar booking "+this.myBookingNumber+" Timeout!");
	}

	/**
	 * Return the booking response object so the calling web service 
	 * may return it to the customer. At this point, the constructor will
	 * already have registered the participant with the coordination context
	 * and reported Completed.
	 * @return The booking response
	 */
	public RentACarBookingResponse getBookingResponse() {

		// Access the field so it is read at least one time...
		if (this.myRequest == null)
			throw new NullPointerException();
		
		return this.myResponse;
	}

	/**
	 * Event handler for all state changes, at this time just prints out
	 * what's going on. (Real applications will generally not need to override
	 * this method.)
	 * @param previousState Previous state.
	 * @param currentState  Current state.
	 * @param coordinatorInitiated Was the state change due to a message from the coordinator?
	 */
	protected void onStateChanged(
			final QName previousState, 
			final QName currentState,
			final boolean coordinatorInitiated
	) {
		// "Fix" compiler warnings about unused parameters
		if (previousState == null || currentState == null || coordinatorInitiated){
			// do nothing
		}
		System.out.println("*** rentacar booking "+this.myBookingNumber+" state changed to "+currentState);
	}

	/**
	 * Event handler for Cancel. Cancel the booking, abort all booking processes and
	 * cancel everything associated.
	 * @return The result to report to the coordinator.
	 */
	protected ParticipantCancelResult onCancel() {
		System.out.println("*** rentacar booking "+this.myBookingNumber+" canceled");

		return ParticipantCancelResult.CANCELED;
	}

	/**
	 * Event handler for Close. Confirm the booking, complete it, print
	 * invoices, voucers and receipts. 
	 * @return The result to report to the coordinator.
	 */
	protected ParticipantCloseResult onClose() {
		System.out.println("*** rentacar booking "+this.myBookingNumber+" closed!");
		
		return ParticipantCloseResult.CLOSED ;
	}

	/**
	 * Event handler for Compensate. Try to cancel the booking.
	 * @return The result to report to the coordinator.
	 */
	protected ParticipantCompensateResult onCompensate() {

		// Check if the booking may be compensated
		if (this.myResponse.getCompensateUntil()==null
				|| Calendar.getInstance().before(this.myResponse.getCompensateUntil())
		){
			// YES!
			System.out.println("*** rentacar booking "+this.myBookingNumber+" compensated");

			return ParticipantCompensateResult.COMPENSATED;
		}
		// YES!
		System.out.println("*** rentacar booking "+this.myBookingNumber+" NOT compensated, too late! Faulting...");

		// Sorry, its too late -- manual intervention required.
		return ParticipantCompensateResult.FAULTED;
	}

	/**
	 * Event handler for disposal. It is called when the transaction has finished
	 * and the participant is not longer needed. The participant should close any
	 * ressources and clear any references to it so the garbage collector
	 * may dispose it.
	 */
	protected void onFinish() {
		System.out.println("*** rentacar booking "+this.myBookingNumber+" finished, disposing");
	
		// TODO remove myself from the active participant list
	}

	/**
	 * The current booking number.
	 * @return ..
	 */
	public long getBookingNumber(){
		return this.myBookingNumber;
	}
}
