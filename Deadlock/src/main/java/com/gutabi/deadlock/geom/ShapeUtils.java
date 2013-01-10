package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;

public class ShapeUtils {
	
	public static boolean intersect(Shape s0, Shape s1) {
		
		if (s0 instanceof CompoundShape) {
			
			return ((CompoundShape)s0).intersect(s1);
			
		} else if (s1 instanceof CompoundShape) {
			
			return ((CompoundShape)s1).intersect(s0);
			
		} else if (s0 instanceof AABB) {
			if (s1 instanceof AABB) {
				return intersectAA((AABB)s1, (AABB)s0);
			} else if (s1 instanceof Circle) {
				return intersectAC((AABB)s0, (Circle)s1);
			} else if (s1 instanceof Quad) {
				return intersectAQ((AABB)s0, (Quad)s1);
			}
		} else if (s0 instanceof Circle) {
			if (s1 instanceof AABB) {
				return intersectAC((AABB)s1, (Circle)s0);
			} else if (s1 instanceof Circle) {
				return intersectCC((Circle)s0, (Circle)s1);
			} else if (s1 instanceof Quad) {
				return intersectCQ((Circle)s0, (Quad)s1);
			}
		} else if (s0 instanceof Quad) {
			if (s1 instanceof AABB) {
				return intersectAQ((AABB)s1, (Quad)s0);
			} else if (s1 instanceof Circle) {
				return intersectCQ((Circle)s1, (Quad)s0);
			} else if (s1 instanceof Quad) {
				return intersectQQ((Quad)s0, (Quad)s1);
			}
		}
		
		assert false;
		return false;
	}
	
	public static boolean intersectAA(AABB a0, AABB a1) {
		return DMath.lessThanEquals(a0.x, a1.brX) && DMath.lessThanEquals(a1.x, a0.brX) &&
				DMath.lessThanEquals(a0.y, a1.brY) && DMath.lessThanEquals(a1.y, a0.brY);
	}
	
