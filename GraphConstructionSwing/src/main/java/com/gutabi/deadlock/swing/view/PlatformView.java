package com.gutabi.deadlock.swing.view;

import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;

import javax.swing.JFrame;

public class PlatformView {
	
	//public static final DeadlockView VIEW = new DeadlockView();
	
	public boolean inited = false;
	
	public JFrame frame;
	public DeadlockPanel panel;
	
	public PlatformView() {
		//System.out.println("new PlatformView");
	}
	
	public void init() {
		
		VIEW.window = new SwingWindow();
		
		frame = createFrame(false);
		
		inited = true;
		
	}
	
	public JFrame createFrame(boolean fullScreen) {
		
		JFrame newFrame;
		
		newFrame = new JFrame("Deadlock Viewer");
		
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new DeadlockPanel();
		panel.setFocusable(true);
		
		newFrame.getContentPane().add(panel);
		
		newFrame.setSize(VIEW.window.windowWidth(), VIEW.window.windowHeight());
		newFrame.setLocation(VIEW.window.windowX(), VIEW.window.windowY());
		
		return newFrame;
	}
	
	public void repaint() {
		frame.repaint();
	}

}
