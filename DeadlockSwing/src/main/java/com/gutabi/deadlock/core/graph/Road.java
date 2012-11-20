package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.ColinearException;
import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.CapsuleSequence;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.model.StopSign;

@SuppressWarnings("static-access")
public class Road extends Edge {
	
	public static final double ROAD_RADIUS = 0.5;
	
	public final Vertex start;
	public final Vertex end;
	public final List<Point> raw;
	
	private CapsuleSequence seq;
	
	private Point startBorderPoint;
	private Point endBorderPoint;
	private int startBorderIndex;
	private int endBorderIndex;
	private double[] cumulativeLengthsFromStart;
	
	private double totalLength;
	
	private final boolean standalone;
	private final boolean loop;
	
	public final StopSign startSign;
	public final StopSign endSign;
	
	protected Color color;
	protected Color hiliteColor;
	
	private EdgeDirection direction;
	
	private final int hash;
	
	private final List<Vertex> vs;
	
	static Logger logger = Logger.getLogger(Road.class);
	
	public Road(Vertex start, Vertex end, List<Point> raw, int dec) {
		
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
		h = 37 * h + dec;
		hash = h;
		
		loop = (start == end);
		standalone = (loop) ? start == null : false;
		
//		color = new Color(0x88, 0x88, 0x88, 0xff);
		color = Color.GRAY;
		hiliteColor = new Color(0xff ^ 0x88, 0xff ^ 0x88, 0xff ^ 0x88, 0xff);
		
		direction = EdgeDirection.NONE;
		
		computeProperties();
		
		if (!standalone) {
			
			vs = new ArrayList<Vertex>();
			vs.add(start);
			vs.add(end);
			
			start.roads.add(this);
			end.roads.add(this);
			
			startSign = new StopSign(this, 0);
			endSign = new StopSign(this, 1);
			
			if ((dec & 1) == 1) {
				startSign.setEnabled(true);
			}
			
			if ((dec & 2) == 2) {
				endSign.setEnabled(true);
			}
			
			if ((dec & 4) == 4) {
				
				if ((dec & 8) == 0) {
					direction = EdgeDirection.STARTTOEND;
				} else {
					direction = EdgeDirection.ENDTOSTART;
				}
				
			}
			
		} else {
			startSign = null;
			endSign = null;
			vs = new ArrayList<Vertex>();
		}
		
	}
	
	public void destroy() {
		if (!standalone) {
			start.roads.remove(this);
			end.roads.remove(this);
		}
	}
	
	public int hashCode() {
		return hash;
	}
	
	public String toString() {
		return "Road[" + start.id + " " + end.id + "]";
	}
	
	
	public boolean isStandAlone() {
		return standalone;
	}
	
	public boolean isLoop() {
		return loop;
	}
	
	public int pointCount() {
		return seq.pointCount();
	}
	
	public int capsuleCount() {
		return seq.capsuleCount();
	}
	
	public double getTotalLength(Vertex a, Vertex b) {
		return totalLength;
	}
	
	public Point getPoint(int i) {
		return seq.getPoint(i);
	}
	
	public Capsule getCapsule(int i) {
		return seq.getCapsule(i);
	}
	
	public Point getStartBorderPoint() {
		return startBorderPoint;
	}
	
	public Point getEndBorderPoint() {
		return endBorderPoint;
	}
	
