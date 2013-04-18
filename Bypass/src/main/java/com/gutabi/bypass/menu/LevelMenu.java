package com.gutabi.bypass.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.bypass.level.LevelDB;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;

public class LevelMenu extends Menu implements Model {
	
	public LevelMenu(final LevelDB levelDB) {
		for (int i = 0; i < levelDB.levelCount; i++) {
			int menuRow = i / 4;
			int menuCol = i % 4;
			final int ii = i;
			add(new MenuItem(LevelMenu.this, Integer.toString(i)) {
				
				public void action() {
					BypassWorld.action(levelDB, ii);
				}
				
			}, menuRow, menuCol);
		}
	}
	
	public static void action() {
		
		try {
			
			LevelDB levelDB = new LevelDB(APP.platform.levelDBResource("levels"));
			
			LevelMenu levelMenu = new LevelMenu(levelDB);
			APP.model = levelMenu;
			
			levelMenu.render();
			
			APP.appScreen.postDisplay();
			
		} catch (Exception e) {
			e.printStackTrace();
			assert false;
		}
	}
	
	public Menu getMenu() {
		return this;
	}
	
	public void escape() {
		
		MainMenu.action();
	}
	
}
