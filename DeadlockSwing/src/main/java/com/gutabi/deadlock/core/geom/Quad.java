package com.gutabi.deadlock.core.geom;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;
import java.util.Arrays;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.SweepEvent.SweepEventType;

@SuppressWarnings("static-access")
public class Quad extends Shape {
	
	public final Point p0;
	public final Point p1;
	public final Point p2;
	public final Point p3;
	
	public Quad(Object parent, Point p0, Point p1, Point p2, Point p3) {
		super(parent);
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		
		double ulX = Math.min(Math.min(p0.x, p1.x), Math.min(p2.x, p3.x));
		double ulY = Math.min(Math.min(p0.y, p1.y), Math.min(p2.y, p3.y));
		double brX = Math.max(Math.max(p0.x, p1.x), Math.max(p2.x, p3.x));
		double brY = Math.max(Math.max(p0.y, p1.y), Math.max(p2.y, p3.y));
		
		aabb = new Rect(ulX, ulY, (brX - ulX), (brY - ulY));
	}
	
	public boolean hitTest(Point p) {
		
		if (Geom.halfPlane(p, p0, p1) == 1) {
			return false;
		}
		if (Geom.halfPlane(p, p1, p2) == 1) {
			return false;
		}
		if (Geom.halfPlane(p, p2, p3) == 1) {
			return false;
		}
		if (Geom.halfPlane(p, p3, p0) == 1) {
			return false;
		}
		return true;
		
	}
	
	public boolean intersect(Shape s) {
		return s.hitTest(p0) || s.hitTest(p1) || s.hitTest(p2) || s.hitTest(p3);
	}
	
	public void sweepStart(Sweeper s) {
		
		Capsule cap = s.getCapsule(0);
		
		if (intersect(cap.ac)) {
			s.start(new SweepEvent(SweepEventType.ENTERMERGER, this, s, 0, 0.0));
		}
		
	}
	
	public void sweep(Sweeper s, int index) {
		
		Capsule cap = s.getCapsule(index);
		Point c = cap.a;
		Point d = cap.b;
		
		boolean outside;
		if (intersect(cap.ac)) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = 0;
		
		double cdParam = SweepUtils.sweepCircleLine(p0, p1, c, d, cap.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p1, p2, c, d, cap.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p2, p3, c, d, cap.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p3, p0, c, d, cap.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		Arrays.sort(params);
		if (paramCount == 2 && DMath.equals(params[0], params[1])) {
			/*
			 * hit a seam
			 */
			paramCount = 1;
		}
		
		for (int i = 0; i < paramCount; i++) {
			double param = params[i];
			assert DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0);
			if (DMath.lessThan(param, 1.0) || index == s.pointCount()-1) {
				if (outside) {
					s.event(new SweepEvent(SweepEventType.ENTERMERGER, this, s, index, param));
				} else {
					s.event(new SweepEvent(SweepEventType.EXITMERGER, this, s, index, param));
				}
				outside = !outside;
			}
		}
		
	}
	
	
	
//	public double distanceTo(Point p) {
//		if (hitTest(p)) {
//			return 0.0;
//		}
//		double d0 = Point.distance(p, p0, p1);
//		double d1 = Point.distance(p, p1, p2);
//		double d2 = Point.distance(p, p2, p3);
//		double d3 = Point.distance(p, p3, p0);
//		return Math.min(Math.min(d0, d1), Math.min(d2, d3));
//	}
	
	public boolean containedIn(Quad q) {
		return hitTest(q.p0) && hitTest(q.p1) && hitTest(q.p2) && hitTest(q.p3);
	}
	
	public void paint(Graphics2D g2) {
		
		int[] xPoints = new int[4];
		int[] yPoints = new int[4];
		
		xPoints[0] = (int)(p0.x * MODEL.PIXELS_PER_METER);
		xPoints[1] = (int)(p1.x * MODEL.PIXELS_PER_METER);
		xPoints[2] = (int)(p2.x * MODEL.PIXELS_PER_METER);
		xPoints[3] = (int)(p3.x * MODEL.PIXELS_PER_METER);
		yPoints[0] = (int)(p0.y * MODEL.PIXELS_PER_METER);
		yPoints[1] = (int)(p1.y * MODEL.PIXELS_PER_METER);
		yPoints[2] = (int)(p2.y * MODEL.PIXELS_PER_METER);
		yPoints[3] = (int)(p3.y * MODEL.PIXELS_PER_METER);
		
		g2.fillPolygon(xPoints, yPoints, 4);
		
	}
}
