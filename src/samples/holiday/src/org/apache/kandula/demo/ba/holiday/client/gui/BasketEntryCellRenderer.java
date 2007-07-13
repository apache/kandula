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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.xml.namespace.QName;

import org.apache.kandula.coordinator.ba.State;

import org.apache.kandula.wsbai.BAParticipantType;

/**
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class BasketEntryCellRenderer extends DefaultTableCellRenderer {
	private final BasketTableModel tableModel;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public Component getTableCellRendererComponent(
			final JTable table, 
			final Object value,
			final boolean selected, 
			final boolean focused, 
			final int row, 
			final int column
	) {
		setEnabled(table == null || table.isEnabled()); // see question above

		super.getTableCellRendererComponent(table, value, selected, focused, row, column);

       
		final BasketEntry e = (BasketEntry) tableModel.content.get(row);
		final BAParticipantType part = e.getParticipant();
		if (part == null){
			setBackground(null);
			return this;
		}

		final QName rs = part.getResultState().getState().getValue();
		
		/*
		 * Canceled 	yellow	Faulting-Active
		 * Fault		red 	Faulting-Compensating
		 * Compensating	orange
		 * Closing		green
		 * Active		cyan
		 * completed	blue
		 */
		
		if (State.STATE_ACTIVE.equals(rs)){
			setBackground(Color.CYAN);

		}else if (State.STATE_FAULTING.equals(rs)
				|| State.STATE_FAULTING_COMPENSATING.equals(rs)
		){
			setBackground(Color.RED);

		}else if (State.STATE_FAULTING_ACTIVE.equals(rs)){
			setBackground(Color.YELLOW);

		}else if (State.STATE_COMPLETED.equals(rs)){
			setBackground(Color.PINK);

		}else if (State.STATE_CLOSING.equals(rs)){
			setBackground(Color.GREEN);

		}else if (State.STATE_EXITING.equals(rs)){
			setBackground(Color.LIGHT_GRAY);

		}else if (State.STATE_CANCELLING.equals(rs)
				|| State.STATE_CANCELLING_ACTIVE.equals(rs)
				|| State.STATE_CANCELLING_COMPLETING.equals(rs)
				|| State.STATE_COMPENSATING.equals(rs)
		){
			setBackground(Color.YELLOW);
		}else{
			setBackground(null);
		}
		
		
		return this;
	}

	/**
	 * 
	 * @param tableModel
	 */
	public BasketEntryCellRenderer(final BasketTableModel pTableModel) {
		super();
		this.tableModel = pTableModel;
	}
}