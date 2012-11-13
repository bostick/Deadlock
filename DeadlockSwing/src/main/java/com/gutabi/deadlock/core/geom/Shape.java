package com.gutabi.deadlock.core.geom;

import com.gutabi.deadlock.core.Point;

public abstract class Shape implements Sweepable {
	
	public final Object parent;
	
	public Rect aabb;
	
	public Shape(Object parent) {
		this.parent = parent;
	}
	
	public abstract boolean hitTest(Point p);
	
//	public abstract boolean bestHitTest(Point c, double r);
	
	public abstract boolean intersect(Shape s);
	
//	public abstract boolean containedIn(Shape s);
	
//	public abstract double distanceTo(Point p);
	
}
