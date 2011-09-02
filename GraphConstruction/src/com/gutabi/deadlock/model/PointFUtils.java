package com.gutabi.deadlock.model;

import android.graphics.PointF;

public final class PointFUtils {
	
	private PointFUtils() {
		
	}
	
	public static boolean equals(PointF a, PointF b) {
		return ((a.x == b.x) && (a.y == b.y));
	}
	
	public static boolean intersection(PointF b, PointF d) {
		return ((b.x == d.x) && (b.y == d.y));
	}
	
	public static boolean intersection(PointF b, PointF c, PointF d) {
		float xbc = (b.x - c.x);
		float xdc = (d.x - c.x);
		float ybc = (b.y - c.y);
		float ydc = (d.y - c.y);
		float denom = (xdc * xdc + ydc * ydc);
		float u = ((xbc * xdc) + (ybc * ydc)) / denom;
		if (0 <= u && u <= 1) {
			float x = c.x + u * xdc;
			float y = c.y + u * ydc;
			return ((b.x == x) && (b.y == y));
		} else {
			return false;
		}
	}
	
	/**
	 * return param for point b on line defined by <c, d>
	 */
	public static float param(PointF b, PointF c, PointF d) {
		float xbc = (b.x - c.x);
		float xdc = (d.x - c.x);
		float ybc = (b.y - c.y);
		float ydc = (d.y - c.y);
		
		if (xdc == 0.0) {
			assert xbc == 0.0;
			float uy = ybc / ydc;
			return uy;
		} else if (ydc == 0.0) {
			assert ybc == 0.0;
			float ux = xbc / xdc;
			return ux;
		} else {
			float ux = xbc / xdc;
			float uy = ybc / ydc;
			assert ux == uy;
//			assert 0 <= ux;
//			assert ux <= 1;
			return ux;
		}
		
	}
	
	public static PointF intersection(PointF a, PointF b, PointF c, PointF d) throws OverlappingException {
		if (a.equals(b.x, b.y)) {
			throw new IllegalArgumentException("a and b are equal");
		}
		if (c.equals(d.x, d.y)) {
			throw new IllegalArgumentException("c and d are equal");
		}
		float ydc = (d.y - c.y);
		float xba = (b.x - a.x);
		float xdc = (d.x - c.x);
		float yba = (b.y - a.y);
		float yac = (a.y - c.y);
		float xac = (a.x - c.x);
		float denom = (xba * ydc) - (xdc * yba);
		float uabn = ((xdc * yac) - (xac * ydc));
		float ucdn = ((xba * yac) - (xac * yba));
		if (denom == 0.0) {
			if (uabn == 0.0 && ucdn == 0.0) {
				//overlapping, or identical
				
				float cu;
				if (xba != 0.0) {
					cu = (c.x - a.x)/xba;
					if (yba != 0.0) {
						assert cu == (c.y - a.y)/yba;
					}
				} else {
					cu = (c.y - a.y)/yba;
				}
				
				float du;
				if (xba != 0.0) {
					du = (d.x - a.x)/xba;
					if (yba != 0.0) {
						assert du == (d.y - a.y)/yba;
					}
				} else {
					du = (d.y - a.y)/yba;
				}
				
				if (du < cu) {
					float tmp = cu;
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
			float uab = uabn / denom;
			float ucd = ucdn / denom;
			if ((0 <= uab && uab <= 1) && (0 <= ucd && ucd <= 1)) {
				// intersecting
				float x = a.x + uab * xba;
				float y = a.y + uab * yba;
				return new PointF(x, y);
			} else {
				// not intersecting
				return null;
			}
		}
	}
	
}
