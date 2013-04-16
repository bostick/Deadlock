package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Capsule;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.OverlappingException;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;

public class Graph {
	
	World world;
	
	public final List<Vertex> vertices = new ArrayList<Vertex>();
	public final List<Road> roads = new ArrayList<Road>();
	public final List<Merger> mergers = new ArrayList<Merger>();
	public final List<BypassBoard> boards = new ArrayList<BypassBoard>();
	
	public GraphPositionPathFactory pathFactory;
	
	public final List<GraphPositionPath> paths = new ArrayList<GraphPositionPath>();
	
	private AABB aabb;
	
	public Graph(World world) {
		this.world = world;
		
		pathFactory = new GraphPositionPathFactory(this);
		
	}
	
	public void preStart() {
		
		initializeMatrices();
		
		for (Vertex v : vertices) {
			v.preStart();
			if (v instanceof Fixture) {
				Fixture f = (Fixture)v;
				if (f.shortestPathToMatch != null) {
					paths.add(f.shortestPathToMatch);
				}
			}
		}
		
		for (int i = 0; i < paths.size()-1; i++) {
			GraphPositionPath ipath = paths.get(i);
			for (int j = i+1; j < paths.size(); j++) {
				GraphPositionPath jpath = paths.get(j);
				
				Set<Edge> sharedEdges = new HashSet<Edge>();
				for (Edge e : ipath.edgesMap.keySet()) {
					for (Edge f : jpath.edgesMap.keySet()) {
						if (e == f && ipath.axesMap.get(e) == jpath.axesMap.get(f)) {
							sharedEdges.add(e);
						}
					}
				}
				
				if (!sharedEdges.isEmpty()) {
					ipath.sharedEdgesMap.put(jpath, sharedEdges);
					jpath.sharedEdgesMap.put(ipath, sharedEdges);
				}
				
			}
		}
	}
	
	public void postStop() {
		
		for (Vertex v : vertices) {
			v.postStop();
		}
		
		paths.clear();
	}
	
	public void preStep(double t) {
		
		for (int i = 0; i < vertices.size(); i++) {
			Vertex v = vertices.get(i);
			v.preStep(t);
		}
	}
	
	public void postStep(double t) {
		
		for (int i = 0; i < vertices.size(); i++) {
			Vertex v = vertices.get(i);
			v.postStep(t);
		}
	}
	
