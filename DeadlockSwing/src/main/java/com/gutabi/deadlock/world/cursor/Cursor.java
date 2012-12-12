package com.gutabi.deadlock.world.cursor;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public abstract class Cursor {
	
	public Point p;
	
	public abstract void setPoint(Point p);
	
	public final Point getPoint() {
		return p;
	}
	
	public abstract Shape getShape();
	
	public abstract void escKey();
	
	public abstract void draw(RenderingContext ctxt);
	
}
