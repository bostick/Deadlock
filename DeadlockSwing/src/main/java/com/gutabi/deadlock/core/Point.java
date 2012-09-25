package com.gutabi.deadlock.core;


public class Point {
	
	private final double x;
	private final double y;
	
	private final int hash;
	private String s;
	
	public Point(double x, double y) {
		
		this.x = x;
		this.y = y;
		
		int h = 17;
		long l = Double.doubleToLongBits(x);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		l = Double.doubleToLongBits(y);
		c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
		
		s = "<" + x + ", " + y + ">";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Point)) {
			return false;
		} else {
			Point b = (Point)o;
			return DMath.equals(x, b.x) && DMath.equals(y, b.y);
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
		assert !a.equals(b);
		assert !c.equals(d);
		
		double ydc = d.y - c.y;
		double xba = b.x - a.x;
		double xdc = d.x - c.x;
		double yba = b.y - a.y;
		double yac = a.y - c.y;
		double xac = a.x - c.x;
		double denom = xba * ydc - xdc * yba;
		double uabn = xdc * yac - xac * ydc;
		double ucdn = xba * yac - xac * yba;
		if (DMath.equals(denom, 0.0)) {
			if (DMath.equals(uabn, 0.0) && DMath.equals(ucdn, 0.0)) {
				//colinear but not overlapping, single point, overlapping, or identical
				
				double cu;
				if (!DMath.equals(xba, 0.0)) {
					cu = (c.x - a.x) / xba;
					if (!DMath.equals(yba, 0.0)) {
						//assert DMath.equals(cu, (c.y - a.y) / yba, 1.0E-4);
						//assert DMath.equals(cu * yba, (c.y - a.y), 1.0E-6);
						assert DMath.equals((c.x - a.x) * yba, (c.y - a.y) * xba, 1.0E-7);
					}
				} else {
					cu = (c.y - a.y) / yba;
				}
				
				double du;
				if (!DMath.equals(xba, 0.0)) {
					du = (d.x - a.x) / xba;
					if (!DMath.equals(yba, 0.0)) {
						//assert DMath.equals(du, (d.y - a.y) / yba, 1.0E-4);
						assert DMath.equals((d.x - a.x) * yba, (d.y - a.y) * xba, 1.0E-7);
					}
				} else {
					du = (d.y - a.y) / yba;
				}
				
				if (du < cu) {
					double tmp = cu;
					cu = du;
					du = tmp;
				}
				
				if (DMath.equals(du, 0.0)) {
					//single point
					return a;
				} else if (DMath.equals(du, 1.0)) {
					if (DMath.equals(cu, 0.0)) {
						//identical
						throw new OverlappingException(a, b, c, d);
					} else if (cu < 0.0) {
						throw new OverlappingException(a, b, c, d);
					} else {
						throw new OverlappingException(a, b, c, d);
					}
				} else if (du < 0.0) {
					//colinear but not intersecting
					return null;
				} else if (du > 0.0 && du < 1.0) {
					if (DMath.equals(cu, 0.0)) {
						throw new OverlappingException(a, b, c, d);
					} else if (cu < 0.0) {
						throw new OverlappingException(a, b, c, d);
					} else {
						throw new OverlappingException(a, b, c, d);
					}
				} else {
					if (DMath.equals(cu, 0.0)) {
						throw new OverlappingException(a, b, c, d);
					} else if (DMath.equals(cu, 1.0)) {
						// single point
						return b;
					} else if (cu < 0.0) {
						throw new OverlappingException(a, b, c, d);
					} else if (cu > 0.0 && cu < 1.0) {
						throw new OverlappingException(a, b, c, d);
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
			if (DMath.lessThanEquals(0.0, uab) && DMath.lessThanEquals(uab, 1.0)) {
				if (DMath.equals(ucd,  0.0)) {
					// intersecting
					return c;
				} else if (DMath.equals(ucd,  1.0)) {
					// intersecting
					return d;
				} else if (0.0 < ucd && ucd < 1.0) {
					// intersecting
					double x = a.x + (uab * xba);
					double y = a.y + (uab * yba);
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
		
		double rad = Math.atan2(end.y - start.y, end.x - start.x);
		double x = Math.cos(rad) * dist + c.x;
		double y = Math.sin(rad) * dist + c.y;
		
		Point m = new Point(x, y);
		
		assert DMath.equals(distance(c, m), dist);
		
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
		
		double rad = Math.atan2(start.y - end.y, start.x - end.x);
		double x = Math.cos(rad) * dist + c.x;
		double y = Math.sin(rad) * dist + c.y;
		
		Point m = new Point(x, y);
		
		assert DMath.equals(distance(c, m), dist);
		
		return param(m, start, end);
		
	}
	
	/**
	 * Does b intersect &lt;c, d> ?
	 */
	public static boolean intersect(Point b, Point c, Point d) {
		if (b.equals(c)) {
			return true;
		}
		if (b.equals(d)) {
			return true;
		}	
		if (c.equals(d)) {
			throw new IllegalArgumentException("c equals d");
		}
		double xbc = b.x - c.x;
		double xdc = d.x - c.x;
		double ybc = b.y - c.y;
		double ydc = d.y - c.y;
		double denom = xdc * xdc + ydc * ydc;
		assert !DMath.equals(denom, 0.0);
		double u = (xbc * xdc + ybc * ydc) / denom;
		if (u >= 0.0 && u <= 1.0) {	
			return DMath.equals(xbc, u * xdc) && DMath.equals(ybc, u * ydc);
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
		if (b.equals(c)) {
			return true;
		}
		if (b.equals(d)) {
			return true;
		}
		if (c.equals(d)) {
			throw new IllegalArgumentException("c equals d");
		}
		double xbc = b.x - c.x;
		double xdc = d.x - c.x;
		double ybc = b.y - c.y;
		double ydc = d.y - c.y;
		double denom = xdc * xdc + ydc * ydc;
		assert !DMath.equals(denom, 0.0);
		// u is where b is perpendicular to <c, d>
		double u = (xbc * xdc + ybc * ydc) / denom;
		if (u >= 0.0 && u <= 1.0) {
			return DMath.equals(xbc, u * xdc) && DMath.equals(ybc, u * ydc);
		} else {
			 if (DMath.equals(xbc, u * xdc) && DMath.equals(ybc, u * ydc)) {
				 throw new ColinearException();
			 } else {
				 return false;
			 }
		}
	}
	
	/**
	 * returns u for point b in segment <c, d>
	 */
	public static double u(Point c, Point b, Point d) {
		if (b.equals(c)) {
			return 0.0;
		}
		if (b.equals(d)) {
			return 1.0;
		}
		if (c.equals(d)) {
			throw new IllegalArgumentException("c equals d");
		}
		double xbc = b.x - c.x;
		double xdc = d.x - c.x;
		double ybc = b.y - c.y;
		double ydc = d.y - c.y;
		double denom = xdc * xdc + ydc * ydc;
		assert !DMath.equals(denom, 0.0);
		// u is where b is perpendicular to <c, d>
		double u = (xbc * xdc + ybc * ydc) / denom;
		return u;
	}
	
	/**
	 * distance of b from the segment <c, d>
	 */
	public static double distance(Point b, Point c, Point d) {
		double u = u(c, b, d);
		if (u < 0.0) {
			return distance(b, c);
		} else if (u > 1.0) {
			return distance(b, d);
		} else {
			Point p = Point.point(c, d, u);
			return distance(b, p);
		}
	}
	
	
	/**
	 * assuming it is, return param for point b on line defined by &lt;c, d>
	 */
	public static double param(Point b, Point c, Point d) {
		
		if (b.equals(c)) {
			return 0.0;
		}
		if (b.equals(d)) {
			return 1.0;
		}
		
		double xbc = b.x - c.x;
		double xdc = d.x - c.x;
		double ybc = b.y - c.y;
		double ydc = d.y - c.y;
		if (DMath.equals(xdc, 0.0)) {
			assert DMath.equals(xbc, 0);
			double uy = ybc / ydc;
			return uy;
		} else if (DMath.equals(ydc, 0.0)) {
			assert DMath.equals(ybc, 0);
			double ux = xbc / xdc;
			return ux;
		} else {
			double ux = xbc / xdc;
			//assert DMath.equals(ux, uy) : "being treated as uneqal: " + ux + " " + uy;
			/*
			 * numerically stable
			 */
			assert DMath.equals(xbc * ydc, ybc * xdc);
			return ux;
		}
		
	}
	
	public static double distance(Point a, Point b) {
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
	
	
	
	/**
	 * returns the number of intersections with the circle and the line
	 * 
	 */
	public static int circleLineIntersections(Point center, double radius, Point a, Point b, Point[] ints) {
		
		Point aa = a.minus(center);
		Point bb = b.minus(center);
		
		double dx = bb.x - aa.x;
		double dy = bb.y - aa.y;
		double dr = Math.hypot(dx, dy);
		double dd = aa.x * bb.y - bb.x * aa.y;
		double disc = radius * radius * dr * dr - dd * dd;
		
		if (DMath.equals(disc, 0.0)) {
			
			Point int1 = new Point((dd * dy)/(dr * dr), (-dd * dx)/(dr * dr)).add(center);
			
			ints[0] = int1;
			
			return 1;
			
		} else if (disc > 0.0) {
			
			Point int1 = new Point((dd * dy + sgn(dy) * dx * Math.sqrt(disc))/(dr * dr), (-dd * dx + Math.abs(dy) * Math.sqrt(disc))/(dr * dr)).add(center);
			Point int2 = new Point((dd * dy - sgn(dy) * dx * Math.sqrt(disc))/(dr * dr), (-dd * dx - Math.abs(dy) * Math.sqrt(disc))/(dr * dr)).add(center);
			
			ints[0] = int1;
			ints[1] = int2;
			
			return 2;
			
		} else {
			return 0;
		}
		
	}
	
	private static double sgn(double x) {
		if (DMath.equals(x, 0.0)) {
			return 1;
		} else if (x < 0.0) {
			return -1;
		} else {
			return 1;
		}
	}
}
