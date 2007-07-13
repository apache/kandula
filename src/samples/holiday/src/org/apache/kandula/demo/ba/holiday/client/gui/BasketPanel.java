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
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.kandula.demo.ba.holiday.client.gui.BasketEvent.EventType;

import java.awt.BorderLayout;
import java.util.ArrayList;

/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class BasketPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JPanel btnPanel = null;
	private JButton complete_Button = null;
	private JButton cancel_Button = null;
	private JButton compensate_Button = null;
	private JButton close_Button = null;
	private JScrollPane jScrollPane = null;
	private JTable basketTable = null;
	private JButton refresh_Button = null;

	private ArrayList listeners = new ArrayList();  //  @jve:decl-index=0:
	private BasketTableModel basketTableModel ;

	private String mode;  //  @jve:decl-index=0:
	public static final String MODE_ATOMIC = "ATOMIC";  //  @jve:decl-index=0:
	public static final String MODE_MIXED = "MIXED";
	
	/**
	 * 
	 */
	public BasketPanel() {
		// TODO Auto-generated constructor stub
		super();
		initialize();
	}

	/**
	 * @param layout
	 */
	public BasketPanel(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
		initialize();
	}

	/**
	 * @param isDoubleBuffered
	 */
	public BasketPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
		initialize();
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public BasketPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new BorderLayout());
		this.add(getBtnPanel(), BorderLayout.SOUTH);  // Generated
		this.add(getJScrollPane(), BorderLayout.CENTER);  // Generated
	}

	/**
	 * This method initializes btnPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getBtnPanel() {
		if (this.btnPanel == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 2;  // Generated
			gridBagConstraints21.fill = GridBagConstraints.BOTH;  // Generated
			gridBagConstraints21.gridheight = 2;  // Generated
			gridBagConstraints21.gridy = 0;  // Generated
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 1;  // Generated
			gridBagConstraints11.fill = GridBagConstraints.BOTH;  // Generated
			gridBagConstraints11.gridy = 1;  // Generated
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;  // Generated
			gridBagConstraints2.gridy = 1;  // Generated
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;  // Generated
			gridBagConstraints1.fill = GridBagConstraints.BOTH;  // Generated
			gridBagConstraints1.gridy = 0;  // Generated
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;  // Generated
			gridBagConstraints.gridy = 0;  // Generated
			this.btnPanel = new JPanel();
			this.btnPanel.setLayout(new GridBagLayout());  // Generated
			this.btnPanel.add(getComplete_Button(), gridBagConstraints);  // Generated
			this.btnPanel.add(getCancel_Button(), gridBagConstraints1);  // Generated
			this.btnPanel.add(getCompensate_Button(), gridBagConstraints2);  // Generated
			this.btnPanel.add(getClose_Button(), gridBagConstraints11);  // Generated
			this.btnPanel.add(getRefresh_Button(), gridBagConstraints21);  // Generated
		}
		return this.btnPanel;
	}

	/**
	 * This method initializes complete_Button	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getComplete_Button() {
		if (this.complete_Button == null) {
			this.complete_Button = new JButton();
			this.complete_Button.setText("Complete");  // Generated
			this.complete_Button.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("Complete actionPerformed(): " + e.getActionCommand());
					BasketPanel.this.fireBasketEvent(BasketEvent.EventType.COMPLETE);
				}
			});
		}
		return this.complete_Button;
	}

	/**
	 * This method initializes cancel_Button	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancel_Button() {
		if (this.cancel_Button == null) {
			this.cancel_Button = new JButton();
			this.cancel_Button.setText("Cancel");  // Generated
			this.cancel_Button.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("Cancel actionPerformed(): " + e.getActionCommand());
					BasketPanel.this.fireBasketEvent(BasketEvent.EventType.CANCEL);
				}
			});
		}
		return this.cancel_Button;
	}

	/**
	 * This method initializes compensate_Button	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCompensate_Button() {
		if (this.compensate_Button == null) {
			this.compensate_Button = new JButton();
			this.compensate_Button.setText("Compensate");  // Generated
			this.compensate_Button.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("Compensate actionPerformed(): " + e.getActionCommand());
					BasketPanel.this.fireBasketEvent(BasketEvent.EventType.COMPENSATE);
				}
			});
		}
		return this.compensate_Button;
	}

	/**
	 * This method initializes close_Button	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getClose_Button() {
		if (this.close_Button == null) {
			this.close_Button = new JButton();
			this.close_Button.setText("Close");  // Generated
			this.close_Button.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("Close actionPerformed(): " + e.getActionCommand());
					BasketPanel.this.fireBasketEvent(BasketEvent.EventType.CLOSE);
				}
			});
		}
		return this.close_Button;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (this.jScrollPane == null) {
			this.jScrollPane = new JScrollPane();
			this.jScrollPane.setViewportView(getBasketTable());  // Generated
		}
		return this.jScrollPane;
	}

	/**
	 * This method initializes basketTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getBasketTable() {
		if (this.basketTable == null) {
			this.basketTable = new JTable();
			this.basketTableModel = new BasketTableModel();
			this.basketTable.setModel(this.basketTableModel);
			this.basketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			this.basketTable.getTableHeader().setReorderingAllowed(false);
			this.basketTable.setDefaultRenderer(Object.class, new BasketEntryCellRenderer(this.basketTableModel));
		}
		return this.basketTable;
	}

	public void addBasketEntry(BasketEntry be) {
		this.basketTableModel.addBasketEntry(be);
	}

	/**
	 * This method initializes refresh_Button	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRefresh_Button() {
		if (this.refresh_Button == null) {
			this.refresh_Button = new JButton();
			this.refresh_Button.setText("<html><center>Refresh basket</html>");  // Generated

			this.refresh_Button.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("Refresh actionPerformed(): " + e.getActionCommand());
					BasketPanel.this.fireBasketEvent(BasketEvent.EventType.REFRESH);
				}
			});
		}
		return this.refresh_Button;
	}
	
	public void addListener(BasketListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(BasketListener listener) {
		this.listeners.remove(listener);
	}
	
	public void fireBasketEvent(BasketEvent.EventType type) {
		if (this.basketTable.getSelectedRows().length > 0) {
			final int[] rows = this.basketTable.getSelectedRows();
			final BasketEntry[] be = ((BasketTableModel)this.basketTable.getModel()).getRows(rows);

			final BasketEvent e = new BasketEvent(this, be, type);
			
			for(int i = 0; i<this.listeners.size(); i++) {
				BasketListener listener = (BasketListener)this.listeners.get(i);
				listener.basketEventFired(e);
			}
		} else if (EventType.REFRESH.equals(type)
				|| (
						getMode().equals(MODE_ATOMIC) && 
						(EventType.COMPENSATE.equals(type) || EventType.CLOSE.equals(type))
				)){
			for(int i = 0; i<this.listeners.size(); i++) {
				final BasketEvent e = new BasketEvent(this, null, type);
				
				BasketListener listener = (BasketListener)this.listeners.get(i);
				listener.basketEventFired(e);
			}			
		}
		this.basketTableModel.fireTableDataChanged();
	}

	public BasketTableModel getBasketTableModel() {
		return this.basketTableModel;
	}

	/**
	 * @return Returns the mode.
	 */
	public String getMode() {
		return this.mode;
	}

	/**
	 * @param mode The mode to set.
	 */
	public void setMode(String mode) {
		if (mode.equals(MODE_ATOMIC) || mode.equals(MODE_MIXED)) {
			this.mode = mode;
			handleModeChange();
		} else
			throw new IllegalArgumentException("Mode not supported! " + mode);
	}
	
	private void handleModeChange(){
		if (getMode().equals(MODE_ATOMIC)) {
			getBtnPanel().removeAll();
			getCompensate_Button().setText("<html><center>Cancel or compensate all</html>");
			getClose_Button().setText("Close all");
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 2;  // Generated
			gridBagConstraints21.fill = GridBagConstraints.BOTH;  // Generated
			gridBagConstraints21.gridheight = 2;  // Generated
			gridBagConstraints21.gridy = 0;  // Generated
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 1;  // Generated
			gridBagConstraints11.fill = GridBagConstraints.BOTH;  // Generated
			gridBagConstraints11.gridy = 1;  // Generated
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridheight = 2;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;  // Generated
			gridBagConstraints.gridy = 0;  // Generated
			this.btnPanel.add(getComplete_Button(), gridBagConstraints);  // Generated
			this.btnPanel.add(getCompensate_Button(), gridBagConstraints2);  // Generated
			this.btnPanel.add(getClose_Button(), gridBagConstraints11);  // Generated
			this.btnPanel.add(getRefresh_Button(), gridBagConstraints21);  // Generated
		} else if (getMode().equals(MODE_MIXED)) {
			getCompensate_Button().setText("Compensate");
			getClose_Button().setText("Close");
			getBtnPanel().removeAll();
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 2;  // Generated
			gridBagConstraints21.fill = GridBagConstraints.BOTH;  // Generated
			gridBagConstraints21.gridheight = 2;  // Generated
			gridBagConstraints21.gridy = 0;  // Generated
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 1;  // Generated
			gridBagConstraints11.fill = GridBagConstraints.BOTH;  // Generated
			gridBagConstraints11.gridy = 1;  // Generated
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;  // Generated
			gridBagConstraints2.gridy = 1;  // Generated
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;  // Generated
			gridBagConstraints1.fill = GridBagConstraints.BOTH;  // Generated
			gridBagConstraints1.gridy = 0;  // Generated
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;  // Generated
			gridBagConstraints.gridy = 0;  // Generated
			this.btnPanel.add(getComplete_Button(), gridBagConstraints);  // Generated
			this.btnPanel.add(getCancel_Button(), gridBagConstraints1);  // Generated
			this.btnPanel.add(getCompensate_Button(), gridBagConstraints2);  // Generated
			this.btnPanel.add(getClose_Button(), gridBagConstraints11);  // Generated
			this.btnPanel.add(getRefresh_Button(), gridBagConstraints21);  // Generated
		}
	}

}
