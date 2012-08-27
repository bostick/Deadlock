package com.gutabi.deadlock.core;

@SuppressWarnings("serial")
public class PositionException extends RuntimeException {
	
	private Point p;
	private Segment s;
	
	public PositionException(Point p, Segment s) {
		this.p = p;
		this.s = s;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public Segment getSegment() {
		return s;
	}
	
}
