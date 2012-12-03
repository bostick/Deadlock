package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.tree.AABB;

@SuppressWarnings("static-access")
public class DeadlockView {
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public static final int CANVAS_WIDTH = 1427;
	public static final int CANVAS_HEIGHT = 822;
	
	public static final int PREVIEW_WIDTH = 100;
	public static final int PREVIEW_HEIGHT = 100;
	
	public URL codebase;
//	private JFrame frame;
	public WorldCanvas canvas;
	public ControlPanel controlPanel;
	public PreviewPanel previewPanel;
	
	public double PIXELS_PER_METER_DEBUG = 32.0;
	
	public AABB worldViewport;
	
	public BufferedImage sheet;
	public BufferedImage explosionSheet;
	public BufferedImage titleBackground;
	
	public BufferedImage quadrantGrass;
	public BufferedImage canvasGrassImage;
	public BufferedImage previewImage;
	public BufferedImage canvasGraphImage;
	
//	public boolean inMiddleOfRenderGrass;
//	public boolean inMiddleOfRenderGraph;
	
	
	public static Color LIGHTGREEN = new Color(128, 255, 128);
	public static Color DARKGREEN = new Color(0, 128, 0);
	
	public final Logger logger = Logger.getLogger(DeadlockView.class);
	
	public DeadlockView() {
		;
	}
	
