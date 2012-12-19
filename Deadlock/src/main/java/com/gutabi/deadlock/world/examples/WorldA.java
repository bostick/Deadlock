package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldCamera;

public class WorldA extends World {
	
	private WorldA(WorldCamera cam) {
		super(cam);
	}
	
	public static WorldA createWorldA(WorldCamera cam) {
		
		int[][] ini = new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0}
			};
		
		WorldA w = new WorldA(cam);
		
		QuadrantMap qm = new QuadrantMap(cam, ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
}
