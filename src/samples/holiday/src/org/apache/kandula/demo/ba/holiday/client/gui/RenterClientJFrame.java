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

import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class RenterClientJFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JTabbedPane jTabbedPane = null;

	private RenterSearchAndResultPanel renterSearchAndResultPanel = null;

	public static BasketPanel basketPanel = null;

	/**
	 * @throws HeadlessException
	 */
	public RenterClientJFrame() throws HeadlessException {
		// TODO Auto-generated constructor stub
		super();
		initialize();
	}

	/**
	 * @param gc
	 */
	public RenterClientJFrame(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
		initialize();
	}

	/**
	 * @param title
	 * @throws HeadlessException
	 */
	public RenterClientJFrame(String title) throws HeadlessException {
		super(title);
		// TODO Auto-generated constructor stub
		initialize();
	}

	/**
	 * @param title
	 * @param gc
	 */
	public RenterClientJFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.exit(0);
			}
		});
		this.setSize(300, 500);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (this.jContentPane == null) {
			this.jContentPane = new JPanel();
			this.jContentPane.setLayout(new BorderLayout());
			this.jContentPane.add(getJTabbedPane(), BorderLayout.CENTER);  // Generated
		}
		return this.jContentPane;
	}

	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane() {
		if (this.jTabbedPane == null) {
			this.jTabbedPane = new JTabbedPane();
			this.jTabbedPane.addTab("Search for Offers", null, getRenterSearchAndResultPanel(), null);  // Generated
			this.jTabbedPane.addTab("Basket", null, getBasketPanel(), null);  // Generated
		}
		return this.jTabbedPane;
	}

	/**
	 * This method initializes renterSearchAndResultPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private RenterSearchAndResultPanel getRenterSearchAndResultPanel() {
		if (this.renterSearchAndResultPanel == null) {
			this.renterSearchAndResultPanel = new RenterSearchAndResultPanel();
		}
		return this.renterSearchAndResultPanel;
	}

	/**
	 * This method initializes basketPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private BasketPanel getBasketPanel() {
		if (basketPanel == null) {
			basketPanel = new BasketPanel();
		}
		return basketPanel;
	}

}
