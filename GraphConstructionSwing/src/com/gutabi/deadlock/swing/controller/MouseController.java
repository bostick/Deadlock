package com.gutabi.deadlock.swing.controller;

import static com.gutabi.deadlock.swing.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.view.DeadlockView.VIEW;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.swing.model.DeadlockModel.EdgeInfo;
import com.gutabi.deadlock.swing.model.Edge;
import com.gutabi.deadlock.swing.model.Vertex;
import com.gutabi.deadlock.swing.utils.EdgeUtils;
import com.gutabi.deadlock.swing.utils.OverlappingException;
import com.gutabi.deadlock.swing.utils.Point;
import com.gutabi.deadlock.swing.utils.PointUtils;

public class MouseController implements MouseListener, MouseMotionListener {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	private Point curPoint;
	
	public void init() {
		VIEW.panel.addMouseListener(this);
		VIEW.panel.addMouseMotionListener(this);
	}
	
	public void pressed(Point ev) {
		logger.debug("pressed " + ev);
		curPoint = ev;
		
		VIEW.repaint();
	}
	
	public void dragged(Point ev) {
		logger.debug("dragged " + ev);
		
		Point a = curPoint;
		
		Point b = ev;
		
		/*
		 * ignore repeated points
		 */
		
		if (PointUtils.equals(a, b)) {
			return;
		}
		
		curPoint = b;
		
		/*
		 * test all pairs <c, d> against <a, b>, since <a, b> could intersect multiple edges
		 * 
		 * find all events inside <a, b> and all events inside <c, d>
		 * 
		 * don't modify the model while we are finding events, so:
		 * 1. remember all events
		 * 2. process them later
		 */
		
		List<PointToBeAdded> betweenABPoints = new ArrayList<PointToBeAdded>();
		betweenABPoints.add(new PointToBeAdded(a, 0.0));
		betweenABPoints.add(new PointToBeAdded(b, 1.0));
		
		for (Edge e : MODEL.getEdges()) {
			
			List<Point> edgePoints = e.getPoints();
			for (int j = 0; j < edgePoints.size()-1; j++) {
				Point c = edgePoints.get(j);
				Point d = edgePoints.get(j+1);
				try {
					Point inter = PointUtils.intersection(a, b, c, d);
					if (inter != null) {
						
						if (PointUtils.intersection(inter, a, b)) {
							PointToBeAdded nptba = new PointToBeAdded(inter, PointUtils.param(inter, a, b));
							boolean fresh = true;
							for (PointToBeAdded ptba : betweenABPoints) {
								if (PointUtils.equals(nptba.p, ptba.p) && nptba.param == ptba.param) {
									fresh = false;
									break;
								}
							}
							if (fresh) {
								betweenABPoints.add(nptba);
							}
						}
						
					}
				} catch (OverlappingException ex) {
					
					if (PointUtils.intersection(c, a, b)) {
						double cParam = PointUtils.param(c, a, b);
						if ((0 < cParam && cParam < 1)) {
							PointToBeAdded nptba = new PointToBeAdded(c, cParam);
							boolean fresh = true;
							for (PointToBeAdded ptba : betweenABPoints) {
								if (PointUtils.equals(nptba.p, ptba.p) && nptba.param == ptba.param) {
									fresh = false;
									break;
								}
							}
							if (fresh) {
								betweenABPoints.add(nptba);
							}
						}
					}
					
					if (PointUtils.intersection(d, a, b)) {
						double dParam = PointUtils.param(d, a, b);
						if ((0 < dParam && dParam < 1)) {
							PointToBeAdded nptba = new PointToBeAdded(d, dParam);
							boolean fresh = true;
							for (PointToBeAdded ptba : betweenABPoints) {
								if (PointUtils.equals(nptba.p, ptba.p) && nptba.param == ptba.param) {
									fresh = false;
									break;
								}
							}
							if (fresh) {
								betweenABPoints.add(nptba);
							}
						}
					}
					
				}
			} // for edgePoints c, d
			
		} // for Edge e
		
		Collections.sort(betweenABPoints, ptbaComparator);
		
		for (int i = 0; i < betweenABPoints.size()-1; i++) {
			PointToBeAdded aa = betweenABPoints.get(i);
			PointToBeAdded bb = betweenABPoints.get(i+1);
			
			process(aa.p, bb.p);
		}
		betweenABPoints.clear();
		
		MODEL.checkConsistency();
		
		VIEW.repaint();
		
	}
	
	static class PointToBeAdded {
		
		Point p;
		
		/**
		 * value ranging from 0..1 measuring distance between points at index and index+1, used for sorting
		 */
		double param;
		
		PointToBeAdded(Point p, double param) {
			assert param >= 0.0;
			assert param <= 1.0;
			this.p = p;
			this.param = param;
		}
		
		public String toString() {
			return "PTBA: " + p + " " + param;
		}
		
	}
	
	static class PTBAComparator implements Comparator<PointToBeAdded> {
		@Override
		public int compare(PointToBeAdded a, PointToBeAdded b) {
			if (a.param < b.param) {
				return -1;
			} else if (a.param > b.param) {
				return 1;
			} else {
				assert PointUtils.equals(a.p, b.p) && a.param == b.param;
				return 0;
			}
		}
	}
	
	static Comparator<PointToBeAdded> ptbaComparator = new PTBAComparator();
	
	
	public void released() {
		logger.debug("released");
		curPoint = null;
	}
	
	void process(final Point a, final Point b) {
		
		Vertex aV = MODEL.tryFindVertex(a);
		
		if (aV == null) {
			EdgeInfo info = MODEL.tryFindEdgeInfo(a);
			if (info == null) {
				aV = MODEL.createVertex(a);
			} else {
				Edge e = info.edge;
				int index = info.index;
				double param = info.param;
				aV = EdgeUtils.split(e, index, param);
			}
		}
		
		assert aV != null;
		
		Vertex bV = MODEL.tryFindVertex(b);
		
		if (bV == null) {
			EdgeInfo info = MODEL.tryFindEdgeInfo(b);
			if (info == null) {
				bV = MODEL.createVertex(b);
			} else {
				Edge e = info.edge;
				int index = info.index;
				double param = info.param;
				bV = EdgeUtils.split(e, index, param);
			}
		}
		
		assert bV != null;
		
		Edge e = null;
		for (Edge ee : aV.getEdges()) {
			if (((ee.getStart() == aV && ee.getEnd() == bV) || (ee.getStart() == bV && ee.getEnd() == aV)) && ee.getPoints().size() == 2) {
				/*
				 * both a and b are already both vertices and connected, so just use this
				 */
				e = ee;
			}
		}
		if (e == null) {
			e = MODEL.createEdge();
			List<Point> ePoints = e.getPoints();
			ePoints.add(a);
			ePoints.add(b);
			e.setStart(aV);
			e.setEnd(bV);
			
			aV.add(e);
			bV.add(e);
		}
		
		Edge working;
		
		List<Edge> aEdges = aV.getEdges();
		if (aEdges.size() == 2) {
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
		 * bV could have been removed if the merging of aEdge and e formed a loop (thereby removing bV in the process)
		 */
		
		if (MODEL.tryFindVertex(b) != null) {
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
	public void mousePressed(MouseEvent ev) {
		pressed(new Point(ev.getX(), ev.getY()));
	}
	
	@Override
	public void mouseDragged(MouseEvent ev) {
		dragged(new Point(ev.getX(), ev.getY()));
	}
	
	@Override
	public void mouseReleased(MouseEvent ev) {
		released();
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
