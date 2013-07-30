package com.brentonbostick.bypass.level;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.ui.Menu;
import com.brentonbostick.capsloc.ui.MenuItem;
import com.brentonbostick.capsloc.ui.MenuTool;

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
			excl = "OK";
		} else {
			excl = "";
		}
		
		world.winnerMenu = new WinnerMenu(world, excl, "Grade: " + world.curLevel.grade);
		
		world.lock.lock();
		try {
			
			world.postDisplay((int)world.worldCamera.panelAABB.width, (int)world.worldCamera.panelAABB.height);
			
			world.render();
			
		} finally {
			world.lock.unlock();
		}
		
		/*
		 * used to be:
		 * 
		 * APP.tool = new MenuTool();
		 * lock(); postDisplay(); render(); unlock();
		 * 
		 * but there is a race condition:
		 * 
		 * after APP.tool = new MenuTool(); but before lock(), a press could come in
		 * this would go to the menu press code, and crash because the menu has not been rendered yet
		 * 
		 * assigning tool should come last
		 */
		
		APP.tool = new MenuTool();
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
		
		if (index < world.levelDB.levelCount-1) {
			
			add(exclMenuItem, 0, 0);
			add(gradeMenuItem, 1, 0);
			add(nextMenuItem, 2, 0);
			add(againMenuItem, 3, 0);
			shimmeringMenuItem = nextMenuItem;
			
		} else {
			
			add(exclMenuItem, 0, 0);
			add(gradeMenuItem, 1, 0);
			add(againMenuItem, 2, 0);
			shimmeringMenuItem = null;
			
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
