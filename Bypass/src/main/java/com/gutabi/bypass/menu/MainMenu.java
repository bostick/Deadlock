package com.gutabi.bypass.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.MenuTool;
import com.gutabi.deadlock.ui.UIAnimationRunnable;

public class MainMenu extends Menu implements Model {
	
	public MainMenu() {
		
		MenuItem newMenuItem = new MenuItem(MainMenu.this, "New Game") {
			public void action() {
				MainMenu.deaction();
				APP.platform.action(LevelMenu.class);
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
	
	
	static AtomicBoolean trigger = new AtomicBoolean(true);
	static Thread uiThread;
	
	public static void action() {
		
		MainMenu mainMenu = new MainMenu();
		APP.model = mainMenu;
		
		AppScreen s = new AppScreen(new MainMenuContentPane());
		APP.appScreen = s;
		
		APP.tool = new MenuTool();
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
		mainMenu.render();
		
		s.postDisplay();
		
		uiThread = new Thread(new UIAnimationRunnable(trigger));
		uiThread.start();
	}
	
	public static void deaction() {
		
		trigger.set(false);
		
		try {
			uiThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		APP.platform.unshowAppScreen();
		
	}
	
	public Menu getMenu() {
		return this;
	}
	
	public void escape() {
		
	}
	
}
