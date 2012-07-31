package com.gutabi.deadlock.core.controller;

import java.util.ArrayList;
import java.util.List;
import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Vertex;
import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;

public class DeadlockController {
	
	public static DeadlockController CONTROLLER = new DeadlockController();
	
	public Point lastPointRaw;
	public List<Point> curStrokeRaw = new ArrayList<Point>();
	public List<List<Point>> allStrokes = new ArrayList<List<Point>>();
	
	public void inputStart(InputEvent e) {
		Point p;
		p = e.getPoint();
		//p = Point.add(p, VIEW.cameraUpperLeft);
		p = new Point((int)(p.x * 1/VIEW.zoom), (int)(p.y * 1/VIEW.zoom));
		p = Point.add(p, VIEW.cameraUpperLeft);
		
		lastPointRaw = p;
		curStrokeRaw.add(p);
		allStrokes.add(new ArrayList<Point>());
		allStrokes.get(allStrokes.size()-1).add(p);
	}
	
	public void inputMove(InputEvent e) {
		Point p;
		p = e.getPoint();
		//p = Point.add(p, VIEW.cameraUpperLeft);
		p = new Point((int)(p.x * 1/VIEW.zoom), (int)(p.y * 1/VIEW.zoom));
		p = Point.add(p, VIEW.cameraUpperLeft);
		
		if (!p.equals(lastPointRaw)) {
			curStrokeRaw.add(p);
			lastPointRaw = p;
			allStrokes.get(allStrokes.size()-1).add(p);
		}
	}
	
	public void inputEnd() {
		inputEnd(true);
	}
	
	public void inputEnd(boolean massage) {
		List<Point> curStroke;
		if (massage) {
			curStroke = massage(curStrokeRaw);
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
		if ((!last.equals(first)) && Point.dist(last, first) * VIEW.zoom <= 20.0) {
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
		
		Vertex firstBest = null;
		
		for (Vertex v : MODEL.graph.getVertices()) {
			
			Point vp = v.getPoint();
			
			if ((!first.equals(vp)) && Point.dist(first, vp) * VIEW.zoom <= 40.0) {
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
				if (!adj.get(1).equals(firstBest.getPoint())) {
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
			
			if ((!last.equals(vp)) && Point.dist(last, vp) * VIEW.zoom <= 40.0) {
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
				if (!adj.get(s-2).equals(lastBest.getPoint())) {
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
