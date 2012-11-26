package com.gutabi.deadlock.core.geom;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.model.cursor.FixtureCursorShape;
import com.gutabi.deadlock.model.cursor.MergerCursorShape;

public class ShapeUtils {
	
	public static boolean intersect(Shape s0, Shape s1) {
		
		if (s0 instanceof Capsule) {
			Capsule c0 = (Capsule)s0;
			
			if (intersect(c0.ac, s1)) {
				return true;
			}
			if (intersect(c0.bc, s1)) {
				return true;
			}
			if (intersect(c0.middle, s1)) {
				return true;
			}
			return false;
			
		} else if (s0 instanceof CapsuleSequence) {
			CapsuleSequence cs0 = (CapsuleSequence)s0;
			
			for (Capsule c : cs0.caps) {
				if (ShapeUtils.intersect(c, s1)) {
					return true;
				}
			}
			return false;
			
		} else if (s0 instanceof Circle) {
			if (s1 instanceof AABB) {
				return intersectAC((AABB)s1, (Circle)s0);
			} else if (s1 instanceof Capsule) {
				return intersect(s1, s0);
			} else if (s1 instanceof Circle) {
				return intersectCC((Circle)s0, (Circle)s1);
			} else if (s1 instanceof FixtureCursorShape) {
				return intersect(s1, s0);
			} else if (s1 instanceof MergerCursorShape) {
				return intersect(s1, s0);
			} else if (s1 instanceof Quad) {
				return intersectCQ((Circle)s0, (Quad)s1);
			}
		} else if (s0 instanceof FixtureCursorShape) {
			FixtureCursorShape fc0 = (FixtureCursorShape)s0;
			
			if (intersect(fc0.worldSource, s1)) {
				return true;
			}
			if (intersect(fc0.worldSink, s1)) {
				return true;
			}
			return false;
			
		} else if (s0 instanceof MergerCursorShape) {
			MergerCursorShape mc0 = (MergerCursorShape)s0;
			
			if (intersect(mc0.worldQ, s1)) {
				return true;
			}
			if (intersect(mc0.worldTop, s1)) {
				return true;
			}
			if (intersect(mc0.worldLeft, s1)) {
				return true;
			}
			if (intersect(mc0.worldRight, s1)) {
				return true;
			}
			if (intersect(mc0.worldBottom, s1)) {
				return true;
			}
			return false;
			
		} else if (s0 instanceof Quad) {
			if (s1 instanceof AABB) {
				return intersectAQ((AABB)s1, (Quad)s0);
			} else if (s1 instanceof Capsule) {
				return intersect(s1, s0);
			} else if (s1 instanceof Circle) {
				return intersectCQ((Circle)s1, (Quad)s0);
			} else if (s1 instanceof FixtureCursorShape) {
				return intersect(s1, s0);
			} else if (s1 instanceof MergerCursorShape) {
				return intersect(s1, s0);
			} else if (s1 instanceof Quad) {
				return intersectQQ((Quad)s0, (Quad)s1);
			}
		}
		
		assert false;
		return false;
	}
	
	public static boolean intersectAC(AABB a0, Circle c1) {
		
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
		
		double[] a0Projection = new double[2];
		double[] q1Projection = new double[2];
		
		a0.projectN01(a0Projection);
		q1.project(a0.n01, q1Projection);
		if (!DMath.rangesOverlap(a0Projection, q1Projection)) {
			return false;
		}
		
		a0.projectN12(a0Projection);
		q1.project(a0.n12, q1Projection);
		if (!DMath.rangesOverlap(a0Projection, q1Projection)) {
			return false;
		}
		
		a0.project(q1.n01, a0Projection);
		q1.projectN01(q1Projection);
		if (!DMath.rangesOverlap(a0Projection, q1Projection)) {
			return false;
		}
		
		a0.project(q1.n12, a0Projection);
		q1.projectN12(q1Projection);
		if (!DMath.rangesOverlap(a0Projection, q1Projection)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean intersectCC(Circle c0, Circle c1) {
		return DMath.lessThanEquals(Point.distance(c0.center, c1.center), c0.radius + c1.radius);
	}
	
	public static boolean intersectCQ(Circle c0, Quad q1) {
		
		double[] c0Projection = new double[2];
		double[] q1Projection = new double[2];
		
		q1.projectN01(q1Projection);
		c0.project(q1.n01, c0Projection);
		if (!DMath.rangesOverlap(q1Projection, c0Projection)) {
			return false;
		}
		
		q1.projectN12(q1Projection);
		c0.project(q1.n12, c0Projection);
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
		
		double[] q0Projection = new double[2];
		double[] q1Projection = new double[2];
		
		q0.projectN01(q0Projection);
		q1.project(q0.n01, q1Projection);
		if (!DMath.rangesOverlap(q0Projection, q1Projection)) {
			return false;
		}
		
		q0.projectN12(q0Projection);
		q1.project(q0.n12, q1Projection);
		if (!DMath.rangesOverlap(q0Projection, q1Projection)) {
			return false;
		}
		
		q0.project(q1.n01, q0Projection);
		q1.projectN01(q1Projection);
		if (!DMath.rangesOverlap(q0Projection, q1Projection)) {
			return false;
		}
		
		q0.project(q1.n12, q0Projection);
		q1.projectN12(q1Projection);
		if (!DMath.rangesOverlap(q0Projection, q1Projection)) {
			return false;
		}
		
		return true;
	}

}
