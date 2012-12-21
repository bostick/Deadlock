package com.gutabi.deadlock.core.geom;

import java.awt.geom.QuadCurve2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class QuadCurve implements Shape {
	
	private final QuadCurve2D q;
	
	public QuadCurve(Point p0, Point c, Point p1) {
		q = new QuadCurve2D.Double(p0.x, p0.y, c.x, c.y, p1.x, p1.y);
	}
	
	public java.awt.Shape java2D() {
		return q;
	}
	
	public void draw(RenderingContext ctxt) {	
		ctxt.draw(q);
	}
	
}
