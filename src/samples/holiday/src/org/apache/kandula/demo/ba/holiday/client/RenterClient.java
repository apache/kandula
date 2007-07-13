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
package org.apache.kandula.demo.ba.holiday.client;

import java.awt.Frame;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.coordinator.ba.initiator.AbstractCoordinatorIProxy;
import org.apache.kandula.coordinator.ba.initiator.AtomicCoordinatorIProxy;
import org.apache.kandula.coordinator.ba.initiator.MixedCoordinatorIProxy;
import org.apache.kandula.demo.ba.holiday.client.gui.AddToBasketEvent;
import org.apache.kandula.demo.ba.holiday.client.gui.AddToBasketListener;
import org.apache.kandula.demo.ba.holiday.client.gui.BasketEntry;
import org.apache.kandula.demo.ba.holiday.client.gui.BasketEvent;
import org.apache.kandula.demo.ba.holiday.client.gui.BasketListener;
import org.apache.kandula.demo.ba.holiday.client.gui.BasketPanel;
import org.apache.kandula.demo.ba.holiday.client.gui.Handler;
import org.apache.kandula.demo.ba.holiday.client.gui.RenterClientJFrame;
import org.apache.kandula.demo.ba.holiday.client.gui.RenterSearchAndResultPanel;
import org.apache.kandula.demo.ba.holiday.client.gui.TableOfferEntry;
import org.apache.kandula.demo.ba.holiday.client.gui.BasketEvent.EventType;

