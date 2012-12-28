package com.gutabi.deadlock;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.ui.WindowInfo;

public class DeadlockApplication {
	
	public URL codebase;
	
	public RootPaneContainer container;
	
	public Random RANDOM = new Random(1);
	
	public BufferedImage sheet;
	public BufferedImage explosionSheet;
	public BufferedImage titleBackground;
	public BufferedImage title_white;
	public BufferedImage copyright;
	
	public Color LIGHTGREEN = new Color(128, 255, 128);
	public Color DARKGREEN = new Color(0, 128, 0);
	
	public Color menuBackground = new Color(0x88, 0x88, 0x88);
	
	public Color brown = new Color(150, 75, 0);
	
	public Color redOrange = new Color(255, 67, 0);
	
	public Color fixtureHiliteColor = new Color(0, 255, 255);
	
	public Color roadHiliteColor = new Color(0xff ^ 0x88, 0xff ^ 0x88, 0xff ^ 0x88, 0xff);
	
//	public Screen screen;
	
	public static DeadlockApplication APP = new DeadlockApplication();
	
	public void init() throws Exception {
		sheet = ImageIO.read(DeadlockApplication.class.getResource("/img/sheet.png"));
		explosionSheet = ImageIO.read(DeadlockApplication.class.getResource("/img/explosionSheet.png"));
		titleBackground = ImageIO.read(DeadlockApplication.class.getResource("/img/title_background.png"));
		title_white = ImageIO.read(DeadlockApplication.class.getResource("/img/title_white.png"));
		copyright = ImageIO.read(DeadlockApplication.class.getResource("/img/copyright.png"));
	}
	
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
