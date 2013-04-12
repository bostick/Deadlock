package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.tools.RegularTool;

public class QuadrantEditorWorld extends World implements Model {
	
	public static QuadrantEditorWorld createWorld(int[][] ini) {
		
		QuadrantEditorWorld w = new QuadrantEditorWorld();
		
		QuadrantMap qm = new QuadrantMap(ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
	public static void action(int[][] ini) {
		
		QuadrantEditorWorld world = QuadrantEditorWorld.createWorld(ini);
		APP.model = world;
		
		AppScreen worldScreen = new AppScreen(new QuadrantEditorContentPane());
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
	
	public Menu getMenu() {
		return null;
	}

}
