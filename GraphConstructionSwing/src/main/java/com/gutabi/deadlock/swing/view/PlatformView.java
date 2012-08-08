package com.gutabi.deadlock.swing.view;

import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

public class PlatformView {
	
	public boolean inited = false;
	
	public JFrame frame;
	public WorldPanel panel;
	public ControlPanel controlPanel;
	
	public PlatformView() {
		;
	}
	
	public void init() {
		
		frame = createFrame(false);
		
		inited = true;
		
	}
	
	public JFrame createFrame(boolean fullScreen) {
		
		JFrame newFrame;
		
		newFrame = new JFrame("Deadlock Viewer");
		
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//newFrame.setLayout(manager);
		
		panel = new WorldPanel();
		panel.setFocusable(true);
		
		controlPanel = new ControlPanel();
		
		Container cp = newFrame.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		cp.add(panel);
		cp.add(controlPanel);
		
		newFrame.pack();
		
		newFrame.setSize(VIEW.window.windowWidth(), VIEW.window.windowHeight());
		newFrame.setLocation(VIEW.window.windowX(), VIEW.window.windowY());
		
		return newFrame;
	}
	
	public void repaint() {
		frame.repaint();
	}

}
