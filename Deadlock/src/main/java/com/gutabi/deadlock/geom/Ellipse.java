package com.gutabi.deadlock.geom;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.math.Point;

public abstract class Ellipse implements Shape {
	
	public final Point center;
	public final double xRadius;
	public final double yRadius;
	
	public final AABB aabb;
	
	static Logger logger = Logger.getLogger(Circle.class);
	
	public Ellipse(Point center, double xRadius, double yRadius) {
		this.center = center;
		this.xRadius = xRadius;
		this.yRadius = yRadius;
		
		aabb = APP.platform.createShapeEngine().createAABB(center.x - xRadius, center.y - yRadius, 2*xRadius, 2*yRadius);
	}
	
	public abstract List<Point> skeleton();
	
}
