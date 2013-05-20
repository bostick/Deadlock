package com.brentonbostick.capsloc.world.graph;

public enum Direction {
	
	STARTTOEND,
	ENDTOSTART;
	
	public static Direction fromFileString(String s) {
		if (s.equals("STARTTOEND")) {
			return Direction.STARTTOEND;
		} else if (s.equals("ENDTOSTART")) {
			return Direction.ENDTOSTART;
		} else if (s.equals("null")) {
			return null;
		}
		assert false;
		return null;
	}
	
}
