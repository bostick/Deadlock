package com.gutabi.deadlock.core;

public class Source extends Driveable {
	
private final Point p;
	
	public Source(Point p) {
		this.p = p;
	}
	
	public Point getPoint() {
		return p;
	}
	
}
