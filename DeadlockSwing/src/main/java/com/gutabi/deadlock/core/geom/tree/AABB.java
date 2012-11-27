package com.gutabi.deadlock.core.geom.tree;

import java.awt.BasicStroke;
import java.awt.geom.Rectangle2D;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class AABB extends Shape {
	
	public final Point ul;
	public final Dim dim;
	public final double x;
	public final double y;
	public final double width;
	public final double height;
	
	private final double brX;
	private final double brY;
	
	public final Point p0;
	public final Point p1;
	public final Point p2;
	public final Point p3;
	
	public final Point n01;
	public final Point n12;
	
	private final Rectangle2D rect;
	
	double[] n01Projection;
	double[] n12Projection;
	
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
		
		p0 = ul;
		p1 = new Point(ul.x + width, ul.y);
		p2 = new Point(brX, brY);
		p3 = new Point(ul.x, ul.y + height);
		
		n01 = new Point(0, -1);
		n12 = new Point(1, 0);
		
		rect = new Rectangle2D.Double(x, y, width, height);
		
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
	
	
	public boolean hitTest(Point p) {
		return DMath.lessThanEquals(x, p.x) && DMath.lessThanEquals(p.x, brX) &&
				DMath.lessThanEquals(y, p.y) && DMath.lessThanEquals(p.y, brY);
	}
	
	public boolean intersect(AABB a) {
		return DMath.lessThanEquals(a.x, brX) && DMath.lessThanEquals(x, a.brX) &&
				DMath.lessThanEquals(a.y, brY) && DMath.lessThanEquals(y, a.brY);
	}
	
	public boolean completelyWithin(AABB par) {
		return DMath.lessThanEquals(par.x, x) && DMath.lessThanEquals(brX, par.brX) &&
				DMath.lessThanEquals(par.y, y) && DMath.lessThanEquals(brY, par.brY);
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
	
	public static java.awt.Stroke aabbStroke = new BasicStroke(0.001f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.g2.fill(rect);
		
	}
	
	public void draw(RenderingContext ctxt) {
		
		ctxt.g2.draw(rect);
		
	}

}
