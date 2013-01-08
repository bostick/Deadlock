package com.gutabi.deadlock.geom;

import java.awt.geom.Ellipse2D;


import org.apache.log4j.Logger;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class Ellipse implements Shape {
	
	public final Point center;
	public final double xRadius;
	public final double yRadius;
	
	public final AABB aabb;
	
	static Logger logger = Logger.getLogger(Circle.class);
	
	public Ellipse(Point center, double xRadius, double yRadius) {
		this.center = center;
		this.xRadius = xRadius;
		this.yRadius = yRadius;
		
		ellipse = new Ellipse2D.Double(center.x - xRadius, center.y - yRadius, 2*xRadius, 2*yRadius);
		
		aabb = new AABB(center.x - xRadius, center.y - yRadius, 2*xRadius, 2*yRadius);
	}
	
	public java.awt.Shape java2D() {
		return ellipse;
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.fill(ellipse);
		
	}
	
	public void draw(RenderingContext ctxt) {
		
		ctxt.draw(ellipse);
		
	}
}
