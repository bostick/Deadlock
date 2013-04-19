package com.gutabi.bypass.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.MenuTool;
import com.gutabi.deadlock.ui.UIAnimationRunnable;

public class MainMenu extends Menu implements Model {
	
	public static MainMenu MAINMENU;
	
	public MainMenu() {
		
		MenuItem newMenuItem = new MenuItem(MainMenu.this, "New Game") {
			public void action() {
				
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
	
	
	
	public static void create() {
		
		MAINMENU = new MainMenu();
		
	}

	public static void start() {
		
		APP.model = MAINMENU;
		
		AppScreen s = new AppScreen(new ContentPane(new MainMenuPanel()));
		APP.appScreen = s;
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
		MAINMENU.render();
		
		s.postDisplay();
		
	}
	
	public static void stop() {
		
	}
	
	static AtomicBoolean trigger = new AtomicBoolean();
	static Thread uiThread;
	
	public static void resume() {
		
		APP.model = MAINMENU;
		
		APP.tool = new MenuTool();
		
		trigger.set(true);
		
		uiThread = new Thread(new UIAnimationRunnable(trigger));
		uiThread.start();
	}
	
	public static void pause() {
		
		trigger.set(false);
		
		try {
			uiThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		uiThread = null;
		
	}
	
	public Menu getMenu() {
		return this;
	}
	
	public void escape() {
		
	}
	
}
