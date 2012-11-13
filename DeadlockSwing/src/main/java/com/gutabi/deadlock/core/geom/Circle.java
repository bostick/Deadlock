package com.gutabi.deadlock.core.geom;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.SweepEvent.SweepEventType;

public class Circle extends Shape {
	
	public final Point center;
	public final double radius;
	
	static Logger logger = Logger.getLogger(Circle.class);
	
	public Circle(Object parent, Point center, double radius) {
		super(parent);
		this.center = center;
		this.radius = radius;
		
		aabb = new Rect(center.x - radius, center.y - radius, 2*radius, 2*radius);
	}
	
	public String toString() {
		return "Circle(" + center + ", " + radius + ")";
	}
	
//	public double distanceTo(Point p) {
//		
//	}
	
	public boolean hitTest(Point p) {
		return DMath.lessThanEquals(Point.distance(p, center), radius);
	}
	
	public boolean intersect(Shape s) {
		
		if (s instanceof Quad) {
			Quad ss = (Quad)s;
			
			
			
		} else {
			Circle ss = (Circle)s;
			
			return DMath.lessThanEquals(Point.distance(center, ss.center), radius + ss.radius);
			
		}
		
	}
	
	public void sweepStart(Sweeper s) {
		
		Capsule cap = s.getCapsule(0);
		
		if (intersect(cap.ac)) {
			SweepEvent e = new SweepEvent(SweepEventType.ENTERVERTEX, this, s, 0, 0.0);
			s.start(e);
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
		int paramCount = SweepUtils.sweepCircleCircle(center, c, d, cap.r, radius, params);
		
		Arrays.sort(params);
		
		if (paramCount == 2) {
			String.class.getName();
		}
		
		for (int i = 0; i < paramCount; i++) {
			double param = params[i];
			assert DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0);
			if (DMath.lessThan(param, 1.0) || index == s.pointCount()-1) {
				if (outside) {
					s.event(new SweepEvent(SweepEventType.ENTERVERTEX, this, s, index, param));
				} else {
					s.event(new SweepEvent(SweepEventType.EXITVERTEX, this, s, index, param));
				}
				outside = !outside;
			}
		}
		
	}

}
