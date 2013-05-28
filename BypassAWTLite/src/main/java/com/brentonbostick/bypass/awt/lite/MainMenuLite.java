package com.brentonbostick.bypass.awt.lite;

import static com.brentonbostick.bypass.BypassApplication.BYPASSAPP;
import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.bypass.menu.LevelMenu;
import com.brentonbostick.bypass.menu.MainMenu;
import com.brentonbostick.bypass.menu.MainMenuItem;
import com.brentonbostick.capsloc.ui.MenuItem;

public class MainMenuLite extends MainMenu {
	
	MenuItem episode1MenuItem;
	MenuItem tutorialMenuItem;
	
	public MainMenuLite() {
		
		episode1MenuItem = new MainMenuItem(MainMenuLite.this, BYPASSAPP.bypassPlatform.levelDB("episode1")) {
			public void action() {
				
				LevelMenu.levelDBName = "episode1";
				
				APP.platform.action(LevelMenu.class);
			}
		};
		add(episode1MenuItem, 0, 0);
		
		tutorialMenuItem = new MainMenuItem(MainMenuLite.this, BYPASSAPP.bypassPlatform.levelDB("tutorial")) {
			public void action() {
				
				LevelMenu.levelDBName = "tutorial";
				
				APP.platform.action(LevelMenu.class);
			}
		};
		add(tutorialMenuItem, 1, 0);
		
		updateFirstUnplayed();
	}
	
	public void updateFirstUnplayed() {
		if (BYPASSAPP.bypassPlatform.levelDB("tutorial").percentage == 0.0) {
			shimmeringMenuItem = tutorialMenuItem;
		} else if (BYPASSAPP.bypassPlatform.levelDB("episode1").percentage == 0.0) {
			shimmeringMenuItem = episode1MenuItem;
		} else {
			shimmeringMenuItem = null;
		}
	}
	
}
