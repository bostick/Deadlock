package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.Point;

public abstract class Polyline implements Shape {
	
	public Point[] pts;
	
	protected Polyline(Point... pts) {
		this.pts = pts;
	}
	
}
