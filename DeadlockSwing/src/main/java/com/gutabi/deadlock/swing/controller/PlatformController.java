package com.gutabi.deadlock.swing.controller;

import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.swing.Main.PLATFORMVIEW;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class PlatformController implements ActionListener {
	
	public MouseController mc;
	public KeyboardController kc;
	
	public void init() {
		
		mc = new MouseController();
		
		kc = new KeyboardController();
		
		PLATFORMVIEW.panel.addMouseListener(mc);
		PLATFORMVIEW.panel.addMouseMotionListener(mc);
		PLATFORMVIEW.panel.addKeyListener(kc);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("start")) {
			JButton b = (JButton)e.getSource();
			b.setText("Stop");
			b.setActionCommand("stop");
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.startRunning();
				}}
			);
			
		} else if (e.getActionCommand().equals("stop")) {
			JButton b = (JButton)e.getSource();
			b.setText("Start");
			b.setActionCommand("start");
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.stopRunning();
				}}
			);
		}
	}
	
}
