package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("static-access")
public class Graph {
	
	private final ArrayList<Edge> edges = new ArrayList<Edge>();
	
	private final ArrayList<Intersection> intersections = new ArrayList<Intersection>();
	private final ArrayList<Source> sources = new ArrayList<Source>();
	private final ArrayList<Sink> sinks = new ArrayList<Sink>();
	
	public List<StopSign> signs = new ArrayList<StopSign>();
	
//	private static final Logger logger = Logger.getLogger(Graph.class);
	
	public Graph() {
		
	}
	
	private List<Vertex> getAllVertices() {
		List<Vertex> all = new ArrayList<Vertex>();
		all.addAll(sources);
		all.addAll(sinks);
		all.addAll(intersections);
		return all;
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
	
	public Edge createEdgeTop(Vertex start, Vertex end, List<Point> pts) {
		
		Edge e = createEdge(start, end, pts, (!start.eds.isEmpty()?1:0)+(!end.eds.isEmpty()?2:0));
		
		automaticMergeOrDestroy(start);
		automaticMergeOrDestroy(end);
		
		return e;
	}
	
	public void removeEdgeTop(Edge e) {
		
		/*
		 * have to properly cleanup start and end intersections before removing edges
		 */
		if (!e.isLoop()) {
			
			Vertex eStart = e.start;
			Vertex eEnd = e.end;
			
			destroyEdge(e);
			
			automaticMergeOrDestroy(eStart);
			automaticMergeOrDestroy(eEnd);
			
		} else if (!e.isStandAlone()) {
			
			Vertex v = e.start;
			
			destroyEdge(e);
			
			automaticMergeOrDestroy(v);
			
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
				
				Vertex eStart = e.start;
				Vertex eEnd = e.end;
				
				affectedVertices.add(eStart);
				affectedVertices.add(eEnd);
				
				destroyEdge(e);
				
			} else {
				
				Vertex eV = e.start;
				
				affectedVertices.add(eV);
				
				destroyEdge(e);
				
			}
		}
		
		destroyVertex(v);
		affectedVertices.remove(v);
		
		for (Vertex a : affectedVertices) {
			automaticMergeOrDestroy(a);
		}
		
	}
	
	public void removeStopSignTop(StopSign s) {
		
		destroyStopSign(s);
		
	}
	
