package com.gutabi.deadlock.model;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.model.cursor.RegularCursor;

//@SuppressWarnings("static-access")
public class DeadlockModel {
	
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
		
		cursor = new RegularCursor();
		
		stats = new Stats();
	}
	
}
