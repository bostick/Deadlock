package com.gutabi.deadlock.core.geom;

import java.awt.geom.CubicCurve2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class CubicCurve extends Shape {
	
	private final CubicCurve2D c;
	
	public CubicCurve(Point p0, Point c0, Point c1, Point p1) {
		c = new CubicCurve2D.Double(p0.x, p0.y, c0.x, c0.y, c1.x, c1.x, p1.x, p1.y);
	}
	
	public java.awt.Shape java2D() {
		return c;
	}
	
	public void draw(RenderingContext ctxt) {	
		ctxt.draw(c);
	}
	
}