package com.gutabi.deadlock.geom;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Dim;
import com.gutabi.deadlock.math.Point;

public abstract class AABB extends SweepableShape {
	
	public final Point ul;
	public final Dim dim;
	public final double x;
	public final double y;
	public final double width;
	public final double height;
	
	public final Point br;
	public final double brX;
	public final double brY;
	
	public final Point center;
	
	public final Point p0;
	public final Point p1;
	public final Point p2;
	public final Point p3;
	
	public static final Point UP = new Point(0, -1);
	public static final Point LEFT = new Point(1, 0);
	
	public final Point n01 = UP;
	public final Point n12 = LEFT;
	
	double[] n01Projection;
	double[] n12Projection;
	
	public AABB(Object parent, double x, double y, double width, double height) {
		super(parent);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		assert width >= 0;
		assert height >= 0;
		
		ul = new Point(x, y);
		dim = new Dim(width, height);
		
		brX = x + width;
		brY = y + height;
		br = new Point(brX, brY);
		
		center = new Point(ul.x + width/2, ul.y + height/2);
		
		p0 = ul;
		p1 = new Point(ul.x + width, ul.y);
		p2 = new Point(brX, brY);
		p3 = new Point(ul.x, ul.y + height);
		
	}
	
	public String toString() {
		return "[" + x + ", " + y + ", " + width + ", " + height + "]";
	}
	
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof AABB)) {
			return false;
		} else {
			AABB b = (AABB)o;
			return DMath.equals(x, b.x) && DMath.equals(y, b.y) && DMath.equals(width, b.width) && DMath.equals(height, b.height);
		}
	}
	
	public AABB getAABB() {
		return this;
	}
	
	public void project(Point axis, double[] out) {
		double min = Point.dot(axis, p0);
		double max = min;
		
		double p = Point.dot(axis, p1);
		if (p < min) {
			min = p;
		} else if (p > max) {
			max = p;
		}
		
		p = Point.dot(axis, p2);
		if (p < min) {
			min = p;
		} else if (p > max) {
			max = p;
		}
		
		p = Point.dot(axis, p3);
		if (p < min) {
			min = p;
		} else if (p > max) {
			max = p;
		}
		
		out[0] = min;
		out[1] = max;
	}
	
	private void computeProjections() {
		
		n01Projection = new double[2];
		project(n01, n01Projection);
		
		n12Projection = new double[2];
		project(n12, n12Projection);
	}
	
	public Point getN01() {
		return n01;
	}
	
	public Point getN12() {
		return n12;
	}
	
	public void projectN01(double[] out) {
		if (n01Projection == null) {
			computeProjections();
		}
		out[0] = n01Projection[0];
		out[1] = n01Projection[1];
	}
	
	public void projectN12(double[] out) {
		if (n12Projection == null) {
			computeProjections();
		}
		out[0] = n12Projection[0];
		out[1] = n12Projection[1];
	}
	
	
	public boolean hitTest(Point p) {
		return DMath.lessThanEquals(x, p.x) && DMath.lessThanEquals(p.x, brX) &&
				DMath.lessThanEquals(y, p.y) && DMath.lessThanEquals(p.y, brY);
	}
	
	public boolean completelyWithin(AABB par) {
		return DMath.lessThanEquals(par.x, x) && DMath.lessThanEquals(brX, par.brX) &&
				DMath.lessThanEquals(par.y, y) && DMath.lessThanEquals(brY, par.brY);
	}
	
	public static final AABB union(AABB a, AABB b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		
		boolean isA = true;
		boolean isB = true;
		
		double ulX = a.ul.x;
		double ulY = a.ul.y;
		double brX = a.brX;
		double brY = a.brY;
		
		if (b.ul.x < ulX) {
			ulX = b.ul.x;
			isA = false;
		} else {
			isB = false;
		}
		if (b.ul.y < ulY) {
			ulY = b.ul.y;
			isA = false;
		} else {
			isB = false;
		}
		if (b.brX > brX) {
			brX = b.brX;
			isA = false;
		} else {
			isB = false;
		}
		if (b.brY > brY) {
			brY = b.brY;
			isA = false;
		} else {
			isB = false;
		}
		
		if (isA) {
			return a;
		}
		if (isB) {
			return b;
		}
		
		return APP.platform.createShapeEngine().createAABB(null, ulX, ulY, brX-ulX, brY-ulY);
	}
	
	public AABB plus(Point p) {
		return APP.platform.createShapeEngine().createAABB(parent, x + p.x, y + p.y, width, height);
	}
	
	public AABB minus(Point p) {
		return APP.platform.createShapeEngine().createAABB(parent, x - p.x, y - p.y, width, height);
	}
	
	
	
	
	public List<SweepEvent> sweepStart(Circle s) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		if (ShapeUtils.intersectAC(this, s)) {
			events.add(new SweepEvent(SweepEventType.enter(parent), this, s, 0, 0.0));
		}
		
		return events;
		
	}
	
	public List<SweepEvent> sweep(Capsule s) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		Point c = s.a;
		Point d = s.b;
		
		boolean outside;
		if (ShapeUtils.intersectAC(this, s.ac)) {
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
			
			if (DMath.greaterThan(param, 0.0)) {
				
				assert DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0);
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
