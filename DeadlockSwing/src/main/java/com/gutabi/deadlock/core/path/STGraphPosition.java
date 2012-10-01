package com.gutabi.deadlock.core.path;

import com.gutabi.deadlock.core.GraphPosition;

public class STGraphPosition {
	
	private GraphPosition s;
	private double t;
	
	public STGraphPosition(GraphPosition s, double t) {
		this.s = s;
		this.t = t;
	}
	
	public GraphPosition getSpace() {
		return s;
	}
	
	public double getTime() {
		return t;
	}
	
}
