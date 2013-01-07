package com.gutabi.deadlock.math;

import java.awt.geom.AffineTransform;

public class Matrix {
	
	public static Point times(double[][] m, Point v) {
		return new Point(m[0][0] * v.x + m[0][1] * v.y, m[1][0] * v.x + m[1][1] * v.y);
	}
	
	public static AffineTransform affineTransform(double[][] m) {
		AffineTransform a = new AffineTransform();
		a.setTransform(m[0][0], m[1][0], m[0][1], m[1][1], 0.0, 0.0);
		return a;
	}
	
}
