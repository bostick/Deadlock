package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.ColinearException;
import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.model.Stroke;

@SuppressWarnings("static-access")
public final class Road implements Entity, Edge {
	
	public static final double ROAD_RADIUS = 0.5;
	
	public final Vertex start;
	public final Vertex end;
	public final List<Point> raw;
	
	private List<Capsule> caps;
//	private SweepEventListener l;
	
	private Point startBorderPoint;
	private Point endBorderPoint;
	private int startBorderIndex;
	private int endBorderIndex;
	private double[] cumulativeLengthsFromStart;
	
	private double totalLength;
	
	private final boolean standalone;
	private final boolean loop;
	
	public int id;
	public StopSign startSign;
	public StopSign endSign;
	
	protected Color color;
	protected Color hiliteColor;
	
	private final int hash;
	
	static Logger logger = Logger.getLogger(Road.class);
	
	public Road(Vertex start, Vertex end, List<Point> raw) {
		
		assert !raw.isEmpty();
		
		this.start = start;
		this.end = end;
		this.raw = raw;
		
		int h = 17;
		if (start != null) {
			h = 37 * h + start.hashCode();
		}
		if (end != null) {
			h = 37 * h + end.hashCode();
		}
		h = 37 * h + raw.hashCode();
		hash = h;
		
		loop = (start == end);
		standalone = (loop) ? start == null : false;
		
		color = new Color(0x88, 0x88, 0x88, 0xff);
		hiliteColor = new Color(0xff ^ 0x88, 0xff ^ 0x88, 0xff ^ 0x88, 0xff);
		
		computeProperties();
		
	}
	
	public int hashCode() {
		return hash;
	}
	
	public String toString() {
		return start.id + " " + end.id;
	}
	
	
	
	public boolean isStandAlone() {
		return standalone;
	}
	
	public boolean isLoop() {
		return loop;
	}
	
	/**
	 * how many points?
	 */
	public int size() {
		return caps.size()+1;
	}
	
	public double getTotalLength(Vertex a, Vertex b) {
		return totalLength;
	}
	
	public double getTotalLength() {
		return totalLength;
	}
	
	public Point get(int i) {
		if (i == caps.size()) {
			return caps.get(i-1).b;
		} else {
			return caps.get(i).a;
		}
	}
	
	/*
	 * used in debugging
	 */
	public Capsule getCapsule(int i) {
		return caps.get(i);
	}
	
	public Point getStartBorderPoint() {
		return startBorderPoint;
	}
	
	public Point getEndBorderPoint() {
		return endBorderPoint;
	}
	
	
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		;
	}
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep(double t) {
		return true;
	}
	
	
	
	
	
	
	
//	public void setSweepEventListener(SweepEventListener l) {
//		this.l = l;
//	}
	
	public void sweepStart(Stroke s, SweepEventListener l) {
		
		/*
		 * TODO:
		 * a lot of room for improvement
		 * adjacent capsules share caps, so could share calculation
		 */
		for (Capsule c : caps.subList(1, caps.size()-1)) {
//			c.setSweepEventListener(l);
			c.sweepStart(s, l);
		}
		
	}
	
	public void sweepEnd(Stroke s, SweepEventListener l) {
		
		/*
		 * TODO:
		 * a lot of room for improvement
		 * adjacent capsules share caps, so could share calculation
		 */
		for (Capsule c : caps.subList(1, caps.size()-1)) {
//			c.setSweepEventListener(l);
			c.sweepEnd(s, l);
		}
		
	}
	
	public void sweep(Stroke s, int index, SweepEventListener l) {
		
		/*
		 * TODO:
		 * a lot of room for improvement
		 * adjacent capsules share caps, so could share calculation
		 */
		for (Capsule c : caps.subList(1, caps.size()-1)) {
//			c.setSweepEventListener(l);
			c.sweep(s, index, l);
		}
		
	}
	
	
	
	
	
	public GraphPosition travelFromConnectedVertex(Vertex v, double dist) {
		
		if (v == start) {
			return RoadPosition.travelFromStart(this, dist);
		} else {
			return RoadPosition.travelFromEnd(this, dist);
		}
		
	}
	
	public final boolean hitTest(Point p) {
		if (aabb.hitTest(p)) {
			
			for (Capsule s : caps.subList(1, caps.size()-1)) {
				if (s.hitTest(p)) {
					return true;
				}
			}
			return false;
			
		} else {
			return false;
		}
	}
	
	public final boolean bestHitTest(Point p, double radius) {
		for (Capsule s : caps.subList(1, caps.size()-1)) {
			if (s.bestHitTest(p, radius)) {
				return true;
			}
		}
		return false;
	}
	
