package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.Point;

public abstract class Line implements Shape {
	
	Point p0;
	Point p1;
	
	public Line(Point p0, Point p1) {
		this.p0 = p0;
		this.p1 = p1;
		
		
	}
	
}
