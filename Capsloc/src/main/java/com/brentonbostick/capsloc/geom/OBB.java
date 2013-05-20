package com.brentonbostick.capsloc.geom;

import com.brentonbostick.capsloc.math.Point;

public class OBB {
	
	public final Point center;
	public final double a;
	public final double xExtant;
	public final double yExtant;
	
	public final Point p0;
	public final Point p1;
	public final Point p2;
	public final Point p3;
	
	private Point n01;
	private Point n12;
	
	double[] n01Projection;
	double[] n12Projection;
	
	Line p0p1Line;
	Line p1p2Line;
	Line p2p3Line;
	Line p3p0Line;
	
	public final boolean rightAngle;
	
	public final AABB aabb;
	
	private int hash;
	
	public OBB(Point center, double preA, double xExtant, double yExtant) {
		
		MutableOBB m = new MutableOBB();
		m.setShape(center, preA, xExtant, yExtant);
		
		this.center = m.center;
		this.xExtant = m.xExtant;
		this.yExtant = m.yExtant;
		this.rightAngle = m.rightAngle;
		this.a = m.a;
		this.p0 = m.p0;
		this.p1 = m.p1;
		this.p2 = m.p2;
		this.p3 = m.p3;
		this.aabb = m.aabb.copy();
		
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
		} else if (!(o instanceof OBB || o instanceof MutableOBB)) {
			return false;
		} else if (o instanceof OBB) {
			OBB b = (OBB)o;
			return p0.equals(b.p0) && p1.equals(b.p1) && p2.equals(b.p2) && p3.equals(b.p3);
		} else {
			MutableOBB b = (MutableOBB)o;
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
	
	public Point getN01() {
		if (n01 == null) {
			computeN01();
		}
		if (rightAngle && !n01.isRightAngleNormal()) {
			assert false;
		}
		return n01;
	}
	
	public Point getN12() {
		if (n12 == null) {
			computeN12();
		}
		if (rightAngle && !n12.isRightAngleNormal()) {
			assert false;
		}
		return n12;
	}
	
	public OBB plus(Point p) {
		return new OBB(center.plus(p), a, xExtant, yExtant);
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
	
	public Point closestCornerTo(Point p) {
		
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
	
}
