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

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * Demonstration Web Service for the Kandula WS-BusinessActivity Implementation.
 * 
 * This Web Service "plays" a car rental business. For any given request, a random
 * response is returned to the caller and stored in the offer registry. 
 * 
 * When the customer wants to book one or more of those offers, they send a
 * CoordinationContext with the order and the web service happily registers itself
 * as a participant in the given context. It is necessary for the customer to issue
 * one doBooking request per option to book, e.g. if the customer still is unsure which
 * of two departure dates to decide for, they simple book both car offers but with different
 * CoordinationContexts and hence two participants (one for each start date) in the transation.
 * This enables the customer to conventiently decide which offer to close, and which to compensate.
 * 
 * The reservations' states are recorded in the order book.
 *   
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class RentACarService implements RentACarPortType {
	/**
	 * The next booking reference number when giving out offers.
	 */
	private static int nextOfferReference = 1003;
	
	/**
	 * An Offer Reference --> CarOfferType map for all offers
	 */
	private static Map offers = new HashMap();

	/**
	 * A Booking Reference --> RentACarParticipant map for all bookings
	 */
	private static Map bookings = new HashMap();

	/**
	 * The car classes this service offers.
	 */
	public static final String[] carClasses = {"A - Economy", "B - Standard", "C - Executive"};
	
	/**
	 * The car class price factors.
	 */
	public static final double[] carClassPriceFactor = {0.75, 1, 1.5};

	/**
	 * Some payment methods from which the service randomly chooses some
	 */
	public static final String[] carPaymentMethods = {
			"Cash on car collect",
			"Bank transfer",
			"PayPal",
			"VISA",
			"MasterCard",
			"DinersClub"
	};

	/**
	 * Fetch the price list for the given car requirements, location and date. The returned data
	 * is randomly generated.
	 * @param params The car requirements, location and date
	 * @return Rental Car Offers for WSBA demonstration.
	 * @throws RemoteException An exception.
	 * 
	 */
	public RentACarOfferResponse getOffers(final RentACarOfferRequest params) throws RemoteException {
		try{
			final ArrayList ret = new ArrayList();
			
			{ // generate offers
				for(int classIndex=0; classIndex<carClasses.length; classIndex++){
					final CarOfferType carOffer = new CarOfferType();
					
					final String curBookingRef = "Car-"+(nextOfferReference++);

					final Calendar expires = Calendar.getInstance();
					expires.add(Calendar.SECOND, (int) (100*carClassPriceFactor[classIndex]));

					carOffer.setCarBookingReference(curBookingRef);
					carOffer.setCarSpec(carClasses[classIndex]);
					carOffer.setDateSpec(params.getDateSpec());
					carOffer.setLocSpec(params.getLocSpec());
					carOffer.setOfferExpires(expires);
					carOffer.setPrice(
						new BigDecimal(
							(100+Math.random()*200) * carClassPriceFactor[classIndex] 
							* Math.ceil(
									(
											(float) params.getDateSpec().getTo().getTime() 
											- (float) params.getDateSpec().getFrom().getTime()
									) / 86400000 +1)
						)
					);
					
	
					final String[] payments = getRandomPayments();
					carOffer.setPayment(payments);
	
					// Add the offer to the return
					ret.add(carOffer);
					// Record the offer
					offers.put(curBookingRef, carOffer);
					System.out.println("Offer ref "+curBookingRef+" --> "+carOffer);
				}
			}
			
			final RentACarOfferResponse resp = new RentACarOfferResponse();
			resp.setMetadata("Result Metadata -- currently not used...");
			resp.setCarOffer((CarOfferType[]) ret.toArray(new CarOfferType[0]));
	
			return resp;
		}catch(final Exception e){
			e.printStackTrace();
			throw new RemoteException("Error while processing your request: ", e);
		}
	}

	/**
	 * Randomly chooses some payments.
	 * @return Some of {@link #carPaymentMethods}
	 */
	private String[] getRandomPayments() {
		final ArrayList ret = new ArrayList();

		for(int i=0; i<carPaymentMethods.length; i++){
			if (Math.random()>0.5){
				ret.add(carPaymentMethods[i]);
			}
		}
		
		// Ensure at least one payment option is returned
		if (ret.size()==0){
			final int entry = (int) Math.random()*carPaymentMethods.length;
			ret.add(carPaymentMethods[entry]);
		}
		
		return (String[]) ret.toArray(new String[0]);
	}

	/**
	 * Actually book one or more offers from getOffers(). The web serivce enrols in the given
	 * WS-BA transaction context as participant, reports completed and waits for closing/compensation.
	 * The booking response includes a xsd:dateTime up until which compensation may be issued.
	 * If the customer decides to compensate at a later time, the participant will report Faulted.   
	 * @param params The booking request.
	 * @return The booking response.
	 * @throws RemoteException 
	 */
	public RentACarBookingResponse doBooking(final RentACarBookingRequest params) throws RemoteException {
		try{
			final String req_OfferReference = params.getCarBookingReference();
			final CarOfferType carOffer = (CarOfferType) offers.get(req_OfferReference);
			
			if (carOffer == null){
				throw new IllegalArgumentException("Sorry, no offer with offer code "+req_OfferReference+" found!");
			}
			
			final RentACarParticipant part = new RentACarParticipant(this, params, carOffer);

			final RentACarBookingResponse ret = part.getBookingResponse();

			bookings.put(new Long(part.getBookingNumber()), part);
			
			return ret;
		}catch(final Exception e){
			e.printStackTrace();
			throw new RemoteException("Error while processing your request: ", e);
		}
	}
	
}
