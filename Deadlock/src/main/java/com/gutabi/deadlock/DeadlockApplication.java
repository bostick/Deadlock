package com.gutabi.deadlock;

import java.util.Random;

import com.gutabi.deadlock.world.sprites.Sheet;

public class DeadlockApplication {
	
//	public int MAINWINDOW_WIDTH = -1;
//	public int MAINWINDOW_HEIGHT = -1;
//	public AABB border;
	
//	public int CONTROLPANEL_WIDTH = -1;
//	public int CONTROLPANEL_HEIGHT = -1;
	
	public Sheet carSheet;
	public Sheet spriteSheet;
	public Sheet explosionSheet;
	
	public AppScreen appScreen;
	public AppScreen debuggerScreen;
	
	public Model model;
	
	public Tool tool;
	
//	public URL codebase;
	
	public Platform platform;
	
	public final boolean DEBUGGER_SCREEN = false;
	
	/*
	 * control panel properties
	 */
	public boolean NORMALCAR = true;
	public boolean FASTCAR = true;
	public boolean REALLYCAR = true;
	public boolean TRUCK = true;
	
	public boolean FPS_DRAW = false;
	public boolean STOPSIGN_DRAW = true;
	public boolean CARTEXTURE_DRAW = true;
	public boolean EXPLOSIONS_DRAW = true;
	public boolean DEBUG_DRAW = false;
	
	public Random RANDOM = new Random(1);
	
	public static DeadlockApplication APP;
	
	public void exit() {
		
//		System.exit(0);
		
		/*
		 * for some reason, the switch to using OpenGL has caused the java process to completely peg the CPU when it exits
		 * there is 1 thread running in the process, inside ftime64 doing something
		 * 
		 * so take extreme measures
		 */
		
		 platform.exit();
		
	}
	
	public void setAppScreen(AppScreen s) {
		this.appScreen = s;
	}
}
