package com.gutabi.deadlock.core.geom;

import java.awt.geom.GeneralPath;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.ui.RenderingContext;

public class Triangle {
	
	private final GeneralPath tri;
	
	public Triangle(Point p0, Point p1, Point p2) {
		
		tri = new GeneralPath();
		tri.moveTo(p0.x, p0.y);
		tri.lineTo(p1.x, p1.y);
		tri.lineTo(p2.x, p2.y);
		tri.closePath();
		
	}
	
	public void paint(RenderingContext ctxt) {
		ctxt.fill(tri);
	}
	
}
