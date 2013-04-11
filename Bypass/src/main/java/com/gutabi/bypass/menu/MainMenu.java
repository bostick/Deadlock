package com.gutabi.bypass.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.MenuTool;

public class MainMenu extends Menu implements Model {
	
	public MainMenu() {
		
		MenuItem newMenuItem = new MenuItem(MainMenu.this, "New Game") {
			public void action() {
				
				LevelMenu.action();
			}
		};
		add(newMenuItem, 0, 0);
		
		MenuItem resumeMenuItem = new MenuItem(MainMenu.this, "Resume") {
			public void action() {
				
			}
		};
		resumeMenuItem.active = false;
		add(resumeMenuItem, 1, 0);
		
	}
	
	public static void action() {
		
		MainMenu mainMenu = new MainMenu();
		APP.model = mainMenu;
		
		MainMenuScreen s = new MainMenuScreen();
		APP.appScreen = s;
		
		APP.tool = new MenuTool();
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
		mainMenu.render();
		
		s.postDisplay();
		
		s.contentPane.repaint();
		
	}
	
	public Menu getMenu() {
		return this;
	}
	
	public void escape() {
		
	}
	
}
