package com.gutabi.deadlock.core;

import java.awt.geom.AffineTransform;

public class Matrix {
	
	public final double[][] m;
	
	public Matrix(double[][] m, double a, double b, double c, double d) {
		
		this.m = m;
		m[0][0] = a;
		m[0][1] = b;
		m[1][0] = c;
		m[1][1] = d;
		
	}
	
	public Point times(Point v) {
		return new Point(m[0][0] * v.x + m[0][1] * v.y, m[1][0] * v.x + m[1][1] * v.y);
	}
	
	public AffineTransform affineTransform() {
		AffineTransform a = new AffineTransform();
		a.setTransform(m[0][0], m[1][0], m[0][1], m[1][1], 0.0, 0.0);
		return a;
	}
	
}
