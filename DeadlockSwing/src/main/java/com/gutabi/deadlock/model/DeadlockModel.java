package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.graph.Vertex;

public class DeadlockModel {
	
	/**
	 * move physics forward by dt seconds
	 */
	public static double dt = 0.01;
	
	public static final int PIXELS_PER_METER = 32;
	public static final double METERS_PER_PIXEL = 1 / ((double)PIXELS_PER_METER);
	
	public static boolean FPS_DRAW = false;
	public static boolean DEBUG_DRAW = false;
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	public World world;
	
	public Cursor cursor;
	public Stroke stroke;
	
	public Entity hilited;
	
	public final Object pauseLock = new Object();
	
	public Stats stats;
	
	public DeadlockModel() {
		
	}
	
	public void init() throws Exception {
		
		world = new World();
		world.init();
		
		cursor = new Cursor(Vertex.INIT_VERTEX_RADIUS);
//		stroke = new Stroke();
		
		stats = new Stats();
	}
	
	
	
//	private BufferedImage backgroundImage;
//	private Graphics2D backgroundImageG2;
	
	public void paint(Graphics2D g2) {
		
		AffineTransform origTrans = g2.getTransform();
		
		g2.translate(VIEW.worldOriginX, VIEW.worldOriginY);
		g2.setStroke(VIEW.worldStroke);
		
		world.paint(g2);
		
		if (stroke != null) {
			stroke.paint(g2);
		}
		
		if (cursor != null) {
			cursor.paint(g2);
		}
		
		if (FPS_DRAW) {
			
			g2.setTransform(origTrans);
			g2.setStroke(VIEW.worldStroke);
			
			g2.translate(VIEW.worldAABBX, VIEW.worldAABBY);
			
			stats.paint(g2);
			
			g2.setTransform(origTrans);
		}
		
	}
	
	
	
//	public void clear() {
//		stroke = null;
//		world = null;
//	}
	
}
