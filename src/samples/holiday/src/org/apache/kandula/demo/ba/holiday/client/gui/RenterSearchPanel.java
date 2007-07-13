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
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.JComboBox;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JButton;

import org.apache.axis.types.PositiveInteger;
import org.apache.kandula.demo.ba.holiday.car.CarRequirements;
import org.apache.kandula.demo.ba.holiday.room.RoomRequirements;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class RenterSearchPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel jPanel_Type = null;
	private JPanel jPanel_SearchClear = null;
	private JLabel jLabel_Type = null;
	private JComboBox jComboBox_Type = null;
	private RenterSearchCarPanel renterSearchCarPanel = null;  //  @jve:decl-index=0:visual-constraint="358,110"
	protected JPanel jPanel_placeHolder = null;
	private RenterSearchRoomPanel renterSearchRoomPanel = null;  //  @jve:decl-index=0:visual-constraint="434,34"
	private JButton jButton_Search = null;
	private JButton jButton_Clear = null;
	
	private ArrayList listeners = new ArrayList();  //  @jve:decl-index=0:

	/**
	 * This is the default constructor
	 */
	public RenterSearchPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 150);
		this.setMinimumSize(new Dimension(300, 150));
		this.setMaximumSize(new Dimension(300, 150));
		
		this.setLayout(new BorderLayout());
		this.add(getJPanel_Type(), BorderLayout.NORTH);  // Generated
		this.add(getJPanel_placeHolder(), BorderLayout.CENTER);  // Generated

		final JPanel searchClear = getJPanel_SearchClear();
		searchClear.add(getJButton_Search(), BorderLayout.WEST);  // Generated
		searchClear.add(getJButton_Clear(), BorderLayout.EAST);  // Generated

		this.add(searchClear, BorderLayout.SOUTH);  // Generated
		
	}

	/**
	 * This method initializes jPanel_Type	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_Type() {
		if (this.jPanel_Type == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;  // Generated
			gridBagConstraints1.gridy = 0;  // Generated
			gridBagConstraints1.weightx = 1.0;  // Generated
			gridBagConstraints1.gridx = 1;  // Generated
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;  // Generated
			gridBagConstraints.gridy = 0;  // Generated
			this.jPanel_Type = new JPanel();
			this.jPanel_Type.setLayout(new GridBagLayout());  // Generated
			this.jPanel_Type.add(getJLabel_Type(), gridBagConstraints);
			this.jPanel_Type.add(getJComboBox_Type(), gridBagConstraints1);  // Generated
		}
		return this.jPanel_Type;
	}
	/**
	 * This method initializes jPanel_Type	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_SearchClear() {
		if (this.jPanel_SearchClear == null) {
			this.jPanel_SearchClear = new JPanel();
			this.jPanel_SearchClear.setLayout(new BorderLayout());  // Generated
		}
		return this.jPanel_SearchClear;
	}
	/**
	 * @return Returns the jLabel_Type.
	 */
	public JLabel getJLabel_Type() {
		if (this.jLabel_Type == null) {
			this.jLabel_Type = new JLabel();
			this.jLabel_Type.setText("Type of rental:");
		}
		return this.jLabel_Type;
	}

	/**
	 * This method initializes jComboBox_Type	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	protected JComboBox getJComboBox_Type() {
		if (this.jComboBox_Type == null) {
			this.jComboBox_Type = new JComboBox();
			this.jComboBox_Type.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						if( e.getItem().equals("Car")) {
							System.out.println("Car was choosen");
//							if (jPanel_placeHolder != null) {
//								RenterSearchPanel.this.remove(jPanel_placeHolder);
//							}
//							RenterSearchPanel.this.add(getJPanel_placeHolder(), BorderLayout.CENTER);
						} else if(e.getItem().equals("Room")) {
							System.out.println("Room was choosen");
//							if (jPanel_placeHolder != null) {
//								RenterSearchPanel.this.remove(jPanel_placeHolder);
//							}
//							RenterSearchPanel.this.add(getJPanel_placeHolder(), BorderLayout.CENTER);
						}
						if (RenterSearchPanel.this.jPanel_placeHolder != null) {
							RenterSearchPanel.this.remove(RenterSearchPanel.this.jPanel_placeHolder);
						}
						RenterSearchPanel.this.add(getJPanel_placeHolder(), BorderLayout.CENTER);
						RenterSearchPanel.this.revalidate();
						RenterSearchPanel.this.repaint();
//						RenterSearchPanel.this.jPanel_placeHolder.revalidate();
//						RenterSearchPanel.this.jPanel_placeHolder.repaint();
					}
				}
			});
			this.jComboBox_Type.addItem("Car");
			this.jComboBox_Type.addItem("Room");
		}
		return this.jComboBox_Type;
	}

	/**
	 * This method initializes renterSearchCarPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private RenterSearchCarPanel getRenterSearchCarPanel() {
		if (this.renterSearchCarPanel == null) {
			this.renterSearchCarPanel = new RenterSearchCarPanel();
			this.renterSearchCarPanel.setPreferredSize(new Dimension(300, 88));  // Generated
			this.renterSearchCarPanel.setSize(new Dimension(300, 88));  // Generated
		}
		return this.renterSearchCarPanel;
	}

	/**
	 * This method initializes jPanel_placeHolder	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getJPanel_placeHolder() {
		if (this.jPanel_placeHolder == null) {
			this.jPanel_placeHolder = new JPanel();
			this.jPanel_placeHolder.setLayout(new GridBagLayout());  // Generated
			this.jPanel_placeHolder.setPreferredSize(new Dimension(300, 88));  // Generated
		}
		if (this.jComboBox_Type.getSelectedItem().equals("Car")) {
			this.jPanel_placeHolder = getRenterSearchCarPanel();
			this.renterSearchRoomPanel = null;
		} else if (this.jComboBox_Type.getSelectedItem().equals("Room")) {
			this.jPanel_placeHolder = getRenterSearchRoomPanel();
			this.renterSearchCarPanel = null;
		}
		this.jPanel_placeHolder.revalidate();
		this.jPanel_placeHolder.repaint();
		return this.jPanel_placeHolder;
	}

	/**
	 * This method initializes renterSearchRoomPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private RenterSearchRoomPanel getRenterSearchRoomPanel() {
		if (this.renterSearchRoomPanel == null) {
			this.renterSearchRoomPanel = new RenterSearchRoomPanel();
			this.renterSearchRoomPanel.setPreferredSize(new Dimension(300, 88));  // Generated
			this.renterSearchRoomPanel.setSize(new Dimension(300, 88));  // Generated
		}
		return this.renterSearchRoomPanel;
	}

	/**
	 * This method initializes jButton_Search	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_Search() {
		if (this.jButton_Search == null) {
			this.jButton_Search = new JButton();
			this.jButton_Search.setText("Search for offers");
			this.jButton_Search.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (e.getID() == ActionEvent.ACTION_PERFORMED) {
						System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
						if (RenterSearchPanel.this.getJComboBox_Type().getSelectedItem().equals("Car")) {
							System.out.println("Search for Car Offers");
							try {
								RenterSearchPanel.this.fireSearchFinished(
										Handler.searchForCarOffers(
												new CarRequirements(
														"Test",
														new PositiveInteger(((SearchDetailInterface)RenterSearchPanel.this.getJPanel_placeHolder()).getCount())
												), 
												((SearchDetailInterface)RenterSearchPanel.this.getJPanel_placeHolder()).getDateSpec(), 
												((SearchDetailInterface)RenterSearchPanel.this.getJPanel_placeHolder()).getLocSpec()
										)
								);
							} catch (MalformedURLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (RemoteException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} else if (RenterSearchPanel.this.getJComboBox_Type().getSelectedItem().equals("Room")) {
							System.out.println("Search for Room Offers");
							try {
								RenterSearchPanel.this.fireSearchFinished(
										Handler.searchForRoomOffers(
												new RoomRequirements(
														"Test",
														new PositiveInteger(((SearchDetailInterface)RenterSearchPanel.this.getJPanel_placeHolder()).getCount())
												), 
												((SearchDetailInterface)RenterSearchPanel.this.getJPanel_placeHolder()).getDateSpec(), 
												((SearchDetailInterface)RenterSearchPanel.this.getJPanel_placeHolder()).getLocSpec()
										)
								);
							} catch (MalformedURLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (RemoteException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
			});
		}
		return this.jButton_Search;
	}
	/**
	 * This method initializes jButton_Search	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_Clear() {
		if (this.jButton_Clear == null) {
			this.jButton_Clear = new JButton();
			this.jButton_Clear.setText("Clear list");
			this.jButton_Clear.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (e.getID() == ActionEvent.ACTION_PERFORMED) {
						System.out.println("Clearing Search Results.");
						RenterSearchPanel.this.fireClearSearch();
					}
				}
			});
		}
		return this.jButton_Clear;
	}
	public void addListener(SearchListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(SearchListener listener) {
		this.listeners.remove(listener);
	}
	
	public void fireSearchFinished(TableOfferEntry[] toe) {
		SearchEvent e = new SearchEvent(this, toe);
		for(int i = 0; i<this.listeners.size(); i++) {
			SearchListener listener = (SearchListener)this.listeners.get(i);
			listener.searchFinished(e);
		}
	}
	public void fireClearSearch() {
		for(int i = 0; i<this.listeners.size(); i++) {
			SearchListener listener = (SearchListener)this.listeners.get(i);
			listener.searchClear();
		}
	}
}
