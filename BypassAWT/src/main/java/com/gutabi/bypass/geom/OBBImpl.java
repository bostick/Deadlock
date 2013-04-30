package com.gutabi.bypass.geom;

import java.awt.geom.GeneralPath;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.capsloc.geom.OBB;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class OBBImpl extends OBB {
	
	private GeneralPath poly;
	
	public OBBImpl(Point center, double angle, double xExtant, double yExtant) {
		super(center, angle, xExtant, yExtant);
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
