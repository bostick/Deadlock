package com.gutabi.core;

public class DPoint {
	
	public final double x;
	public final double y;
	
	public final int hash;
	public final String s;
	
	public DPoint(double x, double y) {
		
		this.x = x;
		this.y = y;
		
		int h = 17;
		h = 37 * h + (int)x;
		h = 37 * h + (int)y;
		hash = h;
		
		s = "<" + x + ", " + y + ">";
	}
	
	public static boolean equals(DPoint a, DPoint b) {
		return Point.doubleEquals(a.x, b.x) && Point.doubleEquals(a.y, b.y);
	}
	
	@Override
	public boolean equals(Object p) {
		throw new AssertionError();
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return s;
	}
	
}
