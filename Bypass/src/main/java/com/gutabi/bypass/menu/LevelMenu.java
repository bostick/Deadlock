package com.gutabi.bypass.menu;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.bypass.level.LevelDB;
import com.gutabi.capsloc.AppScreen;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.ContentPane;
import com.gutabi.capsloc.ui.MenuItem;
import com.gutabi.capsloc.ui.MenuTool;

public class LevelMenu extends BypassMenu {
	
	public static LevelDB levelDB;
	
	public static Map<LevelDB, LevelMenuInfo> map = new HashMap<LevelDB, LevelMenuInfo>();
	
	public static void create() {
		
	}
	
	public static void destroy() {
		
	}
	
	public static void start() {
		
		APP.tool = new MenuTool();
		
		BypassMenu.BYPASSMENU = new LevelMenu();
		for (int i = 0; i < levelDB.levelCount; i++) {
			int menuRow = i / 4;
			int menuCol = i % 4;
			final int ii = i;
			BypassMenu.BYPASSMENU.add(new LevelMenuItem(BypassMenu.BYPASSMENU, i) {
				
				public void action() {
					
					APP.platform.action(BypassWorld.class, ii);
				}
				
			}, menuRow, menuCol);
		}
		
		if (levelDB.firstUnwon != -1) {
			int firstUnwonRow = levelDB.firstUnwon / 4;
			int firstUnwonCol = levelDB.firstUnwon % 4;
			List<MenuItem> col = BypassMenu.BYPASSMENU.tree.get(firstUnwonCol);
			MenuItem item = col.get(firstUnwonRow);
			
			BypassMenu.BYPASSMENU.shimmeringMenuItem = item;
			
		} else {
			BypassMenu.BYPASSMENU.shimmeringMenuItem = null;
		}
		
		APP.model = BypassMenu.BYPASSMENU;
		
		AppScreen s = new AppScreen(new ContentPane(new BypassMenuPanel()));
		APP.appScreen = s;
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
	}
	
	public static void stop() {
		
	}
	
	public static void surfaceChanged(int width, int height) {
		
		BypassMenu.BYPASSMENU.lock.lock();
		try {
			
			APP.appScreen.postDisplay(width, height);
			
			if (tmpPanelOffset != null) {
				BypassMenu.BYPASSMENU.panelOffset = tmpPanelOffset;
				tmpPanelOffset = null;
			}
			BypassMenu.BYPASSMENU.render();
			if (tmpLevelMenuLoc != null) {
				BypassMenu.BYPASSMENU.setLocation(tmpLevelMenuLoc);
				tmpLevelMenuLoc = null;
			}
			
		} finally {
			BypassMenu.BYPASSMENU.lock.unlock();
		}
		
		/*
		 * repaint once just in case there is nothing else driving repainting (like shimmering)
		 */
		APP.appScreen.contentPane.repaint();
	}
	
	public static void pause() {
		
		simThreadTrigger.set(false);
		
		try {
			simThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * we want to save tmpLoc when 
		 */
		BypassMenu.BYPASSMENU.lock.lock();
		try {
			
			if (BypassMenu.BYPASSMENU.rendered) {
				tmpLevelMenuLoc = new Point(BypassMenu.BYPASSMENU.aabb.x, BypassMenu.BYPASSMENU.aabb.y);
			}
			
		} finally {
			BypassMenu.BYPASSMENU.lock.unlock();
		}
		
	}
	
	public void escape() {
		
		APP.platform.finishAction();
	}
	
}
