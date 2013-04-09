package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.rushhour.LevelDB;
import com.gutabi.deadlock.rushhour.RushHourWorld;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;

public class LevelMenu extends Menu {
	
	public LevelMenu(final LevelDB levelDB) {
		
		for (int i = 0; i < levelDB.levelCount; i++) {
			int menuRow = i / 7;
			int menuCol = i % 7;
			final int ii = i;
			add(new MenuItem(LevelMenu.this, Integer.toString(i), 48) { public void action() { RushHourWorld.action(levelDB, ii); } }, menuRow, menuCol);
		}
		
	}
	
	public static void action() {
		
		try {
			
			LevelDB levelDB = new LevelDB(APP.platform.levelDBResource("levels"));
			
			LevelMenu levelMenu = new LevelMenu(levelDB);
			APP.model = levelMenu;
			
			levelMenu.render();
			
			APP.appScreen.postDisplay();
			
			APP.appScreen.contentPane.repaint();
			
		} catch (Exception e) {
			e.printStackTrace();
			assert false;
		}
		
	}
	
	public void escape() {
		
		MainMenu.action();
		
	}
	
}
