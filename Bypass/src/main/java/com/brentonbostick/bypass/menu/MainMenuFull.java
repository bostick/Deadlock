package com.brentonbostick.bypass.menu;

import static com.brentonbostick.bypass.BypassApplication.BYPASSAPP;
import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.ui.MenuItem;

public class MainMenuFull extends MainMenu {
	
	MenuItem episode1MenuItem;
	MenuItem episode2MenuItem;
	MenuItem tutorialMenuItem;
	
	public MainMenuFull() {
		
		episode1MenuItem = new MainMenuItem(MainMenuFull.this, BYPASSAPP.bypassPlatform.levelDB("episode1")) {
			public void action() {
				
				LevelMenu.levelDBName = "episode1";
				
				APP.platform.action(LevelMenu.class);
			}
		};
		add(episode1MenuItem, 0, 0);
		
		episode2MenuItem = new MainMenuItem(MainMenuFull.this, BYPASSAPP.bypassPlatform.levelDB("episode2")) {
			public void action() {
				
				LevelMenu.levelDBName = "episode2";
				
				APP.platform.action(LevelMenu.class);
			}
		};
		add(episode2MenuItem, 1, 0);
		
		tutorialMenuItem = new MainMenuItem(MainMenuFull.this, BYPASSAPP.bypassPlatform.levelDB("tutorial")) {
			public void action() {
				
				LevelMenu.levelDBName = "tutorial";
				
				APP.platform.action(LevelMenu.class);
			}
		};
		add(tutorialMenuItem, 2, 0);
		
		updateFirstUnplayed();
	}
	
	public void updateFirstUnplayed() {
		if (BYPASSAPP.bypassPlatform.levelDB("tutorial").percentage == 0.0) {
			shimmeringMenuItem = tutorialMenuItem;
		} else if (BYPASSAPP.bypassPlatform.levelDB("episode1").percentage == 0.0) {
			shimmeringMenuItem = episode1MenuItem;
		} else if (BYPASSAPP.bypassPlatform.levelDB("episode2").percentage == 0.0) {
			shimmeringMenuItem = episode2MenuItem;
		} else {
			shimmeringMenuItem = null;
		}
	}
	
}
