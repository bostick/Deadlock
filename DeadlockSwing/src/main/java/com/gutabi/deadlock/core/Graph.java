package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
	
	private final ArrayList<Edge> edges = new ArrayList<Edge>();
	private final ArrayList<Intersection> vertices = new ArrayList<Intersection>();
	private final QuadTree segTree = new QuadTree();
	
	private final ArrayList<Source> sources = new ArrayList<Source>();
	private final ArrayList<Sink> sinks = new ArrayList<Sink>();
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	public List<Intersection> getVertices() {
		return vertices;
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
		vertices.clear();
		segTree.clear();
	}
	
	private Edge createEdge(Intersection start, Intersection end, Point... points) {
		Edge e = new Edge(start, end, points);
		edges.add(e);
		segTree.addEdge(e);
		return e;
	}
	
	private Edge createEdge(Intersection start, Intersection end, List<Point> pts) {
		return createEdge(start, end, pts.toArray(new Point[0]));
	}
	
	private Intersection createVertex(Point p) {
		Intersection v = new Intersection(p);
		vertices.add(v);
		return v;
	}
	
	private void destroyVertex(Intersection v) {
		assert vertices.contains(v);
		vertices.remove(v);
		v.remove();
	}
	
	private void destroyEdge(Edge e) {
		assert edges.contains(e);
		edges.remove(e);
		segTree.removeEdge(e);
		e.remove();
	}
	
	public void addSource(Point p) {
		sources.add(new Source(p));
	}
	
	public void addSink(Point p) {
		sinks.add(new Sink(p));
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
		for (Intersection v : getVertices()) {
			Point d = v.getPoint();
			if (Point.equals(b, d)) {
				throw new IllegalArgumentException("point is on vertex");
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
	
	public Intersection findVertex(Point b) {
		Intersection found = null;
		int count = 0;
		for (Intersection v : getVertices()) {
			Point d = v.getPoint();
			/*
			 * d may be null if in the midle of processing (and not currently consistent)
			 */
			if (d != null && Point.equals(b, d)) {
				count++;
				found = v;
			}
		}
		if (count == 0) {
			throw new IllegalArgumentException("vertex not found at point");
		}
		if (count > 1) {
			throw new IllegalArgumentException("multiple vertices found at point");
		}
		return found;
	}
	
	public Intersection tryFindVertex(Point b) {
		for (Intersection v : getVertices()) {
			Point d = v.getPoint();
			if (Point.equals(b, d)) {
				return v;
			}
		}
		return null;
	}
	
	/**
	 * returns the closest vertex within radius that is not the excluded point
	 * 
	 */
	public IntersectionPosition findClosestVertexPosition(Point a, Point anchor, double radius) {
		Intersection anchorVertex = null;
		if (anchor != null) {
			anchorVertex = tryFindVertex(anchor);
		}
		
		Intersection closest = null;
		for (Intersection v : getVertices()) {
			Point vp = v.getPoint();
			if (anchorVertex != null && Point.equals(a, anchor) && v == anchorVertex) {
				/*
				 * use the excluded vertex
				 */
				closest = v;
				break;
			}
			if (anchorVertex != null && !Point.equals(a, anchor) && v == anchorVertex && Point.distance(a, anchor) < radius) {
				/*
				 * ignore the excluded vertex
				 */
				continue;
			}
			double dist = Point.distance(a, vp);
			if (dist < radius && (anchorVertex == null || dist < Point.distance(vp, anchor))) {
				if (closest == null) {
					closest = v;
				} else if (Point.distance(a, vp) < Point.distance(a, closest.getPoint())) {
					closest = v;
				}
			}	
		}
		if (closest != null) {
			return new IntersectionPosition(closest, null, null, 0);
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
					assert ret[0] == null && ret[1] == null || !Point.equals(ret[0], ret[1]);
					return ret;
					
				} else {
					
					ret[0] = null;
					ret[1] = null;
					assert ret[0] == null && ret[1] == null || !Point.equals(ret[0], ret[1]);
					return ret;
					
				}
				
			} else {
				ret[0] = closestA.getPoint();
				ret[1] = b;
				
				assert ret[0] == null && ret[1] == null || !Point.equals(ret[0], ret[1]);
				return ret;
			}
		} else {
			if (closestB != null) {
				ret[0] = a;
				ret[1] = closestB.getPoint();
				
				assert ret[0] == null && ret[1] == null || !Point.equals(ret[0], ret[1]);
				return ret;
				
			} else {
				ret[0] = a;
				ret[1] = b;
				
				assert ret[0] == null && ret[1] == null || !Point.equals(ret[0], ret[1]);
				return ret;
			}
		}
		
	}
	
	public Position closestPosition(Point a, double radius) {
		return closestPosition(a, null, radius);
	}
	
	public Position closestPosition(Point a, Point exclude, double radius) {
		IntersectionPosition closestVertex = findClosestVertexPosition(a, exclude, radius);
		if (closestVertex != null) {
			return closestVertex;
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
		
		Intersection aV = tryFindVertex(a);
		if (aV == null) {
			EdgePosition intInfo = tryFindEdgePosition(a);
			if (intInfo == null) {
				aV = createVertex(a);
			} else {
				aV = split(a);
			}
		}
		
		Intersection bV = tryFindVertex(b);
		if (bV == null) {
			EdgePosition intInfo = tryFindEdgePosition(b);
			if (intInfo == null) {
				bV = createVertex(b);
			} else {
				bV = split(b);
			}
		}
		
		Edge e = createEdge(aV, bV, aV.getPoint(), bV.getPoint());
		
		aV.addEdge(e);
		bV.addEdge(e);
		
		List<Edge> aEdges = aV.getEdges();
		if (aEdges.size() == 2) {
			merge(aEdges.get(0), aEdges.get(1), aV);
		}
		
		List<Edge> bEdges = bV.getEdges();
		if (bEdges.size() == 2) {
			merge(bEdges.get(0), bEdges.get(1), bV);
		}
		
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
	 * return Vertex at split point
	 */
	private Intersection split(Point p) {
		
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
		
		Point c = e.getPoint(index);
		Point d = e.getPoint(index+1);
		
		/*
		 * v may already exist if we are splitting at p and p is different than pInt and v already exists at pInt
		 */
		Intersection v = tryFindVertex(p);
		assert v == null;
		v = createVertex(p);
		
		Intersection eStart = e.getStart();
		Intersection eEnd = e.getEnd();
		
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
			
			Edge f = createEdge(v, v, pts);
			
			v.addEdge(f);
			v.addEdge(f);
			
			destroyEdge(e);
			
			return v;
			
		} else {
			
			/*
			 * f1
			 */
			
			Edge f1 = null;
			for (Edge ee : eStart.getEdges()) {
				if (((ee.getStart() == eStart && ee.getEnd() == v) || (ee.getStart() == v && ee.getEnd() == eStart)) && ee.size() == 2) {
					/*
					 * both a and b are already both vertices and connected, so just use this
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
					
					Intersection cV = createVertex(c);
					Edge f3 = createEdge(cV, v, c, p);
					
					cV.addEdge(f3);
					v.addEdge(f3);
					
				}
				pts.add(p);
				
				f1 = createEdge(eStart, v, pts);
				
				eStart.addEdge(f1);
				v.addEdge(f1);
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
					 * both a and b are already both vertices and connected, so just use this
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
					
					Intersection dV = createVertex(d);
					Edge f3 = createEdge(dV, v, d, p);
					
					dV.addEdge(f3);
					v.addEdge(f3);
					
				}
				for (int i = index+2; i < e.size(); i++) {
					pts.add(e.getPoint(i));
				}
				
				f2 = createEdge(v, eEnd, pts);
				
				v.addEdge(f2);
				eEnd.addEdge(f2);
			}
			
			eStart.removeEdge(e);
			
			eEnd.removeEdge(e);
			
			destroyEdge(e);
			
			return v;
		}
	}
	
	/**
	 * split an edge at point with index i and param p between indices i and i+1
	 * takes care of adjusting to integer coordinates
	 * Edge e will have been removed
	 */
	@SuppressWarnings("serial")
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
	 * merge two edges (possibly the same edge) at the given vertex
	 * 
	 * no colinear points in returned edge
	 */
	private void merge(Edge e1, Edge e2, Intersection v) {
		assert !e1.isRemoved();
		assert !e2.isRemoved();
		assert !v.isRemoved();
		
		Intersection e1Start = e1.getStart();
		Intersection e1End = e1.getEnd();
		
		Intersection e2Start = e2.getStart();
		Intersection e2End = e2.getEnd();
		
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
			
			v.removeEdge(e1);
			v.removeEdge(e2);
			
			destroyVertex(v);
			destroyEdge(e1);
			
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
			
			Edge newEdge = createEdge(e1End, e2End, pts);
			
			e1End.removeEdge(e1);
			e1End.addEdge(newEdge);
			
			e2End.removeEdge(e2);
			e2End.addEdge(newEdge);
			
			v.removeEdge(e1);
			v.removeEdge(e2);
			
			destroyVertex(v);
			destroyEdge(e1);
			destroyEdge(e2);
			
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
			
			Edge newEdge = createEdge(e1End, e2Start, pts);
			
			e1End.removeEdge(e1);
			e1End.addEdge(newEdge);
			
			e2Start.removeEdge(e2);
			e2Start.addEdge(newEdge);
			
			v.removeEdge(e1);
			v.removeEdge(e2);
			
			destroyVertex(v);
			destroyEdge(e1);
			destroyEdge(e2);
			
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
			
			Edge newEdge = createEdge(e1Start, e2End, pts);
			
			e1Start.removeEdge(e1);
			e1Start.addEdge(newEdge);
			
			e2End.removeEdge(e2);
			e2End.addEdge(newEdge);
			
			v.removeEdge(e1);
			v.removeEdge(e2);
			
			destroyVertex(v);
			destroyEdge(e1);
			destroyEdge(e2);
			
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
			
			Edge newEdge = createEdge(e1Start, e2Start, pts);
			
			e1Start.removeEdge(e1);
			e1Start.addEdge(newEdge);
			
			e2Start.removeEdge(e2);
			e2Start.addEdge(newEdge);
			
			v.removeEdge(e1);
			v.removeEdge(e2);
			
			destroyVertex(v);
			destroyEdge(e1);
			destroyEdge(e2);
			
		}
	}
	
	
	/**
	 * remove segment <i, i+1> from edge e
	 * 
	 * post: e has been removed
	 */
	private void removeSegment(Point a, Point b) {
		
		try {
			
			Segment info = segTree.findSegment(a, b);
			Edge e = info.edge;
			int index = info.index;
			
			Intersection eStart = e.getStart();
			Intersection eEnd = e.getEnd();
			
			if (eStart == null && eEnd == null) {
				// stand-alone loop
				
				List<Point> pts = new ArrayList<Point>();
				
				for (int i = index+1; i < e.size(); i++) {
					pts.add(e.getPoint(i));
				}
				for (int i = 1; i <= index; i++) {
					pts.add(e.getPoint(i));
				}
				
				Intersection newStart = createVertex(e.getPoint(index+1));
				Intersection newEnd = createVertex(e.getPoint(index));
				
				Edge newEdge = createEdge(newStart, newEnd, pts);
				
				newStart.addEdge(newEdge);
				newEnd.addEdge(newEdge);
				
				destroyEdge(e);
				
			} else {
				
				if (index == 0) {
					
					if (e.size() > 2) {
						
						List<Point> pts = new ArrayList<Point>();
						
						for (int i = 1; i < e.size(); i++) {
							pts.add(e.getPoint(i));
						}
						
						Intersection newStart = createVertex(e.getPoint(1));
						
						Edge newEdge = createEdge(newStart, eEnd, pts);
						
						newStart.addEdge(newEdge);
						eEnd.addEdge(newEdge);
						
					}
					
				} else if (index == e.size()-2) {
					
					List<Point> pts = new ArrayList<Point>();
					
					for (int i = 0; i < e.size()-1; i++) {
						pts.add(e.getPoint(i));
					}
					
					Intersection newEnd = createVertex(e.getPoint(e.size()-2));
					
					Edge newEdge = createEdge(eStart, newEnd, pts);
					
					eStart.addEdge(newEdge);
					newEnd.addEdge(newEdge);
					
				} else {
					//create 2 new edges without worrying about vertices
					
					List<Point> f1Pts = new ArrayList<Point>();
					
					for (int i = 0; i <= index; i++) {
						f1Pts.add(e.getPoint(i));
					}
					
					Intersection newF1End = createVertex(e.getPoint(index));
					
					Edge f1 = createEdge(eStart, newF1End, f1Pts);
					
					eStart.addEdge(f1);
					newF1End.addEdge(f1);
					
					List<Point> f2Pts = new ArrayList<Point>();
					
					for (int i = index+1; i < e.size(); i++) {
						f2Pts.add(e.getPoint(i));
					}
					
					Intersection newF2Start = createVertex(e.getPoint(index+1));
					
					Edge f2 = createEdge(newF2Start, eEnd, f2Pts);
					
					newF2Start.addEdge(f2);
					eEnd.addEdge(f2);
					
				}
				
				eStart.removeEdge(e);
				List<Edge> eStartEdges = eStart.getEdges();
				if (eStartEdges.size() == 0) {
					destroyVertex(eStart);
				} else if (eStartEdges.size() == 2) {
					merge(eStartEdges.get(0), eStartEdges.get(1), eStart);
				}
				
				eEnd.removeEdge(e);
				List<Edge> eEndEdges = eEnd.getEdges();
				if (eEndEdges.size() == 0) {
					destroyVertex(eEnd);
				} else if (eEndEdges.size() == 2) {
					merge(eEndEdges.get(0), eEndEdges.get(1), eEnd);
				}
				
				destroyEdge(e);
				
			}
			
		} finally {
			
		}
		
	}
	
	public void removeEdgeTop(Edge e) {
		
		/*
		 * have to properly cleanup start and end vertices before removing edges
		 */
		if (!e.isLoop()) {
			
			Intersection eStart = e.getStart();
			Intersection eEnd = e.getEnd();
			
			eStart.removeEdge(e);
			List<Edge> eStartEdges = eStart.getEdges();
			if (eStartEdges.size() == 0) {
				destroyVertex(eStart);
			} else if (eStartEdges.size() == 2) {
				merge(eStartEdges.get(0), eStartEdges.get(1), eStart);
			}
			
			eEnd.removeEdge(e);
			List<Edge> eEndEdges = eEnd.getEdges();
			if (eEndEdges.size() == 0) {
				destroyVertex(eEnd);
			} else if (eEndEdges.size() == 2) {
				merge(eEndEdges.get(0), eEndEdges.get(1), eEnd);
			}
			
			destroyEdge(e);
			
		} else if (!e.isStandAlone()) {
			
			Intersection v = e.getStart();
			
			v.removeEdge(e);
			v.removeEdge(e);
			
			List<Edge> eds = v.getEdges();
			if (eds.size() == 0) {
				assert false;
			} else if (eds.size() == 2) {
				merge(eds.get(0), eds.get(1), v);
			}
			
			destroyEdge(e);
			
		} else {
			destroyEdge(e);
		}
		
	}
	
	public void removeVertexTop(Intersection v) {
		
		Set<Intersection> affectedVertices = new HashSet<Intersection>();
		
		/*
		 * copy, since removing edges modifies v.getEdges()
		 * and use a set since loops will be in the list twice
		 */
		Set<Edge> eds = new HashSet<Edge>(v.getEdges());
		for (Edge e : eds) {
			
			if (!e.isLoop()) {
				
				Intersection eStart = e.getStart();
				Intersection eEnd = e.getEnd();
				
				eStart.removeEdge(e);
				eEnd.removeEdge(e);
				
				affectedVertices.add(eStart);
				affectedVertices.add(eEnd);
				
				destroyEdge(e);
				
			} else {
				
				Intersection eV = e.getStart();
				
				eV.removeEdge(e);
				eV.removeEdge(e);
				
				affectedVertices.add(eV);
				
				destroyEdge(e);
				
			}
		}
		
		destroyVertex(v);
		affectedVertices.remove(v);
		
		for (Intersection a : affectedVertices) {
			List<Edge> aeds = a.getEdges();
			if (aeds.size() == 0) {
				destroyVertex(a);
			} else if (aeds.size() == 2) {
				merge(aeds.get(0), aeds.get(1), a);
			}
		}
	}
	
	public boolean checkConsistency() {
		
		for (Intersection v : vertices) {
			v.check();
			
			/*
			 * there should only be 1 vertex with this point
			 */
			int count = 0;
			for (Intersection w : vertices) {
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
