package com.gutabi.deadlock.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gutabi.deadlock.model.DeadlockModel;
import com.gutabi.deadlock.model.DeadlockModel.EdgeInfo;
import com.gutabi.deadlock.model.Edge;
import com.gutabi.deadlock.model.EdgeUtils;
import com.gutabi.deadlock.model.OverlappingException;
import com.gutabi.deadlock.model.PointFUtils;
import com.gutabi.deadlock.model.Vertex;
import com.gutabi.deadlock.view.DeadlockView;

public class DeadlockController implements OnTouchListener {
	
	private DeadlockModel model;
	private DeadlockView view;
	
	private PointF curPoint;
	
	private List<List<PointF>> strokes = new ArrayList<List<PointF>>();
	
	public DeadlockController(DeadlockModel model, DeadlockView view) {
		this.model = model;
		this.view = view;
	}
	
	
	/*
	 * round a to nearest multiple of b
	 */
	static float round(float a, int b) {
		return Math.round(a / b) * b;
	}
	
	/*
	 * round coords from event to nearest multiple of:
	 */
	int roundingFactor = 60;
	
	public boolean onTouch(View ignored, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		x = round(x, roundingFactor);
		y = round(y, roundingFactor);
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d("touch", "DOWN <"+x+","+y+">");
			touchStart(x, y);
			view.invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("touch", "MOVE <"+x+","+y+">");
			touchMove(x, y);
			view.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			Log.d("touch", "UP   <"+x+","+y+">");
			touchUp();
			view.invalidate();
			break;
		}
		return true;
	}
	
	private void touchStart(float x, float y) {
		curPoint = new PointF(x, y);
		List<PointF> pointsToBeProcessed = model.getPointsToBeProcessed();
		strokes.add(new ArrayList<PointF>());
		pointsToBeProcessed.add(curPoint);
	}
	
	private void touchMove(float x, float y) {
		
		PointF a = curPoint;
		
		PointF b = new PointF(x, y);
		
		/*
		 * ignore repeated points
		 */
		
		if (PointFUtils.equals(a, b)) {
			return;
		}
		
		curPoint = b;
		List<PointF> pointsToBeProcessed = model.getPointsToBeProcessed();
		pointsToBeProcessed.add(curPoint);
		
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
		
		for (int j = 0; j < pointsToBeProcessed.size()-2; j++) {
			PointF c = pointsToBeProcessed.get(j);
			PointF d = pointsToBeProcessed.get(j+1);
			try {
				PointF inter = PointFUtils.intersection(a, b, c, d);
				if (inter != null) {
					if (!(PointFUtils.equals(inter, c) || PointFUtils.equals(inter, d))) {
						betweenABPoints.add(new PointToBeAdded(inter, j, PointFUtils.param(inter, c, d), Event.MIDDLE));
					}
					if (!(PointFUtils.equals(inter, a) || PointFUtils.equals(inter, b))) {
						betweenABPoints.add(new PointToBeAdded(inter, pointsToBeProcessed.size()-2, PointFUtils.param(inter, a, b), Event.MIDDLE));
					}
				}
			} catch (OverlappingException ex) {
				assert false;
				float cParam = PointFUtils.param(c, a, b);
				float dParam = PointFUtils.param(d, a, b);
				if ((cParam == 0 && dParam == 1) || (dParam == 0 && cParam == 1)) {
					// identical
					// a is end of stroke
					// b is start of stroke
				} else if ((0 < cParam && cParam < 1) && (0 < dParam && dParam < 1)) {
					// <a, b> completely overlaps <c, d>
					if (cParam < dParam) {
						//insert c between a and b
						// insert d between a and b
						// c is end
						// d is start
					} else {
						// insert d between a and b
						//insert c between a and b
						// d is end
						// c is start
					}
				} else if ((0 > cParam || cParam > 1) && (0 > dParam || dParam > 1)) {
					// <c, d> completely overlaps <a, b>
					if (cParam < dParam) {
						// insert a between c and d
						// insert b between c and d
						// a is end
						// b is start
					} else {
						// insert b between c and d
						// insert a between c and d
						// a is end
						// b is start
						
					}
				} else if ((0 < cParam && cParam < 1) && (0 > dParam || dParam > 1)) {
					// <a, b> overlaps c
					// insert c between a and b
					// c is end
					// b is start
				} else if ((0 > cParam || cParam > 1) && (0 < dParam && dParam < 1)) {
					// <a, b> overlaps d
					// insert d between a and b
					// c is end
					// b is start
				} else {
					assert false;
				}
			}
		}
		
		Collections.sort(betweenABPoints, ptbaComparatorDescending);
		for (PointToBeAdded ptba : betweenABPoints) {
			PointF p = ptba.p;
			int index = ptba.index;
			float param = ptba.param;
			Event event = ptba.e;
			switch (event) {
			case MIDDLE:
				PointF aa = pointsToBeProcessed.get(index);
				PointF bb = pointsToBeProcessed.get(index+1);
				assert !PointFUtils.equals(p, aa);
				assert !PointFUtils.equals(p, bb);
				assert PointFUtils.intersection(p, aa, bb);
				assert PointFUtils.param(p, aa, bb) == param;
				pointsToBeProcessed.add(index+1, p);
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
		
		for (Edge e : model.getEdges()) {
			
			List<PointToBeAdded> betweenCDPoints = new ArrayList<PointToBeAdded>();
			
			List<PointF> edgePoints = e.getPoints();
			for (int j = 0; j < edgePoints.size()-1; j++) {
				PointF c = edgePoints.get(j);
				PointF d = edgePoints.get(j+1);
				try {
					PointF inter = PointFUtils.intersection(a, b, c, d);
					if (inter != null && !(PointFUtils.equals(inter, c) || PointFUtils.equals(inter, a))) {
						if (!PointFUtils.equals(inter, d)) {
							betweenCDPoints.add(new PointToBeAdded(inter, j, PointFUtils.param(inter, c, d), Event.MIDDLE));
						}
						if (!PointFUtils.equals(inter, b)) {
							betweenABPoints.add(new PointToBeAdded(inter, pointsToBeProcessed.size()-2, PointFUtils.param(inter, a, b), Event.MIDDLE));
						}
					}
				} catch (OverlappingException ex) {
					assert false;
					float cParam = PointFUtils.param(c, a, b);
					float dParam = PointFUtils.param(d, a, b);
					if ((cParam == 0 && dParam == 1) || (dParam == 0 && cParam == 1)) {
						// identical
						// a is end of stroke
						// b is start of stroke
					} else if ((0 < cParam && cParam < 1) && (0 < dParam && dParam < 1)) {
						// <a, b> completely overlaps <c, d>
						if (cParam < dParam) {
							//insert c between a and b
							// insert d between a and b
							// c is end
							// d is start
						} else {
							// insert d between a and b
							//insert c between a and b
							// d is end
							// c is start
						}
					} else if ((0 > cParam || cParam > 1) && (0 > dParam || dParam > 1)) {
						// <c, d> completely overlaps <a, b>
						if (cParam < dParam) {
							// insert a between c and d
							// insert b between c and d
							// a is end
							// b is start
						} else {
							// insert b between c and d
							// insert a between c and d
							// a is end
							// b is start
							
						}
					} else if ((0 < cParam && cParam < 1) && (0 > dParam || dParam > 1)) {
						// <a, b> overlaps c
						// insert c between a and b
						// c is end
						// b is start
					} else if ((0 > cParam || cParam > 1) && (0 < dParam && dParam < 1)) {
						// <a, b> overlaps d
						// insert d between a and b
						// c is end
						// b is start
					} else {
						assert false;
					}
				}
			} // for edgePoint j
			
			Collections.sort(betweenCDPoints, ptbaComparatorDescending);
			for (PointToBeAdded ptba : betweenCDPoints) {
				PointF p = ptba.p;
				int index = ptba.index;
				float param = ptba.param;
				Event event = ptba.e;
				switch (event) {
				case MIDDLE:
					PointF cc = edgePoints.get(index);
					PointF dd = edgePoints.get(index+1);
					assert !PointFUtils.equals(p, cc);
					assert !PointFUtils.equals(p, dd);
					assert PointFUtils.intersection(p, cc, dd);
					assert PointFUtils.param(p, cc, dd) == param;
					edgePoints.add(index+1, p);
					break;
				}
			}
			betweenCDPoints.clear();
			
		} // for Edge e
		
		Collections.sort(betweenABPoints, ptbaComparatorDescending);
		for (PointToBeAdded ptba : betweenABPoints) {
			PointF p = ptba.p;
			int index = ptba.index;
			float param = ptba.param;
			Event event = ptba.e;
			PointF aa = pointsToBeProcessed.get(index);
			PointF bb = pointsToBeProcessed.get(index+1);
			assert !PointFUtils.equals(p, aa);
			assert !PointFUtils.equals(p, bb);
			assert PointFUtils.intersection(p, aa, bb);
			assert PointFUtils.param(p, aa, bb) == param;
			pointsToBeProcessed.add(index+1, p);
		}
		betweenABPoints.clear();
		
	}
	
	enum Event {
		MIDDLE, STARTSTROKE, STOPSTROKE;
	}
	
	static class PointToBeAdded {
		
		PointF p;
		
		/*
		 * index of 0 param point
		 */
		int index;
		
		/**
		 * value ranging from 0..1 measuring distance between points at index and index+1, used for sorting
		 */
		float param;
		
		Event e;
		
		PointToBeAdded(PointF p, int index, float param, Event e) {
			assert param > 0;
			assert param < 1;
			this.p = p;
			this.index = index;
			this.param = param;
			this.e = e;
		}
	}
	
	class PTBAComparator implements Comparator<PointToBeAdded> {
		@Override
		public int compare(PointToBeAdded x, PointToBeAdded y) {
			if (x.index < y.index) {
				return -1;
			} else if (x.index > y.index) {
				return 1;
			} else {
				//assert x == y;
				if (x.param < y.param) {
					return -1;
				} else if (x.param > y.param) {
					return 1;
				} else {
					assert x == y;
					return 0;
				}
			}
		}
	}
	
	Comparator<PointToBeAdded> ptbaComparatorDescending = Collections.reverseOrder(new PTBAComparator());
	
	private void touchUp() {
		
		List<PointF> pointsToBeProcessed = model.getPointsToBeProcessed();
		
		List<PointF> curStroke = strokes.get(strokes.size()-1);
		for (int i = 0; i < pointsToBeProcessed.size(); i++) {
			curStroke.add(pointsToBeProcessed.get(i));
		}
		pointsToBeProcessed.clear();
		
		/*
		 * each stroke consists of pairs <a, b>
		 * there are no events between <a, b>
		 * all events occur at point a or point b
		 */
		
		for (List<PointF> stroke : strokes) {
			for (int i = 0; i < stroke.size()-1; i++) {
				PointF a = stroke.get(i);
				PointF b = stroke.get(i+1);
				process(a, b);
				model.checkConsistency();
			}
		}
		
		strokes.clear();
		
		curPoint = null;
	}
	
	void process(final PointF a, final PointF b) {
		
		Vertex aV = model.tryFindVertex(a);
		
		if (aV == null) {
			EdgeInfo info = model.tryFindEdgeInfo(a);
			if (info == null) {
				aV = model.createVertex(a);
			} else {
				Edge e = info.edge;
				int index = info.index;
				aV = EdgeUtils.split(e, index, model);
			}
		}
		
		assert aV != null;
		
		//int aVEdgeCount = aV.getEdges().size();
		
		Vertex bV = model.tryFindVertex(b);
		
		if (bV == null) {
			EdgeInfo info = model.tryFindEdgeInfo(b);
			if (info == null) {
				bV = model.createVertex(b);
			} else {
				Edge e = info.edge;
				int index = info.index;
				bV = EdgeUtils.split(e, index, model);
			}
		}
		
		assert bV != null;
		
		//int bVEdgeCount = bV.getEdges().size();
		
		Edge e = model.createEdge();
		List<PointF> ePoints = e.getPoints();
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
			working = EdgeUtils.merge(aEdge, e, model);
		} else {
			working = e;
		}
		
		/*
		 * bV could have been removed if the merging of aEdge and e formed a loop (thereby removing bV in the process)
		 */
		
		if (model.tryFindVertex(b) != null) {
			List<Edge> bEdges = bV.getEdges();
			if (bEdges.size() == 2) {
				Edge bEdge;
				if (bEdges.get(0) == e) {
					bEdge = bEdges.get(1);
				} else {
					bEdge = bEdges.get(0);
				}
				EdgeUtils.merge(working, bEdge, model);
			}
		}
		
	}
	
}
