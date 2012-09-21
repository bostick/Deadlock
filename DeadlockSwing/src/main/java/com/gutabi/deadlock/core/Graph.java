package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Event.CloseEvent;
import com.gutabi.deadlock.core.Event.IntersectionEvent;

@SuppressWarnings("serial")
public class Graph {
	
	private final ArrayList<Edge> edges = new ArrayList<Edge>();
	private final ArrayList<Intersection> intersections = new ArrayList<Intersection>();
	private final QuadTree segTree = new QuadTree();
	
	private final ArrayList<Source> sources = new ArrayList<Source>();
	private final ArrayList<Sink> sinks = new ArrayList<Sink>();
	
//	private final ArrayList<Hub> hubs = new ArrayList<Hub>();
	
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
	
//	public List<Hub> getHubs() {
//		return hubs;
//	}
	
	public void clear() {
		edges.clear();
		intersections.clear();
		segTree.clear();
		sources.clear();
		sinks.clear();
	}
	
	private Edge createEdge(Vertex start, Vertex end, List<Point> pts) {
		Edge e = new Edge(start, end, pts);
		edges.add(e);
		segTree.addEdge(e);
		
		//logger.debug("createEdge: " + start + " " + end + " n=" + pts.size() );
		
		if (!e.isStandAlone()) {
			start.addEdge(e);
			end.addEdge(e);
		}
		
		return e;
	}
	
