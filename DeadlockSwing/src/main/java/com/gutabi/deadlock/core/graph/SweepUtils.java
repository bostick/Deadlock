package com.gutabi.deadlock.core.graph;

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
		Point norm = Point.ccw90(diff).normalize();
		
		/*
		 * test <a, b>
		 */
		
		double cDistance = Point.dot(c.minus(a), norm);
		double dDistance = Point.dot(d.minus(a), norm);
		double cdParam = (r - cDistance) / (dDistance - cDistance);
		if (DMath.greaterThanEquals(cdParam, 0.0) && DMath.lessThanEquals(cdParam, 1.0)) {
			Point p = Point.point(c, d, cdParam);
			
			double abParam = Point.u(a, p, b);
			if (DMath.greaterThanEquals(abParam, 0.0) && DMath.lessThanEquals(abParam, 1.0)) {
				assert DMath.equals(Point.distance(p, a, b), r);
				
				if (DMath.equals(cdParam, 0.0)) {
					String.class.getName();
				}
				
				return cdParam;
			}	
		}
		
		/*
		 * test a
		 */
		
		double[] params = new double[2];
		int n = sweepCircleCircle(a, c, d, r, 0.0, params);
		
		double adjustedCDParam;
//		if (n == 2) {
//			/*
//			 * figure out which to choose
//			 */
//			assert false;
//		} else if (n == 1) {
//			adjustedCDParam = params[0];
//			Point p = Point.point(c, d, adjustedCDParam);
//			double abParam = Point.u(a, p, b);
//			double pDistance = Point.dot(p.minus(a), norm);
//			if (DMath.lessThan(abParam, 0.0) && DMath.greaterThanEquals(pDistance, 0.0)) {
//				assert DMath.equals(Point.distance(p, a, b), r);
//				return adjustedCDParam;
//			}
//		}
		for (int i = 0; i < n; i++) {
			adjustedCDParam = params[i];
			Point p = Point.point(c, d, adjustedCDParam);
			double abParam = Point.u(a, p, b);
			double pDistance = Point.dot(p.minus(a), norm);
			if (DMath.lessThan(abParam, 0.0) && DMath.greaterThanEquals(pDistance, 0.0)) {
				assert DMath.equals(Point.distance(p, a, b), r);
				
				if (DMath.equals(adjustedCDParam, 0.0)) {
					String.class.getName();
				}
				
				return adjustedCDParam;
			}
		}
		
		
		
		/*
		 * test b
		 */
		
		n = sweepCircleCircle(b, c, d, r, 0.0, params);
		
