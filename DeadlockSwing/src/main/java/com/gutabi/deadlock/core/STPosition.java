package com.gutabi.deadlock.core;

/**
 * space-time position
 *
 */
public class STPosition {
	
	Position s;
	double t;
	
	public STPosition(Position s, double t) {
		this.s = s;
		this.t = t;
		
		if (DMath.equals(t, 1.0) && t != 1.0) {
			String.class.getName();
		}
	}
	
	public Position getSpace() {
		return s;
	}
	
	public double getTime() {
		return t;
	}
	
	public String toString() {
		return s + " @ " + t;
	}
}
