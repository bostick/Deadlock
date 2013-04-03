package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.gen.BoardsIndex;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.examples.RushHourWorld;
import com.gutabi.deadlock.world.tools.InteractiveCarTool;

public class LevelMenu extends Menu {
	
	public LevelMenu(Panel parPanel) {
		super(parPanel);
		
		for (int i = 0; i < BoardsIndex.table.length; i++) {
			int menuRow = i / 3;
			int menuCol = i % 3;
			final int ii = i;
			add(new MenuItem(LevelMenu.this, Integer.toString(i), 48) { public void action() { foo(ii); } }, menuRow, menuCol);
		}
		
	}
	
	public void foo(int index) {
		
		try {
			
			String id = BoardsIndex.table[index];
			
			char[][] board = APP.platform.readBoard(APP.platform.boardResource(id));
			
			WorldScreen worldScreen = new WorldScreen();
			APP.setAppScreen(worldScreen);
			
			DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
			APP.debuggerScreen = debuggerScreen;
			
			APP.tool = new InteractiveCarTool(worldScreen, debuggerScreen);
			
			worldScreen.contentPane.worldPanel.world = RushHourWorld.createRushHourWorld(board, worldScreen, APP.debuggerScreen);
			
			APP.platform.setupAppScreen(worldScreen.contentPane.pcp);
			
			APP.platform.setupDebuggerScreen(APP.debuggerScreen.contentPane.pcp);
			
			worldScreen.postDisplay();
			
			APP.debuggerScreen.postDisplay();
			
			worldScreen.contentPane.worldPanel.world.startRunning();
			
			worldScreen.contentPane.worldPanel.world.render_worldPanel();
			worldScreen.contentPane.worldPanel.world.render_preview();
			worldScreen.contentPane.repaint();
			
			APP.platform.showAppScreen();
			APP.platform.showDebuggerScreen();
			
		} catch (Exception e) {
			assert false;
		}
		
	}
	
	public void render() {
		
		super.render();
		
		aabb = new AABB(parPanel.aabb.width/2 - aabb.width/2, parPanel.aabb.height/2 - aabb.height/2, aabb.width, aabb.height);
		
	}
	
}