	public void setDirection(Axis a, EdgeDirection dir) {
		if (a == Axis.NONE) {
			this.direction = dir;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public EdgeDirection getDirection(Axis a) {
		if (a == Axis.NONE) {
			return direction;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public List<Vertex> getVertices(Axis a) {
		if (a == Axis.NONE) {
			return vs;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public boolean canTravelFromTo(Vertex a, Vertex b) {
		if (a == start) {
			assert b == end;
			return direction != EdgeDirection.ENDTOSTART;
		} else {
			assert a == end;
			assert b == start;
			return direction != EdgeDirection.STARTTOEND;
		}
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
	
	public void enterDistancesMatrix(double[][] distances) {
		double cur = distances[start.id][end.id];
		/*
		 * there may be multiple roads between start and end, so don't just blindly set it to totalLength
		 */
		if (totalLength < cur) {
			switch (direction) {
			case NONE:
				distances[start.id][end.id] = totalLength;
		    	distances[end.id][start.id] = totalLength;
				break;
			case STARTTOEND:
				distances[start.id][end.id] = totalLength;
//		    	distances[end.id][start.id] = totalLength;
				break;
			case ENDTOSTART:
//				distances[start.id][end.id] = totalLength;
		    	distances[end.id][start.id] = totalLength;
				break;
			}
		}
	}
	
	public void removeStopSignTop(StopSign s) {
		assert s == startSign || s == endSign;
		if (s == startSign) {
			startSign.setEnabled(false);
		} else {
			endSign.setEnabled(false);
		}
	}
	
	public GraphPosition travelFromConnectedVertex(Vertex v, double dist) {
		
		if (v == start) {
			return RoadPosition.travelFromStart(this, dist);
		} else {
			return RoadPosition.travelFromEnd(this, dist);
		}
		
	}
	
	public Entity decorationsHitTest(Point p) {
		
		Entity hit;
		
		if (startSign != null) {
			hit = startSign.hitTest(p);
			if (hit != null) {
				return hit;
			}
		}
		
		if (endSign != null) {
			hit = endSign.hitTest(p);
			if (hit != null) {
				return hit;
			}
		}
		
		return null;
	}
	
	public Entity decorationsBestHitTest(Shape s) {
		
		Entity hit;
		
		if (startSign != null) {
			hit = startSign.bestHitTest(s);
			if (hit != null) {
				return hit;
			}
		}
		
		if (endSign != null) {
			hit = endSign.bestHitTest(s);
			if (hit != null) {
				return hit;
			}
		}
		
		return null;
		
	}
	
	public RoadPosition findSkeletonIntersection(Point c, Point d) {
		for (int i = 0; i < seq.capsuleCount(); i ++) {
			Capsule cap = seq.getCapsule(i);
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

		for (int i = 0; i < seq.capsuleCount(); i++) {
			Capsule c = seq.getCapsule(i);
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
				if (bestIndex == seq.capsuleCount()-1) {
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
			endBorderIndex = seq.capsuleCount()-1;
			startBorderPoint = seq.getPoint(startBorderIndex);
			endBorderPoint = seq.getPoint(endBorderIndex);
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
	
	private void computeCaps() {
		
		if (hash == 1577748441) {
			String.class.getName();
		}
		
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
		
		List<Circle> circs = new ArrayList<Circle>();
		for (Point p : adj) {
			circs.add(new Circle(this, p, ROAD_RADIUS));
		}
		
		List<Capsule> caps = new ArrayList<Capsule>();
		for (int i = 0; i < adj.size()-1; i++) {
			Circle a = circs.get(i);
			Circle b = circs.get(i+1);
			caps.add(new Capsule(this, a, b));
		}
		
		seq = new CapsuleSequence(this, caps);
		
		shape = seq;
		
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
		
		cumulativeLengthsFromStart = new double[seq.pointCount()];
		
		double length;
		double l = 0.0;
		for (int i = 0; i < seq.capsuleCount(); i++) {
			Capsule s = seq.getCapsule(i);
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
	 * @param g2 in world coords
	 */
	public void paint(Graphics2D g2) {
		
		paintPath(g2);
		
		if (MODEL.DEBUG_DRAW) {
			
//			paintAABB(g2);
			shape.aabb.paint(g2);
			
		}
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintHilite(Graphics2D g2) {
		drawPath(g2);
	}
	
	
	static java.awt.Stroke directionStroke = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	static Polygon arrowPointer = new Polygon(new int[]{0, -20, -20}, new int[]{0, -10, 10}, 3);
	
	private void paintPath(Graphics2D g2) {
		
		g2.setColor(color);
		
		seq.paint(g2);
		
		if (direction != EdgeDirection.NONE) {
			
			AffineTransform origTransform = g2.getTransform();
			java.awt.Stroke origStroke = g2.getStroke();
			g2.setStroke(directionStroke);
			g2.setColor(Color.LIGHT_GRAY);
			
			seq.drawSkeleton(g2);
			
			if (direction == EdgeDirection.STARTTOEND) {
				
				Capsule c = seq.getCapsule(capsuleCount()-2);
				
				double angle = Math.atan2(c.b.y-c.a.y, c.b.x-c.a.x);
				
				g2.scale(MODEL.PIXELS_PER_METER, MODEL.PIXELS_PER_METER);
				g2.translate(endBorderPoint.x, endBorderPoint.y);
				g2.rotate(angle);
				g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
				
				g2.fillPolygon(arrowPointer);
				
			} else {
				
				Capsule c = seq.getCapsule(1);
				
				double angle = Math.atan2(c.b.y-c.a.y, c.b.x-c.a.x);
				
				g2.scale(MODEL.PIXELS_PER_METER, MODEL.PIXELS_PER_METER);
				g2.translate(startBorderPoint.x, startBorderPoint.y);
				g2.rotate(angle);
				g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
				g2.rotate(Math.PI);
				
				g2.fillPolygon(arrowPointer);
				
			}
			
			g2.setTransform(origTransform);
			g2.setStroke(origStroke);
			
		}
		
	}
	
	private void drawPath(Graphics2D g2) {
		
		g2.setColor(hiliteColor);
		
		seq.draw(g2);
		
	}
	
	/**
	 * @param g2 in pixels
	 */
	private void paintSkeleton(Graphics2D g2) {
		
		g2.setColor(Color.BLACK);
		
		seq.drawSkeleton(g2);
		
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintBorders(Graphics2D g2) {
		
		g2.setColor(Color.GREEN);
		g2.fillOval((int)(startBorderPoint.x * MODEL.PIXELS_PER_METER)-2, (int)(startBorderPoint.y * MODEL.PIXELS_PER_METER)-2, 4, 4);
		
		g2.setColor(Color.RED);
		g2.fillOval((int)(endBorderPoint.x * MODEL.PIXELS_PER_METER)-2, (int)(endBorderPoint.y * MODEL.PIXELS_PER_METER)-2, 4, 4);
		
	}
	
	public void paintDecorations(Graphics2D g2) {
		
		if (startSign != null) {
			startSign.paint(g2);
		}
		
		if (endSign != null) {
			endSign.paint(g2);
		}
		
		if (MODEL.DEBUG_DRAW) {
			
			paintSkeleton(g2);
			
		}
		
	}
}
