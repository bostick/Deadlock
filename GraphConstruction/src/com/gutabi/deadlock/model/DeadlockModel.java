package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class DeadlockModel {
	
	public static final int GRID_WIDTH = 480;
	public static final int GRID_HEIGHT = 820;
	public static final int GRID_DELTA = 30;
	
	private List<Edge> edges = new ArrayList<Edge>();
	private List<Vertex> vertices = new ArrayList<Vertex>();
	
	private List<PointF> pointsToBeProcessed = new ArrayList<PointF>();
	
	public List<PointF> getPointsToBeProcessed() {
		return pointsToBeProcessed;
	}
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	public List<Vertex> getVertices() {
		return vertices;
	}
	
	public Edge createEdge() {
		Edge e = new EdgeImpl();
		edges.add(e);
		return e;
	}
	
	public Vertex createVertex() {
		Vertex v = new VertexImpl();
		vertices.add(v);
		return v;
	}
	
	public void removeVertex(Vertex v) {
		((VertexImpl)v).remove();
		vertices.remove(v);
	}
	
	public void removeEdge(Edge e) {
		((EdgeImpl)e).remove();
		edges.remove(e);
	}
	
	public Edge findEdge(PointF b) {
		for (Vertex v : vertices) {
			PointF d = v.getPointF();
			if (PointFUtils.equals(b, d) && v.getEdges().size() > 1) {
				throw new IllegalArgumentException("point is on vertex");
			}
		}
		for (Edge e : edges) {
			for (PointF d : e.getPoints()) {
				if (PointFUtils.equals(b, d)) {
					return e;
				}
			}
		}
		throw new IllegalArgumentException("point not found");
	}
	
	public int findIndex(Edge e, PointF b) {
		List<PointF> points = e.getPoints();
		for (int i = 0; i < points.size(); i++) {
			PointF d = points.get(i);
			if (PointFUtils.equals(b, d)) {
				return i;
			}
		}
		return -1;
	}
	
	public Vertex findVertex(PointF b) {
		Vertex found = null;
		int count = 0;
		for (Vertex v : vertices) {
			PointF d = v.getPointF();
			if (PointFUtils.equals(b, d)) {
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
	
	public Vertex tryFindVertex(PointF b) {
		Vertex found = null;
		int count = 0;
		for (Vertex v : vertices) {
			PointF d = v.getPointF();
			if (PointFUtils.equals(b, d)) {
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
			int edgeCount = v.getEdges().size();
			assert edgeCount != 2;
			PointF p = v.getPointF();
			int count = 0;
			for (Vertex w : vertices) {
				if (PointFUtils.equals(p, w.getPointF())) {
					count++;
				}
			}
			assert count == 1;
			for (Edge e : v.getEdges()) {
				count = 0;
				for (Edge f : v.getEdges()) {
					if (e == f) {
						count++;
					}
				}
				assert count == 1;
			}
		}
		/*
		 * TODO: handle stand-alone loops that have start and end null and first and last points the same
		 */
		for (Edge e : edges) {
			for (PointF p : e.getPoints()) {
				int count = 0;
				for (PointF q : e.getPoints()) {
					if (PointFUtils.equals(p, q)) {
						count++;
					}
				}
				//assert count == 1;
			}
//			assert e.getStart() != null;
//			assert e.getEnd() != null;
		}
		return true;
	}
	
	final class VertexImpl implements Vertex {
		
		private PointF p;
		
		private Set<Edge> eds = new HashSet<Edge>();
		
		private boolean removed = false;
		
		public void add(Edge ed) {
			if (removed) {
				throw new IllegalStateException();
			}
			assert !eds.contains(ed);
			eds.add(ed);
		}
		
		public Set<Edge> getEdges() {
			if (removed) {
				throw new IllegalStateException();
			}
			return eds;
		}
		
		public void setPointF(PointF p) {
			if (removed) {
				throw new IllegalStateException();
			}
			this.p = p;
		}
		
		public PointF getPointF() {
			if (removed) {
				throw new IllegalStateException();
			}
			return p;
		}
		
		public void paint(Canvas canvas, Paint paint) {
			if (removed) {
				throw new IllegalStateException();
			}
			canvas.drawPoint(p.x, p.y, paint);
		}
		
		private void remove() {
			removed = true;
		}
	}
	
	final class EdgeImpl implements Edge {
		
		private List<PointF> points = new LinkedList<PointF>();
		
		private Vertex start;
		private Vertex end;
		
		private boolean removed = false;
		
		public void setStart(Vertex v) {
			if (removed) {
				throw new IllegalStateException();
			}
			start = v;
		}
		
		public Vertex getStart() {
			if (removed) {
				throw new IllegalStateException();
			}
			return start;
		}
		
		public void setEnd(Vertex v) {
			if (removed) {
				throw new IllegalStateException();
			}
			end = v;
		}
		
		public Vertex getEnd() {
			if (removed) {
				throw new IllegalStateException();
			}
			return end;
		}
		
		public List<PointF> getPoints() {
			if (removed) {
				throw new IllegalStateException();
			}
			return points;
		}
		
		public void paint(Canvas canvas, Paint paint1, Paint paint2) {
			if (removed) {
				throw new IllegalStateException();
			}
			for (int j = 1; j < points.size(); j++) {
				PointF prev = points.get(j-1);
				PointF cur = points.get(j);
				canvas.drawLine(prev.x, prev.y, cur.x, cur.y, paint1);
			}
			for (int j = 1; j < points.size(); j++) {
				PointF prev = points.get(j-1);
				PointF cur = points.get(j);
				canvas.drawLine(prev.x, prev.y, cur.x, cur.y, paint2);
			}
		}
		
		public void paint(Canvas canvas, Paint paint1) {
			if (removed) {
				throw new IllegalStateException();
			}
			for (int j = 1; j < points.size(); j++) {
				PointF prev = points.get(j-1);
				PointF cur = points.get(j);
				canvas.drawLine(prev.x, prev.y, cur.x, cur.y, paint1);
			}
		}
		
		private void remove() {
			removed = true;
		}
	}
	
}
