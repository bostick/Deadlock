package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
	
	private ArrayList<Segment> segIndices = new ArrayList<Segment>();
	
	public void addEdge(Edge e) {
		for (int i = 0; i < e.size()-1; i++) {
			addSegment(e, i);
		}
	}
	
	public void removeEdge(Edge e) {
		for (int i = 0; i < e.size()-1; i++) {
			removeSegment(e, i);
		}
	}
	
	private void addSegment(Edge e, int i) {
		segIndices.add(new Segment(e, i));
	}

	private void removeSegment(Edge e, int i) {
		Segment toRemove = null;
		for (Segment si : segIndices) {
			if (si.edge == e && si.index == i) {
				toRemove = si;
			}
		}
		if (toRemove != null) {
			segIndices.remove(toRemove);
		} else {
			assert false;
		}
	}
	
	public void clear() {
		segIndices.clear();
	}
	
	public List<Segment> findAllSegments(Point a, Point b) {
		return findAllSegments(a, b, 0);
	}
	
	public List<Segment> findAllSegments(Point a, Point b, double radius) {
		assert !a.equals(b);
//		Point ul = new Point(Math.min(a.getX(), b.getX())-radius, Math.min(a.getY(), b.getY())-radius);
//		Point ur = new Point(Math.max(a.getX(), b.getX())+radius, Math.min(a.getY(), b.getY())-radius);
//		Point bl = new Point(Math.min(a.getX(), b.getX())-radius, Math.max(a.getY(), b.getY())+radius);
//		Point br = new Point(Math.max(a.getX(), b.getX())+radius, Math.max(a.getY(), b.getY())+radius);
		List<Segment> l = new ArrayList<Segment>();
		for (Segment si : segIndices) {
			Edge e = si.edge;
			int i = si.index;
			l.add(new Segment(e, i));
//			Point c = e.getPoint(i);
//			Point d = e.getPoint(i+1);
//			try {
//				if ((ul.getX() <= c.getX() && c.getX() <= br.getX() && ul.getY() <= c.getY() && c.getY() <= br.getY()) || (ul.getX() <= d.getX() && d.getX() <= br.getX() && ul.getY() <= d.getY() && d.getY() <= br.getY()) ||
//						!Point.equals(ul, ur) && Point.intersection(ul, ur, c, d) != null ||
//						!Point.equals(ul, bl) && Point.intersection(ul, bl, c, d) != null ||
//						!Point.equals(ur, br) && Point.intersection(ur, br, c, d) != null ||
//						!Point.equals(bl, br) && Point.intersection(bl, br, c, d) != null) {
//					l.add(new Segment(e, i));
//				}
//			} catch (OverlappingException e1) {
//				l.add(new Segment(e, i));
//			}
		}
		return l;
	}
	
	public List<Segment> findAllSegments(Point a) {
		List<Segment> l = new ArrayList<Segment>();
		for (Segment si : segIndices) {
			Edge e = si.edge;
			int i = si.index;
			l.add(new Segment(e, i));
		}
		return l;
	}
	
	public Segment findSegment(Point a, Point b) {
		for (Segment in : findAllSegments(a, b)) {
			Edge e = in.edge;
			Point c = e.getPoint(in.index);
			Point d = e.getPoint(in.index+1);
			if (a.equals(c) && b.equals(d)) {
				return in;
			}
		}
		throw new IllegalArgumentException("segment not found");
	}
	
	/**
	 * find closest position on <c, d> to the point b
	 */
	public static EdgePosition closestEdgePosition(Point b, Segment si) {
		Point c = si.start;
		Point d = si.end;
		if (b.equals(c)) {
			if (si.index > 0) {
				return new EdgePosition(si.edge, si.index, 0.0, null);
			} else {
				throw new PositionException(b, c, d);
			}
		}
		if (b.equals(d)) {
			throw new PositionException(b, c, d);
		}
		if (c.equals(d)) {
			throw new IllegalArgumentException("c equals d");
		}
		
		double u = Point.u(c, b, d);
		if (DMath.lessThanEquals(u, 0.0)) {
			if (si.index > 0) {
				return new EdgePosition(si.edge, si.index, 0.0, null);
			} else {
				throw new PositionException(b, c, d);
			}
		} else if (DMath.greaterThanEquals(u, 1.0)) {
			throw new PositionException(b, c, d);
		} else {
			return new EdgePosition(si.edge, si.index, u, null);
		}
	}
	
	/**
	 * find closest edge position to point a
	 */
	public EdgePosition findClosestEdgePosition(Point a, Point anchor, double radius) {
		EdgePosition best = null;
		PositionException ex = null;
		
		/*
		 * test the point a against all segments <c, d>
		 */
		for (Segment si : segIndices) {
			try {
				EdgePosition closest = closestEdgePosition(a, si);
				Point ep = closest.getPoint();
				if (anchor != null && a.equals(anchor) && ep.equals(anchor)) {
					/*
					 * if a equals anchor, and exactly on the edge, then ignore (a segment is starting on the edge)
					 */
					continue;
				}
				double dist = Point.distance(a, ep);
				if (DMath.lessThanEquals(dist, radius) && (anchor == null || a.equals(anchor) || dist < Point.distance(anchor, ep))) {
					if (best == null) {
						best = closest;
					} else if (Point.distance(a, ep) < Point.distance(a, best.getPoint())) {
						best = closest;
					}
					if (ex != null) {
						if (Point.distance(a, best.getPoint()) < Point.distance(a, ex.getPoint())) {
							ex = null;
						}
					}
				}
			} catch (PositionException newEx) {
				if (!a.equals(newEx.getPoint()) && Point.distance(a, newEx.getPoint()) < radius && (anchor == null || Point.distance(anchor, newEx.getPoint()) > radius)) {
					if (ex == null) {
						ex = newEx;
					} else if (Point.distance(a, newEx.getPoint()) < Point.distance(a, ex.getPoint())) {
						ex = newEx;
					}
				}
			}
		}
		if (ex != null && best != null && Point.distance(a, ex.getPoint()) < Point.distance(a, best.getPoint())) {
			throw ex;
		}
		
		return best;
	}

}
