package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.tools.RegularTool;

public class QuadrantEditorWorld {
	
	public static World createWorld(int[][] ini) {
		
		World w = new World();
		
		QuadrantMap qm = new QuadrantMap(ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
	public static void action(int[][] ini) {
		
		World world = QuadrantEditorWorld.createWorld(ini);
		APP.model = world;
		
		WorldScreen worldScreen = new WorldScreen();
		APP.setAppScreen(worldScreen);
		
		APP.tool = new RegularTool();
		
		APP.platform.setupAppScreen(worldScreen.contentPane.pcp);
		
		worldScreen.postDisplay();
		
		world.render_worldPanel();
		world.render_preview();
		worldScreen.contentPane.repaint();
		
		APP.platform.showAppScreen();
		APP.platform.showDebuggerScreen();
	}

}