//	public RoadPosition skeletonHitTest(Point p) {
//		for (int i = 0; i < caps.size(); i++) {
//			Capsule c = caps.get(i);
//			double param = c.skeletonHitTest(p);
//			if (param != -1) {
//				return new RoadPosition(this, i, param);
//			}
//		}
//		return null;
//	}
	
	public RoadPosition findSkeletonIntersection(Point c, Point d) {
		for (int i = 0; i < caps.size(); i ++) {
			Capsule cap = caps.get(i);
			double abParam = cap.findSkeletonIntersection(c, d);
			if (abParam != -1 && !DMath.equals(abParam, 1.0)) {
				return new RoadPosition(this, i, abParam);
			}
		}
		return null;
	}
	
	public RoadPosition findClosestRoadPosition(Point p, double radius) {

		int bestIndex = -1;
		double bestParam = -1;
		Point bestPoint = null;

		for (int i = 0; i < caps.size(); i++) {
			Capsule c = caps.get(i);
			double closest = closestParam(p, c);
			Point ep = Point.point(c.a, c.b, closest);
			double dist = Point.distance(p, ep);
			if (DMath.lessThanEquals(dist, radius + Road.ROAD_RADIUS)) {
				if (bestPoint == null) {
					bestIndex = i;
					bestParam = closest;
					bestPoint = ep;
				} else if (Point.distance(p, ep) < Point.distance(p, bestPoint)) {
					bestIndex = i;
					bestParam = closest;
					bestPoint = ep;
				}
			}
		}

		if (bestPoint != null) {
			if (bestParam == 1.0) {
				if (bestIndex == caps.size()-1) {
					return null;
				} else {
					return new RoadPosition(this, bestIndex+1, 0.0);
				}
			} else {
				return new RoadPosition(this, bestIndex, bestParam);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * find closest position on <c, d> to the point b
	 */
	private double closestParam(Point p, Capsule c) {
		if (p.equals(c.a)) {
			return 0.0;
		}
		if (p.equals(c.b)) {
			return 1.0;
		}

		double u = Point.u(c.a, p, c.b);
		if (DMath.lessThanEquals(u, 0.0)) {
			return 0.0;
		} else if (DMath.greaterThanEquals(u, 1.0)) {
			return 1.0;
		} else {
			return u;
		}
	}
	
	
	
	
	public boolean isDeleteable() {
		return true;
	}
	
	
	/**
	 * segment index i
	 */
	public double getLengthFromStart(int i) {
		return cumulativeLengthsFromStart[i];
	}
	
	public double getLengthFromEnd(int i) {
		return totalLength - cumulativeLengthsFromStart[i];
	}
	
	public void computeProperties() {
		
		computeCaps();
		
		/*
		 * after adjustToBorders, the border properties are set to predictable values
		 */
		if (!standalone) {
			startBorderIndex = 1;
			endBorderIndex = caps.size()-1;
			startBorderPoint = get(startBorderIndex);
			endBorderPoint = get(endBorderIndex);
			assert DMath.equals(Point.distance(startBorderPoint, start.p), start.getRadius());
			assert DMath.equals(Point.distance(endBorderPoint, end.p), end.getRadius());
		} else {
			startBorderIndex = -1;
			endBorderIndex = -1;
			startBorderPoint = null;
			endBorderPoint = null;
		}
		
		computeLengths();
		
		if (startSign != null) {
			startSign.computePoint();
		}
		if (endSign != null) {
			endSign.computePoint();
		}
		
		computeAABB();
		
	}
	
	private void computeCaps() {
		
		assert !raw.isEmpty();
		List<Point> adj = raw;
		
		adj = removeDuplicates(adj);
		
		List<Point> newAdj = removeColinear(adj);
		while (!newAdj.equals(adj)) {
			adj = newAdj;
			newAdj = removeColinear(adj);
		}
		adj = newAdj;
		
		if (!standalone) {
			computeBorders(adj);
			adj = adjustToBorders(adj);
			adj = removeDuplicates(adj);
		}
		
		caps = new ArrayList<Capsule>();
		for (int i = 0; i < adj.size()-1; i++) {
			Point a = adj.get(i);
			Point b = adj.get(i+1);
			Capsule c = new Capsule(a, b, Road.ROAD_RADIUS);
			caps.add(c);
		}
		
	}
	
	private static List<Point> removeDuplicates(List<Point> stroke) {
		List<Point> adj = new ArrayList<Point>();
		adj.add(stroke.get(0));
		for (int i = 0; i < stroke.size()-1; i++) {
			Point prev = stroke.get(i);
			Point cur = stroke.get(i+1);
			if (!cur.equals(prev)) {
				adj.add(cur);
			}
		}
		return adj;
	}
	
	private static List<Point> removeColinear(List<Point> stroke) {
		List<Point> adj = new ArrayList<Point>();
		for (int i = 0; i < stroke.size(); i++) {
			Point cur = stroke.get(i);
			if (adj.size() < 2) {
				adj.add(cur);
			} else {
				int n = adj.size();
				Point last = adj.get(n-1);
				Point pen = adj.get(n-2);
				try {
					if (!Point.colinear(pen, last, cur)) {
						adj.add(cur);
					} else {
						adj.remove(n-1);
						adj.add(cur);
					}
				} catch (ColinearException e) {
					/*
					 * cur is between pen and last, so just ignore
					 */
				}
			}
		}
		return adj;
	}
	
	private void computeBorders(List<Point> pts) {
		
		double c = startBorderCombo(start, pts);
		if (c < 0) {
			startBorderIndex = (int)Math.floor(c);
			assert startBorderIndex == -1;
			double startBorderParam = c-startBorderIndex;
			assert 0 <= startBorderParam && startBorderParam <= 1;
			startBorderPoint = Point.point(start.p, pts.get(startBorderIndex+1), startBorderParam);
		} else {
			startBorderIndex = (int)Math.floor(c);
			double startBorderParam = c-startBorderIndex;
			assert 0 <= startBorderParam && startBorderParam <= 1;
			startBorderPoint = Point.point(pts.get(startBorderIndex), pts.get(startBorderIndex+1), startBorderParam);
		}
		
		c = endBorderCombo(end, pts);
		if (c >= pts.size()-1) {
			endBorderIndex = (int)Math.floor(c);
			assert endBorderIndex == pts.size()-1;
			double endBorderParam = c-endBorderIndex;
			assert 0 <= endBorderParam && endBorderParam <= 1;
			endBorderPoint = Point.point(pts.get(endBorderIndex), end.p, endBorderParam);
		} else {
			endBorderIndex = (int)Math.floor(c);
			double endBorderParam = c-endBorderIndex;
			assert 0 <= endBorderParam && endBorderParam <= 1;
			endBorderPoint = Point.point(pts.get(endBorderIndex), pts.get(endBorderIndex+1), endBorderParam);
		}
		
	}
	
	private List<Point> adjustToBorders(List<Point> pts) {
		
		List<Point> adj = new ArrayList<Point>();
		adj.add(start.p);
		adj.add(startBorderPoint);
		for (int i = startBorderIndex+1; i < endBorderIndex; i++) {
			adj.add(pts.get(i));
		}
		adj.add(endBorderPoint);
		adj.add(end.p);
		
		return adj;
	}
	
	/**
	 * returns index + param
	 * returns -1 + param if first point is already outside of radius
	 * returns Infinity if last point is still inside radius
	 */
	private static double startBorderCombo(Vertex start, List<Point> pts) {
		
		Point center = start.p;
		double radius = start.getRadius();
		
		for (int i = 0; i < pts.size()-1; i++) {
			Point a = pts.get(i);
			Point b = pts.get(i+1);
			if (DMath.greaterThan(Point.distance(a, center), radius)) {
				assert i == 0;
				
				Point[] ints = new Point[2];
				int n = Point.circleSegmentIntersections(center, radius, center, a, ints);
				assert n == 1;
				
				return -1 + Point.param(ints[0], center, a);
				
			} else if (DMath.equals(Point.distance(b, center), radius)) {
				return (i+1) + 0.0;
			} else if (DMath.greaterThan(Point.distance(b, center), radius)) {
				assert DMath.lessThanEquals(Point.distance(a, center), radius);
				
				Point[] ints = new Point[2];
				int n = Point.circleSegmentIntersections(center, radius, a, b, ints);
				assert n == 1;
				
				return i + Point.param(ints[0], a, b);
			}
		}
		
		return Double.POSITIVE_INFINITY;
	}
	
	/**
	 * returns index + param
	 * returns size() + param if last point is outside of radius
	 * returns -Infinity if first point is inside radius
	 */
	private static double endBorderCombo(Vertex end, List<Point> pts) {
		
		Point center = end.p;
		double radius = end.getRadius();
		
		for (int i = pts.size()-2; i >= 0; i--) {
			Point a = pts.get(i);
			Point b = pts.get(i+1);
			if (DMath.greaterThan(Point.distance(b, center), radius)) {
				assert i+1 == pts.size()-1;
				
				Point[] ints = new Point[2];
				int n = Point.circleSegmentIntersections(center, radius, b, center, ints);
				assert n == 1;
				
				return i+1 + Point.param(ints[0], b, center);
				
			} else if (DMath.equals(Point.distance(a, center), radius)) {
				return i + 0.0;
			} else if (DMath.greaterThan(Point.distance(a, center), radius)) {
				assert DMath.lessThanEquals(Point.distance(b, center), radius);
				
				Point[] ints = new Point[2];
				int n = Point.circleSegmentIntersections(center, radius, a, b, ints);
				assert n == 1;
				
				return i + Point.param(ints[0], a, b);
			}
		}
		
		return Double.NEGATIVE_INFINITY;
	}
	
	private void computeLengths() {
		
		cumulativeLengthsFromStart = new double[caps.size()+1];
		
		double length;
		double l = 0.0;
		for (int i = 0; i < caps.size(); i++) {
			Capsule s = caps.get(i);
			if (i == 0) {
				cumulativeLengthsFromStart[i] = 0;
			}
			length = Point.distance(s.a, s.b);
			l += length;
			cumulativeLengthsFromStart[i+1] = cumulativeLengthsFromStart[i] + length;
		}
		
		totalLength = l;
		
	}
	
	private void computeAABB() {
		aabb = null;
		
		if (startBorderPoint.equals(endBorderPoint)) {
			aabb = new Rect(startBorderPoint.x, startBorderPoint.y, 0.0, 0.0);
		} else {
			for (Capsule s : caps.subList(1, caps.size()-1)) {
				aabb = Rect.union(aabb, s.aabb);
			}
		}
		
	}
	
	protected Rect aabb;
	public final Rect getAABB() {
		return aabb;
	}
	
	protected void paintAABB(Graphics2D g2) {
		
		g2.setColor(Color.BLACK);
		g2.drawRect(
				(int)(aabb.x * MODEL.PIXELS_PER_METER),
				(int)(aabb.y * MODEL.PIXELS_PER_METER),
				(int)(aabb.width * MODEL.PIXELS_PER_METER),
				(int)(aabb.height * MODEL.PIXELS_PER_METER));
		
	}
	
	public static boolean haveExactlyOneSharedIntersection(Road a, Road b) {
		Vertex as = a.start;
		Vertex ae = a.end;
		Vertex bs = b.start;
		Vertex be = b.end;
		if (as == bs) {
			return (ae != be);
		} else if (as == be) {
			return (ae != bs);
		} else if (ae == bs) {
			return (as != be);
		} else if (ae == be) {
			return (as != bs);
		} else {
			return false;
		}
	}
	
	public static boolean haveTwoSharedIntersections(Road a, Road b) {
		Vertex as = a.start;
		Vertex ae = a.end;
		Vertex bs = b.start;
		Vertex be = b.end;
		if (as == bs) {
			return (ae == be);
		} else if (as == be) {
			return (ae == bs);
		} else if (ae == bs) {
			return (as == be);
		} else if (ae == be) {
			return (as == bs);
		} else {
			return false;
		}
	}
	
	/**
	 * returns the single shared intersection between roads a and b
	 */
	public static Vertex sharedIntersection(Road a, Road b) throws SharedVerticesException {
		assert a != b;
		Vertex as = a.start;
		Vertex ae = a.end;
		Vertex bs = b.start;
		Vertex be = b.end;
		if (as == bs) {
			if (ae == be) {
				throw new SharedVerticesException(as, ae);
			}
			return as;
		} else if (as == be) {
			if (ae == bs) {
				throw new SharedVerticesException(as, ae);
			}
			return as;
		} else if (ae == bs) {
			if (as == be) {
				throw new SharedVerticesException(as, ae);
			}
			return ae;
		} else if (ae == be) {
			if (as == bs) {
				throw new SharedVerticesException(as, ae);
			}
			return be;
		} else {
			return null;
		}
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paint(Graphics2D g2) {
		if (!MODEL.DEBUG_DRAW) {
			
			paintPath(g2);
			
		} else {
			
			paintPath(g2);
			
			AffineTransform origTransform = g2.getTransform();
			
			g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
			
			paintAABB(g2);
			
			g2.setTransform(origTransform);
		}
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintHilite(Graphics2D g2) {
		drawPath(g2);
	}
	
	private void paintPath(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.setColor(color);
		
		for (Capsule s : caps.subList(1, caps.size()-1)) {
			s.paint(g2);
		}
		
		g2.setTransform(origTransform);
	}
	
	private void drawPath(Graphics2D g2) {
		AffineTransform origTransform = g2.getTransform();
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.setColor(hiliteColor);
		
		for (Capsule s : caps.subList(1, caps.size()-1)) {
			s.draw(g2);
		}
		
		g2.setTransform(origTransform);
	}
	
	/**
	 * @param g2 in pixels
	 */
//	public void paintSkeleton(Graphics2D g2) {
//		
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
//		g2.setColor(Color.BLACK);
//		
//		int[] xs = new int[spine.size()+1];
//		int[] ys = new int[spine.size()+1];
//		Point a = spine.get(0).a;
//		xs[0] = (int)(a.x * MODEL.PIXELS_PER_METER);
//		ys[0] = (int)(a.y * MODEL.PIXELS_PER_METER);
//		for (int i = 0; i < spine.size(); i++) {
//			Point p = spine.get(i).b;
//			xs[i+1] = (int)(p.x * MODEL.PIXELS_PER_METER);
//			ys[i+1] = (int)(p.y * MODEL.PIXELS_PER_METER);
//		}
//		g2.drawPolyline(xs, ys, spine.size()+1);
//	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintBorders(Graphics2D g2) {
		
		g2.setColor(Color.GREEN);
		g2.fillOval((int)(startBorderPoint.x * MODEL.PIXELS_PER_METER)-2, (int)(startBorderPoint.y * MODEL.PIXELS_PER_METER)-2, 4, 4);
		
		g2.setColor(Color.RED);
		g2.fillOval((int)(endBorderPoint.x * MODEL.PIXELS_PER_METER)-2, (int)(endBorderPoint.y * MODEL.PIXELS_PER_METER)-2, 4, 4);
		
	}
	
}