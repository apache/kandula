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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class SearchResultTableModel extends AbstractTableModel {

	/**
	 * The number format for the price.
	 */
	public static NumberFormat NF_PRICE = NumberFormat.getCurrencyInstance();
	
	/**
	 * The date format.
	 */
	public static final DateFormat DF_YYYYMMDD_HHMISS = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	/**
	 * 
	 */
	private static final long serialVersionUID = -1217359885695319126L;

	/**
	 * 
	 */
	private static final int COLUMNINDEX_EXPIRES = 8;
	
	/**
	 * 
	 */
	private final Vector content = new Vector();
	
	/**
	 * 
	 */
	private final Vector columnNames = new Vector();
	
	/**
	 * 
	 */
	private final Timer timer = new Timer();
	
	/**
	 * 
	 */
	public SearchResultTableModel() {
		this.columnNames.add("Booking reference");
		this.columnNames.add("When From");
		this.columnNames.add("When To");
		this.columnNames.add("Where From");
		this.columnNames.add("Where To");
		this.columnNames.add("Description");
		this.columnNames.add("Price");
		this.columnNames.add("Payment methods");
		this.columnNames.add("Expires");
		
		this.timer.schedule(new TimerTask(){
			public void run() {
				final Vector contentV = SearchResultTableModel.this.content;
				
				if(contentV == null || contentV.size()==0)
					return ;
				
				for(int i=0; i<contentV.size(); i++)
					SearchResultTableModel.this.fireTableCellUpdated(i, COLUMNINDEX_EXPIRES);
			}
		}, 0, 1000);
		
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
		TableOfferEntry toe = (TableOfferEntry)this.content.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return toe.getBookingReference();
		case 1:
			return sdf.format(toe.getWhen_from());
		case 2:
			return sdf.format(toe.getWhen_to());
		case 3:
			return toe.getWhere_from();
		case 4:
			return toe.getWhere_to();
		case 5:
			return toe.getSpecification();
		case 6:
			return NF_PRICE.format( toe.getPrice() );
		case 7:
			return Arrays.asList(toe.getPayments());
		case COLUMNINDEX_EXPIRES:
			final Calendar expires = toe.getExpires();
			if (expires == null)
				return "";
			
			final long expiresT = Calendar.getInstance().getTimeInMillis() - expires.getTimeInMillis();
			
			if (expiresT < 0)
				return ""+(-expiresT/1000)+" sec";
			return "expired";
		default:
			break;
		}
		return null;
	}

	public void addTableOfferEntry(TableOfferEntry toe){
		this.content.add(toe);
		this.fireTableDataChanged();
	}
	
	public TableOfferEntry getRow(int rowIndex) {
		TableOfferEntry tOE = (TableOfferEntry) this.content.get(rowIndex);
		return tOE;
	}
	
	public TableOfferEntry[] getRows(int[] rowIndices) {
		TableOfferEntry[] toe = new TableOfferEntry[rowIndices.length];
		for (int i = 0; i<rowIndices.length; i++) {
			toe[i] = (TableOfferEntry)this.content.get(rowIndices[i]);
		}
		return toe;
	}

	public void clearResults() {
		this.content.clear();
		this.fireTableDataChanged();
	}
}
