package com.gutabi.bypass.menu;

import static com.gutabi.capsloc.CapslocApplication.APP;
import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.capsloc.AppScreen;
import com.gutabi.capsloc.Model;
import com.gutabi.capsloc.ui.ContentPane;
import com.gutabi.capsloc.ui.Menu;
import com.gutabi.capsloc.ui.MenuItem;
import com.gutabi.capsloc.ui.MenuTool;
import com.gutabi.capsloc.ui.UIAnimationRunnable;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;

public class MainMenu extends Menu implements Model {
	
	public static MainMenu MAINMENU;
	
	public MainMenu() {
		
		MenuItem newMenuItem = new MenuItem(MainMenu.this, " Episode 1 ") {
			public void action() {
				
				LevelMenu.levelDB = BYPASSAPP.episode1LevelDB;
				
				APP.platform.action(LevelMenu.class);
			}
		};
		add(newMenuItem, 0, 0);
		
		MenuItem new2MenuItem = new MenuItem(MainMenu.this, " Episode 2 ") {
			public void action() {
				
				LevelMenu.levelDB = BYPASSAPP.episode2LevelDB;
				
				APP.platform.action(LevelMenu.class);
			}
		};
		add(new2MenuItem, 1, 0);
		
		MenuItem resumeMenuItem = new MenuItem(MainMenu.this, "Tutorial") {
			public void action() {
				
				LevelMenu.levelDB = BYPASSAPP.tutorialLevelDB;
				
				APP.platform.action(LevelMenu.class);
			}
		};
		add(resumeMenuItem, 2, 0);
		
	}
	
	
	
	public static void create() {
		
	}
	
	public static void destroy() {
		
	}
	
	public static void start() {
		
	}
	
	public static void stop() {
		
	}
	
	static AtomicBoolean trigger = new AtomicBoolean();
	static Thread uiThread;
	
	public static void resume() {
		
		MAINMENU = new MainMenu();
		APP.model = MAINMENU;
		
		AppScreen s = new AppScreen(new ContentPane(new BypassMenuPanel()));
		APP.appScreen = s;
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
		APP.tool = new MenuTool();
		
		trigger.set(true);
		
		uiThread = new Thread(new UIAnimationRunnable(trigger));
		uiThread.start();
	}
	
	public static void surfaceChanged(int width, int height) {
		
		MAINMENU.lock.lock();
		
		APP.appScreen.postDisplay(width, height);
		
		MAINMENU.render();
		
		MAINMENU.lock.unlock();
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
		MAINMENU = null;
		
	}
	
	public Menu getMenu() {
		return this;
	}
	
	public void escape() {
		
	}

}
