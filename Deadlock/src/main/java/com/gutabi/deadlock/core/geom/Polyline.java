package com.gutabi.deadlock.core.geom;

import java.awt.geom.Path2D;
import java.util.List;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.ui.RenderingContext;

public class Polyline {
	
	private final Path2D path;
	
	public Polyline(List<Point> pts) {
		
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
	
	public void draw(RenderingContext ctxt) {
		
		ctxt.draw(path);
		
	}
	
}
