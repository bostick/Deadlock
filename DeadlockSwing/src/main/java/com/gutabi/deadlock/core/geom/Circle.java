package com.gutabi.deadlock.core.geom;

import java.util.Arrays;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.SweepEvent.SweepEventType;

public class Circle extends Shape {
	
	public final Point center;
	public final double radius;
	
	public Circle(Object parent, Point center, double radius) {
		super(parent);
		this.center = center;
		this.radius = radius;
	}
	
	public boolean hitTest(Point p) {
		return DMath.lessThanEquals(Point.distance(p, center), radius);
	}
	
	public void sweepStart(Sweeper s) {
		
		Point c = s.get(0);
		
		if (bestHitTest(c, s.getRadius())) {
			SweepEvent e = new SweepEvent(SweepEventType.ENTERVERTEX, this, s, 0, 0.0);
			s.start(e);
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
		int paramCount = SweepUtils.sweepCircleCircle(center, c, d, s.getRadius(), radius, params);
		
		Arrays.sort(params);
		
		if (paramCount == 2) {
			String.class.getName();
		}
		
		for (int i = 0; i < paramCount; i++) {
			double param = params[i];
			assert DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0);
			if (DMath.lessThan(param, 1.0) || index == s.size()-1) {
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
