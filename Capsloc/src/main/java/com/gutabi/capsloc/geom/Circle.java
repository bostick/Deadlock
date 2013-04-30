package com.gutabi.capsloc.geom;

import com.gutabi.capsloc.math.DMath;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class Circle implements Shape {
	
	public final Point center;
	public final double radius;
	
	private AABB aabb;
	
	private int hash;
	
	public Circle(Point center, double radius) {
		this.center = center;
		this.radius = radius;
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
	
	public AABB getAABB() {
		if (aabb == null) {
			aabb = new AABB(center.x - radius, center.y - radius, 2*radius, 2*radius);
		}
		return aabb;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public Circle plus(Point p) {
		return new Circle(center.plus(p), radius);
	}
	
	public boolean hitTest(Point p) {
		return DMath.lessThanEquals(Point.distance(p, center), radius);
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
	
	public void draw(RenderingContext ctxt) {
		ctxt.drawCircle(this);
	}
	
	public void paint(RenderingContext ctxt) {
		ctxt.paintCircle(this);
	}
}
