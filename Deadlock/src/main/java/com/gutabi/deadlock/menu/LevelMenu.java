package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.examples.RushHourWorld;
import com.gutabi.deadlock.world.tools.InteractiveCarTool;

public class LevelMenu extends Menu {
	
	/*
	 * blank
	 */
//	char[][] boardIni = new char[][] {
//		{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
//		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//		{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
//	};
	
//	char[][] boardIni = new char[][] {
//	{' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' '},
//	{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//	{' ', 'A', 'A', 'A', 'X', 'X', 'X', ' '},
//	{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//	{' ', 'X', 'X', 'C', 'B', 'B', 'B', ' '},
//	{' ', 'X', 'X', 'C', 'R', 'X', 'X', ' '},
//	{'K', 'D', 'D', 'D', 'R', 'X', 'X', 'J'},
//	{' ', ' ', ' ', ' ', 'K', ' ', 'J', ' '}
//};
	
//	char[][] boardIni = new char[][] {
//	{' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' '},
//	{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//	{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//	{' ', 'A', 'A', 'A', 'X', 'B', 'B', ' '},
//	{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
//	{' ', 'X', 'X', 'X', 'R', 'X', 'X', ' '},
//	{'K', 'X', 'X', 'X', 'R', 'X', 'X', 'J'},
//	{' ', ' ', ' ', ' ', 'K', ' ', 'J', ' '}
//};
	
	public LevelMenu(Panel parPanel) {
		super(parPanel);
		
		add(new MenuItem(LevelMenu.this, "1-1") { public void action() { foo(APP.board11); } }, 0, 0);
		
		add(new MenuItem(LevelMenu.this, "1-2") { public void action() { foo(RushHourWorld.cw90(APP.board11)); } }, 1, 0);
		
		add(new MenuItem(LevelMenu.this, "2-1") { public void action() { foo(RushHourWorld.cw90(RushHourWorld.cw90(APP.board11))); } }, 0, 1);
		
		add(new MenuItem(LevelMenu.this, "2-2") { public void action() { foo(RushHourWorld.cw90(RushHourWorld.cw90(RushHourWorld.cw90(APP.board11)))); } }, 1, 1);
	}
	
	public void foo(char[][] board) {
		
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
		
	}
	
	public void render() {
		
		super.render();
		
		aabb = new AABB(parPanel.aabb.width/2 - aabb.width/2, parPanel.aabb.height/2 - aabb.height/2, aabb.width, aabb.height);
		
	}
	
}
