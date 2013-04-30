package com.gutabi.capsloc.geom;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.capsloc.math.Point;

public abstract class QuadCurve implements Shape {
	
	Point p0;
	Point c0;
	Point p1;
	
	protected QuadCurve(Point p0, Point c0, Point p1) {
		this.p0 = p0;
		this.c0 = c0;
		this.p1 = p1;
	}
	
	public Point point(double mu) {
	   double mum1;

	   mum1 = 1 - mu;
	   
	   double x = mum1*mum1*p0.x + 2*mu*mum1*c0.x + mu*mu*p1.x;
	   double y = mum1*mum1*p0.y + 2*mu*mum1*c0.y + mu*mu*p1.y;

	   return new Point(x, y);
	}
	
	/*
	 * http://www.antigrain.com/research/adaptive_bezier/index.html
	 */
	public List<Point> skeleton() {
		
		double dx1 = c0.x - p0.x;
		double dy1 = c0.y - p0.y;
		double dx2 = p1.x - c0.x;
		double dy2 = p1.y - c0.y;
		
		double len = Math.sqrt(dx1 * dx1 + dy1 * dy1) +
				Math.sqrt(dx2 * dx2 + dy2 * dy2);
		
		int num_steps = (int)(len * 0.25);
		
		List<Point> pts = new ArrayList<Point>();
		for (int i = 0; i <= num_steps; i++) {
			Point p = point(((double)i) / num_steps);
			pts.add(p);
		}
		return pts;
	}

}
