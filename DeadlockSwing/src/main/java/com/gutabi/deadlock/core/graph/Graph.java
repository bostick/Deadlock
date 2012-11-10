package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.OverlappingException;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.core.graph.SweepEvent.SweepEventType;
import com.gutabi.deadlock.model.Stroke;

@SuppressWarnings("static-access")
public class Graph {
	
	private final ArrayList<Road> roads = new ArrayList<Road>();
	
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
	
	public Road createRoadTop(Vertex start, Vertex end, List<Point> pts) {
		
		Road e = createRoad(start, end, pts, (!start.roads.isEmpty()?1:0)+(!end.roads.isEmpty()?2:0));
		
		automaticMergeOrDestroy(start);
		automaticMergeOrDestroy(end);
		
		computeAABB();
		
		return e;
	}
	
	public void removeRoadTop(Road e) {
		
		/*
		 * have to properly cleanup start and end intersections before removing roads
		 */
		if (!e.isLoop()) {
			
			Vertex eStart = e.start;
			Vertex eEnd = e.end;
			
			destroyRoad(e);
			
			automaticMergeOrDestroy(eStart);
			automaticMergeOrDestroy(eEnd);
			
		} else if (!e.isStandAlone()) {
			
			Vertex v = e.start;
			
			destroyRoad(e);
			
			automaticMergeOrDestroy(v);
			
		} else {
			destroyRoad(e);
		}
		
		computeAABB();
		
	}
	
