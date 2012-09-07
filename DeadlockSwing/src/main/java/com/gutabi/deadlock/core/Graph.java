package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.PointToBeHandled.Event;

@SuppressWarnings("serial")
public class Graph {
	
	private final ArrayList<Edge> edges = new ArrayList<Edge>();
	private final ArrayList<Intersection> intersections = new ArrayList<Intersection>();
	private final QuadTree segTree = new QuadTree();
	
	private final ArrayList<Source> sources = new ArrayList<Source>();
	private final ArrayList<Sink> sinks = new ArrayList<Sink>();
	
	private final ArrayList<Hub> hubs = new ArrayList<Hub>();
	
	private static final Logger logger = Logger.getLogger(Graph.class);
	
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
	
	public List<Hub> getHubs() {
		return hubs;
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
			
			List<Edge> cons = v.getEdges();
			if (cons.size() == 0) {
				destroyVertex(v);
			} else if (cons.size() == 2) {
				merge(cons.get(0), cons.get(1), v);
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
	
	public void addHub(Point p) {
		hubs.add(new Hub(p));
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
		
		List<Vertex> vertices = new ArrayList<Vertex>(){{addAll(intersections);addAll(sources);addAll(sinks);}};
		
		int vertexCount = vertices.size();
		Vertex[] vertexIDs = new Vertex[vertexCount];
		
		int id = 0;
		for (Vertex v : vertices) {
			v.graphID = id;
			vertexIDs[id] = v;
			id++;
		}
		
		distances = new double[vertexCount][vertexCount];
		next = new Vertex[vertexCount][vertexCount];
		
		/*
		 * initialize
		 */
		for (int i = 0; i < distances.length; i++) {
			Arrays.fill(distances[i], Double.POSITIVE_INFINITY);
		}
		for (int i = 0; i < distances.length; i++) {
			distances[i][i] = 0.0;
		}
		for(Hub h : hubs) {
			List<Vertex> hvs = h.getVertices();
			for (int i = 0; i < hvs.size()-1; i++) {
				Vertex vi = hvs.get(i);
				for (int j = i+1; j < hvs.size(); j++) {
					Vertex vj = hvs.get(j);
					double dist = Point.distance(vi.getPoint(), vj.getPoint());
					distances[vi.graphID][vj.graphID] = dist;
					distances[vj.graphID][vi.graphID] = dist;
				}
			}
		}
		
		/*
		 * iterate and find shorter distances via edges
		 */
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
						next[i][j] = vertexIDs[k];
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
		VertexPosition last = new VertexPosition(start);
		poss.add(last);
		for (int i = 1; i < vs.size(); i++) {
			Vertex a = vs.get(i-1);
			Vertex b = vs.get(i);
			
			double d = distances[a.graphID][b.graphID];
			
			Connector cc = null;
			for (Connector c : Vertex.commonConnectors(a, b)) {
				
				if (c instanceof Edge) {
					Edge e = (Edge)c;
					if (DMath.doubleEquals(e.getTotalLength(), d)) {
						cc = e;
						break;
					}
				} else {
					Hub h = (Hub)c;
					if (DMath.doubleEquals(Point.distance(a.getPoint(), b.getPoint()), d)) {
						cc = h;
						break;
					}
				}
				
			}
			assert cc != null;
			
			//poss.add(new VertexPosition(a));
			last = new VertexPosition(b);
			poss.add(last);
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
	
	public double distanceTo(Vertex a, Vertex b) {
		return distances[a.graphID][b.graphID];
	}
	
	
	
	
	
	public Position hitTest(Point p) {
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
		for (Segment in : segTree.findAllSegments(p)) {
			Edge e = in.edge;
			int i = in.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			if (Point.intersect(p, c, d) && !p.equals(d)) {
				return new EdgePosition(e, i, Point.param(p, c, d), null);
			}
		}
		for (Hub h : hubs) {
			double dist = Point.distance(p,  h.getPoint());
			if (DMath.doubleEquals(dist, Hub.RADIUS) || dist < Hub.RADIUS) {
				return new HubPosition(h, p);
			}
		}
		return null;
	}
	
	/**
	 * returns the closest vertex within radius that is not the excluded point
	 * 
	 */
	public VertexPosition findClosestVertexPosition(Point a, Point anchor, double radius) {
		Vertex anchorV = null;
		if (anchor != null) {
			Position pos = hitTest(anchor);
			if (pos instanceof VertexPosition) {
				anchorV = ((VertexPosition) pos).getVertex();
				if (a.equals(anchor)) {
					/*
					 * a equals anchor, so starting this segment
					 * if exactly on a vertex, then use that one
					 */
					return new VertexPosition(anchorV);
				}
			}
		}
		
		Vertex closest = null;
		
		for (Vertex i : new ArrayList<Vertex>(){{addAll(intersections);addAll(sources);addAll(sinks);}}) {
			Point ip = i.getPoint();
			double dist = Point.distance(a, ip);
			if (DMath.doubleEquals(dist, radius) || dist < radius) {
				if (i == anchorV) {
					/*
					 * since a is not equal to anchor, we want to be able to add segments to the existing edge.
					 * so we have to ignore the anchorV when it comes up
					 */
					continue;
				}
				if (anchorV == null || dist < Point.distance(ip, anchor)) {
					if (closest == null) {
						closest = i;
					} else if (Point.distance(a, ip) < Point.distance(a, closest.getPoint())) {
						closest = i;
					}
				}
			}	
		}
		
		if (closest != null) {
			return new VertexPosition(closest);
		} else {
			return null;
		}
	}
	
//	public HubPosition findClosestHubPosition(Point a, Point anchor, double radius) {
//		
//		HubPosition best = null;
//		
//		for (Hub h : hubs) {
//			if (Point.distance(a, h.getPoint()) < Hub.RADIUS && (anchor == null || Point.distance(anchor, h.getPoint()) < Hub.RADIUS)) {
//				/*
//				 * inside hub, so ignore until it crosses the threshold
//				 */
//				best = null;
//				break;
//			}
//			HubPosition closest = closestHubPosition(a, h);
//			Point ep = closest.getPoint();
//			double dist = Point.distance(a, ep);
//			if (dist < radius && (anchor == null || Point.equals(a, anchor) || dist < Point.distance(anchor, ep))) {
//				if (best == null) {
//					best = closest;
//				} else if (Point.distance(a, ep) < Point.distance(a, best.getPoint())) {
//					best = closest;
//				}
//			}
//		}
//		
//		return best;
//		
//	}
	
	/**
	 * returns the closest edge within radius that is not in the excluded radius
	 */
	public EdgePosition findClosestEdgePosition(Point a, Point exclude, double radius) {
		return getSegmentTree().findClosestEdgePosition(a, exclude, radius);
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
	
	public void processNewStroke(List<Point> stroke) {
		
		for (int i = 0; i < stroke.size()-1; i++) {
			Point a = stroke.get(i);
			Point b = stroke.get(i+1);
			processNewSegment(a, b);
		}
		cleanupEdges();
	}
	
	private void processNewSegment(Point a, Point b) {
		
		List<Point> seg = new ArrayList<Point>();
		seg.add(a);
		seg.add(b);
		
		List<PointToBeHandled> splitSegs = splitOnEvents(a, b);
		
		String.class.getName();
		
		if (
				splitSegs.toString().equals("[., .]") ||
				
				splitSegs.toString().equals("[(3) . +, +> >> >>, .]") ||
				
				splitSegs.toString().equals("[(4) . +, .]") ||
				splitSegs.toString().equals("[(4) . +, >>, +> >> >>, .]") ||
				splitSegs.toString().equals("[(4) . +, +> >> >> >> .]") ||
				
				splitSegs.toString().equals("[(5) . +, .]") ||
				splitSegs.toString().equals("[(5) . +, >>, .]") ||
				splitSegs.toString().equals("[(5) . +, >> .]") ||
				splitSegs.toString().equals("[(5) . +, >> V .]") ||
				splitSegs.toString().equals("[(5) . +, >>, >>, +> >> >>, .]") ||
				splitSegs.toString().equals("[(5) . +, >> >>, +> >> >>, .]") ||
				
				splitSegs.toString().equals("[(6) . +, .]") ||
				splitSegs.toString().equals("[(6) . +, >>, >>, .]") ||
				splitSegs.toString().equals("[(6) . +, >>, >> V .]") ||
				splitSegs.toString().equals("[(6) . +, >> >> >>, +> >> >>, .]") ||
				splitSegs.toString().equals("[(6) . +, >> >>, .]") ||
				splitSegs.toString().equals("[(6) . +, >>, >> >>, +> >> >>, .]") ||
				splitSegs.toString().equals("[(6) . +, >> >>, +> >> >> >> .]") ||
				
				splitSegs.toString().equals("[(7) . +, >>, .]") ||
				splitSegs.toString().equals("[(7) . +, >> >> >>, .]") ||
				splitSegs.toString().equals("[(7) . +, >>, >> >> >>, +> >> >>, .]") ||
				splitSegs.toString().equals("[(7) . +, >> >> >>, +> >> >> >> .]") ||
				splitSegs.toString().equals("[(7) . +, .]") ||
				splitSegs.toString().equals("[(7) . +, >>, >> >>, .]") ||
				splitSegs.toString().equals("[(7) . +, >> >> >> .]") ||
				splitSegs.toString().equals("[(7) . +, >> >> >> .]") ||
				
				splitSegs.toString().equals("[(8) . +, .]") ||
				splitSegs.toString().equals("[(8) . +, >>, >>, .]") ||
				splitSegs.toString().equals("[(8) . +, >> >> >> >> .]") ||
				splitSegs.toString().equals("[(8) . +, >>, >> >> >>, .]") ||
				splitSegs.toString().equals("[(8) . +, >>, .]") ||
				splitSegs.toString().equals("[(8) . +, >> >>, >> >>, .]") ||
				
				splitSegs.toString().equals("[(9) . +, .]") ||
				splitSegs.toString().equals("[(9) . +, >>, .]") ||
				splitSegs.toString().equals("[(9) . +, >>, >>, >>, .]") ||
				splitSegs.toString().equals("[(9) . +, >> >> >>, >> >> >>, +> >> >>, .]") ||
				splitSegs.toString().equals("[(9) . +, >>, >> >> >> >> .]") ||
				splitSegs.toString().equals("[(9) . +, >> >>, .]") ||
				splitSegs.toString().equals("[(9) . +, >> >>, >> >> >> .]") ||
				splitSegs.toString().equals("[(9) . +, >> >>, >> >> >> .]") ||
				
				splitSegs.toString().equals("[(10) . +, .]") ||
				splitSegs.toString().equals("[(10) . +, >> >> >>, >> >> >>, .]") ||
				splitSegs.toString().equals("[(10) . +, >> >> >> .]") ||
				splitSegs.toString().equals("[(10) . +, >>, >> >>, .]") ||
				
				splitSegs.toString().equals("[(11) . +, >> >> >>, .]") ||
				splitSegs.toString().equals("[(11) . +, >> >> >>, >> >> >> >> .]") ||
				splitSegs.toString().equals("[(11) . +, >>, >> >> >>, >> >> >>, .]") ||
				splitSegs.toString().equals("[(11) . +, >> .]") ||
				splitSegs.toString().equals("[(11) . +, >> >>, >> >>, .]") ||
				splitSegs.toString().equals("[(11) . +, .]") ||
				splitSegs.toString().equals("[(11) . +, >>, .]") ||
				
				splitSegs.toString().equals("[(12) . +, .]") ||
				splitSegs.toString().equals("[(12) . +, >>, >> >> >>, .]") ||
				splitSegs.toString().equals("[(12) . +, >> >> >> >> .]") ||
				splitSegs.toString().equals("[(12) . +, >>, >> >> >>, >> >> >> >> .]") ||
				splitSegs.toString().equals("[(12) . +, >> >> >>, >> >> >>, >> >> >>, +> >> >>, .]") ||
				splitSegs.toString().equals("[(12) . +, >> >>, .]") ||
				
				splitSegs.toString().equals("[(13) . +, .]") ||
				splitSegs.toString().equals("[(13) . +, >>, .]") ||
				splitSegs.toString().equals("[(13) . +, >>, >> >> >> >> .]") ||
				splitSegs.toString().equals("[(13) . +, >> >> >>, >> >> >>, >> >> >>, .]") ||
				splitSegs.toString().equals("[(13) . +, >> >>, >> >>, >> >>, .]") ||
				splitSegs.toString().equals("[(13) . +, >> >> >> .]") ||
				
				splitSegs.toString().equals("[(14) . +, >> >> >>, >> >> >>, .]") ||
				splitSegs.toString().equals("[(14) . +, >> >>, >> >>, .]") ||
				splitSegs.toString().equals("[(14) . +, .]") ||
				splitSegs.toString().equals("[(14) . +, >> >>, >> >>, >> >> >> .]") ||
				
				splitSegs.toString().equals("[(15) . +, >> >> >>, .]") ||
				splitSegs.toString().equals("[(15) . +, >> >> >>, >> >> >> >> .]") ||
				splitSegs.toString().equals("[(15) . +, >>, >> >> >>, >> >> >>, .]") ||
				splitSegs.toString().equals("[(15) . +, >> >>, .]") ||
				splitSegs.toString().equals("[(15) . +, >> >>, .]") ||
				
				splitSegs.toString().equals("[(16) . +, .]") ||
				splitSegs.toString().equals("[(16) . +, >> >> >> >> .]") ||
				splitSegs.toString().equals("[(16) . +, >>, >> >> >>, .]") ||
				splitSegs.toString().equals("[(16) . +, >> >> >>, >> >> >>, >> >> >>, >> >> >>, .]") ||
				splitSegs.toString().equals("[(16) . +, >> >> >> .]") ||
				splitSegs.toString().equals("[(16) . +, >> >>, >> >>, >> >>, .]") ||
				splitSegs.toString().equals("[(16) . +, >> >> >> .]") ||
				
				splitSegs.toString().equals("[(17) . +, .]") ||
				splitSegs.toString().equals("[(17) . +, >>, .]") ||
				splitSegs.toString().equals("[(17) . +, >> >> >>, >> >> >>, >> >> >>, .]") ||
				splitSegs.toString().equals("[(17) . +, >> >>, >> >>, .]") ||
				splitSegs.toString().equals("[(17) . +, >> >>, >> >>, >> >> >> .]") ||
				
				splitSegs.toString().equals("[(18) . +, >> >> >>, >> >> >>, >> >> >> >> .]") ||
				splitSegs.toString().equals("[(18) . +, >> >> >>, >> >> >>, .]") ||
				splitSegs.toString().equals("[(18) . +, >>, >> >> >>, >> >> >>, >> >> >>, .]") |
				splitSegs.toString().equals("[(18) . +, >> >>, .]") ||
				splitSegs.toString().equals("[(18) . +, >> >>, >> >> >> .]") ||
				
				splitSegs.toString().equals("[(19) . +, >> >> >>, .]") ||
				splitSegs.toString().equals("[(19) . +, >> >> >>, >> >> >> >> .]") ||
				splitSegs.toString().equals("[(19) . +, >>, >> >> >>, >> >> >>, .]") ||
				splitSegs.toString().equals("[(19) . +, >> >>, >> >>, >> >>, >> >> >> .]") ||
				splitSegs.toString().equals("[(19) . +, .]") ||
				splitSegs.toString().equals("[(19) . +, >> >> >> .]") ||
				
				splitSegs.toString().equals("[(20) . +, .]") ||
				splitSegs.toString().equals("[(20) . +, >>, >> >> >>, .]") ||
				splitSegs.toString().equals("[(20) . +, >> >>, >> >>, .]") ||
				splitSegs.toString().equals("[(20) . +, >>, .]") ||
				splitSegs.toString().equals("[(20) . +, >>, .]") ||
				
				splitSegs.toString().equals("[(21) . +, >>, >> >> >> >> .]") ||
				splitSegs.toString().equals("[(21) . +, .]") ||
				splitSegs.toString().equals("[(21) . +, >> >>, .]") ||
				
				splitSegs.toString().equals("[(22) . +, .]") ||
				
				splitSegs.toString().equals("[(23) . +, >> >> >>, .]") ||
				splitSegs.toString().equals("[(23) . +, >> >>, >> >>, .]") ||
				splitSegs.toString().equals("[(23) . +, >> .]") ||
				splitSegs.toString().equals("[(23) . +, .]") ||
				
				splitSegs.toString().equals("[(24) . +, .]") ||
				splitSegs.toString().equals("[(24) . +, >> >>, .]") ||
				
				splitSegs.toString().equals("[(25) . +, .]") ||
				splitSegs.toString().equals("[(25) . +, >> .]") ||
				
				
				splitSegs.toString().equals("[(26) . +, .]") ||
				splitSegs.toString().equals("[(26) . +, >> .]") ||
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				

				
				
				
				false
			) {
			
			addSegment(a, b);
			
		}
		
//		else if (splitSegs.toString().equals("[., <<, .]")) {
//			
//			PointToBeHandled p1 = splitSegs.get(1);
//			
//			addSegment(a, p1.getPoint());
//			addSegment(p1.getPoint(), p1.getEventSource());
//			
//		} else if (splitSegs.toString().equals("[., <+, +, +>, .]")) {
//			
//			PointToBeHandled p1 = splitSegs.get(2);
//			
//			addSegment(a, p1.getPoint());
//			addSegment(p1.getPoint(), b);
//			
//		}
		
		else {
//			throw new AssertionError();
			logger.debug("splitSegs.toString().equals(\"" + splitSegs + "\") ||");
			addSegment(a, b);
		}
			
//		}
		
	}
	
	private List<PointToBeHandled> splitOnEvents(Point a, Point b) {
			
		//List<Point> newStroke = new ArrayList<Point>();
		
		List<PointToBeHandled> betweenABPoints = new ArrayList<PointToBeHandled>();
		
		betweenABPointsAdd(betweenABPoints, new PointToBeHandled(a, 0.0, Event.A, null));
		betweenABPointsAdd(betweenABPoints, new PointToBeHandled(b, 1.0, Event.B, null));
		
//		boolean intersectionAtA = false;
//		boolean intersectionAtB = false;
		
//		for (Segment in : segTree.findAllSegments(a, b)) {
//			//Edge e = in.edge;
//			int index = in.index;
//			Point c = in.edge.getPoint(index);
//			Point d = in.edge.getPoint(index+1);
//			
//			if (Point.intersect(a, c, d)) {
//				intersectionAtA = true;
//			}
//			
//			if (Point.intersect(b, c, d)) {
//				intersectionAtB = true;
//			}
//		}
		
		List<Point> alreadyChecked = new ArrayList<Point>();
		
		for (Segment in : segTree.findAllSegments(a, b)) {
			//Edge e = in.edge;
			int index = in.index;
			Point c = in.edge.getPoint(index);
			Point d = in.edge.getPoint(index+1);
			
			try {
				Point inter = Point.intersection(a, b, c, d);
				if (inter != null) {
					
					double interParam = Point.param(inter, a, b);
					
					betweenABPointsAdd(betweenABPoints, new PointToBeHandled(inter, interParam, Event.INTERSECTION, null));
					
					double interStartParam = Point.travelBackward(a, b, interParam, 10.0);
					double interEndParam = Point.travelForward(a, b, interParam, 10.0);
					
					betweenABPointsAdd(betweenABPoints, new PointToBeHandled(Point.point(a, b, interStartParam), interStartParam, Event.INTERSECTION_START, null));
					betweenABPointsAdd(betweenABPoints, new PointToBeHandled(Point.point(a, b, interEndParam), interEndParam, Event.INTERSECTION_END, null));
					
				}
			} catch (OverlappingException ex) {
				
				double cParam = Point.param(c, a, b);
				double dParam = Point.param(d, a, b);
				
				if (cParam < dParam) {
					betweenABPointsAdd(betweenABPoints, new PointToBeHandled(c, cParam, Event.OVERLAP_START, null));
					betweenABPointsAdd(betweenABPoints, new PointToBeHandled(d, dParam, Event.OVERLAP_END, null));
				} else {
					betweenABPointsAdd(betweenABPoints, new PointToBeHandled(d, dParam, Event.OVERLAP_START, null));
					betweenABPointsAdd(betweenABPoints, new PointToBeHandled(c, cParam, Event.OVERLAP_END, null));
				}
				
			}
			
			if (!alreadyChecked.contains(c)) {
				//how close is c to <a, b>?
	//			if (!(intersectionAtA && (DMath.doubleEquals(Point.distance(a, c), 10.0) || Point.distance(a, c) < 10.0)) &&
	//					!(intersectionAtB && (DMath.doubleEquals(Point.distance(b, c), 10.0) || Point.distance(b, c) < 10.0))) {
				double u = Point.u(a, c, b);
				if (u < 0.0) {
					u = 0.0;
				} else if (u > 1.0) {
					u = 1.0;
				}
	//			if (DMath.doubleEquals(u, 0.0) || u > 0.0) {
				Point cProjected = Point.point(a, b, u);
				double dist = Point.distance(cProjected, c);
	//			if () {
	//				betweenABPointsAdd(betweenABPoints, new PointToBeHandled(cProjected, u, Event.CLOSE_POINT, c));
	//			} else
				if (DMath.doubleEquals(dist, 10.0) || dist < 10.0) {
	//				double distToTravel = Math.sqrt(Math.pow(10.0, 2) - Math.pow(dist, 2));
	//				double start = Point.travelBackward(a, b, u, distToTravel); 
	//				double end = Point.travelForward(a, b, u, distToTravel);
	//				betweenABPointsAdd(betweenABPoints, new PointToBeHandled(Point.point(a, b, start), start, Event.CLOSE_START, c));
	//				betweenABPointsAdd(betweenABPoints, new PointToBeHandled(Point.point(a, b, end), end, Event.CLOSE_END, c));
					Point[] ints = new Point[2];
					int num = Point.circleSegmentIntersections(c, 10.0, a, b, ints);
					switch (num) {
					case 1:
						throw new AssertionError();
					case 2: {
						Point p1 = ints[0];
						Point p2 = ints[1];
						double param1 = Point.param(p1, a, b);
						double param2 = Point.param(p2, a, b);
						if (param1 < param2) {
							betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p1, param1, Event.CLOSE_START, c));
							betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p2, param2, Event.CLOSE_END, c));
						} else {
							betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p2, param2, Event.CLOSE_START, c));
							betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p1, param1, Event.CLOSE_END, c));
						}
						break;
					}
					}
				}
//			}
//			}
				alreadyChecked.add(c);
			}
			
			
			if (!alreadyChecked.contains(d)) {
				//how close is d to <a, b>?
	//			if (!(intersectionAtA && (DMath.doubleEquals(Point.distance(a, d), 10.0) || Point.distance(a, d) < 10.0)) &&
	//					!(intersectionAtB && (DMath.doubleEquals(Point.distance(b, d), 10.0) || Point.distance(b, d) < 10.0))) {
				double u = Point.u(a, d, b);
				if (u < 0.0) {
					u = 0.0;
				} else if (u > 1.0) {
					u = 1.0;
				}
	//			if (DMath.doubleEquals(u, 0.0) || u > 0.0) {
				Point dProjected = Point.point(a, b, u);
				double dist = Point.distance(dProjected, d);
	//			if (DMath.doubleEquals(dist, 10.0)) {
	//				betweenABPointsAdd(betweenABPoints, new PointToBeHandled(dProjected, u, Event.CLOSE_POINT, d));
	//			} else
				if (DMath.doubleEquals(dist, 10.0) || dist < 10.0) {
					Point[] ints = new Point[2];
					int num = Point.circleSegmentIntersections(d, 10.0, a, b, ints);
					switch (num) {
					case 1:
						throw new AssertionError();
					case 2: {
						Point p1 = ints[0];
						Point p2 = ints[1];
						double param1 = Point.param(p1, a, b);
						double param2 = Point.param(p2, a, b);
						if (param1 < param2) {
							betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p1, param1, Event.CLOSE_START, d));
							betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p2, param2, Event.CLOSE_END, d));
						} else {
							betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p2, param2, Event.CLOSE_START, d));
							betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p1, param1, Event.CLOSE_END, d));
						}
						break;
					}
					}
				}
	//			}
	//			}
				alreadyChecked.add(d);
			}
			
			
			double u = Point.u(c, a, d);
			if (u < 0.0) {
				u = 0.0;
			} else if (u > 1.0) {
				u = 1.0;
			}
			Point aProjected = Point.point(c, d, u);
			u = Point.u(c, b, d);
			if (u < 0.0) {
				u = 0.0;
			} else if (u > 1.0) {
				u = 1.0;
			}
			Point bProjected = Point.point(c, d, u);
			
			{
			//how close is a to <c, d>?
//			if (!(intersectionAtA && (DMath.doubleEquals(Point.distance(a, aProjected), 10.0) || Point.distance(a, aProjected) < 10.0)) &&
//					!(intersectionAtB && (DMath.doubleEquals(Point.distance(b, bProjected), 10.0) || Point.distance(b, bProjected) < 10.0))) {
			double dist = Point.distance(aProjected, a);
//			if (DMath.doubleEquals(dist, 10.0)) {
//				betweenABPointsAdd(betweenABPoints, new PointToBeHandled(a, 0.0, Event.CLOSE_POINT, aProjected));
//			} else
			if (DMath.doubleEquals(dist, 10.0) || dist < 10.0) {
				Point[] ints = new Point[2];
				int num = Point.circleSegmentIntersections(aProjected, 10.0, a, b, ints);
				switch (num) {
				case 1:
					throw new AssertionError();
				case 2: {
					Point p1 = ints[0];
					Point p2 = ints[1];
					double param1 = Point.param(p1, a, b);
					double param2 = Point.param(p2, a, b);
					if (param1 < param2) {
						betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p1, param1, Event.CLOSE_START, aProjected));
						betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p2, param2, Event.CLOSE_END, aProjected));
					} else {
						betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p2, param2, Event.CLOSE_START, aProjected));
						betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p1, param1, Event.CLOSE_END, aProjected));
					}
					break;
				}
				}
			}
