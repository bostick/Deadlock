package com.gutabi.deadlock.core.geom;

import java.awt.geom.Line2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.ui.RenderingContext;

//@SuppressWarnings("static-access")
public class Line {
	
	private final Line2D line;
	
	public Line(Point p0, Point p1) {
		line = new Line2D.Double(p0.x, p0.y, p1.x, p1.y);
	}
	
	public void draw(RenderingContext ctxt) {
		
		ctxt.draw(line);
		
	}
	
}
