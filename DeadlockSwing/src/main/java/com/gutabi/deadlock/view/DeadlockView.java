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
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.tree.AABB;

@SuppressWarnings("static-access")
public class DeadlockView {
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public JFrame frame;
	public WorldCanvas canvas;
	public ControlPanel controlPanel;
	public PreviewPanel previewPanel;
	
	public double PIXELS_PER_METER_DEBUG = 32.0;
	
	public AABB worldViewport;
	
	
//	public java.awt.Stroke worldStroke = new BasicStroke(0.05f);
	
	public BufferedImage sheet;
	public BufferedImage explosionSheet;
	public BufferedImage titleBackground;
	
	public BufferedImage quadrantGrass;
	public BufferedImage canvasGrassImage;
	public BufferedImage previewImage;
	public BufferedImage canvasGraphImage;
	
	public static Color LIGHTGREEN = new Color(128, 255, 128);
	public static Color DARKGREEN = new Color(0, 128, 0);
	
	public final Logger logger = Logger.getLogger(DeadlockView.class);
	
	public DeadlockView() {
		;
	}
	
	public void init() throws Exception {
		
		frame = createFrame(false);
		
		assert canvas.getWidth() == 1427;
		assert canvas.getHeight() == 822;
		
		worldViewport = new AABB(0, 0, 1427.0 / PIXELS_PER_METER_DEBUG, 822.0 / PIXELS_PER_METER_DEBUG);
		
		sheet = ImageIO.read(new File("media\\sheet.png"));
		explosionSheet = ImageIO.read(new File("media\\explosionSheet.png"));
		titleBackground = ImageIO.read(new File("media\\title_background.png"));
		
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
		
		canvasGrassImage = new BufferedImage(
				1427,
				822,
				BufferedImage.TYPE_INT_RGB);
		
		canvasGraphImage = new BufferedImage(
				1427,
				822,
				BufferedImage.TYPE_INT_ARGB);
		
	}
	
	public JFrame createFrame(boolean fullScreen) {
		
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
	
	public Point canvasToWorld(Point p) {
		return new Point(
				p.x / PIXELS_PER_METER_DEBUG + worldViewport.x,
				p.y / PIXELS_PER_METER_DEBUG + worldViewport.y);
	}
	
	public int metersToPixels(double m) {
		return (int)(Math.round(m * PIXELS_PER_METER_DEBUG));
	}
	
	private void paintCanvas(RenderingContext ctxt) {
		
		ctxt.setColor(Color.WHITE);
		ctxt.fillRect(0, 0, 1427, 822);
		
		if (CONTROLLER.mode == ControlMode.MENU) {
			
			PIXELS_PER_METER_DEBUG = 1.0;
			
			ctxt.paintImage(1427/2 - 800/2, 822/2 - 600/2, titleBackground, 0, 0, 800, 600, 0, 0, 800, 600);
			
			ctxt.setFont(new Font("Times", Font.PLAIN, 96));
			
			ctxt.paintString(1427/2 - 800/2, 822/2, "Deadlock");
			
		} else {
			
			AffineTransform origTrans = ctxt.getTransform();
			
			ctxt.scale(PIXELS_PER_METER_DEBUG);
			ctxt.translate(-worldViewport.x, -worldViewport.y);
			
			MODEL.world.paint(ctxt);
			
			if (MODEL.stroke != null) {
				MODEL.stroke.paint(ctxt);
			}
			
			if (MODEL.cursor != null) {
				MODEL.cursor.paint(ctxt);
			}
			
			if (MODEL.FPS_DRAW) {
				
//				ctxt.translate(worldViewport.x / PIXELS_PER_METER_DEBUG, worldViewport.y / PIXELS_PER_METER_DEBUG);
				ctxt.translate(worldViewport.x, worldViewport.y);
				
				MODEL.stats.paint(ctxt);
			}
			
			ctxt.setTransform(origTrans);
			
		}
		
	}
	
	public void repaint() {
		assert !Thread.holdsLock(MODEL);
		
		Graphics2D g2 = (Graphics2D)canvas.bs.getDrawGraphics();
		
		paintCanvas(new RenderingContext(g2, RenderingContextType.CANVAS));
		
		canvas.bs.show();
		
		g2.dispose();
		
		controlPanel.repaint();
		
	}
	
	public void renderWorldBackground() {
		assert !Thread.holdsLock(MODEL);
		
		Graphics2D canvasGrassImageG2 = canvasGrassImage.createGraphics();
		
		canvasGrassImageG2.setColor(Color.WHITE);
		canvasGrassImageG2.fillRect(0, 0, 1427, 822);
		
		canvasGrassImageG2.translate((int)(-VIEW.worldViewport.x * PIXELS_PER_METER_DEBUG), (int)(-VIEW.worldViewport.y * PIXELS_PER_METER_DEBUG));
		
		canvasGrassImageG2.scale(VIEW.PIXELS_PER_METER_DEBUG, VIEW.PIXELS_PER_METER_DEBUG);
		
		RenderingContext canvasGrassContext = new RenderingContext(canvasGrassImageG2, RenderingContextType.CANVAS);
		
		MODEL.world.map.renderBackground(canvasGrassContext);
		
		canvasGrassImageG2.dispose();
		
		
		
		
		Graphics2D canvasGraphImageG2 = canvasGraphImage.createGraphics();
		
		Composite orig = canvasGraphImageG2.getComposite();
		AlphaComposite c = AlphaComposite.getInstance(AlphaComposite.SRC, 0.0f);
		canvasGraphImageG2.setComposite(c);
		canvasGraphImageG2.setColor(new Color(0, 0, 0, 0));
		canvasGraphImageG2.fillRect(0, 0, 1427, 822);
		canvasGraphImageG2.setComposite(orig);
		
		canvasGraphImageG2.translate((int)((-VIEW.worldViewport.x) * PIXELS_PER_METER_DEBUG), (int)((-VIEW.worldViewport.y) * PIXELS_PER_METER_DEBUG));
		
		canvasGraphImageG2.scale(VIEW.PIXELS_PER_METER_DEBUG, VIEW.PIXELS_PER_METER_DEBUG);
		
		RenderingContext canvasGraphContext = new RenderingContext(canvasGraphImageG2, RenderingContextType.CANVAS);
		
		MODEL.world.graph.renderBackground(canvasGraphContext);
		
		canvasGraphImageG2.dispose();
		
		
	
		
		
		previewImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D previewImageG2 = previewImage.createGraphics();
		
		previewImageG2.setColor(Color.WHITE);
		previewImageG2.fillRect(0, 0, 100, 100);
		
		previewImageG2.scale(100.0 / MODEL.world.worldWidth, 100.0 / MODEL.world.worldHeight);
		
		RenderingContext previewContext = new RenderingContext(previewImageG2, RenderingContextType.PREVIEW);
		
		MODEL.world.map.renderBackground(previewContext);
		
		MODEL.world.graph.renderBackground(previewContext);
		
		previewImageG2.dispose();
	}
	
}