	public static boolean intersectAC(AABB a0, Circle c1) {
		
		if (!intersectAA(a0, c1.aabb)) {
			return false;
		}
		
		double[] c1Projection = new double[2];
		double[] a0Projection = new double[2];
		
		a0.projectN01(a0Projection);
		c1.project(a0.n01, c1Projection);
		if (!DMath.rangesOverlap(a0Projection, c1Projection)) {
			return false;
		}
		
		a0.projectN12(a0Projection);
		c1.project(a0.n12, c1Projection);
		if (!DMath.rangesOverlap(a0Projection, c1Projection)) {
			return false;
		}
		
		Point closest = a0.p0;
		double closestDist = Point.distance(a0.p0, c1.center);
		
		double dist = Point.distance(a0.p1, c1.center);
		if (dist < closestDist) {
			closest = a0.p1;
			closestDist = dist;
		}
		
		dist = Point.distance(a0.p2, c1.center);
		if (dist < closestDist) {
			closest = a0.p2;
			closestDist = dist;
		}
		
		dist = Point.distance(a0.p3, c1.center);
		if (dist < closestDist) {
			closest = a0.p3;
			closestDist = dist;
		}
		
		Point a = c1.center.minusAndNormalize(closest);
		a0.project(a, a0Projection);
		c1.project(a, c1Projection);
		if (!DMath.rangesOverlap(a0Projection, c1Projection)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean intersectAQ(AABB a0, Quad q1) {
		
		if (!intersectAA(a0, q1.aabb)) {
			return false;
		}
		
		double[] a0Projection = new double[2];
		double[] q1Projection = new double[2];
		
		a0.projectN01(a0Projection);
		q1.project(a0.getN01(), q1Projection);
		if (!DMath.rangesOverlap(a0Projection, q1Projection)) {
			return false;
		}
		
		a0.projectN12(a0Projection);
		q1.project(a0.getN12(), q1Projection);
		if (!DMath.rangesOverlap(a0Projection, q1Projection)) {
			return false;
		}
		
		a0.project(q1.getN01(), a0Projection);
		q1.projectN01(q1Projection);
		if (!DMath.rangesOverlap(a0Projection, q1Projection)) {
			return false;
		}
		
		a0.project(q1.getN12(), a0Projection);
		q1.projectN12(q1Projection);
		if (!DMath.rangesOverlap(a0Projection, q1Projection)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean intersectCC(Circle c0, Circle c1) {
		double dist = Point.distance(c0.center, c1.center);
		return DMath.lessThanEquals(dist, c0.radius + c1.radius);
	}
	
	public static boolean intersectCQ(Circle c0, Quad q1) {
		
		if (!intersectAA(c0.aabb, q1.aabb)) {
			return false;
		}
		
		double[] c0Projection = new double[2];
		double[] q1Projection = new double[2];
		
		q1.projectN01(q1Projection);
		c0.project(q1.getN01(), c0Projection);
		if (!DMath.rangesOverlap(q1Projection, c0Projection)) {
			return false;
		}
		
		q1.projectN12(q1Projection);
		c0.project(q1.getN12(), c0Projection);
		if (!DMath.rangesOverlap(q1Projection, c0Projection)) {
			return false;
		}
		
		Point closest = q1.p0;
		double closestDist = Point.distance(q1.p0, c0.center);
		
		double dist = Point.distance(q1.p1, c0.center);
		if (dist < closestDist) {
			closest = q1.p1;
			closestDist = dist;
		}
		
		dist = Point.distance(q1.p2, c0.center);
		if (dist < closestDist) {
			closest = q1.p2;
			closestDist = dist;
		}
		
		dist = Point.distance(q1.p3, c0.center);
		if (dist < closestDist) {
			closest = q1.p3;
			closestDist = dist;
		}
		
		Point a = c0.center.minusAndNormalize(closest);
		q1.project(a, q1Projection);
		c0.project(a, c0Projection);
		if (!DMath.rangesOverlap(q1Projection, c0Projection)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean intersectQQ(Quad q0, Quad q1) {
		
		if (!intersectAA(q0.aabb, q1.aabb)) {
			return false;
		}
		
		double[] q0Projection = new double[2];
		double[] q1Projection = new double[2];
		
		q0.projectN01(q0Projection);
		q1.project(q0.getN01(), q1Projection);
		if (!DMath.rangesOverlap(q0Projection, q1Projection)) {
			return false;
		}
		
		q0.projectN12(q0Projection);
		q1.project(q0.getN12(), q1Projection);
		if (!DMath.rangesOverlap(q0Projection, q1Projection)) {
			return false;
		}
		
		q0.project(q1.getN01(), q0Projection);
		q1.projectN01(q1Projection);
		if (!DMath.rangesOverlap(q0Projection, q1Projection)) {
			return false;
		}
		
		q0.project(q1.getN12(), q0Projection);
		q1.projectN12(q1Projection);
		if (!DMath.rangesOverlap(q0Projection, q1Projection)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * intersectsQQ counts both area intersects and degenerate edge intersects
	 * this only counts area intersects (where there is actual area overlapping)
	 */
	public static boolean intersectAreaQQ(Quad q0, Quad q1) {
		
		if (!intersectAA(q0.aabb, q1.aabb)) {
			return false;
		}
		
		double[] q0Projection = new double[2];
		double[] q1Projection = new double[2];
		
		q0.projectN01(q0Projection);
		q1.project(q0.getN01(), q1Projection);
		if (!DMath.rangesOverlapArea(q0Projection, q1Projection)) {
			return false;
		}
		
		q0.projectN12(q0Projection);
		q1.project(q0.getN12(), q1Projection);
		if (!DMath.rangesOverlapArea(q0Projection, q1Projection)) {
			return false;
		}
		
		q0.project(q1.getN01(), q0Projection);
		q1.projectN01(q1Projection);
		if (!DMath.rangesOverlapArea(q0Projection, q1Projection)) {
			return false;
		}
		
		q0.project(q1.getN12(), q0Projection);
		q1.projectN12(q1Projection);
		if (!DMath.rangesOverlapArea(q0Projection, q1Projection)) {
			return false;
		}
		
		return true;
	}
	
}