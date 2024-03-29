package com.brentonbostick.capsloc.math;

import java.io.Serializable;
import java.util.Scanner;
import java.util.regex.MatchResult;

import com.brentonbostick.capsloc.geom.AABB;
import com.brentonbostick.capsloc.geom.MutableAABB;
import com.brentonbostick.capsloc.ui.Menu;
import com.brentonbostick.capsloc.ui.Panel;
import com.brentonbostick.capsloc.world.WorldCamera;

public class Point implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final Point UP = new Point(0, -1);
	public static final Point DOWN = new Point(0, 1);
	public static final Point LEFT = new Point(-1, 0);
	public static final Point RIGHT = new Point(1, 0);
	
	public final double x;
	public final double y;
	
	private int hash;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
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
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			long l = Double.doubleToLongBits(x);
			int c = (int)(l ^ (l >>> 32));
			h = 37 * h + c;
			l = Double.doubleToLongBits(y);
			c = (int)(l ^ (l >>> 32));
			h = 37 * h + c;
			hash = h;
		}
		return hash;
	}
	
	public String toString() {
		return "<" + x + ", " + y + ">";
	} 
	
	public String toFileString() {
		return "<" + x + ", " + y + ">";
	}
	
	public static Point fromFileString(String s) {
		Scanner sc = new Scanner(s);
		sc.findInLine("<(\\d+\\.\\d+), (\\d+\\.\\d+)>");
		MatchResult result = sc.match();
		String x = result.group(1);
		String y = result.group(2);
		sc.close();
		return new Point(Double.parseDouble(x), Double.parseDouble(y));
	}
	
	public double distanceTo(Point p) {
		return Point.distance(this, p);
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
						assert DMath.equals((c.x - a.x) * yba, (c.y - a.y) * xba);
					}
				} else {
					cu = (c.y - a.y) / yba;
				}
				
				double du;
				if (!DMath.equals(xba, 0.0)) {
					du = (d.x - a.x) / xba;
					if (!DMath.equals(yba, 0.0)) {
						assert DMath.equals((d.x - a.x) * yba, (d.y - a.y) * xba);
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
	public static double travelForward(Point start, Point end, double param, double dist) {
		
		if (dist == 0.0) {
			return param;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		double cx = start.x + param * (end.x - start.x);
		double cy = start.y + param * (end.y - start.y);
		
		double rad = Math.atan2(end.y - start.y, end.x - start.x);
		double x = Math.cos(rad) * dist + cx;
		double y = Math.sin(rad) * dist + cy;
		
		assert DMath.equals(distance(cx, cy, x, y), dist);
		
		return param(x, y, start, end);
		
	}
	
	/**
	 * return param that is dist away from a along the segment <a, b>
	 */
	public static double travelBackward(Point start, Point end, double param, double dist) {
		
		if (dist == 0.0) {
			return param;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		double cx = start.x + param * (end.x - start.x);
		double cy = start.y + param * (end.y - start.y);
		
		double rad = Math.atan2(start.y - end.y, start.x - end.x);
		double x = Math.cos(rad) * dist + cx;
		double y = Math.sin(rad) * dist + cy;
		
		assert DMath.equals(distance(cx, cy, x, y), dist);
		
		return param(x, y, start, end);
		
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
			throw new ColinearException();
		}
		double xbc = b.x - c.x;
		double xdc = d.x - c.x;
		double ybc = b.y - c.y;
		double ydc = d.y - c.y;
		double denom = xdc * xdc + ydc * ydc;
		assert !DMath.equals(denom, 0.0);
		// u is where b is perpendicular to <c, d>
		double u;
		double n = (xbc * xdc + ybc * ydc);
		if (DMath.equals(n, 0.0)) {
			u = 0.0;
		} else {
			u = n / denom;
		}
		if (DMath.greaterThanEquals(u, 0.0) && DMath.lessThanEquals(u, 1.0)) {
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
	 * returns u for point b in segment &lt;c, d>
	 * 
	 * b could be off of the segment, in which case it is projected onto &lt;c, d>
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
		double u;
		double n = (xbc * xdc + ybc * ydc);
		if (DMath.equals(n, 0.0)) {
			u = 0.0;
		} else {
			u = n / denom;
		}
		return u;
	}
	
	/**
	 * returns u for point b in segment &lt;c, d>
	 * 
	 * if b is not on the segment, then -1 is returned;
	 */
	public static double uNoProjection(Point c, Point b, Point d) {
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
		double denom = xdc * xdc + ydc * ydc;
		assert !DMath.equals(denom, 0.0);
		// u is where b is perpendicular to <c, d>
		double u;
		double n = (xbc * xdc + ybc * ydc);
		if (DMath.equals(n, 0.0)) {
			u = 0.0;
		} else {
			u = n / denom;
		}
		if (DMath.lessThan(u, 0.0) || DMath.greaterThan(u, 1.0)) {
			return -1;
		} else {
			 if (DMath.equals(xbc, u * xdc) && DMath.equals(ybc, u * ydc)) {
				 return u;
			 } else {
				 /*
				  * not colinear
				  */
				 return -1;
			 }
		}
	}
	
	/**
	 * distance of b from the segment &lt;c, d>
	 */
	public static double distance(Point b, Point c, Point d) {
		if (c.equals(d)) {
			return distance(b, c);
		} else {
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
	}
	
	/**
	 * assuming it is, return param for point b on line defined by &lt;c, d>
	 */
	public static double param(Point b, Point c, Point d) {
		return param(b.x, b.y, c, d);
	}
	
	/**
	 * assuming it is, return param for point b on line defined by &lt;c, d>
	 */
	public static double param(double bx, double by, Point c, Point d) {
		
		if (DMath.equals(bx, c.x) && DMath.equals(by, c.y)) {
			return 0.0;
		}
		if (DMath.equals(bx, d.x) && DMath.equals(by, d.y)) {
			return 1.0;
		}
		
		double xbc = bx - c.x;
		double xdc = d.x - c.x;
		double ybc = by - c.y;
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
			/*
			 * numerically stable
			 */
			assert DMath.equals(xbc * ydc, ybc * xdc);
			return ux;
		}
		
	}
	
	public static double distance(Point a, Point b) {
		return Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
	}	
	
	public static double distance(double ax, double ay, double bx, double by) {
		return Math.sqrt((ax - bx)*(ax - bx) + (ay - by)*(ay - by));
	}
	
	public static boolean equalDistances(Point a, Point b, Point c) {
		double ab = (a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y);
		double ac = (a.x-c.x)*(a.x-c.x) + (a.y-c.y)*(a.y-c.y);
		return DMath.equals(ab, ac);
	}
	
	
	/**
	 * returns the number of intersections with the circle and the line
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
			
			Point int1 = new Point((dd * dy)/(dr * dr), (-dd * dx)/(dr * dr)).plus(center);
			
			ints[0] = int1;
			
			return 1;
			
		} else if (disc > 0.0) {
			
			Point int1 = new Point((dd * dy + DMath.sgn(dy) * dx * Math.sqrt(disc))/(dr * dr), (-dd * dx + Math.abs(dy) * Math.sqrt(disc))/(dr * dr)).plus(center);
			Point int2 = new Point((dd * dy - DMath.sgn(dy) * dx * Math.sqrt(disc))/(dr * dr), (-dd * dx - Math.abs(dy) * Math.sqrt(disc))/(dr * dr)).plus(center);
			
			ints[0] = int1;
			ints[1] = int2;
			
			return 2;
			
		} else {
			return 0;
		}
		
	}
	
	/**
	 * returns the number of intersections with the circle and the segment
	 */
	public static int circleSegmentIntersections(Point center, double radius, Point a, Point b, Point[] ints) {
		
		int n = circleLineIntersections(center, radius, a, b, ints);
		
		if (n == 0) {
			return 0;
		} else if (n == 1) {
			
			Point i0 = ints[0];
			
			if (Point.intersect(i0, a, b)) {
				return 1;
			} else {
				return 0;
			}
			
		} else {
			
			Point i0 = ints[0];
			Point i1 = ints[1];
			
			if (Point.intersect(i0, a, b)) {
				if (Point.intersect(i1, a, b)) {
					return 2;
				} else {
					return 1;
				}
			} else {
				ints[0] = i1;
				if (Point.intersect(i1, a, b)) {
					return 1;
				} else {
					return 0;
				}
			}
			
		}
		
	}
	
	
	public static double dot(Point a, Point b) {
		return (a.x * b.x) + (a.y * b.y);
	}
	
	/**
	 * for coord system with <0, 0> in upper left, y extending down
	 */
	public static Point cw90AndNormalize(Point p) {
		double newX = -p.y;
		double newY = p.x;
		return Point.normalize(newX, newY);
	}
	
	/**
	 * for coord system with <0, 0> in upper left, y extending down
	 */
	public static Point ccw90AndNormalize(Point p) {
		double newX = p.y;
		double newY = -p.x;
		return Point.normalize(newX, newY);
	}
	
	public Point normalize() {
		return Point.normalize(x, y);
	}
	
	/**
	 * for coord system with <0, 0> in upper left, y extending down
	 */
	public static Point normalize(double x, double y) {
		
		if (DMath.equals(x, 0.0)) {
			if (DMath.sgn(y) == 1) {
				return Point.DOWN;
			} else {
				return Point.UP;
			}
		} else if (DMath.equals(y, 0.0)) {
			if (DMath.sgn(x) == 1) {
				return Point.RIGHT;
			} else {
				return Point.LEFT;
			}
		} else {
			double len = Math.hypot(x, y);
			assert !DMath.equals(len, 0.0);
			double invLen = 1 / len;
			return new Point(x * invLen, y * invLen);
		}
		
	}
	
	public boolean isRightAngleNormal() {
		return (x == 0.0 && (y == 1.0 || y == -1.0)) || (y == 0.0 && (x == 1.0 || x == -1.0));
	}
	
	public double length() {
		return Math.hypot(x, y);
	}
	
	public Point multiply(double scale) {
		return new Point(x * scale, y * scale);
	}
	
	public Point plus(Point b) {
		return new Point(x + b.x, y + b.y);
	}
	
	public Point plus(double xx, double yy) {
		return new Point(x + xx, y + yy);
	}
	
	public Point minus(Point b) {
		return new Point(x - b.x, y - b.y);
	}
	
	public Point minus(double xx, double yy) {
		return new Point(x - xx, y - yy);
	}
	
	public Point minus(Dim d) {
		return new Point(x - d.width, y - d.height);
	}
	
	public Point minusAndNormalize(Point b) {
		
		double newX = x - b.x;
		double newY = y - b.y;
		
		double len = Math.hypot(newX, newY);
		double invLen = 1 / len;
		return new Point(newX * invLen, newY * invLen);
		
	}
	
	public Point negate() {
		return new Point(-x, -y);
	}
	
	public double lengthSquared() {
		return x*x + y*y;
	}
	
	
	
	public static Point panelToWorld(Point p, WorldCamera cam) {
		return new Point(p.x / cam.pixelsPerMeter + cam.worldViewport.x, p.y / cam.pixelsPerMeter + cam.worldViewport.y);
	}
	
	public static Point panelToWorld(double x, double y, WorldCamera cam) {
		return new Point(x / cam.pixelsPerMeter + cam.worldViewport.x, y / cam.pixelsPerMeter + cam.worldViewport.y);
	}
	
	public static AABB panelToWorld(AABB aabb, WorldCamera cam) {
		Point ul = panelToWorld(aabb.x, aabb.y, cam);
		Point br = panelToWorld(aabb.brX, aabb.brY, cam);
		return new AABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	public static Point worldToPanel(Point p, WorldCamera cam) {
		return new Point(
				(p.x - cam.worldViewport.x) * cam.pixelsPerMeter,
				(p.y - cam.worldViewport.y) * cam.pixelsPerMeter);
	}
	
//	public static Point worldToBackgroundImage(double x, double y, WorldCamera cam) {
//		return new Point(
//				(x ) * cam.pixelsPerMeter,
//				(y ) * cam.pixelsPerMeter);
//	}
	
	public static Point worldToPanel(double x, double y, WorldCamera cam) {
		return new Point(
				(x - cam.worldViewport.x) * cam.pixelsPerMeter,
				(y - cam.worldViewport.y) * cam.pixelsPerMeter);
	}
	
	public static AABB worldToPanel(AABB aabb, WorldCamera cam) {
		Point ul = worldToPanel(aabb.x, aabb.y, cam);
		Point br = worldToPanel(aabb.brX, aabb.brY, cam);
		return new AABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	public static void worldToPanel(MutableAABB aabb, WorldCamera cam, MutableAABB out) {
		double ulX = (aabb.x - cam.worldViewport.x) * cam.pixelsPerMeter;
		double ulY = (aabb.y - cam.worldViewport.y) * cam.pixelsPerMeter;
		double brX = (aabb.x+aabb.width - cam.worldViewport.x) * cam.pixelsPerMeter;
		double brY = (aabb.y+aabb.height - cam.worldViewport.y) * cam.pixelsPerMeter;
		out.setShape(ulX, ulY, brX - ulX, brY - ulY);
	}
	
	public static Point panelToMenu(Point p, Menu menu) {
		return new Point((p.x - menu.aabb.x) / menu.scale, (p.y - menu.aabb.y) / menu.scale);
	}
	
//	public static Point previewToWorld(Point p, WorldCamera cam) {
//		return new Point((1/cam.previewPixelsPerMeter) * p.x, (1/cam.previewPixelsPerMeter) * p.y);
//	}
	
//	public static Point worldToPreview(Point p, WorldCamera cam) {
//		return new Point((cam.previewPixelsPerMeter) * p.x, (cam.previewPixelsPerMeter) * p.y);
//	}
	
//	public static Point worldToPreview(double x, double y, WorldCamera cam) {
//		return new Point((cam.previewPixelsPerMeter) * x, (cam.previewPixelsPerMeter) * y);
//	}
	
//	public static Point controlPanelToPreview(Point p, WorldCamera cam) {
//		return new Point(p.x - cam.previewAABB.x, p.y - cam.previewAABB.y);
//	}
	
	public static Point contentPaneToPanel(Point p, Panel child) {
		return new Point(p.x - child.aabb.x, p.y - child.aabb.y);
	}
	
}
