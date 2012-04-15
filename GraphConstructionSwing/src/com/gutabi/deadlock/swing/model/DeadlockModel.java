package com.gutabi.deadlock.swing.model;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.swing.utils.Point;
import com.gutabi.deadlock.swing.utils.Rat;

public class DeadlockModel {
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	public static final int GRID_WIDTH = 480;
	public static final int GRID_HEIGHT = 820;
	public static final int GRID_DELTA = 30;
	
	private List<Edge> edges = new ArrayList<Edge>();
	private List<Vertex> vertices = new ArrayList<Vertex>();
	
	public void init() {
		
	}
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	public List<Vertex> getVertices() {
		return vertices;
	}
	
	public void clear() {
		edges.clear();
		vertices.clear();
	}
	
	public Edge createEdge() {
		Edge e = new Edge();
		edges.add(e);
		return e;
	}
	
	public Vertex createVertex(Point p) {
		assert p.x.getD() == 1;
		assert p.y.getD() == 1;
		
		/*
		 * there should only be 1 vertex with this point
		 */
		int count = 0;
		for (Vertex w : vertices) {
			if (p.equals(w.getPoint())) {
				assert p == w.getPoint();
				count++;
			}
		}
		assert count == 0;
		Vertex v = new Vertex(p);
		vertices.add(v);
		return v;
	}
	
	public void removeVertex(Vertex v) {
		v.remove();
		vertices.remove(v);
	}
	
	public void removeEdge(Edge e) {
		e.remove();
		edges.remove(e);
	}
	
	public Edge findEdge(Point b) {
		for (Vertex v : vertices) {
			Point d = v.getPoint();
			if (b.equals(d) && v.getEdges().size() > 1) {
				throw new IllegalArgumentException("point is on vertex");
			}
		}
		for (Edge e : edges) {
			//List<Point> ePoints = e.getPoints();
			for (int i = 0; i < e.getPointsSize()-1; i++) {
				Point c = e.getPoint(i);
				Point d = e.getPoint(i+1);
				if (Point.intersect(b, c, d)) {
					return e;
				}
			}
		}
		throw new IllegalArgumentException("point not found");
	}
	
	public class EdgeInfo {
		public Edge edge;
		public int index;
		public Rat param;
		public EdgeInfo(Edge e, int index, Rat param) {
			this.edge = e;
			this.index = index;
			this.param = param;
		}
	}
	
	public EdgeInfo tryFindEdgeInfo(Point b) {
		for (Vertex v : vertices) {
			Point d = v.getPoint();
			if (b.equals(d) && v.getEdges().size() > 1) {
				throw new IllegalArgumentException("point is on vertex");
			}
		}
		for (Edge e : edges) {
			//List<Point> ePoints = e.getPoints();
			for (int i = 0; i < e.getPointsSize()-1; i++) {
				Point c = e.getPoint(i);
				Point d = e.getPoint(i+1);
				if (Point.intersect(b, c, d)) {
					return new EdgeInfo(e, i, Point.param(b, c, d));
				}
			}
		}
		return null;
	}
	
	public int findIndex(Edge e, Point b) {
		//List<Point> points = e.getPoints();
		for (int i = 0; i < e.getPointsSize(); i++) {
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
		for (Vertex v : vertices) {
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
		for (Vertex v : vertices) {
			Point d = v.getPoint();
			if (b.equals(d)) {
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
	
	public boolean checkConsistency() {
		
		for (Vertex v : vertices) {
			
			assert !v.isRemoved();
			
			assert v.getPoint() != null;
			
			int edgeCount = v.getEdges().size();
			
			/*
			 * edgeCount cannot be 0, why have some free-floating vertex?
			 */
			assert edgeCount != 0;
			
			/*
			 * edgeCount cannot be 2, edges should just be merged
			 */
			assert edgeCount != 2;
			
			/*
			 * all edges in v should be unique
			 */
			int count;
			for (Edge e : v.getEdges()) {
				
				assert !e.isRemoved();
				
				count = 0;
				for (Edge f : v.getEdges()) {
					if (e == f) {
						count++;
					}
				}
				if (e.getStart() == v && e.getEnd() == v) {
					assert count == 2;
				} else {
					assert count == 1;
				}
			}
		}
		
		for (Edge e : edges) {
			e.check();
		}
		return true;
	}
	
	
}
