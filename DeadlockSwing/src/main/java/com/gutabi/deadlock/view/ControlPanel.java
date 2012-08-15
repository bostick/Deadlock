package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel {
	
	public JButton simulationButton;
	
	public ControlPanel() {
		
		simulationButton = new JButton("Start");
		
		simulationButton.setPreferredSize(new Dimension(100, 20));
		simulationButton.setActionCommand("start");
		simulationButton.addActionListener(CONTROLLER);
		
		add(simulationButton);
	}
	
}
