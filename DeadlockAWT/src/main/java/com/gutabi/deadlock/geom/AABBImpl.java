package com.gutabi.deadlock.geom;

import java.awt.geom.Rectangle2D;

import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class AABBImpl extends AABB {
	
	private Rectangle2D rect;
	
	public AABBImpl(double x, double y, double width, double height) {
		super(x, y, width, height);
	}
	
	private void computeRect() {
		rect = new Rectangle2D.Double(x, y, width, height);
	}
	
	public void paint(RenderingContext ctxt) {
		if (rect == null) {
			computeRect();
		}
		RenderingContextImpl c = (RenderingContextImpl)ctxt;
		c.g2.fill(rect);
	}
	
	public void draw(RenderingContext ctxt) {
		if (rect == null) {
			computeRect();
		}
		RenderingContextImpl c = (RenderingContextImpl)ctxt;
		c.g2.draw(rect);
	}
	
}
