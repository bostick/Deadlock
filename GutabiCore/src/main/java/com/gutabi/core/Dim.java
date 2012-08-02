package com.gutabi.core;

public class Dim {
	
	public final int width;
	public final int height;
	
	public Dim(int width, int height) {
		assert width > 0;
		assert height > 0;
		this.width = width;
		this.height = height;
	}
	
	public Dim times(double factor) {
		return new Dim((int)Math.round(width * factor), (int)Math.round(height * factor));
	}
	
	public Point divide(int b) {
		return new Point(width / b, height / b);
	}
	
}
