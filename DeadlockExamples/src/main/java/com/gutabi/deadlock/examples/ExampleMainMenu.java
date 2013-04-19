package com.gutabi.deadlock.examples;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.MenuTool;

public class ExampleMainMenu extends Menu implements Model {
	
	public ExampleMainMenu() {
		
		MenuItem newMenuItem = new MenuItem(ExampleMainMenu.this, "1x1") {
			public void action() {
				
				OneByOneWorld.action();
			}
		};
		add(newMenuItem, 0, 0);
		
	}
	
	public static void action() {
		
		ExampleMainMenu mainMenu = new ExampleMainMenu();
		APP.model = mainMenu;
		
		AppScreen s = new AppScreen(new ContentPane(new ExampleMainMenuPanel()));
		APP.appScreen = s;
		
		APP.tool = new MenuTool();
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
		mainMenu.render();
		
		s.postDisplay();
		
//		s.contentPane.repaint();
	}
	
	public Menu getMenu() {
		return this;
	}
	
	public void escape() {
		
	}
	
}
