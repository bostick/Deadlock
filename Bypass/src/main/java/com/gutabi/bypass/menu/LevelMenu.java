package com.gutabi.bypass.menu;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.util.List;

import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.bypass.level.LevelDB;
import com.gutabi.capsloc.AppScreen;
import com.gutabi.capsloc.ui.ContentPane;
import com.gutabi.capsloc.ui.MenuItem;
import com.gutabi.capsloc.ui.MenuTool;

public class LevelMenu extends BypassMenu {
	
	public static LevelDB levelDB;
	
	public static boolean showInfo = false;
	
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
	
	public void escape() {
		
		APP.platform.finishAction();
	}
	
}
