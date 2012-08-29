package com.gutabi.deadlock.core;

public class Sink extends Driveable {
	
	private final Point p;
	
	public Sink(Point p) {
		this.p = p;
	}
	
	public Point getPoint() {
		return p;
	}
	
}
