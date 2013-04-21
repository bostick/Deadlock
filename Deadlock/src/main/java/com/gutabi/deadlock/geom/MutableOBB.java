package com.gutabi.deadlock.geom;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;

public abstract class MutableOBB implements Shape {
	
	public Point center;
	public double a;
	public double xExtant;
	public double yExtant;
	
	public Point p0;
	public Point p1;
	public Point p2;
	public Point p3;
	
	private Point n01;
	private Point n12;
	
	double[] n01Projection;
	double[] n12Projection;
	
	Line p0p1Line;
	Line p1p2Line;
	Line p2p3Line;
	Line p3p0Line;
	
	public boolean rightAngle;
	
	public MutableAABB aabb = new MutableAABB();
	
	private int hash;
	
	protected MutableOBB() {
		
	}
	
	public void setShape(Point center, double preA, double xExtant, double yExtant) {
		
		double adjA = preA;
		while (DMath.greaterThanEquals(adjA, 2*Math.PI)) {
			adjA -= 2*Math.PI;
		}
		while (DMath.lessThan(adjA, 0.0)) {
			adjA += 2*Math.PI;
		}
		
		this.center = center;
		this.xExtant = xExtant;
		this.yExtant = yExtant;
		
		/*
		 * even though this is an OBB and not an AABB, it is still nice to get exact right angles, and have points in the same order
		 */
		if (Math.abs(adjA - 0.0 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			rightAngle = true;
			a = 0.0 * Math.PI;
			this.p0 = new Point(-xExtant + center.x, -yExtant + center.y);
			this.p1 = new Point(xExtant + center.x, -yExtant + center.y);
			this.p2 = new Point(xExtant + center.x, yExtant + center.y);
			this.p3 = new Point(-xExtant + center.x, yExtant + center.y);
			
			aabb.setShape(-xExtant+center.x, -yExtant+center.y, 2*xExtant, 2*yExtant);
			
		} else if (Math.abs(adjA - 0.5 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			
			rightAngle = true;
			a = 0.5 * Math.PI;
			this.p0 = new Point(-yExtant + center.x, -xExtant + center.y);
			this.p1 = new Point(yExtant + center.x, -xExtant + center.y);
			this.p2 = new Point(yExtant + center.x, xExtant + center.y);
			this.p3 = new Point(-yExtant + center.x, xExtant + center.y);
			
			aabb.setShape(-yExtant+center.x, -xExtant+center.y, 2*yExtant, 2*xExtant);
			
		} else if (Math.abs(adjA - 1.0 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			rightAngle = true;
			a = 1.0 * Math.PI;
			this.p0 = new Point(-xExtant + center.x, -yExtant + center.y);
			this.p1 = new Point(xExtant + center.x, -yExtant + center.y);
			this.p2 = new Point(xExtant + center.x, yExtant + center.y);
			this.p3 = new Point(-xExtant + center.x, yExtant + center.y);
			
			aabb.setShape(-xExtant+center.x, -yExtant+center.y, 2*xExtant, 2*yExtant);
			
		} else if (Math.abs(adjA - 1.5 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			rightAngle = true;
			a = 1.5 * Math.PI;
			this.p0 = new Point(-yExtant + center.x, -xExtant + center.y);
			this.p1 = new Point(yExtant + center.x, -xExtant + center.y);
			this.p2 = new Point(yExtant + center.x, xExtant + center.y);
			this.p3 = new Point(-yExtant + center.x, xExtant + center.y);
			
			aabb.setShape(-yExtant+center.x, -xExtant+center.y, 2*yExtant, 2*xExtant);
			
		} else {
			rightAngle = false;
			a = adjA;
			this.p0 = Geom.rotateAndAdd(-xExtant, -yExtant, a, center);
			this.p1 = Geom.rotateAndAdd(xExtant, -yExtant, a, center);
			this.p2 = Geom.rotateAndAdd(xExtant, yExtant, a, center);
			this.p3 = Geom.rotateAndAdd(-xExtant, yExtant, a, center);
			
			double ulX = Math.min(Math.min(p0.x, p1.x), Math.min(p2.x, p3.x));
			double ulY = Math.min(Math.min(p0.y, p1.y), Math.min(p2.y, p3.y));
			double brX = Math.max(Math.max(p0.x, p1.x), Math.max(p2.x, p3.x));
			double brY = Math.max(Math.max(p0.y, p1.y), Math.max(p2.y, p3.y));
			
			aabb.setShape(ulX, ulY, (brX - ulX), (brY - ulY));
			
		}
		
		n01 = null;
		n12 = null;
		n01Projection = null;
		n12Projection = null;
		p0p1Line = null;
		p1p2Line = null;
		p2p3Line = null;
		p3p0Line = null;
		hash = 0;
		
	}
	
	public void copy(MutableOBB out) {
		out.setShape(center, a, xExtant, yExtant);
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
		return APP.platform.createOBB(center.plus(p), a, xExtant, yExtant);
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
	
//	public AABB getAABB() {
//		return aabb;
//	}
	
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
