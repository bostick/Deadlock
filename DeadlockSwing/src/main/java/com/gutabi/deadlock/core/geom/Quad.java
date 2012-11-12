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
	
	public boolean bestHitTest(Point p, double r) {
		if (hitTest(p)) {
			
			return true;
			
		} else {
			
			if (DMath.lessThanEquals(Point.distance(p, p0, p1), r)) {
				return true;
			} else if (DMath.lessThanEquals(Point.distance(p, p1, p2), r)) {
				return true;
			} else if (DMath.lessThanEquals(Point.distance(p, p2, p3), r)) {
				return true;
			} else if (DMath.lessThanEquals(Point.distance(p, p3, p0), r)) {
				return true;
			}
			return false;
			
		}
	}
	
	public void sweepStart(Sweeper s) {
		
		Point c = s.get(0);
		
		if (bestHitTest(c, s.getRadius())) {
			s.start(new SweepEvent(SweepEventType.ENTERMERGER, this, s, 0, 0.0));
		}
		
	}
	
	public void sweep(Sweeper s, int index) {
		
		Point c = s.get(index);
		Point d = s.get(index+1);
		
		boolean outside;
		if (bestHitTest(c, s.getRadius())) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = 0;
		
		double cdParam = SweepUtils.sweepCircleLine(p0, p1, c, d, s.getRadius());
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p1, p2, c, d, s.getRadius());
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p2, p3, c, d, s.getRadius());
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p3, p0, c, d, s.getRadius());
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
			if (DMath.lessThan(param, 1.0) || index == s.size()-1) {
				if (outside) {
					s.event(new SweepEvent(SweepEventType.ENTERMERGER, this, s, index, param));
				} else {
					s.event(new SweepEvent(SweepEventType.EXITMERGER, this, s, index, param));
				}
				outside = !outside;
			}
		}
		
	}
	
	
	
	public double distanceTo(Point p) {
		if (hitTest(p)) {
			return 0.0;
		}
		double d0 = Point.distance(p, p0, p1);
		double d1 = Point.distance(p, p1, p2);
		double d2 = Point.distance(p, p2, p3);
		double d3 = Point.distance(p, p3, p0);
		return Math.min(Math.min(d0, d1), Math.min(d2, d3));
	}
	
	public boolean containedIn(Quad q) {
		
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
