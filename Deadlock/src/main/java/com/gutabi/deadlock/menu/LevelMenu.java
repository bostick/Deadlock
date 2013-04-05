package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.gen.BoardsIndex;
import com.gutabi.deadlock.rushhour.RushHourWorld;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;

public class LevelMenu extends Menu {
	
	public LevelMenu() {
		
		for (int i = 0; i < BoardsIndex.table.length; i++) {
			int menuRow = i / 7;
			int menuCol = i % 7;
			final int ii = i;
			add(new MenuItem(LevelMenu.this, Integer.toString(i), 48) { public void action() { RushHourWorld.action(ii); } }, menuRow, menuCol);
		}
		
	}
	
	public static void action() {
		
		LevelMenu levelMenu = new LevelMenu();
		APP.model = levelMenu;
		
		levelMenu.render();
		
		APP.appScreen.postDisplay();
		
		APP.appScreen.contentPane.repaint();
		
	}
	
	public void escape() {
		
		MainMenu.action();
		
	}
	
}
