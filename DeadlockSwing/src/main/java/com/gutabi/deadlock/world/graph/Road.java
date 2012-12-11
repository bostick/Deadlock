package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.ColinearException;
import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Matrix;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.CapsuleSequence;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.core.geom.Triangle;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;

@SuppressWarnings("static-access")
public class Road extends Edge {
	
	public static final double ROAD_RADIUS = 0.5;
	
	public static final double borderPointRadius = 0.2;
	
	public final Vertex start;
	public final Vertex end;
	public final List<Point> raw;
	public final int dec;
	
	private CapsuleSequence seq;
	
	private Circle startBorderPoint;
	private Circle endBorderPoint;
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
	
	private Direction direction;
	
	private int hash;
	
	private CapsuleSequence shape;
	
	private Triangle arrowPointer;
	
	static Logger logger = Logger.getLogger(Road.class);
	
	public Road(Vertex start, Vertex end, List<Point> raw, int dec) {
		
		assert !raw.isEmpty();
		
		this.start = start;
		this.end = end;
		this.raw = raw;
		this.dec = dec;
		
		loop = (start == end);
		standalone = (loop) ? start == null : false;
		
		color = Color.GRAY;
		hiliteColor = new Color(0xff ^ 0x88, 0xff ^ 0x88, 0xff ^ 0x88, 0xff);
		
//		direction = Direction.NONE;
		
		computeProperties();
		
		if (!standalone) {
			
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
					direction = Direction.STARTTOEND;
				} else {
					direction = Direction.ENDTOSTART;
				}
				
			}
			
		} else {
			startSign = null;
			endSign = null;
		}
		
	}
	
	public void destroy() {
		if (!standalone) {
			start.roads.remove(this);
			end.roads.remove(this);
		}
	}
	
	public int hashCode() {
		if (hash == 0) {
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
		}
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
		return startBorderPoint.center;
	}
	
	public Point getEndBorderPoint() {
		return endBorderPoint.center;
	}
	
	public void setDirection(Axis a, Direction dir) {
		if (a == null) {
			this.direction = dir;
			
			if (dir == Direction.STARTTOEND) {
				
				Capsule c = seq.getCapsule(capsuleCount()-2);
				
				double angle = Math.atan2(c.b.y-c.a.y, c.b.x-c.a.x);
				
				double[][] rotMat = new double[][]{ { Math.cos(angle), -Math.sin(angle) }, { Math.sin(angle), Math.cos(angle) } };
				
				Point p0 = Matrix.times(rotMat, new Point(0, 0)).plus(endBorderPoint.center);
				Point p1 = Matrix.times(rotMat, new Point(-1, 0.3)).plus(endBorderPoint.center);
				Point p2 = Matrix.times(rotMat, new Point(-1, -0.3)).plus(endBorderPoint.center);
				
				arrowPointer = new Triangle(p0, p1, p2);
				
			} else if (dir == Direction.ENDTOSTART) {
				
				Capsule c = seq.getCapsule(1);
				
				double angle = Math.atan2(c.b.y-c.a.y, c.b.x-c.a.x);
				
				double[][] rotMat = new double[][]{ { Math.cos(angle), -Math.sin(angle) }, { Math.sin(angle), Math.cos(angle) } };
				
				Point p0 = Matrix.times(rotMat, new Point(0, 0)).plus(startBorderPoint.center);
				Point p1 = Matrix.times(rotMat, new Point(1, 0.3)).plus(startBorderPoint.center);
				Point p2 = Matrix.times(rotMat, new Point(1, -0.3)).plus(startBorderPoint.center);
				
				arrowPointer = new Triangle(p0, p1, p2);
				
			} else {
				
				arrowPointer = null;
				
			}
			
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public Direction getDirection(Axis a) {
		if (a == null) {
			return direction;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public Vertex getReferenceVertex(Axis a) {
		assert a == null;
		return start;
	}
	
	public Vertex getOtherVertex(Axis a) {
		assert a == null;
		return end;
	}
	
	public final Entity hitTest(Point p) {
		if (shape.hitTest(p)) {
			return this;
		} else {
			return null;
		}
	}
	
	public boolean canTravelFromTo(Vertex a, Vertex b) {
		if (a == start) {
			assert b == end;
			return direction != Direction.ENDTOSTART;
		} else {
			assert a == end;
			assert b == start;
			return direction != Direction.STARTTOEND;
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
			if (direction != null) {
				switch (direction) {
				case STARTTOEND:
					distances[start.id][end.id] = totalLength;
					break;
				case ENDTOSTART:
			    	distances[end.id][start.id] = totalLength;
					break;
				}
			} else {
				distances[start.id][end.id] = totalLength;
		    	distances[end.id][start.id] = totalLength;
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
	
	public GraphPosition travelFromReferenceVertex(Axis a, double dist) {
		return RoadPosition.travelFromStart(this, dist);
	}
	
	public GraphPosition travelFromOtherVertex(Axis a, double dist) {
		return RoadPosition.travelFromEnd(this, dist);
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
		
		if (startSign != null) {
			if (ShapeUtils.intersect(startSign.getShape(), s)) {
				return startSign;
			}
		}
		
		if (endSign != null) {
			if (ShapeUtils.intersect(endSign.getShape(), s)) {
				return endSign;
			}
		}
		
		return null;
		
	}
	
	public GraphPosition findSkeletonIntersection(Point c, Point d) {
		for (int i = 0; i < seq.capsuleCount(); i ++) {
			Capsule cap = seq.getCapsule(i);
			double abParam = cap.findSkeletonIntersection(c, d);
			if (abParam != -1) {
				if (DMath.equals(abParam, 1.0) && i < seq.capsuleCount()-1) {
					return new RoadPosition(this, i+1, 0.0);
				} else {
					return new RoadPosition(this, i, abParam);
				}
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
	
	
	
	
	public boolean isUserDeleteable() {
		return true;
	}
	
	
	/**
	 * segment index i
	 */
	public double getLengthFromStart(int i) {
		return cumulativeLengthsFromStart[i];
	}
	
	public void computeProperties() {
		
		computeCaps();
		
		/*
		 * after adjustToBorders, the border properties are set to predictable values
		 */
		if (!standalone) {
			startBorderIndex = 1;
			endBorderIndex = seq.capsuleCount()-1;
			
			startBorderPoint = new Circle(null, seq.getPoint(startBorderIndex), borderPointRadius);
			endBorderPoint = new Circle(null, seq.getPoint(endBorderIndex), borderPointRadius);
			
			assert DMath.equals(Point.distance(startBorderPoint.center, start.p), start.getRadius());
			assert DMath.equals(Point.distance(endBorderPoint.center, end.p), end.getRadius());
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
			caps.add(new Capsule(this, a, b, i));
		}
		
		seq = new CapsuleSequence(this, caps);
		
		shape = seq;
		
	}
	
	public CapsuleSequence getShape() {
		return shape;
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
			startBorderPoint = new Circle(null, Point.point(start.p, pts.get(startBorderIndex+1), startBorderParam), borderPointRadius);
		} else {
			startBorderIndex = (int)Math.floor(c);
			double startBorderParam = c-startBorderIndex;
			assert 0 <= startBorderParam && startBorderParam <= 1;
			startBorderPoint = new Circle(null, Point.point(pts.get(startBorderIndex), pts.get(startBorderIndex+1), startBorderParam), borderPointRadius);
		}
		
		c = endBorderCombo(end, pts);
		if (c >= pts.size()-1) {
			endBorderIndex = (int)Math.floor(c);
			assert endBorderIndex == pts.size()-1;
			double endBorderParam = c-endBorderIndex;
			assert 0 <= endBorderParam && endBorderParam <= 1;
			endBorderPoint = new Circle(null, Point.point(pts.get(endBorderIndex), end.p, endBorderParam), borderPointRadius);
		} else {
			endBorderIndex = (int)Math.floor(c);
			double endBorderParam = c-endBorderIndex;
			assert 0 <= endBorderParam && endBorderParam <= 1;
			endBorderPoint = new Circle(null, Point.point(pts.get(endBorderIndex), pts.get(endBorderIndex+1), endBorderParam), borderPointRadius);
		}
		
	}
	
	private List<Point> adjustToBorders(List<Point> pts) {
		
		List<Point> adj = new ArrayList<Point>();
		adj.add(start.p);
		adj.add(startBorderPoint.center);
		for (int i = startBorderIndex+1; i <= endBorderIndex; i++) {
			adj.add(pts.get(i));
		}
		adj.add(endBorderPoint.center);
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
	public void paint(RenderingContext ctxt) {
		
		paintPath(ctxt);
		
		if (ctxt.type == RenderingContextType.CANVAS) {
			if (APP.DEBUG_DRAW) {
				ctxt.setColor(Color.BLACK);
				ctxt.setWorldPixelStroke(1);
				shape.getAABB().draw(ctxt);	
			}
		}
		
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintHilite(RenderingContext ctxt) {
		ctxt.setColor(hiliteColor);
		ctxt.setWorldPixelStroke(1);
		drawPath(ctxt);
	}
	
	
	static java.awt.Stroke directionStroke = new BasicStroke(0.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	private void paintPath(RenderingContext ctxt) {
		
		ctxt.setColor(color);
		
		seq.paint(ctxt);
		
		if (ctxt.type == RenderingContextType.CANVAS) {
			if (direction != null) {
				
				ctxt.setStroke(directionStroke);
				ctxt.setColor(Color.LIGHT_GRAY);
				
				seq.drawSkeleton(ctxt);
				
				arrowPointer.paint(ctxt);
				
			}
		}
		
	}
	
	private void drawPath(RenderingContext ctxt) {
		seq.draw(ctxt);
	}
	
	/**
	 * @param g2 in pixels
	 */
	private void paintSkeleton(RenderingContext ctxt) {
		
		ctxt.setColor(Color.BLACK);
		
		seq.drawSkeleton(ctxt);
		
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintBorders(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GREEN);
		startBorderPoint.paint(ctxt);
		
		ctxt.setColor(Color.RED);
		endBorderPoint.paint(ctxt);
		
	}
	
	public void paintDecorations(RenderingContext ctxt) {
		
		if (ctxt.type == RenderingContextType.CANVAS) {
			
			if (!standalone) {
				
				startSign.paint(ctxt);
				
				endSign.paint(ctxt);
				
			}
			
			if (APP.DEBUG_DRAW) {
				
				paintSkeleton(ctxt);
				
			}
		}
		
	}
}
