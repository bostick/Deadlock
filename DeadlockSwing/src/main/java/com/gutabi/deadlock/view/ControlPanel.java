package com.gutabi.deadlock.view;


import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel {
	
	public JButton simulationButton;
	
	public ControlPanel() {
		setSize(new Dimension(156, 822));
		setPreferredSize(new Dimension(156, 822));
		setMaximumSize(new Dimension(156, 822));
	}
	
	public void init() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		simulationButton = new JButton("Start");
		simulationButton.setActionCommand("start");
		simulationButton.addActionListener(CONTROLLER);
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(simulationButton);
		buttonBox.add(Box.createHorizontalGlue());
		
		Box previewBox = Box.createHorizontalBox();
		previewBox.add(Box.createHorizontalGlue());
		previewBox.add(VIEW.previewPanel);
		previewBox.add(Box.createHorizontalGlue());
		
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(Box.createRigidArea(new Dimension(0, 30)));
		verticalBox.add(buttonBox);
		verticalBox.add(Box.createVerticalGlue());
		verticalBox.add(previewBox);
		verticalBox.add(Box.createRigidArea(new Dimension(0, 30)));
		
		add(verticalBox);
		
	}
	
}
