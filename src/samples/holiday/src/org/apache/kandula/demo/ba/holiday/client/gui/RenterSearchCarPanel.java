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

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import org.apache.axis.types.PositiveInteger;
import org.apache.kandula.demo.ba.holiday.car.CarRequirements;
import org.apache.kandula.demo.ba.holiday.DateSpec;
import org.apache.kandula.demo.ba.holiday.LocSpec;

import java.awt.Dimension;
import java.util.Date;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class RenterSearchCarPanel extends JPanel implements SearchDetailInterface {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel_Who = null;
	private JLabel jLabel_When = null;
	private JLabel jLabel_Where = null;
	private JLabel jLabel_passengerCount = null;
	private JTextField jTextField_Who = null;
	private JFormattedTextField jTextField_WhenFrom = null;
	private JTextField jTextField_Where = null;
	private JComboBox jComboBox_PassengerCount = null;
	private JFormattedTextField  jTextField_WhenTo = null;
	private JTextField jTextField_WhereTo = null;

	/**
	 * This is the default constructor
	 */
	public RenterSearchCarPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		gridBagConstraints21.fill = GridBagConstraints.BOTH;  // Generated
		gridBagConstraints21.gridy = 2;  // Generated
		gridBagConstraints21.weightx = 1.0;  // Generated
		gridBagConstraints21.gridx = 2;  // Generated
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.fill = GridBagConstraints.BOTH;  // Generated
		gridBagConstraints11.gridy = 1;  // Generated
		gridBagConstraints11.weightx = 1.0;  // Generated
		gridBagConstraints11.gridx = 2;  // Generated
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.fill = GridBagConstraints.BOTH;  // Generated
		gridBagConstraints7.gridy = 3;  // Generated
		gridBagConstraints7.weightx = 1.0;  // Generated
		gridBagConstraints7.gridwidth = 2;  // Generated
		gridBagConstraints7.gridx = 1;  // Generated
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.fill = GridBagConstraints.BOTH;  // Generated
		gridBagConstraints6.gridy = 2;  // Generated
		gridBagConstraints6.weightx = 1.0;  // Generated
		gridBagConstraints6.gridx = 1;  // Generated
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.fill = GridBagConstraints.BOTH;  // Generated
		gridBagConstraints5.gridy = 1;  // Generated
		gridBagConstraints5.weightx = 1.0;  // Generated
		gridBagConstraints5.gridx = 1;  // Generated
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.fill = GridBagConstraints.BOTH;  // Generated
		gridBagConstraints4.gridy = 0;  // Generated
		gridBagConstraints4.weightx = 1.0;  // Generated
		gridBagConstraints4.gridwidth = 2;  // Generated
		gridBagConstraints4.gridx = 1;  // Generated
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.anchor = GridBagConstraints.EAST;
		gridBagConstraints3.gridx = 0;  // Generated
		gridBagConstraints3.gridy = 3;  // Generated
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.gridx = 0;  // Generated
		gridBagConstraints2.gridy = 2;  // Generated
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.anchor = GridBagConstraints.EAST;
		gridBagConstraints1.gridx = 0;  // Generated
		gridBagConstraints1.gridy = 1;  // Generated
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.gridx = 0;  // Generated
		gridBagConstraints.gridy = 0;  // Generated
		this.setSize(300, 88);
		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(300, 88));  // Generated
		this.add(getJLabel_Who(), gridBagConstraints);  // Generated
		this.add(getJLabel_When(), gridBagConstraints1);  // Generated
		this.add(getJLabel_Where(), gridBagConstraints2);  // Generated
		this.add(getJLabel_passengerCount(), gridBagConstraints3);  // Generated
		this.add(getJTextField_Who(), gridBagConstraints4);  // Generated
		this.add(getJTextField_WhenFrom(), gridBagConstraints5);  // Generated
		this.add(getJTextField_Where(), gridBagConstraints6);  // Generated
		this.add(getJComboBox_PassengerCount(), gridBagConstraints7);  // Generated
		this.add(getJTextField_WhenTo(), gridBagConstraints11);  // Generated
		this.add(getJTextField_WhereTo(), gridBagConstraints21);  // Generated
	}

	/**
	 * @return Returns the jLabel_Who.
	 */
	public JLabel getJLabel_Who() {
		if (this.jLabel_Who == null) {
			this.jLabel_Who = new JLabel();
			this.jLabel_Who.setText("who:");  // Generated
			jLabel_Who.setVisible(false);
		}
		return this.jLabel_Who;
	}

	/**
	 * This method initializes jTextField_Who	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_Who() {
		if (this.jTextField_Who == null) {
			this.jTextField_Who = new JTextField();
			jTextField_Who.setVisible(false);
		}
		return this.jTextField_Who;
	}

	/**
	 * @return Returns the jLabel_When.
	 */
	public JLabel getJLabel_When() {
		if (this.jLabel_When == null) {
			this.jLabel_When = new JLabel();
			this.jLabel_When.setText("when:");  // Generated
		}
		return this.jLabel_When;
	}

	/**
	 * This method initializes jTextField_WhenFrom	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JFormattedTextField getJTextField_WhenFrom() {
		if (this.jTextField_WhenFrom == null) {
			this.jTextField_WhenFrom = new JFormattedTextField();
			this.jTextField_WhenFrom.setValue(new Date());
		}
		return this.jTextField_WhenFrom;
	}

	/**
	 * This method initializes jTextField_WhenTo	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JFormattedTextField getJTextField_WhenTo() {
		if (this.jTextField_WhenTo == null) {
			this.jTextField_WhenTo = new JFormattedTextField();
			this.jTextField_WhenTo.setValue(new Date());
		}
		return this.jTextField_WhenTo;
	}

	/**
	 * This method initializes jTextField_Where	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_Where() {
		if (this.jTextField_Where == null) {
			this.jTextField_Where = new JTextField();
		}
		return this.jTextField_Where;
	}

	/**
	 * @return Returns the jLabel_Where.
	 */
	public JLabel getJLabel_Where() {
		if (this.jLabel_Where == null) {
			this.jLabel_Where = new JLabel();
			this.jLabel_Where.setText("where:");  // Generated
		}
		return this.jLabel_Where;
	}

	/**
	 * This method initializes jComboBox_PassengerCount	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox_PassengerCount() {
		if (this.jComboBox_PassengerCount == null) {
			this.jComboBox_PassengerCount = new JComboBox();
			this.jComboBox_PassengerCount.addItem(new String("1"));
			this.jComboBox_PassengerCount.addItem(new String("2"));
			this.jComboBox_PassengerCount.addItem(new String("3"));
			this.jComboBox_PassengerCount.addItem(new String("4"));
			this.jComboBox_PassengerCount.addItem(new String("5"));
			this.jComboBox_PassengerCount.addItem(new String("6"));
			this.jComboBox_PassengerCount.addItem(new String("7"));
			this.jComboBox_PassengerCount.addItem(new String("8"));
		}
		return this.jComboBox_PassengerCount;
	}
	

	/**
	 * @return Returns the jLabel_passengerCount.
	 */
	public JLabel getJLabel_passengerCount() {
		if (this.jLabel_passengerCount == null) {
			this.jLabel_passengerCount = new JLabel();
			this.jLabel_passengerCount.setText("min passengercount:");  // Generated
		}
		return this.jLabel_passengerCount;
	}

	/**
	 * This method initializes jTextField_WhereTo	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_WhereTo() {
		if (this.jTextField_WhereTo == null) {
			this.jTextField_WhereTo = new JTextField();
		}
		return this.jTextField_WhereTo;
	}

	public String getWho() {
		return this.getJTextField_Who().getText();
	}

	public String getWhenFrom() {
		return ((Date)this.getJTextField_WhenFrom().getValue()).toString();
	}
	
	public String getWhenTo() {
		return ((Date)this.getJTextField_WhenTo().getValue()).toString();
	}
	
	public String getWhereFrom() {
		return this.getJTextField_Where().getText();
	}
	
	public String getWhereTo() {
		return this.getJTextField_WhereTo().getText();
	}
	
	public String getCount() {
		return (String)this.getJComboBox_PassengerCount().getSelectedItem();
	}
	
	public DateSpec getDateSpec() {
		return new DateSpec((Date)getJTextField_WhenFrom().getValue(), (Date)getJTextField_WhenTo().getValue());
		// TODO HOLIDAY use swing calender element or date format to parse
//		return new DateSpec(new Date(getWhenFrom()), new Date(getWhenTo()));
	}
	
	public LocSpec getLocSpec() {
		return new LocSpec(getWhereFrom(), getWhereTo());
	}
	
	public CarRequirements getCarRequirements() {
		return new CarRequirements("carClass", new PositiveInteger(this.getCount()));
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
