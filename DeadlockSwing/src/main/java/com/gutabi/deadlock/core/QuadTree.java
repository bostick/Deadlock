package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

import static com.gutabi.deadlock.core.DMath.doubleEquals;

public class QuadTree {
	
	ArrayList<Segment> segIndices = new ArrayList<Segment>();
	
	public void addEdge(Edge e) {
		for (int i = 0; i < e.size()-1; i++) {
//			Point a = e.getPoint(i);
//			Point b = e.getPoint(i+1);
			addSegment(e, i);
		}
	}
	
	public void removeEdge(Edge e) {
		for (int i = 0; i < e.size()-1; i++) {
//			Point a = e.getPoint(i);
//			Point b = e.getPoint(i+1);
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
	
//	public List<SegmentIndex> findAllSegments(Point a, Point b) {
//		Point ul = new Point((int)Math.floor(Math.min(a.getX(), b.getX())), (int)Math.floor(Math.min(a.getY(), b.getY())));
//		Point br = new Point((int)Math.ceil(Math.max(a.getX(), b.getX())), (int)Math.ceil(Math.max(a.getY(), b.getY())));
//		return findAllSegments(ul, br);
//	}
	
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
		//for (int i = 0; i < e.size()-1; i++) {
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			if (Point.intersect(a, c, d)) {
				l.add(new Segment(e, i));
			}
		//}
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
	public static Position closestPosition(Point b, Segment si) {
		Point c = si.start;
		Point d = si.end;
		if (Point.equals(b, c)) {
			return new Position(si, 0.0);
		}
		if (Point.equals(b, d)) {
			return new Position(si, 1.0);
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
			return new Position(si, 0.0);
		} else if (u >= 1.0) {
			return new Position(si, 1.0);
		} else {
			return new Position(si, u);
		}
	}
	
	/**
	 * find closest existing position to point a
	 */
	public Position findClosestPosition(Point a) {
		Position best = null;
		for (Segment si : segIndices) {
			Position closest = closestPosition(a, si);
			if (best == null) {
				best = closest;
			} else if (Point.dist(a, closest.point) < Point.dist(a, best.point)) {
				best = closest;
			}
		}
		return best;
	}
}
