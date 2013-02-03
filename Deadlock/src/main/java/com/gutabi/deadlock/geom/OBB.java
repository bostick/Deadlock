package com.gutabi.deadlock.geom;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.math.Point;

public abstract class OBB extends SweepableShape {
	
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
	
	public final AABB aabb;
	
	private int hash;
	
	protected OBB(Object parent, Point center, double a, double xExtant, double yExtant) {
		super(parent);
		
		this.center = center;
		this.a = a;
		this.xExtant = xExtant;
		this.yExtant = yExtant;
		
		this.p0 = Geom.rotate(a, new Point(-xExtant, -yExtant)).plus(center);
		this.p1 = Geom.rotate(a, new Point(xExtant, -yExtant)).plus(center);
		this.p2 = Geom.rotate(a, new Point(xExtant, yExtant)).plus(center);
		this.p3 = Geom.rotate(a, new Point(-xExtant, yExtant)).plus(center);
		
		double ulX = Math.min(Math.min(p0.x, p1.x), Math.min(p2.x, p3.x));
		double ulY = Math.min(Math.min(p0.y, p1.y), Math.min(p2.y, p3.y));
		double brX = Math.max(Math.max(p0.x, p1.x), Math.max(p2.x, p3.x));
		double brY = Math.max(Math.max(p0.y, p1.y), Math.max(p2.y, p3.y));
		
		aabb = APP.platform.createShapeEngine().createAABB(parent, ulX, ulY, (brX - ulX), (brY - ulY));
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
		} else if (!(o instanceof OBB)) {
			return false;
		} else {
			OBB b = (OBB)o;
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
		return n01;
	}
	
	public Point getN12() {
		if (n12 == null) {
			computeN12();
		}
		return n12;
	}
	
	public OBB plus(Point p) {
		return APP.platform.createShapeEngine().createOBB(parent, center.plus(p), a, xExtant, yExtant);
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
	
	public Line getP0P1Line() {
		if (p0p1Line == null) {
			p0p1Line = APP.platform.createShapeEngine().createLine(p0, p1);
		}
		return p0p1Line;
	}
	
	public Line getP1P2Line() {
		if (p1p2Line == null) {
			p1p2Line = APP.platform.createShapeEngine().createLine(p1, p2);
		}
		return p1p2Line;
	}
	
	public Line getP2P3Line() {
		if (p2p3Line == null) {
			p2p3Line = APP.platform.createShapeEngine().createLine(p2, p3);
		}
		return p2p3Line;
	}
	
	public Line getP3P0Line() {
		if (p3p0Line == null) {
			p3p0Line = APP.platform.createShapeEngine().createLine(p3, p0);
		}
		return p3p0Line;
	}
	
}
