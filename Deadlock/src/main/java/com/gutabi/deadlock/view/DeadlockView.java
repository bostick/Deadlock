package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import org.apache.log4j.Logger;

//@SuppressWarnings("static-access")
public class DeadlockView {
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public RootPaneContainer container;
	
	public Canvas oldCanvas;
	public Canvas canvas;
	
	public ControlPanel controlPanel;
	public PreviewPanel previewPanel;
	
	public BufferedImage sheet;
	public BufferedImage explosionSheet;
	public BufferedImage titleBackground;
	public BufferedImage title_white;
	public BufferedImage copyright;
	
	public static Color LIGHTGREEN = new Color(128, 255, 128);
	public static Color DARKGREEN = new Color(0, 128, 0);
	
	public final Logger logger = Logger.getLogger(DeadlockView.class);
	
	public void init() throws Exception {
//		sheet = ImageIO.read(new URL(APP.codebase, "img/sheet.png"));
//		explosionSheet = ImageIO.read(new URL(APP.codebase, "img/explosionSheet.png"));
//		titleBackground = ImageIO.read(new URL(APP.codebase, "img/title_background.png"));
//		title_white = ImageIO.read(new URL(APP.codebase, "img/title_white.png"));
//		copyright = ImageIO.read(new URL(APP.codebase, "img/copyright.png"));
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
		
		setupCanvas(newFrame);
		
		newFrame.setSize((int)(WindowInfo.windowDim().width), (int)(WindowInfo.windowDim().height));
		newFrame.setLocation((int)(WindowInfo.windowLoc().x), (int)(WindowInfo.windowLoc().y));
		
		container = newFrame;
	}
	
	public void setupApplet(JApplet app) {
		
		setupCanvas(app);
		
		app.setSize((int)(WindowInfo.windowDim().width), (int)(WindowInfo.windowDim().height));
		app.setLocation((int)(WindowInfo.windowLoc().x), (int)(WindowInfo.windowLoc().y));
	}
	
	public void setupCanvas(RootPaneContainer container) {
		
		canvas = new Canvas();
		
		Container cp = container.getContentPane();
		cp.add(canvas.java());
	}
	
	public void teardownCanvas(RootPaneContainer container) {
		
		Container cp = container.getContentPane();
		cp.remove(canvas.java());
		
		oldCanvas = canvas;
		canvas = null;
	}
	
	public void setupControlPanel(RootPaneContainer container) {
		
//		canvas = new Canvas();
		
		previewPanel = new PreviewPanel();
		
		controlPanel = new ControlPanel();
		controlPanel.init();
		
		Container cp = container.getContentPane();
//		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
//		cp.add(canvas.java());
		cp.add(controlPanel);
		
		cp.validate();
		
	}
	
	public void setupCanvasAndControlPanel(RootPaneContainer container) {
		
		canvas = new Canvas();
		
		previewPanel = new PreviewPanel();
		
		controlPanel = new ControlPanel();
		controlPanel.init();
		
		Container cp = container.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		cp.add(canvas.java());
		cp.add(controlPanel);
	}
	
	public void teardownCanvasAndControlPanel(RootPaneContainer container) {
		
		Container cp = container.getContentPane();
		
		cp.remove(canvas.java());
		cp.remove(controlPanel);
		
		oldCanvas = canvas;
		canvas = null;
		previewPanel = null;
		controlPanel = null;
		
	}
	
}
