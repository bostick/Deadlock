package com.gutabi.deadlock.core.geom;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.SweepEvent.SweepEventType;

@SuppressWarnings("static-access")
public class Circle extends Shape {
	
	public final Point center;
	public final double radius;
	
	private final int hash;
	
	static Logger logger = Logger.getLogger(Circle.class);
	
	public Circle(Object parent, Point center, double radius) {
		super(parent);
		this.center = center;
		this.radius = radius;
		
		int h = 17;
		h = 37 * h + center.hashCode();
		long l = Double.doubleToLongBits(radius);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
		
		aabb = new Rect(center.x - radius, center.y - radius, 2*radius, 2*radius);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Shape)) {
			throw new IllegalArgumentException();
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
		return hash;
	}
	
	public Circle plus(Point p) {
		return new Circle(parent, center.plus(p), radius);
	}
	
	public boolean hitTest(Point p) {
		return DMath.lessThanEquals(Point.distance(p, center), radius);
	}
	
	public boolean intersect(Shape s) {
		
		if (s instanceof Quad) {
			Quad ss = (Quad)s;
			
			return ShapeUtils.intersect(ss, this);
			
		} else {
			Circle ss = (Circle)s;
			
			return DMath.lessThanEquals(Point.distance(center, ss.center), radius + ss.radius);
			
		}
		
	}
	
	public Point project(Point axis) {
		double cen = Point.dot(axis, center);
		return new Point(cen-radius, cen+radius);
	}
	
	public void sweepStart(Sweeper s) {
		
		Capsule cap = s.getCapsule(0);
		
		if (intersect(cap.ac)) {
			SweepEvent e = new SweepEvent(SweepEventType.ENTERCIRCLE, this, s, 0, 0.0);
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
					s.event(new SweepEvent(SweepEventType.ENTERCIRCLE, this, s, index, param));
				} else {
					s.event(new SweepEvent(SweepEventType.EXITCIRCLE, this, s, index, param));
				}
				outside = !outside;
			}
		}
		
	}
	
	public void paint(Graphics2D g2) {
		
		g2.fillOval(
				(int)((center.x - radius) * MODEL.PIXELS_PER_METER),
				(int)((center.y - radius) * MODEL.PIXELS_PER_METER),
				(int)((2 * radius) * MODEL.PIXELS_PER_METER),
				(int)((2 * radius) * MODEL.PIXELS_PER_METER));
		
	}
	
	public void draw(Graphics2D g2) {
		
		g2.drawOval(
				(int)((center.x - radius) * MODEL.PIXELS_PER_METER),
				(int)((center.y - radius) * MODEL.PIXELS_PER_METER),
				(int)((2 * radius) * MODEL.PIXELS_PER_METER),
				(int)((2 * radius) * MODEL.PIXELS_PER_METER));
		
	}
}
