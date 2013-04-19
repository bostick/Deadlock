package com.gutabi.bypass.level;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.MenuTool;

public class WinnerMenu extends Menu {
	
	public static void action() {
		
		BypassWorld world = (BypassWorld)APP.model;
		
		world.winnerMenu = new WinnerMenu();
		
		world.winnerMenu.render();
		
		APP.appScreen.postDisplay();
		
		APP.tool = new MenuTool();
	}
	
	public WinnerMenu() {
		
		BypassWorld world = (BypassWorld)APP.model;
		
		int index = world.curLevel.index;
		
		MenuItem nextMenuItem = new MenuItem(WinnerMenu.this, "Next") {
			public void action() {
				
				BypassWorld world = (BypassWorld)APP.model;
				
				int index = world.curLevel.index;
				
				APP.platform.finishAction();
				
//				BypassWorld.action(world.levelDB, index+1);
				APP.platform.action(BypassWorld.class, index+1);
				
			}
		};
		
		MenuItem backMenuItem = new MenuItem(WinnerMenu.this, "Back") {
			public void action() {
				
//				MainMenu.action();
				APP.platform.finishAction();
			}
		};
		
		if (index < BYPASSAPP.levelDB.levelCount-1) {
			
			add(nextMenuItem, 0, 0);
			add(backMenuItem, 1, 0);
			
		} else {
			
			add(backMenuItem, 0, 0);
			
		}
		
	}
	
	public void escape() {
		APP.platform.finishAction();
	}
	
}
