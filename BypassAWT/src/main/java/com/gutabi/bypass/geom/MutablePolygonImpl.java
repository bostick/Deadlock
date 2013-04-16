package com.gutabi.bypass.geom;

import java.awt.geom.GeneralPath;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.deadlock.geom.MutablePolygon;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MutablePolygonImpl extends MutablePolygon {
	
	private GeneralPath poly = new GeneralPath();
	
	public void setPoints(Point p0, Point p1, Point p2, Point p3) {
		super.setPoints(p0, p1, p2, p3);
		
		poly.reset();
		
		poly.moveTo(p0.x, p0.y);
		poly.lineTo(p1.x, p1.y);
		poly.lineTo(p2.x, p2.y);
		poly.lineTo(p3.x, p3.y);
		poly.closePath();
		
	}
	
	public void setPoints(double p0x, double p0y, double p1x, double p1y, double p2x, double p2y, double p3x, double p3y) {
		super.setPoints(p0x, p0y, p1x, p1y, p2x, p2y, p3x, p3y);
		
		poly.reset();
		
		poly.moveTo(p0x, p0y);
		poly.lineTo(p1x, p1y);
		poly.lineTo(p2x, p2y);
		poly.lineTo(p3x, p3y);
		poly.closePath();
		
	}
	
	public void paint(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		
		ct.g2.fill(poly);
	}
	
	public void draw(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		
		ct.g2.draw(poly);
	}
	
}
