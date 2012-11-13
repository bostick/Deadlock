package com.gutabi.deadlock.core.geom;

import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;

public abstract class Shape implements Sweepable {
	
	public final Object parent;
	
	public Rect aabb;
	
	public Shape(Object parent) {
		this.parent = parent;
	}
	
	public abstract Shape plus(Point p);
	
	public abstract boolean hitTest(Point p);
	
	public abstract boolean intersect(Shape s);
	
	public abstract void paint(Graphics2D g2);
	
	public abstract void draw(Graphics2D g2);
	
}
