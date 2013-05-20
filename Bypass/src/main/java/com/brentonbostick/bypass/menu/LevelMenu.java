package com.brentonbostick.bypass.menu;

import static com.brentonbostick.bypass.BypassApplication.BYPASSAPP;
import static com.brentonbostick.capsloc.CapslocApplication.APP;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brentonbostick.bypass.level.BypassWorld;
import com.brentonbostick.bypass.level.LevelDB;
import com.brentonbostick.capsloc.AppScreen;
import com.brentonbostick.capsloc.ui.ContentPane;
import com.brentonbostick.capsloc.ui.MenuItem;
import com.brentonbostick.capsloc.ui.MenuTool;

public class LevelMenu extends BypassMenu {
	
	public static String levelDBName;
	
	
	public LevelDB levelDB;
	public boolean showInfo;
	
	public static Map<String, LevelMenu> map = new HashMap<String, LevelMenu>();
	
	public LevelMenu(String name) {
		
		levelDB = BYPASSAPP.bypassPlatform.levelDB(name);
		
		for (int i = 0; i < levelDB.levelCount; i++) {
			int menuRow = i / 4;
			int menuCol = i % 4;
			final int ii = i;
			add(new LevelMenuItem(this, i) {
				
				public void action() {
					
					APP.platform.action(BypassWorld.class, ii);
				}
				
			}, menuRow, menuCol);
		}
		
		updateFirstUnwon();
		
	}
	
	public void updateFirstUnwon() {
		if (levelDB.firstUnwon != -1) {
			int firstUnwonRow = levelDB.firstUnwon / 4;
			int firstUnwonCol = levelDB.firstUnwon % 4;
			List<MenuItem> col = tree.get(firstUnwonCol);
			MenuItem item = col.get(firstUnwonRow);
			
			shimmeringMenuItem = item;
			
		} else {
			shimmeringMenuItem = null;
		}
	}
	
	public static void create() {
		
	}
	
	public static void destroy() {
		
	}
	
	public static void start() {
		
		APP.tool = new MenuTool();
		
		BypassMenu.BYPASSMENU = LevelMenu.map.get(LevelMenu.levelDBName);
		APP.model = BypassMenu.BYPASSMENU;
		
		AppScreen s = new AppScreen(new ContentPane(new BypassMenuPanel()));
		APP.appScreen = s;
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
	}
	
	public static void stop() {
		
	}
	
	public void escape() {
		
		APP.platform.finishAction();
	}
	
}
