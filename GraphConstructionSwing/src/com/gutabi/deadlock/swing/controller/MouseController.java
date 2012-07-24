package com.gutabi.deadlock.swing.controller;

import static com.gutabi.deadlock.swing.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.view.DeadlockView.VIEW;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.swing.model.DeadlockModel.EdgeInfo;
import com.gutabi.deadlock.swing.model.Edge;
import com.gutabi.deadlock.swing.model.Vertex;
import com.gutabi.deadlock.swing.utils.DPoint;
import com.gutabi.deadlock.swing.utils.EdgeUtils;
import com.gutabi.deadlock.swing.utils.OverlappingException;
import com.gutabi.deadlock.swing.utils.Point;
import com.gutabi.deadlock.swing.utils.PointToBeAdded;

public class MouseController implements MouseListener, MouseMotionListener {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	public void init() {
		VIEW.panel.addMouseListener(this);
		VIEW.panel.addMouseMotionListener(this);
	}
	
	public void pressed(Point p) {
		assert Thread.currentThread().getName().startsWith("AWT-EventQueue-");
		
		MODEL.lastPointRaw = p;
		MODEL.curStrokeRaw.add(p);
		
		MODEL.allStrokes.add(new ArrayList<Point>());
		
		MODEL.allStrokes.get(MODEL.allStrokes.size()-1).add(p);
		
		VIEW.repaint();
	}
	
	public void dragged(Point p) {
		assert Thread.currentThread().getName().startsWith("AWT-EventQueue-");
		if (!p.equals(MODEL.lastPointRaw)) {
			MODEL.curStrokeRaw.add(p);
			MODEL.allStrokes.get(MODEL.allStrokes.size()-1).add(p);
			MODEL.lastPointRaw = p;
			VIEW.repaint();
		}
	}
	
	public void released() {
		assert Thread.currentThread().getName().startsWith("AWT-EventQueue-");
		
		//List<Point> curStroke1 = massage(MODEL.curStrokeRaw);
		List<Point> curStroke1 = MODEL.curStrokeRaw;
		
		for (int i = 0; i < curStroke1.size()-1; i++) {
			addUserSegment(curStroke1.get(i), curStroke1.get(i+1));
		}
		
		MODEL.lastPointRaw = null;
		MODEL.curStrokeRaw.clear();
		
		VIEW.repaint();
	}
	
	public void pressed_M(final Point p) {
		assert Thread.currentThread().getName().startsWith("main");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				pressed(p);
			}
		});
	}
	
	public void dragged_M(final Point p) {
		assert Thread.currentThread().getName().startsWith("main");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				dragged(p);
			}
		});
	}
	
	public void released_M() {
		released_M(false);
	}
	
	public void released_M(final boolean massage) {
		assert Thread.currentThread().getName().startsWith("main");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (massage) {
					MODEL.curStrokeRaw = massage(MODEL.curStrokeRaw);
				}
				released();
			}
		});
	}
	
	@Override
	public void mousePressed(MouseEvent ev) {
		pressed(new Point(ev.getX(), ev.getY()));
	}
	
	@Override
	public void mouseDragged(MouseEvent ev) {
		//dragged(new Point(ev.getX(), ev.getY()));
		Point p = new Point(ev.getX(), ev.getY());
		dragged(p);
	}
	
	@Override
	public void mouseReleased(MouseEvent ev) {
		
		MODEL.curStrokeRaw = massage(MODEL.curStrokeRaw);
		
		released();
	}
	
	private List<Point> massage(List<Point> raw) {
		
		/*
		 * maybe 1. cut-off rest of points if angle is too sharp
		 * maybe 2. cut-off rest of points if too close to 
		 * 
		 * 1. adjust for first point to be on a vertex, if close enough
		 * 2. adjust for second point, etc.
		 * 
		 * 3. if first point is not on vertex, adjust for first vertex to be on edge, if close enough
		 * 4. if second point is not on vertex, adjust for second vertex to be on edge, if close enough
		 * 
		 * 
		 * scan endpoints of raw first
		 * scan over vertices first since the user will probably be trying to hit vertices more than just edges
		 * 
		 */
		
		//List<Point> diff = new ArrayList<Point>(raw.size());
		List<Point> adj = new ArrayList<Point>(raw);
		
		if (adj.size() == 1) {
			return adj;
		}
		
		int s = raw.size();
		Point first = raw.get(0);
		Point last = raw.get(s-1);
		
		/*
		 * first and last have to be very close
		 */
		if ((!last.equals(first)) && Point.dist(last, first) <= 20.0) {
			/*
			 * maintain invariant that there are no contiguous, equal points
			 */
			if (!raw.get(s-2).equals(first)) {
				adj.set(s-1, first);
				last = first;
			} else {
				adj.remove(s-1);
				s = s-1;
				last = raw.get(s-1);
			}
		}
		
		Vertex best = null;
		
		for (Vertex v : MODEL.getVertices()) {
			
			Point vp = v.getPoint();
			
			if ((!first.equals(vp)) && Point.dist(first, vp) <= 40.0) {
				if (best == null) {
					best = v;
				} else if (Point.dist(first, vp) < Point.dist(first, best.getPoint())) {
					best = v;
				}
			}
			
		}
		
		if (best != null) {
			
			if (adj.size() == 1) {
				adj.set(0, best.getPoint());
			} else {
				if (!adj.get(1).equals(best.getPoint())) {
					adj.set(0, best.getPoint());
					first = adj.get(0);
				} else {
					adj.remove(0);
					s = s-1;
					first = adj.get(0);
				}
			}
			
		}
		
		best = null;
		
		for (Vertex v : MODEL.getVertices()) {
			
			Point vp = v.getPoint();
			
			if ((!last.equals(vp)) && Point.dist(last, vp) <= 40.0) {
				if (best == null) {
					best = v;
				} else if (Point.dist(last, vp) < Point.dist(last, best.getPoint())) {
					best = v;
				}
			}
		}
		
		if (best != null) {
			
			if (adj.size() == 1) {
				adj.set(s-1, best.getPoint());
			} else {
				if (!adj.get(s-2).equals(best.getPoint())) {
					adj.set(s-1, best.getPoint());
					last = adj.get(s-1);
				} else {
					adj.remove(s-1);
					s = s-1;
					last = adj.get(s-1);
				}
			}
			
		}
		
		return adj;
	}
	
	private void addUserSegment(Point a, Point b) {
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
		
		VIEW.repaint();
		
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
				aV = EdgeUtils.split(intInfo);
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
					bV = EdgeUtils.split(intInfo);
					assert bV.getPoint().equals(bInt);
				}
			} else {
				EdgeInfo intInfo = MODEL.tryFindEdgeInfo(bInt);
				if (intInfo == null) {
					/*
					 * edge on b, no edge on bInt
					 */
					bV = EdgeUtils.split(doubleInfo);
				} else {
					/*
					 * edge on b, edge on bInt
					 */
					bV = EdgeUtils.split(intInfo);
					if (intInfo.edge != doubleInfo.edge) {
						bV = EdgeUtils.split(doubleInfo);
					}
					assert bV.getPoint().equals(bInt);
				}
			}
		} else if (!Point.equals(b.p, bInt)) {
			EdgeInfo doubleInfo = MODEL.tryFindEdgeInfo(b.p);
			if (doubleInfo != null) {
				EdgeUtils.split(doubleInfo);
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
		
		MODEL.checkConsistency();
		
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		;
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		;
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		;
	}
	
}
