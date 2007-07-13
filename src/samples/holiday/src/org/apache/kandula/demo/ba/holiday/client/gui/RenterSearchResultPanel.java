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

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JButton;

public class RenterSearchResultPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPane = null;
	private JTable jTable_Result = null;
	private JPanel jPanel = null;  //  @jve:decl-index=0:visual-constraint="329,20"
	private JButton jButton_Book = null;

	private ArrayList listeners = new ArrayList();
	
	/**
	 * This is the default constructor
	 */
	public RenterSearchResultPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;  // Generated
		gridBagConstraints.gridy = 0;  // Generated
		gridBagConstraints.weightx = 1.0;  // Generated
		gridBagConstraints.weighty = 1.0;  // Generated
		gridBagConstraints.gridx = 0;  // Generated
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(getJPanel(), gridBagConstraints);  // Generated
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (this.jScrollPane == null) {
			this.jScrollPane = new JScrollPane();
			this.jScrollPane.setViewportView(getJTable_Result());  // Generated
		}
		return this.jScrollPane;
	}

	/**
	 * This method initializes jTable_Result	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable_Result() {
		if (this.jTable_Result == null) {
			this.jTable_Result = new JTable();
			this.jTable_Result.setModel(new SearchResultTableModel());
			this.jTable_Result.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//			this.jTable_Result.getTableHeader().setReorderingAllowed(false);
		}
		return this.jTable_Result;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (this.jPanel == null) {
			this.jPanel = new JPanel();
			this.jPanel.setLayout(new BorderLayout());  // Generated
			this.jPanel.setSize(new Dimension(264, 205));  // Generated
			this.jPanel.add(getJButton_Book(), BorderLayout.SOUTH);  // Generated
			this.jPanel.add(getJScrollPane(), BorderLayout.CENTER);
		}
		return this.jPanel;
	}

	/**
	 * This method initializes jButton_Book	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_Book() {
		if (this.jButton_Book == null) {
			this.jButton_Book = new JButton();
			this.jButton_Book.setText("Add selected offer to basket");
			this.jButton_Book.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed(): " + e.getActionCommand());
					fireAddToBasketFinished();
				}
			});
		}
		return this.jButton_Book;
	}

	public void addTableOfferEntries(TableOfferEntry[] offers) {
		SearchResultTableModel srtm = (SearchResultTableModel)getJTable_Result().getModel();
		for (int i = 0; i<offers.length; i++) {
			TableOfferEntry toe = offers[i];
			srtm.addTableOfferEntry(toe);
		}
	}
	public void clearTableOffers(){
		SearchResultTableModel srtm = (SearchResultTableModel)getJTable_Result().getModel();
		srtm.clearResults();
	}
	
	public void addAddToBasketListener(AddToBasketListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeAddToBasketListener(AddToBasketListener listener) {
		this.listeners.remove(listener);
	}
	
	public void fireAddToBasketFinished() {
		TableOfferEntry[] toe = ((SearchResultTableModel)this.jTable_Result.getModel()).getRows(this.jTable_Result.getSelectedRows());
		AddToBasketEvent atbe = new AddToBasketEvent(this, toe);
		for(int i = 0; i<this.listeners.size(); i++) {
			AddToBasketListener listener = (AddToBasketListener)this.listeners.get(i);
			listener.addToBasketFinished(atbe);
		}
	}

}
