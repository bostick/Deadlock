package com.gutabi.deadlock.core.geom;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.OverlappingException;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;

@SuppressWarnings("static-access")
public class Capsule extends SweepableShape {
	
	public final Circle ac;
	public final Circle bc;
	
	public final Quad middle;
	
	public final Point a;
	public final Point b;
	public final double r;
	
	/*
	 * normal
	 */
	private final Point n;
	
	private final Point aUp;
	private final Point aDown;
	private final Point bUp;
	private final Point bDown;
	
	private final Line debugNormalLine;
	private final Line debugSkeletonLine;
	
	public final AABB aabb;
	
	private int hash;
	
	static Logger logger = Logger.getLogger(Capsule.class);
	
	public Capsule(Object parent, Circle ac, Circle bc) {
		super(parent);
		this.ac = ac;
		this.bc = bc;
		
		this.a = ac.center;
		this.b = bc.center;
		this.r = ac.radius;
		
		if (a.equals(b)) {
			throw new IllegalArgumentException("a equals b");
		}
		
		if (!DMath.equals(ac.radius, bc.radius)) {
			throw new IllegalArgumentException("radii not equal");
		}
		
		Point diff = new Point(b.x - a.x, b.y - a.y);
		n = Point.ccw90AndNormalize(diff);
		Point nd = Point.cw90AndNormalize(diff);
		
		Point u = n.multiply(r);
		Point d = nd.multiply(r);
		aUp = a.plus(u);
		aDown = a.plus(d);
		bUp = b.plus(u);
		bDown = b.plus(d);
		
		middle = new Quad(parent, aUp, bUp, bDown, aDown);
		
		debugNormalLine = new Line(a.x, a.y, a.x+n.x, a.y+n.y);
		debugSkeletonLine = new Line(a.x, a.y, b.x, b.y);
		
		aabb = AABB.union(ac.getAABB(), bc.getAABB());
		
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
	
	public Capsule plus(Point p) {
		return new Capsule(parent, new Circle(parent, a.plus(p), ac.radius), new Circle(parent, b.plus(p), bc.radius));
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
	
	public void sweepStart(Sweeper s) {
		
		Capsule firstCap = s.getCapsule(0);
		
		if (ShapeUtils.intersect(this, firstCap.ac)) {
			s.start(new SweepEvent(SweepEventType.enter(parent), this, s, 0, 0.0));
		}
		
	}
	
	public void sweep(Sweeper s, int index) {
		
		Capsule cap = s.getCapsule(index);
		Point c = cap.a;
		Point d = cap.b;
		
		if ((a.equals(new Point(8.09375, 7.65625)) && b.equals(new Point(8.53125, 7.71875))) && index == 12) {
			String.class.getName();
		}
		
		boolean outside;
		if (ShapeUtils.intersect(this, cap.ac)) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = 0;
		
		/*
		 * a cap
		 */
		
		double[] capParams = new double[2];
		int n = SweepUtils.sweepCircleCircle(a, c, d, cap.r, r, capParams);
		
		for (int i = 0; i < n; i++) {
			
			double cdParam = capParams[i];
			
			/*
			 * still have to test that it is beyond the end points
			 */
			Point p = Point.point(c, d, cdParam);
			double abParam = Point.u(a, p, b);
			
			if (DMath.lessThanEquals(abParam, 0.0)) {
				
				assert ShapeUtils.intersect(ac, new Circle(null, p, cap.r));
				
				logger.debug("a cap");
				
				params[paramCount] = cdParam;
				paramCount++;
			}
			
		}
		
		/*
		 * top side, left hand side of <a, b>
		 */
		
		double cdParam = SweepUtils.sweepCircleLine(aUp, bUp, c, d, cap.r);
		
		if (cdParam != -1) {
			
			/*
			 * still have to test that it isn't beyond the end points
			 */
			Point p = Point.point(c, d, cdParam);
			double abParam = Point.u(aUp, p, bUp);
			
			if (DMath.greaterThanEquals(abParam, 0.0) && DMath.lessThanEquals(abParam, 1.0)) {
				
				logger.debug("top side");
				
				params[paramCount] = cdParam;
				paramCount++;
			}
			
		}
		
		/*
		 * b cap
		 */
		
		n = SweepUtils.sweepCircleCircle(b, c, d, cap.r, r, capParams);
		
		for (int i = 0; i < n; i++) {
			
			cdParam = capParams[i];
			
			/*
			 * still have to test that it is beyond the end points
			 */
			Point p = Point.point(c, d, cdParam);
			double abParam = Point.u(a, p, b);
			
			if (DMath.greaterThanEquals(abParam, 1.0)) {
				
				assert ShapeUtils.intersect(bc, new Circle(null, p, cap.r));
				
				logger.debug("b cap");
				
				params[paramCount] = cdParam;
				paramCount++;
			}
			
		}
		
		/*
		 * bottom side
		 */
		
		cdParam = SweepUtils.sweepCircleLine(bDown, aDown, c, d, cap.r);
		
		if (cdParam != -1) {
//			logger.debug("bottom side: " + 1);
		}
		
		if (cdParam != -1) {
			
			/*
			 * still have to test that it isn't beyond the end points
			 */
			Point p = Point.point(c, d, cdParam);
			double abParam = Point.u(bDown, p, aDown);
			
			if (DMath.greaterThanEquals(abParam, 0.0) && DMath.lessThanEquals(abParam, 1.0)) {
				
				logger.debug("bottom side");
				
				params[paramCount] = cdParam;
				paramCount++;
			}
			
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
			assert DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0);
			
			if (DMath.lessThan(param, 1.0) || index == s.pointCount()-1) {
				if (outside) {
					s.event(new SweepEvent(SweepEventType.enter(parent), this, s, index, param));
				} else {
					s.event(new SweepEvent(SweepEventType.exit(parent), this, s, index, param));
				}
				outside = !outside;
			}
		}
		
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
	
	public void paint(RenderingContext ctxt) {
		
		ac.paint(ctxt);
		middle.paint(ctxt);
		bc.paint(ctxt);
		
		if (ctxt.type == RenderingContextType.CANVAS) {
			if (MODEL.DEBUG_DRAW) {
				
				Color c = ctxt.getColor();
				ctxt.setColor(Color.BLUE);
				ctxt.setPixelStroke();
				
				debugNormalLine.draw(ctxt);
				
				ctxt.setColor(c);
				
			}
		}
		
	}
	
	public void draw(RenderingContext ctxt) {
		ac.draw(ctxt);
		middle.draw(ctxt);
		bc.draw(ctxt);
	}
	
	public void drawSkeleton(RenderingContext ctxt) {
		
		debugSkeletonLine.draw(ctxt);
		
	}
}
