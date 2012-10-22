package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
	
	private final ArrayList<Edge> edges = new ArrayList<Edge>();
	private final ArrayList<Intersection> intersections = new ArrayList<Intersection>();
	final QuadTree segTree = new QuadTree();
	
	private final ArrayList<Source> sources = new ArrayList<Source>();
	private final ArrayList<Sink> sinks = new ArrayList<Sink>();
	
//	private static final Logger logger = Logger.getLogger(Graph.class);
	
	public Graph() {
		
	}
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	public List<Source> getSources() {
		return sources;
	}
	
	public List<Vertex> getAllVertices() {
		List<Vertex> all = new ArrayList<Vertex>();
		all.addAll(sources);
		all.addAll(sinks);
		all.addAll(intersections);
		return all;
	}
	
	public List<Segment> getAllSegments() {
		return segTree.getAllSegments();
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
	
	public double distanceBetweenVertices(Vertex a, Vertex b) {
		return distances[a.id][b.id];
	}
	
	public boolean areNeighbors(Edge a, Edge b) {
		return neighbors[a.id][b.id];
	}
	
	/**
	 * tests any part of vertex and any part of edge
	 */
	public Entity hitTest(Point p) {
		assert p != null;
		for (Vertex v : getAllVertices()) {
			if (DMath.lessThanEquals(Point.distance(p, v.getPoint()), v.getRadius())) {
				return v;
			}
		}
		for (Segment in : segTree.getAllSegments()) {
			Edge e = in.edge;
			int i = in.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			if (DMath.lessThanEquals(Point.distance(p, c, d), Edge.ROAD_RADIUS)) {
				return e;
			}
		}
		return null;
	}
	
	public List<Entity> hitTest(Point p, double radius) {
		assert p != null;
		List<Entity> hits = new ArrayList<Entity>();
		for (Vertex v : getAllVertices()) {
			if (DMath.lessThanEquals(Point.distance(p, v.getPoint()), v.getRadius() + radius)) {
				hits.add(v);
			}
		}
		for (Segment in : segTree.getAllSegments()) {
			Edge e = in.edge;
			int i = in.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			if (DMath.lessThanEquals(Point.distance(p, c, d), Edge.ROAD_RADIUS + radius)) {
				hits.add(e);
			}
		}
		return hits;
	}
	
	/**
	 * hit test with capsule
	 */
//	public List<Entity> hitTest(Point a, Point b, double radius) {
//		
//	}
	
	public GraphPosition findClosestGraphPosition(Point p, double radius) {
		VertexPosition vp = findClosestVertexPosition(p, radius);
		if (vp != null) {
			return vp;
		}
		EdgePosition ep = findClosestEdgePosition(p, radius);
		if (ep != null) {
			return ep;
		}
		return null;
	}
	
	
	
	public void addSource(Source s) {
		sources.add(s);
		refreshVertexIDs();
	}
	
	public void addSink(Sink s) {
		sinks.add(s);
		refreshVertexIDs();
	}
	
	public void addIntersection(Intersection i) {
		intersections.add(i);
		refreshVertexIDs();
	}
	
	public void createEdgeTop(Vertex start, Vertex end, List<Point> pts) {
		
		createEdge(start, end, pts);
		
		postEdgeChange(start);
		postEdgeChange(end);
		
	}
	
	public void removeEdgeTop(Edge e) {
		
		/*
		 * have to properly cleanup start and end intersections before removing edges
		 */
		if (!e.isLoop()) {
			
			Vertex eStart = e.getStart();
			Vertex eEnd = e.getEnd();
			
			destroyEdge(e);
			
			postEdgeChange(eStart);
			postEdgeChange(eEnd);
			
		} else if (!e.isStandAlone()) {
			
			Vertex v = e.getStart();
			
			destroyEdge(e);
			
			postEdgeChange(v);
			
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
			postEdgeChange(a);
		}
		
	}
	
	/**
	 * split an edge at point
	 * Edge e will have been removed
	 * return Intersection at split point
	 */
	public Vertex split(final EdgePosition pos) {
		
		Edge e = pos.getEdge();
		int index = pos.getIndex();
		double param = pos.getParam();
		final Point p = pos.getPoint();
		
		assert param >= 0.0;
		assert param < 1.0;
		
		Vertex v = createIntersection(p);
		
		Vertex eStart = e.getStart();
		Vertex eEnd = e.getEnd();
		
		if (eStart == null && eEnd == null) {
			// stand-alone loop
			
			List<Point> pts = new ArrayList<Point>();
			
			pts.add(p);
			for (int i = index+1; i < e.size(); i++) {
				pts.add(e.getPoint(i));
			}
			for (int i = 0; i <= index; i++) {
				pts.add(e.getPoint(i));
			}
			pts.add(p);
			
			createEdge(v, v, pts);
			
			destroyEdge(e);
			
			return v;
		}
			
		List<Point> f1Pts = new ArrayList<Point>();
		
		for (int i = 0; i <= index; i++) {
			f1Pts.add(e.getPoint(i));
		}
		f1Pts.add(p);
		
		createEdge(eStart, v, f1Pts);
			
		List<Point> f2Pts = new ArrayList<Point>();
		
		f2Pts.add(p);
		for (int i = index+1; i < e.size(); i++) {
			f2Pts.add(e.getPoint(i));
		}
		
		createEdge(v, eEnd, f2Pts);
		
		destroyEdge(e);
		
		return v;
	}
	
	public void preStart() {
		
		initializeMatrices();
		
		for (Source s : sources) {
			s.preStart();
		}
	}
	
	private void postEdgeChange(Vertex v) {
		
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
		
		Intersection i = new Intersection(p, this);
		assert !intersections.contains(i);
		intersections.add(i);
		
		refreshVertexIDs();
		
		return i;
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
		
		refreshVertexIDs();
		
		v.remove();
	}
	
	private void destroyEdge(Edge e) {
		assert edges.contains(e);
		
		if (!e.isStandAlone()) {
			e.getStart().removeEdge(e);
			e.getEnd().removeEdge(e);
		}
		
		edges.remove(e);
		
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
	
	private void initializeMatrices() {
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
	
	/**
	 * returns the closest vertex within radius that is not the excluded point
	 */
	private VertexPosition findClosestVertexPosition(Point a, double radius) {
		
		Vertex closest = null;
		
		for (Vertex v : getAllVertices()) {
			Point vp = v.getPoint();
			double dist = Point.distance(a, vp);
			if (DMath.lessThanEquals(dist, radius + v.getRadius())) {
				if (closest == null || (Point.distance(a, vp) < Point.distance(a, closest.getPoint()))) {
					closest = v;
				}
			}	
		}
		
		if (closest != null) {
			return new VertexPosition(closest, this);
		} else {
			return null;
		}
	}
	
	/**
	 * returns the closest edge within radius that is not in the excluded radius
	 */
	private EdgePosition findClosestEdgePosition(Point a, double radius) {
		return segTree.findClosestEdgePosition(a, radius);
	}
	
	private void createEdge(Vertex start, Vertex end, List<Point> pts) {
		
		Edge e = new Edge(start, end, this, pts);
		edges.add(e);
		
		refreshEdgeIDs();
		
		if (!e.isStandAlone()) {
			start.addEdge(e);
			end.addEdge(e);
		}
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
			
			assert e1.getPoint(0) == e1.getPoint(e1.size()-1);
			
			for (int i = 0; i < e1.size(); i++) {
				pts.add(e1.getPoint(i));
			}
			
			createEdge(null, null, pts);
			
			destroyEdge(e1);
			
			destroyVertex(v);
			
		} else if (v == e1Start && v == e2Start) {
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = e1.size()-1; i >= 0; i--) {
				pts.add(e1.getPoint(i));
			}
			assert e1.getPoint(0).equals(e2.getPoint(0));
			for (int i = 0; i < e2.size(); i++) {
				pts.add(e2.getPoint(i));
			}
			
			createEdge(e1End, e2End, pts);
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		} else if (v == e1Start && v == e2End) {
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = e1.size()-1; i >= 0; i--) {
				pts.add(e1.getPoint(i));
			}
			assert e1.getPoint(0).equals(e2.getPoint(e2.size()-1));
			for (int i = e2.size()-1; i >= 0; i--) {
				pts.add(e2.getPoint(i));
			}
			
			createEdge(e1End, e2Start, pts); 
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		} else if (v == e1End && v == e2Start) {
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = 0; i < e1.size(); i++) {
				pts.add(e1.getPoint(i));
			}
			assert e1.getPoint(e1.size()-1).equals(e2.getPoint(0));
			for (int i = 0; i < e2.size(); i++) {
				pts.add(e2.getPoint(i));
			}
			
			createEdge(e1Start, e2End, pts);
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		} else {
			assert v == e1End && v == e2End;
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = 0; i < e1.size(); i++) {
				pts.add(e1.getPoint(i));
			}
			for (int i = e2.size()-1; i >= 0; i--) {
				pts.add(e2.getPoint(i));
			}
			
			createEdge(e1Start, e2Start, pts);
			
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
