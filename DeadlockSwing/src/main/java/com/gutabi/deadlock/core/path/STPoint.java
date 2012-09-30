package com.gutabi.deadlock.core.path;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;

public class STPoint {
	
	private Point s;
	private double t;
	
	int hash;
	
	public STPoint(Point s, double t) {
		this.s = s;
		this.t = t;
		
		if (DMath.equals(t, 1.0) && t != 1.0) {
			String.class.getName();
		}
		
		hash = s.hashCode();
	}
	
	public Point getSpace() {
		return s;
	}
	
	public double getTime() {
		return t;
	}
	
	public String toString() {
		return s + " @ " + t;
	}
	
	public int hashCode() {
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof STPoint)) {
			throw new IllegalArgumentException();
		} else {
			STPoint b = (STPoint)o;
			return s.equals(b.s) && DMath.equals(t, b.t);
		}
	}
	
}
