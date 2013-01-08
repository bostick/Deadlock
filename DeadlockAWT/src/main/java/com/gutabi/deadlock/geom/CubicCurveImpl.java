package com.gutabi.deadlock.geom;

import java.awt.geom.CubicCurve2D;
import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class CubicCurveImpl extends CubicCurve {
	
	private final CubicCurve2D c;
	
	public CubicCurveImpl(Point p0, Point c0, Point c1, Point p1) {
		c = new CubicCurve2D.Double(p0.x, p0.y, c0.x, c0.y, c1.x, c1.x, p1.x, p1.y);
	}
	
	public List<Point> skeleton() {
		return AWTShapeUtils.skeleton(c);
	}

	public void paint(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.fill(c);
	}

	public void draw(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.draw(c);
	}
	
}
