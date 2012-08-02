package com.gutabi.core;


public final class Edge {
		
	private final Point[] pts;
	private final Vertex start;
	private final Vertex end;
	
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
		
		check();
	}
	
	public String toString() {
		return "E n=" + pts.length + " " + start + " " + end;
	}
	
	public int size() {
		if (removed) {
			throw new IllegalStateException();
		}
		return pts.length;
	}
	
	public Vertex getStart() {
		if (removed) {
			throw new IllegalStateException("edge has been removed");
		}
		return start;
	}
	
	public Vertex getEnd() {
		if (removed) {
			throw new IllegalStateException();
		}
		return end;
	}
	
	public Point getPoint(int i) {
		if (removed) {
			throw new IllegalStateException();
		}
		return pts[i];
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
	
	private void check() {
		
		assert pts.length >= 2;
		
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
