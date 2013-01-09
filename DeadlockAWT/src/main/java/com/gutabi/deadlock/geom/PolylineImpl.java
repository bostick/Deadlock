package com.gutabi.deadlock.geom;

import java.awt.geom.Path2D;
import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class PolylineImpl extends Polyline {
	
	private final Path2D path;
	
	public PolylineImpl(List<Point> pts) {
		super(pts);
		
		path = new Path2D.Double();
		
		for (int i = 0; i < pts.size(); i++) {
			Point  p = pts.get(i);
			
			if (i == 0) {
				path.moveTo(p.x, p.y);
			} else {
				path.lineTo(p.x, p.y);
			}
			
		}
		path.closePath();
		
	}
	
	public void paint(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.fill(path);
	}

	public void draw(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.draw(path);
	}
	
}
