package com.gutabi.deadlock;

import java.util.Random;

import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.ImageEngine;

public class DeadlockApplication {
	
//	public URL codebase;
	
	public Platform platform;
	
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
	
	public Image carSheet;
	public Image spriteSheet;
	
	public Image explosionSheet;
	public Image titleBackground;
	public Image title_white;
	public Image copyright;
	
	public static DeadlockApplication APP = new DeadlockApplication();
	
	public void init() throws Exception {
		
		ImageEngine iEngine = platform.createImageEngine();
		ResourceEngine rEngine = platform.createResourceEngine();
		
		carSheet = iEngine.readImage(rEngine.resource("carsheet"));
		spriteSheet = iEngine.readImage(rEngine.resource("spritesheet"));
		explosionSheet = iEngine.readImage(rEngine.resource("explosionsheet"));
		titleBackground = iEngine.readImage(rEngine.resource("title_background"));
		title_white = iEngine.readImage(rEngine.resource("title_white"));
		copyright = iEngine.readImage(rEngine.resource("copyright"));
	}
	
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
	
}
