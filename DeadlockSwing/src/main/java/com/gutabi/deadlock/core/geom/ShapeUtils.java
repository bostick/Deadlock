package com.gutabi.deadlock.core.geom;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;

public class ShapeUtils {
	
	public static boolean intersect(Quad q0, Quad q1) {
		
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
	
	public static boolean intersect(Quad q0, Circle c1) {
		
		double[] q0Projection = new double[2];
		double[] c1Projection = new double[2];
		
		q0.projectN01(q0Projection);
		c1.project(q0.n01, c1Projection);
		if (!DMath.rangesOverlap(q0Projection, c1Projection)) {
			return false;
		}
		
		q0.projectN12(q0Projection);
		c1.project(q0.n12, c1Projection);
		if (!DMath.rangesOverlap(q0Projection, c1Projection)) {
			return false;
		}
		
		Point closest = q0.p0;
		double closestDist = Point.distance(q0.p0, c1.center);
		
		double dist = Point.distance(q0.p1, c1.center);
		if (dist < closestDist) {
			closest = q0.p1;
			closestDist = dist;
		}
		
		dist = Point.distance(q0.p2, c1.center);
		if (dist < closestDist) {
			closest = q0.p2;
			closestDist = dist;
		}
		
		dist = Point.distance(q0.p3, c1.center);
		if (dist < closestDist) {
			closest = q0.p3;
			closestDist = dist;
		}
		
		Point a = c1.center.minusAndNormalize(closest);
		q0.project(a, q0Projection);
		c1.project(a, c1Projection);
		if (!DMath.rangesOverlap(q0Projection, c1Projection)) {
			return false;
		}
		
		return true;
	}
	
}
