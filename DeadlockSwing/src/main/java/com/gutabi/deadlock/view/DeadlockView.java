package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.controller.KeyboardController;
import com.gutabi.deadlock.controller.MouseController;

//@SuppressWarnings("static-access")
public class DeadlockView {
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public RootPaneContainer container;
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
		
//		assert canvas.getWidth() == CANVAS_WIDTH;
//		assert canvas.getHeight() == CANVAS_HEIGHT;
		
		sheet = ImageIO.read(new URL(APP.codebase, "media/sheet.png"));
		explosionSheet = ImageIO.read(new URL(APP.codebase, "media\\explosionSheet.png"));
		titleBackground = ImageIO.read(new URL(APP.codebase, "media\\title_background.png"));
		title_white = ImageIO.read(new URL(APP.codebase, "media\\title_white.png"));
		copyright = ImageIO.read(new URL(APP.codebase, "media\\copyright.png"));
		
	}
	
	public void setupFrame() {
		
		JFrame newFrame;
		
		newFrame = new JFrame("Deadlock Viewer");
		
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
		canvas.setFocusable(true);
		
		MouseController mc = new MouseController();
		KeyboardController kc = new KeyboardController();
		
		canvas.addMouseListener(mc);
		canvas.addMouseMotionListener(mc);
		canvas.addKeyListener(kc);
		
		Container cp = container.getContentPane();
//		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		cp.add(canvas);
		
	}
	
	public void teardownCanvas(RootPaneContainer container) {
		
		Container cp = container.getContentPane();
		cp.remove(canvas);
		
		canvas = null;
		
	}
	
	public void setupCanvasAndControlPanel(RootPaneContainer container) {
		
		canvas = new Canvas();
		canvas.setFocusable(true);
		
		MouseController mc = new MouseController();
		KeyboardController kc = new KeyboardController();
		
		canvas.addMouseListener(mc);
		canvas.addMouseMotionListener(mc);
		canvas.addKeyListener(kc);
		
		previewPanel = new PreviewPanel();
		
		previewPanel.addMouseListener(mc);
		previewPanel.addMouseMotionListener(mc);
		previewPanel.addKeyListener(kc);
		
		controlPanel = new ControlPanel();
		controlPanel.init();
		controlPanel.addKeyListener(kc);
		
		Container cp = container.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		cp.add(canvas);
		cp.add(controlPanel);
		
	}
	
	public void teardownCanvasAndControlPanel(RootPaneContainer container) {
		
		Container cp = container.getContentPane();
		
		cp.remove(canvas);
		cp.remove(controlPanel);
		
		canvas = null;
		previewPanel = null;
		controlPanel = null;
		
	}
	
	public void postDisplay() {
		canvas.postDisplay();
	}
	
	private void paintCanvas(RenderingContext ctxt) {
		
		if (CONTROLLER.mode == ControlMode.MENU) {
			APP.menu.paint(ctxt);
		} else {
			APP.world.paint(ctxt);
		}
		
	}
	
	public void repaintCanvas() {
		assert !Thread.holdsLock(APP);
		
//		if (SwingUtilities.isEventDispatchThread()) {
//			if (CONTROLLER.mode == ControlMode.RUNNING) {
//				return;
//			}
//		}
		
		do {
			
			do {
				
				Graphics2D g2 = (Graphics2D)canvas.bs.getDrawGraphics();
				
				paintCanvas(new RenderingContext(g2, RenderingContextType.CANVAS));
				
				g2.dispose();
				
			} while (canvas.bs.contentsRestored());
			
			canvas.bs.show();
			
		} while (canvas.bs.contentsLost());
		
	}
	
	public void repaintControlPanel() {
		controlPanel.repaint();
	}
	
}