	private void edgesChanged(Vertex v) {
		
		List<Edge> cons = v.getEdges();
		
		for (Edge e : cons) {
			assert edges.contains(e);
		}
		
		if (cons.size() == 0) {
			destroyVertex(v);
		} else if (cons.size() == 2) {
			//merge(cons.get(0), cons.get(1), v);
			merge(v);
		}
		
		if (v instanceof Intersection) {
			
			
			
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
	
	private Vertex createVertex(Point p) {
		
		if (p.getY() <= 10 || p.getX() <= 10) {
			// source
			
			Source s = new Source(p);
			assert !sources.contains(s);
			sources.add(s);
			return s;
			
		} else if (p.getX() >= MODEL.WORLD_WIDTH-10 || p.getY() >= MODEL.WORLD_HEIGHT-10) {
			// sink
			
			Sink s = new Sink(p);
			assert !sinks.contains(s);
			sinks.add(s);
			return s;
			
		} else {
			
			Intersection i = new Intersection(p);
			assert !intersections.contains(i);
			intersections.add(i);
			return i;
			
		}
		
	}
	
//	public void addHub(Point p) {
//		hubs.add(new Hub(p));
//	}
	
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
		
		//logger.debug("destroy Edge: " + e);
		
		if (!e.isStandAlone()) {
			e.getStart().removeEdge(e);
			e.getEnd().removeEdge(e);
		}
		
		edges.remove(e);
		segTree.removeEdge(e);
		e.remove();
	}
	
	
	double[][] distances;
	Vertex[][] nextHighest;
	boolean[][] neighbors;
	
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
		id = 0;
		for (Edge e : edges) {
			e.graphID = id;
			id++;
		}
		
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
//		for(Hub h : hubs) {
//			List<Vertex> hvs = h.getVertices();
//			for (int i = 0; i < hvs.size()-1; i++) {
//				Vertex vi = hvs.get(i);
//				for (int j = i+1; j < hvs.size(); j++) {
//					Vertex vj = hvs.get(j);
//					double dist = Point.distance(vi.getPoint(), vj.getPoint());
//					distances[vi.graphID][vj.graphID] = dist;
//					distances[vj.graphID][vi.graphID] = dist;
//				}
//			}
//		}
		for (int i = 0; i < neighbors.length; i++) {
			Arrays.fill(neighbors[i], false);
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
			
			/*
			 * initialize neighbors
			 */
			neighbors[e.graphID][e.graphID] = true;
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
		
		for (Vertex v : vertices) {
			List<Edge> eds = v.getEdges();
			for (int i = 0; i < eds.size()-1; i++) {
				for (int j = i+1; j < eds.size(); j++) {
					Edge ei = eds.get(i);
					Edge ej = eds.get(j);
					neighbors[ei.graphID][ej.graphID] = true;
					neighbors[ej.graphID][ei.graphID] = true;
				}
			}
		}
		
	}
	
	public List<Vertex> shortestPath(final Vertex start, Vertex end) {
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
		
		return vs;
	}
	
	/**
	 * returns the next choice to make for the shortest path from start to end
	 */
	public Vertex shortestPathChoice(final Vertex start, Vertex end) {
		
		int sid = start.graphID;
		int eid = end.graphID;
		
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
		
		int sid = start.graphID;
		int eid = end.graphID;
		
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
		return distances[a.graphID][b.graphID];
	}
	
	public boolean areNeighbors(Edge a, Edge b) {
		return neighbors[a.graphID][b.graphID];
	}
	
	
	
	
	
	public Position hitTest(Point p) {
		for (Intersection v : intersections) {
			if (p.equals(v.getPoint())) {
				return v;
			}
		}
		for (Source s : sources) {
			if (p.equals(s.getPoint())) {
				return s;
			}
		}
		for (Sink s : sinks) {
			if (p.equals(s.getPoint())) {
				return s;
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
//		for (Hub h : hubs) {
//			double dist = Point.distance(p,  h.getPoint());
//			if (DMath.doubleEquals(dist, Hub.RADIUS) || dist < Hub.RADIUS) {
//				return new HubPosition(h, p);
//			}
//		}
		return null;
	}
	
	/**
	 * returns the closest vertex within radius that is not the excluded point
	 * 
	 */
	public Vertex findClosestVertexPosition(Point a, Point anchor, double radius) {
		Vertex anchorV = null;
		if (anchor != null) {
			Position pos = hitTest(anchor);
			if (pos instanceof Vertex) {
				anchorV = (Vertex) pos;
				if (a.equals(anchor)) {
					/*
					 * a equals anchor, so starting this segment
					 * if exactly on a vertex, then use that one
					 */
					return anchorV;
				}
			}
		}
		
		Vertex closest = null;
		
		for (Vertex i : new ArrayList<Vertex>(){{addAll(intersections);addAll(sources);addAll(sinks);}}) {
			Point ip = i.getPoint();
			double dist = Point.distance(a, ip);
			if (DMath.doubleEquals(dist, radius) || dist < radius) {
				if (anchorV == null || dist < Point.distance(ip, anchor)) {
					if (closest == null || (Point.distance(a, ip) < Point.distance(a, closest.getPoint()))) {
						closest = i;
					}
				}
			}	
		}
		
		return closest;
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
		
		List<Point> scratch = new ArrayList<Point>(stroke);
		
		for (int i = 0; i < scratch.size()-1; i++) {
			Point preA = stroke.get(i);
			Point preB = stroke.get(i+1);
			
			Position aP = findClosestPosition(preA, null, MODEL.ROAD_WIDTH);
			Point a = (aP != null) ? aP.getPoint() : preA;
			
			Position bP = findClosestPosition(preB, null, MODEL.ROAD_WIDTH);
			Point b = (bP != null) ? bP.getPoint() : preB;
			
			scratch.set(i, a);
			scratch.set(i+1, b);
			
			if (a.equals(b)) {
				continue;
			}
			
			processNewSegment(a, b);
		}
		
		cleanupEdges();
	}
	
	private void processNewSegment(Point a, Point b) {
		
		logger.debug("process segment: " + a + " " + b);
		
//		Vertex aV = findClosestVertexPosition(preA, null, 20);
//		Point a = (aV != null) ? aV.getPoint() : preA;
//		if (aV == null) {
//			findClosestEdgePosition()
//		}
//		
//		Vertex bV = findClosestVertexPosition(preB, a, 20);
//		Point b = (bV != null) ? bV.getPoint() : preB;
//		if (bV == null) {
//			findClosestEdgePosition()
//		}
		
		/*
		 * first, split the segment up into ranges that do not overlap any existing segments
		 */
		
		List<Double> ranges = nonOverlappingRanges(a, b);
		
		for (int i = 0; i < ranges.size(); i+=2) {
			Point aa = Point.point(a, b, ranges.get(i));
			Point bb = Point.point(a, b, ranges.get(i+1));
			processNewRange(aa, bb);
		}
	}
	
	private List<Double> nonOverlappingRanges(Point a, Point b) {
		
		List<Double> ranges = new ArrayList<Double>();
		ranges.add(0.0);
		ranges.add(1.0);
		
		for (Segment in : segTree.findAllSegments(a, b)) {
			int index = in.index;
			Point c = in.edge.getPoint(index);
			Point d = in.edge.getPoint(index+1);
			
			try {
				Point.intersection(a, b, c, d);
			} catch (OverlappingException ex) {
				
				double cParam = Point.param(c, a, b);
				double dParam = Point.param(d, a, b);
				
				if (dParam < cParam) {
					double tmp = dParam;
					dParam = cParam;
					cParam = tmp;
				}
				
				double overlapStart = Math.max(0.0, cParam);
				double overlapEnd = Math.min(dParam, 1.0);
				
				// find range that overlap breaks
				int r = -1;
				for (int i = 0; i < ranges.size(); i+=2) {
					double aa = ranges.get(i);
					double bb = ranges.get(i+1);
					if ((DMath.doubleEquals(aa, overlapStart) || aa < overlapStart) && (DMath.doubleEquals(bb, overlapEnd) || overlapEnd < bb)) {
						r = i;
					}
				}
				assert r != -1;
				
				double aa = ranges.get(r);
				double bb = ranges.get(r+1);
				
				ranges.remove(r);
				ranges.remove(r);
				
				if (!DMath.doubleEquals(bb, overlapEnd) && overlapEnd < bb) {
					ranges.add(r, bb);
					ranges.add(r, overlapEnd);
				}
				if (!DMath.doubleEquals(aa, overlapStart) && aa < overlapStart) {
					ranges.add(r, overlapStart);
					ranges.add(r, aa);
				}
				
			}
			
			
		}
		
		return ranges;
	}
	
	private void processNewRange(Point a, Point b) {
		
		Timeline timeline = new Timeline(a, b);
		
		for (Segment in : segTree.findAllSegments(a, b)) {
			int index = in.index;
			Point c = in.edge.getPoint(index);
			Point d = in.edge.getPoint(index+1);
			
			try {
				
				Point inter = Point.intersection(a, b, c, d);
				if (inter != null) {
					
					double interParam = Point.param(inter, a, b);
					double interStartParam = Point.travelBackward(a, b, interParam, MODEL.ROAD_WIDTH);
					double interEndParam = Point.travelForward(a, b, interParam, MODEL.ROAD_WIDTH);
					
					timeline.addEvent(new IntersectionEvent(inter, interParam, interStartParam, interEndParam));
				}
//				else {
					
				Point aProjected = Point.point(c, d, DMath.clip(Point.u(c, a, d)));
				Point bProjected = Point.point(c, d, DMath.clip(Point.u(c, b, d)));
				
				CloseEvent cEvent = detectClose(timeline, c, a, b);
				CloseEvent dEvent = detectClose(timeline, d, a, b);
				CloseEvent aEvent = detectClose(timeline, aProjected, a, b);
				CloseEvent bEvent = detectClose(timeline, bProjected, a, b);
				
				CloseEvent e = null;
				
				if (cEvent != null) {
					e = new CloseEvent(cEvent.getBorderStartParam(), cEvent.getBorderEndParam());
				}
				
				if (dEvent != null) {
					if (e == null) {
						e = new CloseEvent(dEvent.getBorderStartParam(), dEvent.getBorderEndParam());
					} else {
						e = new CloseEvent(Math.min(e.getBorderStartParam(), dEvent.getBorderStartParam()), Math.max(e.getBorderEndParam(), dEvent.getBorderEndParam()));
					}
				}
				
				if (aEvent != null) {
					if (e == null) {
						e = new CloseEvent(aEvent.getBorderStartParam(), aEvent.getBorderEndParam());
					} else {
						e = new CloseEvent(Math.min(e.getBorderStartParam(), aEvent.getBorderStartParam()), Math.max(e.getBorderEndParam(), aEvent.getBorderEndParam()));
					}
				}
				
				if (bEvent != null) {
					if (e == null) {
						e = new CloseEvent(bEvent.getBorderStartParam(), bEvent.getBorderEndParam());
					} else {
						e = new CloseEvent(Math.min(e.getBorderStartParam(), bEvent.getBorderStartParam()), Math.max(e.getBorderEndParam(), bEvent.getBorderEndParam()));
					}
				}
				
				if (e != null) {
					timeline.addEvent(e);
				}
//			}
				
			} catch (OverlappingException ex) {
				
				assert false;
				
			}
			
		}
		
		List<Cluster> clusters = timeline.clusters;
		
		if (clusters.size() == 0) {
			
			assert timeline.aClusterIndex == -1;
			assert timeline.bClusterIndex == -1;
			
			Point aa = a;
			
			Point bb = b;
			addSegment(aa, bb);
			
		} else if (timeline.aClusterIndex == timeline.bClusterIndex && timeline.aClusterIndex != -1) {
			
			assert clusters.size() == 1;
			
			Cluster c = clusters.get(0);
			
			if (c.intersectionEvents.isEmpty()) {
				
				;
				
			} else {
				
				Point aa = a;
				Point bb;
				
				for (IntersectionEvent e : c.intersectionEvents) {
					
					bb = e.getSourceStart();
					if (!aa.equals(bb)) {
						addSegment(aa, bb);
					}
					
					aa = bb;
				}
				
				bb = b;
				if (!aa.equals(bb)) {
					addSegment(aa, bb);
				}
				
			}
			
		} else {
			
			Point aa;
			Point bb;
			
			/*
			 * handle A cluster
			 */
			if (timeline.aClusterIndex != -1) {
				assert timeline.aClusterIndex == 0;
				// there is an A cluster
				
				Cluster c = clusters.get(timeline.aClusterIndex);
				
				if (c.intersectionEvents.isEmpty()) {
					
					aa = a;
					
					bb = Point.point(a, b, c.borderEndParam);
					addSegment(aa, bb);
					
					aa = bb;
					
				} else {
					
					aa = a;
					
					for (IntersectionEvent e : c.intersectionEvents) {
						bb = e.getSourceStart();
						
						if (!aa.equals(bb)) {
							addSegment(aa, bb);
						}
						
						aa = bb;
					}
					
					bb = Point.point(a, b, c.borderEndParam);
					addSegment(aa, bb);
					
					aa = bb;
					
				}
				
			} else {
				// there is no A cluster
				aa = a;
			}
			
			/*
			 * handle all in between clusters
			 */
			int middleFirst = (timeline.aClusterIndex != -1) ? 1 : 0;
			int middleLast = (timeline.bClusterIndex != -1) ? clusters.size()-2 : clusters.size()-1;
			for (int middleIndex = middleFirst; middleIndex <= middleLast; middleIndex++) {
				Cluster c = clusters.get(middleIndex);
				
				if (c.intersectionEvents.isEmpty()) {
					
					bb = Point.point(a, b, c.borderStartParam); 
					addSegment(aa, bb);
					
					aa = Point.point(a, b, c.borderEndParam);
					
				} else {
					
					bb = Point.point(a, b, c.borderStartParam); 
					addSegment(aa, bb);
					
					aa = bb;
					
					for (IntersectionEvent e : c.intersectionEvents) {
						
						bb = e.getSourceStart();
						if (!aa.equals(bb)) {
							addSegment(aa, bb);
						}
						
						aa = bb;
						
					}
					
					bb = Point.point(a, b, c.borderEndParam);
					addSegment(aa, bb);
					
					aa = bb;
					
				}
				
			}
			
			/*
			 * handle b cluster
			 */
			if (timeline.bClusterIndex != -1) {
				assert timeline.bClusterIndex == clusters.size()-1;
				// there is a B cluster
				
				Cluster c = clusters.get(timeline.bClusterIndex);
				
				if (c.intersectionEvents.isEmpty()) {
					
					bb = Point.point(a, b, c.borderStartParam);
					addSegment(aa, bb);
					
					aa = bb;
					
					bb = b;
					if (!aa.equals(bb)) {
						addSegment(aa, bb);
					}
					
				} else {
					
					bb = Point.point(a, b, c.borderStartParam);
					addSegment(aa, bb);
					
					aa = bb;
					
					for (IntersectionEvent e : c.intersectionEvents) {
						
						bb = e.getSourceStart();
						addSegment(aa, bb);
						
						aa = bb;
					}
					
					bb = b;
					if (!aa.equals(bb)) {
						addSegment(aa, bb);
					}
					
				}
				
			} else {
				// there is no B cluster
				bb = b;
				addSegment(aa, bb);
			}
			
		}
		
	}
	
	private CloseEvent detectClose(Timeline t, Point p, Point a, Point b) {
		//how close is p to <a, b>?
		Point[] ints = new Point[2];
		int num = Point.circleLineIntersections(p, MODEL.ROAD_WIDTH, a, b, ints);
		switch (num) {
		case 2: {
			Point p1 = ints[0];
			Point p2 = ints[1];
			double param1 = Point.param(p1, a, b);
			double param2 = Point.param(p2, a, b);
			return (param1 < param2) ?
					new CloseEvent(param1, param2) :
						new CloseEvent(param2, param1);
		}
		default:
			return null;
		}
	}
	
	public class Timeline {
		
		Point a;
		Point b;
		
		final List<Cluster> clusters = new ArrayList<Cluster>();
		int aClusterIndex = -1;
		int bClusterIndex = -1;
		
		Timeline(Point a, Point b) {
			this.a = a;
			this.b = b;
		}
		
		void addEvent(Event e) {
			
			if (!DMath.doubleEquals(e.getBorderStartParam(), 1.0) && e.getBorderStartParam() > 1.0) {
				return;
			}
			if (!DMath.doubleEquals(e.getBorderEndParam(), 0.0) && e.getBorderEndParam() < 0.0) {
				return;
			}
			
//			for (Cluster c : clusters) {
//				for (Event ee : c.events) {
////					if (e.getSourceStart().equals(ee.getSourceStart()) && e.getSourceEnd().equals(ee.getSourceEnd())) {
////						return;
////					}
//				}
//				if (c.borderStartParam < e.getBorderStartParam() && c.borderEndParam > e.getBorderEndParam()) {
//					// cluster already "contains" this event
//					
//					//test this
//					String.class.getName();
//				}
//			}
			
//			logger.debug(e + " " + e.getSourceStart() + " " + e.getSourceEnd());
			
			final List<Cluster> overlapping = new ArrayList<Cluster>();
			for (Cluster c : clusters) {
				if (DMath.rangesOverlap(e.getBorderStartParam(), e.getBorderEndParam(), c.borderStartParam, c.borderEndParam)) {
					overlapping.add(c);
				}
			}
			
			Cluster acc = new Cluster(a, b, e);
			
			for (Cluster c : overlapping) {
				acc = Cluster.merge(acc, c);
				clusters.remove(c);
			}
			
			// figure out where to insert acc
			int insertionPoint = -1;
			for (int i = 0; i < clusters.size(); i++) {
				Cluster c = clusters.get(i);
				if (acc.borderEndParam < c.borderStartParam) {
					insertionPoint = i;
					break;
				}
			}
			if (insertionPoint == -1) {
				insertionPoint = clusters.size();
			}
			
			clusters.add(insertionPoint, acc);
			
			aClusterIndex = -1;
			bClusterIndex = -1;
			for (int i = 0; i < clusters.size(); i++) {
				Cluster c = clusters.get(i);
				if (DMath.doubleEquals(c.borderStartParam, 0.0) || c.borderStartParam < 0.0) {
					assert aClusterIndex == -1;
					aClusterIndex = i;
				}
				if (DMath.doubleEquals(c.borderEndParam, 1.0) || c.borderEndParam > 1.0) {
					assert bClusterIndex == -1;
					bClusterIndex = i;
				}
			}
		}
		
	}
	
	private static class Cluster {
		
		Point a;
		Point b;
		
		double borderStartParam;
		double borderEndParam;
		
//		Point startSource;
//		Point endSource;
		
		List<Event> events = new ArrayList<Event>();
		
		List<IntersectionEvent> intersectionEvents = new ArrayList<IntersectionEvent>();
		List<CloseEvent> closeEvents = new ArrayList<CloseEvent>();
		
		private Cluster(Point a, Point b) {
			this.a = a;
			this.b = b;
		}
		
		Cluster(Point a, Point b, Event e) {
			
			this.a = a;
			this.b = b;
			
			events.add(e);
			if (e instanceof IntersectionEvent) {
				// sort based on param of source
//				int index = Collections.binarySearch(intersectionEvents, (IntersectionEvent)e, IntersectionEvent.COMPARATOR);
//				if (index < 0) {
//					// not found
//					int insertionPoint = -(index + 1);
//					intersectionEvents.add(insertionPoint, (IntersectionEvent)e);
//				} else {
//					// found
//					assert false;
//				}
				intersectionEvents.add((IntersectionEvent)e);
			} else if (e instanceof CloseEvent) {
				closeEvents.add((CloseEvent)e);
			} else {
				assert false;
			}
			borderStartParam = e.getBorderStartParam();
			borderEndParam = e.getBorderEndParam();
//			startSource = e.getSourceStart();
//			endSource = e.getSourceEnd();
		}
		
		static Cluster merge(final Cluster c1, final Cluster c2) {
			
			Cluster acc = new Cluster(c1.a, c1.b);
			
			acc.events.addAll(c1.events);
			acc.events.addAll(c2.events);
			
			// sort based on param of source
			List<IntersectionEvent> allIntersectionEvents = new ArrayList<IntersectionEvent>(){{addAll(c1.intersectionEvents);addAll(c2.intersectionEvents);}};
			for (IntersectionEvent e : allIntersectionEvents) {
				int index = Collections.binarySearch(acc.intersectionEvents, e, IntersectionEvent.COMPARATOR);
				if (index < 0) {
					// not found
					int insertionPoint = -(index + 1);
					
					for (IntersectionEvent f : acc.intersectionEvents) {
						double dist = Point.distance(f.getSourceStart(), e.getSourceStart());
						if (dist < 1.0) {
							String.class.getName();
						} else {
							
						}
					}
					
					acc.intersectionEvents.add(insertionPoint, e);
				} else {
					// found
					//assert false;
				}
			}
			
			acc.closeEvents.addAll(c1.closeEvents);
			acc.closeEvents.addAll(c2.closeEvents);
			
			acc.borderStartParam = c1.borderStartParam;
			acc.borderEndParam = c1.borderEndParam;
//			acc.startSource = startSource;
//			acc.endSource = endSource;
			
			if (c2.borderStartParam < c1.borderStartParam) {
				acc.borderStartParam = c2.borderStartParam;
//				acc.startSource = c.startSource;
			}
			if (c2.borderEndParam > c1.borderEndParam) {
				acc.borderEndParam = c2.borderEndParam;
//				acc.endSource = c.endSource;
			}
			
			return acc;
		}
		
	}
	
	
	
	
	
	
	
	public Position findClosestPosition(Point a) {
		return findClosestPosition(a, null, Double.POSITIVE_INFINITY);
	}
	
	public Position findClosestPosition(Point a, double radius) {
		return findClosestPosition(a, null, radius);
	}
	
	public Position findClosestPosition(Point a, Point anchor, double radius) {
		Vertex closestV = findClosestVertexPosition(a, anchor, radius);
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
	
	private void cleanupEdges() {
		
		List<Edge> toRemove = new ArrayList<Edge>();
		boolean changed;
		
		while (true) {
			toRemove.clear();
			changed = false;
			
			for (Edge e : edges) {
				if (e.getTotalLength() <= MODEL.CAR_WIDTH) {
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
		assert !segmentOverlaps(a, b);
		
		logger.debug("addSegment: " + a + " " + b);
		
		Position pos = hitTest(a);
		final Vertex aV;
		if (pos != null) {
			if (pos instanceof Vertex) {
				aV = (Vertex)pos;
			} else {//if (pos instanceof EdgePosition) {
				aV = split((EdgePosition)pos);
			}
//			else {
//				assert pos instanceof HubPosition;
//				aV = addVertexToHub((HubPosition)pos);
//			}
		} else {
			aV = createVertex(a);
		}
		
		pos = hitTest(b);
		final Vertex bV;
		if (pos != null) {
			if (pos instanceof Vertex) {
				bV = (Vertex)pos;
			} else {//if (pos instanceof EdgePosition) {
				bV = split((EdgePosition)pos);
			}
//			else {
//				assert pos instanceof HubPosition;
//				bV = addVertexToHub((HubPosition)pos);
//			}
		} else {
			bV = createVertex(b);
		}
		
		Edge newEdge = createEdge(aV, bV, new ArrayList<Point>(){{add(aV.getPoint());add(bV.getPoint());}});
		
		assert edges.contains(newEdge);
		
		edgesChanged(aV);
		edgesChanged(bV);
	}
	
	private boolean segmentOverlaps(Point a, Point b) {
		
		for (Segment in : segTree.findAllSegments(a, b)) {
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
		
//		for (Hub h : hubs) {
//			if (Point.distance(a, h.getPoint()) < 40) {
//				return true;
//			}
//			if (Point.distance(b, h.getPoint()) < 40) {
//				return true;
//			}
//		}
		
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
		
		boolean betweenPoints = (!DMath.doubleEquals(param, 0.0));
		
		assert param >= 0.0;
		assert param < 1.0;
		
		final Point c = e.getPoint(index);
		final Point d = e.getPoint(index+1);
		
		Vertex v = createVertex(p);
		
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
				
				Vertex cV = createVertex(c);
				createEdge(cV, v,  new ArrayList<Point>(){{add(c);add(p);}});
				
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
				
				Vertex dV = createVertex(d);
				createEdge(dV, v,  new ArrayList<Point>(){{add(d);add(p);}});
				
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
	
//	private Vertex addVertexToHub(HubPosition pos) {
//		Hub h = pos.getHub();
//		Point p = pos.getPoint();
//		assert DMath.doubleEquals(Point.distance(h.getPoint(), p), Hub.RADIUS);
//		
//		Vertex v = createIntersection(p);
//		
//		h.addVertex(v);
//		
//		v.addHub(h);
//		
//		return v;
//	}
	
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
	
	public void addVertexTop(Point p) {
		
		if (p.getY() <= 10 || p.getX() <= 10) {
			// source
			
			createVertex(p);
			
		} else if (p.getX() >= MODEL.WORLD_WIDTH-10 || p.getY() >= MODEL.WORLD_HEIGHT-10) {
			// sink
			
			createVertex(p);
			
		} else {
			//addIntersection(p);
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
			if (e.getTotalLength() <= MODEL.CAR_WIDTH) {
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
