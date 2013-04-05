package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.world.examples.RushHourWorld;

public class WinnerMenu extends Menu {
	
	public WinnerMenu() {
		
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
				
				MainMenu mainMenu = new MainMenu();
				APP.model = mainMenu;
				
				MainMenuScreen s = new MainMenuScreen();
				APP.setAppScreen(s);
				
				APP.platform.setupAppScreen(s.contentPane.pcp);
				
				s.postDisplay();
				
				mainMenu.render();
				s.contentPane.repaint();
			}
		};
		add(backMenuItem, 1, 0);
		
	}
	
}
