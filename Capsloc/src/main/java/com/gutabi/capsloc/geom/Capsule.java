package com.gutabi.capsloc.geom;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.math.DMath;
import com.gutabi.capsloc.math.OverlappingException;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.Cap;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.Join;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class Capsule {
	
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
	
	private GeometryPath path;
	
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
		
		path = APP.platform.createGeometryPath();
		
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
			
			middle = new OBB(Point.point(a, b, 0.5), Math.atan2(diff.y, diff.x), Point.distance(a, b)/2, r);
			
			path.add(ac);
			path.add(middle);
			path.add(bc);
			
			debugNormalLine = new Line(a, a.plus(n));
			debugSkeletonLine = new Line(a, b);
			
			MutableAABB aabbTmp = new MutableAABB();
			aabbTmp.union(ac.getAABB());
			aabbTmp.union(bc.getAABB());
			aabb = new AABB(aabbTmp.x, aabbTmp.y, aabbTmp.width, aabbTmp.height);
			
		} else {
			
			aUp = null;
			aDown = null;
			bUp = null;
			bDown = null;
			
			middle = null;
			
			path.add(ac);
			path.add(bc);
			
			debugNormalLine = null;
			debugSkeletonLine = new Line(a, b);
			
			MutableAABB aabbTmp = new MutableAABB();
			aabbTmp.union(ac.getAABB());
			aabbTmp.union(bc.getAABB());
			aabb = new AABB(aabbTmp.x, aabbTmp.y, aabbTmp.width, aabbTmp.height);
			
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
		
//		if (!ShapeUtils.intersectAA(aabb, ctxt.cam.worldViewport)) {
//			return;
//		}
		
		path.paint(ctxt);
		
		if (APP.DEBUG_DRAW) {
			Color c = ctxt.getColor();
			ctxt.setColor(Color.BLUE);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			
			debugNormalLine.draw(ctxt);
			
			ctxt.setColor(c);
		}
	}
	
//	public void paintA(RenderingContext ctxt) {
//		ac.paint(ctxt);
//	}
//	
//	public void paintB(RenderingContext ctxt) {
//		bc.paint(ctxt);
//	}
//	
//	public void paintMiddle(RenderingContext ctxt) {
//		if (middle != null) {
//			middle.paint(ctxt);
//		}
//	}
	
	public void draw(RenderingContext ctxt) {
		path.draw(ctxt);
	}
	
//	public void drawA(RenderingContext ctxt) {
//		ac.draw(ctxt);
//	}
//	
//	public void drawB(RenderingContext ctxt) {
//		bc.draw(ctxt);
//	}
//	
//	public void drawMiddle(RenderingContext ctxt) {
//		if (middle != null) {
//			middle.draw(ctxt);
//		}
//	}
	
	public void drawSkeleton(RenderingContext ctxt) {
		debugSkeletonLine.draw(ctxt);
	}
}
