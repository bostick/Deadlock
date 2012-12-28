package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;

public class WorldA extends World {
	
	private WorldA(WorldScreen screen) {
		super(screen);
	}
	
	public static WorldA createWorldA(WorldScreen screen) {
		
		int[][] ini = new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0}
			};
		
		WorldA w = new WorldA(screen);
		
		QuadrantMap qm = new QuadrantMap(w, ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
}