import org.apache.kandula.wsbai.BAParticipantReferenceType;
import org.apache.kandula.wsbai.BAParticipantType;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class RenterClient implements AddToBasketListener, BasketListener {
	private int nextMatchcode = 1;

	/**
	 * HashMap holding the Basket Entries by matchcode.
	 */
	private final HashMap participants = new HashMap();
	private AbstractCoordinatorIProxy coordIProxy;

	public RenterClient() throws AxisFault, RemoteException, MalformedURIException, MalformedURLException, ServiceException {

		final Object[] outcomeTypeOptions = { "atomic outcome", "mixed outcome" };
			
			final int n = JOptionPane.showOptionDialog(null, "Please choose your outcome type!",
			          "Outcome Type", JOptionPane.YES_NO_OPTION,
			          JOptionPane.QUESTION_MESSAGE, null, outcomeTypeOptions,
			          outcomeTypeOptions[1]);
			System.out.println("\"" + n + "\" was chosen.");
			if (n != -1) {
				if (n == 0) {
					coordIProxy = AtomicCoordinatorIProxy.createNewContext_WithWSBAI();
				} else {
					coordIProxy = MixedCoordinatorIProxy.createNewContext_WithWSBAI();
				}
				final RenterClientJFrame rc = new RenterClientJFrame("Kandula WS-BA-I - Holiday Demo");
	
				RenterSearchAndResultPanel.rentersearchResultPanel.addAddToBasketListener(this);
				RenterClientJFrame.basketPanel.addListener(this);
				RenterClientJFrame.basketPanel.setMode(n==0?BasketPanel.MODE_ATOMIC:BasketPanel.MODE_MIXED);
	
				rc.setExtendedState(Frame.MAXIMIZED_BOTH);
				rc.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(null, "System will exit now");
			}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			final RenterClient rc = new RenterClient();
			if (rc != null) {};
		}catch(Throwable e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "A critical error occured: "+e.getMessage());
		}

	}

	public void addToBasketFinished(AddToBasketEvent e) {
		TableOfferEntry[] toes = e.getTOE();
		for (int i = 0; i<toes.length; i++) {
			final String matchcode = "P"+(this.nextMatchcode++);

			try {
				final CoordinationContext ctx = this.coordIProxy.getCoordinationContextForParticipant(matchcode);

				final BasketEntry be = Handler.book(ctx, matchcode, toes[i]);
				this.participants.put(matchcode, be);

				RenterClientJFrame.basketPanel.addBasketEntry(be);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void basketEventFired(final BasketEvent e) {
		System.out.println("BasketEvent received in RenterClient: "+e);

		/*
		 * Parse the selected participants into an array of BAParticipants 
		 */
		final BAParticipantReferenceType[] participantsToOperateOn;
		if (e.getBE() != null && e.getBE().length>0){
			participantsToOperateOn =
				new BAParticipantReferenceType[e.getBE().length];

			for(int i=0; i<e.getBE().length; i++){
				final BasketEntry be = e.getBE()[i];
				participantsToOperateOn[i] = new BAParticipantReferenceType(be.getMatchcode());
			}
		}else{
			participantsToOperateOn = null;
		}

		try{
			// The updated participant data to display to the user after handling the action
			final BAParticipantType[] plist;

			// Depending on the event type from the gui, act upon participants
			if (EventType.CANCEL.equals(e.getType())){
				// Cancel...
				if (this.coordIProxy instanceof AtomicCoordinatorIProxy) {
					plist = ((AtomicCoordinatorIProxy) this.coordIProxy).cancelOrCompensateAllParticipants();
				} else if (this.coordIProxy instanceof MixedCoordinatorIProxy) {
					plist = ((MixedCoordinatorIProxy)this.coordIProxy).cancelParticipants(participantsToOperateOn);
				} else {
					// not possible
					throw new IllegalArgumentException("Illegal initiatorproxy type: "+this.coordIProxy);
				}
			
			}else if (EventType.CLOSE.equals(e.getType())){
				// Close...
				if (this.coordIProxy instanceof AtomicCoordinatorIProxy) {
					plist = ((AtomicCoordinatorIProxy) this.coordIProxy).closeAllParticipants();
				} else if (this.coordIProxy instanceof MixedCoordinatorIProxy) {
					plist = ((MixedCoordinatorIProxy)this.coordIProxy).closeParticipants(participantsToOperateOn);
				} else {
					// not possible
					throw new IllegalArgumentException("Illegal initiatorproxy type: "+this.coordIProxy);
				}
			
			}else if (EventType.COMPENSATE.equals(e.getType())){
				// Compensate...
				if (this.coordIProxy instanceof AtomicCoordinatorIProxy) {
					plist = ((AtomicCoordinatorIProxy) this.coordIProxy).cancelOrCompensateAllParticipants();
				} else if (this.coordIProxy instanceof MixedCoordinatorIProxy) {
					plist = ((MixedCoordinatorIProxy)this.coordIProxy).compensateParticipants(participantsToOperateOn);
				} else {
					// not possible
					throw new IllegalArgumentException("Illegal initiatorproxy type: "+this.coordIProxy);
				}

			}else if (EventType.COMPLETE.equals(e.getType())){
				// Complete...
				if (this.coordIProxy instanceof AtomicCoordinatorIProxy) {
					plist = ((AtomicCoordinatorIProxy) this.coordIProxy).completeParticipants(participantsToOperateOn);
				} else if (this.coordIProxy instanceof MixedCoordinatorIProxy) {
					plist = ((MixedCoordinatorIProxy)this.coordIProxy).completeParticipants(participantsToOperateOn);
				} else {
					// not possible
					throw new IllegalArgumentException("Illegal initiatorproxy type: "+this.coordIProxy);
				}

			}else if (EventType.REFRESH.equals(e.getType())){
				// Refresh only...
				plist = this.coordIProxy.listParticipants();

			}else{
				throw new IllegalArgumentException("Invalid BasketEvent type for RenterClient: "+e.getType());
			}

			// Now update the participant list
			for(int i=0; i<plist.length; i++){
				final String cMatchcode = plist[i].getParticipantMatchcode().getParticipantMatchcode();
				final BasketEntry cPart = (BasketEntry) this.participants.get(cMatchcode);

				if (cPart == null){
					throw new NullPointerException("No table entry for listed participant "+plist[i]+" ?");
				}

				cPart.setParticipant(plist[i]);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}


}
