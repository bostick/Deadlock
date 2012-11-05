package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.OverlappingException;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.core.graph.SweepEvent.SweepEventType;
import com.gutabi.deadlock.model.Stroke;

@SuppressWarnings("static-access")
public class Graph implements SweepEventListener {
	
	private final ArrayList<Edge> edges = new ArrayList<Edge>();
	
	private final ArrayList<Intersection> intersections = new ArrayList<Intersection>();
	private final ArrayList<Source> sources = new ArrayList<Source>();
	private final ArrayList<Sink> sinks = new ArrayList<Sink>();
	
	private List<StopSign> signs = new ArrayList<StopSign>();
	private List<Merger> mergers = new ArrayList<Merger>();
	
	private Rect aabb;
	
	private static final Logger logger = Logger.getLogger(Graph.class);
	
	public Graph() {
		
	}
	
	private List<Vertex> getAllVertices() {
		List<Vertex> all = new ArrayList<Vertex>();
		all.addAll(sources);
		all.addAll(sinks);
		all.addAll(intersections);
		return all;
	}
	
	
	public void addSource(WorldSource s) {
		sources.add(s);
		refreshVertexIDs();
		computeAABB();
	}
	
	public void addSink(WorldSink s) {
		sinks.add(s);
		refreshVertexIDs();
		computeAABB();
	}
	
	public void addIntersection(Intersection i) {
		intersections.add(i);
		refreshVertexIDs();
		computeAABB();
	}
	
	public Edge createEdgeTop(Vertex start, Vertex end, List<Point> pts) {
		
		Edge e = createEdge(start, end, pts, (!start.eds.isEmpty()?1:0)+(!end.eds.isEmpty()?2:0));
		
		automaticMergeOrDestroy(start);
		automaticMergeOrDestroy(end);
		
		computeAABB();
		
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
		
		computeAABB();
		
	}
	
	public void removeVertexTop(Vertex v) {
		
		Set<Vertex> affectedVertices = new HashSet<Vertex>();
		
		/*
		 * copy, since removing edges modifies v.eds
		 * and use a set since loops will be in the list twice
		 */
		Set<Edge> eds = new HashSet<Edge>(v.eds);
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
		
		computeAABB();
		
	}
	
	public void removeMergerTop(Merger m) {
		
		Vertex top = m.top;
		Vertex left = m.left;
		Vertex right = m.right;
		Vertex bottom = m.bottom;
		
		destroyMerger(m);
		
		automaticMergeOrDestroy(top);
		automaticMergeOrDestroy(left);
		automaticMergeOrDestroy(right);
		automaticMergeOrDestroy(bottom);
		
		refreshVertexIDs();
		
		computeAABB();
	}
	
	public void removeStopSignTop(StopSign s) {
		
		destroyStopSign(s);
		
		computeAABB();
		
	}
	
