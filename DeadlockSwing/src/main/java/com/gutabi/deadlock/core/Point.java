package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.core.DMath.doubleEquals;

public class Point {
	
	private final double x;
	private final double y;
	
	private final int hash;
	private String s;
	
	public Point(double xx, double yy) {
		
		this.x = (double)Math.round(xx * 1.0E11) / 1.0E11;
		this.y = (double)Math.round(yy * 1.0E11) / 1.0E11;
		
		int h = 17;
		long l = Double.doubleToLongBits(x);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		l = Double.doubleToLongBits(y);
		c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
	}
	
	public static boolean equals(Point a, Point b) {
		return doubleEquals(a.x, b.x) && doubleEquals(a.y, b.y);
	}
	
	@Override
	public boolean equals(Object p) {
		throw new AssertionError();
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		if (s == null) {
			s = "<" + x + ", " + y + ">";
		}
		return s;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	
	/**
	 * does <a, b> intersect <c, d>?
	 * where?
	 * 
	 * @return intersection or null
	 * @throws IllegalArgumentException
	 * @throws OverlappingException
	 */
	public static Point intersection(Point a, Point b, Point c, Point d) throws OverlappingException {
		if (Point.equals(a, b)) {
			throw new IllegalArgumentException("a and b are equal");
		}
		if (Point.equals(c, d)) {
			throw new IllegalArgumentException("c and d are equal");
		}
		double ydc = d.y - c.y;
		double xba = b.x - a.x;
		double xdc = d.x - c.x;
		double yba = b.y - a.y;
		double yac = a.y - c.y;
		double xac = a.x - c.x;
		double denom = xba * ydc - xdc * yba;
		double uabn = xdc * yac - xac * ydc;
		double ucdn = xba * yac - xac * yba;
		if (doubleEquals(denom, 0.0)) {
			if (doubleEquals(uabn, 0.0) && doubleEquals(ucdn, 0.0)) {
				//colinear but not overlapping, single point, overlapping, or identical
				
				double cu;
				if (!doubleEquals(xba, 0.0)) {
					cu = (c.x - a.x) / xba;
					if (!doubleEquals(yba, 0.0)) {
						assert doubleEquals(cu, (c.y - a.y) / yba);
					}
				} else {
					cu = (c.y - a.y) / yba;
				}
				
				double du;
				if (!doubleEquals(xba, 0.0)) {
					du = (d.x - a.x) / xba;
					if (!doubleEquals(yba, 0.0)) {
						assert doubleEquals(du, (d.y - a.y) / yba);
					}
				} else {
					du = (d.y - a.y) / yba;
				}
				
				if (du < cu) {
					double tmp = cu;
					cu = du;
					du = tmp;
				}
				
				if (du < 0.0) {
					//colinear but not intersecting
					return null;
				} else if (doubleEquals(du, 0.0)) {
					//single point
					return a;
				} else if (du > 0.0 && du < 1.0) {
					if (cu < 0.0) {
						throw new OverlappingException();
					} else if (doubleEquals(cu, 0.0)) {
						throw new OverlappingException();
					} else {
						throw new OverlappingException();
					}
				} else if (doubleEquals(du, 1.0)) {
					if (cu < 0.0) {
						throw new OverlappingException();
					} else if (doubleEquals(cu, 0.0)) {
						//identical
						throw new OverlappingException();
					} else {
						throw new OverlappingException();
					}
				} else {
					if (cu < 0.0) {
						throw new OverlappingException();
					} else if (doubleEquals(cu, 0.0)) {
						throw new OverlappingException();
					} else if (cu > 0.0 && cu < 1.0) {
						throw new OverlappingException();
					} else if (doubleEquals(cu, 1.0)) {
						// single point
						return b;
					} else {
						//colinear but not intersecting
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
			if (0.0 <= uab && uab <= 1.0) {
				if (doubleEquals(ucd,  0.0)) {
					// intersecting
					return c;
				} else if (doubleEquals(ucd,  1.0)) {
					// intersecting
					return d;
				} else if (0.0 < ucd && ucd < 1.0) {
					// intersecting
					double x = a.x + (uab * (xba));
					double y = a.y + (uab * (yba));
					return new Point(x, y);
				} else {
					// not intersecting
					return null;
				}
			} else {
				// not intersecting
				return null;
			}
		}
	}
	
	/**
	 * return Point defined by param on line &lt;a, b>
	 */
	public static Point point(Point a, Point b, double param) {
		assert param >= 0.0;
		assert param <= 1.0;
		
		if (param == 0.0) {
			return a;
		}
		if (param == 1.0) {
			return b;
		}
		
		return new Point(a.x + param * (b.x - a.x), a.y + param * (b.y - a.y));
	}
	
	/**
	 * return param that is dist away from a along the segment <a, b>
	 */
	public static double travelForward(Point start, Point end, double param, double dist) throws TravelException {
		
		if (dist == 0.0) {
			return param;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		Point c = point(start, end, param);
		
		if (dist > dist(c, end)) {
			throw new TravelException();
		}
		
		double rad = Math.atan2(end.y - c.y, end.x - c.x);
		double x = Math.cos(rad) * dist + c.x;
		double y = Math.sin(rad) * dist + c.y;
		
		Point m = new Point(x, y);
		
		try {
			assert colinear(start, m, end);
		} catch (ColinearException e) {
			assert false;
		}
		assert doubleEquals(dist(c, m), dist);
		
		return param(m, start, end);
		
	}
	
	/**
	 * return param that is dist away from a along the segment <a, b>
	 */
	public static double travelBackward(Point start, Point end, double param, double dist) throws TravelException {
		
		if (dist == 0.0) {
			return param;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		Point c = point(start, end, param);
		
		if (dist > dist(c, start)) {
			throw new TravelException();
		}
		
		double rad = Math.atan2(start.y - c.y, start.x - c.x);
		double x = Math.cos(rad) * dist + c.x;
		double y = Math.sin(rad) * dist + c.y;
		
		Point m = new Point(x, y);
		
		try {
			assert colinear(start, m, end);
		} catch (ColinearException e) {
			assert false;
		}
		assert doubleEquals(dist(c, m), dist);
		
		return param(m, start, end);
		
	}
	
	/**
	 * Does b intersect &lt;c, d> ?
	 */
	public static boolean intersect(Point b, Point c, Point d) {
		if (Point.equals(b, c)) {
			return true;
		}
		if (Point.equals(b, d)) {
			return false;
		}	
		if (Point.equals(c, d)) {
			throw new IllegalArgumentException("c equals d");
		}
		double xbc = b.x - c.x;
		double xdc = d.x - c.x;
		double ybc = b.y - c.y;
		double ydc = d.y - c.y;
		double denom = xdc * xdc + ydc * ydc;
		assert !doubleEquals(denom, 0.0);
		double u = (xbc * xdc + ybc * ydc) / denom;
		if (u >= 0.0 && u < 1.0) {	
			return doubleEquals(xbc, u * xdc) && doubleEquals(ybc, u * ydc);
		} else {
			return false;
		}
	}
	
	
	
	/**
	 * is b inside the segment <c, d>?
	 * ok if b equals c or d
	 * throws exception if b is on the line, but outside of the segment <c, d>
	 */
	public static boolean colinear(Point c, Point b, Point d) throws ColinearException {
		if (Point.equals(b, c)) {
			return true;
		}
		if (Point.equals(b, d)) {
			return true;
		}
		if (Point.equals(c, d)) {
			throw new IllegalArgumentException("c equals d");
		}
		double xbc = b.x - c.x;
		double xdc = d.x - c.x;
		double ybc = b.y - c.y;
		double ydc = d.y - c.y;
		double denom = xdc * xdc + ydc * ydc;
		assert !doubleEquals(denom, 0.0);
		// u is where b is perpendicular to <c, d>
		double u = (xbc * xdc + ybc * ydc) / denom;
		if (u >= 0.0 && u <= 1.0) {
			return doubleEquals(xbc, u * xdc) && doubleEquals(ybc, u * ydc);
		} else {
			 if (doubleEquals(xbc, u * xdc) && doubleEquals(ybc, u * ydc)) {
				 throw new ColinearException();
			 } else {
				 return false;
			 }
		}
	}
	
	
	/**
	 * assuming it is, return param for point b on line defined by &lt;c, d>
	 */
	public static double param(Point b, Point c, Point d) {
		
		if (Point.equals(b, c)) {
			return 0.0;
		} else if (Point.equals(b, d)) {
			return 1.0;
		}
		
		double xbc = b.x - c.x;
		double xdc = d.x - c.x;
		double ybc = b.y - c.y;
		double ydc = d.y - c.y;
		if (doubleEquals(xdc, 0.0)) {
			assert doubleEquals(xbc, 0);
			double uy = ybc / ydc;
			assert uy < 1.0;
			return uy;
		} else if (doubleEquals(ydc, 0.0)) {
			assert doubleEquals(ybc, 0);
			double ux = xbc / xdc;
			assert ux < 1.0;
			return ux;
		} else {
			double ux = xbc / xdc;
			double uy = ybc / ydc;
			assert doubleEquals(ux, uy) : "being treated as uneqal: " + ux + " " + uy;
			assert ux >= 0.0;
			assert ux < 1.0;
			return ux;
		}
		
	}
	
	public static double dist(Point a, Point b) {
		return Math.hypot(a.x - b.x, a.y - b.y);
	}
	
	public static Point add(Point a, Point b) {
		return new Point(a.x + b.x, a.y + b.y);
	}
	
	public static Point minus(Point a, Point b) {
		return new Point(a.x - b.x, a.y - b.y);
	}
	
	public Point add(Point b) {
		return add(this, b);
	}
	
	public Point minus(Point b) {
		return minus(this, b);
	}	
	
}
