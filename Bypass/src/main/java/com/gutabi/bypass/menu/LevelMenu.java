package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.bypass.level.LevelDB;
import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.MenuTool;
import com.gutabi.deadlock.ui.UIAnimationRunnable;

public class LevelMenu extends Menu implements Model {
	
	public LevelMenu() {
		for (int i = 0; i < BYPASSAPP.levelDB.levelCount; i++) {
			int menuRow = i / 4;
			int menuCol = i % 4;
			final int ii = i;
			add(new MenuItem(LevelMenu.this, Integer.toString(i)) {
				
				public void action() {
					LevelMenu.deaction();
					
					APP.platform.action(BypassWorld.class, ii);
				}
				
			}, menuRow, menuCol);
		}
	}
	
	
	static AtomicBoolean trigger = new AtomicBoolean(true);
	static Thread uiThread;
	
	public static void action() {
		
		try {
			
			LevelDB levelDB = new LevelDB(APP.platform.levelDBResource("levels"));
			BYPASSAPP.levelDB = levelDB;
			
			LevelMenu levelMenu = new LevelMenu();
			APP.model = levelMenu;
			
			AppScreen s = new AppScreen(new LevelMenuContentPane());
			APP.appScreen = s;
			
			APP.tool = new MenuTool();
			
			APP.platform.setupAppScreen(s.contentPane.pcp);
			
			levelMenu.render();
			
			APP.appScreen.postDisplay();
			
//			APP.platform.showAppScreen();
			
			uiThread = new Thread(new UIAnimationRunnable(trigger));
			uiThread.start();
			
		} catch (Exception e) {
			e.printStackTrace();
			assert false;
		}
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
		
		MainMenu.action();
	}
	
}
