package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel {
	
	public ControlPanel() {
		
		JButton b = new JButton("Start");
		
		b.setPreferredSize(new Dimension(100, 20));
		b.setActionCommand("start");
		b.addActionListener(CONTROLLER);
		
		add(b);
		
		//this.setPreferredSize(b.getSize());
		//this.setSize(new Dimension(1, 1));
		
	}
	
}
