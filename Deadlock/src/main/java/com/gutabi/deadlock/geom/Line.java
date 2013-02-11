package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class Line implements Shape {
	
	public final Point p0;
	public final Point p1;
	
	public Point n01;
	public double[] n01Projection;
	
	public Line(Point p0, Point p1) {
		this.p0 = p0;
		this.p1 = p1;
	}
	
	public Point getN01() {
		if (n01 == null) {
			computeN01();
		}
		return n01;
	}
	
	private void computeN01() {
		Point edge = p1.minus(p0);
		n01 = Point.ccw90AndNormalize(edge);
	}
	
	private void computeProjections() {
		
		if (n01 == null) {
			computeN01();
		}
		
		n01Projection = new double[2];
		project(n01, n01Projection);
		
	}
	
	public void project(Point axis, double[] out) {
		double min = Point.dot(axis, p0);
		double max = min;
		
		double p = Point.dot(axis, p1);
		if (p < min) {
			min = p;
		} else if (p > max) {
			max = p;
		}
		
		out[0] = min;
		out[1] = max;
	}
	
	public void projectN01(double[] out) {
		if (n01Projection == null) {
			computeProjections();
		}
		out[0] = n01Projection[0];
		out[1] = n01Projection[1];
	}

	public void paint(RenderingContext ctxt) {
		ctxt.paintLine(this);
	}

	public void draw(RenderingContext ctxt) {
		ctxt.drawLine(this);
	}
}
