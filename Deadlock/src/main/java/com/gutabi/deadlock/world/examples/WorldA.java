package com.gutabi.deadlock.world.examples;

import com.gutabi.deadlock.world.World;

public class WorldA extends World {

	public WorldA() {
		super(new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0}
			}
		);
	}

}
