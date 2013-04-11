package com.gutabi.deadlock.examples;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.tools.RegularTool;

public class OneByOneWorld extends World implements Model {

	private OneByOneWorld() {
		
	}
	
	public static void action() {
		
		OneByOneWorld world = OneByOneWorld.createOneByOneWorld();
		APP.model = world;
		
		WorldScreen worldScreen = new WorldScreen();
		APP.setAppScreen(worldScreen);
		
		DebuggerScreen debuggerScreen = new DebuggerScreen(worldScreen);
		ControlPanel controlPanel = new ControlPanel() {{
			setLocation(0, 0);
		}};
		debuggerScreen.contentPane.pcp.getChildren().add(controlPanel);
		APP.debuggerScreen = debuggerScreen;
		
		APP.tool = new RegularTool();
		
		APP.platform.setupAppScreen(worldScreen.contentPane.pcp);
		
		APP.platform.setupDebuggerScreen(debuggerScreen.contentPane.pcp);
		
		worldScreen.postDisplay();
		
		debuggerScreen.postDisplay();
		
		world.render_worldPanel();
		world.render_preview();
		worldScreen.contentPane.repaint();
		debuggerScreen.contentPane.repaint();
		
		APP.platform.showAppScreen();
		APP.platform.showDebuggerScreen();
		
	}
	
	public static OneByOneWorld createOneByOneWorld() {
		
		int[][] ini = new int[][] {
				{1}
			};
		
		OneByOneWorld w = new OneByOneWorld();
		
		QuadrantMap qm = new QuadrantMap(ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
	public Menu getMenu() {
		return null;
	}
	
}
