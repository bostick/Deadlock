package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Dim;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class AABB implements Shape {
	
	public final Point ul;
	public final Dim dim;
	public final double x;
	public final double y;
	public final double width;
	public final double height;
	
	public final Point br;
	public final double brX;
	public final double brY;
	
	public final Point center;
	
	public final Point p0;
	public final Point p1;
	public final Point p2;
	public final Point p3;
	
	public final Point n01 = Point.UP;
	public final Point n12 = Point.RIGHT;
	
	double[] n01Projection;
	double[] n12Projection;
	
	Line p0p1Line;
	Line p1p2Line;
	Line p2p3Line;
	Line p3p0Line;
	
	public AABB(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		assert width >= 0;
		assert height >= 0;
		
		ul = new Point(x, y);
		dim = new Dim(width, height);
		
		brX = x + width;
		brY = y + height;
		br = new Point(brX, brY);
		
		center = new Point(ul.x + width/2, ul.y + height/2);
		
		p0 = ul;
		p1 = new Point(ul.x + width, ul.y);
		p2 = new Point(brX, brY);
		p3 = new Point(ul.x, ul.y + height);
		
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
			p0p1Line = new Line(p0, p1);
		}
		return p0p1Line;
	}
	
	public Line getP1P2Line() {
		if (p1p2Line == null) {
			p1p2Line = new Line(p1, p2);
		}
		return p1p2Line;
	}
	
	public Line getP2P3Line() {
		if (p2p3Line == null) {
			p2p3Line = new Line(p2, p3);
		}
		return p2p3Line;
	}
	
	public Line getP3P0Line() {
		if (p3p0Line == null) {
			p3p0Line = new Line(p3, p0);
		}
		return p3p0Line;
	}
	
	public boolean hitTest(Point p) {
		return DMath.lessThanEquals(x, p.x) && DMath.lessThanEquals(p.x, brX) &&
				DMath.lessThanEquals(y, p.y) && DMath.lessThanEquals(p.y, brY);
	}
	
	public boolean completelyWithin(AABB parent) {
		return DMath.lessThanEquals(parent.x, x) && DMath.lessThanEquals(brX, parent.brX) &&
				DMath.lessThanEquals(parent.y, y) && DMath.lessThanEquals(brY, parent.brY);
	}
	
	/**
	 * returns 0.0 if completely outside parent, returns 1.0 if completely within parent
	 * 
	 * assumes only overlapping on an edge, and not on a corner
	 */
	public double fractionWithin(AABB inner, AABB outer) {
		
		if (DMath.lessThan(x, inner.x)) {
			
			if (DMath.lessThan(brY, outer.y)) {
				return 0.0;
			}
			if (DMath.greaterThan(y, outer.brY)) {
				return 0.0;
			}
			
			double diff = inner.x - outer.x;
			
			double innerLen = inner.x - x;
			
			return DMath.greaterThan(innerLen, (diff+width)) ? 0.0 : 1.0 - innerLen / (diff+width);
		}
		
		if (DMath.greaterThan(brX, inner.brX)) {
			
			if (DMath.lessThan(brY, outer.y)) {
				return 0.0;
			}
			if (DMath.greaterThan(y, outer.brY)) {
				return 0.0;
			}
			
			double diff = outer.brX - inner.brX;
			
			double innerLen = brX - inner.brX;
			
			return DMath.greaterThan(innerLen, (diff+width)) ? 0.0 : 1.0 - innerLen / (diff+width);
		}
		
		if (DMath.lessThan(y, inner.y)) {
			
			double diff = inner.y - outer.y;
			
			double innerLen = inner.y - y;
			
//			System.out.println(1.0 - innerLen / (diff+width));
			
			return DMath.greaterThan(innerLen, (diff+height)) ? 0.0 : 1.0 - innerLen / (diff+height);
		}
		
		if (DMath.greaterThan(brY, inner.brY)) {
			
			double diff = outer.brY - inner.brY;
			
			double innerLen = brY - inner.brY;
			
			return DMath.greaterThan(innerLen, (diff+height)) ? 0.0 : 1.0 - innerLen / (diff+height);
		}
		
		return 1.0;
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
		
		double ulX = a.ul.x;
		double ulY = a.ul.y;
		double brX = a.brX;
		double brY = a.brY;
		
		if (b.ul.x < ulX) {
			ulX = b.ul.x;
			isA = false;
		} else {
			isB = false;
		}
		if (b.ul.y < ulY) {
			ulY = b.ul.y;
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
