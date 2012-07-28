package com.gutabi.deadlock.core;


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
	
//	public void setStart(Vertex v) {
//		if (removed) {
//			throw new IllegalStateException();
//		}
//		start = v;
//		if (start == end) {
//			loop = true;
//		}
//	}
//	
//	public void setEnd(Vertex v) {
//		if (removed) {
//			throw new IllegalStateException();
//		}
//		end = v;
//		if (start == end) {
//			loop = true;
//		}
//	}
//	
//	public void addPoint(Point p) {
//		if (removed) {
//			throw new IllegalStateException();
//		}
//		
//		/*
//		 * cannot simply test if points.indexOf(p) does not equal 0, since indexOf may return 0 but p may also occur later
//		 */
//		int count = 0;
//		for (Point a : points) {
//			if (p.equals(a)) {
//				count++;
//			}
//		}
//		if (count > 1 || (count == 1 && points.indexOf(p) != 0)) {
//			throw new IllegalStateException();
//		}
//		
//		points.add(p);
//	}
	
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
		assert !isRemoved();
		
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
				if (p.equals(q)) {
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
						assert start.getPoint().equals(p);
						assert end.getPoint().equals(p);
					}
				} else {
					assert start.getPoint().equals(p);
				}
			} else if (i == pts.length-1) {
				if (loop) {
					if (end != null) {
						assert end.getPoint().equals(p);
					}
				} else {
					assert end.getPoint().equals(p);
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
					assert p.equals(pts[pts.length-1]);
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
