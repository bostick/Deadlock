package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import org.apache.log4j.Logger;

//@SuppressWarnings("static-access")
public class DeadlockView {
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public RootPaneContainer container;
	
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
	
	static final Logger logger = Logger.getLogger(DeadlockView.class);
	
	public void init() throws Exception {
		sheet = ImageIO.read(DeadlockView.class.getResource("/img/sheet.png"));
		explosionSheet = ImageIO.read(DeadlockView.class.getResource("/img/explosionSheet.png"));
		titleBackground = ImageIO.read(DeadlockView.class.getResource("/img/title_background.png"));
		title_white = ImageIO.read(DeadlockView.class.getResource("/img/title_white.png"));
		copyright = ImageIO.read(DeadlockView.class.getResource("/img/copyright.png"));
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
	
}
