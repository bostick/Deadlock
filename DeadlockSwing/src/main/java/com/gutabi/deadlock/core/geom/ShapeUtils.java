package com.gutabi.deadlock.core.geom;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.world.cursor.CircleCursorShape;
import com.gutabi.deadlock.world.cursor.FixtureCursorShape;
import com.gutabi.deadlock.world.cursor.MergerCursorShape;
import com.gutabi.deadlock.world.cursor.StraightEdgeCursorShape;

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
			} else if (s1 instanceof CircleCursorShape) {
				return intersect(s1, s0);
			} else if (s1 instanceof FixtureCursorShape) {
				return intersect(s1, s0);
			} else if (s1 instanceof MergerCursorShape) {
				return intersect(s1, s0);
			} else if (s1 instanceof Quad) {
				return intersectCQ((Circle)s0, (Quad)s1);
			} else if (s1 instanceof StraightEdgeCursorShape) {
				return intersect(s1, s0);
			}
		}
//		else if (s0 instanceof CircleCursorShape) {
//			CircleCursorShape c0 = (CircleCursorShape)s0;
//			
//			if (intersect(c0.c1, s1)) {
//				return true;
//			}
//			return false;
//			
//		}
		else if (s0 instanceof FixtureCursorShape) {
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
			} else if (s1 instanceof StraightEdgeCursorShape) {
				return intersect(s1, s0);
			}
		} else if (s0 instanceof StraightEdgeCursorShape) {
			StraightEdgeCursorShape sec0 = (StraightEdgeCursorShape)s0;
			
			if (intersect(sec0.pCircle, s1)) {
				return true;
			}
			return false;
			
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
		double radii = c0.radius + c1.radius;
		return DMath.lessThanEquals(dist, radii);
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
	
	public static boolean intersectCCap(Circle c0, Capsule c1) {
		if (intersectCC(c1.ac, c0)) {
			return true;
		}
		if (intersectCC(c1.bc, c0)) {
			return true;
		}
		if (intersectCQ(c0, c1.middle)) {
			return true;
		}
		return false;
	}
	
	public static boolean intersectCCapSeq(Circle c0, CapsuleSequence cs1) {
		
		if (!intersectAA(c0.aabb, cs1.aabb)) {
			return false;
		}
		
		for (Capsule c : cs1.caps) {
			if (ShapeUtils.intersectCCap(c0, c)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean intersectCapCap(Capsule c0, Capsule c1) {
		if (intersectCCap(c0.ac, c1)) {
			return true;
		}
		if (intersectCCap(c0.bc, c1)) {
			return true;
		}
		if (intersectCapQ(c1, c0.middle)) {
			return true;
		}
		return false;
	}
	
	public static boolean intersectCapQ(Capsule c0, Quad q1) {
		if (intersectCQ(c0.ac, q1)) {
			return true;
		}
		if (intersectCQ(c0.bc, q1)) {
			return true;
		}
		if (intersectQQ(q1, c0.middle)) {
			return true;
		}
		return false;
	}
	
	public static boolean intersectCapCapSeq(Capsule c0, CapsuleSequence cs1) {
		
		if (!intersectAA(c0.aabb, cs1.aabb)) {
			return false;
		}
		
		for (Capsule c : cs1.caps) {
			if (ShapeUtils.intersectCapCap(c0, c)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean intersectCapSeqQ(CapsuleSequence cs0, Quad q1) {
		
		if (!intersectAA(cs0.aabb, q1.aabb)) {
			return false;
		}
		
		for (Capsule c : cs0.caps) {
			if (ShapeUtils.intersectCapQ(c, q1)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean intersectQQ(Quad q0, Quad q1) {
		
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

}
