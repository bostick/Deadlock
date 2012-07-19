package com.gutabi.deadlock.swing.utils;

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
	
	@Override
	public boolean equals(Object p) {
		if (this == p) {
			return true;
		} else if (!(p instanceof DPoint)) {
			throw new IllegalArgumentException();
		} else {
			return Point.doubleEquals(x, ((DPoint)p).x) && Point.doubleEquals(y, ((DPoint)p).y);
		}
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
