package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.core.graph.SweepEvent.SweepEventType;
import com.gutabi.deadlock.model.Stroke;

@SuppressWarnings("static-access")
public class Capsule {
	
	public final Point a;
	public final Point b;
	public final double r;
	
	/*
	 * normal
	 */
	private final Point n;
	
	private final Point aUp;
	private final Point aDown;
	private final Point bUp;
	private final Point bDown;
	
	public final Rect aabb;
	
	SweepEventListener l;
	
	private final int hash;
	
	static Logger logger = Logger.getLogger(Capsule.class);
	
	public Capsule(Point a, Point b, double r) {
		if (a.equals(b)) {
			throw new IllegalArgumentException("a equals b");
		}
			
		this.a = a;
		this.b = b;
		this.r = r;
		
		int h = 17;
		h = 37 * h + a.hashCode();
		h = 37 * h + b.hashCode();
		long l = Double.doubleToLongBits(r);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
		
		Point diff = new Point(b.x - a.x, b.y - a.y);
		n = Point.ccw90(diff).normalize();
		
		Point u = Point.ccw90(diff).multiply(r / diff.length);
		Point d = Point.cw90(diff).multiply(r / diff.length);
		aUp = a.plus(u);
		aDown = a.plus(d);
		bUp = b.plus(u);
		bDown = b.plus(d);
		
		double ulX = Math.min(a.x, b.x) - r;
		double ulY = Math.min(a.y, b.y) - r;
		double brX = Math.max(a.x, b.x) + r;
		double brY = Math.max(a.y, b.y) + r;
		
		aabb = new Rect(ulX, ulY, brX - ulX, brY - ulY);
		
	}
	
	public int hashCode() {
		return hash;
	}
	
	public String toString() {
		return "(" + a + " " + b + ")";
	}
	
	public void setSweepEventListener(SweepEventListener l) {
		this.l = l;
	}
	
	public void sweepStart(Stroke s) {
		
		Point c = s.pts.get(0);
		
		if (hitTest(c, s.r)) {
			l.start(new SweepEvent(SweepEventType.CAPSULESTART, c, 0, 0.0, this));
		}
		
	}
	
	public void sweep(Stroke s, int index) {
		sweepACap(s, index);
		sweepSides(s, index);
		sweepBCap(s, index);
	}
	
	private void sweepSides(Stroke s, int index) {
		
		Point c = s.pts.get(index);
		Point d = s.pts.get(index+1);
		
		boolean outside;
		if (hitTest(c, s.r)) {
			outside = false;
		} else {
			outside = true;
		}
		
		/*
		 * handle aUp, bUp side
		 */
		double cDistance = Point.dot(c.minus(aUp), n);
		double dDistance = Point.dot(d.minus(aUp), n);
		double cdParam = (s.r - cDistance) / (dDistance - cDistance);
		if (DMath.greaterThanEquals(cdParam, 0.0) && DMath.lessThanEquals(cdParam, 1.0)) {
			Point p = Point.point(c, d, cdParam);
			double abParam = Point.u(a, p, b);
			if (DMath.greaterThanEquals(abParam, 0.0) && DMath.lessThanEquals(abParam, 1.0)) {
				assert DMath.equals(Point.distance(p, a, b), r + s.r);
				if (outside) {
					l.event(new SweepEvent(SweepEventType.ENTERCAPSULE, p, index, cdParam, this));
				} else {
					l.event(new SweepEvent(SweepEventType.EXITCAPSULE, p, index, cdParam, this));
				}
			}
		}
		
		/*
		 * handle aDown, bDown side
		 */
		cDistance = Point.dot(c.minus(aDown), n);
		dDistance = Point.dot(d.minus(aDown), n);
		cdParam = (-s.r - cDistance) / (dDistance - cDistance);
		if (DMath.greaterThanEquals(cdParam, 0.0) && DMath.lessThanEquals(cdParam, 1.0)) {
			Point p = Point.point(c, d, cdParam);
			double abParam = Point.u(a, p, b);
			if (DMath.greaterThanEquals(abParam, 0.0) && DMath.lessThanEquals(abParam, 1.0)) {
				assert DMath.equals(Point.distance(p, a, b), r + s.r);
				if (outside) {
					l.event(new SweepEvent(SweepEventType.ENTERCAPSULE, p, index, cdParam, this));
				} else {
					l.event(new SweepEvent(SweepEventType.EXITCAPSULE, p, index, cdParam, this));
				}
			}
		}
		
	}
	
