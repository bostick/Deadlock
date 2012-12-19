package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldCamera;

public class OneByOneWorld extends World {

	private OneByOneWorld(WorldCamera cam) {
		super(cam);
	}
	
	public static OneByOneWorld createOneByOneWorld(WorldCamera cam) {
		
		int[][] ini = new int[][] {
				{1}
			};
		
		OneByOneWorld w = new OneByOneWorld(cam);
		
		QuadrantMap qm = new QuadrantMap(cam, ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
}
