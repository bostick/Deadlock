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
			} else if (s1 instanceof OBB) {
				return intersectAO((AABB)s0, (OBB)s1);
			}
		} else if (s0 instanceof Capsule) {
			if (s1 instanceof AABB) {
				return intersectACap((AABB)s1, (Capsule)s0);
			} else if (s1 instanceof Capsule) {
				return intersectCapCap((Capsule)s0, (Capsule)s1);
			} else if (s1 instanceof Circle) {
				return intersectCapC((Capsule)s0, (Circle)s1);
			} else if (s1 instanceof OBB) {
				return intersectCapO((Capsule)s0, (OBB)s1);
			}
		} else if (s0 instanceof Circle) {
			if (s1 instanceof AABB) {
				return intersectAC((AABB)s1, (Circle)s0);
			} else if (s1 instanceof Circle) {
				return intersectCC((Circle)s0, (Circle)s1);
			} else if (s1 instanceof Capsule) {
				return intersectCapC((Capsule)s1, (Circle)s0);
			} else if (s1 instanceof OBB) {
				return intersectCO((Circle)s0, (OBB)s1);
			}
		} else if (s0 instanceof OBB) {
			if (s1 instanceof AABB) {
				return intersectAO((AABB)s1, (OBB)s0);
			} else if (s1 instanceof Circle) {
				return intersectCO((Circle)s1, (OBB)s0);
			} else if (s1 instanceof OBB) {
				return intersectOO((OBB)s0, (OBB)s1);
			}
		}
		
		assert false;
		return false;
	}
	
	public static boolean intersectArea(Shape s0, Shape s1) {
		
		if (s0 instanceof OBB) {
			if (s1 instanceof AABB) {
				return intersectAreaAO((AABB)s1, (OBB)s0);
			}
		} else if (s0 instanceof Line) {
			if (s1 instanceof OBB) {
				return intersectAreaLO((Line)s0, (OBB)s1);
			}
		}
		
		assert false;
		return false;
	}
	
	public static boolean touch(Shape s0, Shape s1) {
		
		if (s0 instanceof Capsule) {
			if (s1 instanceof Circle) {
				return touchCapC((Capsule)s0, (Circle)s1);
			}
		} else if (s0 instanceof Circle) {
			if (s1 instanceof Circle) {
				return touchCC((Circle)s0, (Circle)s1);
			}
		}
		
		assert false;
		return false;
	}
	
	public static boolean contains(Shape s0, Shape s1) {
		
		if (s0 instanceof AABB) {
			if (s1 instanceof OBB) {
				return containsAO((AABB)s0, (OBB)s1);
			}
		}
		
		assert false;
		return false;
	}
	
	public static boolean intersectAA(AABB a0, AABB a1) {
		return DMath.lessThanEquals(a0.x, a1.brX) && DMath.lessThanEquals(a1.x, a0.brX) &&
				DMath.lessThanEquals(a0.y, a1.brY) && DMath.lessThanEquals(a1.y, a0.brY);
	}
	
	public static boolean intersectACap(AABB a0, Capsule c1) {
		
		if (intersectAC(a0, c1.ac)) {
			return true;
		}
		if (intersectAC(a0, c1.bc)) {
			return true;		
		}
		if (c1.middle != null && intersectAO(a0, c1.middle)) {
			return true;
		}
		return false;
	}
	
	public static boolean intersectAC(AABB a0, Circle c1) {
		
		if (!intersectAA(a0, c1.getAABB())) {
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
	
	public static boolean intersectAO(AABB a0, OBB q1) {
		
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
	
	public static boolean intersectCapCap(Capsule c0, Capsule c1) {
		if (intersectCapC(c1, c0.ac)) {
			return true;
		}
		if (intersectCapC(c1, c0.bc)) {
			return true;		
		}
		if (c0.middle != null && intersectCapO(c1, c0.middle)) {
			return true;
		}
		return false;
	}
	
	public static boolean intersectCapC(Capsule c0, Circle c1) {
		double dist = Point.distance(c1.center, c0.a, c0.b);
		return DMath.lessThanEquals(dist, c0.r + c1.radius);
	}
	
	public static boolean intersectCapO(Capsule c0, OBB q1) {
		if (intersectCO(c0.ac, q1)) {
			return true;
		}
		if (intersectCO(c0.bc, q1)) {
			return true;		
		}
		if (c0.middle != null && intersectOO(c0.middle, q1)) {
			return true;
		}
		return false;
	}
	
	public static boolean intersectCC(Circle c0, Circle c1) {
		double dist = Point.distance(c0.center, c1.center);
		return DMath.lessThanEquals(dist, c0.radius + c1.radius);
	}
	
	public static boolean intersectCO(Circle c0, OBB q1) {
		
		if (!intersectAA(c0.getAABB(), q1.aabb)) {
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
	
	public static boolean intersectOO(OBB q0, OBB q1) {
		
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
	
	
	
	
	/*
	 * intersect area section
	 */
	
	public static boolean intersectAreaAA(AABB a0, AABB a1) {
		return DMath.lessThan(a0.x, a1.brX) && DMath.lessThan(a1.x, a0.brX) &&
				DMath.lessThan(a0.y, a1.brY) && DMath.lessThan(a1.y, a0.brY);
	}
	
	public static boolean intersectAreaAL(AABB a0, Line l1) {
		
		double[] l1Projection = new double[2];
		double[] a0Projection = new double[2];
		
		l1.projectN01(l1Projection);
		a0.project(l1.getN01(), a0Projection);
		if (!DMath.rangesOverlapArea(l1Projection, a0Projection)) {
			return false;
		}
		
		l1.project(a0.getN01(), l1Projection);
		a0.projectN01(a0Projection);
		if (!DMath.rangesOverlapArea(l1Projection, a0Projection)) {
			return false;
		}
		
		l1.project(a0.getN12(), l1Projection);
		a0.projectN12(a0Projection);
		if (!DMath.rangesOverlapArea(l1Projection, a0Projection)) {
			return false;
		}
		
		return true;
	}

	public static boolean intersectAreaAO(AABB a0, OBB o1) {
		
		if (!intersectAO(a0, o1)) {
			return false;
		}
		
		double[] a0Projection = new double[2];
		double[] o1Projection = new double[2];
		
		a0.projectN01(a0Projection);
		o1.project(a0.getN01(), o1Projection);
		if (!DMath.rangesOverlapArea(a0Projection, o1Projection)) {
			return false;
		}
		
		a0.projectN12(a0Projection);
		o1.project(a0.getN12(), o1Projection);
		if (!DMath.rangesOverlapArea(a0Projection, o1Projection)) {
			return false;
		}
		
		a0.project(o1.getN01(), a0Projection);
		o1.projectN01(o1Projection);
		if (!DMath.rangesOverlapArea(a0Projection, o1Projection)) {
			return false;
		}
		
		a0.project(o1.getN12(), a0Projection);
		o1.projectN12(o1Projection);
		if (!DMath.rangesOverlapArea(a0Projection, o1Projection)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean intersectAreaLO(Line l0, OBB o1) {
		
		double[] l0Projection = new double[2];
		double[] o1Projection = new double[2];
		
		l0.projectN01(l0Projection);
		o1.project(l0.getN01(), o1Projection);
		if (!DMath.rangesOverlapArea(l0Projection, o1Projection)) {
			return false;
		}
		
		l0.project(o1.getN01(), l0Projection);
		o1.projectN01(o1Projection);
		if (!DMath.rangesOverlapArea(l0Projection, o1Projection)) {
			return false;
		}
		
		l0.project(o1.getN12(), l0Projection);
		o1.projectN12(o1Projection);
		if (!DMath.rangesOverlapArea(l0Projection, o1Projection)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * intersectsQQ counts both area intersects and degenerate edge intersects
	 * this only counts area intersects (where there is actual area overlapping)
	 * 
	 * so sharing exactly an edge here returns false
	 */
	public static boolean intersectAreaOO(OBB q0, OBB q1) {
		
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
	
	public static boolean containsAO(AABB a0, OBB q1) {
		if (!intersectAA(a0, q1.aabb)) {
			return false;
		}
		
		double[] a0Projection = new double[2];
		double[] q1Projection = new double[2];
		
		a0.projectN01(a0Projection);
		q1.project(a0.getN01(), q1Projection);
		if (!DMath.rangeContains(a0Projection, q1Projection)) {
			return false;
		}
		
		a0.projectN12(a0Projection);
		q1.project(a0.getN12(), q1Projection);
		if (!DMath.rangeContains(a0Projection, q1Projection)) {
			return false;
		}
		
		a0.project(q1.getN01(), a0Projection);
		q1.projectN01(q1Projection);
		if (!DMath.rangeContains(a0Projection, q1Projection)) {
			return false;
		}
		
		a0.project(q1.getN12(), a0Projection);
		q1.projectN12(q1Projection);
		if (!DMath.rangeContains(a0Projection, q1Projection)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean touchCapC(Capsule c0, Circle c1) {
		double dist = Point.distance(c1.center, c0.a, c0.b);
		return DMath.equals(dist, c0.r + c1.radius);
	}
	
	public static boolean touchCC(Circle c0, Circle c1) {
		double dist = Point.distance(c0.center, c1.center);
		return DMath.equals(dist, c0.radius + c1.radius);
	}
}
