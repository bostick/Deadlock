package com.gutabi.deadlock.geom;

import java.awt.geom.GeneralPath;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class PolygonImpl extends Polygon {
	
	private GeneralPath poly;
	
	public PolygonImpl(Point... pts) {
		super(pts);
		
		poly = new GeneralPath();
		poly.moveTo(pts[0].x, pts[0].y);
		for (int i = 1; i < pts.length; i++) {
			Point pp = pts[i];
			poly.lineTo(pp.x, pp.y);
		}
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
