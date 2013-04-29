package com.gutabi.bypass.level;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.MenuTool;

public class WinnerMenu extends Menu {
	
	public boolean ready;
	
	public static void action() {
		
		BypassWorld world = (BypassWorld)APP.model;
		
		String excl;
		char letter = world.curLevel.grade.charAt(0);
		if (letter == 'A') {
			excl = "Excellent!";
		} else if (letter == 'B') {
			excl = "Good!";
		} else if (letter == 'C') {
			excl = "OK!";
		} else if (letter == 'D') {
			excl = "Adequate!";
		} else {
			excl = "Effort!";
		}
		
		world.winnerMenu = new WinnerMenu(world, excl, "Grade: " + world.curLevel.grade);
		
		APP.tool = new MenuTool();
		
		world.lock.lock();
		
		world.render_worldPanel();
		world.panelPostDisplay((int)world.worldCamera.panelAABB.width, (int)world.worldCamera.panelAABB.height);
		
		world.lock.unlock();
		
	}
	
	public WinnerMenu(BypassWorld world, String excl, String grade) {
		
		widthFraction = 0.666;
		
		int index = world.curLevel.index;
		
		MenuItem exclMenuItem = new MenuItem(WinnerMenu.this, excl) {
			public void action() {
				
			}
		};
		exclMenuItem.border = false;
		
		MenuItem gradeMenuItem = new MenuItem(WinnerMenu.this, grade) {
			public void action() {
				
			}
		};
		gradeMenuItem.border = false;
		
		MenuItem nextMenuItem = new MenuItem(WinnerMenu.this, "Next") {
			public void action() {
				
				BypassWorld world = (BypassWorld)APP.model;
				
				BYPASSAPP.bypassPlatform.saveScore(world.curLevel);
				
				int index = world.curLevel.index;
				
				APP.platform.finishAction();
				
				APP.platform.action(BypassWorld.class, index+1);
				
			}
		};
		
		MenuItem againMenuItem = new MenuItem(WinnerMenu.this, "Try Again") {
			public void action() {
				
				BypassWorld world = (BypassWorld)APP.model;
				
				int index = world.curLevel.index;
				
				APP.platform.finishAction();
				
				APP.platform.action(BypassWorld.class, index);
				
			}
		};
		
		MenuItem backMenuItem = new MenuItem(WinnerMenu.this, "Back") {
			public void action() {
				
				BypassWorld world = (BypassWorld)APP.model;
				
				BYPASSAPP.bypassPlatform.saveScore(world.curLevel);
				
				APP.platform.finishAction();
			}
		};
		
		if (index < BYPASSAPP.levelDB.levelCount-1) {
			
			add(exclMenuItem, 0, 0);
			add(gradeMenuItem, 1, 0);
			add(nextMenuItem, 2, 0);
			add(againMenuItem, 3, 0);
			add(backMenuItem, 4, 0);
			shimmeringMenuItem = nextMenuItem;
			
		} else {
			
			add(exclMenuItem, 0, 0);
			add(gradeMenuItem, 1, 0);
			add(againMenuItem, 2, 0);
			add(backMenuItem, 3, 0);
			shimmeringMenuItem = backMenuItem;
			
		}
		
	}
	
	public void escape() {
		APP.platform.finishAction();
	}
	
	public void postDisplay(int width, int height) {
		
		super.postDisplay(width, height);
		
		ready = true;
	}
	
}
