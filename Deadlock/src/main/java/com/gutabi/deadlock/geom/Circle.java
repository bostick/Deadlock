package com.gutabi.deadlock.geom;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;

public abstract class Circle extends SweepableShape implements SweeperShape {
	
	public final Point center;
	public final double radius;
	
	public final AABB aabb;
	
	private int hash;
	
//	static Logger logger = Logger.getLogger(Circle.class);
	
	public Circle(Object parent, Point center, double radius) {
		super(parent);
		this.center = center;
		this.radius = radius;
		
		aabb = APP.platform.createShapeEngine().createAABB(parent, center.x - radius, center.y - radius, 2*radius, 2*radius);
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Circle)) {
			return false;
		} else {
			Circle b = (Circle)o;
			return center.equals(b.center) && DMath.equals(radius, b.radius);
		}
	}
	
	public String toString() {
		return "Circle(" + center + ", " + radius + ")";
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + center.hashCode();
			long l = Double.doubleToLongBits(radius);
			int c = (int)(l ^ (l >>> 32));
			h = 37 * h + c;
			hash = h;
		}
		return hash;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public Circle plus(Point p) {
		return APP.platform.createShapeEngine().createCircle(parent, center.plus(p), radius);
	}
	
	public boolean hitTest(Point p) {
		return DMath.lessThanEquals(Point.distance(p, center), radius);
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	public void project(Point axis, double[] out) {
		double cen = Point.dot(axis, center);
		
		out[0] = cen-radius;
		out[1] = cen+radius;
	}
	
	public Point getPoint(double param) {
		assert DMath.equals(param, 0.0);
		return center;
	}
	
	public List<SweepEvent> sweepStart(Circle s) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		if (ShapeUtils.intersectCC(this, s)) {
			events.add(new SweepEvent(SweepEventType.enter(parent), this, s, 0, 0.0));
		}
		
		return events;
	}
	
	public List<SweepEvent> sweep(Capsule s) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		Point c = s.a;
		Point d = s.b;
		
		boolean outside;
		if (ShapeUtils.intersectCC(this, s.ac)) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = SweepUtils.sweepCircleCircle(center, c, d, s.r, radius, params);
		
		Arrays.sort(params);
		
		for (int i = 0; i < paramCount; i++) {
			double param = params[i];
			
			if (DMath.greaterThan(param, 0.0)) {
				
				assert DMath.greaterThan(param, 0.0) && DMath.lessThanEquals(param, 1.0);
				if (outside) {
					events.add(new SweepEvent(SweepEventType.enter(parent), this, s, s.index, param));
				} else {
					events.add(new SweepEvent(SweepEventType.exit(parent), this, s, s.index, param));
				}
				outside = !outside;
				
			}
			
		}
		
		return events;
	}
	
}
