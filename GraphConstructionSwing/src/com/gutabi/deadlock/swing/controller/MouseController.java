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

import com.gutabi.deadlock.swing.model.DeadlockModel.EdgeInfo;
import com.gutabi.deadlock.swing.model.Edge;
import com.gutabi.deadlock.swing.model.Vertex;
import com.gutabi.deadlock.swing.utils.DoubleUtils;
import com.gutabi.deadlock.swing.utils.EdgeUtils;
import com.gutabi.deadlock.swing.utils.OverlappingException;
import com.gutabi.deadlock.swing.utils.Point;
import com.gutabi.deadlock.swing.utils.PointUtils;

public class MouseController implements MouseListener, MouseMotionListener {
	
	private Point curPoint;
	private List<Point> curStroke;
	private List<List<Point>> strokes = new ArrayList<List<Point>>();
	
	public void init() {
		VIEW.panel.addMouseListener(this);
		VIEW.panel.addMouseMotionListener(this);
	}
	
	public void pressed(Point ev) {
		curPoint = ev;
		List<Point> pointsToBeProcessed = MODEL.getPointsToBeProcessed();
		curStroke = new ArrayList<Point>();
		curStroke.add(curPoint);
		strokes.add(curStroke);
		pointsToBeProcessed.add(curPoint);
		
		VIEW.repaint();
	}
	
