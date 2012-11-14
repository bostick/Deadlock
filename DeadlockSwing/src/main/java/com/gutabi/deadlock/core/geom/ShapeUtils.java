package com.gutabi.deadlock.core.geom;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;

public class ShapeUtils {
	
	public static boolean intersect(Quad q0, Quad q1) {
		
		Point p0 = q0.project(q0.n01);
		Point p1 = q1.project(q0.n01);
		if (!DMath.rangesOverlap(p0.x, p0.y, p1.x, p1.y)) {
			return false;
		}
		
		p0 = q0.project(q0.n12);
		p1 = q1.project(q0.n12);
		if (!DMath.rangesOverlap(p0.x, p0.y, p1.x, p1.y)) {
			return false;
		}
		
		p0 = q0.project(q0.n23);
		p1 = q1.project(q0.n23);
		if (!DMath.rangesOverlap(p0.x, p0.y, p1.x, p1.y)) {
			return false;
		}
		
		p0 = q0.project(q0.n30);
		p1 = q1.project(q0.n30);
		if (!DMath.rangesOverlap(p0.x, p0.y, p1.x, p1.y)) {
			return false;
		}
		
		p0 = q0.project(q1.n01);
		p1 = q1.project(q1.n01);
		if (!DMath.rangesOverlap(p0.x, p0.y, p1.x, p1.y)) {
			return false;
		}
		
		p0 = q0.project(q1.n12);
		p1 = q1.project(q1.n12);
		if (!DMath.rangesOverlap(p0.x, p0.y, p1.x, p1.y)) {
			return false;
		}
		
		p0 = q0.project(q1.n23);
		p1 = q1.project(q1.n23);
		if (!DMath.rangesOverlap(p0.x, p0.y, p1.x, p1.y)) {
			return false;
		}
		
		p0 = q0.project(q1.n30);
		p1 = q1.project(q1.n30);
		if (!DMath.rangesOverlap(p0.x, p0.y, p1.x, p1.y)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean intersect(Quad q0, Circle c1) {
		
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
		
		
		Point edge = c1.center.minus(closest);
//		Point a = Point.ccw90(edge).normalize();
		Point a = edge.normalize();
		Point p0 = q0.project(a);
		Point p1 = c1.project(a);
		if (!DMath.rangesOverlap(p0.x, p0.y, p1.x, p1.y)) {
			return false;
		}
		
		
		
		p0 = q0.project(q0.n01);
		p1 = c1.project(q0.n01);
		if (!DMath.rangesOverlap(p0.x, p0.y, p1.x, p1.y)) {
			return false;
		}
		
		p0 = q0.project(q0.n12);
		p1 = c1.project(q0.n12);
		if (!DMath.rangesOverlap(p0.x, p0.y, p1.x, p1.y)) {
			return false;
		}
		
		p0 = q0.project(q0.n23);
		p1 = c1.project(q0.n23);
		if (!DMath.rangesOverlap(p0.x, p0.y, p1.x, p1.y)) {
			return false;
		}
		
		p0 = q0.project(q0.n30);
		p1 = c1.project(q0.n30);
		if (!DMath.rangesOverlap(p0.x, p0.y, p1.x, p1.y)) {
			return false;
		}
		
		return true;
	}
	
}
