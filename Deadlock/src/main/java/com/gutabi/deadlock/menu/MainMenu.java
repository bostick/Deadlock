package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.quadranteditor.QuadrantEditor;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.examples.FourByFourGridWorld;
import com.gutabi.deadlock.world.examples.OneByOneWorld;
import com.gutabi.deadlock.world.examples.WorldA;
import com.gutabi.deadlock.world.tools.RegularTool;

public class MainMenu extends Menu {
	
	public MainMenu() {
		
		MenuItem oneMenuItem = new MenuItem(MainMenu.this,"1x1 Demo") {
			public void action() {
				
				WorldScreen worldScreen = new WorldScreen();
				APP.setAppScreen(worldScreen);
				
				DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
				APP.debuggerScreen = debuggerScreen;
				
				APP.tool = new RegularTool(worldScreen, debuggerScreen);
				
				worldScreen.contentPane.worldPanel.world = OneByOneWorld.createOneByOneWorld(worldScreen, debuggerScreen);
				
				APP.platform.setupAppScreen(worldScreen.contentPane.cp);
				
				APP.platform.setupDebuggerScreen(debuggerScreen.contentPane.cp);
				
				worldScreen.postDisplay();
				
				debuggerScreen.postDisplay();
				
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.worldPanel.world.render_preview();
				worldScreen.contentPane.repaint();
				debuggerScreen.contentPane.repaint();
				
				APP.platform.showAppScreen();
				APP.platform.showDebuggerScreen();
			}
		};
		add(oneMenuItem);
		
		MenuItem fourMenuItem = new MenuItem(MainMenu.this, "4x4 Grid Demo") {
			public void action() {
				
				WorldScreen worldScreen = new WorldScreen();
				APP.setAppScreen(worldScreen);
				
				DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
				APP.debuggerScreen = debuggerScreen;
				
				APP.tool = new RegularTool(worldScreen, debuggerScreen);
				
				worldScreen.contentPane.worldPanel.world = FourByFourGridWorld.createFourByFourGridWorld(worldScreen, debuggerScreen);
				
				APP.platform.setupAppScreen(worldScreen.contentPane.cp);
				
				APP.platform.setupDebuggerScreen(debuggerScreen.contentPane.cp);
				
				worldScreen.postDisplay();
				
				debuggerScreen.postDisplay();
				
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.worldPanel.world.render_preview();
				worldScreen.contentPane.repaint();
				
				APP.platform.showAppScreen();
				APP.platform.showDebuggerScreen();
			}
		};
		add(fourMenuItem);
		
		MenuItem aMenuItem = new MenuItem(MainMenu.this, "World A Demo") {
			public void action() {
				
				WorldScreen worldScreen = new WorldScreen();
				APP.setAppScreen(worldScreen);
				
				DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
				APP.debuggerScreen = debuggerScreen;
				
				APP.tool = new RegularTool(worldScreen, debuggerScreen);
				
				worldScreen.contentPane.worldPanel.world = WorldA.createWorldA(worldScreen, debuggerScreen);
				
				APP.platform.setupAppScreen(worldScreen.contentPane.cp);
				
				APP.platform.setupDebuggerScreen(debuggerScreen.contentPane.cp);
				
				worldScreen.postDisplay();
				
				debuggerScreen.postDisplay();
				
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.worldPanel.world.render_preview();
				worldScreen.contentPane.repaint();
				
				APP.platform.showAppScreen();
				APP.platform.showDebuggerScreen();
			}
		};
		add(aMenuItem);
		
		MenuItem rMenuItem = new MenuItem(MainMenu.this, "Rush Hour") {
			public void action() {
				
				((MainMenuScreen)APP.appScreen).contentPane.panel.menu = new LevelMenu();
				
				((MainMenuScreen)APP.appScreen).contentPane.panel.render();
				((MainMenuScreen)APP.appScreen).contentPane.repaint();
			}
		};
		add(rMenuItem);
		
		MenuItem dialogMenuItem = new MenuItem(MainMenu.this,  "Quadrant Editor...") {
			public void action() {
				
				QuadrantEditor s = new QuadrantEditor();
				
				APP.platform.setupAppScreen(s.contentPane.cp);
				
				s.postDisplay();
				
				s.contentPane.panel.worldPanel.world.render_worldPanel();
				s.contentPane.repaint();
				
				APP.platform.showAppScreen();
			}
		};
		add(dialogMenuItem);
		
		MenuItem puzzleMenuItem = new MenuItem(MainMenu.this,  "Puzzle Mode") {
			public void action() {
				
			}
		};
		puzzleMenuItem.active = false;
		add(puzzleMenuItem);
		
		MenuItem loadMenuItem = new MenuItem(MainMenu.this, "Load...") {
			public void action() {
				
			}
		};
		loadMenuItem.active = false;
		add(loadMenuItem);
		
		MenuItem captureMenuItem = new MenuItem(MainMenu.this, "Capture the Flag") {
			public void action() {
				
			}
		};
		captureMenuItem.active = false;
		add(captureMenuItem);
		
		MenuItem quitMenuItem = new MenuItem(MainMenu.this, "Quit") {
			public void action() {
				
				APP.exit();
				
			}
		};
		add(quitMenuItem);
		
	}
	
	
	int MENU_CENTER_Y = (854/2);
	
	public void render() {
		
		super.render();
		
		aabb = new AABB(APP.WINDOW_WIDTH/2 - aabb.width/2, MENU_CENTER_Y - aabb.height/2, aabb.width, aabb.height);
		
	}
	
}
