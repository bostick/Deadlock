package com.gutabi.core;

public class Point {
	
	public final int x;
	public final int y;
	
	public final int hash;
	public final String s;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
		
		int h = 17;
		h = 37 * h + x;
		h = 37 * h + y;
		hash = h;
		
		s = "<" + x + ", " + y + ">";
	}
	
//	public Point(DPoint a) {
//		
//		int x = (int)Math.round(a.x);
//		int y = (int)Math.round(a.y);
//		
//		if (x != a.x) {
//			
//		}
//		if (y != a.y) {
//			
//		}
//		
//		this(x, y);
//	}
	
	@Override
	public boolean equals(Object p) {
		throw new AssertionError();
//		if (this == p) {
//			return true;
//		} else if (!(p instanceof Point)) {
//			throw new IllegalArgumentException();
//		} else {
//			return (x == ((Point)p).x) && (y == ((Point)p).y);
//		}
	}
	
	public static boolean equals(Point a, Point b) {
		return (a.x == b.x) && (a.y == b.y);
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return s;
	}
	
	public static DPoint intersection(Point a, Point b, Point c, Point d) throws OverlappingException {
		return intersection(new DPoint(a), new DPoint(b), new DPoint(c), new DPoint(d));
	}
	
	/**
	 * does <a, b> intersect <c, d>?
	 * where?
	 * 
	 * @return intersection or null
	 * @throws IllegalArgumentException
	 * @throws OverlappingException
	 */
	public static DPoint intersection(DPoint a, DPoint b, DPoint c, DPoint d) throws OverlappingException {
		if (DPoint.equals(a, b)) {
			throw new IllegalArgumentException("a and b are equal");
		}
		if (DPoint.equals(c, d)) {
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
		if (Point.doubleEquals(denom, 0.0)) {
			if (Point.doubleEquals(uabn, 0.0) && Point.doubleEquals(ucdn, 0.0)) {
				//colinear but not overlapping, single point, overlapping, or identical
				
				double cu;
				if (!Point.doubleEquals(xba, 0.0)) {
					cu = (c.x - a.x) / xba;
					if (!Point.doubleEquals(yba, 0.0)) {
						assert cu == (c.y - a.y) / yba;
					}
				} else {
					cu = (c.y - a.y) / yba;
				}
				
				double du;
				if (!Point.doubleEquals(xba, 0.0)) {
					du = (d.x - a.x) / xba;
					if (!Point.doubleEquals(yba, 0.0)) {
						assert du == (d.y - a.y) / yba;
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
				} else if (Point.doubleEquals(du, 0.0)) {
					//single point
					return a;
				} else if (du > 0.0 && du < 1.0) {
					if (cu < 0.0) {
						throw new OverlappingException();
					} else if (Point.doubleEquals(cu, 0.0)) {
						throw new OverlappingException();
					} else {
						throw new OverlappingException();
					}
				} else if (Point.doubleEquals(du, 1.0)) {
					if (cu < 0.0) {
						throw new OverlappingException();
					} else if (Point.doubleEquals(cu, 0.0)) {
						//identical
						throw new OverlappingException();
					} else {
						throw new OverlappingException();
					}
				} else {
					if (cu < 0.0) {
						throw new OverlappingException();
					} else if (Point.doubleEquals(cu, 0.0)) {
						throw new OverlappingException();
					} else if (cu > 0.0 && cu < 1.0) {
						throw new OverlappingException();
					} else if (Point.doubleEquals(cu, 1.0)) {
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
				if (Point.doubleEquals(ucd,  0.0)) {
					// intersecting
					return c;
				} else if (Point.doubleEquals(ucd,  1.0)) {
					// intersecting
					return d;
				} else if (0.0 < ucd && ucd < 1.0) {
					// intersecting
					double x = a.x + (uab * (xba));
					double y = a.y + (uab * (yba));
					return new DPoint(x, y);
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
	public static DPoint point(Point a, Point b, double param) {
		assert param >= 0.0;
		assert param <= 1.0;
		return new DPoint(a.x + param * (b.x - a.x), a.y + param * (b.y - a.y));
	}
	
	/**
	 * Does b intersect &lt;c, d> ?
	 */
	public static boolean intersect(Point b, Point c, Point d) {
		return intersect(new DPoint(b), new DPoint(c), new DPoint(d));
	}
	
	/**
	 * Does b intersect &lt;c, d> ?
	 */
	public static boolean intersect(DPoint b, Point c, Point d) {
		return intersect(b, new DPoint(c), new DPoint(d));
	}
	
	/**
	 * Does b intersect &lt;c, d> ?
	 */
//	public static boolean intersect(Point b, DPoint c, DPoint d) {
//		if (Point.equalsD(c, b)) {
//			return true;
//		}
//		if (Point.equalsD(d, b)) {
//			return false;
//		}
//		if (DPoint.equals(c, d)) {
//			throw new IllegalArgumentException("c equals d");
//		}
//		double xbc = b.x - c.x;
//		double xdc = d.x - c.x;
//		double ybc = b.y - c.y;
//		double ydc = d.y - c.y;
//		double denom = xdc * xdc + ydc * ydc;
//		assert !Point.doubleEquals(denom, 0.0);
//		double u = ((double)(xbc * xdc + ybc * ydc)) / ((double)denom);
//		if (u >= 0.0 && u < 1.0) {
//			return Point.doubleEquals(xbc, u * xdc) && Point.doubleEquals(ybc, u * ydc);
//		} else {
//			return false;
//		}
//	}
	
	/**
	 * Does b intersect &lt;c, d> ?
	 */
	public static boolean intersect(DPoint b, DPoint c, DPoint d) {
		if (DPoint.equals(b, c)) {
			return true;
		}
		if (DPoint.equals(b, d)) {
			return false;
		}	
		if (DPoint.equals(c, d)) {
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
			return Point.doubleEquals(xbc, u * xdc) && Point.doubleEquals(ybc, u * ydc);
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
		if (Point.equals(c, b)) {
			return true;
		}
		if (Point.equals(b, d)) {
			return true;
		}
		if (Point.equals(c, d)) {
			throw new IllegalArgumentException("c equals d");
		}
		int xbc = b.x - (c.x);
		int xdc = d.x - (c.x);
		int ybc = b.y - (c.y);
		int ydc = d.y - (c.y);
		int denom = (xdc * (xdc) + (ydc * (ydc)));
		assert denom != 0;
		// u is where b is perpendicular to <c, d>
		double u = ((double)((xbc * (xdc)) + ((ybc * (ydc))))) / ((double)(denom));
		if (u >= 0.0 && u <= 1.0) {
			return Point.doubleEquals(xbc, (u * (xdc))) && Point.doubleEquals(ybc, (u * (ydc)));
		} else {
			 if (Point.doubleEquals(xbc, (u * (xdc))) && Point.doubleEquals(ybc, (u * (ydc)))) {
				 throw new ColinearException();
			 } else {
				 return false;
			 }
		}
	}
	
	/**
	 * is b inside the segment <c, d>?
	 * ok if b equals c or d
	 * throws exception if b is on the line, but outside of the segment <c, d>
	 */
	public static boolean colinear(Point c, DPoint b, Point d) throws ColinearException {
		if (Point.equalsD(b, c)) {
			return true;
		}
		if (Point.equalsD(b, d)) {
			return true;
		}
		if (c.equals(d)) {
			throw new IllegalArgumentException("c equals d");
		}
		double xbc = b.x - (c.x);
		int xdc = d.x - (c.x);
		double ybc = b.y - (c.y);
		int ydc = d.y - (c.y);
		int denom = (xdc * (xdc) + (ydc * (ydc)));
		assert denom != 0;
		// u is where b is perpendicular to <c, d>
		double u = ((double)((xbc * (xdc)) + ((ybc * (ydc))))) / ((double)(denom));
		if (u >= 0.0 && u <= 1.0) {
			return Point.doubleEquals(xbc, (u * (xdc))) && Point.doubleEquals(ybc, (u * (ydc)));
		} else {
			 if (Point.doubleEquals(xbc, (u * (xdc))) && Point.doubleEquals(ybc, (u * (ydc)))) {
				 throw new ColinearException();
			 } else {
				 return false;
			 }
		}
	}
	
//	/**
//	 * assuming it is, return param for point b on line defined by &lt;c, d>
//	 */
//	public static double param(DPoint b, Point c, Point d) {
//		
//		if (Point.equals(b, c)) {
//			return 0.0;
//		} else if (Point.equals(b, d)) {
//			assert false;
//		}
//		
//		int xbc = b.x - (c.x);
//		int xdc = d.x - (c.x);
//		int ybc = b.y - (c.y);
//		int ydc = d.y - (c.y);
//		if (xdc == 0) {
//			assert xbc == 0;
//			double uy = ((double)ybc) / ((double)ydc);
//			assert uy >= 0.0;
//			assert uy < 1.0;
//			return uy;
//		} else if (ydc == 0) {
//			assert ybc == 0;
//			double ux = ((double)xbc) / ((double)xdc);
//			assert ux >= 0.0;
//			assert ux < 1.0;
//			return ux;
//		} else {
//			double ux = ((double)xbc) / ((double)xdc);
//			double uy = ((double)ybc) / ((double)ydc);
//			assert ux == (uy);
//			assert ux >= 0.0;
//			assert ux < 1.0;
//			return ux;
//		}
//		
//	}
	
	/**
	 * assuming it is, return param for point b on line defined by &lt;c, d>
	 */
	public static double param(Point b, Point c, Point d) {
		return param(new DPoint(b), new DPoint(c), new DPoint(d));
	}
	
	/**
	 * assuming it is, return param for point b on line defined by &lt;c, d>
	 */
	public static double param(DPoint b, Point c, Point d) {
		return param(b, new DPoint(c), new DPoint(d));
	}
	
	/**
	 * assuming it is, return param for point b on line defined by &lt;c, d>
	 */
	public static double param(Point b, DPoint c, DPoint d) {
		return param(new DPoint(b), c, d);
	}
	
	/**
	 * assuming it is, return param for point b on line defined by &lt;c, d>
	 */
	public static double param(DPoint b, DPoint c, DPoint d) {
		
		if (DPoint.equals(b, c)) {
			return 0.0;
		} else if (DPoint.equals(b, d)) {
			return 1.0;
		}
		
		double xbc = b.x - (c.x);
		double xdc = d.x - (c.x);
		double ybc = b.y - (c.y);
		double ydc = d.y - (c.y);
		if (Point.doubleEquals(xdc, 0.0)) {
			assert doubleEquals(xbc, 0);
			double uy = ((double)ybc) / ((double)ydc);
			assert uy < 1.0;
			return uy;
		} else if (Point.doubleEquals(ydc, 0.0)) {
			assert doubleEquals(ybc, 0);
			double ux = ((double)xbc) / ((double)xdc);
			assert ux < 1.0;
			return ux;
		} else {
			double ux = ((double)xbc) / ((double)xdc);
			double uy = ((double)ybc) / ((double)ydc);
			assert doubleEquals(ux, uy) : "being treated as uneqal: " + ux + " " + uy;
			assert ux >= 0.0;
			assert ux < 1.0;
			return ux;
		}
		
	}
	
	public static double dist(Point a, Point b) {
		return Math.hypot(a.x - b.x, a.y - b.y);
	}
	
	public static double dist(Point a, DPoint b) {
		return Math.hypot(a.x - b.x, a.y - b.y);
	}
	
	public static double dist(DPoint a, DPoint b) {
		return Math.hypot(a.x - b.x, a.y - b.y);
	}
	
	public static boolean doubleEquals(double a, double b) {
		/*
		 * 1.0E-12 seems to be fine for the math we do here
		 * 1.0E-13 gives StackOverflowErrors when it is expecting some points to be equal
		 */
		return Math.abs(a - b) < 1.0E-12;
	}
	
	public static boolean equalsD(DPoint a, Point b) {
		return doubleEquals(a.x, b.x) && doubleEquals(a.y, b.y);
	}
	
	public static Point add(Point a, Point b) {
		return new Point(a.x + b.x, a.y + b.y);
	}
	
	public static DPoint add(DPoint a, Point b) {
		return new DPoint(a.x + b.x, a.y + b.y);
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
