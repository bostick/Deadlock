package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.core.DMath.doubleEquals;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
	
	ArrayList<Segment> segIndices = new ArrayList<Segment>();
	
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
		assert !Point.equals(a, b);
		Point ul = new Point(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()));
		Point ur = new Point(Math.max(a.getX(), b.getX()), Math.min(a.getY(), b.getY()));
		Point bl = new Point(Math.min(a.getX(), b.getX()), Math.max(a.getY(), b.getY()));
		Point br = new Point(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()));
		List<Segment> l = new ArrayList<Segment>();
		for (Segment si : segIndices) {
			Edge e = si.edge;
			int i = si.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			try {
				if ((ul.getX() <= c.getX() && c.getX() <= br.getX() && ul.getY() <= c.getY() && c.getY() <= br.getY()) || (ul.getX() <= d.getX() && d.getX() <= br.getX() && ul.getY() <= d.getY() && d.getY() <= br.getY()) ||
						!Point.equals(ul, ur) && Point.intersection(ul, ur, c, d) != null ||
						!Point.equals(ul, bl) && Point.intersection(ul, bl, c, d) != null ||
						!Point.equals(ur, br) && Point.intersection(ur, br, c, d) != null ||
						!Point.equals(bl, br) && Point.intersection(bl, br, c, d) != null) {
					l.add(new Segment(e, i));
				}
			} catch (OverlappingException e1) {
				l.add(new Segment(e, i));
			}
		}
		return l;
	}
	
	public List<Segment> findAllSegments(Point a) {
		List<Segment> l = new ArrayList<Segment>();
		for (Segment si : segIndices) {
			Edge e = si.edge;
			int i = si.index;
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			if (Point.intersect(a, c, d)) {
				l.add(new Segment(e, i));
			}
		}
		return l;
	}
	
	public Segment findSegment(Point a, Point b) {
		for (Segment in : findAllSegments(a, b)) {
			Edge e = in.edge;
			Point c = e.getPoint(in.index);
			Point d = e.getPoint(in.index+1);
			if (Point.equals(a, c) && Point.equals(b, d)) {
				return in;
			}
		}
		throw new IllegalArgumentException("segment not found");
	}
	
	/**
	 * find closest position on <c, d> to the point b
	 */
	public static EdgePosition closestPosition(Point b, Segment si) {
		Point c = si.start;
		Point d = si.end;
		if (Point.equals(b, c)) {
			if (si.index > 0) {
				return new EdgePosition(si, 0.0, 0);
			} else {
				throw new PositionException(c);
			}
		}
		if (Point.equals(b, d)) {
			throw new PositionException(d);
		}
		if (Point.equals(c, d)) {
			throw new IllegalArgumentException("c equals d");
		}
		double xbc = b.getX() - c.getX();
		double xdc = d.getX() - c.getX();
		double ybc = b.getY() - c.getY();
		double ydc = d.getY() - c.getY();
		double denom = xdc * xdc + ydc * ydc;
		assert !doubleEquals(denom, 0.0);
		double u = (xbc * xdc + ybc * ydc) / denom;
		if (u <= 0.0) {
			if (si.index > 0) {
				return new EdgePosition(si, 0.0, 0);
			} else {
				throw new PositionException(c);
			}
		} else if (u >= 1.0) {
			throw new PositionException(d);
		} else {
			return new EdgePosition(si, u, 0);
		}
	}
	
	/**
	 * find closest existing position to point a
	 */
	public EdgePosition findClosestEdgePosition(Point a) {
		EdgePosition best = null;
		PositionException e = null;
		for (Segment si : segIndices) {
			try {
				EdgePosition closest = closestPosition(a, si);
				if (best == null) {
					best = closest;
				} else if (Point.dist(a, closest.getPoint()) < Point.dist(a, best.getPoint())) {
					best = closest;
				}
				if (e != null) {
					if (Point.dist(a, best.getPoint()) < Point.dist(a, e.getPoint())) {
						e = null;
					}
				}
			} catch (PositionException ex) {
				if (e == null) {
					e = ex;
				} else if (Point.dist(a, ex.getPoint()) < Point.dist(a, e.getPoint())) {
					e = ex;
				}
			}
		}
		if (e != null && best != null && Point.dist(a, e.getPoint()) < Point.dist(a, best.getPoint())) {
			throw e;
		}
		return best;
	}
}