	private void sweepACap(Stroke s, int index) {
		
		Point c = s.pts.get(index);
		Point d = s.pts.get(index+1);
		
		boolean outside;
		if (hitTest(c, s.r)) {
			outside = false;
		} else {
			outside = true;
		}
		
		/*
		 * handle a cap
		 */
		double aCoeff = ((d.x - c.x)*(d.x - c.x) + (d.y - c.y)*(d.y - c.y));
		double bCoeff = -2 * ((d.x - c.x)*(a.x - c.x) + (d.y - c.y)*(a.y - c.y));
		double cCoeff = ((a.x - c.x)*(a.x - c.x) + (a.y - c.y)*(a.y - c.y) - (s.r + r)*(s.r + r));
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
				if (DMath.lessThan(u, 0.0)) {
					assert DMath.equals(Point.distance(p, a, b), r + s.r);
					if (outside) {
						l.event(new SweepEvent(SweepEventType.ENTERCAPSULE, p, index, cdParam, this));
					} else {
						l.event(new SweepEvent(SweepEventType.EXITCAPSULE, p, index, cdParam, this));
					}
				}
			}

		} else if (discriminant > 0) {
			/*
			 * 2 events
			 */
			double cdParam0 = roots[0];
			if (DMath.greaterThanEquals(cdParam0, 0.0) && DMath.lessThanEquals(cdParam0, 1.0)) {
				Point p0 = Point.point(c, d, cdParam0);
				double u0 = Point.u(a, p0, b);
				if (DMath.lessThan(u0, 0.0)) {
					assert DMath.equals(Point.distance(p0, a, b), r + s.r);
					if (outside) {
						l.event(new SweepEvent(SweepEventType.ENTERCAPSULE, p0, index, cdParam0, this));
					} else {
						l.event(new SweepEvent(SweepEventType.EXITCAPSULE, p0, index, cdParam0, this));
					}
				}
			}
			double cdParam1 = roots[1];
			if (DMath.greaterThanEquals(cdParam1, 0.0) && DMath.lessThanEquals(cdParam1, 1.0)) {
				Point p1 = Point.point(c, d, cdParam1);
				double u1 = Point.u(a, p1, b);
				if (DMath.lessThan(u1, 0.0)) {
					assert DMath.equals(Point.distance(p1, a, b), r + s.r);
					if (outside) {
						l.event(new SweepEvent(SweepEventType.ENTERCAPSULE, p1, index, cdParam1, this));
					} else {
						l.event(new SweepEvent(SweepEventType.EXITCAPSULE, p1, index, cdParam1, this));
					}
				}
			}
			
		} else {
			/*
			 * 0 events
			 */
		}
		
	}
	
	private void sweepBCap(Stroke s, int index) {
		
		Point c = s.pts.get(index);
		Point d = s.pts.get(index+1);
		
		boolean outside;
		if (hitTest(c, s.r)) {
			outside = false;
		} else {
			outside = true;
		}
		
		/*
		 * handle b cap
		 */
		double aCoeff = ((d.x - c.x)*(d.x - c.x) + (d.y - c.y)*(d.y - c.y));
		double bCoeff = -2 * ((d.x - c.x)*(b.x - c.x) + (d.y - c.y)*(b.y - c.y));
		double cCoeff = ((b.x - c.x)*(b.x - c.x) + (b.y - c.y)*(b.y - c.y) - (s.r + r)*(s.r + r));
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
					assert DMath.equals(Point.distance(p, a, b), r + s.r);
					if (outside) {
						l.event(new SweepEvent(SweepEventType.ENTERCAPSULE, p, index, cdParam, this));
					} else {
						l.event(new SweepEvent(SweepEventType.EXITCAPSULE, p, index, cdParam, this));
					}
				}
			}
			
		} else if (discriminant > 0) {
			/*
			 * 2 events
			 */
			double cdParam0 = roots[0];
			if (DMath.greaterThanEquals(cdParam0, 0.0) && DMath.lessThanEquals(cdParam0, 1.0)) {
				Point p0 = Point.point(c, d, cdParam0);
				double u0 = Point.u(a, p0, b);
				if (DMath.greaterThan(u0, 1.0)) {
					assert DMath.equals(Point.distance(p0, a, b), r + s.r);
					if (outside) {
						l.event(new SweepEvent(SweepEventType.ENTERCAPSULE, p0, index, cdParam0, this));
					} else {
						l.event(new SweepEvent(SweepEventType.EXITCAPSULE, p0, index, cdParam0, this));
					}
				}
			}
			double cdParam1 = roots[1];
			if (DMath.greaterThanEquals(cdParam1, 0.0) && DMath.lessThanEquals(cdParam1, 1.0)) {
				Point p1 = Point.point(c, d, cdParam1);
				double u1 = Point.u(a, p1, b);
				if (DMath.greaterThan(u1, 1.0)) {
					assert DMath.equals(Point.distance(p1, a, b), r + s.r);
					if (outside) {
						l.event(new SweepEvent(SweepEventType.ENTERCAPSULE, p1, index, cdParam1, this));
					} else {
						l.event(new SweepEvent(SweepEventType.EXITCAPSULE, p1, index, cdParam1, this));
					}
				}
			}
			
		} else {
			/*
			 * 0 events
			 */
		}
		
	}
	
	public boolean hitTest(Point p) {
		if (aabb.hitTest(p)) {
			
			if (DMath.lessThanEquals(Point.distance(p, a, b), r)) {
				return true;
			}
			return false;
			
		} else {
			return false;
		}
	}
	
	public boolean hitTest(Point p, double radius) {
		if (DMath.lessThanEquals(Point.distance(p, a, b), r + radius)) {
			return true;
		}
		return false;
	}
	
	public double skeletonHitTest(Point p) {
		if (Point.intersect(p, a, b)){
			return Point.param(p, a, b);
		}
		return -1;
	}
	
	public void paint(Graphics2D g2) {
		
		int[] xPoints = new int[4];
		int[] yPoints = new int[4];
		
		xPoints[0] = (int)(aUp.x * MODEL.PIXELS_PER_METER);
		xPoints[1] = (int)(bUp.x * MODEL.PIXELS_PER_METER);
		xPoints[2] = (int)(bDown.x * MODEL.PIXELS_PER_METER);
		xPoints[3] = (int)(aDown.x * MODEL.PIXELS_PER_METER);
		yPoints[0] = (int)(aUp.y * MODEL.PIXELS_PER_METER);
		yPoints[1] = (int)(bUp.y * MODEL.PIXELS_PER_METER);
		yPoints[2] = (int)(bDown.y * MODEL.PIXELS_PER_METER);
		yPoints[3] = (int)(aDown.y * MODEL.PIXELS_PER_METER);
		
		g2.fillOval(
				(int)((a.x - r) * MODEL.PIXELS_PER_METER),
				(int)((a.y - r) * MODEL.PIXELS_PER_METER),
				(int)(2 * r * MODEL.PIXELS_PER_METER),
				(int)(2 * r * MODEL.PIXELS_PER_METER));
		g2.fillPolygon(xPoints, yPoints, 4);
		g2.fillOval(
				(int)((b.x - r) * MODEL.PIXELS_PER_METER),
				(int)((b.y - r) * MODEL.PIXELS_PER_METER),
				(int)(2 * r * MODEL.PIXELS_PER_METER),
				(int)(2 * r * MODEL.PIXELS_PER_METER));
		
		if (MODEL.DEBUG_DRAW) {
			
			Color c = g2.getColor();
			g2.setColor(Color.BLUE);
			
			g2.drawLine(
					(int)(a.x * MODEL.PIXELS_PER_METER),
					(int)(a.y * MODEL.PIXELS_PER_METER),
					(int)((a.x + n.x) * MODEL.PIXELS_PER_METER),
					(int)((a.y + n.y) * MODEL.PIXELS_PER_METER));
			
			g2.setColor(c);
			
		}
	}
	
	public void draw(Graphics2D g2) {
		
		int[] xPoints = new int[4];
		int[] yPoints = new int[4];
		
		xPoints[0] = (int)(aUp.x * MODEL.PIXELS_PER_METER);
		xPoints[1] = (int)(bUp.x * MODEL.PIXELS_PER_METER);
		xPoints[2] = (int)(bDown.x * MODEL.PIXELS_PER_METER);
		xPoints[3] = (int)(aDown.x * MODEL.PIXELS_PER_METER);
		yPoints[0] = (int)(aUp.y * MODEL.PIXELS_PER_METER);
		yPoints[1] = (int)(bUp.y * MODEL.PIXELS_PER_METER);
		yPoints[2] = (int)(bDown.y * MODEL.PIXELS_PER_METER);
		yPoints[3] = (int)(aDown.y * MODEL.PIXELS_PER_METER);
		
		g2.drawOval(
				(int)((a.x - r) * MODEL.PIXELS_PER_METER),
				(int)((a.y - r) * MODEL.PIXELS_PER_METER),
				(int)(2 * r * MODEL.PIXELS_PER_METER),
				(int)(2 * r * MODEL.PIXELS_PER_METER));
		g2.drawPolygon(xPoints, yPoints, 4);
		g2.drawOval(
				(int)((b.x - r) * MODEL.PIXELS_PER_METER),
				(int)((b.y - r) * MODEL.PIXELS_PER_METER),
				(int)(2 * r * MODEL.PIXELS_PER_METER),
				(int)(2 * r * MODEL.PIXELS_PER_METER));
		
	}
}