//		if (n == 2) {
//			/*
//			 * figure out which to choose
//			 */
//			assert false;
//		} else if (n == 1) {
//			adjustedCDParam = params[0];
//			Point p = Point.point(c, d, adjustedCDParam);
//			double abParam = Point.u(a, p, b);
//			double pDistance = Point.dot(p.minus(a), norm);
//			if (DMath.greaterThan(abParam, 1.0) && DMath.greaterThanEquals(pDistance, 0.0)) {
//				assert DMath.equals(Point.distance(p, a, b), r);
//				return adjustedCDParam;
//			}
//		}
		
		for (int i = 0; i < n; i++) {
			adjustedCDParam = params[i];
			Point p = Point.point(c, d, adjustedCDParam);
			double abParam = Point.u(a, p, b);
			double pDistance = Point.dot(p.minus(a), norm);
			if (DMath.greaterThan(abParam, 1.0) && DMath.greaterThanEquals(pDistance, 0.0)) {
				assert DMath.equals(Point.distance(p, a, b), r);
				
				if (DMath.equals(adjustedCDParam, 0.0)) {
					String.class.getName();
				}
				
				return adjustedCDParam;
			}
		}
		
		return -1;
	}
	
	/**
	 * 
	 */
	public static int sweepCircleCap(Point a, Point b, Point c, Point d, double cdRadius, double abRadius, double[] params) {
		
		double aCoeff = ((d.x - c.x)*(d.x - c.x) + (d.y - c.y)*(d.y - c.y));
		double bCoeff = -2 * ((d.x - c.x)*(b.x - c.x) + (d.y - c.y)*(b.y - c.y));
		double cCoeff = ((b.x - c.x)*(b.x - c.x) + (b.y - c.y)*(b.y - c.y) - (cdRadius + abRadius)*(cdRadius + abRadius));
		double[] roots = new double[2];
		double discriminant = DMath.quadraticSolve(aCoeff, bCoeff, cCoeff, roots);
		if (DMath.equals(discriminant, 0.0)) {
			/*
			 * 1 event
			 */
			double cdParam = roots[0];
			if (DMath.greaterThanEquals(cdParam, 0.0) && DMath.lessThanEquals(cdParam, 1.0)) {
				Point p = Point.point(c, d, cdParam);
				double u = Point.u(a, p, b);
				if (DMath.greaterThan(u, 1.0)) {
					assert DMath.equals(Point.distance(p, a, b), abRadius + cdRadius);
					
					if (DMath.equals(cdParam, 0.0)) {
						String.class.getName();
					}
					
					params[0] = cdParam;
					return 1;
				}
			}
			
		} else if (discriminant > 0) {
			/*
			 * 2 events
			 */
			double cdParam0 = roots[0];
			boolean cdParam0Set = false;
			if (DMath.greaterThanEquals(cdParam0, 0.0) && DMath.lessThanEquals(cdParam0, 1.0)) {
				Point p0 = Point.point(c, d, cdParam0);
				double u0 = Point.u(a, p0, b);
				if (DMath.greaterThan(u0, 1.0)) {
					assert DMath.equals(Point.distance(p0, a, b), abRadius + cdRadius);
					
					if (DMath.equals(cdParam0, 0.0)) {
						String.class.getName();
					}
					
					params[0] = cdParam0;
					cdParam0Set = true;
				}
			}
			double cdParam1 = roots[1];
			boolean cdParam1Set = false;
			if (DMath.greaterThanEquals(cdParam1, 0.0) && DMath.lessThanEquals(cdParam1, 1.0)) {
				Point p1 = Point.point(c, d, cdParam1);
				double u1 = Point.u(a, p1, b);
				if (DMath.greaterThan(u1, 1.0)) {
					assert DMath.equals(Point.distance(p1, a, b), abRadius + cdRadius);
					
					if (DMath.equals(cdParam1, 0.0)) {
						String.class.getName();
					}
					
					if (cdParam0Set) {
						params[1] = cdParam1;
					} else {
						params[0] = cdParam1;
					}
					cdParam1Set = true;
				}
			}
			
			if (cdParam0Set) {
				if (cdParam1Set) {
					return 2;
				} else {
					return 1;
				}
			} else {
				if (cdParam1Set) {
					return 1;
				} else {
					return 0;
				}
			}
			
		} else {
			/*
			 * 0 events
			 */
			return 0;
		}
		
		/*
		 * quadraticSolve returned events, but they were too far away to count
		 */
		return 0;
		
	}
	
	public static int sweepCircleCircle(Point p, Point c, Point d, double cdRadius, double pRadius, double[] params) {
		
		double aCoeff = ((d.x - c.x)*(d.x - c.x) + (d.y - c.y)*(d.y - c.y));
		double bCoeff = -2 * ((d.x - c.x)*(p.x - c.x) + (d.y - c.y)*(p.y - c.y));
		double cCoeff = ((p.x - c.x)*(p.x - c.x) + (p.y - c.y)*(p.y - c.y) - (cdRadius + pRadius)*(cdRadius + pRadius));
		double[] roots = new double[2];
		double discriminant = DMath.quadraticSolve(aCoeff, bCoeff, cCoeff, roots);
		if (DMath.equals(discriminant, 0.0)) {
			/*
			 * 1 event
			 */
			double cdParam = roots[0];
			if (DMath.greaterThanEquals(cdParam, 0.0) && DMath.lessThanEquals(cdParam, 1.0)) {
				Point cdPoint = Point.point(c, d, cdParam);
				assert DMath.equals(Point.distance(p, cdPoint), pRadius + cdRadius);
				
				if (DMath.equals(cdParam, 0.0)) {
					String.class.getName();
				}
				
				params[0] = cdParam;
				return 1;
			}
			
		} else if (discriminant > 0) {
			/*
			 * 2 events
			 */
			double cdParam0 = roots[0];
			boolean cdParam0Set = false;
			if (DMath.greaterThanEquals(cdParam0, 0.0) && DMath.lessThanEquals(cdParam0, 1.0)) {
				Point cdPoint0 = Point.point(c, d, cdParam0);
				assert DMath.equals(Point.distance(p, cdPoint0), pRadius + cdRadius);
				
				if (DMath.equals(cdParam0, 0.0)) {
					String.class.getName();
				}
				
				params[0] = cdParam0;
				cdParam0Set = true;
			}
			double cdParam1 = roots[1];
			boolean cdParam1Set = false;
			if (DMath.greaterThanEquals(cdParam1, 0.0) && DMath.lessThanEquals(cdParam1, 1.0)) {
				Point cdPoint1 = Point.point(c, d, cdParam1);
				assert DMath.equals(Point.distance(p, cdPoint1), pRadius + cdRadius);
				
				if (DMath.equals(cdParam1, 0.0)) {
					String.class.getName();
				}
				
				if (cdParam0Set) {
					params[1] = cdParam1;
				} else {
					params[0] = cdParam1;
				}
				cdParam1Set = true;
			}
			
			if (cdParam0Set) {
				if (cdParam1Set) {
					return 2;
				} else {
					return 1;
				}
			} else {
				if (cdParam1Set) {
					return 1;
				} else {
					return 0;
				}
			}
			
		} else {
			/*
			 * 0 events
			 */
			return 0;
		}
		
		/*
		 * quadraticSolve returned events, but they were too far away to count
		 */
		return 0;
		
	}
}
