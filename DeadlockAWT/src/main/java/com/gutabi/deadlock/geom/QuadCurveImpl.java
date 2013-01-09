package com.gutabi.deadlock.geom;

import java.awt.geom.QuadCurve2D;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class QuadCurveImpl extends QuadCurve {
	
	private final QuadCurve2D q;
	
	public QuadCurveImpl(Point p0, Point c0, Point p1) {
		super(p0, c0, p1);
		q = new QuadCurve2D.Double(p0.x, p0.y, c0.x, c0.y, p1.x, p1.y);
	}
	
	public void paint(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.fill(q);
	}

	public void draw(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.draw(q);
	}
	
}
