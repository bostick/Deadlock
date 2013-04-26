package com.gutabi.bypass.menu;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Label;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuTool;
import com.gutabi.deadlock.ui.UIAnimationRunnable;
import com.gutabi.deadlock.ui.paint.FontStyle;

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
		LEVELMENU.ready = false;
		LEVELMENU.lock.unlock();
		
		
		Label lab;
		String fmt;
		if (BYPASSAPP.levelDB.levelCount <= 10) {
			lab = new Label("0");
//			fmt = "%01d";
		} else if (BYPASSAPP.levelDB.levelCount <= 100) {
			lab = new Label("00");
//			fmt = "%02d";
		} else {
			lab = new Label("000");
//			fmt = "%03d";
		}
		fmt = "%d";
		
		lab.fontFile = APP.platform.fontResource("visitor1");
		lab.fontStyle = FontStyle.PLAIN;
		lab.fontSize = 72;
		lab.renderLocal();
		double labWidth = lab.localAABB.width;
		int itemsPerRow = (int)Math.floor((width - labWidth) / (labWidth + 10));
		
		for (int i = 0; i < BYPASSAPP.levelDB.levelCount; i++) {
			int menuRow = i / itemsPerRow;
			int menuCol = i % itemsPerRow;
			final int ii = i;
			LEVELMENU.add(new LevelMenuItem(LEVELMENU, i, fmt) {
				
				public void action() {
					
					APP.platform.action(BypassWorld.class, ii);
				}
				
			}, menuRow, menuCol);
		}
		
//		if (loc == null) {
//		Point loc = new Point(0, -((firstUnwon / itemsPerRow)) * (LEVELMENU.items.get(0).localAABB.height + 10));
//		}
//		if (firstUnwon/itemsPerRow > 0) {
//			loc = loc.plus(new Point(0, 0.5 * LEVELMENU.items.get(0).localAABB.height));
//		}
		
		LEVELMENU.shimmeringMenuItem = LEVELMENU.items.get(firstUnwon);
		
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
