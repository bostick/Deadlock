package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.Point;

public abstract class Polygon implements Shape {
	
	Point[] pts;
	
	protected Polygon(Point... pts) {
		this.pts = pts;	
	}
	
}
