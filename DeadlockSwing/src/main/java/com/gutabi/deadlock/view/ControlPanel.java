package com.gutabi.deadlock.view;


import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings({"serial", "static-access"})
public class ControlPanel extends JPanel {
	
	public JCheckBox normalCarButton;
	public JCheckBox fastCarButton;
	public JCheckBox randomCarButton;
	public JCheckBox reallyFastCarButton;
	
	public JButton startButton;
	public JButton stopButton;
	
	public JCheckBox fpsCheckBox;
	public JCheckBox debugCheckBox;
	public JTextField dtField;
	
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
		
		fastCarButton = new JCheckBox("Fast Cars");
		fastCarButton.setSelected(true);
		hBox = Box.createHorizontalBox();
		hBox.add(fastCarButton);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
//		randomCarButton = new JCheckBox("Random Cars");
//		randomCarButton.setSelected(true);
//		hBox = Box.createHorizontalBox();
//		hBox.add(randomCarButton);
//		hBox.add(Box.createHorizontalGlue());
//		verticalBox.add(hBox);
		
		reallyFastCarButton = new JCheckBox("Really Fast Cars");
		reallyFastCarButton.setSelected(true);
		hBox = Box.createHorizontalBox();
		hBox.add(reallyFastCarButton);
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
		
		fpsCheckBox = new JCheckBox("fps draw");
		fpsCheckBox.setSelected(MODEL.FPS_DRAW);
		fpsCheckBox.setActionCommand("fpsDraw");
		fpsCheckBox.addActionListener(CONTROLLER);
		hBox = Box.createHorizontalBox();
		hBox.add(fpsCheckBox);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		debugCheckBox = new JCheckBox("debug draw");
		debugCheckBox.setSelected(MODEL.DEBUG_DRAW);
		debugCheckBox.setActionCommand("debugDraw");
		debugCheckBox.addActionListener(CONTROLLER);
		hBox = Box.createHorizontalBox();
		hBox.add(debugCheckBox);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		dtField = new JTextField();
		dtField.setText(Double.toString(MODEL.dt));
		dtField.setMaximumSize(new Dimension(10000, 100));
		dtField.setActionCommand("dt");
		dtField.addActionListener(CONTROLLER);
		hBox = Box.createHorizontalBox();
		hBox.add(new JLabel("dt"));
		hBox.add(dtField);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		verticalBox.add(Box.createVerticalGlue());
		
		Box previewBox = Box.createHorizontalBox();
		//buttonBox.add(Box.createHorizontalGlue());
		previewBox.add(Box.createHorizontalGlue());
		previewBox.add(VIEW.previewPanel);
		//buttonBox.add(Box.createHorizontalGlue());
		previewBox.add(Box.createHorizontalGlue());
		verticalBox.add(previewBox);
		
		verticalBox.add(Box.createRigidArea(new Dimension(0, 30)));
		
		add(verticalBox);
	}
	
}
