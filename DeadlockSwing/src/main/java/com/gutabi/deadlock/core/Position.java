package com.gutabi.deadlock.core;

public abstract class Position {
	
	Point p;
	
	public Position(Point p) {
		this.p = p;
	}
	
	public Point getPoint() {
		return p;
	}

}
