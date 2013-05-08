package com.gutabi.capsloc;

import java.util.Random;

import com.gutabi.capsloc.world.sprites.Sheet;

public class CapslocApplication {
	
	public Sheet carSheet;
	public Sheet spriteSheet;
	public Sheet explosionSheet;
	
	public AppScreen appScreen;
	public AppScreen debuggerScreen;
	
	public Model model;
	
	public Tool tool;
	
	public Platform platform;
	
//	public final boolean DEBUGGER_SCREEN = false;
	
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
	
	public static CapslocApplication APP;
	
	public void setAppScreen(AppScreen s) {
		this.appScreen = s;
	}
}
