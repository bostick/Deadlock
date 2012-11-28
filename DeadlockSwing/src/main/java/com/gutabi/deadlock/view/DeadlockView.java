package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Point;

@SuppressWarnings("static-access")
public class DeadlockView {
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public JFrame frame;
	public WorldCanvas canvas;
	public ControlPanel controlPanel;
	public PreviewPanel previewPanel;
	
	public static final double PIXELS_PER_METER_DEBUG = 32.0;
	public int worldOriginX;
	public int worldOriginY;
	
	public java.awt.Stroke worldStroke = new BasicStroke(0.05f);
	
	public BufferedImage sheet;
	public BufferedImage explosionSheet;
	
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
				
		worldOriginX = 0;
		worldOriginY = 0;
		
		sheet = ImageIO.read(new File("media\\sheet.png"));
		explosionSheet = ImageIO.read(new File("media\\explosionSheet.png"));
		
		quadrantGrass = new BufferedImage(
				metersToPixels(MODEL.QUADRANT_WIDTH),
				metersToPixels(MODEL.QUADRANT_HEIGHT),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D quadrantGrassG2 = VIEW.quadrantGrass.createGraphics();
		for (int i = 0; i < metersToPixels(MODEL.QUADRANT_WIDTH)/32; i++) {
			for (int j = 0; j < metersToPixels(MODEL.QUADRANT_HEIGHT)/32; j++) {
				quadrantGrassG2.drawImage(VIEW.sheet,
						32 * i, 32 * j, 32 * i + 32, 32 * j + 32,
						0, 224, 0+32, 224+32, null);
			}
		}
		
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
		return new Point((p.x + worldOriginX) / PIXELS_PER_METER_DEBUG, (p.y + worldOriginY) / PIXELS_PER_METER_DEBUG);
	}
	
	public int metersToPixels(double m) {
		return (int)(Math.round(m * PIXELS_PER_METER_DEBUG));
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.g2.setColor(Color.WHITE);
		ctxt.g2.fillRect(0, 0, 1427, 822);
		
		ctxt.g2.setStroke(worldStroke);
		
		AffineTransform origTrans = ctxt.g2.getTransform();
		
		ctxt.g2.translate(-worldOriginX, -worldOriginY);
		ctxt.g2.scale(PIXELS_PER_METER_DEBUG, PIXELS_PER_METER_DEBUG);
		
		MODEL.world.paint(ctxt);
		
		if (MODEL.stroke != null) {
			MODEL.stroke.paint(ctxt);
		}
		
		if (MODEL.cursor != null) {
			MODEL.cursor.paint(ctxt);
		}
		
		if (MODEL.FPS_DRAW) {
			
			ctxt.g2.translate(VIEW.worldOriginX / PIXELS_PER_METER_DEBUG, VIEW.worldOriginY / PIXELS_PER_METER_DEBUG);
			
			MODEL.stats.paint(ctxt);
		}
		
		ctxt.g2.setTransform(origTrans);
		
	}
	
	public void repaint() {
		assert !Thread.holdsLock(MODEL);
		
		Graphics2D g2 = (Graphics2D)canvas.bs.getDrawGraphics();
		
		paint(new RenderingContext(g2, RenderingContextType.CANVAS));
		
		canvas.bs.show();
		
		g2.dispose();
		
		controlPanel.repaint();
		
	}
	
	public void renderBackground() {
		assert !Thread.holdsLock(MODEL);
		
		
		canvasGrassImage = new BufferedImage(
				metersToPixels(MODEL.world.worldWidth),
				metersToPixels(MODEL.world.worldHeight),
				BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D canvasGrassImageG2 = canvasGrassImage.createGraphics();
		
		AffineTransform origBackgroundTransform = canvasGrassImageG2.getTransform();
		
		canvasGrassImageG2.scale(VIEW.PIXELS_PER_METER_DEBUG, VIEW.PIXELS_PER_METER_DEBUG);
		
		RenderingContext canvasGrassContext = new RenderingContext(canvasGrassImageG2, RenderingContextType.CANVAS);
		
		MODEL.world.map.renderBackground(canvasGrassContext);
		
		canvasGrassImageG2.setTransform(origBackgroundTransform);
		
		canvasGraphImage = new BufferedImage(
				VIEW.metersToPixels(MODEL.world.aabb.width),
				VIEW.metersToPixels(MODEL.world.aabb.height),
				BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D canvasGraphImageG2 = canvasGraphImage.createGraphics();
		
		canvasGraphImageG2.setStroke(VIEW.worldStroke);
		
		canvasGraphImageG2.translate(
				-VIEW.metersToPixels(MODEL.world.aabb.x),
				-VIEW.metersToPixels(MODEL.world.aabb.y));
		
		canvasGraphImageG2.scale(VIEW.PIXELS_PER_METER_DEBUG, VIEW.PIXELS_PER_METER_DEBUG);
		
		RenderingContext canvasGraphContext = new RenderingContext(canvasGraphImageG2, RenderingContextType.CANVAS);
		
		MODEL.world.graph.renderBackground(canvasGraphContext);
		
		canvasGrassImageG2.dispose();
		canvasGraphImageG2.dispose();
		
		
	
		
		
		previewImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D previewImageG2 = previewImage.createGraphics();
		
		previewImageG2.setColor(Color.WHITE);
		previewImageG2.fillRect(0, 0, 100, 100);
		
		previewImageG2.scale(100.0 / VIEW.metersToPixels(MODEL.world.worldWidth), 100.0 / VIEW.metersToPixels(MODEL.world.worldHeight));
		previewImageG2.scale(VIEW.PIXELS_PER_METER_DEBUG, VIEW.PIXELS_PER_METER_DEBUG);
		
		RenderingContext previewContext = new RenderingContext(previewImageG2, RenderingContextType.PREVIEW);
		
		MODEL.world.map.renderBackground(previewContext);
		
		MODEL.world.graph.renderBackground(previewContext);
		
		previewImageG2.dispose();
	}
	
}
