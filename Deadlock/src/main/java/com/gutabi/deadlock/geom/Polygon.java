package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.Point;

public abstract class Polygon implements Shape {
	
	Point[] pts = new Point[4];
	
	protected Polygon(Point p0, Point p1, Point p2, Point p3) {
		pts[0] = p0;
		pts[1] = p1;
		pts[2] = p2;
		pts[3] = p3;
	}
	
}
