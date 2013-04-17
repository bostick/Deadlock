package com.gutabi.deadlock.geom;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.OverlappingException;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class Capsule implements Shape {
	
	public final Circle ac;
	public final Circle bc;
	
	public final OBB middle;
	
	public final Point a;
	public final Point b;
	public final double r;
	
	public final Point aUp;
	public final Point aDown;
	public final Point bUp;
	public final Point bDown;
	
	private final Line debugNormalLine;
	private final Line debugSkeletonLine;
	
	public final AABB aabb;
	
	private Line abUp;
	private Line baDown;
	
	private int hash;
	
	public Capsule(Circle ac, Circle bc) {
		this.ac = ac;
		this.bc = bc;
		
		this.a = ac.center;
		this.b = bc.center;
		this.r = ac.radius;
		
		if (!DMath.equals(ac.radius, bc.radius)) {
			throw new IllegalArgumentException("radii not equal");
		}
		
		if (!a.equals(b)) {
			
			Point diff = new Point(b.x - a.x, b.y - a.y);
			Point n = Point.ccw90AndNormalize(diff);
			Point nd = Point.cw90AndNormalize(diff);
			
			Point u = n.multiply(r);
			Point d = nd.multiply(r);
			aUp = a.plus(u);
			aDown = a.plus(d);
			bUp = b.plus(u);
			bDown = b.plus(d);
			
			middle = APP.platform.createOBB(Point.point(a, b, 0.5), Math.atan2(diff.y, diff.x), Point.distance(a, b)/2, r);
			
			debugNormalLine = new Line(a, a.plus(n));
			debugSkeletonLine = new Line(a, b);
			
			aabb = AABB.union(ac.getAABB(), bc.getAABB());
			
		} else {
			
			aUp = null;
			aDown = null;
			bUp = null;
			bDown = null;
			
			middle = null;
			
			debugNormalLine = null;
			debugSkeletonLine = new Line(a, b);
			
			aabb = AABB.union(ac.getAABB(), bc.getAABB());
			
		}
		
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + ac.hashCode();
			h = 37 * h + bc.hashCode();
			hash = h;
		}
		return hash;
	}
	
	public String toString() {
		return "(" + a + " " + b + ")";
	}
	
	public double getRadius() {
		return r;
	}
	
	public Capsule plus(Point p) {
		return new Capsule(new Circle(a.plus(p), ac.radius), new Circle(b.plus(p), bc.radius));
	}
	
	public boolean hitTest(Point p) {
		if (ac.hitTest(p)) {
			return true;
		}
		if (bc.hitTest(p)) {
			return true;
		}
		if (middle.hitTest(p)) {
			return true;
		}
		return false;
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	public Point getPoint(double param) {
		return Point.point(a, b, param);
	}
	
	public double skeletonHitTest(Point p) {
		if (Point.intersect(p, a, b)){
			return Point.param(p, a, b);
		}
		return -1;
	}
	
	public double findSkeletonIntersection(Point c, Point d) {
		try {
			Point inter = Point.intersection(a, b, c, d);
			if (inter != null) {
				return Point.param(inter, a, b);
			} else {
				return -1;
			}
		} catch (OverlappingException e) {
			assert false;
			return -1;
		}
	}
	
	public static boolean contains(Capsule c0, Capsule c1) {
		
		if (DMath.greaterThan(c1.r, c0.r)) {
			return false;
		}
		
		double dist = Math.min(Point.distance(c1.a, c0.a, c0.b), Point.distance(c1.b, c0.a, c0.b));
		
		return DMath.lessThanEquals(dist, c0.r - c1.r);
	}
	
	public Line getABUp() {
		if (abUp == null) {
			abUp = new Line(aUp, bUp);
		}
		return abUp;
	}
	
	public Line getBADown() {
		if (baDown == null) {
			baDown = new Line(bDown, aDown);
		}
		return baDown;
	}
	
	public void paint(RenderingContext ctxt) {
		
		ac.paint(ctxt);
		if (middle != null) {
			middle.paint(ctxt);
		}
		bc.paint(ctxt);
		
		if (APP.DEBUG_DRAW) {
			Color c = ctxt.getColor();
			ctxt.setColor(Color.BLUE);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			
			debugNormalLine.draw(ctxt);
			
			ctxt.setColor(c);
		}
	}
	
	public void draw(RenderingContext ctxt) {
		ac.draw(ctxt);
		if (middle != null) {
			middle.draw(ctxt);
		}
		bc.draw(ctxt);
	}
	
	public void drawA(RenderingContext ctxt) {
		ac.draw(ctxt);
	}
	
	public void drawB(RenderingContext ctxt) {
		bc.draw(ctxt);
	}
	
	public void drawMiddle(RenderingContext ctxt) {
		if (middle != null) {
			middle.draw(ctxt);
		}
	}
	
	public void drawSkeleton(RenderingContext ctxt) {
		
		debugSkeletonLine.draw(ctxt);
		
	}
}
