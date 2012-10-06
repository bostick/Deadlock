package com.gutabi.deadlock.core;

public abstract class Position {
	
	Point p;
	
	public Position(Point p) {
		this.p = p;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public abstract Hilitable getHilitable();
	
	/**
	 * A position is a bound if it is not interpolated, i.e., it is on a well-defined point on the graph
	 */
	public abstract boolean isBound();

}
