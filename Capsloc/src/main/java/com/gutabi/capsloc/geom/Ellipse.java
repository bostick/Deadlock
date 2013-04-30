package com.gutabi.capsloc.geom;

import com.gutabi.capsloc.math.Point;

public abstract class Ellipse implements Shape {
	
	public final Point center;
	public final double xRadius;
	public final double yRadius;
	
	public final AABB aabb;
	
	protected Ellipse(Point center, double xRadius, double yRadius) {
		this.center = center;
		this.xRadius = xRadius;
		this.yRadius = yRadius;
		
		aabb = new AABB(center.x - xRadius, center.y - yRadius, 2*xRadius, 2*yRadius);
	}
	
	public Point[] skeleton() {
		
		Point[] pts = new Point[33];
		for (int i = 0; i < 33; i ++) {
			Point p = new Point(xRadius * Math.cos(2 * Math.PI * i / 32), yRadius * Math.sin(2 * Math.PI * i / 32)).plus(center);
			pts[i] = p;
		}
		
		return pts;
	}
	
}
