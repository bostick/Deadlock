package com.gutabi.bypass.geom;

import java.awt.geom.GeneralPath;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.capsloc.geom.MutableOBB;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class MutableOBBImpl extends MutableOBB {
	
	private GeneralPath poly;
	
	public MutableOBBImpl() {
		
	}
	
	public void setShape(Point center, double preA, double xExtant, double yExtant) {
		super.setShape(center, preA, xExtant, yExtant);
		
		poly = null;
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
