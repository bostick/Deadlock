package com.gutabi.deadlock.model;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;

public class SweepUtils {
	
	/**
	 * move circle of radius r from c to d
	 * 
	 * return Point between c and d where the circle touches <a, b>
	 * 
	 * the orientation of <a, b> matters. Going from a to b, the "outside" is considered
	 * to be on the left.
	 * 
	 */
	public static double sweepCircleLine(Point a, Point b, Point c, Point d, double r) {
		
		Point diff = new Point(b.x - a.x, b.y - a.y);
		Point n = Point.ccw90(diff).normalize();
		
		double cDistance = Point.dot(c.minus(a), n);
		double dDistance = Point.dot(d.minus(a), n);
		double cdParam = (r - cDistance) / (dDistance - cDistance);
		if (DMath.greaterThanEquals(cdParam, 0.0) && DMath.lessThanEquals(cdParam, 1.0)) {
			Point p = Point.point(c, d, cdParam);
			double abParam = Point.u(a, p, b);
			if (DMath.greaterThanEquals(abParam, 0.0) && DMath.lessThanEquals(abParam, 1.0)) {
				assert DMath.equals(Point.distance(p, a, b), r);
				return cdParam;
			}
		}
		
		return -1;
	}
	
}
