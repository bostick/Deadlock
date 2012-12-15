package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.world.World;

public class WorldA extends World {

	public WorldA() {
		super(new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0}
			}
		);
		
		APP.PIXELS_PER_METER = 10;
		
	}
	
	public void canvasPostDisplay(Dim d) {
		super.canvasPostDisplay(d);
		
		worldViewport = new AABB(0, 0, worldWidth, worldHeight);
		
	}

}
