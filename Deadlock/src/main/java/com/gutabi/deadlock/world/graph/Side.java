package com.gutabi.deadlock.world.graph;

import com.gutabi.deadlock.math.DMath;

public enum Side {
	
	TOP { public Side other() { return BOTTOM; } public double getAngle() { return 1.5 * Math.PI; } },
	LEFT { public Side other() { return RIGHT; } public double getAngle() { return 1.0 * Math.PI; } },
	RIGHT { public Side other() { return LEFT; } public double getAngle() { return 0.0 * Math.PI; } },
	BOTTOM { public Side other() { return TOP; } public double getAngle() { return 0.5 * Math.PI; } };
	
	public abstract Side other();
	public abstract double getAngle();
	
	public static Side angleToSide(double angle) {
		if (DMath.equals(angle, 0.0 * Math.PI)) {
			return RIGHT;
		} else if (DMath.equals(angle, 0.5 * Math.PI)) {
			return BOTTOM;
		} else if (DMath.equals(angle, 1.0 * Math.PI)) {
			return LEFT;
		} else if (DMath.equals(angle, 1.5 * Math.PI)) {
			return TOP;
		} else {
			return null;
		}
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
