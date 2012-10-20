package com.gutabi.deadlock.model;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Entity;

public class DeadlockModel {
	
	/**
	 * move physics forward by dt seconds
	 */
	public static double dt = 0.10;
	
	public static final int PIXELS_PER_METER = 32;
	
	public static final double MOUSE_RADIUS = Math.sqrt(2 * 0.5 * 0.5) * PIXELS_PER_METER;
	
	public static boolean FPS_DRAW = true;
	public static boolean DEBUG_DRAW = true;
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	public World world;
	
	private ControlMode mode = ControlMode.IDLE;
	
	public Stroke stroke;
	
	public Entity hilited;
	
	public final Object pauseLock = new Object();
	
	public DeadlockModel() {
		
	}
	
	public void init() throws Exception {
		
		world = new World();
		
		world.init();
		
		stroke = new Stroke();
		
	}
	
	public void clear() {
		stroke.clearAll();
		world = new World();
	}
	
	public ControlMode getMode() {
		return mode;
	}
	
	public void setMode(ControlMode mode) {
		this.mode = mode;
	}
	
}
