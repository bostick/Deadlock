package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class Graph {
	
	private final ArrayList<Edge> edges = new ArrayList<Edge>();
	private final ArrayList<Intersection> intersections = new ArrayList<Intersection>();
	private final QuadTree segTree = new QuadTree();
	
	private final ArrayList<Source> sources = new ArrayList<Source>();
	private final ArrayList<Sink> sinks = new ArrayList<Sink>();
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	public List<Intersection> getIntersections() {
		return intersections;
	}
	
	public QuadTree getSegmentTree() {
		return segTree;
	}
	
	public List<Source> getSources() {
		return sources;
	}
	
	public List<Sink> getSinks() {
		return sinks;
	}
	
	
	public void clear() {
		edges.clear();
		intersections.clear();
		segTree.clear();
	}
	
	private Edge createEdge(Vertex start, Vertex end, List<Point> pts) {
		Edge e = new Edge(start, end, pts);
		edges.add(e);
		segTree.addEdge(e);
		
		if (!e.isStandAlone()) {
			start.addEdge(e);
			end.addEdge(e);
		}
		
		return e;
	}
	
	private void edgesChanged(Vertex v) {
		
		if (v instanceof Intersection) {
			
			List<Edge> eds = v.getEdges();
			if (eds.size() == 0) {
				destroyVertex(v);
			} else if (eds.size() == 2) {
				merge(eds.get(0), eds.get(1), v);
			}
			
		} else if (v instanceof Source) {
			
			/*
			 * sources stay around
			 */
			
		} else if (v instanceof Sink) {
			
			/*
			 * sinks stay around
			 */
			
		} else {
			assert false;
		}
		
	}
	
	private Intersection createIntersection(Point p) {
		Intersection i = new Intersection(p);
		intersections.add(i);
		
		return i;
	}
	
	public void addSource(Point p) {
		sources.add(new Source(p));
	}
	
	public void addSink(Point p) {
		sinks.add(new Sink(p));
	}
	
	private void destroyVertex(Vertex v) {
		
		if (intersections.contains(v)) {
			intersections.remove(v);
		} else if (sources.contains(v)) {
			sources.remove(v);
		} else if (sinks.contains(v)) {
			sinks.remove(v);
		} else {
			assert false;
		}
		
		v.remove();
	}
	
	private void destroyEdge(Edge e) {
		assert edges.contains(e);
		
		if (!e.isStandAlone()) {
			e.getStart().removeEdge(e);
			e.getEnd().removeEdge(e);
		}
		
		edges.remove(e);
		segTree.removeEdge(e);
		e.remove();
	}
	
	
	double[][] distances;
	Vertex[][] next;
	
	public void calculateChoices() {
		
		/*
		 * Floyd-Warshall
		 */
		
		int vertexCount = intersections.size() + sources.size() + sinks.size();
		Vertex[] vertices = new Vertex[vertexCount];
		
		int id = 0;
		for (Intersection i : intersections) {
			i.graphID = id;
			vertices[id] = i;
			id++;
		}
		for (Source s : sources) {
			s.graphID = id;
			vertices[id] = s;
			id++;
		}
		for (Sink s : sinks) {
			s.graphID = id;
			vertices[id] = s;
			id++;
		}
		
		distances = new double[vertexCount][vertexCount];
		next = new Vertex[vertexCount][vertexCount];
		
		for (int i = 0; i < distances.length; i++) {
			Arrays.fill(distances[i], Double.POSITIVE_INFINITY);
		}
		for (int i = 0; i < distances.length; i++) {
			distances[i][i] = 0.0;
		}
		for(Edge e : edges) {
			double l = e.getTotalLength();
			double cur = distances[e.getStart().graphID][e.getEnd().graphID];
			/*
			 * there may be multiple edges between start and end, so don't just blindly set it to l
			 */
			if (l < cur) {
				distances[e.getStart().graphID][e.getEnd().graphID] = l;
		    	distances[e.getEnd().graphID][e.getStart().graphID] = l;
			}
		}
		
		for (int k = 0; k < vertexCount; k++){
			for (int i = 0; i < vertexCount; i++){
				for (int j = 0; j < vertexCount; j++){
					if (distances[i][k] + distances[k][j] < distances[i][j]) {
						distances[i][j] = distances[i][k] + distances[k][j];
						next[i][j] = vertices[k];
					}
				}
			}
		}
		
	}
	
	public Path shortestPath(final Vertex start, Vertex end) {
		if (start == end) {
			throw new IllegalArgumentException();
		}
		
		int sid = start.graphID;
		int eid = end.graphID;
		
		if (distances[sid][eid] == Double.POSITIVE_INFINITY) {
			return null;
		}
		
		List<Vertex> vs = new ArrayList<Vertex>();
		vs.add(start);
		vs.addAll(intermediateVertices(start, end));
		vs.add(end);
		
		List<Position> poss = new ArrayList<Position>();
		poss.add(new VertexPosition(start, null, null, 0));
		for (int i = 1; i < vs.size(); i++) {
			Vertex a = vs.get(i-1);
			Vertex b = vs.get(i);
			
			double d = distances[a.graphID][b.graphID];
			
			Edge ee = null;
			for (Edge e : Vertex.commonEdges(a, b)) {
				if (DMath.doubleEquals(e.getTotalLength(), d)) {
					ee = e;
					break;
				}
			}
			assert ee != null;
			
			poss.add(new VertexPosition(b, poss.get(i-1), ee, (a == ee.getStart() ? 1 : -1)));
		}
		
		return new Path(poss);
	}
	
	private List<Vertex> intermediateVertices(Vertex start, Vertex end) {
		
		int sid = start.graphID;
		int eid = end.graphID;
		
		Vertex n = next[sid][eid];
		
		if (n == null) {
			return new ArrayList<Vertex>();
		}
		
		List<Vertex> vs = new ArrayList<Vertex>();
		vs.addAll(intermediateVertices(start, n));
		vs.add(n);
		vs.addAll(intermediateVertices(n, end));
		
		return vs;
	}
	
	
	
	
	
	
	public EdgePosition tryFindEdgePosition(Point b) {
		for (Segment in : segTree.findAllSegments(b)) {
			Edge e = in.edge;
			int i = in.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			if (Point.intersect(b, c, d) && !Point.equals(b, d)) {
				return new EdgePosition(e, i, Point.param(b, c, d), null, null, 0);
			}
		}
		for (Intersection v : getIntersections()) {
			Point d = v.getPoint();
			if (Point.equals(b, d)) {
				throw new IllegalArgumentException("point is on intersection");
			}
		}
		return null;
	}
	
	public int findIndex(Edge e, Point b) {
		assert !e.isRemoved();
		
		for (int i = 0; i < e.size(); i++) {
			Point d = e.getPoint(i);
			if (b.equals(d)) {
				return i;
			}
		}
		return -1;
	}
	
	public Vertex tryFindVertex(Point b) {
		for (Intersection v : intersections) {
			Point p = v.getPoint();
			if (Point.equals(b, p)) {
				return v;
			}
		}
		for (Source s : sources) {
			Point p = s.getPoint();
			if (Point.equals(b, p)) {
				return s;
			}
		}
		for (Sink s : sinks) {
			Point p = s.getPoint();
			if (Point.equals(b, p)) {
				return s;
			}
		}
		return null;
	}
	
	/**
	 * returns the closest intersection within radius that is not the excluded point
	 * 
	 */
	public VertexPosition findClosestVertexPosition(Point a, Point anchor, double radius) {
		Vertex anchorV = null;
		if (anchor != null) {
			anchorV = tryFindVertex(anchor);
		}
		
		Vertex closest = null;
		
		for (Intersection i : intersections) {
			Point ip = i.getPoint();
			if (anchorV != null && Point.equals(a, anchor) && i == anchorV) {
				/*
				 * use the excluded intersection
				 */
				closest = i;
				break;
			}
			if (anchorV != null && !Point.equals(a, anchor) && i == anchorV && Point.distance(a, anchor) < radius) {
				/*
				 * ignore the excluded intersection
				 */
				continue;
			}
			double dist = Point.distance(a, ip);
			if (dist < radius && (anchorV == null || dist < Point.distance(ip, anchor))) {
				if (closest == null) {
					closest = i;
				} else if (Point.distance(a, ip) < Point.distance(a, closest.getPoint())) {
					closest = i;
				}
			}	
		}
		
		for (Source s : sources) {
			Point sp = s.getPoint();
			if (anchorV != null && Point.equals(a, anchor) && s == anchorV) {
				closest = s;
				break;
			}
			if (anchorV != null && !Point.equals(a, anchor) && s == anchorV && Point.distance(a, anchor) < radius) {
				continue;
			}
			double dist = Point.distance(a, sp);
			if (dist < radius && (anchorV == null || dist < Point.distance(sp, anchor))) {
				if (closest == null) {
					closest = s;
				} else if (Point.distance(a, sp) < Point.distance(a, closest.getPoint())) {
					closest = s;
				}
			}	
		}
		
		for (Sink s : sinks) {
			Point sp = s.getPoint();
			if (anchorV != null && Point.equals(a, anchor) && s == anchorV) {
				closest = s;
				break;
			}
			if (anchorV != null && !Point.equals(a, anchor) && s == anchorV && Point.distance(a, anchor) < radius) {
				continue;
			}
			double dist = Point.distance(a, sp);
			if (dist < radius && (anchorV == null || dist < Point.distance(sp, anchor))) {
				if (closest == null) {
					closest = s;
				} else if (Point.distance(a, sp) < Point.distance(a, closest.getPoint())) {
					closest = s;
				}
			}	
		}
		
		if (closest != null) {
			return new VertexPosition(closest, null, null, 0);
		} else {
			return null;
		}
	}
	
	/**
	 * returns the closest edge within radius that is not in the excluded radius
	 */
	public EdgePosition findClosestEdgePosition(Point a, Point exclude, double radius) {
		return getSegmentTree().findClosestEdgePosition(a, exclude, radius);
	}
	
	public void processNewStroke(List<Point> stroke) {
		addStroke(stroke, true);
		cleanupEdges();
	}
	
//	private List<Point> cullAngles(List<Point> stroke) {
//		
//		List<Point> stroke2 = new ArrayList<Point>();
//		
//		/*
//		 * initialize
//		 */
//		Point a = stroke.get(0);
//		Point b = stroke.get(1);
//		stroke2.add(a);
//		stroke2.add(b);
//		
//		for (int i = 2; i < stroke.size(); i++) {
//			
//			Point aa = stroke2.get(stroke2.size()-2);
//			
//			// last good point added
//			a = stroke2.get(stroke2.size()-1);
//			
//			double rad1 = Math.atan2(a.getY()-aa.getY(), a.getX()-aa.getX());
//			
//			b = stroke.get(i);
//			double rad2 = Math.atan2(b.getY()-a.getY(), b.getX()-a.getX());
//			
//			double rad = rad2 - rad1;
//			while (rad > Math.PI) {
//				rad -= 2 * Math.PI;
//			}
//			while (rad < -Math.PI) {
//				rad += 2 * Math.PI;
//			}
//			double sharpRight = -2.35;
//			double sharpLeft = 2.35;
//			
//			if (rad > sharpRight && rad < sharpLeft) {
//				stroke2.add(b);
//			} else {
//				//String.class.getName();
//			}
//			
//		}
//		
//		return stroke2;
//	}
	
	/**
	 * handleClose is needed since when adjusting edges to the grid, we want to stay exact, and ignore worrying about "closeness" 
	 */
	public void addStroke(List<Point> stroke, boolean handleClose) {
		
		Point a;
		Point b;
		
		for (int i = 0; i < stroke.size()-1; i++) {
			a = stroke.get(i);
			b = stroke.get(i+1);
			assert !Point.equals(a, b);
			
			List<PointToBeAdded> betweenABPoints = splitOnEvents(a, b);

			for (int j = 0; j < betweenABPoints.size()-1; j++) {
				PointToBeAdded ptba1 = betweenABPoints.get(j);
				PointToBeAdded ptba2 = betweenABPoints.get(j+1);
				
				Point p1 = ptba1.p;
				Point p2 = ptba2.p;
				
				if (handleClose) {
					Point[] newSegment;
					newSegment = handleIntersections(p1, p2);
					if (newSegment[0] == null && newSegment[1] == null) {
						/*
						 * <a, b> is too close to another edge, so it is being skipped
						 */
						continue;
					}
					p1 = newSegment[0];
					p2 = newSegment[1];
				}
				
//				List<PointToBeAdded> betweenP1P2Points = splitOnEvents(p1, p2);
//				assert betweenP1P2Points.size() == 2;
				
				final Point p1Int = p1.toInteger();
				
				final Point p2Int = p2.toInteger();
				
				if (!Point.equals(p1Int, p2Int)) {
					
					boolean p1AdjustResult = false;
					boolean p2AdjustResult = false;
					
					if (!Point.equals(p1, p1Int) && tryFindEdgePosition(p1) != null) {
						p1AdjustResult = adjustEdgePointToGrid(p1);
					}
					
					if (!Point.equals(p2, p2Int) && tryFindEdgePosition(p2) != null) {
						p2AdjustResult = adjustEdgePointToGrid(p2);
					}
					
					if (!p1AdjustResult && !p2AdjustResult) {
						
						if (!segmentOverlaps(p1Int, p2Int)) {
							addSegment(p1Int, p2Int);
						}
						
					} else {
						
						List<Point> recurStroke = new ArrayList<Point>();
						recurStroke.add(p1Int);
						recurStroke.add(p2Int);
						addStroke(recurStroke, true);
						
					}
					
				}
				
			}
		}
	}
	
	/*
	 * deal with curvature and intersections here
	 */
	private Point[] handleIntersections(Point a, Point b) {
		
		Point[] ret = new Point[2];
		Position closestA;
		Position closestB;
		
		closestA = closestPosition(a, a, 10);
		closestB = closestPosition(b, a, 10);
		
		if (closestA != null) {
			if (closestB != null) {
				
				if (Point.distance(closestA.getPoint(), closestB.getPoint()) > 10) {
					
					ret[0] = closestA.getPoint();
					ret[1] = closestB.getPoint();
					return ret;
					
				} else if (closestA instanceof VertexPosition && closestB instanceof VertexPosition && !Point.equals(closestA.getPoint(), closestB.getPoint())) {
					
					/*
					 * even if they are close together, connect vertices
					 */
					
					ret[0] = closestA.getPoint();
					ret[1] = closestB.getPoint();
					return ret;
					
				} else {
					
					ret[0] = null;
					ret[1] = null;
					return ret;
					
				}
				
			} else {
				ret[0] = closestA.getPoint();
				ret[1] = b;
				
				assert !Point.equals(ret[0], ret[1]);
				return ret;
			}
		} else {
			if (closestB != null) {
				ret[0] = a;
				ret[1] = closestB.getPoint();
				
				assert !Point.equals(ret[0], ret[1]);
				return ret;
				
			} else {
				ret[0] = a;
				ret[1] = b;
				
				assert !Point.equals(ret[0], ret[1]);
				return ret;
			}
		}
		
	}
	
	public Position closestPosition(Point a) {
		return closestPosition(a, null, Double.POSITIVE_INFINITY);
	}
	
	public Position closestPosition(Point a, double radius) {
		return closestPosition(a, null, radius);
	}
	
	public Position closestPosition(Point a, Point exclude, double radius) {
		VertexPosition closestV = findClosestVertexPosition(a, exclude, radius);
		if (closestV != null) {
			return closestV;
		}
		EdgePosition closestEdge = findClosestEdgePosition(a, exclude, radius);
		if (closestEdge != null) {
			return closestEdge;
		}
		return null;
	}
	
	private List<PointToBeAdded> splitOnEvents(Point a, Point b) {
		
		List<PointToBeAdded> betweenABPoints = new ArrayList<PointToBeAdded>();
		
		betweenABPointsAdd(betweenABPoints, new PointToBeAdded(a, 0.0));
		betweenABPointsAdd(betweenABPoints, new PointToBeAdded(b, 1.0));
		
		for (Segment in : segTree.findAllSegments(a, b)) {
			Edge e = in.edge;
			int i = in.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			
			try {
				Point inter = Point.intersection(a, b, c, d);
				if (inter != null) {
					/*
					 * an intersection event
					 */
					PointToBeAdded nptba = new PointToBeAdded(inter, Point.param(inter, a, b));
					betweenABPointsAdd(betweenABPoints, nptba);
				}
			} catch (OverlappingException ex) {
				
				if (Point.intersect(c, a, b) && !Point.equals(c, b)) {
					/*
					 * an overlapping event
					 */
					PointToBeAdded nptba = new PointToBeAdded(c, Point.param(c, a, b));
					betweenABPointsAdd(betweenABPoints, nptba);
				}
				
				if (Point.intersect(d, a, b) && !Point.equals(d, b)) {
					/*
					 * an overlapping event
					 */
					PointToBeAdded nptba = new PointToBeAdded(d, Point.param(d, a, b));
					betweenABPointsAdd(betweenABPoints, nptba);
				}
				
			}
			
			/*
			 * find too-close to other segments events
			 * start and end of being too close
			 */
			//d;
			
		}
		
		return betweenABPoints;
	}
	
	private static void betweenABPointsAdd(List<PointToBeAdded> ls, PointToBeAdded ptba) {
		int i = Collections.binarySearch(ls, ptba, PointToBeAdded.COMPARATOR);
		if (i < 0) {
			/*
			 * not found
			 */
			int insertionPoint = -(i+1);
			ls.add(insertionPoint, ptba);
		}
	}
	
	private boolean segmentOverlaps(Point a, Point b) {
		
		for (Segment in : segTree.findAllSegments(a, b, 10)) {
			Edge e = in.edge;
			int i = in.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			try {
				Point.intersection(a, b, c, d);
			} catch (OverlappingException e1) {
				return true;
			}
		}
		
		return false;
		
	}
	
	private void cleanupEdges() {
		
		List<Edge> toRemove = new ArrayList<Edge>();
		boolean changed;
		
		while (true) {
			toRemove.clear();
			changed = false;
			
			for (Edge e : edges) {
				if (e.getTotalLength() <= 10) {
					toRemove.add(e);
					changed = true;
				}
			}
			
			for (Edge e : toRemove) {
				
				if (e.isRemoved()) {
					/*
					 * may be removed from having been merged in a previous iteration
					 */
					continue;
				}
				
				removeEdgeTop(e);
				
			}
			
			if (!changed) {
				break;
			}
		}
		
	}
	
	private void addSegment(Point a, Point b) {
			
		assert !Point.equals(a, b);
		assert !segmentExists(a, b) : "segment " + a + " " + b + " already exists";
		
		assert a.isInteger();
		assert b.isInteger();
		
		Vertex tmp = tryFindVertex(a);
		final Vertex aV;
		if (tmp == null) {
			EdgePosition intInfo = tryFindEdgePosition(a);
			if (intInfo == null) {
				aV = createIntersection(a);
			} else {
				aV = split(a);
			}
		} else {
			aV = tmp;
		}
		
		tmp = tryFindVertex(b);
		final Vertex bV;
		if (tmp == null) {
			EdgePosition intInfo = tryFindEdgePosition(b);
			if (intInfo == null) {
				bV = createIntersection(b);
			} else {
				bV = split(b);
			}
		} else {
			bV = tmp;
		}
		
		createEdge(aV, bV, new ArrayList<Point>(){{add(aV.getPoint());add(bV.getPoint());}});
		
		edgesChanged(aV);
		edgesChanged(bV);
	}
	
	/**
	 * not exactly like tryFindSegment
	 * returns true even if <a, b> is between <c, d>
	 */
	private boolean segmentExists(Point a, Point b) {
		for (Segment in : segTree.findAllSegments(a, b)) {
			Edge e = in.edge;
			int i = in.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			if (Point.intersect(a, c, d) && Point.intersect(b, c, d)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * split an edge at point
	 * Edge e will have been removed
	 * return Intersection at split point
	 */
	private Vertex split(final Point p) {
		
		EdgePosition info = tryFindEdgePosition(p);
		assert info != null;
		
		Edge e = info.getEdge();
		int index = info.index;
		double param = info.param;
		
		assert param >= 0.0;
		assert param < 1.0;
		
		/*
		 * we assert that param < 1.0, but after adjusting, we may be at d
		 */
		
		final Point c = e.getPoint(index);
		final Point d = e.getPoint(index+1);
		
		/*
		 * v may already exist if we are splitting at p and p is different than pInt and v already exists at pInt
		 */
		Vertex v = tryFindVertex(p);
		assert v == null;
		v = createIntersection(p);
		
		Vertex eStart = e.getStart();
		Vertex eEnd = e.getEnd();
		
		if (eStart == null && eEnd == null) {
			// stand-alone loop
			
			List<Point> pts = new ArrayList<Point>();
			
			pts.add(p);
			try {
				if (index+1 < e.size()-1 && !Point.colinear(p, d, e.getPoint(index+2))) {
					pts.add(d);
				}
			} catch (ColinearException ex) {
				assert false;
			}
			for (int i = index+2; i < e.size()-1; i++) {
				pts.add(e.getPoint(i));
			}
			for (int i = 0; i < index; i++) {
				pts.add(e.getPoint(i));
			}
			try {
				if (index > 0 && !Point.colinear(e.getPoint(index-1), c, p)) {
					pts.add(c);
				}
			} catch (ColinearException ex) {
				assert false;
			}
			pts.add(p);
			
			createEdge(v, v, pts);
			
			destroyEdge(e);
			
			return v;
			
		}
			
		/*
		 * f1
		 */
		
		Edge f1 = null;
		for (Edge ee : eStart.getEdges()) {
			if (((ee.getStart() == eStart && ee.getEnd() == v) || (ee.getStart() == v && ee.getEnd() == eStart)) && ee.size() == 2) {
				/*
				 * both a and b are already both intersections and connected, so just use this
				 */
				f1 = ee;
				break;
			}
		}
		if (f1 == null) {
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = 0; i < index; i++) {
				pts.add(e.getPoint(i));
			}
			try {
				if (!(index > 0) || !Point.colinear(e.getPoint(index-1), c, p)) {
					pts.add(c);
				}
			} catch (ColinearException ex) {
				
				assert tryFindVertex(c) == null;
				
				Intersection cV = createIntersection(c);
				createEdge(cV, v,  new ArrayList<Point>(){{add(c);add(p);}});
				
			}
			pts.add(p);
			
			createEdge(eStart, v, pts);
		}
		
		/*
		 * f2
		 */
		
		Edge f2 = null;
		for (Edge ee : eStart.getEdges()) {
			if (ee == f1) {
				/*
				 * ignore if ee is the newly-created edge f1
				 */
				continue;
			}
			if (((ee.getStart() == v && ee.getEnd() == eEnd) || (ee.getStart() == eEnd && ee.getEnd() == v)) && ee.size() == 2) {
				/*
				 * both a and b are already both intersections and connected, so just use this
				 */
				f2 = ee;
				break;
			}
		}
		if (f2 == null) {
			
			List<Point> pts = new ArrayList<Point>();
			
			pts.add(p);
			try {
				if (!(index+1 < e.size()-1) || !Point.colinear(p, d, e.getPoint(index+2))) {
					pts.add(d);
				}
			} catch (ColinearException ex) {
				
				assert tryFindVertex(d) == null;
				
				Intersection dV = createIntersection(d);
				createEdge(dV, v,  new ArrayList<Point>(){{add(d);add(p);}});
				
			}
			for (int i = index+2; i < e.size(); i++) {
				pts.add(e.getPoint(i));
			}
			
			createEdge(v, eEnd, pts);
		}
		
		destroyEdge(e);
		
		return v;
	}
	
	/**
	 * split an edge at point with index i and param p between indices i and i+1
	 * takes care of adjusting to integer coordinates
	 * Edge e will have been removed
	 */
	private boolean adjustEdgePointToGrid(Point p) {
		
		try {
			
			EdgePosition info = tryFindEdgePosition(p);
			assert info != null;
			
			Edge e = info.getEdge();
			int index = info.index;
			double param = info.param;
			
			assert param >= 0.0;
			assert param < 1.0;
			
			/*
			 * we assert thar param < 1.0, but after adjusting, we may be at d
			 */
			
			final Point c = e.getPoint(index);
			final Point d = e.getPoint(index+1);
			
			final Point pInt = p.toInteger();
			
			assert !Point.equals(p, pInt);
			
			if (Point.equals(c, pInt) || Point.equals(d, pInt)) {
				/*
				 * nothing being adjusted
				 */
				return false;
			}
			
			/*
			 * p and pInt, while not equal, could both be on <c, d>, so adjusting ultimately would have no effect
			 */
			if (Point.intersect(pInt, c, d)) {
				return false;
			}
			
			/*
			 * adjust segment to integer coords first
			 */
			removeSegment(c, d);
			
			info = tryFindEdgePosition(p);
			assert info == null;
			
			if (!Point.equals(c, pInt)) {
				/*
				 * the segment <c, pInt> may intersect with other segments, so we have to start fresh and
				 * not assume anything
				 */
				addStroke(new ArrayList<Point>(){{add(c);add(pInt);}}, false);
			}
			if (!Point.equals(pInt, d)) {
				/*
				 * the segment <pInt, d> may intersect with other segments, so we have to start fresh and
				 * not assume anything
				 */
				addStroke(new ArrayList<Point>(){{add(pInt);add(d);}}, false);
			}
			
			info = tryFindEdgePosition(p);
			assert info == null;
			
			return true;
			
		} finally {
			
		}
		
	}
	
	/**
	 * merge two edges (possibly the same edge) at the given intersection
	 * 
	 * no colinear points in returned edge
	 */
	private void merge(Edge e1, Edge e2, Vertex v) {
		assert !e1.isRemoved();
		assert !e2.isRemoved();
		assert !v.isRemoved();
		
		Vertex e1Start = e1.getStart();
		Vertex e1End = e1.getEnd();
		
		Vertex e2Start = e2.getStart();
		Vertex e2End = e2.getEnd();
		
		assert e1Start != null;
		assert e1End != null;
		assert e2Start != null;
		assert e2End != null;
		assert v != null;
		
		assert v == e1Start || v == e1End;
		assert v == e2Start || v == e2End;
		
		if (e1 == e2) {
			// in the middle of merging a stand-alone loop
			assert v == e1Start && v == e1End;
			assert v.getEdges().size() == 2;
				
			List<Point> pts = new ArrayList<Point>();
			
			int n = e1.size();
			assert e1.getPoint(0) == e1.getPoint(n-1);
			
			// only add if not colinear
			try {
				if (!Point.colinear(e1.getPoint(e1.size()-2), e1.getPoint(0), e1.getPoint(1))) {
					pts.add(e1.getPoint(0));
				}
			} catch (ColinearException ex) {
				assert false;
			}
			for (int i = 1; i < e1.size()-1; i++) {
				pts.add(e1.getPoint(i));
			}
			/*
			 * add whatever the first point is
			 */
			pts.add(pts.get(0));
			
			createEdge(null, null, pts);
			
			destroyEdge(e1);
			
			destroyVertex(v);
			
		} else if (v == e1Start && v == e2Start) {
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = e1.size()-1; i > 0; i--) {
				pts.add(e1.getPoint(i));
			}
			assert Point.equals(e1.getPoint(0), e2.getPoint(0));
			try {
				// only add if not colinear
				if (!Point.colinear(e1.getPoint(1), e1.getPoint(0), e2.getPoint(1))) {
					pts.add(e1.getPoint(0));
				}
			} catch (ColinearException ex) {
				assert false;
			}
			for (int i = 1; i < e2.size(); i++) {
				pts.add(e2.getPoint(i));
			}
			
			createEdge(e1End, e2End, pts);
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		} else if (v == e1Start && v == e2End) {
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = e1.size()-1; i > 0; i--) {
				pts.add(e1.getPoint(i));
			}
			assert Point.equals(e1.getPoint(0), e2.getPoint(e2.size()-1));
			try {
				// only add if not colinear
				if (!Point.colinear(e1.getPoint(1), e1.getPoint(0), e2.getPoint(e2.size()-2))) {
					pts.add(e1.getPoint(0));
				}
			} catch (ColinearException ex) {
				assert false;
			}
			for (int i = e2.size()-2; i >= 0; i--) {
				pts.add(e2.getPoint(i));
			}
			
			createEdge(e1End, e2Start, pts);
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		} else if (v == e1End && v == e2Start) {
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = 0; i < e1.size()-1; i++) {
				pts.add(e1.getPoint(i));
			}
			assert Point.equals(e1.getPoint(e1.size()-1), e2.getPoint(0));
			try {
				// only add if not colinear
				if (!Point.colinear(e1.getPoint(e1.size()-2), e1.getPoint(e1.size()-1), e2.getPoint(1))) {
					pts.add(e1.getPoint(e1.size()-1));
				}
			} catch (ColinearException ex) {
				assert false;
			}
			for (int i = 1; i < e2.size(); i++) {
				pts.add(e2.getPoint(i));
			}
			
			createEdge(e1Start, e2End, pts);
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		} else {
			assert v == e1End && v == e2End;
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = 0; i < e1.size()-1; i++) {
				pts.add(e1.getPoint(i));
			}
			assert Point.equals(e1.getPoint(e1.size()-1), e2.getPoint(e2.size()-1));
			try {
				// only add if not colinear
				if (!Point.colinear(e1.getPoint(e1.size()-2), e1.getPoint(e1.size()-1), e2.getPoint(e2.size()-2))) {
					pts.add(e1.getPoint(e1.size()-1));
				}
			} catch (ColinearException ex) {
				assert false;
			}
			for (int i = e2.size()-2; i >= 0; i--) {
				pts.add(e2.getPoint(i));
			}
			
			createEdge(e1Start, e2Start, pts);
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		}
	}
	
	
	/**
	 * remove segment <i, i+1> from edge e
	 * 
	 * post: e has been removed
	 */
	private void removeSegment(Point a, Point b) {
			
		Segment info = segTree.findSegment(a, b);
		Edge e = info.edge;
		int index = info.index;
		
		Vertex eStart = e.getStart();
		Vertex eEnd = e.getEnd();
		
		if (eStart == null && eEnd == null) {
			// stand-alone loop
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = index+1; i < e.size(); i++) {
				pts.add(e.getPoint(i));
			}
			for (int i = 1; i <= index; i++) {
				pts.add(e.getPoint(i));
			}
			
			Intersection newStart = createIntersection(e.getPoint(index+1));
			Intersection newEnd = createIntersection(e.getPoint(index));
			
			createEdge(newStart, newEnd, pts);
			
			destroyEdge(e);
			
		} else {
			
			if (index == 0) {
				
				if (e.size() > 2) {
					
					List<Point> pts = new ArrayList<Point>();
					
					for (int i = 1; i < e.size(); i++) {
						pts.add(e.getPoint(i));
					}
					
					Intersection newStart = createIntersection(e.getPoint(1));
					
					createEdge(newStart, eEnd, pts);
				}
				
			} else if (index == e.size()-2) {
				
				List<Point> pts = new ArrayList<Point>();
				
				for (int i = 0; i < e.size()-1; i++) {
					pts.add(e.getPoint(i));
				}
				
				Intersection newEnd = createIntersection(e.getPoint(e.size()-2));
				
				createEdge(eStart, newEnd, pts);
				
			} else {
				//create 2 new edges without worrying about intersection
				
				List<Point> f1Pts = new ArrayList<Point>();
				
				for (int i = 0; i <= index; i++) {
					f1Pts.add(e.getPoint(i));
				}
				
				Intersection newF1End = createIntersection(e.getPoint(index));
				
				createEdge(eStart, newF1End, f1Pts);
				
				List<Point> f2Pts = new ArrayList<Point>();
				
				for (int i = index+1; i < e.size(); i++) {
					f2Pts.add(e.getPoint(i));
				}
				
				Intersection newF2Start = createIntersection(e.getPoint(index+1));
				
				createEdge(newF2Start, eEnd, f2Pts);
				
			}
			
			destroyEdge(e);
			
			edgesChanged(eStart);
			edgesChanged(eEnd);
			
		}
		
	}
	
	public void removeEdgeTop(Edge e) {
		
		/*
		 * have to properly cleanup start and end intersections before removing edges
		 */
		if (!e.isLoop()) {
			
			Vertex eStart = e.getStart();
			Vertex eEnd = e.getEnd();
			
			destroyEdge(e);
			
			edgesChanged(eStart);
			edgesChanged(eEnd);
			
		} else if (!e.isStandAlone()) {
			
			Vertex v = e.getStart();
			
			destroyEdge(e);
			
			edgesChanged(v);
			
		} else {
			destroyEdge(e);
		}
		
	}
	
	public void removeVertexTop(Vertex v) {
		
		Set<Vertex> affectedVertices = new HashSet<Vertex>();
		
		/*
		 * copy, since removing edges modifies v.getEdges()
		 * and use a set since loops will be in the list twice
		 */
		Set<Edge> eds = new HashSet<Edge>(v.getEdges());
		for (Edge e : eds) {
			
			if (!e.isLoop()) {
				
				Vertex eStart = e.getStart();
				Vertex eEnd = e.getEnd();
				
				affectedVertices.add(eStart);
				affectedVertices.add(eEnd);
				
				destroyEdge(e);
				
			} else {
				
				Vertex eV = e.getStart();
				
				affectedVertices.add(eV);
				
				destroyEdge(e);
				
			}
		}
		
		destroyVertex(v);
		affectedVertices.remove(v);
		
		for (Vertex a : affectedVertices) {
			edgesChanged(a);
		}
	}
	
	public boolean checkConsistency() {
		
		for (Intersection v : intersections) {
			v.check();
			
			/*
			 * there should only be 1 intersection with this point
			 */
			int count = 0;
			for (Intersection w : intersections) {
				if (Point.equals(v.getPoint(), w.getPoint())) {
					count++;
				}
			}
			assert count == 1;
		}
		
		for (Edge e : edges) {
			if (e.getTotalLength() <= 10.0) {
				throw new IllegalStateException("too small");
			}
			if (e.getStart() == null && e.getEnd() == null) {
				continue;
			}
			for (Edge f : edges) {
				if (e == f) {
					continue;
				}
				for (int i = 0; i < e.size()-1; i++) {
					Point a = e.getPoint(i);
					Point b = e.getPoint(i+1);
					for (int j = 0; j < f.size()-1; j++) {
						Point c = f.getPoint(j);
						Point d = f.getPoint(j+1);
						try {
							Point inter = Point.intersection(a, b, c, d);
							if (inter != null && !(Point.equals(inter, a) || Point.equals(inter, b) || Point.equals(inter, c) || Point.equals(inter, d))) {
								//assert false : "No edges should intersect";
								throw new IllegalStateException("No edges should intersect");
							}
						} catch (OverlappingException ex) {
							throw new IllegalStateException("Segments overlapping: " + "<" + a + " " + b + "> and <" + c + " " + d + ">");
						}
					}
				}
				if ((e.getStart() == f.getStart() && e.getEnd() == f.getEnd()) || (e.getStart() == f.getEnd() && e.getEnd() == f.getStart())) {
					/*
					 * e and f share endpoints
					 */
					Set<Point> shared = new HashSet<Point>();
					for (int i = 0; i < e.size(); i++) {
						Point eP = e.getPoint(i);
						for (int j = 0; j < f.size(); j++) {
							Point fP = f.getPoint(j);
							if (Point.equals(eP, fP)) {
								shared.add(eP);
							}
						}
					}
					if (e.getStart() == e.getEnd()) {
						assert shared.size() == 1;
					} else {
						assert shared.size() == 2;
					}
				}
			}
		}
		
		return true;
	}
	
	
}
