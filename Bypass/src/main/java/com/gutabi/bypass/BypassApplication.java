package com.gutabi.bypass;

import com.gutabi.bypass.level.LevelDB;
import com.gutabi.capsloc.CapslocApplication;
import com.gutabi.capsloc.Resource;
import com.gutabi.capsloc.ui.Image;
import com.gutabi.capsloc.world.sprites.CarSheet;
import com.gutabi.capsloc.world.sprites.SpriteSheet;

public class BypassApplication extends CapslocApplication {
	
	public Image logo;
	public Image copyright;
	
	public LevelDB tutorialLevelDB;
	public LevelDB episode1LevelDB;
	
	public BypassPlatform bypassPlatform;
	
	public static BypassApplication BYPASSAPP;
	
	public BypassApplication() {
		
	}
	
	public static void create(BypassPlatform plat) throws Exception {
		
		BypassApplication app = new BypassApplication();
		APP = app;
		BYPASSAPP = app;
		
		APP.platform = plat;
		BYPASSAPP.bypassPlatform = plat;
		
		try {
			
			Resource logo = APP.platform.imageResource("logo");
			Resource copy = APP.platform.imageResource("copyright");
			
			BYPASSAPP.logo = APP.platform.readImage(logo);
			BYPASSAPP.copyright = APP.platform.readImage(copy);
			
			APP.carSheet = new CarSheet();
			APP.spriteSheet = new SpriteSheet();
			
			APP.carSheet.load();
			APP.spriteSheet.load();
			
			BYPASSAPP.tutorialLevelDB = new LevelDB(APP.platform.levelDBResource("tutorial"));
			BYPASSAPP.episode1LevelDB = new LevelDB(APP.platform.levelDBResource("episode1"));
			
			BYPASSAPP.bypassPlatform.loadScores(BYPASSAPP.tutorialLevelDB);
			BYPASSAPP.tutorialLevelDB.setFirstUnwon();
			
			BYPASSAPP.bypassPlatform.loadScores(BYPASSAPP.episode1LevelDB);
			BYPASSAPP.episode1LevelDB.setFirstUnwon();
			
			APP.platform.showAppScreen();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
}
