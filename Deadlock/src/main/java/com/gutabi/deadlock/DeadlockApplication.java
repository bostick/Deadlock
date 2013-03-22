package com.gutabi.deadlock;

import java.util.Random;

import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.sprites.CarSheet;
import com.gutabi.deadlock.world.sprites.ExplosionSheet;
import com.gutabi.deadlock.world.sprites.Sheet;
import com.gutabi.deadlock.world.sprites.SpriteSheet;

public class DeadlockApplication {
	
	public int WINDOW_WIDTH = -1;
	public int WINDOW_HEIGHT = -1;
	
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
	
	
	public AppScreen appScreen;
	public DebuggerScreen debuggerScreen;
	
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
	
	public Sheet carSheet;
	public Sheet spriteSheet;
	public Sheet explosionSheet;
	
	
	public Image titleBackground;
	public Image title_white;
	public Image copyright;
	
	public static DeadlockApplication APP = new DeadlockApplication();
	
	public void init() throws Exception {
		
		ResourceEngine rEngine = platform.createResourceEngine();
		
		carSheet = new CarSheet();
		spriteSheet = new SpriteSheet();
		explosionSheet = new ExplosionSheet();
		
		carSheet.load();
		spriteSheet.load();
		explosionSheet.load();
		
		titleBackground = platform.readImage(rEngine.imageResource("title_background"));
		
		title_white = platform.readImage(rEngine.imageResource("title_white"));
		copyright = platform.readImage(rEngine.imageResource("copyright"));
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
