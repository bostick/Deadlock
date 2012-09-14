package com.gutabi.deadlock.core;

public class Dim {
	
	public final double width;
	public final double height;
	
	public Dim(double width, double height) {
		assert width > 0;
		assert height > 0;
		this.width = width;
		this.height = height;
	}
	
	public Dim times(double factor) {
		return new Dim(width * factor, height * factor);
	}
	
	public Point divide(double b) {
		return new Point(width / b, height / b);
	}
	
}