//			}
			}
			
			
			if (!bProjected.equals(aProjected)) {
//			if (!(intersectionAtA && (DMath.doubleEquals(Point.distance(a, aProjected), 10.0) || Point.distance(a, aProjected) < 10.0)) &&
//					!(intersectionAtB && (DMath.doubleEquals(Point.distance(b, bProjected), 10.0) || Point.distance(b, bProjected) < 10.0))) {
			//how close is b to <c, d>?
			double dist = Point.distance(bProjected, b);
//			if (DMath.doubleEquals(dist, 10.0)) {
//				betweenABPointsAdd(betweenABPoints, new PointToBeHandled(b, 1.0, Event.CLOSE_POINT, bProjected));
//			} else
			if (DMath.doubleEquals(dist, 10.0) || dist < 10.0) {
				Point[] ints = new Point[2];
				int num = Point.circleSegmentIntersections(bProjected, 10.0, a, b, ints);
				switch (num) {
				case 1:
					throw new AssertionError();
				case 2: {
					Point p1 = ints[0];
					Point p2 = ints[1];
					double param1 = Point.param(p1, a, b);
					double param2 = Point.param(p2, a, b);
					if (param1 < param2) {
						betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p1, param1, Event.CLOSE_START, bProjected));
						betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p2, param2, Event.CLOSE_END, bProjected));
					} else {
						betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p2, param2, Event.CLOSE_START, bProjected));
						betweenABPointsAdd(betweenABPoints, new PointToBeHandled(p1, param1, Event.CLOSE_END, bProjected));
					}
				}
				}
			}
