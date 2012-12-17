package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;

public class WorldA extends World {
	
	private WorldA() {
		
	}
	
	public static WorldA createWorldA() {
		
		int[][] ini = new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0}
			};
		
		WorldA w = new WorldA();
		
		QuadrantMap qm = new QuadrantMap(w, ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
}
