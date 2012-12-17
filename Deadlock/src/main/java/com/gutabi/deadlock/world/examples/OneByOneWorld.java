package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;

public class OneByOneWorld extends World {

	private OneByOneWorld() {
		
	}
	
	public static OneByOneWorld createOneByOneWorld() {
		
		int[][] ini = new int[][] {
				{1}
			};
		
		OneByOneWorld w = new OneByOneWorld();
		
		QuadrantMap qm = new QuadrantMap(w, ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
}
