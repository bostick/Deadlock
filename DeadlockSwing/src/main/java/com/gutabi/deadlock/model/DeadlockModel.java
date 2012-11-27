package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.model.cursor.RegularCursor;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class DeadlockModel {
	
	public static final int PIXELS_PER_METER_DEBUG = 32;
	public static final double METERS_PER_PIXEL_DEBUG = 1 / ((double)PIXELS_PER_METER_DEBUG);
	
	public static final double QUADRANT_WIDTH = 16.0;
	public static final double QUADRANT_HEIGHT = QUADRANT_WIDTH;
	
	/**
	 * move physics forward by dt seconds
	 */
	public static double dt = 0.01;
	
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
	
	public void renderBackground() {
		world.renderBackground();
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.g2.setColor(Color.WHITE);
		ctxt.g2.fillRect(0, 0, 1427, 822);
		
		AffineTransform origTrans = ctxt.g2.getTransform();
		
		ctxt.g2.translate((-VIEW.worldOriginX), (-VIEW.worldOriginY));
		ctxt.g2.setStroke(VIEW.worldStroke);
		
		ctxt.g2.scale(MODEL.PIXELS_PER_METER_DEBUG, MODEL.PIXELS_PER_METER_DEBUG);
		
		world.paint(ctxt);
		
		if (stroke != null) {
			stroke.paint(ctxt);
		}
		
		if (cursor != null) {
			cursor.paint(ctxt);
		}
		
		ctxt.g2.setTransform(origTrans);
		
		if (FPS_DRAW) {
			
			stats.paint(ctxt);
			
			ctxt.g2.setTransform(origTrans);
		}
		
	}
	
}
