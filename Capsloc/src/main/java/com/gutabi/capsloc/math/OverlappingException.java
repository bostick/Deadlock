package com.gutabi.capsloc.math;

@SuppressWarnings("serial")
public class OverlappingException extends Exception {
	
	public Point a;
	public Point b;
	public Point c;
	public Point d;
	
	public OverlappingException(Point a, Point b, Point c, Point d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
}
