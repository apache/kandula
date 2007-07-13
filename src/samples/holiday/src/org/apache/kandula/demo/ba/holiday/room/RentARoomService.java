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

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Demonstration Web Service for the Kandula WS-BusinessActivity Implementation.
 * 
 * This Web Service "plays" a hotel business. For any given request, a random
 * response is returned to the caller and stored in the offer registry. 
 * 
 * When the customer wants to book one or more of those offers, they send a
 * CoordinationContext with the order and the web service happily registers itself
 * as a participant in the given context. It is necessary for the customer to issue
 * one doBooking request per option to book, e.g. if the customer still is unsure which
 * of two departure dates to decide for, they simple book both room offers but with different
 * CoordinationContexts and hence two participants (one for each start date) in the transation.
 * This enables the customer to conventiently decide which offer to close, and which to compensate.
 * 
 * The reservations' states are recorded in the order book.
 *   
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class RentARoomService implements RentARoomPortType {

	/**
	 * The next booking reference number when giving out offers.
	 */
	private static int nextOfferReference = 9999;

	/**
	 * An Offer Reference --> CarOfferType map for all offers
	 */
	private static Map offers = new HashMap();

	private static final String[] roomClasses = {"cubbyhole", "room", "suite", "penthouse"};
	
	private static final double[] priceFactor = {0.5, 0.75, 1, 1.5};
	
	/**
	 * A Booking Reference --> RentARoomParticipant map for all bookings
	 */
	private static Map bookings = new HashMap();

	/**
	 * Some payment methods from which the service randomly chooses some
	 */
	public static final String[] paymentMethods = {
			"Cash",
			"Bank transfer",
			"PayPal",
			"VISA",
			"MasterCard",
			"DinersClub"
	};

	/**
	 * Fetch the price list for the given room requirements and date. The returned data
	 * is randomly generated.
	 * @param params The room requirements and date
	 * @return Rental Room Offers for WSBA demonstration.
	 * @throws RemoteException 
	 * 
	 */
	public RentARoomOfferResponse getOffers(final RentARoomOfferRequest params) throws RemoteException {
		try{
			final ArrayList ret = new ArrayList();
			for (int classInd=0; classInd<roomClasses.length; classInd++) {
				final RoomOfferType roomOffer = new RoomOfferType();
	
				final String curBookingRef = "Room "+(nextOfferReference++);
	
				final Calendar expires = Calendar.getInstance();
				expires.add(Calendar.SECOND, (int) (100*priceFactor[classInd]));
				
				roomOffer.setDateSpec(params.getDateSpec());
				roomOffer.setLocSpec(params.getLocSpec());
				roomOffer.setOfferExpires(expires);
				roomOffer.setPayment(getRandomPaymentMethods());
				roomOffer.setPrice(
						new BigDecimal(
								(new Random().nextInt(749)+100)*priceFactor[classInd]
						)
				);
				roomOffer.setRoomBookingReference(curBookingRef);
				roomOffer.setRoomSpec(roomClasses[classInd]);
				
				// Add the offer to the return
				ret.add(roomOffer);
				// Record the offer
				offers.put(curBookingRef, roomOffer);
				System.out.println("Offer ref "+curBookingRef+" --> "+roomOffer);
			}
			
			final RentARoomOfferResponse offerResp = new RentARoomOfferResponse();
			offerResp.setMetadata("Metadata currently not used!");
			offerResp.setRoomOffer((RoomOfferType[]) ret.toArray(new RoomOfferType[0]));
			
			return offerResp;
		} catch(Exception e) {
			e.printStackTrace();
			throw new RemoteException("Error while proccessing your request: " + e);
		}
	}
	
	private String[] getRandomPaymentMethods() {
		final ArrayList ret = new ArrayList();
		
		for (int i = 0; i<paymentMethods.length; i++) {
			if (Math.random() > 0.5) {
				ret.add(paymentMethods[i]);
			}
		}
		
		if (ret.size() == 0) {
			ret.add(paymentMethods[new Random().nextInt(paymentMethods.length)]);
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
	public RentARoomBookingResponse doBooking(final RentARoomBookingRequest params) throws RemoteException {
		try{
			final String req_OfferReference = params.getRoomBookingReference();
			
			if (offers.containsKey(req_OfferReference)) {
				RoomOfferType offer = (RoomOfferType)offers.get(req_OfferReference);
				final RentARoomParticipant part = new RentARoomParticipant(this, params, offer);
	
				final RentARoomBookingResponse ret = part.getBookingResponse();
	
				bookings.put(new Long(part.getBookingNumber()), part);
				
				return ret;
				
			} else {
				throw new IllegalArgumentException("Sorry, no offer with offer code "+req_OfferReference+" found!");
			}
		}catch(final Exception e){
			e.printStackTrace();
			throw new RemoteException("Error while processing your request: ", e);
		}
	}

}
