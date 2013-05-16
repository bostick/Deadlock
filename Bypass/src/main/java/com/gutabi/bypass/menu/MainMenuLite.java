package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.AppScreen;
import com.gutabi.capsloc.ui.ContentPane;
import com.gutabi.capsloc.ui.MenuItem;
import com.gutabi.capsloc.ui.MenuTool;

public class MainMenuLite extends MainMenu {
	
	public MainMenuLite() {
		
		MenuItem episode1MenuItem = new MainMenuItem(MainMenuLite.this, BYPASSAPP.bypassPlatform.levelDB("episode1")) {
			public void action() {
				
				LevelMenu.levelDB = BYPASSAPP.bypassPlatform.levelDB("episode1");
				
				APP.platform.action(LevelMenu.class);
			}
		};
		add(episode1MenuItem, 0, 0);
		
		MenuItem tutorialMenuItem = new MainMenuItem(MainMenuLite.this, BYPASSAPP.bypassPlatform.levelDB("tutorial")) {
			public void action() {
				
				LevelMenu.levelDB = BYPASSAPP.bypassPlatform.levelDB("tutorial");
				
				APP.platform.action(LevelMenu.class);
			}
		};
		add(tutorialMenuItem, 1, 0);
		
		if (BYPASSAPP.bypassPlatform.levelDB("tutorial").percentage == 0.0) {
			shimmeringMenuItem = tutorialMenuItem;
		} else if (BYPASSAPP.bypassPlatform.levelDB("episode1").percentage == 0.0) {
			shimmeringMenuItem = episode1MenuItem;
		} else {
			shimmeringMenuItem = null;
		}
		
	}
	
	public static void start() {
		
		APP.tool = new MenuTool();
		
		BypassMenu.BYPASSMENU = new MainMenuLite();
		APP.model = BypassMenu.BYPASSMENU;
		
		AppScreen s = new AppScreen(new ContentPane(new BypassMenuPanel()));
		APP.appScreen = s;
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
	}

}
