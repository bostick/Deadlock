package com.gutabi.bypass;

import com.gutabi.bypass.level.LevelDB;
import com.gutabi.deadlock.DeadlockApplication;
import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.world.sprites.CarSheet;
import com.gutabi.deadlock.world.sprites.SpriteSheet;

public class BypassApplication extends DeadlockApplication {
	
	public Image titleBackground;
	public Image title_white;
	public Image copyright;
	
	public LevelDB levelDB;
	
	public BypassPlatform bypassPlatform;
	
	public static BypassApplication BYPASSAPP;
	
	public BypassApplication() {
		
		/*
		 * Droid X
		 */
//		MAINWINDOW_WIDTH = 480;
//		MAINWINDOW_HEIGHT = 854;
		
		/*
		 * Galaxy Tab 2 7.0
		 */
//		MAINWINDOW_WIDTH = 600;
//		MAINWINDOW_HEIGHT = 976;
		
	}
	
	public void init() throws Exception {
		
		Resource background = APP.platform.imageResource("title_background");
		Resource white = APP.platform.imageResource("logo605x132");
		Resource copy = APP.platform.imageResource("copyright");
		
		titleBackground = platform.readImage(background);
		title_white = platform.readImage(white);
		copyright = platform.readImage(copy);
		
		carSheet = new CarSheet();
		spriteSheet = new SpriteSheet();
		
		carSheet.load();
		spriteSheet.load();
		
		levelDB = new LevelDB(APP.platform.levelDBResource("levels"));
		
		APP.platform.showAppScreen();
		
	}
	
}
