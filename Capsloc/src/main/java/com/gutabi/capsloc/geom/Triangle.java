package com.gutabi.capsloc.geom;

import com.gutabi.capsloc.math.Point;

public abstract class Triangle implements Shape {
	
	Point p0;
	Point p1;
	Point p2;
	
	protected Triangle(Point p0, Point p1, Point p2) {
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
	}
	
}
