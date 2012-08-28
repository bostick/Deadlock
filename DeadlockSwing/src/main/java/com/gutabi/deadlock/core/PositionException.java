package com.gutabi.deadlock.core;

@SuppressWarnings("serial")
public class PositionException extends RuntimeException {
	
	private Point p;
	private Point c;
	private Point d;
	
	public PositionException(Point p, Point c, Point d) {
		this.p = p;
		this.c = c;
		this.d = d;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public Point getSegmentStart() {
		return c;
	}
	
	public Point getSegmentEnd() {
		return d;
	}
	
}
