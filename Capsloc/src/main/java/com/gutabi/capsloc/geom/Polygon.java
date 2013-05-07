package com.gutabi.capsloc.geom;

import com.gutabi.capsloc.math.Point;

public class Polygon {
	
	public Point[] pts = new Point[4];
	
	public Polygon(Point p0, Point p1, Point p2, Point p3) {
		pts[0] = p0;
		pts[1] = p1;
		pts[2] = p2;
		pts[3] = p3;
	}
	
}
