package com.gutabi.deadlock.core.geom;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class CapsuleSequence extends SweepableShape {
	
	public final List<Capsule> caps;
	
	public final int capsuleCount;
	
	public final double radius;
	
	public final AABB aabb;
	
	static Logger logger = Logger.getLogger(CapsuleSequence.class);
	
	public CapsuleSequence(Object parent, List<Capsule> caps) {
		super(parent);
		this.caps = caps;
		
		capsuleCount = caps.size();
		
		for (int i = 0; i < caps.size()-1; i++) {
			Capsule a = caps.get(i);
			Capsule b = caps.get(i+1);
			assert DMath.equals(a.r, b.r);
		}
		
		radius = caps.get(0).r;
		
		AABB aabbTmp = null;
		for (Capsule c : caps) {
			aabbTmp = AABB.union(aabbTmp, c.aabb);
		}
		aabb = aabbTmp;
	}
	
	public Capsule getCapsule(int index) {
		return caps.get(index);
	}
	
	public Point getPoint(int index) {
		if (index == caps.size()) {
			return caps.get(index-1).b;
		} else {
			return caps.get(index).a;
		}
	}
	
//	public Point getPoint(int index, double param) {
//		if (DMath.equals(param, 0.0)) {
//			return getPoint(index);
//		} else {
//			return Point.point(getPoint(index), getPoint(index+1), param);
//		}
//	}
	
	public int capsuleCount() {
		return capsuleCount;
	}
	
	public int pointCount() {
		return capsuleCount+1;
	}
	
	public Circle getStart() {
		return caps.get(0).ac;
	}
	
	/**
	 * @param index (exclusive)
	 */
	public CapsuleSequence subsequence(int index) {
		return new CapsuleSequence(parent, caps.subList(0, index));
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	public CapsuleSequence plus(Point p) {
		List<Capsule> newCaps = new ArrayList<Capsule>();
		for (Capsule c : caps) {
			newCaps.add(c.plus(p));
		}
		return new CapsuleSequence(parent, newCaps);
	}
	
	public List<SweepEvent> sweepStart(Circle s) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		for (Capsule c : caps) {
			events.addAll(c.sweepStart(s));
		}
		
		return events;
	}
	
	public List<SweepEvent> sweep(Capsule s) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		for (int i = 0; i < caps.size(); i++) {
			Capsule c = caps.get(i);
			
			List<SweepEvent> capsuleEvents = c.sweep(s);
			
			for (SweepEvent e : capsuleEvents) {
				if (DMath.lessThan(e.param, 1.0)) {
					
					events.add(e);
					
				} else {
					assert DMath.equals(e.param, 1.0);
					if (i < caps.size()-1) {
						
//						events.add(new SweepEvent(e.type, e.shape, caps.get(e.index+1), e.index+1, 0.0));
						events.add(e);
						
					} else {
						events.add(e);
					}
				}
				
			}
//			events.addAll(capsuleEvents);
			
		}
		
		return events;
	}
	
	public boolean hitTest(Point p) {
		for (Capsule c : caps) {
			if (c.hitTest(p)) {
				return true;
			}
		}
		return false;
	}
	
	public CapsuleSequencePosition findSkeletonIntersection(Point c, Point d) {
		for (int i = 0; i < capsuleCount(); i ++) {
			Capsule cap = getCapsule(i);
			double abParam = cap.findSkeletonIntersection(c, d);
			if (abParam != -1) {
				if (DMath.equals(abParam, 1.0) && i < capsuleCount()-1) {
					return new CapsuleSequencePosition(this, i+1, 0.0);
				} else {
					return new CapsuleSequencePosition(this, i, abParam);
				}
			}
		}
		return null;
	}
	
	public CapsuleSequencePosition findClosestStrokePosition(Point p, double r) {
		int bestIndex = -1;
		double bestParam = -1;
		Point bestPoint = null;
		double bestDist = Double.POSITIVE_INFINITY;
		
		for (int i = 0; i < capsuleCount(); i++) {
			Capsule c = getCapsule(i);
			double closest = closestParam(p, c);
			Point ep = Point.point(c.a, c.b, closest);
			double dist = Point.distance(p, ep);
			if (DMath.lessThanEquals(dist, radius + r)) {
				if (bestPoint == null) {
					bestIndex = i;
					bestParam = closest;
					bestPoint = ep;
					bestDist = dist;
				} else if (dist < bestDist) {
					bestIndex = i;
					bestParam = closest;
					bestPoint = ep;
					bestDist = dist;
				}
			}
		}
		
		if (bestPoint != null) {
			if (bestParam == 1.0) {
				if (bestIndex == capsuleCount()-1) {
					return new CapsuleSequencePosition(this, bestIndex, 1.0);
				} else {
					return new CapsuleSequencePosition(this, bestIndex+1, 0.0);
				}
			} else {
				return new CapsuleSequencePosition(this, bestIndex, bestParam);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * find closest position on <c, d> to the point b
	 */
	private double closestParam(Point p, Capsule c) {
		if (p.equals(c.a)) {
			return 0.0;
		}
		if (p.equals(c.b)) {
			return 1.0;
		}

		double u = Point.u(c.a, p, c.b);
		if (DMath.lessThanEquals(u, 0.0)) {
			return 0.0;
		} else if (DMath.greaterThanEquals(u, 1.0)) {
			return 1.0;
		} else {
			return u;
		}
	}
	
	
	
	public java.awt.Shape java2D() {
		assert false;
		return null;
	}
	
	public void paint(RenderingContext ctxt) {
		for (Capsule c : caps) {
			c.paint(ctxt);
		}
	}
	
	public void draw(RenderingContext ctxt) {
		for (Capsule c : caps) {
			c.drawA(ctxt);
			c.drawMiddle(ctxt);
		}
		caps.get(caps.size()-1).drawB(ctxt);
	}
	
	public void drawSkeleton(RenderingContext ctxt) {
		for (Capsule c : caps) {
			c.drawSkeleton(ctxt);
		}
	}
	
}
