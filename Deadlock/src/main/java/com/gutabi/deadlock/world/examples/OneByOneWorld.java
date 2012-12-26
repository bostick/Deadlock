package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.view.ControlPanel;
import com.gutabi.deadlock.world.QuadrantMap;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldCamera;

public class OneByOneWorld extends World {

	private OneByOneWorld(WorldCamera cam, ControlPanel cp) {
		super(cam, cp);
	}
	
	public static OneByOneWorld createOneByOneWorld(WorldCamera cam, ControlPanel cp) {
		
		int[][] ini = new int[][] {
				{1}
			};
		
		OneByOneWorld w = new OneByOneWorld(cam, cp);
		
		QuadrantMap qm = new QuadrantMap(cam, ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
}
