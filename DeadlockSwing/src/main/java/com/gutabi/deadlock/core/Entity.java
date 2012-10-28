package com.gutabi.deadlock.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

public abstract class Entity {
	
	public abstract boolean hitTest(Point p, double radius);
	
	public abstract boolean isDeleteable();
	
	public abstract void preStep(double t);
	public abstract boolean postStep();
	
	
	
	protected Path2D path;
	public abstract void computePath();
	
	protected Color color;
	protected Color hiliteColor;
	
	public abstract void paint(Graphics2D g2);
	
	public abstract void paintHilite(Graphics2D g2);
	
	protected Point aabbLoc;
	protected Dim aabbDim;
	
	public final boolean hitTest(Point p) {
		if (DMath.lessThanEquals(aabbLoc.x, p.x) && DMath.lessThanEquals(p.x, aabbLoc.x+aabbDim.width) &&
				DMath.lessThanEquals(aabbLoc.y, p.y) && DMath.lessThanEquals(p.y, aabbLoc.y+aabbDim.height)) {
			return hitTest(p, 0.0);
		} else {
			return false;
		}
	}
	
}
