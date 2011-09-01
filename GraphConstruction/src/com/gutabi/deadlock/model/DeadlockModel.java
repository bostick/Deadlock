package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
	
	public Vertex createVertex(PointF p) {
		/*
		 * there should only be 1 vertex with this point
		 */
		int count = 0;
		for (Vertex w : vertices) {
			if (PointFUtils.equals(p, w.getPointF())) {
				count++;
			}
		}
		assert count == 0;
		Vertex v = new VertexImpl(p);
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
			List<PointF> ePoints = e.getPoints();
			for (int i = 0; i < ePoints.size()-1; i++) {
				PointF c = ePoints.get(i);
				PointF d = ePoints.get(i+1);
				if (PointFUtils.intersection(b, c, d)) {
					return e;
				}
			}
		}
		throw new IllegalArgumentException("point not found");
	}
	
//	public Edge tryFindEdge(PointF b) {
//		for (Vertex v : vertices) {
//			PointF d = v.getPointF();
//			if (PointFUtils.equals(b, d) && v.getEdges().size() > 1) {
//				throw new IllegalArgumentException("point is on vertex");
//			}
//		}
//		for (Edge e : edges) {
//			List<PointF> ePoints = e.getPoints();
//			for (int i = 0; i < ePoints.size()-1; i++) {
//				PointF c = ePoints.get(i);
//				PointF d = ePoints.get(i+1);
//				if (PointFUtils.intersection(b, c, d)) {
//					return e;
//				}
//			}
//		}
//		return null;
//	}
	
	public class EdgeInfo {
		public Edge edge;
		public int index;
		public EdgeInfo(Edge e, int index) {
			this.edge = e;
			this.index = index;
		}
	}
	
	public EdgeInfo tryFindEdgeInfo(PointF b) {
		for (Vertex v : vertices) {
			PointF d = v.getPointF();
			if (PointFUtils.equals(b, d) && v.getEdges().size() > 1) {
				throw new IllegalArgumentException("point is on vertex");
			}
		}
		for (Edge e : edges) {
			List<PointF> ePoints = e.getPoints();
			for (int i = 0; i < ePoints.size()-1; i++) {
				PointF c = ePoints.get(i);
				PointF d = ePoints.get(i+1);
				if (PointFUtils.intersection(b, c, d) && !PointFUtils.equals(b, c)) {
					if (PointFUtils.equals(b, d)) {
						return new EdgeInfo(e, i+1);
					} else {
						assert false;
					}
				}
			}
		}
		return null;
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
			/*
			 * d may be null if in the midle of processing (and not currently consistent)
			 */
			if (d != null && PointFUtils.equals(b, d)) {
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
			
			assert !((VertexImpl)v).isRemoved();
			
			assert v.getPointF() != null;
			
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
				
				assert !((EdgeImpl)e).isRemoved();
				
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
			
			assert !((EdgeImpl)e).isRemoved();
			
			boolean loop = false;
			if (e.getStart() == e.getEnd()) {
				loop = true;
			} else {
				assert e.getStart() != null && e.getEnd() != null;
				assert !((VertexImpl)e.getStart()).isRemoved();
				assert !((VertexImpl)e.getEnd()).isRemoved();
			}
			List<PointF> points = e.getPoints();
			for (int i = 0; i < points.size(); i++) {
				PointF p = points.get(i);
				int count = 0;
				for (PointF q : e.getPoints()) {
					if (PointFUtils.equals(p, q)) {
						count++;
					}
				}
				if (loop && (i == 0 || i == points.size()-1)) {
					assert count == 2;
				} else {
					assert count == 1;
				}
			}
		}
		return true;
	}
	
	final class VertexImpl implements Vertex {
		
		private final PointF p;
		
		private List<Edge> eds = new ArrayList<Edge>();
		
		private Edge lastEdgeAdded;
		
		private boolean removed = false;
		
		VertexImpl(PointF p) {
			this.p = p;
		}
		
		public void add(Edge ed) {
			if (removed) {
				throw new IllegalStateException();
			}
			if (!(ed.getStart() == this && ed.getEnd() == this)) {
				assert !eds.contains(ed);
			}
			eds.add(ed);
			lastEdgeAdded = ed;
		}
		
		public void remove(Edge ed) {
			if (removed) {
				throw new IllegalStateException();
			}
			assert eds.contains(ed);
			eds.remove(ed);
		}
		
		public List<Edge> getEdges() {
			if (removed) {
				throw new IllegalStateException();
			}
			return eds;
		}
		
		public Edge getOnlyEdge() {
			assert eds.size() == 1;
			return lastEdgeAdded;
		}
		
//		public void setPointF(PointF p) {
//			if (removed) {
//				throw new IllegalStateException();
//			}
//			this.p = p;
//		}
		
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
			if (removed) {
				throw new IllegalStateException();
			}
			removed = true;
		}
		
		private boolean isRemoved() {
			return removed;
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
			for (int i = 0; i < points.size()-1; i++) {
				PointF prev = points.get(i);
				PointF cur = points.get(i+1);
				canvas.drawLine(prev.x, prev.y, cur.x, cur.y, paint1);
			}
//			if (start == null && end == null) {
//				PointF prev = points.get(points.size()-1);
//				PointF cur = points.get(0);
//				canvas.drawLine(prev.x, prev.y, cur.x, cur.y, paint1);
//			}
			for (int i = 0; i < points.size()-1; i++) {
				PointF prev = points.get(i);
				PointF cur = points.get(i+1);
				canvas.drawLine(prev.x, prev.y, cur.x, cur.y, paint2);
			}
//			if (start == null && end == null) {
//				PointF prev = points.get(points.size()-1);
//				PointF cur = points.get(0);
//				canvas.drawLine(prev.x, prev.y, cur.x, cur.y, paint2);
//			}
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
			if (removed) {
				throw new IllegalStateException();
			}
			removed = true;
		}
		
		private boolean isRemoved() {
			return removed;
		}
	}
	
}
