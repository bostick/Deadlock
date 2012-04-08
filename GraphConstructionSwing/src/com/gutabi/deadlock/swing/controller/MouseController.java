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
	//private List<Point> curStroke;
	//private List<List<Point>> strokes = new ArrayList<List<Point>>();
	
	public void init() {
		VIEW.panel.addMouseListener(this);
		VIEW.panel.addMouseMotionListener(this);
	}
	
	public void pressed(Point ev) {
		logger.debug("pressed " + ev);
		curPoint = ev;
		//List<Point> pointsToBeProcessed = MODEL.getPointsToBeProcessed();
		//curStroke = new ArrayList<Point>();
		//curStroke.add(curPoint);
		//strokes.add(curStroke);
		//pointsToBeProcessed.add(curPoint);
		
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
		//curStroke.add(b);
		
//		{
//		List<Point> pointsToBeProcessed = MODEL.getPointsToBeProcessed();
//		pointsToBeProcessed.add(curPoint);
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
		
		List<PointToBeAdded> betweenABPoints = new ArrayList<PointToBeAdded>();
		betweenABPoints.add(new PointToBeAdded(a, 0.0));
		betweenABPoints.add(new PointToBeAdded(b, 1.0));
		
		for (Edge e : MODEL.getEdges()) {
			
			//List<PointToBeAdded> betweenCDPoints = new ArrayList<PointToBeAdded>();
			
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
						
//						
//							if (!PointUtils.equals(inter, d)) {
//								//betweenCDPoints.add(new PointToBeAdded(inter, j, PointUtils.param(inter, c, d), Event.POINT, c, d));
//								// split <c, d> at inter
//							} else {
//								// add vertex at d
//							}
//							
//							if (!PointUtils.equals(inter, b)) {
//								//betweenABPoints.add(new PointToBeAdded(inter, curStroke.size()-2, PointUtils.param(inter, a, b), Event.POINT, a, b));
//								// split <a, b> at inter
//							} else {
//								// add vertex at b
//							}
						
					}
				} catch (OverlappingException ex) {
//					double aParam = PointUtils.param(a, c, d);
//					double bParam = PointUtils.param(b, c, d);
					//double dParam = PointUtils.param(d, a, b);
					
//					if ((0 <= aParam && aParam <= 1)) {
//						d;
//						// remember a for <a, b> and <c, d>
//					}
//					
//					if ((0 <= bParam && bParam <= 1)) {
//						d;
//						// remember b for <a, b> and <c, d>
//					}
					
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
			
			// insert all of the points for <c, d>
			
//			Collections.sort(betweenCDPoints, ptbaComparatorDescending);
//			for (PointToBeAdded ptba : betweenCDPoints) {
//				Point p = ptba.p;
//				int index = ptba.index;
//				double param = ptba.param;
//				Event event = ptba.e;
//				switch (event) {
//				case POINT:
//					Point cc = edgePoints.get(index);
//					Point dd = edgePoints.get(index+1);
//					assert !PointUtils.equals(p, cc);
//					assert !PointUtils.equals(p, dd);
//					assert PointUtils.intersection(p, cc, dd);
//					assert cc == ptba.a;
//					assert dd == ptba.b;
//					double tmp = PointUtils.param(p, cc, dd);
//					assert DoubleUtils.doubleEqual(tmp, param) : Math.abs(tmp - param);
//					edgePoints.add(index+1, p);
//					break;
//				}
//			}
//			betweenCDPoints.clear();
			
		} // for Edge e
		
		/*
		 * have a bunch of edges with points that is not consistent yet
		 * need to do splits and merges
		 */
		
		Collections.sort(betweenABPoints, ptbaComparator);
		
		for (int i = 0; i < betweenABPoints.size()-1; i++) {
			PointToBeAdded aa = betweenABPoints.get(i);
			PointToBeAdded bb = betweenABPoints.get(i+1);
			
			process(aa.p, bb.p);
			
//			int index = ptba.index;
//			double param = ptba.param;
//			Event event = ptba.e;
//			switch (event) {
//			case POINT:
//				Point aa = curStroke.get(index);
//				Point bb = curStroke.get(index+1);
//				assert !PointUtils.equals(p, aa);
//				assert !PointUtils.equals(p, bb);
//				assert PointUtils.intersection(p, aa, bb);
//				assert aa == ptba.a;
//				assert bb == ptba.b;
//				double tmp = PointUtils.param(p, aa, bb);
//				assert DoubleUtils.doubleEqual(tmp, param) : Math.abs(tmp - param);
//				curStroke.add(index+1, p);
//				break;
//			}
		}
		betweenABPoints.clear();
		
		//process(a, b);
		
		MODEL.checkConsistency();
		
		VIEW.repaint();
		
	}
	
//	enum Event {
//		POINT, STOP;
//	}
//	
	static class PointToBeAdded {
		
		Point p;
		
//		/*
//		 * index of 0 param point
//		 */
//		int index;
		
		/**
		 * value ranging from 0..1 measuring distance between points at index and index+1, used for sorting
		 */
		double param;
		
//		Event e;
//		
//		/*
//		 * for debugging
//		 */
//		Point a;
//		Point b;
		
		PointToBeAdded(Point p, double param) {
			assert param >= 0.0;
			assert param <= 1.0;
			this.p = p;
			//this.index = index;
			this.param = param;
//			this.e = e;
//			this.a = a;
//			this.b = b;
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
		
//		List<Point> pointsToBeProcessed = MODEL.getPointsToBeProcessed();
//		
//		List<PointF> curStroke = strokes.get(strokes.size()-1);
//		for (int i = 0; i < pointsToBeProcessed.size(); i++) {
//			curStroke.add(pointsToBeProcessed.get(i));
//		}
//		pointsToBeProcessed.clear();
		
		//strokes.clear();
		//curStroke.clear();
		curPoint = null;
		
		//VIEW.repaint();
	}
	
	void process(final Point a, final Point b) {
		
		Vertex aV = MODEL.tryFindVertex(a);
		boolean createdA = false;
		
		if (aV == null) {
			EdgeInfo info = MODEL.tryFindEdgeInfo(a);
			if (info == null) {
				aV = MODEL.createVertex(a);
				createdA = true;
			} else {
				Edge e = info.edge;
				int index = info.index;
				double param = info.param;
				aV = EdgeUtils.split(e, index, param);
			}
		}
		
		assert aV != null;
		
		//int aVEdgeCount = aV.getEdges().size();
		
		Vertex bV = MODEL.tryFindVertex(b);
		boolean createdB = false;
		
		if (bV == null) {
			EdgeInfo info = MODEL.tryFindEdgeInfo(b);
			if (info == null) {
				bV = MODEL.createVertex(b);
				createdB = true;
			} else {
				Edge e = info.edge;
				int index = info.index;
				double param = info.param;
				bV = EdgeUtils.split(e, index, param);
			}
		}
		
		assert bV != null;
		
		if (!createdA && !createdB) {
			for (Edge e : aV.getEdges()) {
				if (((e.getStart() == aV && e.getEnd() == bV) || (e.getStart() == bV && e.getEnd() == aV)) && e.getPoints().size() == 2) {
					/*
					 * both a and b are already both vertices and connected, so nothing to do here
					 */
					return;
				}
			}
		}
		
		//int bVEdgeCount = bV.getEdges().size();
		
		Edge e = MODEL.createEdge();
		List<Point> ePoints = e.getPoints();
		ePoints.add(a);
		ePoints.add(b);
		e.setStart(aV);
		e.setEnd(bV);
		
		aV.add(e);
		bV.add(e);
		
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
