package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuTool;
import com.gutabi.deadlock.ui.UIAnimationRunnable;

public class LevelMenu extends Menu implements Model {
	
	public static LevelMenu LEVELMENU;
	public static int firstUnwon = 0;
	public static Point loc = new Point(0, 0);
	
	public LevelMenu() {
		
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
		
		LEVELMENU = new LevelMenu();
		LEVELMENU.setLocation(loc);
		
		APP.model = LEVELMENU;
		
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
		
		for (int i = 0; i < BYPASSAPP.levelDB.levelCount; i++) {
			int menuRow = i / 4;
			int menuCol = i % 4;
			final int ii = i;
			LEVELMENU.add(new LevelMenuItem(LEVELMENU, i, "%d") {
				
				public void action() {
					
					APP.platform.action(BypassWorld.class, ii);
				}
				
			}, menuRow, menuCol);
		}
		
		LEVELMENU.shimmeringMenuItem = LEVELMENU.items.get(firstUnwon);
		
		LEVELMENU.render();
		
		APP.appScreen.postDisplay(width, height);
		
		LEVELMENU.lock.unlock();
	}
	
	public static void pause() {
		
		trigger.set(false);
		
		try {
			uiThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LevelMenu.loc = new Point(LEVELMENU.aabb.x, LEVELMENU.aabb.y);
		LEVELMENU = null;
		
	}
	
	public Menu getMenu() {
		return this;
	}
	
	public void escape() {
		
		APP.platform.finishAction();
	}
	
}
