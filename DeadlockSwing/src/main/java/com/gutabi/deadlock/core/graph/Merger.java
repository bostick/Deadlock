package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.core.graph.MergerPosition.MergerDirection;
import com.gutabi.deadlock.core.graph.SweepEvent.SweepEventType;
import com.gutabi.deadlock.model.Stroke;

@SuppressWarnings("static-access")
public class Merger implements Entity, Edge, Sweepable {
	
	public static final double MERGER_WIDTH = 5.0;
	public static final double MERGER_HEIGHT = 5.0;
	
	public final Point ul;
	
	private Rect aabb;
	private Color color;
	private Color hiliteColor;
	
	public final MergerSink top;
	public final MergerSink left;
	public final MergerSource right;
	public final MergerSource bottom;
	
	private Point p0;
	private Point p1;
	private Point p2;
	private Point p3;
	
	public Merger(Point ul) {
		this.ul = ul;
		
		aabb = new Rect(ul.x, ul.y, MERGER_WIDTH, MERGER_HEIGHT);
		color = Color.BLUE;
		hiliteColor = Color.ORANGE;
		
		top = new MergerSink(new Point(ul.x + MERGER_WIDTH/2, ul.y));
		left = new MergerSink(new Point(ul.x, ul.y + MERGER_HEIGHT/2));
		right = new MergerSource(new Point(ul.x + MERGER_WIDTH, ul.y + MERGER_HEIGHT/2));
		bottom = new MergerSource(new Point(ul.x + MERGER_WIDTH/2, ul.y+MERGER_HEIGHT));
		
		top.matchingSource = bottom;
		bottom.matchingSink = top;
		
		left.matchingSource = right;
		right.matchingSink = left;
		
		p0 = ul;
		p1 = new Point(ul.x + MERGER_WIDTH, ul.y);
		p2 = new Point(ul.x + MERGER_WIDTH, ul.y + MERGER_HEIGHT);
		p3 = new Point(ul.x, ul.y + MERGER_HEIGHT);
		
	}
	
	public double getTotalLength(Vertex a, Vertex b) {
		if (a == top) {
			assert b == bottom;
			return MERGER_HEIGHT;
		} else if (a == left) {
			assert b == right;
			return MERGER_WIDTH;
		} else if (a == right) {
			assert b == left;
			return MERGER_WIDTH;
		} else {
			assert a == bottom;
			assert b== top;
			return MERGER_HEIGHT;
		}
	}
	
	
	public void sweepStart(Stroke s, SweepEventListener l) {
		
		Point c = s.pts.get(0);
		
		if (hitTest(c, s.r)) {
			l.start(new SweepEvent(SweepEventType.ENTERMERGER, this, s, 0, 0.0));
		}
		
	}
	
	public void sweepEnd(Stroke s, SweepEventListener l) {
		
		Point d = s.pts.get(s.pts.size()-1);
		
		if (hitTest(d, s.r)) {
			l.end(new SweepEvent(SweepEventType.EXITMERGER, this, s, s.pts.size()-1, 0.0));
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
		
		double cdParam = SweepUtils.sweepCircleLine(p0, p1, c, d, s.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p1, p2, c, d, s.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p2, p3, c, d, s.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p3, p0, c, d, s.r);
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
			if (DMath.lessThan(param, 1.0) || index == s.pts.size()-1) {
				if (outside) {
					l.event(new SweepEvent(SweepEventType.ENTERMERGER, this, s, index, param));
				} else {
					l.event(new SweepEvent(SweepEventType.EXITMERGER, this, s, index, param));
				}
				outside = !outside;
			}
		}
		
	}
	
	
	
	public GraphPosition travelFromConnectedVertex(Vertex v, double dist) {
		if (v == top) {
			assert DMath.lessThan(dist, MERGER_HEIGHT);
			
			return new MergerPosition(this, MergerDirection.TOPBOTTOM, dist / MERGER_HEIGHT);
			
		} else if (v == left) {
			assert DMath.lessThan(dist, MERGER_WIDTH);
			
			return new MergerPosition(this, MergerDirection.LEFTRIGHT, dist / MERGER_WIDTH);
			
		} else if (v == right) {
			assert DMath.lessThan(dist, MERGER_WIDTH);
			
			return new MergerPosition(this, MergerDirection.LEFTRIGHT, dist / MERGER_WIDTH);
			
		} else {
			assert v == bottom;
			assert DMath.lessThan(dist, MERGER_HEIGHT);
			
			return new MergerPosition(this, MergerDirection.TOPBOTTOM, dist / MERGER_HEIGHT);
			
		}
	}
	
	public boolean hitTest(Point p) {
		if (DMath.lessThanEquals(ul.x, p.x) && DMath.lessThanEquals(p.x, ul.x+MERGER_WIDTH) &&
				DMath.lessThanEquals(ul.y, p.y) && DMath.lessThanEquals(p.y, ul.y+MERGER_HEIGHT)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hitTest(Point p, double r) {
		if (hitTest(p)) {
			return true;
		} else {
			
//			if (DMath.equals(Point.distance(p, p0, p1), r)) {
//				String.class.getName();
//			} else if (DMath.equals(Point.distance(p, p1, p2), r)) {
//				String.class.getName();
//			} else if (DMath.equals(Point.distance(p, p2, p3), r)) {
//				String.class.getName();
//			} else if (DMath.equals(Point.distance(p, p3, p0), r)) {
//				String.class.getName();
//			}
			
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
	
	
	
	@Override
	public boolean isDeleteable() {
		return true;
	}
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		;
	}
	
	@Override
	public void preStep(double t) {
		;
	}

	@Override
	public boolean postStep(double t) {
		return true;
	}

	@Override
	public Rect getAABB() {
		return aabb;
	}
	
	@Override
	public void paint(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.setColor(color);
		
		g2.fillRect(
				(int)(ul.x * MODEL.PIXELS_PER_METER),
				(int)(ul.y * MODEL.PIXELS_PER_METER),
				(int)(MERGER_WIDTH * MODEL.PIXELS_PER_METER),
				(int)(MERGER_HEIGHT * MODEL.PIXELS_PER_METER));
		
		g2.setTransform(origTransform);
		
//		top.paint(g2);
//		left.paint(g2);
//		right.paint(g2);
//		bottom.paint(g2);
		
	}

	@Override
	public void paintHilite(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.setColor(hiliteColor);
		
		g2.fillRect(
				(int)(ul.x * MODEL.PIXELS_PER_METER),
				(int)(ul.y * MODEL.PIXELS_PER_METER),
				(int)(MERGER_WIDTH * MODEL.PIXELS_PER_METER),
				(int)(MERGER_HEIGHT * MODEL.PIXELS_PER_METER));
		
		g2.setTransform(origTransform);
		
	}
}
