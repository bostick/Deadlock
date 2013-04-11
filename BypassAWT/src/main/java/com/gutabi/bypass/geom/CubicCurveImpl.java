package com.gutabi.bypass.geom;

import java.awt.geom.CubicCurve2D;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.deadlock.geom.CubicCurve;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class CubicCurveImpl extends CubicCurve {
	
	private final CubicCurve2D c;
	
	public CubicCurveImpl(Point p0, Point c0, Point c1, Point p1) {
		super(p0, c0, c1, p1);
		c = new CubicCurve2D.Double(p0.x, p0.y, c0.x, c0.y, c1.x, c1.y, p1.x, p1.y);
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
