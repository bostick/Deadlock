package com.brentonbostick.capsloc.world.graph;

public enum Axis {
	
	TOPBOTTOM,
	LEFTRIGHT;
	
	public static Axis fromFileString(String s) {
		if (s.equals("TOPBOTTOM")) {
			return Axis.TOPBOTTOM;
		} else if (s.equals("LEFTRIGHT")) {
			return Axis.LEFTRIGHT;
		}
		assert false;
		return null;
	}
	
}
