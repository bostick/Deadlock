package com.gutabi.deadlock.geom;

import java.awt.geom.QuadCurve2D;
import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class QuadCurveImpl extends QuadCurve {
	
	private final QuadCurve2D q;
	
	public QuadCurveImpl(Point p0, Point c, Point p1) {
		q = new QuadCurve2D.Double(p0.x, p0.y, c.x, c.y, p1.x, p1.y);
	}
	
	public List<Point> skeleton() {
		return AWTShapeUtils.skeleton(q);
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
