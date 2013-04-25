package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuTool;
import com.gutabi.deadlock.ui.UIAnimationRunnable;

public class LevelMenu extends Menu implements Model {
	
	public static LevelMenu LEVELMENU;
	
	public LevelMenu() {
		
		for (int i = 0; i < BYPASSAPP.levelDB.levelCount; i++) {
			int menuRow = i / 3;
			int menuCol = i % 3;
			final int ii = i;
			add(new LevelMenuItem(LevelMenu.this, i) {
				
				public void action() {
					
					APP.platform.action(BypassWorld.class, ii);
				}
				
			}, menuRow, menuCol);
		}
		
	}
	
	
	public static void create() {
		
		LEVELMENU = new LevelMenu();
		
	}
	
	public static void destroy() {
		LEVELMENU = null;
	}
	
	public static void start() {
		
	}
	
	public static void stop() {
		
	}
	
	static AtomicBoolean trigger = new AtomicBoolean();
	static Thread uiThread;
	
	public static void resume() {
		
		APP.model = LEVELMENU;
		
		int firstUnwon = 0;
		for (int i = 0; i < BYPASSAPP.levelDB.levelCount; i++) {
			if (BYPASSAPP.levelDB.levelMap.keySet().contains(i)) {
				if (BYPASSAPP.levelDB.levelMap.get(i).isWon) {
					
				} else {
					firstUnwon = i;
					break;
				}
			} else {
				firstUnwon = i;
				break;
			}
		}
		LEVELMENU.shimmeringMenuItem = LEVELMENU.items.get(firstUnwon);
		
		AppScreen s = new AppScreen(new ContentPane(new BypassMenuPanel()));
		APP.appScreen = s;
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
		APP.tool = new MenuTool();
		
		trigger.set(true);
		
		uiThread = new Thread(new UIAnimationRunnable(trigger));
		uiThread.start();
		
	}
	
	public static void surfaceChanged(int width, int height) {
		
		LEVELMENU.lock.lock();
		LEVELMENU.ready = false;
		LEVELMENU.lock.unlock();
		
		LEVELMENU.render();
		
		APP.appScreen.postDisplay(width, height);
		
		LEVELMENU.lock.lock();
		LEVELMENU.ready = true;
		LEVELMENU.lock.unlock();
	}
	
	public static void pause() {
		
		LEVELMENU.lock.lock();
		LEVELMENU.ready = false;
		LEVELMENU.lock.unlock();
		
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
