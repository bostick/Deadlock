package com.gutabi.deadlock.core;

public class Dim {
	
	public final double width;
	public final double height;
	
	public Dim(double width, double height) {
		assert DMath.greaterThanEquals(width, 0.0);
		assert DMath.greaterThanEquals(height, 0.0);
		this.width = width;
		this.height = height;
	}
	
}