	private void automaticMergeOrDestroy(Vertex v) {
		
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


	
	/**
	 * dec is a set of bit flags indicating what decorations to add to the new edge
	 * bit 0 set = add start stop sign
	 * bit 1 set = add end stop sign
	 */
	private Edge createEdge(Vertex start, Vertex end, List<Point> pts, int dec) {
		
		Edge e = new Edge(start, end, pts);
		edges.add(e);
		
		refreshEdgeIDs();
		
		if (!e.isStandAlone()) {
			
			start.addEdge(e);
			end.addEdge(e);
			
			if ((dec & 1) == 1) {
				StopSign startSign = new StopSign(e, 0);
				signs.add(startSign);
				e.startSign = startSign;
			}
			
			if ((dec & 2) == 2) {
				StopSign endSign = new StopSign(e, 1);
				signs.add(endSign);
				e.endSign = endSign;
			}

		}
		
		e.computeProperties();
		e.computePath();
		
		return e;
	}
	
	private void destroyEdge(Edge e) {
		assert edges.contains(e);
		
		if (!e.isStandAlone()) {
			e.start.removeEdge(e);
			e.end.removeEdge(e);
			
			destroyStopSign(e.startSign);
			destroyStopSign(e.endSign);
			
		}
		
		edges.remove(e);
		
		refreshEdgeIDs();
		
	}
	
	private void destroyStopSign(StopSign s) {
		if (s == null) {
			return;
		}
		
		if (s.e.startSign == s) {
			s.e.startSign = null;
		} else {
			assert s.e.endSign == s;
			s.e.endSign = null;
		}
		
		signs.remove(s);
	}
	
	private Vertex createIntersection(Point p) {
		
		Intersection i = new Intersection(p);
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
		
	}
	
	
	
	
	
	public void preStart() {
		
		initializeMatrices();
		
		for (Source s : sources) {
			s.preStart();
		}
	}
	
	public void computeVertexRadii() {
		
		List<Vertex> all = getAllVertices();
		for (int i = 0; i < all.size(); i++) {
			Vertex vi = all.get(i);
			double maximumRadius = Double.POSITIVE_INFINITY;
			for (int j = 0; j < all.size(); j++) {
				Vertex vj = all.get(j);
				if (vi == vj) {
					continue;
				}
				double max = Point.distance(vi.p, vj.p) - vj.getRadius();
				if (max < maximumRadius) {
					maximumRadius = max;
				}
			}
			vi.computeRadius(maximumRadius);
		}
		
	}
	
	public Object[] getRenderingRectCombo(double ulX, double ulY, double width, double height) {
		
		double brX = ulX + width;
		double brY = ulY + height;
		
		for (Vertex v : getAllVertices()) {
			if (v.aabbLoc.x < ulX) {
				ulX = v.aabbLoc.x;
			}
			if (v.aabbLoc.y < ulY) {
				ulY = v.aabbLoc.y;
			}
			Point renderingBottomRight = new Point(v.aabbLoc.x + v.aabbDim.width, v.aabbLoc.y + v.aabbDim.height);
			if (renderingBottomRight.x > brX) {
				brX = renderingBottomRight.x;
			}
			if (renderingBottomRight.y > brY) {
				brY = renderingBottomRight.y;
			}
		}
		for (Edge ed : edges) {
			if (ed.aabbLoc.x < ulX) {
				ulX = ed.aabbLoc.x;
			}
			if (ed.aabbLoc.y < ulY) {
				ulY = ed.aabbLoc.y;
			}
			Point renderingBottomRight = new Point(ed.aabbLoc.x + ed.aabbDim.width, ed.aabbLoc.y + ed.aabbDim.height);
			if (renderingBottomRight.x > brX) {
				brX = renderingBottomRight.x;
			}
			if (renderingBottomRight.y > brY) {
				brY = renderingBottomRight.y;
			}
		}
		
		/*
		 * signs do not contribute to the rendering rect
		 */
//		for (StopSign s : signs) {
//			
//		}
		
		Object[] combo = new Object[2];
		combo[0] = new Point(ulX, ulY);
		combo[1] = new Dim(brX - ulX, brY - ulY);
		
		return combo;
	}
	
	public void preStep(double t) {
		
		for (Vertex v : getAllVertices()) {
			v.preStep(t);
		}
		
	}
	
	
	
	
	
	
	
	
	
	public List<Vertex> shortestPath(Vertex start, Vertex end) {
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
	public Vertex shortestPathChoice(Vertex start, Vertex end) {
		
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
	
	public Vertex randomPathChoice(Edge prev, Vertex start, Vertex end) {
		
		List<Edge> eds = new ArrayList<Edge>(start.getEdges());
		
		for (Edge e : start.getEdges()) {
			if (prev != null && prev == e) {
				eds.remove(e);
			} else {
				
				Vertex other;
				if (start == e.start) {
					other = e.end;
				} else {
					other = e.start;
				}
				
				if (isDeadEnd(e, other, end)) {
					eds.remove(e);
				}
			}
		}
		
		int n = eds.size();
		
		Vertex v;
		Edge e;
		
		int r = MODEL.world.RANDOM.nextInt(n);
		
		e = eds.get(r);
		
		if (start == e.start) {
			v = e.end;
		} else {
			v = e.start;
		}
		
		return v;
	}
	
	public double distanceBetweenVertices(Vertex a, Vertex b) {
		return distances[a.id][b.id];
	}
	
	/**
	 * coming down edge e, is vertex v a dead end?
	 * 
	 * but if there is a way to end, then it is not a dead end
	 */
	public boolean isDeadEnd(Edge e, Vertex v, Vertex end) {
		return isDeadEnd(e, v, end, new ArrayList<Vertex>());
	}
	
	private boolean isDeadEnd(Edge e, Vertex v, Vertex end, List<Vertex> visited) {
		/*
		 * TODO: should figure this out along with distances
		 */
		if (v == end) {
			return false;
		}
		if (v.getEdges().size() == 1) {
			return true;
		} else {
			List<Edge> eds = new ArrayList<Edge>(v.getEdges());
			eds.remove(e);
			for (Edge ee : eds) {
				Vertex other;
				if (v == ee.start) {
					other = ee.end;
				} else {
					other = ee.start;
				}
				if (visited.contains(other)) {
					// know there is a loop, so no dead end
					return false;
				}
				List<Vertex> newVisited = new ArrayList<Vertex>(visited);
				newVisited.add(v);
				if (!isDeadEnd(ee, other, end, newVisited)) {
					return false;
				}
			}
			return true;
		}
	}
	
	
	
	/**
	 * tests any part of vertex and any part of edge
	 */
	public Entity hitTest(Point p) {
		assert p != null;
		for (StopSign s : signs) {
			if (s.hitTest(p)) {
				return s;
			}
		}
		for (Vertex v : getAllVertices()) {
			if (v.hitTest(p)) {
				return v;
			}
		}
		for (Edge e : edges) {
			if (e.hitTest(p)) {
				return e;
			}
		}
		return null;
	}
	
	public Set<Entity> hitTest(Point p, double radius) {
		assert p != null;
		Set<Entity> hits = new HashSet<Entity>();
		for (StopSign s : signs) {
			if (s.hitTest(p, radius)) {
				hits.add(s);
			}
		}
		for (Vertex v : getAllVertices()) {
			if (v.hitTest(p, radius)) {
				hits.add(v);
			}
		}
		for (Edge e : edges) {
			if (e.hitTest(p, radius)) {
				hits.add(e);
			}
		}
		return hits;
	}
	
	/**
	 * hit test with capsule
	 */
//	public Set<Entity> hitTest(Point a, Point b, double radius) {
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
	
	
	
	
	
	
	
	
	
	/**
	 * split an edge at point
	 * Edge e will have been removed
	 * return Intersection at split point
	 */
	public Vertex split(final EdgePosition pos) {
		
		Edge e = pos.e;
		int index = pos.index;
		double param = pos.param;
		final Point p = pos.p;
		
		assert param >= 0.0;
		assert param < 1.0;
		
		Vertex v = createIntersection(p);
		
		Vertex eStart = e.start;
		Vertex eEnd = e.end;
		
		if (eStart == null && eEnd == null) {
			// stand-alone loop
			
			List<Point> pts = new ArrayList<Point>();
			
			pts.add(p);
			for (int i = index+1; i < e.size(); i++) {
				pts.add(e.get(i));
			}
			for (int i = 0; i <= index; i++) {
				pts.add(e.get(i));
			}
			pts.add(p);
			
			createEdge(v, v, pts, 3);
			
			destroyEdge(e);
			
			return v;
		}
			
		List<Point> f1Pts = new ArrayList<Point>();
		
		for (int i = 0; i <= index; i++) {
			f1Pts.add(e.get(i));
		}
		f1Pts.add(p);
		
		createEdge(eStart, v, f1Pts, (e.startSign!=null?1:0)+2);
		
		List<Point> f2Pts = new ArrayList<Point>();
		
		f2Pts.add(p);
		for (int i = index+1; i < e.size(); i++) {
			f2Pts.add(e.get(i));
		}
		
		createEdge(v, eEnd, f2Pts, 1+(e.endSign!=null?2:0));
		
		destroyEdge(e);
		
		return v;
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
	
	private void initializeMatrices() {
		/*
		 * Floyd-Warshall
		 */
		
		int vertexCount = intersections.size() + sources.size() + sinks.size();
		
		distances = new double[vertexCount][vertexCount];
		nextHighest = new Vertex[vertexCount][vertexCount];
		
		/*
		 * initialize
		 */
		for (int i = 0; i < distances.length; i++) {
			Arrays.fill(distances[i], Double.POSITIVE_INFINITY);
		}
		for (int i = 0; i < distances.length; i++) {
			distances[i][i] = 0.0;
		}
		
		/*
		 * iterate and find shorter distances via edges
		 */
		for(Edge e : edges) {
			double l = e.getTotalLength();
			double cur = distances[e.start.id][e.end.id];
			/*
			 * there may be multiple edges between start and end, so don't just blindly set it to l
			 */
			if (l < cur) {
				distances[e.start.id][e.end.id] = l;
		    	distances[e.end.id][e.start.id] = l;
			}
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
		
		VertexPosition closest = null;
		
		for (Vertex v : getAllVertices()) {
			VertexPosition vp = v.findClosestVertexPosition(a, radius);
			if (vp != null) {
				if (closest == null || Point.distance(a, vp.p) < Point.distance(a, closest.p)) {
					closest = vp;
				}
			}
		}
		
		return closest;
	}
	
	/**
	 * returns the closest edge within radius
	 */
	private EdgePosition findClosestEdgePosition(Point a, double radius) {
		
		EdgePosition closest = null;
		
		for (Edge e : edges) {
			EdgePosition ep = e.findClosestEdgePosition(a, radius);
			if (ep != null) {
				if (closest == null || Point.distance(a, ep.p) < Point.distance(a, closest.p)) {
					closest = ep;
				}
			}
		}
		
		return closest;
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
		
		assert edges.contains(e1);
		assert edges.contains(e2);
		
		Vertex e1Start = e1.start;
		Vertex e1End = e1.end;
		
		Vertex e2Start = e2.start;
		Vertex e2End = e2.end;
		
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
			
			assert e1.get(0) == e1.get(e1.size()-1);
			
			for (int i = 0; i < e1.size(); i++) {
				pts.add(e1.get(i));
			}
			
			createEdge(null, null, pts, 0);
			
			destroyEdge(e1);
			
			destroyVertex(v);
			
		} else if (v == e1Start && v == e2Start) {
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = e1.size()-1; i >= 0; i--) {
				pts.add(e1.get(i));
			}
			assert e1.get(0).equals(e2.get(0));
			for (int i = 0; i < e2.size(); i++) {
				pts.add(e2.get(i));
			}
			
			createEdge(e1End, e2End, pts, (e1.startSign!=null?1:0) + (e2.endSign!=null?2:0));
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		} else if (v == e1Start && v == e2End) {
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = e1.size()-1; i >= 0; i--) {
				pts.add(e1.get(i));
			}
			assert e1.get(0).equals(e2.get(e2.size()-1));
			for (int i = e2.size()-1; i >= 0; i--) {
				pts.add(e2.get(i));
			}
			
			createEdge(e1End, e2Start, pts, (e1.endSign!=null?1:0) + (e2.startSign!=null?2:0)); 
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		} else if (v == e1End && v == e2Start) {
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = 0; i < e1.size(); i++) {
				pts.add(e1.get(i));
			}
			assert e1.get(e1.size()-1).equals(e2.get(0));
			for (int i = 0; i < e2.size(); i++) {
				pts.add(e2.get(i));
			}
			
			createEdge(e1Start, e2End, pts, (e1.startSign!=null?1:0) + (e2.endSign!=null?2:0));
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		} else {
			assert v == e1End && v == e2End;
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = 0; i < e1.size(); i++) {
				pts.add(e1.get(i));
			}
			for (int i = e2.size()-1; i >= 0; i--) {
				pts.add(e2.get(i));
			}
			
			createEdge(e1Start, e2Start, pts, (e1.startSign!=null?1:0) + (e2.startSign!=null?2:0));
			
			destroyEdge(e1);
			destroyEdge(e2);
			
			destroyVertex(v);
			
		}
	}
	
	
	
