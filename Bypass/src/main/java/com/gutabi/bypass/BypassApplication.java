package com.gutabi.bypass;

import com.gutabi.bypass.level.LevelDB;
import com.gutabi.bypass.menu.LevelMenu;
import com.gutabi.capsloc.CapslocApplication;
import com.gutabi.capsloc.Resource;
import com.gutabi.capsloc.ui.Image;
import com.gutabi.capsloc.world.sprites.CarSheet;
import com.gutabi.capsloc.world.sprites.SpriteSheet;

public class BypassApplication extends CapslocApplication {
	
	public Image titleBackground;
	public Image title_white;
	public Image copyright;
	
	public LevelDB levelDB;
	
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
			
			Resource background = APP.platform.imageResource("title_background");
			Resource white = APP.platform.imageResource("logo605x132");
			Resource copy = APP.platform.imageResource("copyright");
			
			BYPASSAPP.titleBackground = APP.platform.readImage(background);
			BYPASSAPP.title_white = APP.platform.readImage(white);
			BYPASSAPP.copyright = APP.platform.readImage(copy);
			
			APP.carSheet = new CarSheet();
			APP.spriteSheet = new SpriteSheet();
			
			APP.carSheet.load();
			APP.spriteSheet.load();
			
			BYPASSAPP.levelDB = new LevelDB(APP.platform.levelDBResource("levels"));
			
			BYPASSAPP.bypassPlatform.loadScores();
			
			for (int i = 0; i < BYPASSAPP.levelDB.levelCount; i++) {
				if (BYPASSAPP.levelDB.levelMap.keySet().contains(i)) {
					if (BYPASSAPP.levelDB.levelMap.get(i).isWon) {
						
					} else {
						LevelMenu.firstUnwon = i;
						break;
					}
				} else {
					LevelMenu.firstUnwon = i;
					break;
				}
			}
			
			APP.platform.showAppScreen();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
}
