package com.gutabi.capsloc.geom;

import com.gutabi.capsloc.math.Point;

public abstract class Polyline implements Shape {
	
	public Point[] pts;
	
	protected Polyline(Point... pts) {
		this.pts = pts;
	}
	
}
