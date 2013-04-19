package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.MenuTool;
import com.gutabi.deadlock.ui.UIAnimationRunnable;

public class LevelMenu extends Menu implements Model {
	
	public static LevelMenu LEVELMENU;
	
	public LevelMenu() {
		
		for (int i = 0; i < BYPASSAPP.levelDB.levelCount; i++) {
			int menuRow = i / 4;
			int menuCol = i % 4;
			final int ii = i;
			add(new MenuItem(LevelMenu.this, Integer.toString(i)) {
				
				public void action() {
					
					APP.platform.action(BypassWorld.class, ii);
				}
				
			}, menuRow, menuCol);
		}
		
	}
	
	
	public static void create() {
		
		LEVELMENU = new LevelMenu();
		
	}
	
	public static void start() {
		
		APP.model = LEVELMENU;
		
		AppScreen s = new AppScreen(new ContentPane(new LevelMenuPanel()));
		APP.appScreen = s;
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
		LEVELMENU.render();
		
		APP.appScreen.postDisplay();
		
	}
	
	public static void stop() {
		
	}
	
	static AtomicBoolean trigger = new AtomicBoolean();
	static Thread uiThread;
	
	public static void resume() {
		
		APP.model = LEVELMENU;
		
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
		
	}
	
	public Menu getMenu() {
		return this;
	}
	
	public void escape() {
		
		APP.platform.finishAction();
	}
	
}
