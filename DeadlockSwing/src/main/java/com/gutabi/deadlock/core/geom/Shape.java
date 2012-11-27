package com.gutabi.deadlock.core.geom;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.view.RenderingContext;

public abstract class Shape {
	
	public abstract Shape plus(Point p);
	
	public abstract boolean hitTest(Point p);
	
	public abstract AABB getAABB();
	
	public abstract void draw(RenderingContext ctxt);
	
}
