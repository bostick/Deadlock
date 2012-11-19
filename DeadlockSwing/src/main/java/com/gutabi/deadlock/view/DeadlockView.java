package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Rect;
import com.gutabi.deadlock.model.World;

@SuppressWarnings("static-access")
public class DeadlockView {
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public JFrame frame;
//	public WorldPanel panel;
	public WorldCanvas canvas;
	public ControlPanel controlPanel;
	
	/*
	 * where in the panel (in pixels) is the world origin?
	 */
	public int worldOriginX;
	public int worldOriginY;
	
	public int worldAABBX;
	public int worldAABBY;
	
	public java.awt.Stroke worldStroke = new BasicStroke(0.05f);
	
	public BufferedImage sheet;
	public BufferedImage explosionSheet;
	public BufferedImage tiledGrass;
	
	public final Logger logger = Logger.getLogger(DeadlockView.class);
	
	public DeadlockView() {
		;
	}
	
	public void init() throws Exception {
		frame = createFrame(false);
		
		worldOriginX = (int)(canvas.getWidth() * 0.5 - (MODEL.world.WORLD_WIDTH * 0.5 * MODEL.PIXELS_PER_METER));
		worldOriginY = (int)(canvas.getHeight() * 0.5 - (MODEL.world.WORLD_HEIGHT * 0.5 * MODEL.PIXELS_PER_METER));
		
		sheet = ImageIO.read(new File("media\\sheet.png"));
		explosionSheet = ImageIO.read(new File("media\\explosionSheet.png"));
		
		tiledGrass = new BufferedImage(
				(int)(World.WORLD_WIDTH * MODEL.PIXELS_PER_METER),
				(int)(World.WORLD_HEIGHT * MODEL.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = tiledGrass.createGraphics();
		
		for (int i = 0; i < (World.WORLD_WIDTH * MODEL.PIXELS_PER_METER)/World.GRASS_WIDTH; i++) {
			for (int j = 0; j < (World.WORLD_HEIGHT * MODEL.PIXELS_PER_METER)/World.GRASS_HEIGHT; j++) {
				g2.drawImage(sheet,
						World.GRASS_WIDTH * i, World.GRASS_HEIGHT * j, World.GRASS_WIDTH * i + World.GRASS_WIDTH, World.GRASS_HEIGHT * j + World.GRASS_HEIGHT,
						0, 224, 0+World.GRASS_WIDTH, 224+World.GRASS_HEIGHT, null);
			}
		} 
		
	}
	
	public JFrame createFrame(boolean fullScreen) {
		
		JFrame newFrame;
		
		newFrame = new JFrame("Deadlock Viewer");
		
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		canvas = new WorldCanvas();
		canvas.setFocusable(true);
		
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
	
	public Point panelToWorld(Point p) {
		return new Point((p.x - worldOriginX) * MODEL.METERS_PER_PIXEL, (p.y - worldOriginY) * MODEL.METERS_PER_PIXEL);
	}
	
	public Point worldToPanel(Point p) {
		return new Point(p.x * MODEL.PIXELS_PER_METER + worldOriginX, p.y * MODEL.PIXELS_PER_METER + worldOriginY);
	}
	
	public void renderBackgroundFresh() {
		MODEL.world.renderBackgroundFresh();
	}
	
	
	Rect prevAABB;
	
	public void repaint() {
		assert !Thread.holdsLock(MODEL);
		
		worldOriginX = (int)(canvas.getWidth() * 0.5 - (MODEL.world.WORLD_WIDTH * 0.5 * MODEL.PIXELS_PER_METER));
		worldOriginY = (int)(canvas.getHeight() * 0.5 - (MODEL.world.WORLD_HEIGHT * 0.5 * MODEL.PIXELS_PER_METER));
		
		Rect aabb = MODEL.world.getAABB();
		
		switch (CONTROLLER.mode) {
		case DRAFTING:
			aabb = Rect.union(aabb, MODEL.stroke.getAABB());
		case IDLE:
		case MERGERCURSOR:
		case FIXTURECURSOR:
			aabb = Rect.union(aabb, MODEL.cursor.getAABB());
			break;
		case PAUSED:
		case RUNNING:
			break;
		}
		
		worldAABBX = (int)(worldOriginX + (aabb.x * MODEL.PIXELS_PER_METER));
		worldAABBY = (int)(worldOriginY + (aabb.y * MODEL.PIXELS_PER_METER));
		
		Graphics2D g2 = (Graphics2D)canvas.bs.getDrawGraphics();
		
		MODEL.paint(g2);
		
		canvas.bs.show();
		
		Toolkit.getDefaultToolkit().sync();
		
//		if (aabb == prevAABB) {
//			
//			canvas.repaint(
//					worldAABBX,
//					worldAABBY,
//					(int)((aabb.width * MODEL.PIXELS_PER_METER)),
//					(int)((aabb.height * MODEL.PIXELS_PER_METER)));
//			
//		} else {
//			/*
//			 * aabb has changed, so do a whole repaint
//			 */
//			canvas.repaint();
//		}
		
		prevAABB = aabb;
		
	}
	
}
