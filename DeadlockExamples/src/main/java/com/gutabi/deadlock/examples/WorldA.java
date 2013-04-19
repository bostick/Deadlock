package com.gutabi.deadlock.examples;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldPanel;
import com.gutabi.deadlock.world.tools.RegularTool;

public class WorldA extends World implements Model {
	
	private WorldA() {
		
	}
	
	public static void action() {
		
		WorldA world = WorldA.createWorldA();
		APP.model = world;
		
		AppScreen worldScreen = new AppScreen(new ContentPane(new WorldPanel()));
		APP.setAppScreen(worldScreen);
		
		AppScreen debuggerScreen = new AppScreen(new ContentPane(new ControlPanel()));
		APP.debuggerScreen = debuggerScreen;
		
		APP.tool = new RegularTool();
		
		APP.platform.setupAppScreen(worldScreen.contentPane.pcp);
		
		APP.platform.setupDebuggerScreen(APP.debuggerScreen.contentPane.pcp);
		
		worldScreen.postDisplay();
		
		APP.debuggerScreen.postDisplay();
		
		world.startRunning();
		
		world.render_worldPanel();
		world.render_preview();
		
		APP.platform.showAppScreen();
		APP.platform.showDebuggerScreen();
		
	}
	
	public static WorldA createWorldA() {
		
		int[][] ini = new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0}
			};
		
		WorldA w = new WorldA();
		
		QuadrantMap qm = new QuadrantMap(ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
	public Menu getMenu() {
		return null;
	}
	
}
