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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class BasketTableModel extends AbstractTableModel {

	/**
	 * The number format for the price.
	 */
	public static NumberFormat NF_PRICE = NumberFormat.getCurrencyInstance();
	
	protected Timer timer = new Timer();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1960180259106055961L;

	protected static final int INDEX_OF_COMPENSATEUNTIL = 10;
	
	
	Vector content = new Vector();
	Vector columnNames = new Vector();
	/**
	 * 
	 */
	public BasketTableModel() {
		this.columnNames.add("Matchcode");
		this.columnNames.add("State");
		this.columnNames.add("Progress");
		this.columnNames.add("Booking reference");
		this.columnNames.add("When From");
		this.columnNames.add("When To");
		this.columnNames.add("Where From");
		this.columnNames.add("Where To");
		this.columnNames.add("Description");
		this.columnNames.add("Price");
		this.columnNames.add("Compensate until");
		
		timer.scheduleAtFixedRate(new TimerTask(){
			public void run() {
				final Vector content = BasketTableModel.this.content;
				synchronized (content) {
					if (content == null || content.size()==0 )
						return ;
					
					for(int i=0; i<content.size(); i++){
						final BasketEntry be = (BasketEntry) content.get(i);
						if (be.getCompensateUntil() != null)
							BasketTableModel.this.fireTableCellUpdated(i, INDEX_OF_COMPENSATEUNTIL);
					}
				}
			}
		}, 
		0, 
		1000
		);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		return (String)this.columnNames.get(column);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return this.columnNames.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return this.content.size();
	}

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		BasketEntry be = (BasketEntry)this.content.get(rowIndex);
		TableOfferEntry toe = be.getTOE();

		switch (columnIndex) {
		case 0:
			return be.getMatchcode();
		case 1:
			return be.getState().getLocalPart();
		case 2:
			if (be.getParticipant() != null){
				return be.getParticipant().getResultState().getState().getValue().getLocalPart();
			}
			return "-";
		case 3:
			return toe.getBookingReference();
		case 4:
			return sdf.format(toe.getWhen_from());
		case 5:
			return sdf.format(toe.getWhen_to());
		case 6:
			return toe.getWhere_from();
		case 7:
			return toe.getWhere_to();
		case 8:
			return toe.getSpecification();
		case 9:
			return NF_PRICE.format( toe.getPrice() );
		case INDEX_OF_COMPENSATEUNTIL:
			final Calendar compensateUntil = be.getCompensateUntil();
			if (compensateUntil==null)
				return "-";
			
			final Calendar now = Calendar.getInstance();
			final long compensateTime = ((now.getTimeInMillis()-compensateUntil.getTimeInMillis())/1000);
			if (compensateTime<0){
				return ""+(-compensateTime)+" sec";
			}
			return "-too late-";
		default:
			break;
		}
		return null;
	}

	public void addBasketEntry(BasketEntry be){
		synchronized (this.content) {
			this.content.add(be);
		}
		this.fireTableDataChanged();
	}
	
	public BasketEntry[] getRows(int[] rowIndices) {
		BasketEntry[] bes = new BasketEntry[rowIndices.length];
		for (int i= 0; i<rowIndices.length; i++) {
			bes[i] = (BasketEntry)this.content.get(rowIndices[i]);
		}
		return bes;
	}
	public BasketEntry[] getRows(){
		return (BasketEntry[]) this.content.toArray(new BasketEntry[0]);
	}
}