	public void removeVertexTop(Vertex v) {
		
		Set<Vertex> affectedVertices = new HashSet<Vertex>();
		
		/*
		 * copy, since removing roads modifies v.eds
		 * and use a set since loops will be in the list twice
		 */
		Set<Road> roads = new HashSet<Road>(v.roads);
		for (Road e : roads) {
			
			if (!e.isLoop()) {
				
				Vertex eStart = e.start;
				Vertex eEnd = e.end;
				
				affectedVertices.add(eStart);
				affectedVertices.add(eEnd);
				
				destroyRoad(e);
				
			} else {
				
				Vertex eV = e.start;
				
				affectedVertices.add(eV);
				
				destroyRoad(e);
				
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
		
		forceMergeOrDestroy(top);
		forceMergeOrDestroy(left);
		forceMergeOrDestroy(right);
		forceMergeOrDestroy(bottom);
		
		refreshVertexIDs();
		
		computeAABB();
	}
	
	public void removeStopSignTop(StopSign s) {
		
		destroyStopSign(s);
		
		computeAABB();
		
	}
	
	private void automaticMergeOrDestroy(Vertex v) {
		
		if (!v.isDeleteable()) {
			return;
		}
			
		forceMergeOrDestroy(v);
		
	}
	
	private void forceMergeOrDestroy(Vertex v) {
			
		for (Road e : v.roads) {
			assert roads.contains(e);
		}
		
		if (v.roads.size() == 0) {
			destroyVertex(v);
		} else if (v.roads.size() == 2) {
			merge(v);
		}
		
	}


	
	
	public void processNewStrokeTop(Stroke stroke) {
		
		List<SweepEvent> vertexEvents = stroke.vertexEvents();
		
		logger.debug("vertexEvents:");
		logger.debug(vertexEvents);
		
		SweepEvent e = vertexEvents.get(0);
		Point p = stroke.getPoint(e.index, e.param);
		if (e.type == SweepEventType.ENTERCAPSULE) {
			logger.debug("split");
			RoadPosition pos = findClosestRoadPosition(p, stroke.r);
			split(pos);
		} else if (e.type == SweepEventType.ENTERVERTEX) {
			logger.debug("already exists");
		} else if (e.type == SweepEventType.ENTERMERGER) {
			
			return;
			
		} else if (e.type == null) {
			logger.debug("create");
			Intersection v = new Intersection(p);
			addIntersection(v);
		}
		
		for (int i = 0; i < vertexEvents.size()-1; i+=2) {
			SweepEvent e0 = vertexEvents.get(i);
			SweepEvent e1 = vertexEvents.get(i+1);
			
			if (e0.type == SweepEventType.ENTERVERTEX && e1.type == SweepEventType.EXITVERTEX) {
				
				logger.debug("skipping");
				i = i+1;
				if (i == vertexEvents.size()-1) {
					break;
				}
				e0 = vertexEvents.get(i);
				e1 = vertexEvents.get(i+1);
			} else if (e0.type == SweepEventType.ENTERCAPSULE && e1.type == SweepEventType.EXITCAPSULE) {
				
				logger.debug("skipping");
				
				i = i+1;
				if (i == vertexEvents.size()-1) {
					break;
				}
				e0 = vertexEvents.get(i);
				e1 = vertexEvents.get(i+1);
			} else if (e1.type == SweepEventType.ENTERMERGER) {
				
				return;
				
			}
			
			Point e0p = stroke.getPoint(e0.index, e0.param);
			Entity bh = graphBestHitTest(e0p, stroke.r);
			if (bh == null) {
				logger.debug("broken");
				return;
			} else if (bh instanceof Road) {
				logger.debug("broken 2");
				return;
			}
			Vertex v0 = (Vertex)bh;
			
			Vertex v1;
			Point e1p = stroke.getPoint(e1.index, e1.param);
			Entity hit1 = graphBestHitTest(e1p, stroke.r);
			if (hit1 instanceof Road) {
				logger.debug("split");
				
				RoadPosition pos = null;
				
				/*
				 * find better place to split by checking for intersection with road
				 */
				for (int j = e1.index; j < stroke.pts.size()-1; j++) {
					Point a = stroke.pts.get(j);
					Point b = stroke.pts.get(j+1);
					
					RoadPosition skeletonIntersection = ((Road) hit1).findSkeletonIntersection(a, b);
					
					if (skeletonIntersection != null) {
						
						double strokeCombo = j + Point.param(skeletonIntersection.p, a, b);
						
						if (DMath.greaterThanEquals((strokeCombo), (e1.index+e1.param))
//								&& DMath.lessThanEquals(Point.distance(skeletonIntersection.p, e1p), stroke.r)
								) {
							pos = skeletonIntersection;
							break;
						}
					}
					
				}
				
				if (pos == null) {
					pos = findClosestRoadPosition(e1p, stroke.r);
				}
				
				v1 = split(pos);
				
			} else if (hit1 instanceof Vertex) {
				logger.debug("already exists");
				v1 = (Vertex)hit1;
				
				if (v1 == v0) {
					logger.debug("same vertex");
					return;
				}
				
			} else {
				assert graphHitTest(e1p) == null;
				logger.debug("create");
				Intersection ii = new Intersection(e1p);
				addIntersection(ii);
				v1 = ii;
				
			}
			
			List<Point> roadPts = new ArrayList<Point>();
			roadPts.add(stroke.getPoint(e0.index, e0.param));
			for (int j = e0.index+1; j < e1.index; j++) {
				roadPts.add(stroke.pts.get(j));
			}
			roadPts.add(stroke.pts.get(e1.index));
			if (!DMath.equals(e1.param, 0.0)) {
				roadPts.add(stroke.getPoint(e1.index, e1.param));
			}
			
//			stroke.pts.subList(e0.index, e1.index+1)
			
			createRoadTop(v0, v1, roadPts);
			
			computeVertexRadii();
			
		}
		
	}
	
	public void sweepStart(Stroke s, SweepEventListener l) {
		
		for (Vertex v : getAllVertices()) {
			v.sweepStart(s, l);
		}
		for (Road e : roads) {
			e.sweepStart(s, l);
		}
		for (Merger m : mergers) {
			m.sweepStart(s, l);
		}
		
	}
	
	public void sweepEnd(Stroke s, SweepEventListener l) {
		
		for (Vertex v : getAllVertices()) {
			v.sweepEnd(s, l);
		}
		for (Road e : roads) {
			e.sweepEnd(s, l);
		}
		for (Merger m : mergers) {
			m.sweepEnd(s, l);
		}
		
	}
	
	public void sweep(Stroke s, int index, SweepEventListener l) {
		for (Vertex v : getAllVertices()) {
			v.sweep(s, index, l);
		}
		for (Road e : roads) {
			e.sweep(s, index, l);
		}
		for (Merger m : mergers) {
			m.sweep(s, index, l);
		}
	}
	
	public void insertMergerTop(Point p) {
		
		
		
		Merger m = new Merger(p);
		
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
	 * dec is a set of bit flags indicating what decorations to add to the new road
	 * bit 0 set = add start stop sign
	 * bit 1 set = add end stop sign
	 */
	private Road createRoad(Vertex start, Vertex end, List<Point> pts, int dec) {
		
		assert pts.size() >= 2;
		
		Road e = new Road(start, end, pts);
		
		roads.add(e);
		
		refreshRoadIDs();
		
		if (!e.isStandAlone()) {
			
			start.roads.add(e);
			end.roads.add(e);
			
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
	
	private void destroyRoad(Road e) {
		assert roads.contains(e);
		
		if (!e.isStandAlone()) {
			
			e.start.roads.remove(e);
			e.end.roads.remove(e);
			
			destroyStopSign(e.startSign);
			destroyStopSign(e.endSign);
			
		}
		
		roads.remove(e);
		
		refreshRoadIDs();
		
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
			assert false : "Vertex was not in lists";
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
		for (Road e : roads) {
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
		
		List<Edge> edges = new ArrayList<Edge>();
		edges.addAll(start.roads);
		if (start.m != null) {
			edges.add(start.m);
		}
		
		for (Road e : start.roads) {
			if (prev != null && prev == e) {
				edges.remove(e);
			} else {
				
				Vertex other;
				if (start == e.start) {
					other = e.end;
				} else {
					other = e.start;
				}
				
				if (isDeadEnd(e, other, end)) {
					edges.remove(e);
				}
			}
		}
		if (start.m != null) {
			if (prev != null && prev == start.m) {
				edges.remove(start.m);
			} else {
				
				Vertex other;
				if (start == start.m.top) {
					other = start.m.bottom;
				} else if (start == start.m.left) {
					other = start.m.right;
				} else if (start == start.m.right) {
					other = start.m.left;
				} else {
					assert start == start.m.bottom;
					other = start.m.top;
				}
				
				if (isDeadEnd(start.m, other, end)) {
					edges.remove(start.m);
				}
			}
		}
		
		int n = edges.size();
		
		Vertex v;
		Edge e;
		
		int r = MODEL.world.RANDOM.nextInt(n);
		
		e = edges.get(r);
		
		if (e instanceof Road) {
			if (start == ((Road)e).start) {
				v = ((Road)e).end;
			} else {
				v = ((Road)e).start;
			}
		} else {
			if (start == ((Merger)e).top) {
				v = ((Merger)e).bottom;
			} else if (start == ((Merger)e).left) {
				v = ((Merger)e).right;
			} else if (start == ((Merger)e).right) {
				v = ((Merger)e).left;
			} else {
				assert start == ((Merger)e).bottom;
				v = ((Merger)e).top;
			}
		}
		
		return v;
	}
	
	public double distanceBetweenVertices(Vertex a, Vertex b) {
		return distances[a.id][b.id];
	}
	
	/**
	 * coming down road e, is vertex v a dead end?
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
		if ((v.roads.size() + ((v.m!=null)?1:0)) == 1) {
			return true;
		} else {
			List<Edge> eds = new ArrayList<Edge>();
			eds.addAll(v.roads);
			if (v.m != null) {
				eds.add(v.m);
			}
			eds.remove(e);
			for (Edge ee : eds) {
				Vertex other;
				if (ee instanceof Road) {
					Road r = (Road)ee;
					if (v == r.start) {
						other = r.end;
					} else {
						other = r.start;
					}
				} else {
					Merger m = (Merger)ee;
					if (v == m.top) {
						other = m.bottom;
					} else if (v == m.left) {
						other = m.right;
					} else if (v == m.right) {
						other = m.left;
					} else {
						other = m.top;
					}
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
	 * tests any part of vertex and any part of road
	 */
	public Entity graphHitTest(Point p) {
		assert p != null;
		for (Vertex v : getAllVertices()) {
			if (v.hitTest(p)) {
				return v;
			}
		}
		for (Road e : roads) {
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
	
	public StopSign signHitTest(Point p) {
		assert p != null;
		for (StopSign s : signs) {
			if (s.hitTest(p)) {
				return s;
			}
		}
		return null;
	}
	
	public Entity graphBestHitTest(Point p, double r) {
		assert p != null;
		for (Vertex v : getAllVertices()) {
			if (v.bestHitTest(p, r)) {
				return v;
			}
		}
		for (Road e : roads) {
			if (e.bestHitTest(p, r)) {
				return e;
			}
		}
		for (Merger m : mergers) {
			if (m.bestHitTest(p, r)) {
				return m;
			}
		}
		return null;
	}
	
	public StopSign signBestHitTest(Point p, double r) {
		assert p != null;
		for (StopSign s : signs) {
			if (s.bestHitTest(p, r)) {
				return s;
			}
		}
		return null;
	}
	
//	public RoadPosition roadSkeletonHitTest(Point p) {
//		assert p != null;
//		for (Road r : roads) {
//			RoadPosition pos = r.skeletonHitTest(p);
//			if (pos != null) {
//				return pos;
//			}
//		}
//		return null;
//	}
	
	/**
	 * returns the closest road within radius
	 */
	public RoadPosition findClosestRoadPosition(Point a, double radius) {

		RoadPosition closest = null;

		for (Road e : roads) {
			RoadPosition ep = e.findClosestRoadPosition(a, radius);
			if (ep != null) {
				if (closest == null || Point.distance(a, ep.p) < Point.distance(a, closest.p)) {
					closest = ep;
				}
			}
		}

		return closest;
	}
	
	
	
	
	
	/**
	 * split a road at point
	 * Road e will have been removed
	 * return Intersection at split point
	 */
	public Intersection split(RoadPosition pos) {
		
//		EdgePosition pos = (EdgePosition)skeletonHitTest(p);
		
		Road e = pos.r;
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
			
			createRoad(v, v, pts, 3);
			
			destroyRoad(e);
			
			return v;
		}
			
		List<Point> f1Pts = new ArrayList<Point>();
		
		for (int i = 0; i <= index; i++) {
			f1Pts.add(e.get(i));
		}
		f1Pts.add(p);
		
		createRoad(eStart, v, f1Pts, (e.startSign!=null?1:0)+2);
		
		List<Point> f2Pts = new ArrayList<Point>();
		
		f2Pts.add(p);
		for (int i = index+1; i < e.size(); i++) {
			f2Pts.add(e.get(i));
		}
		
		createRoad(v, eEnd, f2Pts, 1+(e.endSign!=null?2:0));
		
		destroyRoad(e);
		
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
	
	private void refreshRoadIDs() {
		int id = 0;
		for (Road e : roads) {
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
			distances[i][i] = 0.0;
		}
		
		/*
		 * iterate and find shorter distances via roads and mergers
		 */
		for(Road e : roads) {
			double l = e.getTotalLength();
			double cur = distances[e.start.id][e.end.id];
			/*
			 * there may be multiple roads between start and end, so don't just blindly set it to l
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
	 * merge two roads (possibly the same road) at the given intersection
	 * 
	 * no colinear points in returned road
	 */
	private void merge(Vertex v) {
		
		assert v.roads.size() == 2;
		Road e1 = v.roads.get(0);
		Road e2 = v.roads.get(1);
		
		assert roads.contains(e1);
		assert roads.contains(e2);
		
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
			assert v.roads.size() == 2;
				
			List<Point> pts = new ArrayList<Point>();
			
			assert e1.get(0) == e1.get(e1.size()-1);
			
			for (int i = 0; i < e1.size(); i++) {
				pts.add(e1.get(i));
			}
			
			createRoad(null, null, pts, 0);
			
			destroyRoad(e1);
			
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
			
			createRoad(e1End, e2End, pts, (e1.endSign!=null?1:0) + (e2.endSign!=null?2:0));
			
			destroyRoad(e1);
			destroyRoad(e2);
			
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
			
			createRoad(e1End, e2Start, pts, (e1.endSign!=null?1:0) + (e2.startSign!=null?2:0)); 
			
			destroyRoad(e1);
			destroyRoad(e2);
			
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
			
			createRoad(e1Start, e2End, pts, (e1.startSign!=null?1:0) + (e2.endSign!=null?2:0));
			
			destroyRoad(e1);
			destroyRoad(e2);
			
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
			
			createRoad(e1Start, e2Start, pts, (e1.startSign!=null?1:0) + (e2.startSign!=null?2:0));
			
			destroyRoad(e1);
			destroyRoad(e2);
			
			destroyVertex(v);
			
		}
	}
	
	
	
	public void renderBackground(Graphics2D g2) {
		
		List<Road> roadsCopy;
		List<Vertex> verticesCopy;
		List<Merger> mergersCopy;
		List<StopSign> signsCopy;
		synchronized (MODEL) {
			roadsCopy = new ArrayList<Road>(roads);
			verticesCopy = new ArrayList<Vertex>(getAllVertices());
			mergersCopy = new ArrayList<Merger>(mergers);
			signsCopy = new ArrayList<StopSign>(signs);
		}
		
		AffineTransform origTransform = g2.getTransform();
		AffineTransform trans = (AffineTransform)origTransform.clone();
		trans.scale(MODEL.PIXELS_PER_METER, MODEL.PIXELS_PER_METER);
		g2.setTransform(trans);
		
		for (Road e : roadsCopy) {
			e.paint(g2);
		}
		
		for (Merger m : mergersCopy) {
			m.paint(g2);
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
		g2.drawString("road count: " + roads.size(), (int)p.x, (int)p.y);
		
		p = new Point(1, 3).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("sign count: " + signs.size(), (int)p.x, (int)p.y);
		
	}
	
	public void paintScene(Graphics2D g2) {
		
		if (MODEL.DEBUG_DRAW) {
			List<Road> roadsCopy;
			
			synchronized (MODEL) {
				roadsCopy = new ArrayList<Road>(roads);
			}
			
			for (Road e : roadsCopy) {
//				e.paintSkeleton(g2);
				e.paintBorders(g2);
			}
		}
		
	}
	
	public void paintIDs(Graphics2D g2) {
		
		List<Vertex> verticesCopy;
		synchronized (MODEL) {
			verticesCopy = new ArrayList<Vertex>(getAllVertices());
		}
		
		for (Vertex v : verticesCopy) {
			v.paintID(g2);
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
		
		for (Road e : roads) {
			if (e.start == null && e.end == null) {
				continue;
			}
			for (Road f : roads) {
				if (e == f) {
					
					for (int i = 0; i < e.size()-2; i++) {
						Capsule es = e.getCapsule(i);
						
						for (int j = i+1; j < f.size()-1; j++) {
							Capsule fs = f.getCapsule(j);
							
							try {
								Point inter = Point.intersection(es.a, es.b, fs.a, fs.b);
								if (inter != null && !(inter.equals(es.a) || inter.equals(es.b) || inter.equals(fs.a) || inter.equals(fs.b))) {
									//assert false : "No edges should intersect";
									throw new IllegalStateException("No roads should intersect");
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
									throw new IllegalStateException("No roads should intersect");
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
