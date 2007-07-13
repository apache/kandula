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

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Calendar;

import org.apache.axis.AxisFault;
import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.demo.ba.holiday.DateSpec;
import org.apache.kandula.demo.ba.holiday.LocSpec;
import org.apache.kandula.demo.ba.holiday.car.CarOfferType;
import org.apache.kandula.demo.ba.holiday.car.CarRentalPersonType;
import org.apache.kandula.demo.ba.holiday.car.CarRequirements;
import org.apache.kandula.demo.ba.holiday.car.RentACarBookingRequest;
import org.apache.kandula.demo.ba.holiday.car.RentACarBookingResponse;
import org.apache.kandula.demo.ba.holiday.car.RentACarOfferRequest;
import org.apache.kandula.demo.ba.holiday.car.RentACarOfferResponse;
import org.apache.kandula.demo.ba.holiday.car.RentACarPortTypeBindingStub;
import org.apache.kandula.demo.ba.holiday.room.RentARoomBookingRequest;
import org.apache.kandula.demo.ba.holiday.room.RentARoomBookingResponse;
import org.apache.kandula.demo.ba.holiday.room.RentARoomOfferRequest;
import org.apache.kandula.demo.ba.holiday.room.RentARoomOfferResponse;
import org.apache.kandula.demo.ba.holiday.room.RentARoomPortTypeBindingStub;
import org.apache.kandula.demo.ba.holiday.room.RoomOfferType;
import org.apache.kandula.demo.ba.holiday.room.RoomRentalPersonType;
import org.apache.kandula.demo.ba.holiday.room.RoomRequirements;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class Handler {

	private static TableOfferEntry[] handleCarOffers(CarOfferType[] offers) {
		int count = offers.length;
		TableOfferEntry[] offerEntries = new TableOfferEntry[count];
		for (int i = 0; i < count; i++) {
			CarOfferType offer = offers[i];
			offerEntries[i] = new CarTableOfferEntry(
					offer.getCarBookingReference(), offer.getCarSpec(), offer.getLocSpec(), 
					offer.getDateSpec(), offer.getPrice(), offer.getPayment(), offer.getOfferExpires()
			);
		}
		return offerEntries;
	}
	
	private static TableOfferEntry[] handleRoomOffers(RoomOfferType[] offers) {
		int count = offers.length;
		TableOfferEntry[] offerEntries = new TableOfferEntry[count];
		for (int i = 0; i < count; i++) {
			RoomOfferType offer = offers[i];
			offerEntries[i] = new RoomTableOfferEntry(
					offer.getRoomBookingReference(), offer.getRoomSpec(), offer.getLocSpec().getLocation(), 
					offer.getDateSpec(), offer.getPrice(), offer.getPayment(),
					offer.getOfferExpires()
			);
		}
		return offerEntries;
	}
	
	public static TableOfferEntry[] searchForCarOffers(CarRequirements carReq, DateSpec date, LocSpec loc) throws MalformedURLException, RemoteException {
		RentACarPortTypeBindingStub stub = null;
		try {
			// TODO HOLIDAY make service URLs configurable
			stub = new RentACarPortTypeBindingStub(new URL("http://localhost:8181/axis/services/RentACarServicePort"), null);
		} catch (AxisFault e) {
			e.printStackTrace();
			throw e;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		}
		RentACarOfferRequest params = 
			new RentACarOfferRequest(
					carReq, 
					date.getCarDateSpec(),
					loc.getCarLocSpec(),
					new CarRentalPersonType(),
					new CarRentalPersonType()					
			);
		
		try {
			RentACarOfferResponse resp = stub.getOffers(params);
			if (resp != null) {
				return handleCarOffers(resp.getCarOffer());
			}
			return new TableOfferEntry[0];
		} catch (RemoteException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static TableOfferEntry[] searchForRoomOffers(RoomRequirements roomReq, DateSpec date, LocSpec loc) throws MalformedURLException, RemoteException {
		RentARoomPortTypeBindingStub stub = null;
		try {
			// TODO HOLIDAY make service URLs configurable
			stub = new RentARoomPortTypeBindingStub(new URL("http://localhost:8181/axis/services/RentARoomServicePort"), null);
		} catch (AxisFault e) {
			e.printStackTrace();
			throw e;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		}
		RentARoomOfferRequest params = 
			new RentARoomOfferRequest(
					roomReq, 
					date.getRoomDateSpec(),
					loc.getRoomLocSpec(),
					new RoomRentalPersonType()					
			);
		
		try {
			RentARoomOfferResponse resp = stub.getOffers(params);
			if (resp != null) {
				return handleRoomOffers(resp.getRoomOffer());
			}
			return new TableOfferEntry[0];
		} catch (RemoteException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static BasketEntry book(
			final CoordinationContext cc, 
			final String matchcode, 
			final TableOfferEntry toe
	) throws AxisFault, RemoteException, MalformedURLException{
		
		final String bookRef;
		final Calendar compensateUntil;
		
		if (toe instanceof CarTableOfferEntry){
			final RentACarBookingResponse resp = bookCar(cc, (CarTableOfferEntry) toe);
			bookRef = resp.getCarBookingReference();
			compensateUntil = resp.getCompensateUntil();
		}else if (toe instanceof RoomTableOfferEntry){
			final RentARoomBookingResponse resp = bookRoom(cc, (RoomTableOfferEntry) toe);			
			bookRef= resp.getRoomBookingReference();
			compensateUntil = resp.getCompensateUntil();
		}else{
			throw new IllegalArgumentException("Unknown offer type "+toe.getClass().getName());
		}
		
		final BasketEntry be = new BasketEntry(matchcode, null, toe, bookRef, compensateUntil);
		
		return be;
	}

	public static RentACarBookingResponse bookCar(
			final CoordinationContext ctx,
			final CarTableOfferEntry carOffer
	) throws MalformedURLException, RemoteException {
		RentACarPortTypeBindingStub stub = null;
		try {
			// TODO HOLIDAY make service URLs configurable
			stub = new RentACarPortTypeBindingStub(new URL("http://localhost:8181/axis/services/RentACarServicePort"), null);
		} catch (AxisFault e) {
			e.printStackTrace();
			throw e;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		}
		
		final RentACarBookingRequest params = 
			new RentACarBookingRequest(
					new org.apache.kandula.demo.ba.holiday.car.ContextChoiceType(ctx),
					carOffer.getBookingReference()
			);
		final RentACarBookingResponse resp = stub.doBooking(params);
		return resp ;
	}

	private static RentARoomBookingResponse bookRoom(
			final CoordinationContext ctx, 
			final RoomTableOfferEntry roomOffer
	) throws MalformedURLException, RemoteException {
		RentARoomPortTypeBindingStub stub = null;
		try {
			// TODO HOLIDAY make service URLs configurable
			stub = new RentARoomPortTypeBindingStub(new URL("http://localhost:8181/axis/services/RentARoomServicePort"), null);
		} catch (AxisFault e) {
			e.printStackTrace();
			throw e;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		}
		
		final RentARoomBookingRequest params = 
			new RentARoomBookingRequest(
					new org.apache.kandula.demo.ba.holiday.room.ContextChoiceType(ctx),
					roomOffer.getBookingReference()
			);
		final RentARoomBookingResponse resp = stub.doBooking(params);
		return resp ;
	}

}
