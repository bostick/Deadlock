package com.gutabi.deadlock;

import java.util.Random;

import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.ImageEngine;
import com.gutabi.deadlock.world.sprites.Sheet;

public class DeadlockApplication {
	
	public int MENUPANEL_WIDTH = -1;
	public int MENUPANEL_HEIGHT = -1;
	
	public int QUADRANTEDITORPANEL_WIDTH = -1;
	public int QUADRANTEDITORPANEL_HEIGHT = -1;
	
	public int CONTROLPANEL_WIDTH = -1;
	public int CONTROLPANEL_HEIGHT = -1;
	
	public int WORLDPANEL_WIDTH = -1;
	public int WORLDPANEL_HEIGHT = -1;
	
	
	public int MENU_WIDTH = -1;
	public int MENU_HEIGHT = -1;
	
	public int TITLE_CENTER_Y = -1;
	public int MENU_CENTER_Y = -1;
	public int COPYRIGHT_CENTER_Y = -1;
	
	
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
	
//	public Image carSheet;
//	public Image spriteSheet;
//	public Image explosionSheet;
	public Sheet carSheet;
	public Sheet spriteSheet;
	public Sheet explosionSheet;
	
	
	public Image titleBackground;
	public Image title_white;
	public Image copyright;
	
	public static DeadlockApplication APP = new DeadlockApplication();
	
	public void init() throws Exception {
		
		ImageEngine iEngine = platform.createImageEngine();
		ResourceEngine rEngine = platform.createResourceEngine();
		
		carSheet = new Sheet(rEngine.imageResource("carsheet"));
		spriteSheet = new Sheet(rEngine.imageResource("spritesheet"));
		explosionSheet = new Sheet(rEngine.imageResource("explosionsheet"));
		
		carSheet.load();
		spriteSheet.load();
		explosionSheet.load();
		
		titleBackground = iEngine.readImage(rEngine.imageResource("title_background"));
		title_white = iEngine.readImage(rEngine.imageResource("title_white"));
		copyright = iEngine.readImage(rEngine.imageResource("copyright"));
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
