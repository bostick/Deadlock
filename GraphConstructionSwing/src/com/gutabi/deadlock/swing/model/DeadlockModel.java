package com.gutabi.deadlock.swing.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.swing.utils.DPoint;
import com.gutabi.deadlock.swing.utils.Point;

public class DeadlockModel {
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	private ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	
	public Point lastPointRaw;
	public List<Point> curStrokeRaw = new ArrayList<Point>();
	
	public List<Point> curStroke1 = new ArrayList<Point>();
	
	static Logger logger = Logger.getLogger("deadlock");
	
	public void init() {
		
	}
	
	public List<Edge> getEdges() {
		assert Thread.currentThread().getName().startsWith("AWT-EventQueue-");
		return edges;
	}
		
	public List<Vertex> getVertices() {
		assert Thread.currentThread().getName().startsWith("AWT-EventQueue-");
		return vertices;
	}
	
//	//@SuppressWarnings("unchecked")
//	public List<Edge> cloneEdges() {
//		//return (List<Edge>)edges.clone();
//		return new ArrayList<Edge>(getEdges());
//	}
//	
//	public List<Vertex> cloneVertices() {
////		synchronized (this) {
////			
////		}
//		return new ArrayList<Vertex>(getVertices());
//	}
	
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
		logger.debug("createVertex: " + p);
//		assert p.x.getD() == 1;
//		assert p.y.getD() == 1;
		
		/*
		 * there should only be 1 vertex with this point
		 */
		int count = 0;
		for (Vertex w : vertices) {
			if (p.equals(w.getPoint())) {
				//assert p == w.getPoint();
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
		public double param;
		public EdgeInfo(Edge e, int index, double param) {
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
	
	public EdgeInfo tryFindEdgeInfo(DPoint b) {
		logger.debug("tryFindEdgeInfo: " + b);
		
		for (Vertex v : vertices) {
			Point d = v.getPoint();
			if (Point.doubleEquals(b.x, d.x) && Point.doubleEquals(b.y, d.y) && v.getEdges().size() > 1) {
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
		logger.debug("tryFindVertex: " + b);
		
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
			v.check();
		}
		
		for (Edge e : edges) {
			e.check();
		}
		return true;
	}
	
	
}
