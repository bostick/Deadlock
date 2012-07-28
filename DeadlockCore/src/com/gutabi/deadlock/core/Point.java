package com.gutabi.deadlock.core;

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
	
	@Override
	public boolean equals(Object p) {
		if (this == p) {
			return true;
		} else if (!(p instanceof Point)) {
			throw new IllegalArgumentException();
		} else {
			return (x == ((Point)p).x) && (y == ((Point)p).y);
		}
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return s;
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return intersection or null
	 * @throws IllegalArgumentException
	 * @throws OverlappingException
	 */
	public static DPoint intersection(Point a, Point b, Point c, Point d) throws OverlappingException {
		if (a.equals(b)) {
			throw new IllegalArgumentException("a and b are equal");
		}
		if (c.equals(d)) {
			throw new IllegalArgumentException("c and d are equal");
		}
		int ydc = d.y - (c.y);
		int xba = b.x - (a.x);
		int xdc = d.x - (c.x);
		int yba = b.y - (a.y);
		int yac = a.y - (c.y);
		int xac = a.x - (c.x);
		int denom = (xba * (ydc)) - (xdc * (yba));
		int uabn = (xdc * (yac)) - (xac * (ydc));
		int ucdn = (xba * (yac)) - (xac * (yba));
		if (denom == 0) {
			if (uabn == 0 && ucdn == 0) {
				//colinear but not overlapping, single point, overlapping, or identical
				
				double cu;
				if (xba != 0) {
					cu = ((double)(c.x - (a.x))) / ((double)(xba));
					if (yba != 0) {
						assert cu == ((double)((c.y - (a.y))) / ((double)(yba)));
					}
				} else {
					cu = ((double)(c.y - (a.y))) / ((double)(yba));
				}
				
				double du;
				if (xba != 0) {
					du = ((double)(d.x - (a.x))) / ((double)(xba));
					if (yba != 0) {
						assert du == ((double)((d.y - (a.y))) / ((double)(yba)));
					}
				} else {
					du = ((double)(d.y - (a.y))) / ((double)(yba));
				}
				
				if (du < (cu)) {
					double tmp = cu;
					cu = du;
					du = tmp;
				}
				
				if (du < 0.0) {
					//colinear but not intersecting
					return null;
				} else if (Point.doubleEquals(du, 0.0)) {
					//single point
					return new DPoint(a.x, a.y);
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
						return new DPoint(b.x, b.y);
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
			double uab = ((double)uabn) / ((double)(denom));
			double ucd = ((double)ucdn) / ((double)(denom));
			if ((uab >= 0.0 && uab <= 1.0 && (ucd >= 0.0 && ucd <= 1.0))) {
				// intersecting
				double x = a.x + (uab * (xba));
				double y = a.y + (uab * (yba));
				return new DPoint(x, y);
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
		assert param < 1.0;
		return new DPoint(a.x + (param * (b.x - (a.x))), a.y + (param * (b.y - (a.y))));
	}
	
	/**
	 * Where is b perpendicular to &lt;c, d> ?
	 * could be > 1 or < 0
	 */
//	public static double perp(Point b, Point c, Point d) {
//		if (c.equals(d)) {
//			throw new IllegalArgumentException("c equals d");
//		}
//		int xbc = b.x - (c.x);
//		int xdc = d.x - (c.x);
//		int ybc = b.y - (c.y);
//		int ydc = d.y - (c.y);
//		int denom = (xdc * (xdc) + (ydc * (ydc)));
//		assert denom != 0;
//		// u is where b is perpendicular to <c, d>
//		double u = ((double)((xbc * (xdc)) + ((ybc * (ydc))))) / ((double)(denom));
//		return u;
//	}
	
	/**
	 * Where is b perpendicular to &lt;c, d> ?
	 * could be > 1 or < 0
	 */
//	public static double perp(DPoint b, Point c, Point d) {
//		if (c.equals(d)) {
//			throw new IllegalArgumentException("c equals d");
//		}
//		double xbc = b.x - (c.x);
//		int xdc = d.x - (c.x);
//		double ybc = b.y - (c.y);
//		int ydc = d.y - (c.y);
//		int denom = (xdc * (xdc) + (ydc * (ydc)));
//		assert denom != 0;
//		// u is where b is perpendicular to <c, d>
//		double u = ((double)((xbc * (xdc)) + ((ybc * (ydc))))) / ((double)(denom));
//		return u;
//	}
	
	/**
	 * Does b intersect &lt;c, d> ?
	 */
	public static boolean intersect(Point b, Point c, Point d) {
		if (b.equals(c)) {
			return true;
		}
		if (b.equals(d)) {
			return false;
		}
		if (c.equals(d)) {
			throw new IllegalArgumentException("c equals d");
		}
		int xbc = b.x - c.x;
		int xdc = d.x - c.x;
		int ybc = b.y - c.y;
		int ydc = d.y - c.y;
		int denom = xdc * xdc + ydc * ydc;
		assert denom != 0;
		double u = ((double)(xbc * xdc + ybc * ydc)) / ((double)denom);
		if (u >= 0.0 && u < 1.0) {
			return Point.doubleEquals(xbc, u * xdc) && Point.doubleEquals(ybc, u * ydc);
		} else {
			return false;
		}
	}
	
	/**
	 * Does b intersect &lt;c, d> ?
	 */
	public static boolean intersect(DPoint b, Point c, Point d) {
		if (Point.equals(b, c)) {
			return true;
		}
		if (Point.equals(b, d)) {
			return false;
		}	
		if (c.equals(d)) {
			throw new IllegalArgumentException("c equals d");
		}
		double xbc = b.x - c.x;
		int xdc = d.x - c.x;
		double ybc = b.y - c.y;
		int ydc = d.y - c.y;
		int denom = xdc * xdc + ydc * ydc;
		assert denom != 0;
		double u = ((double)(xbc * xdc + ybc * ydc)) / ((double)denom);
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
		if (c.equals(b)) {
			return true;
		}
		if (b.equals(d)) {
			return true;
		}
		if (c.equals(d)) {
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
		if (Point.equals(b, c)) {
			return true;
		}
		if (Point.equals(b, d)) {
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
	
	/**
	 * assuming it is, return param for point b on line defined by &lt;c, d>
	 */
	public static double param(Point b, Point c, Point d) {
		
		if (b.equals(c)) {
			return 0.0;
		} else if (b.equals(d)) {
			assert false;
		}
		
		int xbc = b.x - (c.x);
		int xdc = d.x - (c.x);
		int ybc = b.y - (c.y);
		int ydc = d.y - (c.y);
		if (xdc == 0) {
			assert xbc == 0;
			double uy = ((double)ybc) / ((double)ydc);
			assert uy >= 0.0;
			assert uy < 1.0;
			return uy;
		} else if (ydc == 0) {
			assert ybc == 0;
			double ux = ((double)xbc) / ((double)xdc);
			assert ux >= 0.0;
			assert ux < 1.0;
			return ux;
		} else {
			double ux = ((double)xbc) / ((double)xdc);
			double uy = ((double)ybc) / ((double)ydc);
			assert ux == (uy);
			assert ux >= 0.0;
			assert ux < 1.0;
			return ux;
		}
		
	}
	
	/**
	 * assuming it is, return param for point b on line defined by &lt;c, d>
	 */
	public static double param(DPoint b, Point c, Point d) {
		
		if (Point.equals(b, c)) {
			return 0.0;
		} else if (Point.equals(b, d)) {
			return 1.0;
		}
		
		double xbc = b.x - (c.x);
		int xdc = d.x - (c.x);
		double ybc = b.y - (c.y);
		int ydc = d.y - (c.y);
		if (xdc == 0) {
			assert doubleEquals(xbc, 0);
			double uy = ((double)ybc) / ((double)ydc);
			assert uy < 1.0;
			return uy;
		} else if (ydc == 0) {
			assert doubleEquals(ybc, 0);
			double ux = ((double)xbc) / ((double)xdc);
			assert ux < 1.0;
			return ux;
		} else {
			double ux = ((double)xbc) / ((double)xdc);
			double uy = ((double)ybc) / ((double)ydc);
			assert doubleEquals(ux, uy);
			assert ux >= 0.0;
			assert ux < 1.0;
			return ux;
		}
		
	}
	
	public static double dist(Point a, Point b) {
		return Math.hypot(a.x - b.x, a.y - b.y);
	}
	
	public static boolean doubleEquals(double a, double b) {
		return Math.abs(a - b) < 0.0001;
	}
	
	public static boolean equals(DPoint a, Point b) {
		return doubleEquals(a.x, b.x) && doubleEquals(a.y, b.y);
	}
}
