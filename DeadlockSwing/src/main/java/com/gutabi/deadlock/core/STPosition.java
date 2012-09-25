package com.gutabi.deadlock.core;

/**
 * space-time position
 *
 */
public class STPosition {
	
	private PathPosition s;
	private double t;
	
	int hash;
	
	public STPosition(PathPosition s, double t) {
		this.s = s;
		this.t = t;
		
		if (DMath.equals(t, 1.0) && t != 1.0) {
			String.class.getName();
		}
		
		hash = s.hashCode();
	}
	
	public PathPosition getSpace() {
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
		} else if (!(o instanceof STPosition)) {
			throw new IllegalArgumentException();
		} else {
			STPosition b = (STPosition)o;
			return s.equals(b.s) && DMath.equals(t, b.t);
		}
	}
	
}
