package com.gutabi.deadlock;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Random;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.view.WindowInfo;

public class DeadlockApplication {
	
	public URL codebase;
	
	public RootPaneContainer container;
	
	public Random RANDOM = new Random(1);
	
//	public Screen screen;
	
	public static DeadlockApplication APP = new DeadlockApplication();
	
	public void setupFrame() {
		
		JFrame newFrame;
		
		newFrame = new JFrame("Deadlock Viewer");
		
		newFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		newFrame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		        APP.exit();
		    }
		});
		
		newFrame.setSize((int)(WindowInfo.windowDim().width), (int)(WindowInfo.windowDim().height));
		newFrame.setLocation((int)(WindowInfo.windowLoc().x), (int)(WindowInfo.windowLoc().y));
		
		container = newFrame;
	}
	
	public void setupApplet(JApplet app) {
		
		app.setSize((int)(WindowInfo.windowDim().width), (int)(WindowInfo.windowDim().height));
		app.setLocation((int)(WindowInfo.windowLoc().x), (int)(WindowInfo.windowLoc().y));
	}
	
	public void exit() {
		
		 try {
			 String pidstr = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		      String pid[] = pidstr.split("@");
		      Runtime.getRuntime().exec("taskkill /F /PID " + pid[0]).waitFor();
			  }
			  catch (Exception e) {
			  }
		
	}
	
}
