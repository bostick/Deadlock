package com.gutabi.deadlock.swing.utils;

public class Point {
	
	/*
	 * the given points
	 */
//	private final Rat ax;
//	private final Rat ay;
//	private final Rat bx;
//	private final Rat by;
//	private final Rat cx;
//	private final Rat cy;
//	private final Rat dx;
//	private final Rat dy;
	
	public final Rat x;
	public final Rat y;
	
	/*
	 * computed value of intersection, only used for printing/debugging
	 */
//	private final double xVal;
//	private final double yVal;
	
	/*
	 * whether single point or intersection
	 */
	//public final boolean single;
	
	public final int hash;
	public final String s;
	
	public Point(Rat x, Rat y) {
//		ax = x;
//		ay = y;
//		bx = null;
//		by = null;
//		cx = null;
//		cy = null;
//		dx = null;
//		dy = null;
		
		this.x = x;
		this.y = y;
		
//		xVal = x.getVal();
//		yVal = y.getVal();
//		single = true;
		
		int h = 17;
		h = 37 * h + x.hashCode();
		h = 37 * h + y.hashCode();
		hash = h;
		
		s = "<" + x + ", " + y + ">";
	}
	
	public Point(int x, int y) {
		this(new Rat(x, 1), new Rat(y, 1));
	}
	
	public Rat getX() {
		return x;
	}
	
	public Rat getY() {
		return y;
	}
	
