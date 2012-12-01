package com.gutabi.deadlock.model;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public abstract class Cursor {
	
	protected Point p;
	
	public abstract void setPoint(Point p);
	
	public final Point getPoint() {
		return p;
	}
	
	public abstract Shape getShape();
	
	public abstract void draw(RenderingContext ctxt);
	
}
