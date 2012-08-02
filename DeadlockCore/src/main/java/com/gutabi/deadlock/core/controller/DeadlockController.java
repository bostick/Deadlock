package com.gutabi.deadlock.core.controller;

import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.core.Point;
import com.gutabi.core.Vertex;

public class DeadlockController {
	
	public static DeadlockController CONTROLLER = new DeadlockController();
	
	public Point lastPointRaw;
	public List<Point> curStrokeRaw = new ArrayList<Point>();
	public List<List<Point>> allStrokes = new ArrayList<List<Point>>();
	
	public MassageStrategy strat;
	
	public void inputStart(InputEvent e) {
		Point p;
		p = e.getPoint();
		//p = Point.add(p, VIEW.cameraUpperLeft);
		p = new Point((int)(p.x * 1/VIEW.getZoom()), (int)(p.y * 1/VIEW.getZoom()));
		p = Point.add(p, VIEW.viewLoc);
		
		lastPointRaw = p;
		curStrokeRaw.add(p);
		allStrokes.add(new ArrayList<Point>());
		allStrokes.get(allStrokes.size()-1).add(p);
	}
	
	public void inputMove(InputEvent e) {
		Point p;
		p = e.getPoint();
		//p = Point.add(p, VIEW.cameraUpperLeft);
		p = new Point((int)(p.x * 1/VIEW.getZoom()), (int)(p.y * 1/VIEW.getZoom()));
		p = Point.add(p, VIEW.viewLoc);
		
		if (!Point.equals(p, lastPointRaw)) {
			curStrokeRaw.add(p);
			lastPointRaw = p;
			allStrokes.get(allStrokes.size()-1).add(p);
		}
	}
	
	public void inputEnd() {
		List<Point> curStroke = null;
		if (strat != null) {
			switch (strat) {
			case STRATEGY1:
				curStroke = massageStrategy1(curStrokeRaw);
				break;
			}
		} else {
			curStroke = curStrokeRaw;
		}
		for (int i = 0; i < curStroke.size()-1; i++) {
			MODEL.graph.addStroke(curStroke.get(i), curStroke.get(i+1));
		}
		assert MODEL.graph.checkConsistency();
		lastPointRaw = null;
		curStrokeRaw.clear();
	}
	
	/*
	 * only adjust to closest vertices
	 */
	private List<Point> massageStrategy1(List<Point> raw) {
		
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
		if ((!Point.equals(last, first)) && Point.dist(last, first) * VIEW.getZoom() <= 20.0) {
			/*
			 * maintain invariant that there are no contiguous, equal points
			 */
			if (!Point.equals(raw.get(s-2), first)) {
				adj.set(s-1, first);
				last = first;
			} else {
				adj.remove(s-1);
				s = s-1;
				last = raw.get(s-1);
			}
		}
		
		Vertex firstBest = null;
		
		for (Vertex v : MODEL.graph.getVertices()) {
			
			Point vp = v.getPoint();
			
			if ((!Point.equals(first, vp)) && Point.dist(first, vp) * VIEW.getZoom() <= 40.0) {
				if (firstBest == null) {
					firstBest = v;
				} else if (Point.dist(first, vp) < Point.dist(first, firstBest.getPoint())) {
					firstBest = v;
				}
			}
			
		}
		
		if (firstBest != null) {
			
			if (adj.size() == 1) {
				adj.set(0, firstBest.getPoint());
			} else {
				if (!Point.equals(adj.get(1), firstBest.getPoint())) {
					adj.set(0, firstBest.getPoint());
					first = adj.get(0);
				} else {
					adj.remove(0);
					s = s-1;
					first = adj.get(0);
				}
			}
			
		}
		
		Vertex lastBest = null;
		
		for (Vertex v : MODEL.graph.getVertices()) {
			
			Point vp = v.getPoint();
			
			if ((!Point.equals(last, vp)) && Point.dist(last, vp) * VIEW.getZoom() <= 40.0) {
				if (lastBest == null) {
					lastBest = v;
				} else if (Point.dist(last, vp) < Point.dist(last, lastBest.getPoint())) {
					lastBest = v;
				}
			}
		}
		
		/*
		 * if firstBest and lastBest are both non-null and equal, then don't also connect the last to the same vertex, it looks weird
		 */
		if (lastBest != null && lastBest != firstBest) {
			
			if (adj.size() == 1) {
				adj.set(s-1, lastBest.getPoint());
			} else {
				if (!Point.equals(adj.get(s-2), lastBest.getPoint())) {
					adj.set(s-1, lastBest.getPoint());
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
	
}
