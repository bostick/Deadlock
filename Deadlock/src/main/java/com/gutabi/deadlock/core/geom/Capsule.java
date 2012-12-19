package com.gutabi.deadlock.core.geom;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.OverlappingException;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.WorldCamera;

//@SuppressWarnings("static-access")
public class Capsule extends SweepableShape implements SweeperShape {
	
//	public final WorldScreen screen;
	WorldCamera cam;
	
	public final Circle ac;
	public final Circle bc;
	
	public final int index;
	
	public final Quad middle;
	
	public final Point a;
	public final Point b;
	public final double r;
	
	private final Point aUp;
	private final Point aDown;
	private final Point bUp;
	private final Point bDown;
	
	private final Line debugNormalLine;
	private final Line debugSkeletonLine;
	
	public final AABB aabb;
	
	private int hash;
	
	static Logger logger = Logger.getLogger(Capsule.class);
	
	public Capsule(WorldCamera cam, Object parent, Circle ac, Circle bc, int index) {
		super(parent);
//		this.screen = screen;
		this.ac = ac;
		this.bc = bc;
		this.index = index;
		
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
			
			middle = new Quad(parent, aUp, bUp, bDown, aDown);
			
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
		return new Capsule(cam, parent, new Circle(parent, a.plus(p), ac.radius), new Circle(parent, b.plus(p), bc.radius), -1);
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
	
	public List<SweepEvent> sweepStart(Circle c) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		if (ShapeUtils.intersectCCap(c, this)) {
			events.add(new SweepEvent(SweepEventType.enter(parent), this, c, 0, 0.0));
		}
		
		return events;
	}
	
	public List<SweepEvent> sweep(Capsule cap) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		Point c = cap.a;
		Point d = cap.b;
		
		boolean outside;
		if (ShapeUtils.intersectCCap(cap.ac, this)) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = 0;
		
		double cdParam;
		
		/*
		 * a cap
		 */
		
		double[] capParams = new double[2];
		int n = SweepUtils.sweepCircleCircle(a, c, d, cap.r, r, capParams);
		
		for (int i = 0; i < n; i++) {
			
			cdParam = capParams[i];
			
			if (DMath.greaterThan(cdParam, 0.0)) {
				
				/*
				 * still have to test that it is beyond the end points
				 */
				Point p = Point.point(c, d, cdParam);
				double abParam = Point.u(a, p, b);
				
				if (DMath.lessThanEquals(abParam, 0.0)) {
					
					assert ShapeUtils.intersectCC(ac, new Circle(null, p, cap.r));
					
					logger.debug("a cap");
					
					boolean present = false;
					for (int j = 0; j < paramCount; j++) {
						if (DMath.equals(params[j], cdParam)) {
							present = true;
							break;
						}
					}
					if (!present) {
						params[paramCount] = cdParam;
						paramCount++;
					}
				}
				
			}
			
		}
		
		/*
		 * top side, left hand side of <a, b>
		 */
		
		cdParam = SweepUtils.sweepCircleLine(aUp, bUp, c, d, cap.r);
		
		if (cdParam != -1) {
			
			if (DMath.greaterThan(cdParam, 0.0)) {
				
				/*
				 * still have to test that it isn't beyond the end points
				 */
				Point p = Point.point(c, d, cdParam);
				double abParam = Point.u(aUp, p, bUp);
				
				if (DMath.greaterThanEquals(abParam, 0.0) && DMath.lessThanEquals(abParam, 1.0)) {
					
					logger.debug("top side");
					
					boolean present = false;
					for (int j = 0; j < paramCount; j++) {
						if (DMath.equals(params[j], cdParam)) {
							present = true;
							break;
						}
					}
					if (!present) {
						params[paramCount] = cdParam;
						paramCount++;
					}
					
				}
				
			}
			
		}
		
		/*
		 * b cap
		 */
		
		n = SweepUtils.sweepCircleCircle(b, c, d, cap.r, r, capParams);
		
		for (int i = 0; i < n; i++) {
			
			cdParam = capParams[i];
			
			if (DMath.greaterThan(cdParam, 0.0)) {
				
				/*
				 * still have to test that it is beyond the end points
				 */
				Point p = Point.point(c, d, cdParam);
				double abParam = Point.u(a, p, b);
				
				if (DMath.greaterThanEquals(abParam, 1.0)) {
					
					assert ShapeUtils.intersectCC(bc, new Circle(null, p, cap.r));
					
					logger.debug("b cap");
					
					boolean present = false;
					for (int j = 0; j < paramCount; j++) {
						if (DMath.equals(params[j], cdParam)) {
							present = true;
							break;
						}
					}
					if (!present) {
						params[paramCount] = cdParam;
						paramCount++;
					}
				}
				
			}
			
		}
		
		/*
		 * bottom side
		 */
		
		cdParam = SweepUtils.sweepCircleLine(bDown, aDown, c, d, cap.r);
		
		if (cdParam != -1) {
			
			if (DMath.greaterThan(cdParam, 0.0)) {
				
				/*
				 * still have to test that it isn't beyond the end points
				 */
				Point p = Point.point(c, d, cdParam);
				double abParam = Point.u(bDown, p, aDown);
				
				if (DMath.greaterThanEquals(abParam, 0.0) && DMath.lessThanEquals(abParam, 1.0)) {
					
					logger.debug("bottom side");
					
					boolean present = false;
					for (int j = 0; j < paramCount; j++) {
						if (DMath.equals(params[j], cdParam)) {
							present = true;
							break;
						}
					}
					if (!present) {
						params[paramCount] = cdParam;
						paramCount++;
					}
				}
				
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
			
			if (outside) {
				events.add(new SweepEvent(SweepEventType.enter(parent), this, cap, cap.index, param));
			} else {
				events.add(new SweepEvent(SweepEventType.exit(parent), this, cap, cap.index, param));
			}
			outside = !outside;
		}
		
		return events;
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
	
	public java.awt.Shape java2D() {
		assert false;
		return null;
	}
	
	public void paint(RenderingContext ctxt) {
		
		ac.paint(ctxt);
		middle.paint(ctxt);
		bc.paint(ctxt);
		
		if (ctxt.type == RenderingContextType.CANVAS) {
			if (APP.DEBUG_DRAW) {
				
				Color c = ctxt.getColor();
				ctxt.setColor(Color.BLUE);
				ctxt.setPixelStroke(cam.pixelsPerMeter, 1);
				
				debugNormalLine.draw(ctxt);
				
				ctxt.setColor(c);
				
			}
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
