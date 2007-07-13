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

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;

public class RenterSearchAndResultPanel extends JPanel implements SearchListener {

	private static final long serialVersionUID = 1L;
	private JSplitPane jSplitPane = null;
	private RenterSearchPanel renterSearchPanel = null;
	public static RenterSearchResultPanel rentersearchResultPanel = null;

	/**
	 * This is the default constructor
	 */
	public RenterSearchAndResultPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(400, 400);
		this.setLayout(new BorderLayout());
		this.add(getJSplitPane(), BorderLayout.CENTER);  // Generated
	}

	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane() {
		if (this.jSplitPane == null) {
			this.jSplitPane = new JSplitPane();
			this.jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);  // Generated
			this.jSplitPane.setDividerSize(7);  // Generated
			this.jSplitPane.setTopComponent(getRenterSearchPanel());  // Generated
			this.jSplitPane.setBottomComponent(getRentersearchResultPanel());  // Generated
		}
		return this.jSplitPane;
	}

	/**
	 * This method initializes rentersearchResultPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private RenterSearchResultPanel getRentersearchResultPanel() {
		if (rentersearchResultPanel == null) {
			rentersearchResultPanel = new RenterSearchResultPanel();
		}
		return rentersearchResultPanel;
	}

	/**
	 * This method initializes renterSearchPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private RenterSearchPanel getRenterSearchPanel() {
		if (this.renterSearchPanel == null) {
			this.renterSearchPanel = new RenterSearchPanel();
			this.renterSearchPanel.addListener(this);
		}
		return this.renterSearchPanel;
	}

	public void searchFinished(SearchEvent e) {
		System.out.println("Search finished!");
		this.getRentersearchResultPanel().addTableOfferEntries(e.getToe());
	}

	public void searchClear() {
		this.getRentersearchResultPanel().clearTableOffers();
	}

}
