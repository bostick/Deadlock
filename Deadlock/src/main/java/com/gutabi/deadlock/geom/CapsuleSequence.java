package com.gutabi.deadlock.geom;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class CapsuleSequence implements Shape, SweeperShape, CompoundShape {
	
	public final List<Capsule> caps;
	
	public final int capsuleCount;
	
	public final double radius;
	
	public final AABB aabb;
	
	public CapsuleSequence(List<Capsule> caps) {
		super();
		this.caps = caps;
		
		capsuleCount = caps.size();
		
		for (int i = 0; i < caps.size()-1; i++) {
			Capsule a = caps.get(i);
			Capsule b = caps.get(i+1);
			assert DMath.equals(a.r, b.r);
		}
		
		if (!caps.isEmpty()) {
			radius = caps.get(0).r;
		} else {
			radius = 0.0;
		}
		
		AABB aabbTmp = null;
		for (Capsule c : caps) {
			aabbTmp = AABB.union(aabbTmp, c.aabb);
		}
		aabb = aabbTmp;
	}
	
	public Capsule getCapsule(int index) {
		return caps.get(index);
	}
	
	public double getRadius() {
		return radius;
	}
	
	public Point getPoint(int index) {
		if (index == caps.size()) {
			return caps.get(index-1).b;
		} else {
			return caps.get(index).a;
		}
	}
	
	public Point getPoint(int index, double param) {
		if (index == caps.size()) {
			assert param == 0.0;
			return caps.get(index-1).b;
		} else {
			return caps.get(index).getPoint(param);
		}
	}
	
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
		return new CapsuleSequence(caps.subList(0, index));
	}
	
	/**
	 * single capsule
	 */
	public CapsuleSequence capseq(int index) {
		return new CapsuleSequence(caps.subList(index, index+1));
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	public CapsuleSequence plus(Point p) {
		List<Capsule> newCaps = new ArrayList<Capsule>();
		for (Capsule c : caps) {
			newCaps.add(c.plus(p));
		}
		return new CapsuleSequence(newCaps);
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
		for (int i = 0; i < capsuleCount(); i++) {
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
	
	public boolean intersect(Shape s) {
		
		for (Capsule c : caps) {
			if (ShapeUtils.intersect(c, s)) {
				return true;
			}
		}
		return false;
		
	}
	
	public boolean contains(Shape s) {
		assert false;
		return false;
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
