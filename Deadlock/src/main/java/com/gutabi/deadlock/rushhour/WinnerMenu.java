package com.gutabi.deadlock.rushhour;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.gen.BoardsIndex;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.menu.MenuTool;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;

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
		
		int index = -1;
		for (int i = 0; i < BoardsIndex.table.length; i++) {
			if (BoardsIndex.table[i] == world.level.id) {
				index = i;
				break;
			}
		}
		assert index != -1;
		
		MenuItem nextMenuItem = new MenuItem(WinnerMenu.this, "Next", 36) {
			public void action() {
				RushHourWorld world = (RushHourWorld)APP.model;
				
				int index = -1;
				for (int i = 0; i < BoardsIndex.table.length; i++) {
					if (BoardsIndex.table[i] == world.level.id) {
						index = i;
						break;
					}
				}
				assert index != -1;
				
				world.stopRunning();
				
				RushHourWorld.action(index+1);
				
			}
		};
		
		MenuItem backMenuItem = new MenuItem(WinnerMenu.this, "Back", 36) {
			public void action() {
				
				APP.platform.unshowDebuggerScreen();
				
				MainMenu.action();
			}
		};
		
		if (index < BoardsIndex.table.length-1) {
			
			add(nextMenuItem, 0, 0);
			add(backMenuItem, 1, 0);
			
		} else {
			
			add(backMenuItem, 0, 0);
			
		}
		
	}
	
	public void escape() {
		
	}
	
}