	public void computeVertexRadii(Set<Vertex> affected) {
		
		for (Vertex v : affected) {
			double maximumRadiusForV = Double.POSITIVE_INFINITY;
			for (int j = 0; j < vertices.size(); j++) {
				Vertex vj = vertices.get(j);
				if (v == vj) {
					continue;
				}
				double max = Point.distance(v.p, vj.p) - vj.getRadius();
				if (max < maximumRadiusForV) {
					maximumRadiusForV = max;
				}
			}
			v.computeRadius(maximumRadiusForV);
		}
		
		computeAABB();
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	public Set<Vertex> addVertexTop(Vertex v) {
		
		addVertex(v);
		
		Set<Vertex> affected = new HashSet<Vertex>();
		affected.add(v);
		
		computeAABB();
		
		return affected;
	}
	
	public Set<Vertex> createRoadTop(Road r) {
		
		createRoad(r, (r.start.m==null&&r.start.supportsStopSigns()?1:0)+(r.end.m==null&&r.end.supportsStopSigns()?2:0));
		
		Set<Vertex> affected = new HashSet<Vertex>();
		
		boolean persist;
		persist = automaticMergeOrDestroy(r.start);
		if (persist) {
			affected.add(r.start);
		}
		
		persist = automaticMergeOrDestroy(r.end);
		if (persist) {
			affected.add(r.end);
		}
		
		computeAABB();
		
		return affected;
	}
	
	public Set<Vertex> insertMergerTop(Merger m) {
		
		mergers.add(m);
		refreshMergerIDs();
		
		vertices.add(m.top);
		vertices.add(m.left);
		
		vertices.add(m.right);
		vertices.add(m.bottom);
		
		refreshVertexIDs();
		
		Set<Vertex> affected = new HashSet<Vertex>();
		affected.add(m.top);
		affected.add(m.left);
		affected.add(m.right);
		affected.add(m.bottom);
		
		computeAABB();
		
		return affected;
	}
	
	public void insertBypassBoardTop(BypassBoard b) {
		
		boards.add(b);
		
	}	
	
	
	public Set<Vertex> removeVertexTop(Vertex v) {
		
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
		
		Set<Vertex> affected = new HashSet<Vertex>();
		
		for (Vertex a : affectedVertices) {
			boolean persist = automaticMergeOrDestroy(a);
			if (persist) {
				affected.add(a);
			}
		}
		
		computeAABB();
		
		return affected;
	}
	
	public Set<Vertex> removeRoadTop(Road e) {
		
		Set<Vertex> affectedVertices = new HashSet<Vertex>();
		if (e.start != null) {
			affectedVertices.add(e.start);
		}
		if (e.end != null) {
			affectedVertices.add(e.end);
		}
		
		destroyRoad(e);
		
		Set<Vertex> affected = new HashSet<Vertex>();
		
		for (Vertex a : affectedVertices) {
			boolean persist = automaticMergeOrDestroy(a);
			if (persist) {
				affected.add(a);
			}
		}
		
		computeAABB();
		
		return affected;
	}
	
	public Set<Vertex> removeMergerTop(Merger m) {
		
		Vertex top = m.top;
		Vertex left = m.left;
		Vertex right = m.right;
		Vertex bottom = m.bottom;
		
		Set<Vertex> affected = new HashSet<Vertex>();
		
		affected.addAll(removeVertexTop(top));
		affected.addAll(removeVertexTop(left));
		affected.addAll(removeVertexTop(right));
		affected.addAll(removeVertexTop(bottom));
		
		destroyMerger(m);
		
		forceMergeOrDestroy(top);
		forceMergeOrDestroy(left);
		forceMergeOrDestroy(right);
		forceMergeOrDestroy(bottom);
		
		computeAABB();
		
		return affected;
	}
	
	private boolean automaticMergeOrDestroy(Vertex v) {
		
		if (!v.isUserDeleteable()) {
			return true;
		}
		
		boolean persist = forceMergeOrDestroy(v);
		
		return persist;
	}
	
	private boolean forceMergeOrDestroy(Vertex v) {
		
		if (v.roads.size() == 0) {
			destroyVertex(v);
			return false;
		} else if (v.roads.size() == 2) {
			merge(v);
			return false;
		}
		
		return true;
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
	private void createRoad(Road r, int dec) {
		
		if ((dec & 1) == 1) {
			r.startSign.setEnabled(true);
		}
		
		if ((dec & 2) == 2) {
			r.endSign.setEnabled(true);
		}
		
		if ((dec & 4) == 4) {
			
			if ((dec & 8) == 0) {
				r.setDirection(null, Direction.STARTTOEND);
			} else {
				r.setDirection(null, Direction.ENDTOSTART);
			}
			
		}
		
		roads.add(r);
		refreshRoadIDs();
		
	}
	
	private void destroyVertex(Vertex v) {
		vertices.remove(v);
		refreshVertexIDs();
	}
	
	private void destroyRoad(Road r) {
		assert roads.contains(r);
		r.destroy();
		roads.remove(r);
		refreshRoadIDs();
	}
	
	private void destroyMerger(Merger m) {
		assert mergers.contains(m);
		m.destroy();
		mergers.remove(m);
		refreshMergerIDs();
	}
	
	private void computeAABB() {
		
		aabb = null;
		
		for (Vertex v : vertices) {
			aabb = AABB.union(aabb, v.getShape().getAABB());
		}
		for (Road r : roads) {
			aabb = AABB.union(aabb, r.getShape().getAABB());
		}
		for (Merger m : mergers) {
			aabb = AABB.union(aabb, m.getShape().getAABB());
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
	
	/**
	 * returns the a random next choice to make
	 */
	public Vertex randomPathChoice(Edge prev, Vertex start, Vertex end) {
		
		List<Edge> edges = new ArrayList<Edge>();
		edges.addAll(start.roads);
		if (start.m != null) {
			edges.add(start.m);
		}
		
		for (Road e : start.roads) {
			if (prev != null && prev == e) {
				edges.remove(e);
				continue;
			}
			
			Vertex other;
			if (start == e.start) {
				other = e.end;
				if (isDeadEnd(e, other, end)) {
					edges.remove(e);
					continue;
				}
				if (e.getDirection(null) == Direction.ENDTOSTART) {
					edges.remove(e);
					continue;
				}
				if (distances[other.id][end.id] == Double.POSITIVE_INFINITY) {
					edges.remove(e);
					continue;
				}
				
			} else {
				other = e.start;
				if (isDeadEnd(e, other, end)) {
					edges.remove(e);
					continue;
				}
				if (e.getDirection(null) == Direction.STARTTOEND) {
					edges.remove(e);
					continue;
				}
				if (distances[other.id][end.id] == Double.POSITIVE_INFINITY) {
					edges.remove(e);
					continue;
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
					if (isDeadEnd(start.m, other, end)) {
						edges.remove(start.m);
					} else if (start.m.getDirection(Axis.TOPBOTTOM) == Direction.ENDTOSTART) {
						edges.remove(start.m);
					} else if (distances[other.id][end.id] == Double.POSITIVE_INFINITY) {
						edges.remove(start.m);
					}
				} else if (start == start.m.left) {
					other = start.m.right;
					if (isDeadEnd(start.m, other, end)) {
						edges.remove(start.m);
					} else if (start.m.getDirection(Axis.LEFTRIGHT) == Direction.ENDTOSTART) {
						edges.remove(start.m);
					} else if (distances[other.id][end.id] == Double.POSITIVE_INFINITY) {
						edges.remove(start.m);
					}
				} else if (start == start.m.right) {
					other = start.m.left;
					if (isDeadEnd(start.m, other, end)) {
						edges.remove(start.m);
					} else if (start.m.getDirection(Axis.LEFTRIGHT) == Direction.STARTTOEND) {
						edges.remove(start.m);
					} else if (distances[other.id][end.id] == Double.POSITIVE_INFINITY) {
						edges.remove(start.m);
					}
				} else {
					assert start == start.m.bottom;
					other = start.m.top;
					if (isDeadEnd(start.m, other, end)) {
						edges.remove(start.m);
					} else if (start.m.getDirection(Axis.LEFTRIGHT) == Direction.STARTTOEND) {
						edges.remove(start.m);
					} else if (distances[other.id][end.id] == Double.POSITIVE_INFINITY) {
						edges.remove(start.m);
					}
				}
			}
		}
		
		int n = edges.size();
		
		Vertex v;
		Edge e;
		
		int r = APP.RANDOM.nextInt(n);
		
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
			/*
			 * reached end!
			 */
			return false;
		}
		if ((v.roads.size() + ((v.m!=null)?1:0)) == 1) {
			/*
			 * literally only one edge
			 */
			return true;
		} else {
			List<Edge> eds = v.getEdges();
			for (Edge ee : eds) {
				if (ee == e) {
					continue;
				}
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
				if (ee.canTravelFromTo(v, other)) {
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
		for (Road r : roads) {
			hit = r.decorationsHitTest(p);
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
		for (Road r : roads) {
			hit = r.hitTest(p);
			if (hit != null) {
				return r;
			}
		}
		for (Merger m : mergers) {
			hit = m.hitTest(p);
			if (hit != null) {
				return m;
			}
		}
		return null;
	}
	
	public Entity pureGraphIntersect(Shape s) {
		for (Vertex v : vertices) {
			if (ShapeUtils.intersect(v.getShape(), s)) {
				return v;
			}
		}
		for (Road r : roads) {
			if (ShapeUtils.intersect(r.getShape(), s)) {
				return r;
			}
		}
		for (Merger m : mergers) {
			if (ShapeUtils.intersect(m.getShape(), s)) {
				return m;
			}
		}
		return null;
	}
	
	public Entity pureGraphIntersectOBB(OBB q) {
		for (Vertex v : vertices) {
			if (ShapeUtils.intersectCO(v.getShape(), q)) {
				return v;
			}
		}
		for (Road r : roads) {
			if (r.getShape().intersect(q)) {
				return r;
			}
		}
		for (Merger m : mergers) {
			if (ShapeUtils.intersectAO(m.getShape(), q)) {
				return m;
			}
		}
		return null;
	}
	
	public Entity pureGraphIntersectCircle(Circle c) {
		for (Vertex v : vertices) {
			if (ShapeUtils.intersectCC(v.getShape(), c)) {
				return v;
			}
		}
		for (Road r : roads) {
			if (r.getShape().intersect(c)) {
				return r;
			}
		}
		for (Merger m : mergers) {
			if (ShapeUtils.intersectAC(m.getShape(), c)) {
				return m;
			}
		}
		return null;
	}
	
	public Entity pureGraphIntersectCapsule(Capsule c) {
		for (Vertex v : vertices) {
			if (ShapeUtils.intersect(c, v.getShape())) {
				return v;
			}
		}
		for (Road r : roads) {
			if (ShapeUtils.intersect(c, r.getShape())) {
				return r;
			}
		}
		for (Merger m : mergers) {
			if (ShapeUtils.intersect(c, m.getShape())) {
				return m;
			}
		}
		return null;
	}
	
	/**
	 * returns the closest road within radius
	 */
	public RoadPosition findClosestRoadPosition(Point p, double radius) {

		RoadPosition closest = null;
		
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
		
		Intersection v = new Intersection(world, p);
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
			
			/*
			 * 3 so that both stop signs are introduced
			 */
			createRoad(new Road(world, v, v, pts), 3);
			
			destroyRoad(r);
			
			return v;
		}
			
		List<Point> f1Pts = new ArrayList<Point>();
		
		for (int i = 0; i <= index; i++) {
			f1Pts.add(r.getPoint(i));
		}
		f1Pts.add(p);
		
		int directionMask = 0;
		Direction dir = r.getDirection(null);
		if (dir != null) {
			switch (dir) {
			case STARTTOEND:
				directionMask = 4;
				break;
			case ENDTOSTART:
				directionMask = 4 + 8;
				break;
			}
		} else {
			directionMask = 0;
		}
		
		createRoad(new Road(world, eStart, v, f1Pts), (r.startSign.isEnabled()?1:0)+2+directionMask);
		
		List<Point> f2Pts = new ArrayList<Point>();
		
		f2Pts.add(p);
		for (int i = index+1; i < r.pointCount(); i++) {
			f2Pts.add(r.getPoint(i));
		}
		
		createRoad(new Road(world, v, eEnd, f2Pts), 1+(r.endSign.isEnabled()?2:0)+directionMask);
		
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
	
	private void refreshRoadIDs() {
		int id = 0;
		for (Road r : roads) {
			r.id = id;
			id++;
		}
	}
	
	private void refreshMergerIDs() {
		int id = 0;
		for (Merger m : mergers) {
			m.id = id;
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
		for (Road r : roads) {
			r.enterDistancesMatrix(distances);
		}
		for (Merger m : mergers) {
			m.enterDistancesMatrix(distances);
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
			
			assert e1.getPoint(0) == e1.getPoint(e1.pointCount()-1);
			
			for (int i = 0; i < e1.pointCount(); i++) {
				pts.add(e1.getPoint(i));
			}
			
			createRoad(new Road(world, null, null, pts), 0);
			
			destroyRoad(e1);
			
			destroyVertex(v);
			
		} else {
			
			int directionMask = 0;
			if (e1.getDirection(null) == e2.getDirection(null)) {
				Direction dir = e1.getDirection(null);
				if (dir != null) {
					switch (dir) {
					case STARTTOEND:
						directionMask = 4;
						break;
					case ENDTOSTART:
						directionMask = 4 + 8;
						break;
					}
				} else {
					directionMask = 0;
				}
			}
			
			if (v == e1Start && v == e2Start) {
				
				List<Point> pts = new ArrayList<Point>();
				
				for (int i = e1.pointCount()-1; i >= 0; i--) {
					pts.add(e1.getPoint(i));
				}
				assert e1.getPoint(0).equals(e2.getPoint(0));
				for (int i = 0; i < e2.pointCount(); i++) {
					pts.add(e2.getPoint(i));
				}
				
				createRoad(new Road(world, e1End, e2End, pts), (e1.endSign.isEnabled()?1:0) + (e2.endSign.isEnabled()?2:0)+directionMask);
				
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
				
				createRoad(new Road(world, e1End, e2Start, pts), (e1.endSign.isEnabled()?1:0) + (e2.startSign.isEnabled()?2:0)+directionMask); 
				
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
				
				createRoad(new Road(world, e1Start, e2End, pts), (e1.startSign.isEnabled()?1:0) + (e2.endSign.isEnabled()?2:0)+directionMask);
				
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
				
				createRoad(new Road(world, e1Start, e2Start, pts), (e1.startSign.isEnabled()?1:0) + (e2.startSign.isEnabled()?2:0)+directionMask);
				
				destroyRoad(e1);
				destroyRoad(e2);
				
				destroyVertex(v);
				
			}
			
		}
	}
	
	public String toFileString() {
		StringBuilder s = new StringBuilder();
		
		s.append("start graph\n");
		
		s.append("vertices " + vertices.size() + "\n");
		s.append("roads " + roads.size() + "\n");
		s.append("mergers " + mergers.size() + "\n");
		
		for (Vertex v : vertices) {
			s.append(v.toFileString());
		}
		for (Road r : roads) {
			s.append(r.toFileString());
		}
		for (Merger m : mergers) {
			s.append(m.toFileString());
		}
		
		s.append("end graph\n");
		
		return s.toString();
	}
	
	public static Graph fromFileString(World world, String s) {
		BufferedReader r = new BufferedReader(new StringReader(s));
		
		Vertex[] vs = null;
		Road[] rs = null;
		Merger[] ms = null;
		
		try {
			String l = r.readLine();
			assert l.equals("start graph");
			
			l = r.readLine();
			Scanner sc = new Scanner(l);
			String tok = sc.next();
			assert tok.equals("vertices");
			int vCount = sc.nextInt();
			sc.close();
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("roads");
			int rCount = sc.nextInt();
			sc.close();
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("mergers");
			int mCount = sc.nextInt();
			sc.close();
			
			vs = new Vertex[vCount];
			rs = new Road[rCount];
			ms = new Merger[mCount];
			
			while (true) {
				l = r.readLine();
				
				StringBuilder builder;
				
				if (l.equals("end graph")) {
					break;
				} else if (l.equals("start fixture")) {
					
					builder = new StringBuilder();
					builder.append(l+"\n");
					while (true) {
						l = r.readLine();
						builder.append(l+"\n");
						if (l.equals("end fixture")) {
							break;
						}
					}
					
					Fixture f = Fixture.fromFileString(world, builder.toString());
					
					vs[f.id] = f;
					
				} else if (l.equals("start intersection")) {
					
					builder = new StringBuilder();
					builder.append(l+"\n");
					while (true) {
						l = r.readLine();
						builder.append(l+"\n");
						if (l.equals("end intersection")) {
							break;
						}
					}
					
					Intersection i = Intersection.fromFileString(world, builder.toString());
					
					vs[i.id] = i;
					
				} else if (l.equals("start road")) {
					
					builder = new StringBuilder();
					builder.append(l+"\n");
					while (true) {
						l = r.readLine();
						builder.append(l+"\n");
						if (l.equals("end road")) {
							break;
						}
					}
					
					Road rd = Road.fromFileString(world, vs, builder.toString());
					
					rs[rd.id] = rd;
					
				} else {
					assert false;
					break;
				}
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Vertex v : vs) {
			if (v instanceof Fixture) {
				Fixture vf = (Fixture)v;
				vf.match = (Fixture)vs[vf.matchID];
			}
		}
		
		Graph g = new Graph(world);
		
		for (Vertex v : vs) {
			g.vertices.add(v);
		}
		for (Road ro : rs) {
			g.roads.add(ro);
		}
		for (Merger m : ms) {
			g.mergers.add(m);
		}
		
		g.refreshVertexIDs();
		g.refreshRoadIDs();
		g.refreshMergerIDs();
		
		return g;
	}
	
	public void render_panel(RenderingContext ctxt) {
		
//		List<Road> roadsCopy;
//		List<Merger> mergersCopy;
//		List<BypassBoard> boardsCopy;
//		List<Vertex> verticesCopy;
//		synchronized (APP) {
//			roadsCopy = new ArrayList<Road>(roads);
//			mergersCopy = new ArrayList<Merger>(mergers);
//			boardsCopy = new ArrayList<BypassBoard>(boards);
//			verticesCopy = new ArrayList<Vertex>(vertices);
//		}
		
		for (int i = 0; i < roads.size(); i++) {
			Road r = roads.get(i);
			r.paint_panel(ctxt);
		}
		for (int i = 0; i < mergers.size(); i++) {
			Merger m = mergers.get(i);
			m.paint_panel(ctxt);
		}
		
		for (int i = 0; i < vertices.size(); i++) {
			Vertex v = vertices.get(i);
			v.paint_panel(ctxt);
		}
		
		for (int i = 0; i < boards.size(); i++) {
			BypassBoard b = boards.get(i);
			b.paint_panel(ctxt);
		}
		
		for (int i = 0; i < roads.size(); i++) {
			Road r = roads.get(i);
			r.paintDecorations(ctxt);
		}
		
	}
	
	public void render_preview(RenderingContext ctxt) {
		
		List<Road> roadsCopy;
		List<Merger> mergersCopy;
		List<BypassBoard> boardsCopy;
		List<Vertex> verticesCopy;
		synchronized (APP) {
			roadsCopy = new ArrayList<Road>(roads);
			mergersCopy = new ArrayList<Merger>(mergers);
			boardsCopy = new ArrayList<BypassBoard>(boards);
			verticesCopy = new ArrayList<Vertex>(vertices);
		}
		
		for (Road r : roadsCopy) {
			r.paint_preview(ctxt);
		}
		for (Merger m : mergersCopy) {
			m.paint_preview(ctxt);
		}
		
		for (BypassBoard b : boardsCopy) {
			b.paint_preview(ctxt);
		}
		
		for (Vertex v : verticesCopy) {
			v.paint_preview(ctxt);
		}
		
	}
	
	
	
	Transform origTransform = APP.platform.createTransform();
	
	public void paintStats(RenderingContext ctxt) {
		
		ctxt.getTransform(origTransform);
		
		ctxt.paintString(0, 0, 1.0/ctxt.cam.pixelsPerMeter, "vertex count: " + vertices.size());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1.0/ctxt.cam.pixelsPerMeter, "road count: " + roads.size());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1.0/ctxt.cam.pixelsPerMeter, "merger count: " + mergers.size());
		
		ctxt.setTransform(origTransform);
	}
	
	public void paintScene(RenderingContext ctxt) {
		
//		List<Vertex> verticesCopy;
//		synchronized (APP) {
//			verticesCopy = new ArrayList<Vertex>(vertices);
//		}
		for (int i = 0; i < vertices.size(); i++) {
			Vertex v = vertices.get(i);
			v.paintScene(ctxt);
		}
		
		if (APP.DEBUG_DRAW) {
//			List<Road> roadsCopy;
//			synchronized (APP) {
//				roadsCopy = new ArrayList<Road>(roads);
//			}
			
			for (Road r : roads) {
				r.paintBorders(ctxt);
			}
		}
		
	}
	
	public void paintIDs(RenderingContext ctxt) {
		
		List<Vertex> verticesCopy;
		synchronized (APP) {
			verticesCopy = new ArrayList<Vertex>(vertices);
		}
		for (Vertex v : verticesCopy) {
			v.paintID(ctxt);
		}
		
	}

	
	
	
	
	
	public boolean checkConsistency() {
		
		for (Vertex v : vertices) {
			if (v instanceof Fixture) {
				/*
				 * disallow more than 1 road from a fixture
				 */
				assert v.roads.size() <= 1;
			}
			for (Vertex w : vertices) {
				if (v == w) {
					
				} else {
					double distance = Point.distance(v.p, w.p);
					assert DMath.greaterThan(distance, v.r + w.r);
				}
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
					
					/*
					 * ignore first and last capsules, since these are within vertices
					 */
					for (int i = 1; i < e.capsuleCount()-1; i++) {
						Capsule es = e.getCapsule(i);
						
						for (int j = 1; j < f.capsuleCount()-1; j++) {
							Capsule fs = f.getCapsule(j);
							
							if (ShapeUtils.intersect(es, fs)) {
								assert false : "fix me";
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
