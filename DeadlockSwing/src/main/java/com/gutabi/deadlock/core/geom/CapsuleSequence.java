package com.gutabi.deadlock.core.geom;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;

@SuppressWarnings("static-access")
public class CapsuleSequence extends Shape {
	
	public final List<Capsule> caps;
	
	public final double radius;
	
	public CapsuleSequence(Object parent, List<Capsule> caps) {
		super(parent);
		
		this.caps = caps;
		
		for (int i = 0; i < caps.size()-1; i++) {
			Capsule a = caps.get(i);
			Capsule b = caps.get(i+1);
			assert DMath.equals(a.r, b.r);
		}
		
		radius = caps.get(0).r;
	}
	
	public Capsule getCapsule(int index) {
		return caps.get(index);
	}
	
	public Point getPoint(int index) {
		if (index == caps.size()-1) {
			return caps.get(index).b;
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
	
	public boolean hitTest(Point p) {
		for (Capsule c : caps) {
			if (c.hitTest(p)) {
				return true;
			}
		}
		return false;
	}
	
	public void paint(Graphics2D g2) {
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		for (Capsule c : caps.subList(1, caps.size()-1)) {
			c.paint(g2);
		}
		
	}
	
	public void draw(Graphics2D g2) {
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		for (Capsule c : caps.subList(1, caps.size()-1)) {
			c.draw(g2);
		}
		
	}
	
	public void drawSkeleton(Graphics2D g2) {
		
		for (Capsule c : caps) {
			c.drawSkeleton(g2);
		}
		
	}
	
}
