package com.gutabi.deadlock;

import java.net.URL;

public class DeadlockApplication {
	
	public URL codebase;
	
	/**
	 * move physics forward by dt seconds
	 */
	public double dt = 0.01;
	
	public boolean FPS_DRAW = false;
	public boolean STOPSIGN_DRAW = true;
	public boolean CARTEXTURE_DRAW = true;
	public boolean EXPLOSIONS_DRAW = true;
	public boolean DEBUG_DRAW = false;
	
	public Screen screen;
	
	public static DeadlockApplication APP = new DeadlockApplication();
	
}
