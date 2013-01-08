package com.gutabi.deadlock.geom;

import java.awt.geom.Path2D;
import java.util.List;


import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class Polyline {
	
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
