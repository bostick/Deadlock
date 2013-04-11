package com.gutabi.bypass.geom;

import java.awt.geom.GeneralPath;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.deadlock.geom.Triangle;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class TriangleImpl extends Triangle {
	
	private final GeneralPath tri;
	
	public TriangleImpl(Point p0, Point p1, Point p2) {
		super(p0, p1, p2);
		
		tri = new GeneralPath();
		tri.moveTo(p0.x, p0.y);
		tri.lineTo(p1.x, p1.y);
		tri.lineTo(p2.x, p2.y);
		tri.closePath();
		
	}
	
	public void paint(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.fill(tri);
	}

	public void draw(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.draw(tri);
	}

}
