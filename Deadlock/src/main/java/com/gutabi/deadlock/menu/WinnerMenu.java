package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.gen.BoardsIndex;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.world.examples.RushHourWorld;

public class WinnerMenu extends Menu {
	
	public static void action() {
		
		RushHourWorld world = (RushHourWorld)APP.model;
		
		world.winnerMenu = new WinnerMenu();
		
		world.winnerMenu.render();
		
		APP.appScreen.postDisplay();
		
		APP.tool = new MenuTool();
	}
	
	public WinnerMenu() {
		
		RushHourWorld world = (RushHourWorld)APP.model;
		
		int index = world.index;
		
		if (index < BoardsIndex.table.length-1) {
			
			MenuItem nextMenuItem = new MenuItem(WinnerMenu.this, "Next", 36) {
				public void action() {
					RushHourWorld world = (RushHourWorld)APP.model;
					
					int index = world.index;
					
					RushHourWorld.action(index+1);
					
				}
			};
			add(nextMenuItem, 0, 0);
			
			MenuItem backMenuItem = new MenuItem(WinnerMenu.this, "Back", 36) {
				public void action() {
					
					APP.platform.unshowDebuggerScreen();
					
					MainMenu.action();
				}
			};
			add(backMenuItem, 1, 0);
			
		} else {
			
			MenuItem backMenuItem = new MenuItem(WinnerMenu.this, "Back", 36) {
				public void action() {
					
					MainMenu.action();
				}
			};
			add(backMenuItem, 0, 0);
			
		}
		
	}
	
}
