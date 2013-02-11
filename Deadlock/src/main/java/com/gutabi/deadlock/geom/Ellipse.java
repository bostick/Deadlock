package com.gutabi.deadlock.geom;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.math.Point;

public abstract class Ellipse implements Shape {
	
	public final Point center;
	public final double xRadius;
	public final double yRadius;
	
	public final AABB aabb;
	
//	static Logger logger = Logger.getLogger(Circle.class);
	
	protected Ellipse(Point center, double xRadius, double yRadius) {
		this.center = center;
		this.xRadius = xRadius;
		this.yRadius = yRadius;
		
		aabb = new AABB(center.x - xRadius, center.y - yRadius, 2*xRadius, 2*yRadius);
	}
	
	public List<Point> skeleton() {
		
		List<Point> pts = new ArrayList<Point>();
		for (int i = 0; i <= 32; i ++) {
			Point p = new Point(xRadius * Math.cos(2 * Math.PI * i / 32), yRadius * Math.sin(2 * Math.PI * i / 32)).plus(center);
			pts.add(p);
		}
		
		return pts;
	}
	
}