//			}
			}
			
		}
		
		return betweenABPoints;
	}
	
	private static void betweenABPointsAdd(List<PointToBeHandled> ls, PointToBeHandled ptbh) {
		if (!DMath.doubleEquals(ptbh.getParam(), 0.0) && ptbh.getParam() < 0.0) {
			
			switch (ptbh.getEvent()) {
			case INTERSECTION_START: {
				PointToBeHandled a = ls.get(0);
				ls.set(0, new PointToBeHandled(a.getPoint(), a.getParam(), Event.increment(a.getEvent()), null));
				break;
			}
			case CLOSE_START: {
				PointToBeHandled a = ls.get(0);
				ls.set(0, new PointToBeHandled(a.getPoint(), a.getParam(), Event.increment(a.getEvent()), null));
				break;
			}
			default:
				throw new AssertionError();
			}
			
		}
		else if (!DMath.doubleEquals(ptbh.getParam(), 1.0) && ptbh.getParam() > 1.0) {
			
			return;
			
//			switch (ptbh.getEvent()) {
//			case INTERSECTION_END: {
//				int n = ls.size()-1;
//				PointToBeHandled b = ls.get(n);
//				ls.set(n, new PointToBeHandled(b.getPoint(), b.getParam(), Event.decrement(b.getEvent()), null));
//				break;
//			}
//			case CLOSE_END: {
//				int n = ls.size()-1;
//				PointToBeHandled b = ls.get(n);
//				ls.set(n, new PointToBeHandled(b.getPoint(), b.getParam(), Event.decrement(b.getEvent()), null));
//				break;
//			}
//			default:
//				throw new AssertionError();
//			}
			
		}
		else {
			
			int i = Collections.binarySearch(ls, ptbh, PointToBeHandled.COMPARATOR);
			if (i < 0) {
				/*
				 * not found
				 */
				int insertionPoint = -(i+1);
				ls.add(insertionPoint, ptbh);
			} else {
				/*
				 * found
				 */
				PointToBeHandled old = ls.get(i);
				assert old.getPoint().equals(ptbh.getPoint());
				ls.set(i, new PointToBeHandled(old.getPoint(), old.getParam(), Event.merge(old.getEvent(), ptbh.getEvent()), null));
			}
			
		}
	}
	
	private List<Point> handleEvents(PointToBeHandled a, PointToBeHandled b) {
		
		Point ap = a.getPoint();
		Point bp = b.getPoint();
		
		List<Point> handled = new ArrayList<Point>();
		
		Position closestA = closestPosition(ap, ap, 10);
		Position closestB = closestPosition(bp, ap, 10);
		
		if (closestA != null) {
			if (closestB != null) {
				if (Point.distance(closestA.getPoint(), closestB.getPoint()) > 10) {
					handled.add(closestA.getPoint());
					handled.add(closestB.getPoint());
				} else if (closestA instanceof VertexPosition && closestB instanceof VertexPosition &&
						!closestA.getPoint().equals(closestB.getPoint())) {
					/*
					 * even if they are close together, connect vertices
					 */
					handled.add(closestA.getPoint());
					handled.add(closestB.getPoint());
				} else {
					/*
					 * both too close, do nothing
					 */
					;
				}
			} else {
				handled.add(closestA.getPoint());
				handled.add(bp);
			}
		} else {
			if (closestB != null) {
				handled.add(ap);
				handled.add(closestB.getPoint());
			} else {
				handled.add(ap);
				handled.add(bp);
			}
		}
		
		return handled;	
	}
	
	public Position closestPosition(Point a) {
		return closestPosition(a, null, Double.POSITIVE_INFINITY);
	}
	
	public Position closestPosition(Point a, double radius) {
		return closestPosition(a, null, radius);
	}
	
	public Position closestPosition(Point a, Point anchor, double radius) {
		VertexPosition closestV = findClosestVertexPosition(a, anchor, radius);
		if (closestV != null) {
			return closestV;
		}
//		HubPosition closestH = findClosestHubPosition(a, anchor, radius);
//		if (closestH != null) {
//			return closestH;
//		}
		EdgePosition closestEdge = findClosestEdgePosition(a, anchor, radius);
		if (closestEdge != null) {
			return closestEdge;
		}
		return null;
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
		
		for (Hub h : hubs) {
			if (Point.distance(a, h.getPoint()) < 40) {
				return true;
			}
			if (Point.distance(b, h.getPoint()) < 40) {
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
			
		assert !a.equals(b);
		assert !segmentExists(a, b) : "segment " + a + " " + b + " already exists";
		assert !segmentOverlaps(a, b);
		
		Position pos = hitTest(a);
		final Vertex aV;
		if (pos != null) {
			if (pos instanceof VertexPosition) {
				aV = ((VertexPosition)pos).getVertex();
			} else if (pos instanceof EdgePosition) {
				aV = split((EdgePosition)pos);
			} else {
				assert pos instanceof HubPosition;
				aV = addVertexToHub((HubPosition)pos);
			}
		} else {
			aV = createIntersection(a);
		}
		
		pos = hitTest(b);
		final Vertex bV;
		if (pos != null) {
			if (pos instanceof VertexPosition) {
				bV = ((VertexPosition)pos).getVertex();
			} else if (pos instanceof EdgePosition) {
				bV = split((EdgePosition)pos);
			} else {
				assert pos instanceof HubPosition;
				bV = addVertexToHub((HubPosition)pos);
			}
		} else {
			bV = createIntersection(b);
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
	private Vertex split(final EdgePosition pos) {
		
		Edge e = pos.getEdge();
		int index = pos.getIndex();
		double param = pos.getParam();
		final Point p = pos.getPoint();
		
		assert param >= 0.0;
		assert param < 1.0;
		
		/*
		 * we assert that param < 1.0, but after adjusting, we may be at d
		 */
		
		final Point c = e.getPoint(index);
		final Point d = e.getPoint(index+1);
		
		Vertex v = createIntersection(p);
		
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
				
				//assert tryFindVertex(c) == null;
				
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
				
				//assert tryFindVertex(d) == null;
				
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
	
	private Vertex addVertexToHub(HubPosition pos) {
		Hub h = pos.getHub();
		Point p = pos.getPoint();
		assert DMath.doubleEquals(Point.distance(h.getPoint(), p), Hub.RADIUS);
		
		Vertex v = createIntersection(p);
		
		h.addVertex(v);
		
		v.addHub(h);
		
		return v;
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
			assert e1.getPoint(0).equals(e2.getPoint(0));
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
			assert e1.getPoint(0).equals(e2.getPoint(e2.size()-1));
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
			assert e1.getPoint(e1.size()-1).equals(e2.getPoint(0));
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
			assert e1.getPoint(e1.size()-1).equals(e2.getPoint(e2.size()-1));
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
				if (v.getPoint().equals(w.getPoint())) {
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
		
		return true;
	}
	
}
