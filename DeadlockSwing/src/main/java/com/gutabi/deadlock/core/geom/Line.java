package com.gutabi.deadlock.core.geom;

import java.awt.geom.Line2D;

import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class Line {
	
	private final Line2D line;
	
	public Line(double x0, double y0, double x1, double y1) {
		line = new Line2D.Double(x0, y0, x1, y1);
	}
	
	public void draw(RenderingContext ctxt) {
		
		ctxt.draw(line);
		
	}
	
}
