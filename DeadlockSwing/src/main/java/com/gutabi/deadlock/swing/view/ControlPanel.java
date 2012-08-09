package com.gutabi.deadlock.swing.view;

import static com.gutabi.deadlock.swing.Main.PLATFORMCONTROLLER;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel {
	
	public ControlPanel() {
		
		JButton b = new JButton("Start");
		
		b.setPreferredSize(new Dimension(100, 20));
		b.setActionCommand("start");
		b.addActionListener(PLATFORMCONTROLLER);
		
		add(b);
		
		//this.setPreferredSize(b.getSize());
		//this.setSize(new Dimension(1, 1));
		
	}
	
}
