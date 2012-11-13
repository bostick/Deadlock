package com.gutabi.deadlock.core.geom;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;

@SuppressWarnings("static-access")
public class CapsuleSequence extends Shape {
	
	public final List<Capsule> caps;
	
	public final double radius;
	
	static Logger logger = Logger.getLogger(CapsuleSequence.class);
	
	public CapsuleSequence(Object parent, List<Capsule> caps) {
		super(parent);
		
		this.caps = caps;
		
		for (int i = 0; i < caps.size()-1; i++) {
			Capsule a = caps.get(i);
			Capsule b = caps.get(i+1);
			assert DMath.equals(a.r, b.r);
		}
		
		radius = caps.get(0).r;
		
		aabb = null;
		for (Capsule c : caps) {
			aabb = Rect.union(aabb, c.aabb);
		}
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
	
	public Point getPoint(int index, double param) {
		if (DMath.equals(param, 0.0)) {
			return getPoint(index);
		} else {
			return Point.point(getPoint(index), getPoint(index+1), param);
		}
	}
	
	public int capsuleCount() {
		return caps.size();
	}
	
	public int pointCount() {
		return caps.size()+1;
	}
	
	public CapsuleSequence plus(Point p) {
		List<Capsule> newCaps = new ArrayList<Capsule>();
		for (Capsule c : caps) {
			newCaps.add(c.plus(p));
		}
		return new CapsuleSequence(parent, newCaps);
	}
	
	public void sweepStart(Sweeper s) {
		for (Capsule c : caps) {
			c.sweepStart(s);
		}
	}
	
	public void sweep(Sweeper s, int index) {
		for (Capsule c : caps) {
			c.sweep(s, index);
		}
	}
	
//	public double distanceTo(Point p) {
//		double bestDist = Double.POSITIVE_INFINITY;
//		for (Capsule c : caps) {
//			double d = c.distanceTo(p);
//			if (DMath.lessThan(d, bestDist)) {
//				bestDist = d;
//			}
//		}
//		return bestDist;
//	}
	
	public boolean hitTest(Point p) {
		for (Capsule c : caps) {
			if (c.hitTest(p)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean intersect(Shape s) {
		logger.debug("testing intersecting of: " + s);
		for (Capsule c : caps) {
			logger.debug("testing intersect with " + c + ": ");
			if (c.intersect(s)) {
				logger.debug("yes");
				return true;
			} else {
				logger.debug("nope");
			}
		}
		return false;
	}
	
	public void paint(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		for (Capsule c : caps.subList(1, caps.size()-1)) {
			c.paint(g2);
		}
		
		g2.setTransform(origTransform);
	}
	
	public void draw(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		for (Capsule c : caps.subList(1, caps.size()-1)) {
			c.draw(g2);
		}
		
		g2.setTransform(origTransform);
	}
	
	public void drawSkeleton(Graphics2D g2) {
		
		for (Capsule c : caps) {
			c.drawSkeleton(g2);
		}
		
	}
	
}
