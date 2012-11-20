package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.model.cursor.RegularCursor;

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
	
	public Stroke debugStroke;
	public Stroke debugStroke2;
	
	public Entity hilited;
	
	public final Object pauseLock = new Object();
	
	public Stats stats;
	
	public boolean grid;
	
	public DeadlockModel() {
		
	}
	
	public void init() throws Exception {
		
		world = new World();
		world.init();
		
		cursor = new RegularCursor();
		
		stats = new Stats();
	}
	
	public void paint(Graphics2D g2) {
		
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, 1427, 822);
		
		AffineTransform origTrans = g2.getTransform();
		
		g2.translate(VIEW.worldOffsetX - VIEW.worldOriginX, VIEW.worldOffsetY - VIEW.worldOriginY);
		g2.setStroke(VIEW.worldStroke);
		
		world.paint(g2);
		
		if (stroke != null) {
			stroke.paint(g2);
		}
		
		if (cursor != null) {
			cursor.paint(g2);
		}
		
		g2.setTransform(origTrans);
		
		if (FPS_DRAW) {
			
			stats.paint(g2);
			
			g2.setTransform(origTrans);
		}
		
	}
	
}
