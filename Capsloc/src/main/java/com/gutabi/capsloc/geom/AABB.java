package com.gutabi.capsloc.geom;

import java.io.Serializable;

import com.gutabi.capsloc.math.DMath;
import com.gutabi.capsloc.math.Dim;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class AABB implements Shape, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public final Dim dim;
	public final double x;
	public final double y;
	public final double width;
	public final double height;
	
	public final double brX;
	public final double brY;
	
	public final double centerX;
	public final double centerY;
	
	public final Point n01 = Point.UP;
	public final Point n12 = Point.RIGHT;
	
	private transient double[] n01Projection;
	private transient double[] n12Projection;
	
	private transient Line p0p1Line;
	private transient Line p1p2Line;
	private transient Line p2p3Line;
	private transient Line p3p0Line;
	
	public AABB(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		assert width >= 0;
		assert height >= 0;
		
		dim = new Dim(width, height);
		
		brX = x + width;
		brY = y + height;
		
		centerX = x + width/2;
		centerY = y + height/2;
		
	}
	
	public String toString() {
		return "[" + x + ", " + y + ", " + width + ", " + height + "]";
	}
	
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof AABB)) {
			return false;
		} else {
			AABB b = (AABB)o;
			return DMath.equals(x, b.x) && DMath.equals(y, b.y) && DMath.equals(width, b.width) && DMath.equals(height, b.height);
		}
	}
	
	public AABB getAABB() {
		return this;
	}
	
	public void project(Point axis, double[] out) {
		
		Point p0 = new Point(x, y);
		Point p1 = new Point(x + width, y);
		Point p2 = new Point(brX, brY);
		Point p3 = new Point(x, y + height);
		
		double min = Point.dot(axis, p0);
		double max = min;
		
		double p = Point.dot(axis, p1);
		if (p < min) {
			min = p;
		} else if (p > max) {
			max = p;
		}
		
		p = Point.dot(axis, p2);
		if (p < min) {
			min = p;
		} else if (p > max) {
			max = p;
		}
		
		p = Point.dot(axis, p3);
		if (p < min) {
			min = p;
		} else if (p > max) {
			max = p;
		}
		
		out[0] = min;
		out[1] = max;
	}
	
	public Point closestCornerTo(Point p) {
		
		Point p0 = new Point(x, y);
		Point p1 = new Point(x + width, y);
		Point p2 = new Point(brX, brY);
		Point p3 = new Point(x, y + height);
		
		Point closest = p0;
		double closestDist = Point.distance(p0, p);
		
		double dist = Point.distance(p1, p);
		if (dist < closestDist) {
			closest = p1;
			closestDist = dist;
		}
		
		dist = Point.distance(p2, p);
		if (dist < closestDist) {
			closest = p2;
			closestDist = dist;
		}
		
		dist = Point.distance(p3, p);
		if (dist < closestDist) {
			closest = p3;
			closestDist = dist;
		}
		
		return closest;
		
	}
	
	private void computeProjections() {
		
		n01Projection = new double[2];
		project(n01, n01Projection);
		
		n12Projection = new double[2];
		project(n12, n12Projection);
	}
	
	public Point getN01() {
		return n01;
	}
	
	public Point getN12() {
		return n12;
	}
	
	public void projectN01(double[] out) {
		if (n01Projection == null) {
			computeProjections();
		}
		out[0] = n01Projection[0];
		out[1] = n01Projection[1];
	}
	
	public void projectN12(double[] out) {
		if (n12Projection == null) {
			computeProjections();
		}
		out[0] = n12Projection[0];
		out[1] = n12Projection[1];
	}
	
	public Line getP0P1Line() {
		if (p0p1Line == null) {
			Point p0 = new Point(x, y);
			Point p1 = new Point(x + width, y);
			p0p1Line = new Line(p0, p1);
		}
		return p0p1Line;
	}
	
	public Line getP1P2Line() {
		if (p1p2Line == null) {
			Point p1 = new Point(x + width, y);
			Point p2 = new Point(brX, brY);
			p1p2Line = new Line(p1, p2);
		}
		return p1p2Line;
	}
	
	public Line getP2P3Line() {
		if (p2p3Line == null) {
			Point p2 = new Point(brX, brY);
			Point p3 = new Point(x, y + height);
			p2p3Line = new Line(p2, p3);
		}
		return p2p3Line;
	}
	
	public Line getP3P0Line() {
		if (p3p0Line == null) {
			Point p0 = new Point(x, y);
			Point p3 = new Point(x, y + height);
			p3p0Line = new Line(p3, p0);
		}
		return p3p0Line;
	}
	
	public boolean hitTest(Point p) {
		if (p == null) {
			throw new IllegalArgumentException();
		}
		return DMath.lessThanEquals(x, p.x) && DMath.lessThanEquals(p.x, brX) &&
				DMath.lessThanEquals(y, p.y) && DMath.lessThanEquals(p.y, brY);
	}
	
	public boolean completelyWithin(AABB parent) {
		return DMath.lessThanEquals(parent.x, x) && DMath.lessThanEquals(brX, parent.brX) &&
				DMath.lessThanEquals(parent.y, y) && DMath.lessThanEquals(brY, parent.brY);
	}
	
	public static final AABB union(AABB a, AABB b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		
		boolean isA = true;
		boolean isB = true;
		
		double ulX = a.x;
		double ulY = a.y;
		double brX = a.brX;
		double brY = a.brY;
		
		if (b.x < ulX) {
			ulX = b.x;
			isA = false;
		} else {
			isB = false;
		}
		if (b.y < ulY) {
			ulY = b.y;
			isA = false;
		} else {
			isB = false;
		}
		if (b.brX > brX) {
			brX = b.brX;
			isA = false;
		} else {
			isB = false;
		}
		if (b.brY > brY) {
			brY = b.brY;
			isA = false;
		} else {
			isB = false;
		}
		
		if (isA) {
			return a;
		}
		if (isB) {
			return b;
		}
		
		return new AABB(ulX, ulY, brX-ulX, brY-ulY);
	}
	
	public AABB plus(Point p) {
		return new AABB(x + p.x, y + p.y, width, height);
	}
	
	public AABB plus(double xx, double yy) {
		return new AABB(x + xx, y + yy, width, height);
	}
	
	public AABB minus(Point p) {
		return new AABB(x - p.x, y - p.y, width, height);
	}
	
	public void draw(RenderingContext ctxt) {
		ctxt.drawAABB(this);
	}
	
	public void paint(RenderingContext ctxt) {
		ctxt.paintAABB(this);
	}
}
