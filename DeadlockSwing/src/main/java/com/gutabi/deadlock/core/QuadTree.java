package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
	
	private ArrayList<Segment> segIndices = new ArrayList<Segment>();
	
	public void addEdge(Edge e) {
		for (int i = 0; i < e.size()-1; i++) {
			segIndices.add(new Segment(e, i));
		}
	}
	
	public void removeEdge(Edge e) {
		List<Segment> toRemove = new ArrayList<Segment>();
		for (Segment si : segIndices) {
			if (si.edge == e) {
				toRemove.add(si);
			}
		}
		for (Segment si : toRemove) {
			segIndices.remove(si);
		}
	}
	
	public List<Segment> getAllSegments() {
		List<Segment> l = new ArrayList<Segment>();
		for (Segment si : segIndices) {
			Edge e = si.edge;
			int i = si.index;
			l.add(new Segment(e, i));
		}
		return l;
	}
	
	/**
	 * find closest position on <c, d> to the point b
	 */
	private static double closestParam(Point b, Segment si) {
		Point c = si.start;
		Point d = si.end;
		if (b.equals(c)) {
			return 0.0;
		}
		if (b.equals(d)) {
			return 1.0;
		}
		if (c.equals(d)) {
			throw new IllegalArgumentException("c equals d");
		}
		
		double u = Point.u(c, b, d);
		if (DMath.lessThanEquals(u, 0.0)) {
			return 0.0;
		} else if (DMath.greaterThanEquals(u, 1.0)) {
			return 1.0;
		} else {
			return u;
		}
	}
	
	/**
	 * find closest edge position to point a
	 */
	public EdgePosition findClosestEdgePosition(Point a, Point anchor, double radius) {
		
		Segment bestSegment = null;
		double bestParam = -1;
		Point bestPoint = null;
		
		/*
		 * test the point a against all segments <c, d>
		 */
		for (Segment si : segIndices) {
//			Edge e = si.edge;
			double closest = closestParam(a, si);
			Point ep = si.getPoint(closest);
			if (anchor != null && a.equals(anchor) && ep.equals(anchor)) {
				/*
				 * if a equals anchor, and exactly on the edge, then ignore (a segment is starting on the edge)
				 */
				continue;
			}
			double dist = Point.distance(a, ep);
			if (DMath.lessThanEquals(dist, radius + Edge.ROAD_RADIUS) && (anchor == null || a.equals(anchor) || dist < Point.distance(anchor, ep))) {
				if (bestSegment == null) {
					bestSegment = si;
					bestParam = closest;
					bestPoint = ep;
				} else if (Point.distance(a, ep) < Point.distance(a, bestPoint)) {
					bestSegment = si;
					bestParam = closest;
					bestPoint = ep;
				}
			}
		}
		
		if (bestSegment != null) {
			if (DMath.equals(bestParam, 1.0)) {
				if (bestSegment.edge.size() > bestSegment.index+2) {
					return new EdgePosition(bestSegment.edge, bestSegment.index+1, 0.0);
				} else {
					/*
					 * this is really the end of the edge, the vertex, so return null
					 */
					return null;
				}
			} else if (DMath.equals(bestParam, 0.0) && bestSegment.index == 0) {
				/*
				 * this is really the start of the edge, the vertex, so return null
				 */
				return null;
			} else {
				return new EdgePosition(bestSegment.edge, bestSegment.index, bestParam);
			}
		} else {
			return null;
		}
	}

}
