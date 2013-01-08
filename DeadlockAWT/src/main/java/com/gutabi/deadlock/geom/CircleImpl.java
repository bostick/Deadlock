package com.gutabi.deadlock.geom;

import java.awt.geom.Ellipse2D;
import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class CircleImpl extends Circle {
	
	private final Ellipse2D ellipse;
	
	public CircleImpl(Object parent, Point center, double radius) {
		super(parent, center, radius);
		
		ellipse = new Ellipse2D.Double(center.x - radius, center.y - radius, 2*radius, 2*radius);
	}
	
	public List<Point> skeleton() {
		return AWTShapeUtils.skeleton(ellipse);
	}

	public void paint(RenderingContext ctxt) {
		RenderingContextImpl c = (RenderingContextImpl)ctxt;
		c.g2.fill(ellipse);
	}

	public void draw(RenderingContext ctxt) {
		RenderingContextImpl c = (RenderingContextImpl)ctxt;
		c.g2.draw(ellipse);
	}
	
}
