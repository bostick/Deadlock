package com.gutabi.bypass.geom;

import java.awt.geom.Ellipse2D;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class CircleImpl extends Circle {
	
	private final Ellipse2D ellipse;
	
	public CircleImpl(Point center, double radius) {
		super(center, radius);
		
		ellipse = new Ellipse2D.Double(center.x - radius, center.y - radius, 2*radius, 2*radius);
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
