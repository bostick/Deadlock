package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;

public class OneByOneWorld extends World {

	private OneByOneWorld(WorldScreen screen) {
		super(screen);
	}
	
	public static OneByOneWorld createOneByOneWorld(WorldScreen screen) {
		
		int[][] ini = new int[][] {
				{1}
			};
		
		OneByOneWorld w = new OneByOneWorld(screen);
		
		QuadrantMap qm = new QuadrantMap(w, ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
}
