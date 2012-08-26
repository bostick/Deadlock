package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
	
	private final ArrayList<Edge> edges = new ArrayList<Edge>();
	private final ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private final QuadTree segTree = new QuadTree();
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	public List<Vertex> getVertices() {
		return vertices;
	}
	
	public QuadTree getSegmentTree() {
		return segTree;
	}
	
	public void clear() {
		edges.clear();
		vertices.clear();
		segTree.clear();
	}
	
	public Edge createEdge(Vertex start, Vertex end, Point... points) {
		Edge e = new Edge(start, end, points);
		edges.add(e);
		segTree.addEdge(e);
		return e;
	}
	
	public Edge createEdge(Vertex start, Vertex end, List<Point> pts) {
		return createEdge(start, end, pts.toArray(new Point[0]));
	}
	
	public Vertex createVertex(Point p) {
		Vertex v = new Vertex(p);
		vertices.add(v);
		return v;
	}
	
	public void removeVertex(Vertex v) {
		v.remove();
		vertices.remove(v);
	}
	
	public void removeEdge(Edge e) {
		edges.remove(e);
		segTree.removeEdge(e);
		e.remove();
	}
	
	public EdgePosition tryFindEdgePosition(Point b) {
		for (Segment in : segTree.findAllSegments(b)) {
			Edge e = in.edge;
			int i = in.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			if (Point.intersect(b, c, d)) {
				return new EdgePosition(e, i, Point.param(b, c, d), null, null, 0);
			}
		}
		for (Vertex v : getVertices()) {
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
	
	public Vertex findVertex(Point b) {
		Vertex found = null;
		int count = 0;
		for (Vertex v : getVertices()) {
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
	
	public Vertex tryFindVertex(Point b) {
		
		Vertex found = null;
		int count = 0;
		for (Vertex v : getVertices()) {
			Point d = v.getPoint();
			if (Point.equals(b, d)) {
				count++;
				found = v;
			}
		}
		if (count == 0) {
			return null;
		}
		if (count > 1) {
			throw new IllegalArgumentException("multiple vertices found at point");
		}
		return found;
	}
	
	/**
	 * returns the closest vertex within radius that is not the excluded point
	 * 
	 */
	public VertexPosition findClosestVertexPosition(Point a, Point exclude, double radius) {
		Vertex closest = null;
		for (Vertex v : getVertices()) {
			Point vp = v.getPoint();
			double dist = Point.distance(a, vp);
			if (dist < radius && (exclude == null || dist < Point.distance(vp, exclude))) {
				if (closest == null) {
					closest = v;
				} else if (Point.distance(a, vp) < Point.distance(a, closest.getPoint())) {
					closest = v;
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
	
	/**
	 * this is down at the grid point level
	 * only adjusts intersections to grid points and adds segments 
	 */
	@SuppressWarnings("serial")
	public void addStroke(List<Point> stroke, boolean newStroke) {
		
		for (int i = 0; i < stroke.size()-1; i++) {
			Point a = stroke.get(i);
			Point b = stroke.get(i+1);
			assert !Point.equals(a, b);
			
			if (newStroke) {
				Point[] newSegment = handleIntersections(a, b, (i == 0));
				if (newSegment[0] == null && newSegment[1] == null) {
					/*
					 * <a, b> is too close to another edge, so it is being skipped
					 */
					continue;
				}
				a = newSegment[0];
				b = newSegment[1];
			}
			
			List<PointToBeAdded> betweenABPoints = fillinIntersections(a, b);
			
			betweenLoop:
			for (int j = 0; j < betweenABPoints.size()-1; j++) {
				PointToBeAdded p1 = betweenABPoints.get(j);
				PointToBeAdded p2 = betweenABPoints.get(j+1);
				
				for (Segment in : segTree.findAllSegments(p1.p, p2.p)) {
					Edge e = in.edge;
					int index = in.index;
					Point c = e.getPoint(index);
					Point d = e.getPoint(index+1);
					if ((Point.equals(p1.p, d) || Point.intersect(p1.p, c, d)) && (Point.equals(p2.p, d) || Point.intersect(p2.p, c, d))) {
						/*
						 * <p1, p2> is completely within <c, d>, so do not add
						 */
						continue betweenLoop;
					}
				}
				
				final Point p1Int = p1.p.toInteger();
				
				final Point p2Int = p2.p.toInteger();
				
				if (!Point.equals(p1Int, p2Int)) {
					
					if (!Point.equals(p1.p, p1Int) && tryFindEdgePosition(p1.p) != null) {
						adjustEdgePointToGrid(p1.p);
					}
					
					if (!Point.equals(p2.p, p2Int) && tryFindEdgePosition(p2.p) != null) {
						adjustEdgePointToGrid(p2.p);
					}
					
					if (!segmentOverlapsOrIntersects(p1Int, p2Int)) {
						/*
						 * the vast majority of cases will be going here
						 */
						addSegment(p1Int, p2Int);
					} else {
						addStroke(new ArrayList<Point>(){{add(p1Int);add(p2Int);}}, true);
					}
					
				}
				
			}
		}
		
		if (newStroke) {
			cleanupEdges();
		}
		
	}
	
	
	boolean tooClose = false;
	
	/*
	 * deal with curvature and intersections here
	 */
	public Point[] handleIntersections(Point a, Point b, boolean firstSegmentOfStroke) {
		
		Point[] ret = new Point[2];
		Position closestA;
		Position closestB;
		
		closestA = closestPosition(a, 10);
		closestB = closestPosition(b, a, 10);
		
		if (closestA != null) {
			if (closestB != null) {
				
				if (closestA instanceof VertexPosition && closestB instanceof VertexPosition) {
					Vertex v1 = ((VertexPosition)closestA).getVertex();
					Vertex v2 = ((VertexPosition)closestB).getVertex();
					if (v1 != v2) {
						
						// connect 2 vertices that are close together, and stay tooclose
						tooClose = true;
						ret[0] = closestA.getPoint();
						ret[1] = closestB.getPoint();
						
					} else {
						
						tooClose = true;
						ret[0] = null;
						ret[1] = null;
						
					}
				} else {
					
					if (tooClose) {
						ret[0] = null;
						ret[1] = null;
					} else {
						tooClose = true;
						ret[0] = closestA.getPoint();
						ret[1] = closestB.getPoint();
					}
					
				}
				
			} else {
				tooClose = false;
				ret[0] = closestA.getPoint();
				ret[1] = b;
			}
		} else {
			if (closestB != null) {
				tooClose = true;
				ret[0] = a;
				ret[1] = closestB.getPoint();
			} else {
				tooClose = false;
				ret[0] = a;
				ret[1] = b;
			}
		}
		
		assert ret[0] == null && ret[1] == null || !Point.equals(ret[0], ret[1]);
		return ret;
		
	}
	
	public Position closestPosition(Point a, double radius) {
		return closestPosition(a, null, radius);
	}
	
	public Position closestPosition(Point a, Point exclude, double radius) {
		VertexPosition closestVertex = findClosestVertexPosition(a, exclude, radius);
		if (closestVertex != null) {
			return closestVertex;
		}
		EdgePosition closestEdge = findClosestEdgePosition(a, exclude, radius);
		if (closestEdge != null) {
			return closestEdge;
		}
		return null;
	}
	
	private List<PointToBeAdded> fillinIntersections(Point a, Point b) {
		
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
					PointToBeAdded nptba = new PointToBeAdded(inter, Point.param(inter, a, b));
					betweenABPointsAdd(betweenABPoints, nptba);
				}
			} catch (OverlappingException ex) {
				
				if (Point.intersect(c, a, b)) {
					PointToBeAdded nptba = new PointToBeAdded(c, Point.param(c, a, b));
					betweenABPointsAdd(betweenABPoints, nptba);
				}
				
				if (Point.intersect(d, a, b)) {
					PointToBeAdded nptba = new PointToBeAdded(d, Point.param(d, a, b));
					betweenABPointsAdd(betweenABPoints, nptba);
				}
				
			}
			
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
	
	private boolean segmentOverlapsOrIntersects(Point a, Point b) {
		
		for (Segment in : segTree.findAllSegments(a, b, 10)) {
			Edge e = in.edge;
			int i = in.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			try {
				Point inter = Point.intersection(a, b, c, d);
				if (inter != null && !inter.isInteger()) {
					return true;
				}
			} catch (OverlappingException ex) {	
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
				
				/*
				 * have to properly cleanup start and end vertices before removing edges
				 */
				if (!e.isLoop()) {
					
					Vertex eStart = e.getStart();
					Vertex eEnd = e.getEnd();
					
					eStart.removeEdge(e);
					List<Edge> eStartEdges = eStart.getEdges();
					if (eStartEdges.size() == 0) {
						removeVertex(eStart);
					} else if (eStartEdges.size() == 2) {
						merge(eStartEdges.get(0), eStartEdges.get(1));
					}
					
					eEnd.removeEdge(e);
					List<Edge> eEndEdges = eEnd.getEdges();
					if (eEndEdges.size() == 0) {
						removeVertex(eEnd);
					} else if (eEndEdges.size() == 2) {
						merge(eEndEdges.get(0), eEndEdges.get(1));
					}
					
					removeEdge(e);
					
				} else if (!e.isStandAlone()) {
					
					Vertex v = e.getStart();
					
					v.removeEdge(e);
					v.removeEdge(e);
					
					List<Edge> eds = v.getEdges();
					if (eds.size() == 0) {
						assert false;
					} else if (eds.size() == 2) {
						merge(eds.get(0), eds.get(1));
					}
					
					removeEdge(e);
					
				} else {
					removeEdge(e);
				}
				
			}
			
			if (!changed) {
				break;
			}
		}
		
	}
	
	public void addSegment(Point a, Point b) {
		
		try {
			
			assert !Point.equals(a, b);
			assert !segmentExists(a, b) : "segment " + a + " " + b + " already exists";
			
			assert a.isInteger();
			assert b.isInteger();
			
			Vertex aV = tryFindVertex(a);
			if (aV == null) {
				EdgePosition intInfo = tryFindEdgePosition(a);
				if (intInfo == null) {
					aV = createVertex(a);
				} else {
					aV = split(a);
				}
			}
			
			Vertex bV = tryFindVertex(b);
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
			
			Edge working = e;
			
			List<Edge> aEdges = aV.getEdges();
			if (aEdges.size() == 2) {
				/*
				 * a has 2 edges now, counting e, so the 2 should be merged
				 */
				Edge aEdge;
				if (aEdges.get(0) == e) {
					aEdge = aEdges.get(1);
				} else {
					aEdge = aEdges.get(0);
				}
				working = merge(aEdge, e);
			}
			
			/*
			 * bV could have been removed if the merging of aEdge and e formed a loop (thereby removing bV in the process),
			 * so check that b is still a vertex first
			 */
			if (!bV.isRemoved()) {
				List<Edge> bEdges = bV.getEdges();
				if (bEdges.size() == 2) {
					Edge bEdge;
					if (bEdges.get(0) == e) {
						bEdge = bEdges.get(1);
					} else {
						bEdge = bEdges.get(0);
					}
					working = merge(working, bEdge);
				}
			}
			
		} finally {
			
		}
	}
	
	/**
	 * not exactly like tryFindSegment
	 * returns true even if <a, b> is between <c, d>
	 */
	public boolean segmentExists(Point a, Point b) {
		for (Segment in : segTree.findAllSegments(a, b)) {
			Edge e = in.edge;
			int i = in.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			if ((Point.equals(a, d) || Point.intersect(a, c, d)) && (Point.equals(b, d) || Point.intersect(b, c, d))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * split an edge at point with index i and param p between indices i and i+1
	 * takes care of adjusting to integer coordinates
	 * Edge e will have been removed
	 * return Vertex at split point
	 */
	public Vertex split(Point p) {
		
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
		
		Point c = e.getPoint(index);
		Point d = e.getPoint(index+1);
		
		/*
		 * v may already exist if we are splitting at p and p is different than pInt and v already exists at pInt
		 */
		Vertex v = tryFindVertex(p);
		if (v == null) {
			v = createVertex(p);
		}
		assert Point.equals(v.getPoint(), p);
		
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
			
			Edge f = createEdge(v, v, pts);
			
			v.addEdge(f);
			v.addEdge(f);
			/*
			 * a loop has just been split, so it is in an inconsistent state, do not check here
			 * hopefully, another edge will be added soon
			 */
			//v.check();
			
			removeEdge(e);
			
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
					
					Vertex cV = createVertex(c);
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
					
					Vertex dV = createVertex(d);
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
			
			/*
			 * splitting into 2 edges, so in an inconsistent state
			 * don't check here
			 */
			//v.check();
			
			eStart.removeEdge(e);
			
			eEnd.removeEdge(e);
			
			removeEdge(e);
			
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
			
			/*
			 * if pInt is c or d, then do not duplicate
			 * and if the intersection is already an integer point, then there is nothing to do
			 */
			if (Point.equals(c, pInt) || Point.equals(d, pInt) ||
					Point.equals(p, pInt)) {
				/*
				 * nothing being adjusted
				 */
				return false;
			}
			
			/*
			 * adjust segment to integer coords first
			 */
			removeSegment(c, d);
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
			
			return true;
			
		} finally {
			
		}
		
	}
	
	/**
	 * merge two edges with common vertex (possibly the same edge)
	 * no colinear points in returned edge
	 */
	public Edge merge(Edge e1, Edge e2) {
		assert !e1.isRemoved();
		assert !e2.isRemoved();
		
		Vertex e1Start = e1.getStart();
		Vertex e1End = e1.getEnd();
		
		Vertex e2Start = e2.getStart();
		Vertex e2End = e2.getEnd();
		
		assert e1Start != null;
		assert e1End != null;
		assert e2Start != null;
		assert e2End != null;
		assert e1Start == e2Start || e1Start == e2End || e1End == e2Start || e1End == e2End;
		
		if (e1 == e2) {
			// in the middle of merging a stand-alone loop
			assert e1Start.getEdges().size() == 2 && e1End.getEdges().size() == 2;
				
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
			
			Edge newEdge = createEdge(null, null, pts);
			
			e1Start.removeEdge(e1);
			e1Start.removeEdge(e1);
			
			removeVertex(e1Start);
			removeEdge(e1);
			
			return newEdge;
			
		} else if (e1Start == e2End && e1End == e2Start) {
			// forming a loop
			
			assert Point.equals(e1.getPoint(e1.size()-1), e2.getPoint(0));
			assert Point.equals(e2.getPoint(e2.size()-1), e1.getPoint(0));
			assert e1Start.getEdges().size() == 2 || e1End.getEdges().size() == 2;
			
			List<Point> pts = new ArrayList<Point>();
			
			if (e1Start.getEdges().size() == 2 && e1End.getEdges().size() == 2) {
				// creating stand-alone loop
				
				/*
				 * both e1Start and e1End will be merged (so they will be removed)
				 * use e1Start as the starting point
				 */
				// only add if not colinear
				try {
					if (!Point.colinear(e2.getPoint(e2.size()-2), e1.getPoint(0), e1.getPoint(1))) {
						pts.add(e1.getPoint(0));
					}
				} catch (ColinearException e) {
					assert false;
				}
				for (int i = 1; i < e1.size()-1; i++) {
					pts.add(e1.getPoint(i));
				}
				try {
					// only add if not colinear
					if (!Point.colinear(e1.getPoint(e1.size()-2), e1.getPoint(e1.size()-1), e2.getPoint(1))) {
						pts.add(e1.getPoint(e1.size()-1));
					}
				} catch (ColinearException ex) {
					assert false;
				}
				for (int i = 1; i < e2.size()-1; i++) {
					pts.add(e2.getPoint(i));
				}
				/*
				 * add whatever the first point is
				 */
				pts.add(pts.get(0));
				
				e1Start.removeEdge(e1);
				
				e1End.removeEdge(e1);
				
				e2Start.removeEdge(e2);
				
				e2End.removeEdge(e2);
				
				removeVertex(e1Start);
				removeVertex(e1End);
				removeEdge(e1);
				removeEdge(e2);
				
				return createEdge(null, null, pts);
				
			} else if (e1Start.getEdges().size() == 2) {
				// creating loop with 1 vertex
				
				/*
				 * e1Start is the vertex that will be merged (so it will be removed)
				 * use e1End as the starting point
				 */
				/*
				 * don't test colinearity here,
				 * e2.getPoint(0) is a vertex
				 */
				pts.add(e2.getPoint(0));
				for (int i = 1; i < e2.size()-1; i++) {
					pts.add(e2.getPoint(i));
				}
				try {
					// only add if not colinear
					if (!Point.colinear(e2.getPoint(e2.size()-2), e2.getPoint(e2.size()-1), e1.getPoint(1))) {
						pts.add(e2.getPoint(e2.size()-1));
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
				
				Edge newEdge = createEdge(e1End, e1End, pts);
				
				e1End.addEdge(newEdge);
				e1End.removeEdge(e1);
				
				e1Start.removeEdge(e1);
				
				e2Start.addEdge(newEdge);
				e2Start.removeEdge(e2);
				
				e2End.removeEdge(e2);
				
				removeVertex(e1Start/*e2End*/);
				removeEdge(e1);
				removeEdge(e2);
				
				return newEdge;
				
			} else {
				// creating loop with 1 vertex
				assert e1End.getEdges().size() == 2;
				
				/*
				 * e1End is the vertex that will be merged (so it will be removed)
				 * use e1Start as the starting point
				 */
				/*
				 * don't test colinearity here,
				 * e1.getPoint(0) is a vertex
				 */
				pts.add(e1.getPoint(0));
				for (int i = 1; i < e1.size()-1; i++) {
					pts.add(e1.getPoint(i));
				}
				try {
					// only add if not colinear
					if (!Point.colinear(e1.getPoint(e1.size()-2), e1.getPoint(e1.size()-1), e2.getPoint(1))) {
						pts.add(e1.getPoint(e1.size()-1));
					}
				} catch (ColinearException ex) {
					assert false;
				}
				for (int i = 1; i < e2.size()-1; i++) {
					pts.add(e2.getPoint(i));
				}
				/*
				 * add whatever the first point is
				 */
				pts.add(pts.get(0));
				
				Edge newEdge = createEdge(e1Start, e1Start, pts);
				
				e1Start.addEdge(newEdge);
				e1Start.removeEdge(e1);
				
				e1End.removeEdge(e1);
				
				e2Start.removeEdge(e2);
				
				e2End.addEdge(newEdge);
				e2End.removeEdge(e2);
				
				removeVertex(e1End/*e2Start*/);
				removeEdge(e1);
				removeEdge(e2);
				
				return newEdge;
			}
			
		} else if (e1Start == e2Start && e1End == e2End) {
			// forming a loop
			
			assert Point.equals(e1.getPoint(e1.size()-1), e2.getPoint(e2.size()-1));
			assert Point.equals(e2.getPoint(0), e1.getPoint(0));
			assert e1Start.getEdges().size() == 2 || e1End.getEdges().size() == 2;
			
			List<Point> pts = new ArrayList<Point>();
			
			if (e1Start.getEdges().size() == 2 && e1End.getEdges().size() == 2) {
				// creating stand-alone loop
				
				/*
				 * both e1Start and e1End will be merged (so they will be removed)
				 * use e1Start as the starting point
				 */
				// only add if not colinear
				try {
					if (!Point.colinear(e2.getPoint(1), e1.getPoint(0), e1.getPoint(1))) {
						pts.add(e1.getPoint(0));
					}
				} catch (ColinearException e) {
					assert false;
				}
				for (int i = 1; i < e1.size()-1; i++) {
					pts.add(e1.getPoint(i));
				}
				try {
					// only add if not colinear
					if (!Point.colinear(e1.getPoint(e1.size()-2), e1.getPoint(e1.size()-1), e2.getPoint(e2.size()-2))) {
						pts.add(e1.getPoint(e1.size()-1));
					}
				} catch (ColinearException ex) {
					assert false;
				}
				for (int i = e2.size()-2; i >= 1; i--) {
					pts.add(e2.getPoint(i));
				}
				/*
				 * add whatever the first point is
				 */
				pts.add(pts.get(0));
				
				e1Start.removeEdge(e1);
				
				e1End.removeEdge(e1);
				
				e2Start.removeEdge(e2);
				
				e2End.removeEdge(e2);
				
				removeVertex(e1Start);
				removeVertex(e1End);
				removeEdge(e1);
				removeEdge(e2);
				
				return createEdge(null, null, pts);
				
			} else if (e1Start.getEdges().size() == 2) {
				// creating loop with 1 vertex
				
				/*
				 * e1Start is the vertex that will be merged (so it will be removed)
				 * use e1End as the starting point
				 */
				/*
				 * don't test colinearity here,
				 * e2.getPoint(e2.size()-1) is a vertex
				 */
				pts.add(e2.getPoint(e2.size()-1));
				for (int i = e2.size()-2; i >= 1; i--) {
					pts.add(e2.getPoint(i));
				}
				
				try {
					// only add if not colinear
					if (!Point.colinear(e2.getPoint(1), e1.getPoint(0), e1.getPoint(1))) {
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
				
				Edge newEdge = createEdge(e1End, e1End, pts);
				
				e1Start.removeEdge(e1);
				
				e1End.addEdge(newEdge);
				e1End.removeEdge(e1);
				
				e2Start.removeEdge(e2);
				
				e2End.addEdge(newEdge);
				e2End.removeEdge(e2);
				
				removeVertex(e1Start/*e2Start*/);
				removeEdge(e1);
				removeEdge(e2);
				
				return newEdge;
				
			} else {
				// creating loop with 1 vertex
				assert e1End.getEdges().size() == 2;
				
				/*
				 * e1End is the vertex that will be merged (so it will be removed)
				 * use e1Start as the starting point
				 */
				/*
				 * don't test colinearity here,
				 * e1.getPoint(0) is a vertex
				 */
				pts.add(e1.getPoint(0));
				for (int i = 1; i < e1.size()-1; i++) {
					pts.add(e1.getPoint(i));
				}
				try {
					// only add if not colinear
					if (!Point.colinear(e1.getPoint(e1.size()-2), e1.getPoint(e1.size()-1), e2.getPoint(e2.size()-2))) {
						pts.add(e1.getPoint(e1.size()-1));
					}
				} catch (ColinearException ex) {
					assert false;
				}
				for (int i = e2.size()-2; i >= 1; i--) {
					pts.add(e2.getPoint(i));
				}
				/*
				 * add whatever the first point is
				 */
				pts.add(pts.get(0));
				
				Edge newEdge = createEdge(e1Start, e1Start, pts);
				
				e1Start.removeEdge(e1);
				e1Start.addEdge(newEdge);
				
				e1End.removeEdge(e1);
				
				e2Start.removeEdge(e2);
				e2Start.addEdge(newEdge);
				
				e2End.removeEdge(e2);
				
				removeVertex(e1End/*e2End*/);
				removeEdge(e1);
				removeEdge(e2);
				
				return newEdge;
				
			}
			
		} else if (e1Start == e2Start) {
			// not a loop
			
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
			
			e1Start.removeEdge(e1);
			
			e1End.removeEdge(e1);
			e1End.addEdge(newEdge);
			
			e2Start.removeEdge(e2);
			
			e2End.removeEdge(e2);
			e2End.addEdge(newEdge);
			
			removeVertex(e1Start/*e2Start*/);
			removeEdge(e1);
			removeEdge(e2);
			
			return newEdge;
			
		} else if (e1Start == e2End) {
			// not a loop
			
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
			
			e1Start.removeEdge(e1);
			
			e1End.removeEdge(e1);
			e1End.addEdge(newEdge);
			
			e2Start.removeEdge(e2);
			e2Start.addEdge(newEdge);
			
			e2End.removeEdge(e2);
			
			removeVertex(e1Start/*e2End*/);
			removeEdge(e1);
			removeEdge(e2);
			
			return newEdge;
			
		} else if (e1End == e2Start) {
			// not a loop
			
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
			
			e1End.removeEdge(e1);
			
			e2Start.removeEdge(e2);
			
			e2End.removeEdge(e2);
			e2End.addEdge(newEdge);
			
			removeVertex(e1End/*e2Start*/);
			removeEdge(e1);
			removeEdge(e2);
			
			return newEdge;
			
		} else if (e1End == e2End) {
			// not a loop
			
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
			
			e1End.removeEdge(e1);
			
			e2Start.removeEdge(e2);
			e2Start.addEdge(newEdge);
			
			e2End.removeEdge(e2);
			
			removeVertex(e1End);
			removeEdge(e1);
			removeEdge(e2);
			
			return newEdge;
			
		} else {
			throw new AssertionError("should never be seen");
		}
	}
	
	
	/**
	 * remove segment <i, i+1> from edge e
	 * 
	 * post: e has been removed
	 */
	public void removeSegment(Point a, Point b) {
		
		try {
			
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
				
				Vertex newStart = createVertex(e.getPoint(index+1));
				Vertex newEnd = createVertex(e.getPoint(index));
				
				Edge newEdge = createEdge(newStart, newEnd, pts);
				
				newStart.addEdge(newEdge);
				newEnd.addEdge(newEdge);
				
				removeEdge(e);
				
			} else {
				
				if (index == 0) {
					
					if (e.size() > 2) {
						
						List<Point> pts = new ArrayList<Point>();
						
						for (int i = 1; i < e.size(); i++) {
							pts.add(e.getPoint(i));
						}
						
						Vertex newStart = createVertex(e.getPoint(1));
						
						Edge newEdge = createEdge(newStart, eEnd, pts);
						
						newStart.addEdge(newEdge);
						eEnd.addEdge(newEdge);
						
					}
					
				} else if (index == e.size()-2) {
					
					List<Point> pts = new ArrayList<Point>();
					
					for (int i = 0; i < e.size()-1; i++) {
						pts.add(e.getPoint(i));
					}
					
					Vertex newEnd = createVertex(e.getPoint(e.size()-2));
					
					Edge newEdge = createEdge(eStart, newEnd, pts);
					
					eStart.addEdge(newEdge);
					newEnd.addEdge(newEdge);
					
				} else {
					//create 2 new edges without worrying about vertices
					
					List<Point> f1Pts = new ArrayList<Point>();
					
					for (int i = 0; i <= index; i++) {
						f1Pts.add(e.getPoint(i));
					}
					
					Vertex newF1End = createVertex(e.getPoint(index));
					
					Edge f1 = createEdge(eStart, newF1End, f1Pts);
					
					eStart.addEdge(f1);
					newF1End.addEdge(f1);
					
					List<Point> f2Pts = new ArrayList<Point>();
					
					for (int i = index+1; i < e.size(); i++) {
						f2Pts.add(e.getPoint(i));
					}
					
					Vertex newF2Start = createVertex(e.getPoint(index+1));
					
					Edge f2 = createEdge(newF2Start, eEnd, f2Pts);
					
					newF2Start.addEdge(f2);
					eEnd.addEdge(f2);
					
				}
				
				eStart.removeEdge(e);
				List<Edge> eStartEdges = eStart.getEdges();
				if (eStartEdges.size() == 0) {
					removeVertex(eStart);
				} else if (eStartEdges.size() == 2) {
					merge(eStartEdges.get(0), eStartEdges.get(1));
				}
				
				eEnd.removeEdge(e);
				List<Edge> eEndEdges = eEnd.getEdges();
				if (eEndEdges.size() == 0) {
					removeVertex(eEnd);
				} else if (eEndEdges.size() == 2) {
					merge(eEndEdges.get(0), eEndEdges.get(1));
				}
				
				removeEdge(e);
				
			}
			
		} finally {
			
		}
		
	}
	
	
	public boolean checkConsistency() {
		
		for (Vertex v : vertices) {
			v.check();
			
			/*
			 * there should only be 1 vertex with this point
			 */
			int count = 0;
			for (Vertex w : vertices) {
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
