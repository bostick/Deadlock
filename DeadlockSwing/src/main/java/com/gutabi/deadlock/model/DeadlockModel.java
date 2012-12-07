package com.gutabi.deadlock.model;

import com.gutabi.deadlock.model.menu.Menu;

//@SuppressWarnings("static-access")
public class DeadlockModel {
	
	public static final double QUADRANT_WIDTH = 16.0;
	public static final double QUADRANT_HEIGHT = QUADRANT_WIDTH;
	
	/**
	 * move physics forward by dt seconds
	 */
	public static double dt = 0.01;
	
	public static boolean FPS_DRAW = false;
	public static boolean STOPSIGN_DRAW = true;
	public static boolean CARTEXTURE_DRAW = true;
	public static boolean EXPLOSIONS_DRAW = true;
	public static boolean DEBUG_DRAW = false;
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	public Menu menu;
	
	public World world;
	
	public DeadlockModel() {
		
	}
	
}
