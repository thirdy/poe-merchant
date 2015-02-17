/**
 * Copyright (C) 2015  Vicente de Rivera III
 *
 * This file is part of poe-merchant.
 * 
 *    poe-merchant is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 * 
 *    poe-merchant is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 * 
 *    You should have received a copy of the GNU General Public License
 *    along with poe-merchant.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.poemerchant.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JToggleButton;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JSlider;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import javax.swing.JButton;

/**
 * 
 *
 */
public class UIMockup extends JFrame {
	private JTextField txtLife;
	public UIMockup() {
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel lblSearch = new JLabel("Search:");
		panel.add(lblSearch);
		
		txtLife = new JTextField();
		txtLife.setText("Gloves Life~50 TriRes");
		panel.add(txtLife);
		txtLife.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		
		JLabel lblLife = new JLabel("Life: 50");
		panel_1.add(lblLife);
		
		JSlider slider = new JSlider();
		slider.setOrientation(SwingConstants.VERTICAL);
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		panel_1.add(slider);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.add(panel_2);
		
		JRadioButton rdbtnSingleResistance = new JRadioButton("Single Resistance");
		panel_2.add(rdbtnSingleResistance);
		
		JRadioButton rdbtnDoubleResistance = new JRadioButton("Double Resistance");
		panel_2.add(rdbtnDoubleResistance);
		
		JRadioButton rdbtnTripleResistance = new JRadioButton("Triple Resistance");
		rdbtnTripleResistance.setSelected(true);
		panel_2.add(rdbtnTripleResistance);
	}

}
