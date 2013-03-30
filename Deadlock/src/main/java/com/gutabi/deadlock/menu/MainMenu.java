package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.quadranteditor.QuadrantEditor;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.MenuItem;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.examples.FourByFourGridWorld;
import com.gutabi.deadlock.world.examples.OneByOneWorld;
import com.gutabi.deadlock.world.examples.WorldA;
import com.gutabi.deadlock.world.tools.RegularTool;

public class MainMenu extends Menu {
	
	String layout =
			"<(0)>\n" +
			"<(1)>\n" +
			"<(2)>\n" +
			"<(3)>\n" +
			"<(4)>  (9)\n" +
			"<(5)>\n" +
			"<(6)>\n" +
			"<(7)>\n" +
			"(8)\n";
	
	public MainMenu(Panel parPanel) {
		super(parPanel);
		
		MenuItem oneMenuItem = new MenuItem(MainMenu.this,"1x1 Demo") {
			public void action() {
				
				WorldScreen worldScreen = new WorldScreen();
				APP.setAppScreen(worldScreen);
				
				DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
				APP.debuggerScreen = debuggerScreen;
				
				APP.tool = new RegularTool(worldScreen, debuggerScreen);
				
				worldScreen.contentPane.worldPanel.world = OneByOneWorld.createOneByOneWorld(worldScreen, debuggerScreen);
				
				APP.platform.setupAppScreen(worldScreen.contentPane.pcp);
				
				APP.platform.setupDebuggerScreen(debuggerScreen.contentPane.pcp);
				
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
		add(oneMenuItem, 0, 0);
		
		MenuItem fourMenuItem = new MenuItem(MainMenu.this, "4x4 Grid Demo") {
			public void action() {
				
				WorldScreen worldScreen = new WorldScreen();
				APP.setAppScreen(worldScreen);
				
				DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
				APP.debuggerScreen = debuggerScreen;
				
				APP.tool = new RegularTool(worldScreen, debuggerScreen);
				
				worldScreen.contentPane.worldPanel.world = FourByFourGridWorld.createFourByFourGridWorld(worldScreen, debuggerScreen);
				
				APP.platform.setupAppScreen(worldScreen.contentPane.pcp);
				
				APP.platform.setupDebuggerScreen(debuggerScreen.contentPane.pcp);
				
				worldScreen.postDisplay();
				
				debuggerScreen.postDisplay();
				
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.worldPanel.world.render_preview();
				worldScreen.contentPane.repaint();
				
				APP.platform.showAppScreen();
				APP.platform.showDebuggerScreen();
			}
		};
		add(fourMenuItem, 1, 0);
		
		MenuItem aMenuItem = new MenuItem(MainMenu.this, "World A Demo") {
			public void action() {
				
				WorldScreen worldScreen = new WorldScreen();
				APP.setAppScreen(worldScreen);
				
				DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
				APP.debuggerScreen = debuggerScreen;
				
				APP.tool = new RegularTool(worldScreen, debuggerScreen);
				
				worldScreen.contentPane.worldPanel.world = WorldA.createWorldA(worldScreen, debuggerScreen);
				
				APP.platform.setupAppScreen(worldScreen.contentPane.pcp);
				
				APP.platform.setupDebuggerScreen(debuggerScreen.contentPane.pcp);
				
				worldScreen.postDisplay();
				
				debuggerScreen.postDisplay();
				
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.worldPanel.world.render_preview();
				worldScreen.contentPane.repaint();
				
				APP.platform.showAppScreen();
				APP.platform.showDebuggerScreen();
			}
		};
		add(aMenuItem, 2, 0);
		
		MenuItem rMenuItem = new MenuItem(MainMenu.this, "Rush Hour") {
			public void action() {
				
				((MainMenuScreen)APP.appScreen).contentPane.panel.menu = new LevelMenu(MainMenu.this.parPanel);
				
				((MainMenuScreen)APP.appScreen).contentPane.panel.render();
				((MainMenuScreen)APP.appScreen).contentPane.repaint();
			}
		};
		add(rMenuItem, 3, 0);
		
		MenuItem dialogMenuItem = new MenuItem(MainMenu.this,  "Quadrant Editor...") {
			public void action() {
				
				QuadrantEditor s = new QuadrantEditor();
				
				APP.platform.setupAppScreen(s.contentPane.pcp);
				
				s.postDisplay();
				
				s.contentPane.panel.worldPanel.world.render_worldPanel();
				s.contentPane.repaint();
				
				APP.platform.showAppScreen();
			}
		};
		add(dialogMenuItem, 4, 0);
		
		MenuItem puzzleMenuItem = new MenuItem(MainMenu.this,  "Puzzle Mode") {
			public void action() {
				
			}
		};
		puzzleMenuItem.active = false;
		add(puzzleMenuItem, 5, 0);
		
		MenuItem loadMenuItem = new MenuItem(MainMenu.this, "Load...") {
			public void action() {
				
			}
		};
		loadMenuItem.active = false;
		add(loadMenuItem, 6, 0);
		
		MenuItem captureMenuItem = new MenuItem(MainMenu.this, "Capture the Flag") {
			public void action() {
				
			}
		};
		captureMenuItem.active = false;
		add(captureMenuItem, 7, 0);
		
		MenuItem quitMenuItem = new MenuItem(MainMenu.this, "Quit") {
			public void action() {
				
				APP.exit();
				
			}
		};
		add(quitMenuItem, 8, 0);
		
		MenuItem testItem = new MenuItem(MainMenu.this, "Test") {
			public void action() {
				
			}
		};
		add(testItem, 4, 1);
		
		add(new MenuItem(MainMenu.this, " ") { {active = false;} public void action() { } }, 0, 1);
		add(new MenuItem(MainMenu.this, " ") { {active = false;} public void action() { } }, 1, 1);
		add(new MenuItem(MainMenu.this, " ") { {active = false;} public void action() { } }, 2, 1);
		add(new MenuItem(MainMenu.this, " ") { {active = false;} public void action() { } }, 3, 1);
	}
	
	public void render() {
		
		super.render();
		
		aabb = new AABB(parPanel.aabb.width/2 - aabb.width/2, parPanel.aabb.height/2 - aabb.height/2, aabb.width, aabb.height);
		
	}
	
}
