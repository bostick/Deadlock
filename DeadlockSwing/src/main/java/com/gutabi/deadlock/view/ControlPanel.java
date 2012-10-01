package com.gutabi.deadlock.view;


import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel {
	
	public JCheckBox normalCarButton = new JCheckBox("Normal Cars");
	public JCheckBox fastCarButton = new JCheckBox("Fast Cars");
	public JButton startButton;
	public JButton stopButton;
	
	public ControlPanel() {
		setSize(new Dimension(156, 822));
		setPreferredSize(new Dimension(156, 822));
		setMaximumSize(new Dimension(156, 822));
	}
	
	public void init() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(Box.createRigidArea(new Dimension(0, 30)));
		
		Box hBox = Box.createHorizontalBox();
		hBox.add(new JLabel("Simulation Init:"));
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		normalCarButton = new JCheckBox("Normal Cars");
		normalCarButton.setSelected(true);
		hBox = Box.createHorizontalBox();
		hBox.add(normalCarButton);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		hBox = Box.createHorizontalBox();
		fastCarButton = new JCheckBox("Fast Cars");
		fastCarButton.setSelected(true);
		hBox.add(fastCarButton);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		startButton = new JButton("Start");
		startButton.setActionCommand("start");
		startButton.addActionListener(CONTROLLER);
		stopButton = new JButton("Stop");
		stopButton.setActionCommand("stop");
		stopButton.addActionListener(CONTROLLER);
		stopButton.setEnabled(false);
		hBox = Box.createHorizontalBox();
		hBox.add(startButton);
		hBox.add(stopButton);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		verticalBox.add(Box.createRigidArea(new Dimension(0, 30)));
		hBox = Box.createHorizontalBox();
		hBox.add(new JLabel("Simulation State:"));
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		verticalBox.add(Box.createVerticalGlue());
		
		verticalBox.add(Box.createRigidArea(new Dimension(0, 30)));
		
		add(verticalBox);
		
	}
	
}
