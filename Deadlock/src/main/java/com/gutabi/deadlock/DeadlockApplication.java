package com.gutabi.deadlock;

import java.util.Random;

import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.sprites.CarSheet;
import com.gutabi.deadlock.world.sprites.ExplosionSheet;
import com.gutabi.deadlock.world.sprites.Sheet;
import com.gutabi.deadlock.world.sprites.SpriteSheet;

public class DeadlockApplication {
	
	public int MAINWINDOW_WIDTH = 480;
	public int MAINWINDOW_HEIGHT = 854;
	
	public int CONTROLPANEL_WIDTH = 200;
	public int CONTROLPANEL_HEIGHT = 854;
	
	
	public AppScreen appScreen;
	public DebuggerScreen debuggerScreen;
	
	public Object model;
	
	public Tool tool;
	
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
		
		carSheet = new CarSheet();
		spriteSheet = new SpriteSheet();
		explosionSheet = new ExplosionSheet();
		
		carSheet.load();
		spriteSheet.load();
		explosionSheet.load();
		
		Resource background = platform.imageResource("title_background");
		Resource white = platform.imageResource("title_white");
		Resource copy = platform.imageResource("copyright");
		
		titleBackground = platform.readImage(background);
		title_white = platform.readImage(white);
		copyright = platform.readImage(copy);
		
		MainMenu.action();
		
		APP.platform.showAppScreen();
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
	
	public void setAppScreen(AppScreen s) {
		this.appScreen = s;
	}
}
