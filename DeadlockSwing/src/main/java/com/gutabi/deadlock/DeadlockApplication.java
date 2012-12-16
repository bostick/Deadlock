package com.gutabi.deadlock;

import java.net.URL;

public class DeadlockApplication {
	
	public URL codebase;
	
	/**
	 * move physics forward by dt seconds
	 */
	public static double dt = 0.01;
	
	public static boolean FPS_DRAW = false;
	public static boolean STOPSIGN_DRAW = true;
	public static boolean CARTEXTURE_DRAW = true;
	public static boolean EXPLOSIONS_DRAW = true;
	public static boolean DEBUG_DRAW = false;
	
	public Screen screen;
	
	public static DeadlockApplication APP = new DeadlockApplication();
	
}
