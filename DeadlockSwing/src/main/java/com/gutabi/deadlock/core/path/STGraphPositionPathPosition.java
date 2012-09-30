package com.gutabi.deadlock.core.path;

import com.gutabi.deadlock.core.DMath;

/**
 * space-time position
 *
 */
public class STGraphPositionPathPosition {
	
	private GraphPositionPathPosition s;
	private double t;
	
	int hash;
	
	public STGraphPositionPathPosition(GraphPositionPathPosition s, double t) {
		this.s = s;
		this.t = t;
		
		if (DMath.equals(t, 1.0) && t != 1.0) {
			String.class.getName();
		}
		
		hash = s.hashCode();
	}
	
	public GraphPositionPathPosition getSpace() {
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
		} else if (!(o instanceof STGraphPositionPathPosition)) {
			throw new IllegalArgumentException();
		} else {
			STGraphPositionPathPosition b = (STGraphPositionPathPosition)o;
			return s.equals(b.s) && DMath.equals(t, b.t);
		}
	}
	
}
