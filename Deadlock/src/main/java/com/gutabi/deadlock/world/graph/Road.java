package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.Capsule;
import com.gutabi.deadlock.geom.CapsuleSequence;
import com.gutabi.deadlock.geom.CapsuleSequencePosition;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.geom.Geom;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.geom.Triangle;
import com.gutabi.deadlock.math.ColinearException;
import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;

public class Road extends Edge {
	
	public static final double ROAD_RADIUS = 0.5;
	
	public static final double borderPointRadius = 0.2;
	
	public World world;
	public final Vertex start;
	public final Vertex end;
	public final List<Point> raw;
	
	private Direction direction;
	
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
	
	private int hash;
	
	private CapsuleSequence shape;
	
	private Triangle arrowPointer;
	
	static Logger logger = Logger.getLogger(Road.class);
	
	public Road(World world, Vertex start, Vertex end, List<Point> raw) {
		
		assert !raw.isEmpty();
		
		this.world = world;
		this.start = start;
		this.end = end;
		this.raw = raw;
		
		loop = (start == end);
		standalone = (loop) ? start == null : false;
		
		computeProperties();
		
		if (!standalone) {
			
			start.roads.add(this);
			end.roads.add(this);
			
			startSign = new StopSign(this, 0);
			endSign = new StopSign(this, 1);
			
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
				
				Point p0 = Geom.times(rotMat, new Point(0, 0)).plus(endBorderPoint.center);
				Point p1 = Geom.times(rotMat, new Point(-1, 0.3)).plus(endBorderPoint.center);
				Point p2 = Geom.times(rotMat, new Point(-1, -0.3)).plus(endBorderPoint.center);
				
				arrowPointer = new Triangle(p0, p1, p2);
				
			} else if (dir == Direction.ENDTOSTART) {
				
				Capsule c = seq.getCapsule(1);
				
				double angle = Math.atan2(c.b.y-c.a.y, c.b.x-c.a.x);
				
				double[][] rotMat = new double[][]{ { Math.cos(angle), -Math.sin(angle) }, { Math.sin(angle), Math.cos(angle) } };
				
				Point p0 = Geom.times(rotMat, new Point(0, 0)).plus(startBorderPoint.center);
				Point p1 = Geom.times(rotMat, new Point(1, 0.3)).plus(startBorderPoint.center);
				Point p2 = Geom.times(rotMat, new Point(1, -0.3)).plus(startBorderPoint.center);
				
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
	
	public Entity decorationsIntersect(Shape s) {
		
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
		CapsuleSequencePosition pos = seq.findSkeletonIntersection(c, d);
		if (pos != null) {
			return new RoadPosition(this, pos.index, pos.param);
		}
		return null;
	}
	
	public RoadPosition findClosestRoadPosition(Point p, double r) {
		CapsuleSequencePosition pos = seq.findClosestStrokePosition(p, r);
		if (pos != null) {
			if (DMath.equals(pos.param, 1.0) && pos.index == seq.capsuleCount-1) {
				return null;
			} else if (pos.index == 0 && DMath.equals(pos.param, 0.0)) {
				return null;
			} else {
				return new RoadPosition(this, pos.index, pos.param);
			}
		}
		return null;
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
	
	public String toFileString() {
		
		StringBuilder s = new StringBuilder();
		
		s.append("start road\n");
		
		s.append("id " + id + "\n");
		
		s.append("start " + start.id + "\n");
		s.append("end " + end.id + "\n");
		
		s.append("start points\n");
		for (Point p : raw) {
			s.append(p.toFileString() + "\n");
		}
		s.append("end points\n");
		
		s.append("direction " + direction + "\n");
		s.append("startSign " + startSign.isEnabled() + "\n");
		s.append("endSign " + endSign.isEnabled() + "\n");
		
		s.append("end road\n");
		
		return s.toString();
	}
	
	public static Road fromFileString(World world, Vertex[] vs, String s) {
		BufferedReader r = new BufferedReader(new StringReader(s));
		
		int id = -1;
//		int start = 0;
//		int end = 0;
		Vertex start = null;
		Vertex end = null;
		List<Point> pts = new ArrayList<Point>();
		Direction d = null;
		boolean startSignEnabled = false;
		boolean endSignEnabled = false;
		
		
		try {
			String l = r.readLine();
			assert l.equals("start road");
			
			l = r.readLine();
			Scanner sc = new Scanner(l);
			String tok = sc.next();
			assert tok.equals("id");
			id = sc.nextInt();
			sc.close();
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("start");
			int startID = sc.nextInt();
			start = vs[startID];
			assert start != null;
			sc.close();
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("end");
			int endID = sc.nextInt();
			end = vs[endID];
			assert end != null;
			sc.close();
			
			l = r.readLine();
			assert l.equals("start points");
			
			while (true) {
				
				l = r.readLine();
				
				if (l.equals("end points")) {
					break;
				} else {
					
					Point p = Point.fromFileString(l);
					pts.add(p);
					
				}
				
			}
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("direction");
			String rest = sc.next();
			d = Direction.fromFileString(rest);
			sc.close();
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("startSign");
			startSignEnabled = sc.nextBoolean();
			sc.close();
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("endSign");
			endSignEnabled = sc.nextBoolean();
			sc.close();
			
			l = r.readLine();
			assert l.equals("end road");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Road rd = new Road(world, start, end, pts);
		
		rd.id = id;
		rd.direction = d;
		if (startSignEnabled) {
			rd.startSign.setEnabled(true);
		}
		if (endSignEnabled) {
			rd.endSign.setEnabled(true);
		}
		return rd;
	}
	
	
	public void paint_panel(RenderingContext ctxt) {
		
		paintPath_panel(ctxt);
		
		if (APP.DEBUG_DRAW) {
			ctxt.setColor(Color.BLACK);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			shape.getAABB().draw(ctxt);
		}
		
	}
	
	public void paint_preview(RenderingContext ctxt) {
		
		paintPath_preview(ctxt);
		
	}
	
	public void paintHilite(RenderingContext ctxt) {
		ctxt.setColor(Color.roadHiliteColor);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		drawPath(ctxt);
	}
	
	private void paintPath_panel(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GRAY);
		
		seq.paint(ctxt);
		
		if (direction != null) {
			
			ctxt.setStroke(0.1, Cap.ROUND, Join.ROUND);
			ctxt.setColor(Color.LIGHT_GRAY);
			
			seq.drawSkeleton(ctxt);
			
			arrowPointer.paint(ctxt);
			
		}
		
	}
	
	private void paintPath_preview(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GRAY);
		
		seq.paint(ctxt);
		
	}
	
	private void drawPath(RenderingContext ctxt) {
		seq.draw(ctxt);
	}
	
	private void paintSkeleton(RenderingContext ctxt) {
		
		ctxt.setColor(Color.BLACK);
		
		seq.drawSkeleton(ctxt);	
	}
	
	public void paintBorders(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GREEN);
		startBorderPoint.paint(ctxt);
		
		ctxt.setColor(Color.RED);
		endBorderPoint.paint(ctxt);	
	}
	
	public void paintDecorations(RenderingContext ctxt) {
		
		if (startSign != null) {
			startSign.paint(ctxt);
		}
		
		if (endSign != null) {
			endSign.paint(ctxt);
		}
		
		if (APP.DEBUG_DRAW) {
			paintSkeleton(ctxt);
		}
		
	}
}
