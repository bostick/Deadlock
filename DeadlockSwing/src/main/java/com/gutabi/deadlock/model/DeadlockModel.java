package com.gutabi.deadlock.model;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Entity;

public class DeadlockModel {
	
	/**
	 * move physics forward by dt seconds
	 */
	public static double dt = 0.01;
	
	public static final int PIXELS_PER_METER = 32;
	public static final double METERS_PER_PIXEL = 1 / ((double)PIXELS_PER_METER);
	
	public static boolean FPS_DRAW = true;
	public static boolean DEBUG_DRAW = true;
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	public World world;
	
	public ControlMode mode;
	
	public Stroke stroke;
	
	public Entity hilited;
	
	public final Object pauseLock = new Object();
	
	public Stats stats;
	
	public DeadlockModel() {
		mode = ControlMode.IDLE;
	}
	
	public void init() throws Exception {
		
		world = new World();
		world.init();
		
		stroke = new Stroke();
		
		stats = new Stats();
	}
	
	public void clear() {
		stroke = null;
		world = null;
	}
	
}
