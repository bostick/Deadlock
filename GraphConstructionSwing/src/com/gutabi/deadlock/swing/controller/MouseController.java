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
import com.gutabi.deadlock.swing.utils.EdgeUtils;
import com.gutabi.deadlock.swing.utils.OverlappingException;
import com.gutabi.deadlock.swing.utils.Point;
import com.gutabi.deadlock.swing.utils.PointToBeAdded;
import com.gutabi.deadlock.swing.utils.Rat;

public class MouseController implements MouseListener, MouseMotionListener {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	//private Point curPoint;
	public List<Point> curStroke = new ArrayList<Point>();
	
	public void init() {
		VIEW.panel.addMouseListener(this);
		VIEW.panel.addMouseMotionListener(this);
	}
	
	@Override
	public void mousePressed(MouseEvent ev) {
		pressed(new Point(new Rat(ev.getX(), 1), new Rat(ev.getY(), 1)));
	}
	
	public void pressed(Point p) {
		curStroke.add(p);
		
		VIEW.repaint();
	}
	
	@Override
	public void mouseDragged(MouseEvent ev) {
		//dragged(new Point(ev.getX(), ev.getY()));
		dragged(new Point(new Rat(ev.getX(), 1), new Rat(ev.getY(), 1)));
	}
	
	public void dragged(Point p) {
		curStroke.add(p);
		
		VIEW.repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent ev) {
		released();
	}
	
	public void released() {
		for (int i = 0; i < curStroke.size()-1; i++) {
			dragged(curStroke.get(i), curStroke.get(i+1));
		}
		
		curStroke.clear();
		
		VIEW.repaint();
	}
	
	private void dragged(Point a, Point b) {
		logger.debug("dragged ");
		
		/*
		 * ignore repeated points
		 */
		
//		if (a.equals(b)) {
//			return;
//		}
		
		/*
		 * test all pairs <c, d> against <a, b>, since <a, b> could intersect multiple edges
		 * 
		 * find all events inside <a, b> and all events inside <c, d>
		 * 
		 * don't modify the model while we are finding events, so:
		 * 1. remember all events
		 * 2. process them later
		 */
		
		SortedSet<PointToBeAdded> betweenABPoints = new TreeSet<PointToBeAdded>(PointToBeAdded.ptbaComparator);
		betweenABPoints.add(new PointToBeAdded(a, Rat.ZERO));
		betweenABPoints.add(new PointToBeAdded(b, Rat.ONE));
		
		for (Edge e : MODEL.getEdges()) {
			
			for (int j = 0; j < e.getPointsSize()-1; j++) {
				Point c = e.getPoint(j);
				Point d = e.getPoint(j+1);
				try {
					Point inter = Point.intersection(a, b, c, d);
					if (inter != null && !inter.equals(b)) {
						PointToBeAdded nptba = new PointToBeAdded(inter, Point.param(inter, a, b));
						betweenABPoints.add(nptba);
					}
				} catch (OverlappingException ex) {
					
					if (Point.intersect(c, a, b)) {
						Rat cParam = Point.param(c, a, b);
						if ((cParam.isGreaterThan(Rat.ZERO) && cParam.isLessThan(Rat.ONE))) {
							PointToBeAdded nptba = new PointToBeAdded(c, cParam);
							betweenABPoints.add(nptba);
						}
					}
					
					if (Point.intersect(d, a, b)) {
						Rat dParam = Point.param(d, a, b);
						if ((dParam.isGreaterThan(Rat.ZERO) && dParam.isLessThan(Rat.ONE))) {
							PointToBeAdded nptba = new PointToBeAdded(d, dParam);
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
				process(last.p, p.p);
			}
			last = p;
		}
		betweenABPoints.clear();
		
		MODEL.checkConsistency();
		
		VIEW.repaint();
		
	}
	
	void process(final Point aaa, final Point bbb) {
		/*
		 * a is either the first in the betweenAB list, so an integer,
		 * or is later in betweenAB and has already been treated as integer when it was b
		 */
		Point aInt = new Point((int)Math.round(aaa.x.getVal()), (int)Math.round(aaa.y.getVal()));
		
		// b is not yet an integer
		
		Point bInt = new Point((int)Math.round(bbb.x.getVal()), (int)Math.round(bbb.y.getVal()));
		
		if (aInt.equals(bInt)) {
			return;
		}
		
		Vertex aV = MODEL.tryFindVertex(aInt);
		if (aV == null) {
			EdgeInfo info = MODEL.tryFindEdgeInfo(aInt);
			if (info == null) {
				aV = MODEL.createVertex(aInt);
			} else {
				Edge e = info.edge;
				int index = info.index;
				Rat param = info.param;
				aV = EdgeUtils.split(e, index, param);
			}
		}
		
		assert aV != null;
		
		Vertex bV = MODEL.tryFindVertex(bbb);
		if (bV == null) {
			EdgeInfo info = MODEL.tryFindEdgeInfo(bbb);
			if (info == null) {
				/*
				 * completely new, so use bInt
				 */
				bV = MODEL.tryFindVertex(bInt);
				if (bV == null) {
					info = MODEL.tryFindEdgeInfo(bInt);
					if (info == null) {
						/*
						 * completely new, so use bInt
						 */
						bV = MODEL.createVertex(bInt);
					} else {
						Edge e = info.edge;
						int index = info.index;
						Rat param = info.param;
						/*
						 * split takes care of adjusting b to integer coords
						 */
						bV = EdgeUtils.split(e, index, param);
					}
				} else {
					// if b is a vertex, then it is an integer
					assert bInt.x.getD() == 1;
					assert bInt.y.getD() == 1;
				}
			} else {
				Edge e = info.edge;
				int index = info.index;
				Rat param = info.param;
				/*
				 * split takes care of adjusting b to integer coords
				 */
				bV = EdgeUtils.split(e, index, param);
			}
		} else {
			// if b is a vertex, then it is an integer
			assert bbb.x.getD() == 1;
			assert bbb.y.getD() == 1;
		}
		
		assert bV != null;
		
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
			//List<Point> ePoints = e.getPoints();
			e.addPoint(aInt);
			e.addPoint(bInt);
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
		if (MODEL.tryFindVertex(bInt) != null) {
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
