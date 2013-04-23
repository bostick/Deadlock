package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.Point;

public abstract class MutablePolygon implements Shape {
	
	double[][] pts = new double[4][2];
	
	protected MutablePolygon() {
		
	}
	
	public void setPoints(Point p0, Point p1, Point p2, Point p3) {
		pts[0][0] = p0.x;
		pts[0][1] = p0.y;
		pts[1][0] = p1.x;
		pts[1][1] = p1.y;
		pts[2][0] = p2.x;
		pts[2][1] = p2.y;
		pts[3][0] = p3.x;
		pts[3][1] = p3.y;
	}
	
	public void setPoints(double p0x, double p0y, double p1x, double p1y, double p2x, double p2y, double p3x, double p3y) {
		pts[0][0] = p0x;
		pts[0][1] = p0y;
		pts[1][0] = p1x;
		pts[1][1] = p1y;
		pts[2][0] = p2x;
		pts[2][1] = p2y;
		pts[3][0] = p3x;
		pts[3][1] = p3y;
	}
	
}