	public void init() throws Exception {
		
		assert canvas.getWidth() == CANVAS_WIDTH;
		assert canvas.getHeight() == CANVAS_HEIGHT;
		
		worldViewport = new AABB(
				-(CANVAS_WIDTH / PIXELS_PER_METER_DEBUG) / 2 + MODEL.world.worldWidth/2 ,
				-(CANVAS_HEIGHT / PIXELS_PER_METER_DEBUG) / 2 + MODEL.world.worldHeight/2,
				CANVAS_WIDTH / PIXELS_PER_METER_DEBUG,
				CANVAS_HEIGHT / PIXELS_PER_METER_DEBUG);
		
		sheet = ImageIO.read(new URL(codebase, "media/sheet.png"));
		explosionSheet = ImageIO.read(new URL(codebase, "media\\explosionSheet.png"));
		titleBackground = ImageIO.read(new URL(codebase, "media\\title_background.png"));
		
		quadrantGrass = new BufferedImage(
				512,
				512,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D quadrantGrassG2 = VIEW.quadrantGrass.createGraphics();
		for (int i = 0; i < metersToPixels(MODEL.QUADRANT_WIDTH)/32; i++) {
			for (int j = 0; j < metersToPixels(MODEL.QUADRANT_HEIGHT)/32; j++) {
				quadrantGrassG2.drawImage(VIEW.sheet,
						32 * i, 32 * j, 32 * i + 32, 32 * j + 32,
						0, 224, 0+32, 224+32, null);
			}
		}
		
		canvasGrassImage = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		canvasGraphImage = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
	}
	
	public JFrame setupFrame() {
		
		JFrame newFrame;
		
		newFrame = new JFrame("Deadlock Viewer");
		
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		canvas = new WorldCanvas();
		canvas.setFocusable(true);
		
		previewPanel = new PreviewPanel();
		
		controlPanel = new ControlPanel();
		controlPanel.init();
		
		Container cp = newFrame.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		cp.add(canvas);
		cp.add(controlPanel);
		
		newFrame.pack();
		
		newFrame.setSize((int)(WindowInfo.windowDim().width), (int)(WindowInfo.windowDim().height));
		newFrame.setLocation((int)(WindowInfo.windowLoc().x), (int)(WindowInfo.windowLoc().y));
		
		return newFrame;
	}
	
	public void setupApplet(JApplet app) {
		
//		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
//		app.setJMenuBar(null);
		
		canvas = new WorldCanvas();
		canvas.setFocusable(true);
		
		previewPanel = new PreviewPanel();
		
		controlPanel = new ControlPanel();
		controlPanel.init();
		
		Container cp = app.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		cp.add(canvas);
		cp.add(controlPanel);
		
//		app.pack();
		
		app.setSize((int)(WindowInfo.windowDim().width), (int)(WindowInfo.windowDim().height));
		app.setLocation((int)(WindowInfo.windowLoc().x), (int)(WindowInfo.windowLoc().y));
	}
	
	public Point canvasToWorld(Point p) {
		return new Point(
				p.x / PIXELS_PER_METER_DEBUG + worldViewport.x,
				p.y / PIXELS_PER_METER_DEBUG + worldViewport.y);
	}
	
	public int metersToPixels(double m) {
		return (int)(Math.round(m * PIXELS_PER_METER_DEBUG));
	}
	
	public Point previewToWorld(Point p) {
		return new Point((MODEL.world.worldWidth / PREVIEW_WIDTH) * p.x, (MODEL.world.worldHeight / PREVIEW_HEIGHT) * p.y);
	}
	
	public Point worldToPreview(Point p) {
		return new Point((PREVIEW_WIDTH / MODEL.world.worldWidth) * p.x, (PREVIEW_HEIGHT / MODEL.world.worldHeight) * p.y);
	}
	
	public void pan(Point prevDp) {
		Point worldDP = VIEW.previewToWorld(prevDp);
		
		VIEW.worldViewport = new AABB(
				VIEW.worldViewport.x + worldDP.x,
				VIEW.worldViewport.y + worldDP.y,
				VIEW.worldViewport.width,
				VIEW.worldViewport.height);
	}
	
	public void zoom(double factor) {
		
		Point center = new Point(VIEW.worldViewport.x + VIEW.worldViewport.width / 2, VIEW.worldViewport.y + VIEW.worldViewport.height / 2);
		
		VIEW.PIXELS_PER_METER_DEBUG = factor * VIEW.PIXELS_PER_METER_DEBUG; 
		
		double newWidth = CANVAS_WIDTH / VIEW.PIXELS_PER_METER_DEBUG;
		double newHeight = CANVAS_HEIGHT / VIEW.PIXELS_PER_METER_DEBUG;
		
		VIEW.worldViewport = new AABB(center.x - newWidth/2, center.y - newHeight/2, newWidth, newHeight);
		
	}
	
	private void paintCanvas(RenderingContext ctxt) {
//		assert SwingUtilities.isEventDispatchThread();
		
//		ctxt.setColor(Color.WHITE);
//		ctxt.fillRect(0, 0, CANVAS_WIDTH, VIEW.CANVAS_HEIGHT);
		
		if (CONTROLLER.mode == ControlMode.MENU) {
			
			PIXELS_PER_METER_DEBUG = 1.0;
			
			ctxt.paintImage(CANVAS_WIDTH/2 - 800/2, VIEW.CANVAS_HEIGHT/2 - 600/2, titleBackground, 0, 0, 800, 600, 0, 0, 800, 600);
			
			ctxt.setFont(new Font("Times", Font.PLAIN, 96));
			
			ctxt.paintString(CANVAS_WIDTH/2 - 800/2, VIEW.CANVAS_HEIGHT/2, 1.0, "Deadlock");
			
		} else {
			
			AffineTransform origTrans = ctxt.getTransform();
			
			ctxt.scale(PIXELS_PER_METER_DEBUG);
			ctxt.translate(-worldViewport.x, -worldViewport.y);
			
			MODEL.world.paint(ctxt);
			
			if (MODEL.stroke != null) {
				MODEL.stroke.paint(ctxt);
			}
			
			if (MODEL.cursor != null) {
				MODEL.cursor.draw(ctxt);
			}
			
			if (MODEL.FPS_DRAW) {
				
//				ctxt.translate(worldViewport.x / PIXELS_PER_METER_DEBUG, worldViewport.y / PIXELS_PER_METER_DEBUG);
				ctxt.translate(worldViewport.x, worldViewport.y);
				
				MODEL.stats.paint(ctxt);
			}
			
			ctxt.setTransform(origTrans);
			
		}
		
	}
	
//	private Runnable repaintRunner = new Runnable() {
//		public void run() {
//			repaintCanvas();
//		}
//	};
//	
//	public void repaintCanvasOnEDT() {
////		assert !SwingUtilities.isEventDispatchThread();
//		SwingUtilities.invokeLater(repaintRunner);
//	}
	
	public void repaintCanvas() {
//		assert SwingUtilities.isEventDispatchThread();
		assert !Thread.holdsLock(MODEL);
		
		if (SwingUtilities.isEventDispatchThread()) {
			if (CONTROLLER.mode == ControlMode.RUNNING) {
				return;
			}
		}
		
		do {
			
			do {
				
				Graphics2D g2 = (Graphics2D)canvas.bs.getDrawGraphics();
				
				paintCanvas(new RenderingContext(g2, RenderingContextType.CANVAS));
				
				g2.dispose();
				
			} while (canvas.bs.contentsRestored());
			
			canvas.bs.show();
//			canvas.repaint();
			
		} while (canvas.bs.contentsLost());
		
	}
	
	public void repaintControlPanel() {
//		assert SwingUtilities.isEventDispatchThread();
		controlPanel.repaint();
	}
	
	public void renderWorldBackground() {
//		assert !SwingUtilities.isEventDispatchThread();
		assert !Thread.holdsLock(MODEL);
		
		synchronized (VIEW) {
			Graphics2D canvasGrassImageG2 = canvasGrassImage.createGraphics();
			
			canvasGrassImageG2.setColor(Color.WHITE);
			canvasGrassImageG2.fillRect(0, 0, CANVAS_WIDTH, VIEW.CANVAS_HEIGHT);
			
			canvasGrassImageG2.translate((int)(-VIEW.worldViewport.x * PIXELS_PER_METER_DEBUG), (int)(-VIEW.worldViewport.y * PIXELS_PER_METER_DEBUG));
			
			canvasGrassImageG2.scale(VIEW.PIXELS_PER_METER_DEBUG, VIEW.PIXELS_PER_METER_DEBUG);
			
			RenderingContext canvasGrassContext = new RenderingContext(canvasGrassImageG2, RenderingContextType.CANVAS);
			
			MODEL.world.map.renderBackground(canvasGrassContext);
			
			canvasGrassImageG2.dispose();
		}
		
		synchronized (VIEW) {
			Graphics2D canvasGraphImageG2 = canvasGraphImage.createGraphics();
			
			Composite orig = canvasGraphImageG2.getComposite();
			AlphaComposite c = AlphaComposite.getInstance(AlphaComposite.SRC, 0.0f);
			canvasGraphImageG2.setComposite(c);
			canvasGraphImageG2.setColor(new Color(0, 0, 0, 0));
			canvasGraphImageG2.fillRect(0, 0, CANVAS_WIDTH, VIEW.CANVAS_HEIGHT);
			canvasGraphImageG2.setComposite(orig);
			
			canvasGraphImageG2.translate((int)((-VIEW.worldViewport.x) * PIXELS_PER_METER_DEBUG), (int)((-VIEW.worldViewport.y) * PIXELS_PER_METER_DEBUG));
			
			canvasGraphImageG2.scale(VIEW.PIXELS_PER_METER_DEBUG, VIEW.PIXELS_PER_METER_DEBUG);
			
			RenderingContext canvasGraphContext = new RenderingContext(canvasGraphImageG2, RenderingContextType.CANVAS);
			
			MODEL.world.graph.renderBackground(canvasGraphContext);
			
			canvasGraphImageG2.dispose();
		}
		
		previewImage = new BufferedImage(PREVIEW_WIDTH, PREVIEW_HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D previewImageG2 = previewImage.createGraphics();
		
		previewImageG2.setColor(Color.WHITE);
		previewImageG2.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		
		previewImageG2.scale(PREVIEW_WIDTH / MODEL.world.worldWidth, PREVIEW_HEIGHT / MODEL.world.worldHeight);
		
		RenderingContext previewContext = new RenderingContext(previewImageG2, RenderingContextType.PREVIEW);
		
		MODEL.world.map.renderBackground(previewContext);
		
		MODEL.world.graph.renderBackground(previewContext);
		
		previewImageG2.dispose();
	}
	
}