	public void renderBackground(Graphics2D g2) {
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		List<Edge> edgesCopy;
		List<Vertex> verticesCopy;
		List<StopSign> signsCopy;
		synchronized (MODEL) {
			edgesCopy = new ArrayList<Edge>(edges);
			verticesCopy = new ArrayList<Vertex>(getAllVertices());
			signsCopy = new ArrayList<StopSign>(signs);
		}
		
		AffineTransform origTransform = g2.getTransform();
		AffineTransform trans = (AffineTransform)origTransform.clone();
		trans.scale(MODEL.PIXELS_PER_METER, MODEL.PIXELS_PER_METER);
		g2.setTransform(trans);
		
		for (Edge e : edgesCopy) {
			e.paint(g2);
		}
		
		for (Vertex v : verticesCopy) {
			v.paint(g2);
		}
		
		for (StopSign s : signsCopy) {
			s.paint(g2);
		}
		
		g2.setTransform(origTransform);
		
	}
	
	public void paintStats(Graphics2D g2) {
		
		Point p = new Point(1, 1).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("vertex count: " + getAllVertices().size(), (int)p.x, (int)p.y);
		
		p = new Point(1, 2).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("edge count: " + edges.size(), (int)p.x, (int)p.y);
		
		p = new Point(1, 3).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("sign count: " + signs.size(), (int)p.x, (int)p.y);
		
	}
	