	public void dragged(Point ev) {
		
		Point a = curPoint;
		
		Point b = ev;
		
		/*
		 * ignore repeated points
		 */
		
		if (PointUtils.equals(a, b)) {
			return;
		}
		
		curPoint = b;
		curStroke.add(b);
		
		{
		List<Point> pointsToBeProcessed = MODEL.getPointsToBeProcessed();
		pointsToBeProcessed.add(curPoint);
		}
		
		/*
		 * test all pairs <c, d> against <a, b>
		 * the pairs <c, d> come from existing edges, and from current motion of user
		 * 
		 * find all events in <a, b>
		 * insert points for all events
		 * 
		 * any overlapping causes a new stroke to be started
		 */
		
		/*
		 * handle intersecting and overlapping with itself first
		 */
		
		List<PointToBeAdded> betweenABPoints = new ArrayList<PointToBeAdded>();
		
		for (int j = 0; j < curStroke.size()-2; j++) {
			Point c = curStroke.get(j);
			Point d = curStroke.get(j+1);
			try {
				Point inter = PointUtils.intersection(a, b, c, d);
				if (inter != null && !(PointUtils.equals(inter, c) || PointUtils.equals(inter, a))) {
					if (!PointUtils.equals(inter, d)) {
						betweenABPoints.add(new PointToBeAdded(inter, j, PointUtils.param(inter, c, d), Event.POINT, c, d));
					}
					if (!PointUtils.equals(inter, b)) {
						betweenABPoints.add(new PointToBeAdded(inter, curStroke.size()-2, PointUtils.param(inter, a, b), Event.POINT, a, b));
					}
				}
			} catch (OverlappingException ex) {
				double cParam = PointUtils.param(c, a, b);
				double dParam = PointUtils.param(d, a, b);
				if ((cParam == 0 && dParam == 1) || (dParam == 0 && cParam == 1)) {
					// identical
					// a is end of stroke
					// b is start of stroke
					assert false;
				} else if ((0 < cParam && cParam < 1) && (0 < dParam && dParam < 1)) {
					// <a, b> completely overlaps <c, d>
					if (cParam < dParam) {
						//insert c between a and b
						// insert d between a and b
						// c is end
						// d is start
						assert false;
					} else {
						// insert d between a and b
						//insert c between a and b
						// d is end
						// c is start
						assert false;
					}
				} else if ((0 > cParam || cParam > 1) && (0 > dParam || dParam > 1)) {
					// <c, d> completely overlaps <a, b>
					if (cParam < dParam) {
						// insert a between c and d
						// insert b between c and d
						// a is end
						// b is start
						assert false;
					} else {
						// insert b between c and d
						// insert a between c and d
						// a is end
						// b is start
						assert false;
					}
				} else if ((0 < cParam && cParam < 1) && (0 > dParam || dParam > 1)) {
					// <a, b> overlaps c
					// insert c between a and b
					// c is end
					// b is start
					assert false;
				} else if ((0 > cParam || cParam > 1) && (0 < dParam && dParam < 1)) {
					// <a, b> overlaps d
					// insert d between a and b
					// c is end
					// b is start
					assert false;
				} else {
					assert false;
				}
			}
		} // PointF j
		
		Collections.sort(betweenABPoints, ptbaComparatorDescending);
		for (PointToBeAdded ptba : betweenABPoints) {
			Point p = ptba.p;
			int index = ptba.index;
			double param = ptba.param;
			Event event = ptba.e;
			switch (event) {
			case POINT:
				Point aa = curStroke.get(index);
				Point bb = curStroke.get(index+1);
				assert !PointUtils.equals(p, aa);
				assert !PointUtils.equals(p, bb);
				assert PointUtils.intersection(p, aa, bb);
				assert aa == ptba.a;
				assert bb == ptba.b;
				double tmp = PointUtils.param(p, aa, bb);
				assert DoubleUtils.doubleEqual(tmp, param) : Math.abs(tmp - param);
				curStroke.add(index+1, p);
				break;
			}
		}
		betweenABPoints.clear();
		
		/*
		 * now handle intersecting and overlapping with existing edges
		 */
		
		/*
		 * collect between-events for edge e (<a, b> could be going between middle points <c, d>)
		 * insert them later
		 */
		
		for (Edge e : MODEL.getEdges()) {
			
			List<PointToBeAdded> betweenCDPoints = new ArrayList<PointToBeAdded>();
			
			List<Point> edgePoints = e.getPoints();
			for (int j = 0; j < edgePoints.size()-1; j++) {
				Point c = edgePoints.get(j);
				Point d = edgePoints.get(j+1);
				try {
					Point inter = PointUtils.intersection(a, b, c, d);
					if (inter != null && !(PointUtils.equals(inter, c) || PointUtils.equals(inter, a))) {
						if (!PointUtils.equals(inter, d)) {
							betweenCDPoints.add(new PointToBeAdded(inter, j, PointUtils.param(inter, c, d), Event.POINT, c, d));
						}
						if (!PointUtils.equals(inter, b)) {
							betweenABPoints.add(new PointToBeAdded(inter, curStroke.size()-2, PointUtils.param(inter, a, b), Event.POINT, a, b));
						}
					}
				} catch (OverlappingException ex) {
					double cParam = PointUtils.param(c, a, b);
					double dParam = PointUtils.param(d, a, b);
					if ((cParam == 0 && dParam == 1) || (dParam == 0 && cParam == 1)) {
						// identical
						// a is end of stroke
						// b is start of stroke
						assert false;
						//strokes.get(strokes.size()-1);
					} else if ((0 < cParam && cParam < 1) && (0 < dParam && dParam < 1)) {
						// <a, b> completely overlaps <c, d>
						if (cParam < dParam) {
							//insert c between a and b
							// insert d between a and b
							// c is end
							// d is start
							assert false;
						} else {
							// insert d between a and b
							//insert c between a and b
							// d is end
							// c is start
							assert false;
						}
					} else if ((0 > cParam || cParam > 1) && (0 > dParam || dParam > 1)) {
						// <c, d> completely overlaps <a, b>
						if (cParam < dParam) {
							// insert a between c and d
							// insert b between c and d
							// a is end
							// b is start
							assert false;
						} else {
							// insert b between c and d
							// insert a between c and d
							// a is end
							// b is start
							assert false;
						}
					} else if ((0 < cParam && cParam < 1) && (0 > dParam || dParam > 1)) {
						// <a, b> overlaps c
						// insert c between a and b
						// c is end
						// b is start
						assert false;
					} else if ((0 > cParam || cParam > 1) && (0 < dParam && dParam < 1)) {
						// <a, b> overlaps d
						// insert d between a and b
						// c is end
						// b is start
						assert false;
					} else {
						assert false;
					}
				}
			} // for edgePoint j
			
			Collections.sort(betweenCDPoints, ptbaComparatorDescending);
			for (PointToBeAdded ptba : betweenCDPoints) {
				Point p = ptba.p;
				int index = ptba.index;
				double param = ptba.param;
				Event event = ptba.e;
				switch (event) {
				case POINT:
					Point cc = edgePoints.get(index);
					Point dd = edgePoints.get(index+1);
					assert !PointUtils.equals(p, cc);
					assert !PointUtils.equals(p, dd);
					assert PointUtils.intersection(p, cc, dd);
					assert cc == ptba.a;
					assert dd == ptba.b;
					double tmp = PointUtils.param(p, cc, dd);
					assert DoubleUtils.doubleEqual(tmp, param) : Math.abs(tmp - param);
					edgePoints.add(index+1, p);
					break;
				}
			}
			betweenCDPoints.clear();
			
		} // for Edge e
		
		Collections.sort(betweenABPoints, ptbaComparatorDescending);
		for (PointToBeAdded ptba : betweenABPoints) {
			Point p = ptba.p;
			int index = ptba.index;
			double param = ptba.param;
			Event event = ptba.e;
			switch (event) {
			case POINT:
				Point aa = curStroke.get(index);
				Point bb = curStroke.get(index+1);
				assert !PointUtils.equals(p, aa);
				assert !PointUtils.equals(p, bb);
				assert PointUtils.intersection(p, aa, bb);
				assert aa == ptba.a;
				assert bb == ptba.b;
				double tmp = PointUtils.param(p, aa, bb);
				assert DoubleUtils.doubleEqual(tmp, param) : Math.abs(tmp - param);
				curStroke.add(index+1, p);
				break;
			}
		}
		betweenABPoints.clear();
		
		VIEW.repaint();
		
	}
	
