package com.gutabi.deadlock.swing.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.swing.utils.DPoint;
import com.gutabi.deadlock.swing.utils.EdgeUtils;
import com.gutabi.deadlock.swing.utils.OverlappingException;
import com.gutabi.deadlock.swing.utils.Point;
import com.gutabi.deadlock.swing.utils.PointToBeAdded;

public class DeadlockModel {
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	private ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	
	public Point lastPointRaw;
	public List<Point> curStrokeRaw = new ArrayList<Point>();
	
	public List<List<Point>> allStrokes = new ArrayList<List<Point>>();
	
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
		assert Thread.currentThread().getName().startsWith("AWT-EventQueue-");
		edges.clear();
		vertices.clear();
	}
	
	public void clear_M() {
		assert Thread.currentThread().getName().startsWith("main");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				clear();
			}
		});
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
			if (Point.doubleEquals(b.x, d.x) && Point.doubleEquals(b.y, d.y)) {
				throw new IllegalArgumentException("point is on vertex");
			}
		}
		for (Edge e : edges) {
			for (int i = 0; i < e.getPointsSize()-1; i++) {
				Point c = e.getPoint(i);
				Point d = e.getPoint(i+1);
				if (Point.intersect(b, c, d)) {
					if ((e.getStart() != null && e.getEnd() != null) && (Point.equals(b, e.getStart().getPoint()) || Point.equals(b, e.getEnd().getPoint()))) {
						throw new IllegalArgumentException("point is on vertex and for some reason it is not a vertex");
					}
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
	
	
	
	public void addUserSegment(Point a, Point b) {
		logger.debug("dragged");
		
		SortedSet<PointToBeAdded> betweenABPoints = new TreeSet<PointToBeAdded>(PointToBeAdded.ptbaComparator);
		betweenABPoints.add(new PointToBeAdded(new DPoint(a.x, a.y), 0.0));
		betweenABPoints.add(new PointToBeAdded(new DPoint(b.x, b.y), 1.0));
		
		for (Edge e : MODEL.getEdges()) {
			
			for (int j = 0; j < e.getPointsSize()-1; j++) {
				Point c = e.getPoint(j);
				Point d = e.getPoint(j+1);
				try {
					DPoint inter = Point.intersection(a, b, c, d);
					if (inter != null && !(Point.doubleEquals(inter.x, b.x) && Point.doubleEquals(inter.y, b.y))) {
						PointToBeAdded nptba = new PointToBeAdded(inter, Point.param(inter, a, b));
						betweenABPoints.add(nptba);
					}
				} catch (OverlappingException ex) {
					
					if (Point.intersect(c, a, b)) {
						double cParam = Point.param(c, a, b);
						if ((cParam > 0.0 && cParam < 1.0)) {
							PointToBeAdded nptba = new PointToBeAdded(new DPoint(c.x, c.y), cParam);
							betweenABPoints.add(nptba);
						}
					}
					
					if (Point.intersect(d, a, b)) {
						double dParam = Point.param(d, a, b);
						if ((dParam > 0.0 && dParam < 1.0)) {
							PointToBeAdded nptba = new PointToBeAdded(new DPoint(d.x, d.y), dParam);
							betweenABPoints.add(nptba);
						}
					}
					
				}
			} // for edgePoints c, d
			
		} // for Edge e
		
		boolean first = true;
		PointToBeAdded last = null;
		for (PointToBeAdded p : betweenABPoints) {
			if (first) {
				first = false;
			} else {
				assert last != null;
				process(last, p);
			}
			last = p;
		}
		betweenABPoints.clear();
	}
	
	void process(final PointToBeAdded a, final PointToBeAdded b) {
		logger.debug("process: a: " + a + " b: " + b);
		
		/*
		 * a will only ever be treated as an integer
		 * that is, only aInt will be used
		 * either a is the first point coming in (so it is int) or it was b in the previous iteration
		 * and all of the proper vertex and edge handling was done for the integer value of a
		 */
		Point aInt = new Point((int)Math.round(a.p.x), (int)Math.round(a.p.y));
		
		// b is not yet an integer
		
		Point bInt = new Point((int)Math.round(b.p.x), (int)Math.round(b.p.y));
		
		logger.debug("finding a");
		Vertex aV = MODEL.tryFindVertex(aInt);
		if (aV == null) {
			EdgeInfo intInfo = MODEL.tryFindEdgeInfo(aInt);
			if (intInfo == null) {
				aV = MODEL.createVertex(aInt);
			} else {
				aV = EdgeUtils.split(aInt);
			}
		}
		
		assert aV != null;
		
		logger.debug("finding b");
		Vertex bV = MODEL.tryFindVertex(bInt);
		if (bV == null) {
			EdgeInfo doubleInfo = MODEL.tryFindEdgeInfo(b.p);
			if (doubleInfo == null) {
				EdgeInfo intInfo = MODEL.tryFindEdgeInfo(bInt);
				if (intInfo == null) {
					/*
					 * no edge on b, no edge on bInt
					 */
					bV = MODEL.createVertex(bInt);
					assert bV.getPoint().equals(bInt);
				} else {
					/*
					 * no edge on b, edge on bInt
					 */
					bV = EdgeUtils.split(bInt);
					assert bV.getPoint().equals(bInt);
				}
			} else {
				EdgeInfo intInfo = MODEL.tryFindEdgeInfo(bInt);
				if (intInfo == null) {
					/*
					 * edge on b, no edge on bInt
					 */
					bV = EdgeUtils.split(b.p);
				} else {
					/*
					 * edge on b, edge on bInt
					 */
					bV = EdgeUtils.split(bInt);
					if (intInfo.edge != doubleInfo.edge) {
						bV = EdgeUtils.split(b.p);
					}
					assert bV.getPoint().equals(bInt);
				}
			}
		} else if (!Point.equals(b.p, bInt)) {
			EdgeInfo doubleInfo = MODEL.tryFindEdgeInfo(b.p);
			if (doubleInfo != null) {
				EdgeUtils.split(b.p);
			}
		}
		
		assert bV != null;
		
		if (aV == bV) {
			/*
			 * could happen if bV is adjusted
			 */
			return;
		}
		
		Edge e = null;
		for (Edge ee : aV.getEdges()) {
			if (((ee.getStart() == aV && ee.getEnd() == bV) || (ee.getStart() == bV && ee.getEnd() == aV)) && ee.getPointsSize() == 2) {
				/*
				 * both a and b are already both vertices and connected, so just use this
				 */
				e = ee;
				break;
			}
		}
		if (e == null) {
			e = MODEL.createEdge();
			e.addPoint(aInt);
			e.addPoint(bV.getPoint());
			e.setStart(aV);
			e.setEnd(bV);
			
			e.check();
			
			aV.add(e);
			bV.add(e);
		}
		
		Edge working;
		
		List<Edge> aEdges = aV.getEdges();
		if (aEdges.size() == 2) {
			/*
			 * a has 2 edges now, counting e, so the 2 should be merged
			 */
			Edge aEdge;
			if (aEdges.get(0) == e) {
				aEdge = aEdges.get(1);
			} else {
				aEdge = aEdges.get(0);
			}
			working = EdgeUtils.merge(aEdge, e);
		} else {
			working = e;
		}
		
		/*
		 * bV could have been removed if the merging of aEdge and e formed a loop (thereby removing bV in the process),
		 * so check that b is still a vertex first
		 */
		if (!bV.isRemoved()) {
			List<Edge> bEdges = bV.getEdges();
			if (bEdges.size() == 2) {
				Edge bEdge;
				if (bEdges.get(0) == e) {
					bEdge = bEdges.get(1);
				} else {
					bEdge = bEdges.get(0);
				}
				EdgeUtils.merge(working, bEdge);
			}
		}
		
	}
	
	
	
	
	public boolean checkConsistency() {
		
		for (Vertex v : vertices) {
			v.check();
		}
		
		for (Edge e : edges) {
			e.check();
			if (e.getStart() == null && e.getEnd() == null) {
				continue;
			}
			for (Edge f : edges) {
				if (e == f) {
					continue;
				}
				for (int i = 0; i < e.getPointsSize()-1; i++) {
					Point a = e.getPoint(i);
					Point b = e.getPoint(i+1);
					for (int j = 0; j < f.getPointsSize()-1; j++) {
						Point c = f.getPoint(j);
						Point d = f.getPoint(j+1);
						DPoint inter = Point.intersection(a, b, c, d);
						if (inter != null && !(Point.equals(inter, a) || Point.equals(inter, b) || Point.equals(inter, c) || Point.equals(inter, d))) {
							assert false : "No edges should intersect";
						}
					}
				}
				if ((e.getStart() == f.getStart() && e.getEnd() == f.getEnd()) || (e.getStart() == f.getEnd() && e.getEnd() == f.getStart())) {
					/*
					 * e and f share endpoints
					 */
					Set<Point> shared = new HashSet<Point>();
					for (int i = 0; i < e.getPointsSize(); i++) {
						Point eP = e.getPoint(i);
						for (int j = 0; j < f.getPointsSize(); j++) {
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
