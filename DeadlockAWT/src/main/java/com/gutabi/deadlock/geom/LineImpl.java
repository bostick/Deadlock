package com.gutabi.deadlock.geom;

import java.awt.geom.Line2D;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class LineImpl extends Line {
	
	private final Line2D line;
	
	public LineImpl(Point p0, Point p1) {
		super(p0, p1);
		line = new Line2D.Double(p0.x, p0.y, p1.x, p1.y);
	}
	
	public void paint(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.fill(line);
	}

	public void draw(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.draw(line);
	}
	
}
