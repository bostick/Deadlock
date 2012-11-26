package com.gutabi.deadlock.core.geom;

import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.tree.AABB;

public abstract class Shape {
	
	public abstract Shape plus(Point p);
	
	public abstract boolean hitTest(Point p);
	
	public abstract void paint(Graphics2D g2);
	
	public abstract AABB getAABB();
	
	public abstract void draw(Graphics2D g2);
	
}
