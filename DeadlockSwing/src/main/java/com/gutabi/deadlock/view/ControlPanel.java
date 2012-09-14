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
		//simulationButton.setPreferredSize(null);
		simulationButton.setActionCommand("start");
		simulationButton.addActionListener(CONTROLLER);
		//simulationButton.setBackground(Color.RED);
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		//buttonBox.add(Box.createRigidArea(new Dimension(50, 0)));
		buttonBox.add(simulationButton);
		buttonBox.add(Box.createHorizontalGlue());
		//buttonBox.add(Box.createRigidArea(new Dimension(50, 0)));
		
		Box previewBox = Box.createHorizontalBox();
		//buttonBox.add(Box.createHorizontalGlue());
		previewBox.add(Box.createHorizontalGlue());
		previewBox.add(VIEW.previewPanel);
		//buttonBox.add(Box.createHorizontalGlue());
		previewBox.add(Box.createHorizontalGlue());
		
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(Box.createRigidArea(new Dimension(0, 30)));
		verticalBox.add(buttonBox);
		verticalBox.add(Box.createVerticalGlue());
		verticalBox.add(previewBox);
		verticalBox.add(Box.createRigidArea(new Dimension(0, 30)));
		
		add(verticalBox);
		
		
		
		
		
		
		
		
//		simulationButton = new JButton("Start");
//		
//		simulationButton.setPreferredSize(new Dimension(200, 20));
//		simulationButton.setActionCommand("start");
//		simulationButton.addActionListener(CONTROLLER);
//		
//		add(simulationButton);
//		
//		
//		
//		
//		
//		JPanel pdfLibPanel = new JPanel();
//		pdfLibPanel.setLayout(new BoxLayout(pdfLibPanel, BoxLayout.PAGE_AXIS));
//		pdfLibPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
//		
//		JLabel lab = new JLabel("PDF Library");
//		
//		Box labBox = Box.createHorizontalBox();
//		labBox.add(lab);
//		labBox.add(Box.createHorizontalGlue());
//		
//		Box verticalBox = Box.createVerticalBox();
//		verticalBox.add(Box.createVerticalGlue());
//		verticalBox.add(labBox);
//		verticalBox.add(pdfLibList);
//		
//		pdfLibPanel.add(verticalBox);
		
		
		
	}
	
}
