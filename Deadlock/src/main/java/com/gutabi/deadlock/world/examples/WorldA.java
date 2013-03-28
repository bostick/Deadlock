package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;

public class WorldA extends World {
	
	private WorldA(WorldScreen screen, DebuggerScreen debuggerScreen) {
		super(screen, debuggerScreen);
	}
	
	public static WorldA createWorldA(WorldScreen screen, DebuggerScreen debuggerScreen) {
		
		int[][] ini = new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0}
			};
		
		WorldA w = new WorldA(screen, debuggerScreen);
		
		QuadrantMap qm = new QuadrantMap(ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
}
