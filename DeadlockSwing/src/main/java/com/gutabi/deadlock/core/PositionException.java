package com.gutabi.deadlock.core;

@SuppressWarnings("serial")
public class PositionException extends RuntimeException {
	
	private Point p;
	
	public PositionException(Point p) {
		this.p = p;
	}
	
	public Point getPoint() {
		return p;
	}
	
}
