package com.gutabi.deadlock.geom;

import java.awt.geom.GeneralPath;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class QuadImpl extends Quad {
	
	private GeneralPath poly;
	
	public QuadImpl(Object parent, Point p0, Point p1, Point p2, Point p3) {
		super(parent, p0, p1, p2, p3);
	}
	
	private void computePoly() {
		poly = new GeneralPath();
		poly.moveTo(p0.x, p0.y);
		poly.lineTo(p1.x, p1.y);
		poly.lineTo(p2.x, p2.y);
		poly.lineTo(p3.x, p3.y);
		poly.closePath();
	}
	
	public void paint(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		
		if (poly == null) {
			computePoly();
		}
		
		ct.g2.fill(poly);
		
	}
	
	public void draw(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		
		if (poly == null) {
			computePoly();
		}
		
		ct.g2.draw(poly);
		
	}
	
}