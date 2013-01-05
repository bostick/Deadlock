package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.World;

public class RushHourStud {
	
	World world;
	AABB aabb;
	
	public RushHourStud(World world, AABB aabb) {
		this.world = world;
		this.aabb = aabb;
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.paintImage(APP.spriteSheet, world.screen.pixelsPerMeter,
				aabb.ul.x, aabb.ul.y, aabb.ul.x + 1, aabb.ul.y + 1,
				160, 0, 160+32, 0+32);
		
	}
	
}
