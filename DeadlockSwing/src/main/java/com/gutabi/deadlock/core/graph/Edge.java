package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bric.geom.AreaX;
import com.gutabi.deadlock.core.ColinearException;
import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.utils.Java2DUtils;

@SuppressWarnings("static-access")
public final class Edge extends Entity {
	
	public static final double ROAD_RADIUS = 0.5;
	
	public final Vertex start;
	public final Vertex end;
	public final List<Point> raw;
	
	private List<EdgeSegment> spine;
	
	private Path2D path;
	
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
	
	private final int hash;
	
	static Logger logger = Logger.getLogger(Edge.class);
	
	public Edge(Vertex start, Vertex end, List<Point> raw) {
		
		assert !raw.isEmpty();
		
		this.start = start;
		this.end = end;
		this.raw = raw;
		
		loop = (start == end);
		standalone = (loop) ? start == null : false;
		
		color = new Color(0x88, 0x88, 0x88, 0xff);
		hiliteColor = new Color(0xff ^ 0x88, 0xff ^ 0x88, 0xff ^ 0x88, 0xff);
		
		int h = 17;
		if (start != null) {
			h = 37 * h + start.hashCode();
		}
		if (end != null) {
			h = 37 * h + end.hashCode();
		}
		h = 37 * h + raw.hashCode();
		hash = h;
		
		computeProperties();
		computePath();
		
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
	
	public int size() {
		return spine.size()+1;
	}
	
	public double getTotalLength() {
		return totalLength;
	}
	
	public Point get(int i) {
		if (i == spine.size()) {
			return spine.get(i-1).b;
		} else {
			return spine.get(i).a;
		}
	}
	
	public EdgeSegment getSegment(int i) {
		return spine.get(i);
	}
	
	public Point getStartBorderPoint() {
		return startBorderPoint;
	}
	
	public Point getEndBorderPoint() {
		return endBorderPoint;
	}
	
	
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep() {
		return true;
	}
	
	
	
	
	
	public boolean hitTest(Point p, double radius) {
		for (int i = 0; i < spine.size(); i++) {
			EdgeSegment s = spine.get(i);
			if (s.hitTest(p, radius)) {
				return true;
			}
		}
		return false;
	}
	
	public EdgePosition findClosestEdgePosition(Point p, double radius) {
		
		int bestIndex = -1;
		double bestParam = -1;
		Point bestPoint = null;
		
		for (int i = 0; i < spine.size(); i++) {
			EdgeSegment s = spine.get(i);
			double closest = closestParam(p, s);
			Point ep = Point.point(s.a, s.b, closest);
			double dist = Point.distance(p, ep);
			if (DMath.lessThanEquals(dist, radius + Edge.ROAD_RADIUS)) {
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
				if (bestIndex == spine.size()-1) {
					return null;
				} else {
					return new EdgePosition(this, bestIndex+1, 0.0);
				}
			} else {
				return new EdgePosition(this, bestIndex, bestParam);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * find closest position on <c, d> to the point b
	 */
	private double closestParam(Point p, EdgeSegment s) {
		if (p.equals(s.a)) {
			return 0.0;
		}
		if (p.equals(s.b)) {
			return 1.0;
		}
		if (s.a.equals(s.b)) {
			throw new IllegalArgumentException("c equals d");
		}
		
		double u = Point.u(s.a, p, s.b);
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
		
		computeSegs();
		
		/*
		 * after adjustToBorders, the border properties are set to predictable values
		 */
		if (!standalone) {
			startBorderIndex = 1;
			endBorderIndex = spine.size()-1;
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
		
	}
	
	private void computeSegs() {
		
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
		}
		
		spine = new ArrayList<EdgeSegment>();
		for (int i = 0; i < adj.size()-1; i++) {
			Point a = adj.get(i);
			Point b = adj.get(i+1);
			spine.add(new EdgeSegment(a, b));
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
		
		cumulativeLengthsFromStart = new double[spine.size()+1];
		
		double length;
		double l = 0.0;
		for (int i = 0; i < spine.size(); i++) {
			EdgeSegment s = spine.get(i);
			if (i == 0) {
				cumulativeLengthsFromStart[i] = 0;
			}
			length = Point.distance(s.a, s.b);
			l += length;
			cumulativeLengthsFromStart[i+1] = cumulativeLengthsFromStart[i] + length;
		}
		
		totalLength = l;
		
	}
	
	/**
	 * computes path, and also renderingUpperLeft and renderingBottomRight
	 */
	public void computePath() {
		
		assert !standalone;
		
		AreaX area = new AreaX();
		
		if (startBorderIndex == endBorderIndex) {
			assert startBorderPoint.equals(endBorderPoint);
			
			addToArea(area, startBorderPoint);
			
		} else {
			
			for (int i = startBorderIndex; i < endBorderIndex; i++) {
				addToArea(area, spine.get(i));
			}
			
		}
		
		assert area.isSingular();
		
		List<Point> poly = Java2DUtils.shapeToList(area, 0.1);
		
		path = Java2DUtils.listToPath(poly);
		
		computeAABB();
	}
	
	protected void computeAABB() {
		Rectangle2D bound = path.getBounds2D();
		aabb = new Rect(bound.getX(), bound.getY(), bound.getWidth(), bound.getHeight());
	}
	
	private void addToArea(AreaX area, EdgeSegment s) {

		Point diff = new Point(s.b.x - s.a.x, s.b.y - s.a.y);
		Point up = Point.ccw90(diff).multiply(ROAD_RADIUS / diff.length);
		Point down = Point.cw90(diff).multiply(ROAD_RADIUS / diff.length);
		
		Point p0 = s.a.plus(up);
		Point p1 = s.a.plus(down);
		Point p2 = s.b.plus(up);
		Point p3 = s.b.plus(down);
		Path2D path = new Path2D.Double();
		path.moveTo(p0.x, p0.y);
		path.lineTo(p1.x, p1.y);
		path.lineTo(p3.x, p3.y);
		path.lineTo(p2.x, p2.y);
		path.lineTo(p0.x, p0.y);
		
		Area disk1 = new Area(new Ellipse2D.Double(s.a.x-ROAD_RADIUS, s.a.y-ROAD_RADIUS, 2 * ROAD_RADIUS, 2 * ROAD_RADIUS));
		Area rect = new Area(path);
		Area disk2 = new Area(new Ellipse2D.Double(s.b.x-ROAD_RADIUS, s.b.y-ROAD_RADIUS, 2 * ROAD_RADIUS, 2 * ROAD_RADIUS));
		
		Area capsule = new Area();
		capsule.add(disk1);
		capsule.add(rect);
		capsule.add(disk2);
		
		area.add(capsule);
	}
	
	private void addToArea(AreaX area, Point p) {
		
		Area disk = new Area(new Ellipse2D.Double(p.x-ROAD_RADIUS, p.y-ROAD_RADIUS, 2 * ROAD_RADIUS, 2 * ROAD_RADIUS));
		
		area.add(disk);
	}
	
	
	
	
	
	public static boolean haveExactlyOneSharedIntersection(Edge a, Edge b) {
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
	
	public static boolean haveTwoSharedIntersections(Edge a, Edge b) {
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
	 * returns the single shared intersection between edges a and b
	 */
	public static Vertex sharedIntersection(Edge a, Edge b) throws SharedVerticesException {
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
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setColor(color);
		g2.fill(path);
	}
	
	private void drawPath(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setColor(hiliteColor);
		g2.draw(path);
	}
	
	/**
	 * @param g2 in pixels
	 */
	public void paintSkeleton(Graphics2D g2) {
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setColor(Color.BLACK);
//		g2.setStroke(new BasicStroke(1.0f));
		
		int[] xs = new int[spine.size()+1];
		int[] ys = new int[spine.size()+1];
		Point a = spine.get(0).a;
		xs[0] = (int)(a.x * MODEL.PIXELS_PER_METER);
		ys[0] = (int)(a.y * MODEL.PIXELS_PER_METER);
		for (int i = 0; i < spine.size(); i++) {
			Point p = spine.get(i).b;
			xs[i+1] = (int)(p.x * MODEL.PIXELS_PER_METER);
			ys[i+1] = (int)(p.y * MODEL.PIXELS_PER_METER);
		}
		g2.drawPolyline(xs, ys, spine.size()+1);
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintBorders(Graphics2D g2) {
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		
		g2.setColor(Color.GREEN);
		g2.fillOval((int)(startBorderPoint.x * MODEL.PIXELS_PER_METER)-2, (int)(startBorderPoint.y * MODEL.PIXELS_PER_METER)-2, 4, 4);
		
		g2.setColor(Color.RED);
		g2.fillOval((int)(endBorderPoint.x * MODEL.PIXELS_PER_METER)-2, (int)(endBorderPoint.y * MODEL.PIXELS_PER_METER)-2, 4, 4);
		
	}
	
}
