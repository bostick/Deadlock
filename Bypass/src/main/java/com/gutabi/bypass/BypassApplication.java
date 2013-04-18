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
	
	public static BypassApplication BYPASSAPP;
	
	public BypassApplication() {
		
		MAINWINDOW_WIDTH = 480;
		MAINWINDOW_HEIGHT = 854;
		
		CONTROLPANEL_WIDTH = 200;
		CONTROLPANEL_HEIGHT = 854;
		
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
		
		APP.platform.showAppScreen();
		
	}
	
}
