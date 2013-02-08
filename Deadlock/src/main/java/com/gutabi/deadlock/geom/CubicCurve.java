package com.gutabi.deadlock.geom;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.math.Point;

public abstract class CubicCurve implements Shape {
	
	Point p0;
	Point c0;
	Point c1;
	Point p1;
	
	protected CubicCurve(Point p0, Point c0, Point c1, Point p1) {
		
		this.p0 = p0;
		this.c0 = c0;
		this.c1 = c1;
		this.p1 = p1;
		
	}
	
	/*
	 * http://www.antigrain.com/research/adaptive_bezier/index.html
	 */
	public Point point(double mu) {
	   double mum1;
	   double mum13;
	   double mu3;

	   mum1 = 1 - mu;
	   mum13 = mum1 * mum1 * mum1;
	   mu3 = mu * mu * mu;
	   
	   double x = mum13*p0.x + 3*mu*mum1*mum1*c0.x + 3*mu*mu*mum1*c1.x + mu3*p1.x;
	   double y = mum13*p0.y + 3*mu*mum1*mum1*c0.y + 3*mu*mu*mum1*c1.y + mu3*p1.y;

	   return new Point(x, y);
	}
	
	/*
	 * http://www.antigrain.com/research/adaptive_bezier/index.html
	 */
	public List<Point> skeleton() {
		
		double dx1 = c0.x - p0.x;
		double dy1 = c0.y - p0.y;
		double dx2 = c1.x - c0.x;
		double dy2 = c1.y - c0.y;
		double dx3 = p1.x - c1.x;
		double dy3 = p1.y - c1.y;
		
		double len = Math.sqrt(dx1 * dx1 + dy1 * dy1) +
				Math.sqrt(dx2 * dx2 + dy2 * dy2) +
				Math.sqrt(dx3 * dx3 + dy3 * dy3);
		
		int num_steps = (int)(2 * len * 0.25);
		
		List<Point> pts = new ArrayList<Point>();
		for (int i = 0; i <= num_steps; i++) {
			Point p = point(((double)i) / num_steps);
			pts.add(p);
		}
		return pts;
	}

}
