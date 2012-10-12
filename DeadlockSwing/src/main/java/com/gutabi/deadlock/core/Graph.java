package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
	
	final ArrayList<Edge> edges = new ArrayList<Edge>();
	private final ArrayList<Intersection> intersections = new ArrayList<Intersection>();
	final QuadTree segTree = new QuadTree();
	
	public final ArrayList<Source> sources = new ArrayList<Source>();
	public final ArrayList<Sink> sinks = new ArrayList<Sink>();
	
//	private static final Logger logger = Logger.getLogger(Graph.class);
	
	public Graph() {
		
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
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	private List<Vertex> getAllVertices() {
		List<Vertex> all = new ArrayList<Vertex>();
		all.addAll(sources);
		all.addAll(sinks);
		all.addAll(intersections);
		return all;
	}
	
	private Edge createEdge(Vertex start, Vertex end, List<Point> pts) {
		
		Edge e = new Edge(start, end, pts);
		edges.add(e);
		segTree.addEdge(e);
		
		refreshEdgeIDs();
		
		//logger.debug("createEdge: " + start + " " + end + " n=" + pts.size() );
		
		if (!e.isStandAlone()) {
			start.addEdge(e);
			end.addEdge(e);
		}
		
		return e;
	}
	
	void postEdgeChange(Vertex v) {
		
		if (v instanceof Intersection) {
			
			List<Edge> cons = v.getEdges();
			
			for (Edge e : cons) {
				assert edges.contains(e);
			}
			
			if (cons.size() == 0) {
				destroyVertex(v);
			} else if (cons.size() == 2) {
				merge(v);
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
	
	private Vertex createIntersection(Point p) {
		
		Intersection i = new Intersection(p);
		assert !intersections.contains(i);
		intersections.add(i);
		
		refreshVertexIDs();
		
		return i;
	}
	
	public void addSource(Source s) {
		sources.add(s);
		refreshVertexIDs();
	}
	
	public void addSink(Sink s) {
		sinks.add(s);
		refreshVertexIDs();
	}
	
	void destroyVertex(Vertex v) {
		
		if (intersections.contains(v)) {
			intersections.remove(v);
		} else if (sources.contains(v)) {
			sources.remove(v);
		} else if (sinks.contains(v)) {
			sinks.remove(v);
		} else {
			assert false;
		}
		
		refreshVertexIDs();
		
		v.remove();
	}
	
	void destroyEdge(Edge e) {
		assert edges.contains(e);
		
		//logger.debug("destroy Edge: " + e);
		
		if (!e.isStandAlone()) {
			e.getStart().removeEdge(e);
			e.getEnd().removeEdge(e);
		}
		
		edges.remove(e);
		segTree.removeEdge(e);
		
		refreshEdgeIDs();
		
		e.remove();
	}
	
	Vertex[] vertexIDs;
	
	private void refreshVertexIDs() {
		int vertexCount = intersections.size() + sources.size() + sinks.size();
		vertexIDs = new Vertex[vertexCount];
		int id = 0;
		for (Vertex v : sources) {
			v.id = id;
			vertexIDs[id] = v;
			id++;
		}
		for (Vertex v : sinks) {
			v.id = id;
			vertexIDs[id] = v;
			id++;
		}
		for (Vertex v : intersections) {
			v.id = id;
			vertexIDs[id] = v;
			id++;
		}
	}
	
	private void refreshEdgeIDs() {
		int id = 0;
		for (Edge e : edges) {
			e.id = id;
			id++;
		}
	}
	
	
	
	
	
	
	double[][] distances;
	Vertex[][] nextHighest;
	boolean[][] neighbors;
	
	public void preStart() {
		
		/*
		 * Floyd-Warshall
		 */
		
		int vertexCount = intersections.size() + sources.size() + sinks.size();
		
		distances = new double[vertexCount][vertexCount];
		nextHighest = new Vertex[vertexCount][vertexCount];
		neighbors = new boolean[edges.size()][edges.size()];
		
		/*
		 * initialize
		 */
		for (int i = 0; i < distances.length; i++) {
			Arrays.fill(distances[i], Double.POSITIVE_INFINITY);
		}
		for (int i = 0; i < distances.length; i++) {
			distances[i][i] = 0.0;
		}
		for (int i = 0; i < neighbors.length; i++) {
			Arrays.fill(neighbors[i], false);
		}
		
		
		/*
		 * iterate and find shorter distances via edges
		 */
		for(Edge e : edges) {
			double l = e.getTotalLength();
			double cur = distances[e.getStart().id][e.getEnd().id];
			/*
			 * there may be multiple edges between start and end, so don't just blindly set it to l
			 */
			if (l < cur) {
				distances[e.getStart().id][e.getEnd().id] = l;
		    	distances[e.getEnd().id][e.getStart().id] = l;
			}
			
			/*
			 * initialize neighbors
			 */
			neighbors[e.id][e.id] = true;
		}
		
		for (int k = 0; k < vertexCount; k++){
			for (int i = 0; i < vertexCount; i++){
				for (int j = 0; j < vertexCount; j++){
					if (distances[i][k] + distances[k][j] < distances[i][j]) {
						distances[i][j] = distances[i][k] + distances[k][j];
						nextHighest[i][j] = vertexIDs[k];
					}
				}
			}
		}
		
		for (Vertex v : sources) {
			List<Edge> eds = v.getEdges();
			for (int i = 0; i < eds.size()-1; i++) {
				for (int j = i+1; j < eds.size(); j++) {
					Edge ei = eds.get(i);
					Edge ej = eds.get(j);
					neighbors[ei.id][ej.id] = true;
					neighbors[ej.id][ei.id] = true;
				}
			}
		}
		for (Vertex v : sinks) {
			List<Edge> eds = v.getEdges();
			for (int i = 0; i < eds.size()-1; i++) {
				for (int j = i+1; j < eds.size(); j++) {
					Edge ei = eds.get(i);
					Edge ej = eds.get(j);
					neighbors[ei.id][ej.id] = true;
					neighbors[ej.id][ei.id] = true;
				}
			}
		}
		for (Vertex v : intersections) {
			List<Edge> eds = v.getEdges();
			for (int i = 0; i < eds.size()-1; i++) {
				for (int j = i+1; j < eds.size(); j++) {
					Edge ei = eds.get(i);
					Edge ej = eds.get(j);
					neighbors[ei.id][ej.id] = true;
					neighbors[ej.id][ei.id] = true;
				}
			}
		}
		
	}
	
	public List<Vertex> shortestPath(final Vertex start, Vertex end) {
		if (start == end) {
			throw new IllegalArgumentException();
		}
		
		int sid = start.id;
		int eid = end.id;
		
		if (distances[sid][eid] == Double.POSITIVE_INFINITY) {
			return null;
		}
		
		List<Vertex> vs = new ArrayList<Vertex>();
		vs.add(start);
		vs.addAll(intermediateVertices(start, end));
		vs.add(end);
		
		return vs;
	}
	
	/**
	 * returns the next choice to make for the shortest path from start to end
	 */
	public Vertex shortestPathChoice(final Vertex start, Vertex end) {
		
		int sid = start.id;
		int eid = end.id;
		
		if (distances[sid][eid] == Double.POSITIVE_INFINITY) {
			return null;
		}
		
		Vertex n = nextHighest[sid][eid];
		
		if (n == null) {
			return end;
		}
		
		return shortestPathChoice(start, n);
	}
	
	private List<Vertex> intermediateVertices(Vertex start, Vertex end) {
		
		int sid = start.id;
		int eid = end.id;
		
		Vertex n = nextHighest[sid][eid];
		
		if (n == null) {
			return new ArrayList<Vertex>();
		}
		
		List<Vertex> vs = new ArrayList<Vertex>();
		vs.addAll(intermediateVertices(start, n));
		vs.add(n);
		vs.addAll(intermediateVertices(n, end));
		
		return vs;
	}
	
	public double distanceBetweenVertices(Vertex a, Vertex b) {
		return distances[a.id][b.id];
	}
	
	public boolean areNeighbors(Edge a, Edge b) {
		return neighbors[a.id][b.id];
	}
	
	
	
	
	
	
	
	
	public GraphPosition graphHitTest(Point p) {
		for (Intersection v : intersections) {
			if (p.equals(v.getPoint())) {
				return new VertexPosition(v);
			}
		}
		for (Source s : sources) {
			if (p.equals(s.getPoint())) {
				return new VertexPosition(s);
			}
		}
		for (Sink s : sinks) {
			if (p.equals(s.getPoint())) {
				return new VertexPosition(s);
			}
		}
		for (Segment in : segTree.getAllSegments()) {
			Edge e = in.edge;
			int i = in.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			if (Point.intersect(p, c, d)) {
				double param = Point.param(p, c, d);
				if (DMath.equals(param, 1.0)) {
					return new EdgePosition(e, i+1, 0.0);
				} else {
					return new EdgePosition(e, i, param);
				}
			}
		}
		return null;
	}
	
	/**
	 * returns the closest vertex within radius that is not the excluded point
	 */
	private VertexPosition findClosestVertexPosition(Point a, Point anchor, double radius, boolean onlyDeleteables) {
		Vertex anchorV = null;
		if (anchor != null) {
			GraphPosition pos = graphHitTest(anchor);
			if (pos instanceof VertexPosition) {
				VertexPosition poss = (VertexPosition)pos;
				anchorV = poss.getVertex();
				if (a.equals(anchor) && (!onlyDeleteables || anchorV instanceof Intersection)) {
					/*
					 * a equals anchor, so starting this segment
					 * if exactly on a vertex, then use that one
					 */
					return poss;
				}
			}
		}
		
		Vertex closest = null;
		
		for (Vertex i : getAllVertices()) {
			Point ip = i.getPoint();
			double dist = Point.distance(a, ip);
			if (DMath.lessThanEquals(dist, radius)) {
				if (anchorV == null || dist < Point.distance(ip, anchor)) {
					if (closest == null || (Point.distance(a, ip) < Point.distance(a, closest.getPoint()))) {
						if (!onlyDeleteables || i instanceof Intersection) {
							closest = i;
						}
					}
				}
			}	
		}
		
		if (closest != null) {
			return new VertexPosition(closest);
		} else {
			if (radius == Double.POSITIVE_INFINITY) {
				String.class.getName();
			}
			return null;
		}
	}
	
	/**
	 * returns the closest edge within radius that is not in the excluded radius
	 */
	private EdgePosition findClosestEdgePosition(Point a, Point exclude, double radius) {
		return segTree.findClosestEdgePosition(a, exclude, radius);
	}
	
	public GraphPosition findClosestPosition(Point a, Point anchor, double radius, boolean onlyDeleteables) {
		VertexPosition closestV = findClosestVertexPosition(a, anchor, radius, onlyDeleteables);
		if (closestV != null) {
			return closestV;
		}
		EdgePosition closestEdge = findClosestEdgePosition(a, anchor, radius);
		if (closestEdge != null) {
			return closestEdge;
		}
		return null;
	}
	
	public void addSegment(Point a, Point b) {
			
		assert !a.equals(b);
		assert !segmentOverlaps(a, b);
		
//		logger.debug("addSegment: " + a + " " + b);
		
		GraphPosition pos = graphHitTest(a);
		final Vertex aV;
		if (pos != null) {
			if (pos instanceof VertexPosition) {
				aV = ((VertexPosition)pos).getVertex();
			} else {
				aV = split((EdgePosition)pos);
			}
		} else {
			aV = createIntersection(a);
		}
		
		pos = graphHitTest(b);
		final Vertex bV;
		if (pos != null) {
			if (pos instanceof VertexPosition) {
				bV = ((VertexPosition)pos).getVertex();
			} else {
				bV = split((EdgePosition)pos);
			}
		} else {
			bV = createIntersection(b);
		}
		
		List<Point> pts = new ArrayList<Point>();
		pts.add(aV.getPoint());
		pts.add(bV.getPoint());
		Edge newEdge = createEdge(aV, bV, pts);
		
		assert edges.contains(newEdge);
		
		postEdgeChange(aV);
		postEdgeChange(bV);
	}
	
	private boolean segmentOverlaps(Point a, Point b) {
		
		for (Segment in : segTree.getAllSegments()) {
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
	
	/**
	 * split an edge at point
	 * Edge e will have been removed
	 * return Intersection at split point
	 */
	private Vertex split(final EdgePosition pos) {
		
		Edge e = pos.getEdge();
		int index = pos.getIndex();
		double param = pos.getParam();
		final Point p = pos.getPoint();
		
		boolean betweenPoints = (!DMath.equals(param, 0.0));
		
		assert param >= 0.0;
		assert param < 1.0;
		
		final Point c = e.getPoint(index);
		final Point d = e.getPoint(index+1);
		
		Vertex v = createIntersection(p);
		
		Vertex eStart = e.getStart();
		Vertex eEnd = e.getEnd();
		
		if (eStart == null && eEnd == null) {
			// stand-alone loop
			
			List<Point> pts = new ArrayList<Point>();
			
			int colinearCount = 0;
			
			pts.add(p);
			try {
				if (index+1 < e.size()-1) {
					if (!Point.colinear(p, d, e.getPoint(index+2))) {
						pts.add(d);
					} else {
						colinearCount++;
					}
				} else if (index+1 == e.size()-1) {
					if (!Point.colinear(p, d, e.getPoint(1))) {
						pts.add(d);
					} else {
						colinearCount++;
					}
				}
			} catch (ColinearException ex) {
				assert false;
			}
			// just changed i < e.size()-1; to i < e.size();
			for (int i = index+2; i < e.size(); i++) {
				pts.add(e.getPoint(i));
			}
			// just changed i = 0 to i = 1
			for (int i = 1; i < index; i++) {
				pts.add(e.getPoint(i));
			}
			try {
				if (betweenPoints) {
					if (index > 0) {
						if (!Point.colinear(e.getPoint(index-1), c, p)) {
							pts.add(c);
						} else {
							colinearCount++;
						}
					} else if (index == 0) {
						/*
						 * c has already been added
						 */
					}
				}
			} catch (ColinearException ex) {
				assert false;
			}
			/*
			 * if p is exactly the beginning of e (and so also the end), then it has already been added
			 */
			if (!(index == 0 && !betweenPoints)) {
				pts.add(p);
			}
			
			Edge newEdge = createEdge(v, v, pts);
			
			assert newEdge.size() == e.size() - colinearCount + (betweenPoints ? 1 : 0);
			
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
				
				//assert tryFindVertex(c) == null;
				
				Vertex cV = createIntersection(c);
				List<Point> pts2 = new ArrayList<Point>();
				pts2.add(c);
				pts2.add(p);
				createEdge(cV, v, pts2);
				
			}
			pts.add(p);
			
			f1 = createEdge(eStart, v, pts);
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
				
				//assert tryFindVertex(d) == null;
				
				Vertex dV = createIntersection(d);
				List<Point> pts2 = new ArrayList<Point>();
				pts2.add(d);
				pts2.add(p);
				createEdge(dV, v, pts2);
				
			}
			for (int i = index+2; i < e.size(); i++) {
				pts.add(e.getPoint(i));
			}
			
			f2 = createEdge(v, eEnd, pts);
		}
		
		assert f1.size() + f2.size() == e.size() + 1 + (betweenPoints ? 1 : 0);
		
		destroyEdge(e);
		
		return v;
	}
	
	/**
	 * merge two edges (possibly the same edge) at the given intersection
	 * 
	 * no colinear points in returned edge
	 */
	private void merge(Vertex v) {
		
		assert v.getEdges().size() == 2;
		Edge e1 = v.getEdges().get(0);
		Edge e2 = v.getEdges().get(1);
		
		assert !e1.isRemoved();
		assert !e2.isRemoved();
		assert !v.isRemoved();
		assert edges.contains(e1);
		assert edges.contains(e2);
		
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
			
			int colinearCount = 0;
			
			int n = e1.size();
			assert e1.getPoint(0) == e1.getPoint(n-1);
			
			// only add if not colinear
			try {
				if (!Point.colinear(e1.getPoint(e1.size()-2), e1.getPoint(0), e1.getPoint(1))) {
					pts.add(e1.getPoint(0));
				} else {
					colinearCount++;
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
			
			assert newEdge.size() == e1.size() - colinearCount;
			
			destroyEdge(e1);
			
			destroyVertex(v);
			
		} else if (v == e1Start && v == e2Start) {
			
			List<Point> pts = new ArrayList<Point>();
			
			int colinearCount = 0;
			
			for (int i = e1.size()-1; i > 0; i--) {
				pts.add(e1.getPoint(i));
			}
			assert e1.getPoint(0).equals(e2.getPoint(0));
			try {
				// only add if not colinear
				if (!Point.colinear(e1.getPoint(1), e1.getPoint(0), e2.getPoint(1))) {
					pts.add(e1.getPoint(0));
				} else {
					colinearCount++;
				}
			} catch (ColinearException ex) {
				assert false;
			}
			for (int i = 1; i < e2.size(); i++) {
				pts.add(e2.getPoint(i));
			}
			
			Edge newEdge = createEdge(e1End, e2End, pts);
			
			assert newEdge.size() == e1.size() + e2.size() - 1 - colinearCount;
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		} else if (v == e1Start && v == e2End) {
			
			List<Point> pts = new ArrayList<Point>();
			
			int colinearCount = 0;
			
			for (int i = e1.size()-1; i > 0; i--) {
				pts.add(e1.getPoint(i));
			}
			assert e1.getPoint(0).equals(e2.getPoint(e2.size()-1));
			try {
				// only add if not colinear
				if (!Point.colinear(e1.getPoint(1), e1.getPoint(0), e2.getPoint(e2.size()-2))) {
					pts.add(e1.getPoint(0));
				} else {
					colinearCount++;
				}
			} catch (ColinearException ex) {
				assert false;
			}
			for (int i = e2.size()-2; i >= 0; i--) {
				pts.add(e2.getPoint(i));
			}
			
			Edge newEdge = createEdge(e1End, e2Start, pts);
			
			assert newEdge.size() == e1.size() + e2.size() - 1 - colinearCount; 
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		} else if (v == e1End && v == e2Start) {
			
			List<Point> pts = new ArrayList<Point>();
			
			int colinearCount = 0;
			
			for (int i = 0; i < e1.size()-1; i++) {
				pts.add(e1.getPoint(i));
			}
			assert e1.getPoint(e1.size()-1).equals(e2.getPoint(0));
			try {
				// only add if not colinear
				if (!Point.colinear(e1.getPoint(e1.size()-2), e1.getPoint(e1.size()-1), e2.getPoint(1))) {
					pts.add(e1.getPoint(e1.size()-1));
				} else {
					colinearCount++;
				}
			} catch (ColinearException ex) {
				assert false;
			}
			for (int i = 1; i < e2.size(); i++) {
				pts.add(e2.getPoint(i));
			}
			
			Edge newEdge = createEdge(e1Start, e2End, pts);
			
			assert newEdge.size() == e1.size() + e2.size() - 1 - colinearCount;
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		} else {
			assert v == e1End && v == e2End;
			
			List<Point> pts = new ArrayList<Point>();
			
			int colinearCount = 0;
			
			for (int i = 0; i < e1.size()-1; i++) {
				pts.add(e1.getPoint(i));
			}
			assert e1.getPoint(e1.size()-1).equals(e2.getPoint(e2.size()-1));
			try {
				// only add if not colinear
				if (!Point.colinear(e1.getPoint(e1.size()-2), e1.getPoint(e1.size()-1), e2.getPoint(e2.size()-2))) {
					pts.add(e1.getPoint(e1.size()-1));
				} else {
					colinearCount++;
				}
			} catch (ColinearException ex) {
				assert false;
			}
			for (int i = e2.size()-2; i >= 0; i--) {
				pts.add(e2.getPoint(i));
			}
			
			Edge newEdge = createEdge(e1Start, e2Start, pts);
			
			assert newEdge.size() == e1.size() + e2.size() - 1 - colinearCount;
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
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
				if (v.getPoint().equals(w.getPoint())) {
					count++;
				}
			}
			assert count == 1;
		}
		
		for (Edge e : edges) {
			if (e.getTotalLength() <= MODEL.world.CAR_LENGTH) {
				throw new IllegalStateException("too small");
			}
			if (e.getStart() == null && e.getEnd() == null) {
				continue;
			}
			for (Edge f : edges) {
				if (e == f) {
					
					for (int i = 0; i < e.size()-2; i++) {
						Point a = e.getPoint(i);
						Point b = e.getPoint(i+1);
						for (int j = i+1; j < f.size()-1; j++) {
							Point c = f.getPoint(j);
							Point d = f.getPoint(j+1);
							try {
								Point inter = Point.intersection(a, b, c, d);
								if (inter != null && !(inter.equals(a) || inter.equals(b) || inter.equals(c) || inter.equals(d))) {
									//assert false : "No edges should intersect";
									throw new IllegalStateException("No edges should intersect");
								}
							} catch (OverlappingException ex) {
								throw new IllegalStateException("Segments overlapping: " + "<" + a + " " + b + "> and <" + c + " " + d + ">");
							}
						}
					}
					
				} else {
					
					for (int i = 0; i < e.size()-1; i++) {
						Point a = e.getPoint(i);
						Point b = e.getPoint(i+1);
						for (int j = 0; j < f.size()-1; j++) {
							Point c = f.getPoint(j);
							Point d = f.getPoint(j+1);
							try {
								Point inter = Point.intersection(a, b, c, d);
								if (inter != null && !(inter.equals(a) || inter.equals(b) || inter.equals(c) || inter.equals(d))) {
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
								if (eP.equals(fP)) {
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
		}
		
		return true;
	}
	
}
