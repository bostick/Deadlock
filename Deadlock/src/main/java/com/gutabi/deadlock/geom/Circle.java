package com.gutabi.deadlock.geom;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;

public abstract class Circle implements Shape {
	
	public final Point center;
	public final double radius;
	
	public final AABB aabb;
	
	private int hash;
	
//	static Logger logger = Logger.getLogger(Circle.class);
	
	protected Circle(Point center, double radius) {
		super();
		this.center = center;
		this.radius = radius;
		
		aabb = APP.platform.createShapeEngine().createAABB(center.x - radius, center.y - radius, 2*radius, 2*radius);
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Circle)) {
			return false;
		} else {
			Circle b = (Circle)o;
			return center.equals(b.center) && DMath.equals(radius, b.radius);
		}
	}
	
	public String toString() {
		return "Circle(" + center + ", " + radius + ")";
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + center.hashCode();
			long l = Double.doubleToLongBits(radius);
			int c = (int)(l ^ (l >>> 32));
			h = 37 * h + c;
			hash = h;
		}
		return hash;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public Circle plus(Point p) {
		return APP.platform.createShapeEngine().createCircle(center.plus(p), radius);
	}
	
	public boolean hitTest(Point p) {
		return DMath.lessThanEquals(Point.distance(p, center), radius);
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	public void project(Point axis, double[] out) {
		double cen = Point.dot(axis, center);
		
		out[0] = cen-radius;
		out[1] = cen+radius;
	}
	
	public Point getPoint(double param) {
		assert DMath.equals(param, 0.0);
		return center;
	}
	
}
