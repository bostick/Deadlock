package com.gutabi.deadlock.core.geom;

import java.awt.geom.GeneralPath;
import java.util.Arrays;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class Quad extends SweepableShape {
	
	public final Point p0;
	public final Point p1;
	public final Point p2;
	public final Point p3;
	
	private Point n01;
	private Point n12;
	
	double[] n01Projection;
	double[] n12Projection;
	
	private GeneralPath poly;
	
	public final AABB aabb;
	
	private int hash;
	
	public Quad(Object parent, Point p0, Point p1, Point p2, Point p3) {
		super(parent);
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		
		double ulX = Math.min(Math.min(p0.x, p1.x), Math.min(p2.x, p3.x));
		double ulY = Math.min(Math.min(p0.y, p1.y), Math.min(p2.y, p3.y));
		double brX = Math.max(Math.max(p0.x, p1.x), Math.max(p2.x, p3.x));
		double brY = Math.max(Math.max(p0.y, p1.y), Math.max(p2.y, p3.y));
		
		aabb = new AABB(ulX, ulY, (brX - ulX), (brY - ulY));
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + p0.hashCode();
			h = 37 * h + p1.hashCode();
			h = 37 * h + p2.hashCode();
			h = 37 * h + p3.hashCode();
			hash = h;
		}
		return hash;
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Quad)) {
			return false;
		} else {
			Quad b = (Quad)o;
			return p0.equals(b.p0) && p1.equals(b.p1) && p2.equals(b.p2) && p3.equals(b.p3);
		}
	}
	
	private void computeProjections() {
		
		if (n01 == null) {
			computeN01();
		}
		if (n12 == null) {
			computeN12();
		}
		
		n01Projection = new double[2];
		project(n01, n01Projection);
		
		n12Projection = new double[2];
		project(n12, n12Projection);
	}
	
	private void computeN01() {
		Point edge = p1.minus(p0);
		n01 = Point.ccw90AndNormalize(edge);
	}
	
	private void computeN12() {
		Point edge = p2.minus(p1);
		n12 = Point.ccw90AndNormalize(edge);
	}
	
	private void computePoly() {
		poly = new GeneralPath();
		poly.moveTo(p0.x, p0.y);
		poly.lineTo(p1.x, p1.y);
		poly.lineTo(p2.x, p2.y);
		poly.lineTo(p3.x, p3.y);
		poly.closePath();
	}
	
	public Point getN01() {
		if (n01 == null) {
			computeN01();
		}
		return n01;
	}
	
	public Point getN12() {
		if (n12 == null) {
			computeN12();
		}
		return n12;
	}
	
	public Quad plus(Point p) {
		return new Quad(parent, p0.plus(p), p1.plus(p), p2.plus(p), p3.plus(p));
	}
	
	public boolean hitTest(Point p) {
		
		if (Geom.halfPlane(p, p0, p1) == -1) {
			return false;
		}
		if (Geom.halfPlane(p, p1, p2) == -1) {
			return false;
		}
		if (Geom.halfPlane(p, p2, p3) == -1) {
			return false;
		}
		if (Geom.halfPlane(p, p3, p0) == -1) {
			return false;
		}
		return true;
		
	}
	
	public AABB getAABB() {
		return aabb;
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
	
	public void sweepStart(Sweeper s) {
		
		Capsule cap = s.getCapsule(0);
		
		if (ShapeUtils.intersectCQ(cap.ac, this)) {
			s.start(new SweepEvent(SweepEventType.enter(parent), this, s, 0, 0.0));
		}
		
	}
	
	public void sweep(Sweeper s, int index) {
		
		Capsule cap = s.getCapsule(index);
		Point c = cap.a;
		Point d = cap.b;
		
		boolean outside;
		if (ShapeUtils.intersectCQ(cap.ac, this)) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = 0;
		
		double cdParam = SweepUtils.sweepCircleLine(p0, p1, c, d, cap.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p1, p2, c, d, cap.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p2, p3, c, d, cap.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p3, p0, c, d, cap.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		Arrays.sort(params);
		if (paramCount == 2 && DMath.equals(params[0], params[1])) {
			/*
			 * hit a seam
			 */
			paramCount = 1;
		}
		
		for (int i = 0; i < paramCount; i++) {
			double param = params[i];
			assert DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0);
			if (DMath.lessThan(param, 1.0) || index == s.pointCount()-1) {
				if (outside) {
					s.event(new SweepEvent(SweepEventType.enter(parent), this, s, index, param));
				} else {
					s.event(new SweepEvent(SweepEventType.exit(parent), this, s, index, param));
				}
				outside = !outside;
			}
		}
		
	}
	
	public boolean containedIn(Quad q) {
		return q.hitTest(p0) && q.hitTest(p1) && q.hitTest(p2) && q.hitTest(p3);
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (poly == null) {
			computePoly();
		}
		
		ctxt.fill(poly);
		
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (poly == null) {
			computePoly();
		}
		
		ctxt.draw(poly);
		
	}

}
