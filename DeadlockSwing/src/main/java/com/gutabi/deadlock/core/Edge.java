package com.gutabi.deadlock.core;

import java.util.List;



public final class Edge {
		
	private final Point[] pts;
	private final Vertex start;
	private final Vertex end;
	
	private final double totalLength;
	
	/*
	 * both vs could be null (stand-alone loop) or both could be a vertex (shared with other edges)
	 */
	private final boolean loop;
	
	private boolean removed = false;
	
	public Edge(Vertex start, Vertex end, Point... pts) {
		this.start = start;
		this.end = end;
		loop = (start == end);
		this.pts = pts;
		
		double l = 0.0;
		for (int i = 0; i < pts.length-1; i++) {
			l += Point.dist(pts[i], pts[i+1]);
		}
		totalLength = l;
		
		//assert totalLength > 10;
		
		check();
	}
	
	public Edge copy() {
		if (removed) {
			throw new IllegalStateException();
		}
		return new Edge(start, end, pts);
	}
	
	public String toString() {
		return "E n=" + pts.length + " " + start + " " + end;
	}
	
	public boolean isLoop() {
		return loop;
	}
	
	public int size() {
		if (removed) {
			throw new IllegalStateException();
		}
		return pts.length;
	}
	
	public double getTotalLength() {
		return totalLength;
	}
	
	public Vertex getStart() {
		if (removed) {
			throw new IllegalStateException("edge has been removed");
		}
		if (loop) {
			throw new IllegalStateException();
		}
		return start;
	}
	
	public Vertex getEnd() {
		if (removed) {
			throw new IllegalStateException();
		}
		if (loop) {
			throw new IllegalStateException();
		}
		return end;
	}
	
	public Point getPoint(int i) {
		if (removed) {
			throw new IllegalStateException();
		}
		if (i >= 0) {
			return pts[i];
		} else {
			return pts[i + pts.length];
		}
	}
	
	public void remove() {
		if (removed) {
			throw new IllegalStateException();
		}
		removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	/**
	 * returns the single shared vertex between edges a and b
	 */
	public static Vertex sharedVertex(Edge a, Edge b) {
		assert a != b;
		Vertex as = a.getStart();
		Vertex ae = a.getEnd();
		Vertex bs = b.getStart();
		Vertex be = b.getEnd();
		if (as == bs) {
			assert ae != be;
			return as;
		} else if (as == be) {
			assert ae != bs;
			return as;
		} else if (ae == bs) {
			assert as != be;
			return ae;
		} else if (ae == be) {
			assert as != bs;
			return be;
		} else {
			return null;
		}
	}
	
	public Vertex otherVertex(Vertex a) {
		if (a == start) {
			return end;
		} else if (a == end) {
			return start;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public static Edge commonEdge(Vertex a, Vertex b) {
		
		List<Edge> eds = a.getEdges();
		
		for (Edge e : eds) {
			if (e.getStart() == b || e.getEnd() == b) {
				return e;
			}
		}
		
		return null;
		
	}
	
	private void check() {
		
		assert pts.length >= 2;
		
		for (Point p : pts) {
			double x = p.getX();
			double y = p.getY();
			assert x == Math.rint(x);
			assert y == Math.rint(y);
		}
		
		if (loop) {
			assert start == end;
		} else {
			assert !(start == null || end == null);
			assert !start.isRemoved();
			assert !end.isRemoved();
		}
		
		for (int i = 0; i < pts.length; i++) {
			Point p = pts[i];
			int count = 0;
			for (Point q : pts) {
				if (Point.equals(p, q)) {
					count++;
				}
			}
			if (loop && (i == 0 || i == pts.length-1)) {
				assert count == 2;
			} else {
				assert count == 1;
			}
			
			if (i == 0) {
				if (loop) {
					if (start != null) {
						assert Point.equals(start.getPoint(), p);
						assert Point.equals(end.getPoint(), p);
					}
				} else {
					assert Point.equals(start.getPoint(), p);
				}
			} else if (i == pts.length-1) {
				if (loop) {
					if (end != null) {
						assert Point.equals(end.getPoint(), p);
					}
				} else {
					assert Point.equals(end.getPoint(), p);
				}
			}
		}
		
		checkColinearity();
	}
	
	private void checkColinearity() {
		assert !isRemoved();
		
		for (int i = 0; i < pts.length; i++) {
			Point p = pts[i];
			/*
			 * test point p for colinearity
			 */
			if (i == 0) {
				if (loop && (start == null && end == null)) {
					/*
					 * if loop, only check if stand-alone
					 * if not stand-alone, then it is possible to have first point be colinear
					 */
					assert Point.equals(p, pts[pts.length-1]);
					try {
						if (Point.colinear(pts[pts.length-2], p, pts[1])) {
							assert false : "Point " + p + " (index 0) is colinear";
						}
					} catch (ColinearException ex) {
						assert false;
					}
				}
			} else if (i == pts.length-1) {
				;
			} else {
				try {
					if (Point.colinear(pts[i-1], p, pts[i+1])) {
						assert false : "Point " + p + " (index " + i + ") is colinear";
					}
				} catch (ColinearException ex) {
					assert false;
				}
			}
		}
	}
}