	@Override
	public boolean equals(Object p) {
		if (this == p) {
			return true;
		} else if (!(p instanceof Point)) {
			return false;
		} else {
			return (x.equals(((Point)p).x)) && (y.equals(((Point)p).y));
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
	public static Point intersection(Point a, Point b, Point c, Point d) {
		if (a.equals(b)) {
			throw new IllegalArgumentException("a and b are equal");
		}
		if (c.equals(d)) {
			throw new IllegalArgumentException("c and d are equal");
		}
		Rat ydc = d.y.minus(c.y);
		Rat xba = b.x.minus(a.x);
		Rat xdc = d.x.minus(c.x);
		Rat yba = b.y.minus(a.y);
		Rat yac = a.y.minus(c.y);
		Rat xac = a.x.minus(c.x);
		Rat denom = (xba.times(ydc)).minus(xdc.times(yba));
		Rat uabn = (xdc.times(yac)).minus(xac.times(ydc));
		Rat ucdn = (xba.times(yac)).minus(xac.times(yba));
		if (denom.isZero()) {
			if (uabn.isZero() && ucdn.isZero()) {
				//colinear but not overlapping, single point, overlapping, or identical
				
				Rat cu;
				if (!xba.isZero()) {
					cu = (c.x.minus(a.x)).over(xba);
					if (!yba.isZero()) {
						assert cu.equals((c.y.minus(a.y)).over(yba));
					}
				} else {
					cu = (c.y.minus(a.y)).over(yba);
				}
				
				Rat du;
				if (!xba.isZero()) {
					du = (d.x.minus(a.x)).over(xba);
					if (!yba.isZero()) {
						assert du.equals((d.y.minus(a.y)).over(yba));
					}
				} else {
					du = (d.y.minus(a.y)).over(yba);
				}
				
				if (du.isLessThan(cu)) {
					Rat tmp = cu;
					cu = du;
					du = tmp;
				}
				
				if (du.isLessThan(Rat.ZERO)) {
					//colinear but not intersecting
					return null;
				} else if (du.isZero()) {
					//single point
					return a;
				} else if (du.isGreaterThan(Rat.ZERO) && du.isLessThan(Rat.ONE)) {
					if (cu.isLessThan(Rat.ZERO)) {
						throw new OverlappingException();
					} else if (cu.isZero()) {
						throw new OverlappingException();
					} else {
						throw new OverlappingException();
					}
				} else if (du.isOne()) {
					if (cu.isLessThan(Rat.ZERO)) {
						throw new OverlappingException();
					} else if (cu.isZero()) {
						//identical
						throw new OverlappingException();
					} else {
						throw new OverlappingException();
					}
				} else {
					if (cu.isLessThan(Rat.ZERO)) {
						throw new OverlappingException();
					} else if (cu.isZero()) {
						throw new OverlappingException();
					} else if (cu.isGreaterThan(Rat.ZERO) && cu.isLessThan(Rat.ONE)) {
						throw new OverlappingException();
					} else if (cu.isOne()) {
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
			Rat uab = uabn.over(denom);
			Rat ucd = ucdn.over(denom);
//			Rat uabtxba = uabn.times(xba).over(denom);
//			Rat ucdtyba = ucdn.times(yba).over(denom);
			if ((uab.isGreaterThanOrEquals(Rat.ZERO) && uab.isLessThanOrEquals(Rat.ONE)) &&
					(ucd.isGreaterThanOrEquals(Rat.ZERO) && ucd.isLessThanOrEquals(Rat.ONE))) {
//			if ((uabtxba.isGreaterThanOrEquals(Rat.ZERO) && uabtxba.isLessThanOrEquals(xba)) &&
//					(ucdtyba.isGreaterThanOrEquals(Rat.ZERO) && ucdtyba.isLessThanOrEquals(yba))) {
				// intersecting
				Rat x = a.x.plus(uab.times(xba));
				Rat y = a.y.plus(uab.times(yba));
				return new Point(x, y);
			} else {
				// not intersecting
				return null;
			}
		}
	}
	
	/**
	 * return Point defined by param on line &lt;a, b>
	 */
	public static Point point(Point a, Point b, Rat param) {
		assert param.isGreaterThanOrEquals(Rat.ZERO);
		assert param.isLessThan(Rat.ONE);
		return new Point(a.x.plus(param.times(b.x.minus(a.x))), a.y.plus(param.times(b.y.minus(a.y))));
	}
	
	/**
	 * Does b intersect &lt;c, d> ?
	 */
	public static boolean intersect(Point b, Point c, Point d) {
		if (c.equals(d)) {
			throw new IllegalArgumentException("c equals d");
		}
		Rat xbc = b.x.minus(c.x);
		Rat xdc = d.x.minus(c.x);
		Rat ybc = b.y.minus(c.y);
		Rat ydc = d.y.minus(c.y);
		Rat denom = (xdc.times(xdc).plus(ydc.times(ydc)));
		// u is where b is perpendicular to <c, d>
		Rat u = ((xbc.times(xdc)).plus((ybc.times(ydc)))).over(denom);
		if (b.equals(d)) {
			return false;
		} else if (u.isGreaterThanOrEquals(Rat.ZERO) && u.isLessThanOrEquals(Rat.ONE)) {
//			Rat x = c.x.plus(u.times(xdc));
//			Rat y = c.y.plus(u.times(ydc));
//			return b.x.equals(x) && b.y.equals(y);
			
			return xbc.equals(u.times(xdc)) && ybc.equals(u.times(ydc));
			
		} else {
			return false;
		}
	}
	
	/**
	 * are x, y, z colinear?
	 */
	public static boolean colinear(Point x, Point y, Point z) {
		if (x.equals(y)) {
			return true;
		}
		if (y.equals(z)) {
			return true;
		}
		if (x.equals(z)) {
			throw new IllegalArgumentException("x equals z");
		}
		Rat xbc = y.x.minus(x.x);
		Rat xdc = z.x.minus(x.x);
		Rat ybc = y.y.minus(x.y);
		Rat ydc = z.y.minus(x.y);
		Rat denom = (xdc.times(xdc).plus(ydc.times(ydc)));
		// u is where b is perpendicular to <c, d>
		Rat u = ((xbc.times(xdc)).plus((ybc.times(ydc)))).over(denom);
		if (u.isGreaterThanOrEquals(Rat.ZERO) && u.isLessThanOrEquals(Rat.ONE)) {
//			Rat x = c.x.plus(u.times(xdc));
//			Rat y = c.y.plus(u.times(ydc));
//			return b.x.equals(x) && b.y.equals(y);
			
			return xbc.equals(u.times(xdc)) && ybc.equals(u.times(ydc));
			
		} else {
			return false;
		}
	}
	
	/**
	 * assuming it is, return param for point b on line defined by &lt;c, d>
	 */
	public static Rat param(Point b, Point c, Point d) {
		Rat xbc = b.x.minus(c.x);
		Rat xdc = d.x.minus(c.x);
		Rat ybc = b.y.minus(c.y);
		Rat ydc = d.y.minus(c.y);
		if (xdc.equals(Rat.ZERO)) {
			assert xbc.isZero();
			Rat uy = ybc.over(ydc);
			assert uy.isLessThan(Rat.ONE);
			return uy;
		} else if (ydc.equals(Rat.ZERO)) {
			assert ybc.isZero();
			Rat ux = xbc.over(xdc);
			assert ux.isLessThan(Rat.ONE);
			return ux;
		} else {
			Rat ux = xbc.over(xdc);
			Rat uy = ybc.over(ydc);
			assert ux.equals(uy);
			//assert 0 <= ux;
			assert ux.isGreaterThanOrEquals(Rat.ZERO);
//			assert ux <= 1;
			assert ux.isLessThan(Rat.ONE);
			return ux;
		}
		
	}
}