	public void paintScene(Graphics2D g2) {
		
		List<Edge> edgesCopy;
		
		synchronized (MODEL) {
			edgesCopy = new ArrayList<Edge>(edges);
		}
		
		for (Edge e : edgesCopy) {
			if (MODEL.DEBUG_DRAW) {
				e.paintSkeleton(g2);
				e.paintBorders(g2);
			}
		}
		
	}
	
	public void paintIDs(Graphics2D g2) {
		
		List<Vertex> verticesCopy;
		synchronized (MODEL) {
			verticesCopy = new ArrayList<Vertex>(getAllVertices());
		}
		
		if (MODEL.DEBUG_DRAW) {
			
			for (Vertex v : verticesCopy) {
				v.paintID(g2);
			}
			
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
				if (v.p.equals(w.p)) {
					count++;
				}
			}
			assert count == 1;
		}
		
		for (Edge e : edges) {
			if (e.start == null && e.end == null) {
				continue;
			}
			for (Edge f : edges) {
				if (e == f) {
					
					for (int i = 0; i < e.size()-2; i++) {
						EdgeSegment es = e.getSegment(i);
						
						for (int j = i+1; j < f.size()-1; j++) {
							EdgeSegment fs = f.getSegment(j);
							
							try {
								Point inter = Point.intersection(es.a, es.b, fs.a, fs.b);
								if (inter != null && !(inter.equals(es.a) || inter.equals(es.b) || inter.equals(fs.a) || inter.equals(fs.b))) {
									//assert false : "No edges should intersect";
									throw new IllegalStateException("No edges should intersect");
								}
							} catch (OverlappingException ex) {
								throw new IllegalStateException("Segments overlapping");
							}
						}
					}
					
				} else {
					
					for (int i = 0; i < e.size()-1; i++) {
						EdgeSegment es = e.getSegment(i);
						
						for (int j = 0; j < f.size()-1; j++) {
							EdgeSegment fs = f.getSegment(j);
							
							try {
								Point inter = Point.intersection(es.a, es.b, fs.a, fs.b);
								if (inter != null && !(inter.equals(es.a) || inter.equals(es.b) || inter.equals(fs.a) || inter.equals(fs.b))) {
									//assert false : "No edges should intersect";
									throw new IllegalStateException("No edges should intersect");
								}
							} catch (OverlappingException ex) {
								throw new IllegalStateException("Segments overlapping");
							}
						}
					}
					
					if ((e.start == f.start && e.end == f.end) || (e.start == f.end && e.end == f.start)) {
						/*
						 * e and f share endpoints
						 */
						Set<Point> shared = new HashSet<Point>();
						for (int i = 0; i < e.size(); i++) {
							Point eP = e.get(i);
							for (int j = 0; j < f.size(); j++) {
								Point fP = f.get(j);
								if (eP.equals(fP)) {
									shared.add(eP);
								}
							}
						}
						if (e.start == e.end) {
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
