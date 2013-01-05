package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.World;

public class RushHourStud {
	
	public static double SIZE = 1.0;
	
	World world;
	RushHourBoard board;
	
	int row;
	int col;
	
	AABB aabb;
	
	public RushHourStud(World world, RushHourBoard board, int row, int col) {
		this.world = world;
		this.board = board;
		
		aabb = new AABB(board.aabb.ul.x + RushHourStud.SIZE * col, board.aabb.ul.y + RushHourStud.SIZE * row, RushHourStud.SIZE, RushHourStud.SIZE);
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.paintImage(APP.spriteSheet, world.screen.pixelsPerMeter,
				aabb.ul.x, aabb.ul.y, aabb.ul.x + aabb.width, aabb.ul.y + aabb.height,
				160, 0, 160+32, 0+32);
		
	}
	
}
