package com.gutabi.deadlock.swing.view;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel {
	
	public ControlPanel() {
		
		JButton b = new JButton("Start");
		
		add(b);
		
		//this.setPreferredSize(b.getSize());
		//this.setSize(new Dimension(1, 1));
		
	}
	
}
