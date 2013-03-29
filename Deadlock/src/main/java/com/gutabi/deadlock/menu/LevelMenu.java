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
	
	public LevelMenu(Panel parPanel) {
		super(parPanel);
		
		MenuItem oneMenuItem = new MenuItem(LevelMenu.this, "1") {
			public void action() {
				
				WorldScreen worldScreen = new WorldScreen();
				APP.setAppScreen(worldScreen);
				
				DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
				APP.debuggerScreen = debuggerScreen;
				
				APP.tool = new InteractiveCarTool(worldScreen, debuggerScreen);
				
				worldScreen.contentPane.worldPanel.world = RushHourWorld.createRushHourWorld(worldScreen, APP.debuggerScreen);
				
				APP.platform.setupAppScreen(worldScreen.contentPane.cp);
				
				APP.platform.setupDebuggerScreen(APP.debuggerScreen.contentPane.cp);
				
				worldScreen.postDisplay();
				
				APP.debuggerScreen.postDisplay();
				
				worldScreen.contentPane.worldPanel.world.startRunning();
				
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.worldPanel.world.render_preview();
				worldScreen.contentPane.repaint();
				
				APP.platform.showAppScreen();
				APP.platform.showDebuggerScreen();
				
			}
		};
		add(oneMenuItem, 0, 0);
		
		MenuItem twoMenuItem = new MenuItem(LevelMenu.this, "2") {
			public void action() {
				
				WorldScreen worldScreen = new WorldScreen();
				APP.setAppScreen(worldScreen);
				
				DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
				APP.debuggerScreen = debuggerScreen;
				
				APP.tool = new InteractiveCarTool(worldScreen, debuggerScreen);
				
				worldScreen.contentPane.worldPanel.world = RushHourWorld.createRushHourWorld(worldScreen, APP.debuggerScreen);
				
				APP.platform.setupAppScreen(worldScreen.contentPane.cp);
				
				APP.platform.setupDebuggerScreen(APP.debuggerScreen.contentPane.cp);
				
				worldScreen.postDisplay();
				
				APP.debuggerScreen.postDisplay();
				
				worldScreen.contentPane.worldPanel.world.startRunning();
				
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.worldPanel.world.render_preview();
				worldScreen.contentPane.repaint();
				
				APP.platform.showAppScreen();
				APP.platform.showDebuggerScreen();
				
			}
		};
		add(twoMenuItem, 1, 0);
		
		MenuItem threeMenuItem = new MenuItem(LevelMenu.this, "3") {
			public void action() {
				
				WorldScreen worldScreen = new WorldScreen();
				APP.setAppScreen(worldScreen);
				
				DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
				APP.debuggerScreen = debuggerScreen;
				
				APP.tool = new InteractiveCarTool(worldScreen, debuggerScreen);
				
				worldScreen.contentPane.worldPanel.world = RushHourWorld.createRushHourWorld(worldScreen, APP.debuggerScreen);
				
				APP.platform.setupAppScreen(worldScreen.contentPane.cp);
				
				APP.platform.setupDebuggerScreen(APP.debuggerScreen.contentPane.cp);
				
				worldScreen.postDisplay();
				
				APP.debuggerScreen.postDisplay();
				
				worldScreen.contentPane.worldPanel.world.startRunning();
				
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.worldPanel.world.render_preview();
				worldScreen.contentPane.repaint();
				
				APP.platform.showAppScreen();
				APP.platform.showDebuggerScreen();
				
			}
		};
		add(threeMenuItem, 2, 0);
		
	}
	
	public void render() {
		
		super.render();
		
		aabb = new AABB(parPanel.aabb.width/2 - aabb.width/2, parPanel.aabb.height/2 - aabb.height/2, aabb.width, aabb.height);
		
	}
	
}
