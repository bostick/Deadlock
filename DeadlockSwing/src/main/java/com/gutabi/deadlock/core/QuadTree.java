package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
	
	ArrayList<SegmentIndex> segIndices = new ArrayList<SegmentIndex>();
	
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
		segIndices.add(new SegmentIndex(e, i));
	}

	private void removeSegment(Edge e, int i) {
		SegmentIndex toRemove = null;
		for (SegmentIndex si : segIndices) {
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
	
	
	public class SegmentIndex {
		public final Edge edge;
		public final int index;
		public final Point c;
		public final Point d;
		private SegmentIndex(Edge e, int index) {
			this.edge = e;
			this.index = index;
			this.c = e.getPoint(index);
			this.d = e.getPoint(index+1);
		}
	}
	
	public List<SegmentIndex> findAllSegments(DPoint a, DPoint b) {
		Point ul = new Point((int)Math.floor(Math.min(a.x, b.x)), (int)Math.floor(Math.min(a.y, b.y)));
		Point br = new Point((int)Math.ceil(Math.max(a.x, b.x)), (int)Math.ceil(Math.max(a.y, b.y)));
		return findAllSegments(ul, br);
	}
	
	public List<SegmentIndex> findAllSegments(Point a, Point b) {
		assert !Point.equals(a, b);
		DPoint ul = new DPoint(Math.min(a.x, b.x), Math.min(a.y, b.y));
		DPoint ur = new DPoint(Math.max(a.x, b.x), Math.min(a.y, b.y));
		DPoint bl = new DPoint(Math.min(a.x, b.x), Math.max(a.y, b.y));
		DPoint br = new DPoint(Math.max(a.x, b.x), Math.max(a.y, b.y));
		List<SegmentIndex> l = new ArrayList<SegmentIndex>();
		for (SegmentIndex si : segIndices) {
			Edge e = si.edge;
			int i = si.index;
			DPoint c = new DPoint(e.getPoint(i));
			DPoint d = new DPoint(e.getPoint(i+1));
			try {
				if ((ul.x <= c.x && c.x <= br.x && ul.y <= c.y && c.y <= br.y) || (ul.x <= d.x && d.x <= br.x && ul.y <= d.y && d.y <= br.y) ||
						!DPoint.equals(ul, ur) && Point.intersection(ul, ur, c, d) != null ||
						!DPoint.equals(ul, bl) && Point.intersection(ul, bl, c, d) != null ||
						!DPoint.equals(ur, br) && Point.intersection(ur, br, c, d) != null ||
						!DPoint.equals(bl, br) && Point.intersection(bl, br, c, d) != null) {
					l.add(new SegmentIndex(e, i));
				}
			} catch (OverlappingException e1) {
				l.add(new SegmentIndex(e, i));
			}
		}
		return l;
	}
	
	public List<SegmentIndex> findAllSegments(Point a) {
		List<SegmentIndex> l = new ArrayList<SegmentIndex>();
		for (SegmentIndex si : segIndices) {
			Edge e = si.edge;
			int i = si.index;
		//for (int i = 0; i < e.size()-1; i++) {
			Point c = e.getPoint(i);
			Point d = e.getPoint(i+1);
			if (Point.intersect(a, c, d)) {
				l.add(new SegmentIndex(e, i));
			}
		//}
		}
		return l;
	}
	
	public SegmentIndex findSegment(Point a, Point b) {
		for (SegmentIndex in : findAllSegments(a, b)) {
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
	 * <c, d>
	 */
	public static IntersectionInfo closestDPoint(DPoint b, SegmentIndex si) {
		Point c = si.c;
		Point d = si.d;
		if (Point.equalsD(b, c)) {
			return new IntersectionInfo(si, 0.0);
		}
		if (Point.equalsD(b, d)) {
			return new IntersectionInfo(si, 1.0);
		}
		if (Point.equals(c, d)) {
			throw new IllegalArgumentException("c equals d");
		}
		double xbc = b.x - c.x;
		int xdc = d.x - c.x;
		double ybc = b.y - c.y;
		int ydc = d.y - c.y;
		int denom = xdc * xdc + ydc * ydc;
		assert denom != 0;
		double u = ((double)(xbc * xdc + ybc * ydc)) / ((double)denom);
		if (u <= 0.0) {
			return new IntersectionInfo(si, 0.0);
		} else if (u >= 1.0) {
			return new IntersectionInfo(si, 1.0);
		} else {
			return new IntersectionInfo(si, u);
		}
	}
	
	public IntersectionInfo findClosestSegment(DPoint a) {
		IntersectionInfo best = null;
		for (SegmentIndex si : segIndices) {
			IntersectionInfo closest = closestDPoint(a, si);
			if (best == null) {
				best = closest;
			} else if (Point.dist(a, closest.point) < Point.dist(a, best.point)) {
				best = closest;
			}
		}
		return best;
	}
}
