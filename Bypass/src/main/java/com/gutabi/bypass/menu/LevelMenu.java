package com.gutabi.bypass.menu;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.bypass.level.LevelDB;
import com.gutabi.capsloc.AppScreen;
import com.gutabi.capsloc.Model;
import com.gutabi.capsloc.SimulationRunnable;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.ContentPane;
import com.gutabi.capsloc.ui.Menu;
import com.gutabi.capsloc.ui.MenuItem;
import com.gutabi.capsloc.ui.MenuTool;

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
	
//	static AtomicBoolean uiThreadTrigger = new AtomicBoolean();
//	static Thread uiThread;
	static AtomicBoolean simThreadTrigger = new AtomicBoolean();
	static Thread simThread;
	
	public static void resume() {
		
		LEVELMENU = new LevelMenu();
		LEVELMENU.setLocation(levelDB.loc);
		
		APP.model = LEVELMENU;
		
		AppScreen s = new AppScreen(new ContentPane(new BypassMenuPanel()));
		APP.appScreen = s;
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
		APP.tool = new MenuTool();
		
//		uiThreadTrigger.set(true);
		simThreadTrigger.set(true);
		
//		uiThread = new Thread(new UIAnimationRunnable(uiThreadTrigger));
//		uiThread.start();
		
		simThread = new Thread(new SimulationRunnable(simThreadTrigger));
		simThread.start();
		
	}
	
	public static void surfaceChanged(int width, int height) {
		
		LEVELMENU.lock.lock();
		
		for (int i = 0; i < levelDB.levelCount; i++) {
			int menuRow = i / 4;
			int menuCol = i % 4;
			final int ii = i;
			LEVELMENU.add(new LevelMenuItem(LEVELMENU, i) {
				
				public void action() {
					
					APP.platform.action(BypassWorld.class, ii);
				}
				
			}, menuRow, menuCol);
		}
		
		int firstUnwonRow = levelDB.firstUnwon / 4;
		int firstUnwonCol = levelDB.firstUnwon % 4;
		List<MenuItem> col = LEVELMENU.tree.get(firstUnwonCol);
		MenuItem item = col.get(firstUnwonRow);
		
		LEVELMENU.shimmeringMenuItem = item;
		
		APP.appScreen.postDisplay(width, height);
		
		LEVELMENU.render();
		
		LEVELMENU.lock.unlock();
	}
	
	public static void pause() {
		
//		uiThreadTrigger.set(false);
		simThreadTrigger.set(false);
		
		try {
//			uiThread.join();
			simThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		uiThread = null;
		simThread = null;
		
		levelDB.loc = new Point(LEVELMENU.aabb.x, LEVELMENU.aabb.y);
		LEVELMENU = null;
		
	}
	
	public Menu getMenu() {
		return this;
	}
	
	public double getTime() {
		return 0.0;
	}
	
	public boolean integrate(double t) {
		
		if (!rendered) {
			return false;
		}
		
		boolean res = false;
		
		res = res || shimmer.step();
		
		return res;
	}
	
	public void escape() {
		
		APP.platform.finishAction();
	}
	
}
