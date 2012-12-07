package com.gutabi.deadlock.model.graph;

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
	
}
