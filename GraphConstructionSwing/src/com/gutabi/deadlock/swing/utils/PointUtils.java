package com.gutabi.deadlock.swing.utils;

public final class PointUtils {
	
	private PointUtils() {
		
	}
	
	public static boolean equals(Point a, Point b) {
		return DoubleUtils.doubleEqual(a.x, b.x) && DoubleUtils.doubleEqual(a.y, b.y);
	}
	
	/**
	 * Does b intersect &lt;c, d> ?
	 */
	public static boolean intersection(Point b, Point c, Point d) {
		double xbc = (b.x - c.x);
		double xdc = (d.x - c.x);
		double ybc = (b.y - c.y);
		double ydc = (d.y - c.y);
		double denom = (xdc * xdc + ydc * ydc);
		// u is where b is perpendicular to <c, d>
		double u = ((xbc * xdc) + (ybc * ydc)) / denom;
		if (PointUtils.equals(b, d)) {
			return false;
		} else if (0 <= u && u <= 1) {
			double x = c.x + u * xdc;
			double y = c.y + u * ydc;
			return DoubleUtils.doubleEqual(b.x, x) && DoubleUtils.doubleEqual(b.y, y);
		} else {
			return false;
		}
	}
	
	/**
	 * assuming it is, return param for point b on line defined by &lt;c, d>
	 */
	public static double param(Point b, Point c, Point d) {
		double xbc = (b.x - c.x);
		double xdc = (d.x - c.x);
		double ybc = (b.y - c.y);
		double ydc = (d.y - c.y);
		
		if (DoubleUtils.doubleEqual(xdc, 0.0)) {
			assert DoubleUtils.doubleEqual(xbc, 0.0);
			double uy = ybc / ydc;
			assert uy < 1.0;
			return uy;
		} else if (DoubleUtils.doubleEqual(ydc, 0.0)) {
			assert DoubleUtils.doubleEqual(ybc, 0.0);
			double ux = xbc / xdc;
			assert ux < 1.0;
			return ux;
		} else {
			double ux = xbc / xdc;
			double uy = ybc / ydc;
			assert DoubleUtils.doubleEqual(ux, uy) : Math.abs(ux - uy);
//			assert 0 <= ux;
//			assert ux <= 1;
			assert ux < 1.0;
			return ux;
		}
		
	}
	
	/**
	 * return Point defined by param on line &lt;a, b>
	 */
	public static Point point(Point a, Point b, double param) {
		assert param >= 0.0;
		assert param < 1.0;
		return new Point(a.x + param * (b.x - a.x), a.y + param * (b.y - a.y));
	}
	
	/**
	 * intersection of lines &lt;a, b> and &lt;c, d>
	 */
	public static Point intersection(Point a, Point b, Point c, Point d) throws OverlappingException {
		if (PointUtils.equals(a, b)) {
			throw new IllegalArgumentException("a and b are equal");
		}
		if (PointUtils.equals(c, d)) {
			throw new IllegalArgumentException("c and d are equal");
		}
		double ydc = (d.y - c.y);
		double xba = (b.x - a.x);
		double xdc = (d.x - c.x);
		double yba = (b.y - a.y);
		double yac = (a.y - c.y);
		double xac = (a.x - c.x);
		double denom = (xba * ydc) - (xdc * yba);
		double uabn = ((xdc * yac) - (xac * ydc));
		double ucdn = ((xba * yac) - (xac * yba));
		if (denom == 0.0) {
			if (uabn == 0.0 && ucdn == 0.0) {
				//overlapping, or identical
				
				double cu;
				if (xba != 0.0) {
					cu = (c.x - a.x)/xba;
					if (yba != 0.0) {
						assert cu == (c.y - a.y)/yba;
					}
				} else {
					cu = (c.y - a.y)/yba;
				}
				
				double du;
				if (xba != 0.0) {
					du = (d.x - a.x)/xba;
					if (yba != 0.0) {
						assert du == (d.y - a.y)/yba;
					}
				} else {
					du = (d.y - a.y)/yba;
				}
				
				if (du < cu) {
					double tmp = cu;
					cu = du;
					du = tmp;
				}
				
				if (du < 0.0) {
					//colinear
					return null;
				} else if (du == 0.0) {
					//single point
					return a;
				} else if (du > 0.0 && du < 1.0) {
					if (cu < 0.0) {
						throw new OverlappingException();
					} else if (cu == 0.0) {
						throw new OverlappingException();
					} else {
						throw new OverlappingException();
					}
				} else if (du == 1.0) {
					if (cu < 0.0) {
						throw new OverlappingException();
					} else if (cu == 0.0) {
						//identical
						throw new OverlappingException();
					} else {
						throw new OverlappingException();
					}
				} else {
					if (cu < 0.0) {
						throw new OverlappingException();
					} else if (cu == 0.0) {
						throw new OverlappingException();
					} else if (cu > 0.0 && cu < 1.0) {
						throw new OverlappingException();
					} else if (cu == 1.0) {
						// single point
						return b;
					} else {
						//colinear
						return null;
					}
				}
				
			} else {
				// parallel
				return null;
			}
		} else {
			// skew
			double uab = uabn / denom;
			double ucd = ucdn / denom;
			if ((0 <= uab && uab <= 1) && (0 <= ucd && ucd <= 1)) {
				// intersecting
				double x = a.x + uab * xba;
				double y = a.y + uab * yba;
				return new Point(x, y);
			} else {
				// not intersecting
				return null;
			}
		}
	}
	
}
