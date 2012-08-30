package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

public class Fixture extends Vertex {
	
	private final Direction dir;
	
	public Fixture(Point p) {
		super(p);
		
		if (p.getX() >= MODEL.WORLD_WIDTH-10) {
			dir = Direction.EAST;
		} else if (p.getY() >= MODEL.WORLD_HEIGHT-10) {
			dir = Direction.SOUTH;
		} else if (p.getY() <= 10) {
			dir = Direction.NORTH;
		} else if (p.getX() <= 10) {
			dir = Direction.WEST;
		} else {
			throw new AssertionError();
		}

	}
	
	public Direction getDirection() {
		return dir;
	}

}
