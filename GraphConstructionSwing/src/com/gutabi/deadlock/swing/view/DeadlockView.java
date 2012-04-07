package com.gutabi.deadlock.swing.view;

import javax.swing.JFrame;

import com.gutabi.deadlock.swing.utils.WindowUtils;

public class DeadlockView {
	
	public static final DeadlockView VIEW = new DeadlockView();
	
	public boolean inited = false;
	
	public JFrame frame;
	public DeadlockPanel panel;
	
	public void init() {
		
		panel = new DeadlockPanel();
		
		frame = createFrame(false);
		
		inited = true;
		
	}
	
	public JFrame createFrame(boolean fullScreen) {
		
		JFrame newFrame;
		
		newFrame = new JFrame("Deadlock Viewer");
		
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		newFrame.getContentPane().add(panel);
		
		newFrame.setSize(WindowUtils.windowWidth(), WindowUtils.windowHeight());
		newFrame.setLocation(WindowUtils.windowX(), WindowUtils.windowY());
		
		return newFrame;
	}
	
	public void repaint() {
		frame.repaint();
	}

}
