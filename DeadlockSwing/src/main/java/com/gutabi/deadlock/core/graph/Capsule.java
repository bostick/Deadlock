package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.OverlappingException;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.core.graph.SweepEvent.SweepEventType;
import com.gutabi.deadlock.model.Stroke;
import com.gutabi.deadlock.model.SweepUtils;

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
	
//	SweepEventListener l;
	
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
	
//	public void setSweepEventListener(SweepEventListener l) {
//		this.l = l;
//	}
	
	public void sweepStart(Stroke s, SweepEventListener l) {
		
		Point c = s.pts.get(0);
		
		if (hitTest(c, s.r)) {
			l.start(new SweepEvent(SweepEventType.ENTERCAPSULE, s, 0, 0.0));
		}
		
	}
	
	public void sweep(Stroke s, int index, SweepEventListener l) {
		
		Point c = s.pts.get(index);
		Point d = s.pts.get(index+1);
		
		boolean outside;
		if (hitTest(c, s.r)) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = 0;
		
		/*
		 * a cap
		 */
		
		double[] capParams = new double[2];
		int n = SweepUtils.sweepCircleCap(b, a, c, d, s.r, r, capParams);
		
		for (int i = 0; i < n; i++) {
			params[paramCount] = capParams[i];
			paramCount++;
		}
		
		/*
		 * top side, left hand side of <a, b>
		 */
		
		double cdParam = SweepUtils.sweepCircleLine(aUp, bUp, c, d, s.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		/*
		 * b cap
		 */
		
		n = SweepUtils.sweepCircleCap(a, b, c, d, s.r, r, capParams);
		
		for (int i = 0; i < n; i++) {
			params[paramCount] = capParams[i];
			paramCount++;
		}
		
		/*
		 * bottom side
		 */
		
		cdParam = SweepUtils.sweepCircleLine(bDown, aDown, c, d, s.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		Arrays.sort(params);
		for (int i = 0; i < paramCount-1; i++) {
			double p0 = params[i];
			double p1 = params[i+1];
			assert !DMath.equals(p0, p1);
		}
		
		for (int i = 0; i < paramCount; i++) {
			if (outside) {
				l.event(new SweepEvent(SweepEventType.ENTERCAPSULE, s, index, params[i]));
			} else {
				l.event(new SweepEvent(SweepEventType.EXITCAPSULE, s, index, params[i]));
			}
			outside = !outside;
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
	
	public double findSkeletonIntersection(Point c, Point d) {
		try {
			Point inter = Point.intersection(a, b, c, d);
			if (inter != null) {
				return Point.param(inter, a, b);
			} else {
				return -1;
			}
		} catch (OverlappingException e) {
			assert false;
			return -1;
		}
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
