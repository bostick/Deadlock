package com.gutabi.deadlock.world.graph;

public enum Side {
	
	TOP,
	
	LEFT,
	
	RIGHT,
	
	BOTTOM;
	
	public Side other() {
		switch (this) {
		case TOP:
			return BOTTOM;
		case BOTTOM:
			return TOP;
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		}
		return null;
	}
	
	public static Side fromFileString(String s) {
		if (s.equals("TOP")) {
			return Side.TOP;
		} else if (s.equals("LEFT")) {
			return Side.LEFT;
		} else if (s.equals("RIGHT")) {
			return Side.RIGHT;
		} else if (s.equals("BOTTOM")) {
			return Side.BOTTOM;
		}
		assert false;
		return null;
	}
	
}
