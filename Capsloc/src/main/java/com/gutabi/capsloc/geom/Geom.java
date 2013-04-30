package com.gutabi.capsloc.geom;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.math.DMath;
import com.gutabi.capsloc.math.Point;

public class Geom {
	
	public static Point times(double[][] m, Point v) {
		return new Point(m[0][0] * v.x + m[0][1] * v.y, m[1][0] * v.x + m[1][1] * v.y);
	}
	
	public static Point rotate(double a, Point p) {
		return new Point(Math.cos(a) * p.x + -Math.sin(a) * p.y, Math.sin(a) * p.x + Math.cos(a) * p.y);
	}
	
	public static Point rotateAndAdd(double x, double y, double a, Point center) {
		return new Point(Math.cos(a) * x + -Math.sin(a) * y + center.x, Math.sin(a) * x + Math.cos(a) * y + center.y);
	}
	
	/**
	 * 
	 * return -1 if p is to the left of <a, b>
	 * return 1 if p is to the right
	 * return 0 if p is on the line
	 */
	public static int halfPlane(Point p, Point p1, Point p2) {
		double a = -(p2.y - p1.y);
		double b = p2.x - p1.x;
		double c = -(a * p1.x + b * p1.y);
		double d = a * p.x + b * p.y + c;
		if (DMath.equals(d, 0.0)) {
			return 0;
		} else if (d > 0) {
			return 1;
		} else {
			return -1;
		}
	}
	
	public static OBB localToWorld(AABB a, double angle, Point t) {
		return APP.platform.createOBB(t, angle, a.width/2, a.height/2);
	}
	
	public static void localToWorld(AABB a, double angle, Point t, MutableOBB out) {
		out.setShape(t, angle, a.width/2, a.height/2);
	}
	
	public static void localToWorldAndTakeAABB(AABB a, double preAngle, Point p, MutableAABB out) {
		
		double adjA = preAngle;
		while (DMath.greaterThanEquals(adjA, 2*Math.PI)) {
			adjA -= 2*Math.PI;
		}
		while (DMath.lessThan(adjA, 0.0)) {
			adjA += 2*Math.PI;
		}
		
		double xExtant = a.width/2;
		double yExtant = a.height/2;
		
		/*
		 * even though this is an OBB and not an AABB, it is still nice to get exact right angles, and have points in the same order
		 */
		if (Math.abs(adjA - 0.0 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			
			out.setShape(-xExtant+p.x, -yExtant+p.y, 2*xExtant, 2*yExtant);
			
		} else if (Math.abs(adjA - 0.5 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			
			out.setShape(-yExtant+p.x, -xExtant+p.y, 2*yExtant, 2*xExtant);
			
		} else if (Math.abs(adjA - 1.0 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			
			out.setShape(-xExtant+p.x, -yExtant+p.y, 2*xExtant, 2*yExtant);
			
		} else if (Math.abs(adjA - 1.5 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			
			out.setShape(-yExtant+p.x, -xExtant+p.y, 2*yExtant, 2*xExtant);
			
		} else {
			
			Point p0 = Geom.rotateAndAdd(-xExtant, -yExtant, preAngle, p);
			Point p1 = Geom.rotateAndAdd(xExtant, -yExtant, preAngle, p);
			Point p2 = Geom.rotateAndAdd(xExtant, yExtant, preAngle, p);
			Point p3 = Geom.rotateAndAdd(-xExtant, yExtant, preAngle, p);
			
			double ulX = Math.min(Math.min(p0.x, p1.x), Math.min(p2.x, p3.x));
			double ulY = Math.min(Math.min(p0.y, p1.y), Math.min(p2.y, p3.y));
			double brX = Math.max(Math.max(p0.x, p1.x), Math.max(p2.x, p3.x));
			double brY = Math.max(Math.max(p0.y, p1.y), Math.max(p2.y, p3.y));
			
			out.setShape(ulX, ulY, (brX - ulX), (brY - ulY));
		}
		
		
	}
	
	public static void rotationMatrix(double a, double[][] out) {
		out[0][0] = Math.cos(a);
		out[0][1] = -Math.sin(a);
		out[1][0] = Math.sin(a);
		out[1][1] = Math.cos(a);
	}
	
}
