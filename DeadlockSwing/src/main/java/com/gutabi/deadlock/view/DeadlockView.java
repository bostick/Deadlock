package com.gutabi.deadlock.view;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

public class DeadlockView {
	
	public JFrame frame;
	public WorldPanel panel;
	public ControlPanel controlPanel;
		
	public final Logger logger = Logger.getLogger(DeadlockView.class);
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public DeadlockView() {
		;
	}
	
	public void init() {
		frame = createFrame(false);
	}
	
	public JFrame createFrame(boolean fullScreen) {
		
		JFrame newFrame;
		
		newFrame = new JFrame("Deadlock Viewer");
		
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new WorldPanel();
		panel.setFocusable(true);
		
		controlPanel = new ControlPanel();
		
		Container cp = newFrame.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		cp.add(panel);
		cp.add(controlPanel);
		
		newFrame.pack();
		
		newFrame.setSize(WindowInfo.windowWidth(), WindowInfo.windowHeight());
		newFrame.setLocation(WindowInfo.windowX(), WindowInfo.windowY());
		
		return newFrame;
	}
	
	public void repaint() {
		frame.repaint();
	}
	
}
