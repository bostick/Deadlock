package com.gutabi.bypass.menu;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.bypass.level.LevelDB;
import com.gutabi.capsloc.AppScreen;
import com.gutabi.capsloc.Model;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.ContentPane;
import com.gutabi.capsloc.ui.Menu;
import com.gutabi.capsloc.ui.MenuTool;
import com.gutabi.capsloc.ui.UIAnimationRunnable;

public class LevelMenu extends Menu implements Model {
	
	public static LevelMenu LEVELMENU;
	
	public static LevelDB levelDB;
	
	public static boolean showInfo = false;
	
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
		LEVELMENU.setLocation(levelDB.loc);
		
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
		
		for (int i = 0; i < levelDB.levelCount; i++) {
			int menuRow = i / 4;
			int menuCol = i % 4;
			final int ii = i;
			LEVELMENU.add(new LevelMenuItem(LEVELMENU, i, "%d") {
				
				public void action() {
					
					APP.platform.action(BypassWorld.class, ii);
				}
				
			}, menuRow, menuCol);
		}
		
		LEVELMENU.shimmeringMenuItem = LEVELMENU.items.get(levelDB.firstUnwon);
		
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
		
		levelDB.loc = new Point(LEVELMENU.aabb.x, LEVELMENU.aabb.y);
		LEVELMENU = null;
		
	}
	
	public Menu getMenu() {
		return this;
	}
	
	public void escape() {
		
		APP.platform.finishAction();
	}
	
}
