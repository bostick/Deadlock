package com.gutabi.deadlock.core.controller;

import java.util.ArrayList;
import java.util.List;
import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Vertex;

public class DeadlockController {
	
	public static DeadlockController CONTROLLER = new DeadlockController();
	
	public Point lastPointRaw;
	public List<Point> curStrokeRaw = new ArrayList<Point>();
	public List<List<Point>> allStrokes = new ArrayList<List<Point>>();
	
	public Point cameraUpperLeft = new Point(0, 0);
	
	public void inputStart(InputEvent e) {
		Point p = Point.add(e.getPoint(), cameraUpperLeft);
		lastPointRaw = p;
		curStrokeRaw.add(p);
		allStrokes.add(new ArrayList<Point>());
		allStrokes.get(allStrokes.size()-1).add(p);
	}
	
	public void inputMove(InputEvent e) {
		Point p = Point.add(e.getPoint(), cameraUpperLeft);
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
		
		for (Vertex v : MODEL.graph.getVertices()) {
			
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
		
		for (Vertex v : MODEL.graph.getVertices()) {
			
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
	
}