	enum Event {
		POINT, STOP;
	}
	
	static class PointToBeAdded {
		
		Point p;
		
		/*
		 * index of 0 param point
		 */
		int index;
		
		/**
		 * value ranging from 0..1 measuring distance between points at index and index+1, used for sorting
		 */
		double param;
		
		Event e;
		
		/*
		 * for debugging
		 */
		Point a;
		Point b;
		
		PointToBeAdded(Point p, int index, double param, Event e, Point a, Point b) {
			assert param > 0;
			assert param < 1;
			this.p = p;
			this.index = index;
			this.param = param;
			this.e = e;
			this.a = a;
			this.b = b;
		}
		
		public String toString() {
			return "PTBA: " + p + " " + index + " " + param + " " + e;
		}
		
	}
	
	class PTBAComparator implements Comparator<PointToBeAdded> {
		@Override
		public int compare(PointToBeAdded a, PointToBeAdded b) {
			if (a.index < b.index) {
				return -1;
			} else if (a.index > b.index) {
				return 1;
			} else {
				//assert a == b;
				if (a.param < b.param) {
					return -1;
				} else if (a.param > b.param) {
					return 1;
				} else {
					assert a == b;
					return 0;
				}
			}
		}
	}
	
	Comparator<PointToBeAdded> ptbaComparatorDescending = Collections.reverseOrder(new PTBAComparator());
	
	public void released() {
		
		List<Point> pointsToBeProcessed = MODEL.getPointsToBeProcessed();
//		
//		List<PointF> curStroke = strokes.get(strokes.size()-1);
//		for (int i = 0; i < pointsToBeProcessed.size(); i++) {
//			curStroke.add(pointsToBeProcessed.get(i));
//		}
		pointsToBeProcessed.clear();
		
		/*
		 * each stroke consists of pairs <a, b>
		 * there are no events between <a, b>
		 * all events occur at point a or point b
		 */
		
		for (List<Point> stroke : strokes) {
			for (int i = 0; i < stroke.size()-1; i++) {
				Point a = stroke.get(i);
				Point b = stroke.get(i+1);
				process(a, b);
				MODEL.checkConsistency();
			}
		}
		
		strokes.clear();
		
		curPoint = null;
		
		VIEW.repaint();
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
				aV = EdgeUtils.split(e, index);
			}
		}
		
		assert aV != null;
		
		//int aVEdgeCount = aV.getEdges().size();
		
		Vertex bV = MODEL.tryFindVertex(b);
		
		if (bV == null) {
			EdgeInfo info = MODEL.tryFindEdgeInfo(b);
			if (info == null) {
				bV = MODEL.createVertex(b);
			} else {
				Edge e = info.edge;
				int index = info.index;
				bV = EdgeUtils.split(e, index);
			}
		}
		
		assert bV != null;
		
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
