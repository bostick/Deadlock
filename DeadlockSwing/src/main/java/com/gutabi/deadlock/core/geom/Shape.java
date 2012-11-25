package com.gutabi.deadlock.core.geom;

import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.tree.AABB;

public abstract class Shape implements Sweepable {
	
	public final Sweepable parent;
	
	public AABB aabb;
	
	public Shape(Sweepable parent) {
		this.parent = parent;
	}
	
	public abstract Shape plus(Point p);
	
	public abstract boolean hitTest(Point p);
	
//	public abstract boolean intersect(Shape s);
	
	public abstract void paint(Graphics2D g2);
	
	public abstract void draw(Graphics2D g2);
	
}
