package com.gutabi.deadlock.geom;

import java.awt.geom.Ellipse2D;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class EllipseImpl extends Ellipse {
	
	private final Ellipse2D e;
	
	public EllipseImpl(Point center, double xRadius, double yRadius) {
		super(center, xRadius, yRadius);
		
		e = new Ellipse2D.Double(center.x - xRadius, center.y - yRadius, 2*xRadius, 2*yRadius);
	}
	
	public void paint(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.fill(e);
	}

	public void draw(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.draw(e);
	}
	
}