	private void automaticMergeOrDestroy(Vertex v) {
		
		if (v instanceof Intersection) {
			
			List<Edge> cons = v.eds;
			
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
	
	
	
	
	public void processNewStrokeTop(Stroke stroke) {
		
		vertexCount = 0;
		capsuleCount = 0;
		vertexEvents = new ArrayList<SweepEvent>();
//		startVertex = null;
		
		sweepStart(stroke);
		
		if ((vertexCount + capsuleCount) == 0) {
//			logger.debug("start in nothing");
			vertexEvents.add(new SweepEvent(null, stroke.pts.get(0), 0, 0.0));
		} else if (vertexCount > 0) {
			SweepEvent e = new SweepEvent(SweepEventType.ENTERVERTEX, stroke.pts.get(0), 0, 0.0);
			vertexEvents.add(e);
		} else {
			vertexEvents.add(new SweepEvent(SweepEventType.ENTERCAPSULE, stroke.pts.get(0), 0, 0.0));
		}
		
		for (int i = 0; i < stroke.pts.size()-1; i++) {
			sweep(stroke, i);
		}
		
		if ((vertexCount + capsuleCount) == 0) {
//			logger.debug("end in nothing");
			vertexEvents.add(new SweepEvent(null, stroke.pts.get(stroke.pts.size()-1), stroke.pts.size()-1, 0.0));
		} else if (vertexCount > 0) {
			vertexEvents.add(new SweepEvent(SweepEventType.EXITVERTEX, stroke.pts.get(stroke.pts.size()-1), stroke.pts.size()-1, 0.0));
		} else {
			vertexEvents.add(new SweepEvent(SweepEventType.EXITCAPSULE, stroke.pts.get(stroke.pts.size()-1), stroke.pts.size()-1, 0.0));
		}
		
		
		logger.debug("vertexEvents:");
		logger.debug(vertexEvents);
		
		SweepEvent e = vertexEvents.get(0);
		Point p = e.p;
//		Entity hit = bestHitTest(p, stroke.r);
		if (e.type == SweepEventType.ENTERCAPSULE) {
			logger.debug("split");
			EdgePosition pos = findClosestEdgePosition(p, stroke.r);
			split(pos);
//			e.setVertex(v);
		} else if (e.type == SweepEventType.ENTERVERTEX) {
			logger.debug("already exists");
//			Vertex v = e.getVertex();
		} else {
//			assert hitTest(p) == null;
			logger.debug("create");
			Intersection v = new Intersection(p);
			addIntersection(v);
//			e.setVertex(v);
		}
		
		for (int i = 0; i < vertexEvents.size()-1; i+=2) {
			SweepEvent e0 = vertexEvents.get(i);
			SweepEvent e1 = vertexEvents.get(i+1);
			
			if (e0.type == SweepEventType.ENTERVERTEX && e1.type == SweepEventType.EXITVERTEX) {
				
//				e1.setVertex(e0.getVertex());
				
				i = i+1;
				e0 = vertexEvents.get(i);
				e1 = vertexEvents.get(i+1);
			} else if (e0.type == SweepEventType.ENTERCAPSULE && e1.type == SweepEventType.EXITCAPSULE) {
				i = i+1;
				e0 = vertexEvents.get(i);
				e1 = vertexEvents.get(i+1);
			}
			
			Entity bh = bestHitTest(e0.p, stroke.r);
			if (bh == null) {
				logger.debug("broken");
				return;
			} else if (bh instanceof Edge) {
				logger.debug("broken 2");
				return;
			}
			Vertex v0 = (Vertex)bh;
			
			Vertex v1;
			Point p1 = e1.p;
			Entity hit1 = bestHitTest(p1, stroke.r);
			if (hit1 instanceof Edge) {
				logger.debug("split");
				EdgePosition pos = findClosestEdgePosition(p1, stroke.r);
				v1 = split(pos);
			} else if (hit1 instanceof Vertex) {
				logger.debug("already exists");
				v1 = (Vertex)hit1;
			} else {
				assert hitTest(p1) == null;
				logger.debug("create");
				Intersection ii = new Intersection(p1);
				addIntersection(ii);
				v1 = ii;
			}
			
			createEdgeTop(v0, v1, stroke.pts.subList(e0.index, e1.index+1));
		}
		
	}
	
	List<SweepEvent> events;
	int vertexCount = 0;
	int capsuleCount = 0;
	List<SweepEvent> vertexEvents;
//	Vertex startVertex;
	
	public void sweepStart(Stroke s) {
		events = new ArrayList<SweepEvent>();
		
		for (Vertex v : getAllVertices()) {
			v.setSweepEventListener(this);
			v.sweepStart(s);
		}
		for (Edge e : edges) {
			e.setSweepEventListener(this);
			e.sweepStart(s);
		}
		
		Collections.sort(events, SweepEvent.COMPARATOR);
		
		for (SweepEvent e : events) {
			startSorted(e);
		}
		
	}
	
	public void start(SweepEvent e) {
		events.add(e);
	}
	
	public void sweep(Stroke s, int index) {
		events = new ArrayList<SweepEvent>();
		
		for (Vertex v : getAllVertices()) {
			v.setSweepEventListener(this);
			v.sweep(s, index);
		}
		for (Edge e : edges) {
			e.setSweepEventListener(this);
			e.sweep(s, index);
		}
		
		Collections.sort(events, SweepEvent.COMPARATOR);
		
		for (SweepEvent e : events) {
			eventSorted(e);
		}
		
	}
	
	public void event(SweepEvent e) {
		events.add(e);
	}
	
	private void startSorted(SweepEvent e) {
		switch (e.type) {
//		case CAPSULESTART:
//			count++;
////			if (count == 1) {
////				vertexEvents.add(e);
////			}
//			break;
//		case VERTEXSTART:
//			count++;
////			if (count == 1) {
////				vertexEvents.add(e);
////			}
//			break;
		case ENTERCAPSULE:
			capsuleCount++;
			break;
		case ENTERVERTEX:
//			startVertex = e.getVertex();
			vertexCount++;
			break;
		case EXITCAPSULE:
		case EXITVERTEX:
			assert false;
			break;
		}
	}
	
	private void eventSorted(SweepEvent e) {
		switch (e.type) {
//		case CAPSULESTART:
//		case VERTEXSTART:
//		case NOTHINGSTART:
//		case NOTHINGEND:
//			assert false;
//			break;
		case ENTERVERTEX:
			vertexCount++;
			if ((vertexCount + capsuleCount) == 1) {
				vertexEvents.add(e);
			}
			break;
		case ENTERCAPSULE:
			capsuleCount++;
			if ((vertexCount + capsuleCount) == 1) {
				vertexEvents.add(e);
			}
			break;
		case EXITVERTEX:
			vertexCount--;
			if ((vertexCount + capsuleCount) == 0) {
				vertexEvents.add(e);
			}
			break;
		case EXITCAPSULE:
			capsuleCount--;
			if ((vertexCount + capsuleCount) == 0) {
				vertexEvents.add(e);
			}
			break;
		}
	}
	
	public void insertMergerTop(Point p) {
		
		Merger m = new Merger(new Point(p.x-Merger.MERGER_WIDTH/2, p.y-Merger.MERGER_HEIGHT/2));
		
		mergers.add(m);
		
		sinks.add(m.top);
		sinks.add(m.left);
		
		sources.add(m.right);
		sources.add(m.bottom);
		
		refreshVertexIDs();
		
		m.top.m = m;
		m.left.m = m;
		m.right.m = m;
		m.bottom.m = m;
		
		computeAABB();
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
			
			start.eds.add(e);
			end.eds.add(e);
			
			if ((dec & 1) == 1) {
				StopSign startSign = new StopSign(e, 0);
				signs.add(startSign);
				
				e.startSign = startSign;
				startSign.computePoint();
				
			}
			
			if ((dec & 2) == 2) {
				StopSign endSign = new StopSign(e, 1);
				signs.add(endSign);
				
				e.endSign = endSign;
				endSign.computePoint();
				
			}

		}
		
		return e;
	}
	
	private void destroyEdge(Edge e) {
		assert edges.contains(e);
		
		if (!e.isStandAlone()) {
			
			e.start.eds.remove(e);
			e.end.eds.remove(e);
			
			destroyStopSign(e.startSign);
			destroyStopSign(e.endSign);
			
		}
		
		edges.remove(e);
		
		refreshEdgeIDs();
		
	}
	
	private void destroyMerger(Merger m) {
		
		m.top.m = null;
		m.left.m = null;
		m.right.m = null;
		m.bottom.m = null;
		
		mergers.remove(m);
		
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
	
	private Intersection createIntersection(Point p) {
		
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
	
	public void postStop() {
		
		for (Vertex v : getAllVertices()) {
			v.postStop();
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
	
	private void computeAABB() {
		
		aabb = null;
		
		for (Vertex v : getAllVertices()) {
			aabb = Rect.union(aabb, v.getAABB());
		}
		for (Edge e : edges) {
			aabb = Rect.union(aabb, e.getAABB());
		}
		
		/*
		 * signs do not contribute to the rendering rect
		 */
//		for (StopSign s : signs) {
//			
//		}
		
	}
	
	public Rect getAABB() {
		return aabb;
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
		
		List<Edge> eds = new ArrayList<Edge>(start.eds);
		
		for (Edge e : start.eds) {
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
		if (v.eds.size() == 1) {
			return true;
		} else {
			List<Edge> eds = new ArrayList<Edge>(v.eds);
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
		for (Merger m : mergers) {
			if (m.hitTest(p)) {
				return m;
			}
		}
		return null;
	}
	
	/**
	 * even if hitting both a vertex and an edge, only return vertex
	 * ignore stop signs
	 */
	public Entity bestHitTest(Point p, double radius) {
		assert p != null;
		for (Vertex v : getAllVertices()) {
			if (v.hitTest(p, radius)) {
				return v;
			}
		}
		for (Edge e : edges) {
			if (e.hitTest(p, radius)) {
				return e;
			}
		}
		return null;
	}
	
//	public GraphPosition skeletonHitTest(Point p) {
//		assert p != null;
//		for (Vertex v : getAllVertices()) {
//			VertexPosition pos = v.skeletonHitTest(p);
//			if (pos != null) {
//				return pos;
//			}
//		}
//		for (Edge e : edges) {
//			EdgePosition pos = e.skeletonHitTest(p);
//			if (pos != null) {
//				return pos;
//			}
//		}
//		return null;
//	}
	
	/**
	 * returns the closest edge within radius
	 */
	public EdgePosition findClosestEdgePosition(Point a, double radius) {

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
	 * split an edge at point
	 * Edge e will have been removed
	 * return Intersection at split point
	 */
	public Intersection split(EdgePosition pos) {
		
//		EdgePosition pos = (EdgePosition)skeletonHitTest(p);
		
		Edge e = pos.e;
		int index = pos.index;
		double param = pos.param;
		final Point p = pos.p;
		
		assert param >= 0.0;
		assert param < 1.0;
		
		Intersection v = createIntersection(p);
		
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
		for (Merger m : mergers) {
			distances[m.top.id][m.bottom.id] = Merger.MERGER_HEIGHT;
			distances[m.bottom.id][m.top.id] = Merger.MERGER_HEIGHT;
			distances[m.left.id][m.right.id] = Merger.MERGER_WIDTH;
			distances[m.right.id][m.left.id] = Merger.MERGER_WIDTH;
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
	 * merge two edges (possibly the same edge) at the given intersection
	 * 
	 * no colinear points in returned edge
	 */
	private void merge(Vertex v) {
		
		assert v.eds.size() == 2;
		Edge e1 = v.eds.get(0);
		Edge e2 = v.eds.get(1);
		
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
			assert v.eds.size() == 2;
				
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
			
			createEdge(e1End, e2End, pts, (e1.endSign!=null?1:0) + (e2.endSign!=null?2:0));
			
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
		
		List<Edge> edgesCopy;
		List<Vertex> verticesCopy;
		List<Merger> mergersCopy;
		List<StopSign> signsCopy;
		synchronized (MODEL) {
			edgesCopy = new ArrayList<Edge>(edges);
			verticesCopy = new ArrayList<Vertex>(getAllVertices());
			mergersCopy = new ArrayList<Merger>(mergers);
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
		
		for (Merger m : mergersCopy) {
			m.paint(g2);
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
		
		if (MODEL.DEBUG_DRAW) {
			List<Edge> edgesCopy;
			
			synchronized (MODEL) {
				edgesCopy = new ArrayList<Edge>(edges);
			}
			
			for (Edge e : edgesCopy) {
//				e.paintSkeleton(g2);
				e.paintBorders(g2);
			}
		}
		
	}
	
	public void paintIDs(Graphics2D g2) {
		
		if (MODEL.DEBUG_DRAW) {
			
			List<Vertex> verticesCopy;
			synchronized (MODEL) {
				verticesCopy = new ArrayList<Vertex>(getAllVertices());
			}
			
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
						Capsule es = e.getCapsule(i);
						
						for (int j = i+1; j < f.size()-1; j++) {
							Capsule fs = f.getCapsule(j);
							
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
						Capsule es = e.getCapsule(i);
						
						for (int j = 0; j < f.size()-1; j++) {
							Capsule fs = f.getCapsule(j);
							
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
