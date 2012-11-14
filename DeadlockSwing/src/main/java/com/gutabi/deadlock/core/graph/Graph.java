package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.OverlappingException;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.Rect;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.Sweepable;
import com.gutabi.deadlock.core.geom.Sweeper;
import com.gutabi.deadlock.model.Cursor;

@SuppressWarnings("static-access")
public class Graph implements Sweepable {
	
	public final List<Vertex> vertices = new ArrayList<Vertex>();
	public final List<Edge> edges = new ArrayList<Edge>();
	
	private Rect aabb;
	
//	private static final Logger logger = Logger.getLogger(Graph.class);
	
	public Graph() {
		
	}
	
	public void preStart() {
		
		initializeMatrices();
		
		for (Vertex v : vertices) {
			v.preStart();
		}
	}
	
	public void postStop() {
		
		for (Vertex v : vertices) {
			v.postStop();
		}
		
	}
	
	public void preStep(double t) {
		
		for (Vertex v : vertices) {
			v.preStep(t);
		}
		
	}
	
	public void computeVertexRadii() {
		
		for (int i = 0; i < vertices.size(); i++) {
			Vertex vi = vertices.get(i);
			double maximumRadius = Double.POSITIVE_INFINITY;
			for (int j = 0; j < vertices.size(); j++) {
				Vertex vj = vertices.get(j);
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
	
	public Rect getAABB() {
		return aabb;
	}
	
	public void addVertexTop(Vertex v) {
		addVertex(v);
		computeAABB();
	}
	
	public void createRoadTop(Vertex start, Vertex end, List<Point> pts) {
		
		createRoad(start, end, pts, (start.m==null&&!start.roads.isEmpty()?1:0)+(end.m==null&&!end.roads.isEmpty()?2:0));
		
		automaticMergeOrDestroy(start);
		automaticMergeOrDestroy(end);
		
		computeAABB();
	}
	
	public void insertMergerTop(Point p) {
		createMerger(p);
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
			
			if (e.start != null) {
				affectedVertices.add(e.start);
			}
			if (e.end != null) {
				affectedVertices.add(e.end);
			}
			
			destroyRoad(e);
		}
		
		destroyVertex(v);
		affectedVertices.remove(v);
		
		for (Vertex a : affectedVertices) {
			automaticMergeOrDestroy(a);
		}
		
		computeAABB();
		
	}
	
	public void removeRoadTop(Road e) {
		
		Set<Vertex> affectedVertices = new HashSet<Vertex>();
		if (e.start != null) {
			affectedVertices.add(e.start);
		}
		if (e.end != null) {
			affectedVertices.add(e.end);
		}
		
		destroyRoad(e);
		
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
		
		computeAABB();
	}
	
	private void automaticMergeOrDestroy(Vertex v) {
		
		if (!v.isDeleteable()) {
			return;
		}
			
		forceMergeOrDestroy(v);
		
	}
	
	private void forceMergeOrDestroy(Vertex v) {
		
		if (v.roads.size() == 0) {
			destroyVertex(v);
		} else if (v.roads.size() == 2) {
			merge(v);
		}
		
	}
	
	private void addVertex(Vertex v) {
		vertices.add(v);
		refreshVertexIDs();
	}
	
	/**
	 * dec is a set of bit flags indicating what decorations to add to the new road
	 * bit 0 set = add start stop sign
	 * bit 1 set = add end stop sign
	 */
	private Road createRoad(Vertex start, Vertex end, List<Point> pts, int dec) {
		assert pts.size() >= 2;
		Road e = new Road(start, end, pts, dec);
		edges.add(e);
		refreshEdgeIDs();
		
		return e;
	}
	
	private void createMerger(Point p) {
		Merger m = new Merger(p);
		
		edges.add(m);
		refreshEdgeIDs();
		
		vertices.add(m.top);
		vertices.add(m.left);
		
		vertices.add(m.right);
		vertices.add(m.bottom);
		
		refreshVertexIDs();
	}
	
	private void destroyVertex(Vertex v) {
		vertices.remove(v);
		refreshVertexIDs();
	}
	
	private void destroyRoad(Road r) {
		assert edges.contains(r);
		r.destroy();
		edges.remove(r);
		refreshEdgeIDs();
	}
	
	private void destroyMerger(Merger m) {
		assert edges.contains(m);
		m.destroy();
		edges.remove(m);
		refreshEdgeIDs();
	}
	
	
	
	public void sweepStart(Sweeper s) {
		
		for (Vertex v : vertices) {
			v.sweepStart(s);
		}
		for (Edge e : edges) {
			e.sweepStart(s);
		}
		
	}
	
	public void sweep(Sweeper s, int index) {
		for (Vertex v : vertices) {
			v.sweep(s, index);
		}
		for (Edge e : edges) {
			e.sweep(s, index);
		}
	}
	
	
	
	
	
	private void computeAABB() {
		
		aabb = null;
		
		for (Vertex v : vertices) {
			aabb = Rect.union(aabb, v.getAABB());
		}
		for (Edge e : edges) {
			aabb = Rect.union(aabb, e.getAABB());
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
		Entity hit;
		for (Edge e : edges) {
			hit = e.decorationsHitTest(p);
			if (hit != null) {
				return hit;
			}
		}
		for (Vertex v : vertices) {
			hit = v.hitTest(p);
			if (hit != null) {
				return hit;
			}
		}
		for (Edge e : edges) {
			hit = e.hitTest(p);
			if (hit != null) {
				return e;
			}
		}
		return null;
	}
	
	public Entity graphBestHitTest(Shape s) {
		Entity hit;
		for (Edge ed : edges) {
			hit = ed.decorationsBestHitTest(s);
			if (hit != null) {
				return hit;
			}
		}
		for (Vertex v : vertices) {
			hit = v.bestHitTest(s);
			if (hit != null) {
				return hit;
			}
		}
		for (Edge ed : edges) {
			hit = ed.bestHitTest(s);
			if (hit != null) {
				return ed;
			}
		}
		return null;
	}
	
	public Entity pureGraphBestHitTest(Shape s) {
//		assert p != null;
		for (Vertex v : vertices) {
			if (v.bestHitTest(s) != null) {
				return v;
			}
		}
		for (Edge ed : edges) {
			if (ed.bestHitTest(s) != null) {
				return ed;
			}
		}
		return null;
	}
	
	/**
	 * returns the closest road within radius
	 */
	public RoadPosition findClosestRoadPosition(Point p, double radius) {

		RoadPosition closest = null;
		
		List<Road> roads = new ArrayList<Road>();
		for (Edge e : edges) {
			if (e instanceof Road) {
				roads.add((Road)e);
			}
		}
		
		for (Road r : roads) {
			RoadPosition ep = r.findClosestRoadPosition(p, radius);
			if (ep != null) {
				if (closest == null || p.distanceTo(ep.p) < p.distanceTo(closest.p)) {
					closest = ep;
				}
			}
		}

		return closest;
	}
	
	public boolean cursorIntersect(Cursor c) {
		for (Vertex v : vertices) {
			if (c.intersect(v.shape)) {
				return true;
			}
		}
		for (Edge ed : edges) {
			if (c.intersect(ed.shape)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	/**
	 * split a road at point
	 * Road e will have been removed
	 * return Intersection at split point
	 */
	public Intersection split(RoadPosition pos) {
		
		Road r = pos.r;
		int index = pos.index;
		double param = pos.param;
		final Point p = pos.p;
		
		assert param >= 0.0;
		assert param < 1.0;
		
		Intersection v = new Intersection(p);
		addVertex(v);
		
		Vertex eStart = r.start;
		Vertex eEnd = r.end;
		
		if (eStart == null && eEnd == null) {
			// stand-alone loop
			
			List<Point> pts = new ArrayList<Point>();
			
			pts.add(p);
			for (int i = index+1; i < r.pointCount(); i++) {
				pts.add(r.getPoint(i));
			}
			for (int i = 0; i <= index; i++) {
				pts.add(r.getPoint(i));
			}
			pts.add(p);
			
			createRoad(v, v, pts, 3);
			
			destroyRoad(r);
			
			return v;
		}
			
		List<Point> f1Pts = new ArrayList<Point>();
		
		for (int i = 0; i <= index; i++) {
			f1Pts.add(r.getPoint(i));
		}
		f1Pts.add(p);
		
		createRoad(eStart, v, f1Pts, (r.startSign!=null?1:0)+2);
		
		List<Point> f2Pts = new ArrayList<Point>();
		
		f2Pts.add(p);
		for (int i = index+1; i < r.pointCount(); i++) {
			f2Pts.add(r.getPoint(i));
		}
		
		createRoad(v, eEnd, f2Pts, 1+(r.endSign!=null?2:0));
		
		destroyRoad(r);
		
		return v;
	}
	
	
	
	
	Vertex[] vertexIDs;
	
	private void refreshVertexIDs() {
		int vertexCount = vertices.size();
		vertexIDs = new Vertex[vertexCount];
		int id = 0;
		for (Vertex v : vertices) {
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
		
		int vertexCount = vertices.size();
		
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
		for (Edge e : edges) {
			e.enterDistancesMatrix(distances);
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
			assert v.roads.size() == 2;
				
			List<Point> pts = new ArrayList<Point>();
			
			assert e1.getPoint(0) == e1.getPoint(e1.pointCount()-1);
			
			for (int i = 0; i < e1.pointCount(); i++) {
				pts.add(e1.getPoint(i));
			}
			
			createRoad(null, null, pts, 0);
			
			destroyRoad(e1);
			
			destroyVertex(v);
			
		} else if (v == e1Start && v == e2Start) {
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = e1.pointCount()-1; i >= 0; i--) {
				pts.add(e1.getPoint(i));
			}
			assert e1.getPoint(0).equals(e2.getPoint(0));
			for (int i = 0; i < e2.pointCount(); i++) {
				pts.add(e2.getPoint(i));
			}
			
			createRoad(e1End, e2End, pts, (e1.endSign!=null?1:0) + (e2.endSign!=null?2:0));
			
			destroyRoad(e1);
			destroyRoad(e2);
			
			destroyVertex(v);
			
		} else if (v == e1Start && v == e2End) {
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = e1.pointCount()-1; i >= 0; i--) {
				pts.add(e1.getPoint(i));
			}
			assert e1.getPoint(0).equals(e2.getPoint(e2.pointCount()-1));
			for (int i = e2.pointCount()-1; i >= 0; i--) {
				pts.add(e2.getPoint(i));
			}
			
			createRoad(e1End, e2Start, pts, (e1.endSign!=null?1:0) + (e2.startSign!=null?2:0)); 
			
			destroyRoad(e1);
			destroyRoad(e2);
			
			destroyVertex(v);
			
		} else if (v == e1End && v == e2Start) {
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = 0; i < e1.pointCount(); i++) {
				pts.add(e1.getPoint(i));
			}
			assert e1.getPoint(e1.pointCount()-1).equals(e2.getPoint(0));
			for (int i = 0; i < e2.pointCount(); i++) {
				pts.add(e2.getPoint(i));
			}
			
			createRoad(e1Start, e2End, pts, (e1.startSign!=null?1:0) + (e2.endSign!=null?2:0));
			
			destroyRoad(e1);
			destroyRoad(e2);
			
			destroyVertex(v);
			
		} else {
			assert v == e1End && v == e2End;
			
			List<Point> pts = new ArrayList<Point>();
			
			for (int i = 0; i < e1.pointCount(); i++) {
				pts.add(e1.getPoint(i));
			}
			for (int i = e2.pointCount()-1; i >= 0; i--) {
				pts.add(e2.getPoint(i));
			}
			
			createRoad(e1Start, e2Start, pts, (e1.startSign!=null?1:0) + (e2.startSign!=null?2:0));
			
			destroyRoad(e1);
			destroyRoad(e2);
			
			destroyVertex(v);
			
		}
	}
	
	
	
	public void renderBackground(Graphics2D g2) {
		
		List<Edge> edgesCopy;
		List<Vertex> verticesCopy;
		synchronized (MODEL) {
			edgesCopy = new ArrayList<Edge>(edges);
			verticesCopy = new ArrayList<Vertex>(vertices);
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
		
		for (Edge e : edgesCopy) {
			e.paintDecorations(g2);
		}
		
		g2.setTransform(origTransform);
		
	}
	
	public void paintStats(Graphics2D g2) {
		
		Point p = new Point(1, 1).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("vertex count: " + vertices.size(), (int)p.x, (int)p.y);
		
		p = new Point(1, 2).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("edge count: " + edges.size(), (int)p.x, (int)p.y);
		
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
		
		List<Vertex> verticesCopy;
		synchronized (MODEL) {
			verticesCopy = new ArrayList<Vertex>(vertices);
		}
		
		for (Vertex v : verticesCopy) {
			v.paintID(g2);
		}
		
	}

	
	
	
	
	
	public boolean checkConsistency() {
		
		for (Vertex v : vertices) {
			for (Vertex w : vertices) {
				if (v == w) {
					
				} else {
					double distance = Point.distance(v.p, w.p);
					assert DMath.greaterThan(distance, v.r + w.r);
				}
			}
		}
		
		List<Road> roads = new ArrayList<Road>();
		for (Edge e : edges) {
			if (e instanceof Road) {
				roads.add((Road)e);
			}
		}
		
		for (Road e : roads) {
			if (e.start == null && e.end == null) {
				continue;
			}
			for (Road f : roads) {
				if (e == f) {
					
					for (int i = 0; i < e.pointCount()-2; i++) {
						Capsule es = e.getCapsule(i);
						
						for (int j = i+1; j < f.pointCount()-1; j++) {
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
					
					for (int i = 0; i < e.pointCount()-1; i++) {
						Capsule es = e.getCapsule(i);
						
						for (int j = 0; j < f.pointCount()-1; j++) {
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
						for (int i = 0; i < e.pointCount(); i++) {
							Point eP = e.getPoint(i);
							for (int j = 0; j < f.pointCount(); j++) {
								Point fP = f.getPoint(j);
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
