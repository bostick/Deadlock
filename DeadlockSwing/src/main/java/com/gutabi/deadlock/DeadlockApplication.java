package com.gutabi.deadlock;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import java.net.URL;

import com.gutabi.deadlock.menu.Menu;
import com.gutabi.deadlock.world.Preview;
import com.gutabi.deadlock.world.World;

public class DeadlockApplication {
	
	public URL codebase;
	
	public static final double QUADRANT_WIDTH = 16.0;
	public static final double QUADRANT_HEIGHT = QUADRANT_WIDTH;
	
	/**
	 * move physics forward by dt seconds
	 */
	public static double dt = 0.01;
	
	public static boolean FPS_DRAW = false;
	public static boolean STOPSIGN_DRAW = true;
	public static boolean CARTEXTURE_DRAW = true;
	public static boolean EXPLOSIONS_DRAW = true;
	public static boolean DEBUG_DRAW = false;
	
	public Menu menu;
	
	public World world;
	
	public Preview preview = new Preview();
	
	public static DeadlockApplication APP = new DeadlockApplication();
	
	public void init() throws Exception {
		switch (CONTROLLER.mode) {
		case MENU:
			break;
		case QUADRANTEDITOR:
			break;
		case WORLD:
			world.init();
			preview.init();
			break;
		}
	}
	
	public void render() {
		switch (CONTROLLER.mode) {
		case MENU:
			break;
		case QUADRANTEDITOR:
			break;
		case WORLD:
			world.render();
			preview.render();
			break;
		}
	}
	
}
