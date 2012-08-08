package com.gutabi.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.gutabi.core.QuadTree.SegmentIndex;


public class Graph {
	
	private final ArrayList<Edge> edges = new ArrayList<Edge>();
	private final ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private final QuadTree segTree = new QuadTree();
	
	private final VertexHandler h;
	
	public Graph(VertexHandler h) {
		this.h = h;
	}
	
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
		h.vertexCreated(v);
		return v;
	}
	
	public void removeVertex(Vertex v) {
		h.vertexRemoved(v);
		v.remove();
		vertices.remove(v);
	}
	
	public void removeEdge(Edge e) {
		edges.remove(e);
		segTree.removeEdge(e);
		e.remove();
	}
	
	public IntersectionInfo tryFindEdgeInfo(Point b) {
		//for (Edge e : getEdges()) {
		for (SegmentIndex in : segTree.findAllSegments(b)) {
			Edge e = in.edge;
			Point c = e.getPoint(in.index);
			Point d = e.getPoint(in.index+1);
			if (Point.intersect(b, c, d)) {
				return new IntersectionInfo(e, in.index, Point.param(b, c, d));
			}
		}
		for (Vertex v : getVertices()) {
			Point d = v.getPoint();
			if (Point.equals(b, d) && v.getEdges().size() > 1) {
				throw new IllegalArgumentException("point is on vertex");
			}
		}
		return null;
	}
	
	public IntersectionInfo tryFindEdgeInfoD(DPoint b) {
		for (SegmentIndex in : segTree.findAllSegments(b, b)) {
			Edge e = in.edge;
			int i = in.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			if (Point.intersect(b, c, d)) {
				return new IntersectionInfo(e, i, Point.param(b, c, d));
			}
		}
		for (Vertex v : getVertices()) {
			Point d = v.getPoint();
			if (Point.doubleEquals(b.x, d.x) && Point.doubleEquals(b.y, d.y)) {
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
			if (d != null && b.equals(d)) {
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
	
	public void processStroke(DPoint a, DPoint b) {
		try {
			
			assert !DPoint.equals(a, b);
			
			SortedSet<PointToBeAdded> betweenABPoints = new TreeSet<PointToBeAdded>(PointToBeAdded.ptbaComparator);
			betweenABPoints.add(new PointToBeAdded(a, 0.0));
			betweenABPoints.add(new PointToBeAdded(b, 1.0));
			
			try {
				addBetweenPoints(a, b, betweenABPoints);
			} catch (OverlappingException ex) {
				return;
			}
			
			Point aInt = new Point((int)a.x, (int)a.y);
			Point bInt = new Point((int)b.x, (int)b.y);
			
			if (betweenABPoints.size() == 2 && Point.equalsD(a, aInt) && Point.equalsD(b, bInt)) {
				addSegment(aInt, bInt);
			} else {
				iterateBetweenPoints(betweenABPoints);
			}
			
		} finally {
			
		}
	}
	
	private void addBetweenPoints(DPoint a, DPoint b, SortedSet<PointToBeAdded> betweenABPoints) throws OverlappingException {
		
		for (SegmentIndex in : segTree.findAllSegments(a, b)) {
			Edge e = in.edge;
			int i = in.index;
			DPoint c = new DPoint(e.getPoint(i));
			DPoint d = new DPoint(e.getPoint(i+1));
			try {
				DPoint inter = Point.intersection(a, b, c, d);
				if (inter != null) {
					PointToBeAdded nptba = new PointToBeAdded(inter, Point.param(inter, a, b));
					betweenABPoints.add(nptba);
				}
			} catch (OverlappingException ex) {
				
				if ((DPoint.equals(a, d) || Point.intersect(a, c, d)) && (DPoint.equals(b, d) || Point.intersect(b, c, d))) {
					/*
					 * <a, b> is completely within <c, d>, so do nothing
					 */
					throw ex;
				}
				
				if (Point.intersect(c, a, b)) {
					double cParam = Point.param(c, a, b);
					if ((cParam > 0.0 && cParam < 1.0)) {
						PointToBeAdded nptba = new PointToBeAdded(new DPoint(c.x, c.y), cParam);
						betweenABPoints.add(nptba);
					}
				}
				
				if (Point.intersect(d, a, b)) {
					double dParam = Point.param(d, a, b);
					if ((dParam > 0.0 && dParam < 1.0)) {
						PointToBeAdded nptba = new PointToBeAdded(new DPoint(d.x, d.y), dParam);
						betweenABPoints.add(nptba);
					}
				}
				
			}
		}
		
	}
	
	private boolean segmentOverlapsOrIntersects(DPoint a, DPoint b) {
		
		for (SegmentIndex in : segTree.findAllSegments(a, b)) {
			Edge e = in.edge;
			int i = in.index;
			DPoint c = new DPoint(e.getPoint(i));
			DPoint d = new DPoint(e.getPoint(i+1));
			try {
				DPoint inter = Point.intersection(a, b, c, d);
				if (inter != null && !(DPoint.equals(inter, c) || DPoint.equals(inter, d))) {
					return true;
				}
			} catch (OverlappingException ex) {	
				return true;
			}
		}
		
		return false;
	}
	
	private void iterateBetweenPoints(SortedSet<PointToBeAdded> betweenABPoints) {
		
		Object[] betweenArray = betweenABPoints.toArray();
		
		betweenLoop:
		for (int i = 0; i < betweenArray.length-1; i++) {
			PointToBeAdded p1 = (PointToBeAdded)betweenArray[i];
			PointToBeAdded p2 = (PointToBeAdded)betweenArray[i+1];
			
			for (SegmentIndex in : segTree.findAllSegments(p1.p, p2.p)) {
				Edge e = in.edge;
				int j = in.index;
				Point c = e.getPoint(j);
				Point d = e.getPoint(j+1);
				if ((Point.equalsD(p1.p, d) || Point.intersect(p1.p, c, d)) && (Point.equalsD(p2.p, d) || Point.intersect(p2.p, c, d))) {
					/*
					 * <p1, p2> is completely within <c, d>, so do nothing
					 */
					continue betweenLoop;
				}
			}
			
			DPoint p1Int = new DPoint(Math.round(p1.p.x), Math.round(p1.p.y));
			
			// b is not yet an integer
			DPoint p2Int = new DPoint(Math.round(p2.p.x), Math.round(p2.p.y));
			
			if (!DPoint.equals(p1Int, p2Int)) {
				
				boolean adjusted = false;
				
				if (!DPoint.equals(p1.p, p1Int) && tryFindEdgeInfoD(p1.p) != null) {
					adjustToGrid(p1.p);
					adjusted = true;
				}
				
				if (!DPoint.equals(p2.p, p2Int) && tryFindEdgeInfoD(p2.p) != null) {
					adjustToGrid(p2.p);
					adjusted = true;
				}
				
				/*
				 * segment could have been added while adjusting
				 */
				if (!adjusted) {
					
					if (!segmentOverlapsOrIntersects(p1Int, p2Int)) {
						
						Point a = new Point((int)p1Int.x, (int)p1Int.y);
						Point b = new Point((int)p2Int.x, (int)p2Int.y);
						
						addSegment(a, b);
						
					} else {
						processStroke(p1Int, p2Int);
					}
					
				} else {
					processStroke(p1Int, p2Int);
				}
			}
			
		}
		
	}
	
	public void addSegment(Point a, Point b) {
		
		try {
			
			assert !Point.equals(a, b);
			assert !segmentExists(a, b) : "segment " + a + " " + b + " already exists";
			
			Vertex aV = tryFindVertex(a);
			if (aV == null) {
				IntersectionInfo intInfo = tryFindEdgeInfo(a);
				if (intInfo == null) {
					aV = createVertex(a);
				} else {
					aV = split(a);
				}
			}
			
			Vertex bV = tryFindVertex(b);
			if (bV == null) {
				IntersectionInfo intInfo = tryFindEdgeInfo(b);
				if (intInfo == null) {
					bV = createVertex(b);
				} else {
					bV = split(b);
				}
			}
			
			Edge e = createEdge(aV, bV, aV.getPoint(), bV.getPoint());
			
			aV.add(e);
			bV.add(e);
			
			Edge working;
			
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
			} else {
				working = e;
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
					merge(working, bEdge);
				}
			}
			
		} finally {
			
		}
	}
	
	public boolean segmentExists(Point a, Point b) {
		for (SegmentIndex in : segTree.findAllSegments(a, b)) {
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
		
		IntersectionInfo info = tryFindEdgeInfo(p);
		assert info != null;
		
		Edge e = info.edge;
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
			
			v.add(f);
			v.add(f);
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
					
					cV.add(f3);
					v.add(f3);
					
				}
				pts.add(p);
				
				f1 = createEdge(eStart, v, pts);
				
				eStart.add(f1);
				v.add(f1);
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
					
					dV.add(f3);
					v.add(f3);
					
				}
				for (int i = index+2; i < e.size(); i++) {
					pts.add(e.getPoint(i));
				}
				
				f2 = createEdge(v, eEnd, pts);
				
				v.add(f2);
				eEnd.add(f2);
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
	 * return Vertex at split point
	 */
	public void adjustToGrid(DPoint p) {
		
		try {
			
			IntersectionInfo info = tryFindEdgeInfoD(p);
			assert info != null;
			
			Edge e = info.edge;
			int index = info.index;
			double param = info.param;
			
			assert param >= 0.0;
			assert param < 1.0;
			
			/*
			 * we assert thar param < 1.0, but after adjusting, we may be at d
			 */
			
			Point c = e.getPoint(index);
			Point d = e.getPoint(index+1);
			
			Point pInt = new Point((int)Math.round(p.x), (int)Math.round(p.y));
			
			if (Point.equals(c, pInt) || Point.equals(d, pInt)) {
				/*
				 * nothing being adjusted
				 */
				return;
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
				processStroke(new DPoint(c), new DPoint(pInt));
			}
			if (!Point.equals(pInt, d)) {
				/*
				 * the segment <pInt, d> may intersect with other segments, so we have to start fresh and
				 * not assume anything
				 */
				processStroke(new DPoint(pInt), new DPoint(d));
			}
			
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
			// in the middle of merging a loop
			
			if (e1Start.getEdges().size() == 2 && e1End.getEdges().size() == 2) {
				// stand-alone loop
				
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
				
			} else {
				throw new AssertionError("vertices of stand-alone loop have more than 2 edges");
			}
			
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
				
				e1End.add(newEdge);
				e1End.removeEdge(e1);
				
				e1Start.removeEdge(e1);
				
				e2Start.add(newEdge);
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
				
				e1Start.add(newEdge);
				e1Start.removeEdge(e1);
				
				e1End.removeEdge(e1);
				
				e2Start.removeEdge(e2);
				
				e2End.add(newEdge);
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
				
				Edge newEdge = createEdge(null, null, pts);
				
				removeVertex(e1Start);
				removeVertex(e1End);
				removeEdge(e1);
				removeEdge(e2);
				
				return newEdge;
				
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
				
				e1End.add(newEdge);
				e1End.removeEdge(e1);
				
				e2Start.removeEdge(e2);
				
				e2End.add(newEdge);
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
				e1Start.add(newEdge);
				
				e1End.removeEdge(e1);
				
				e2Start.removeEdge(e2);
				e2Start.add(newEdge);
				
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
			e1End.add(newEdge);
			
			e2Start.removeEdge(e2);
			
			e2End.removeEdge(e2);
			e2End.add(newEdge);
			
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
			e1End.add(newEdge);
			
			e2Start.removeEdge(e2);
			e2Start.add(newEdge);
			
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
			e1Start.add(newEdge);
			
			e1End.removeEdge(e1);
			
			e2Start.removeEdge(e2);
			
			e2End.removeEdge(e2);
			e2End.add(newEdge);
			
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
			e1Start.add(newEdge);
			
			e1End.removeEdge(e1);
			
			e2Start.removeEdge(e2);
			e2Start.add(newEdge);
			
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
			
			SegmentIndex info = segTree.findSegment(a, b);
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
				
				newStart.add(newEdge);
				newEnd.add(newEdge);
				
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
						
						newStart.add(newEdge);
						eEnd.add(newEdge);
						
					}
					
				} else if (index == e.size()-2) {
					
					List<Point> pts = new ArrayList<Point>();
					
					for (int i = 0; i < e.size()-1; i++) {
						pts.add(e.getPoint(i));
					}
					
					Vertex newEnd = createVertex(e.getPoint(e.size()-2));
					
					Edge newEdge = createEdge(eStart, newEnd, pts);
					
					eStart.add(newEdge);
					newEnd.add(newEdge);
					
				} else {
					//create 2 new edges without worrying about vertices
					
					List<Point> f1Pts = new ArrayList<Point>();
					
					for (int i = 0; i <= index; i++) {
						f1Pts.add(e.getPoint(i));
					}
					
					Vertex newF1End = createVertex(e.getPoint(index));
					
					Edge f1 = createEdge(eStart, newF1End, f1Pts);
					
					eStart.add(f1);
					newF1End.add(f1);
					
					List<Point> f2Pts = new ArrayList<Point>();
					
					for (int i = index+1; i < e.size(); i++) {
						f2Pts.add(e.getPoint(i));
					}
					
					Vertex newF2Start = createVertex(e.getPoint(index+1));
					
					Edge f2 = createEdge(newF2Start, eEnd, f2Pts);
					
					newF2Start.add(f2);
					eEnd.add(f2);
					
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
							DPoint inter = Point.intersection(a, b, c, d);
							if (inter != null && !(Point.equalsD(inter, a) || Point.equalsD(inter, b) || Point.equalsD(inter, c) || Point.equalsD(inter, d))) {
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
