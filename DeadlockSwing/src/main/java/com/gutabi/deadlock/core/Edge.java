package com.gutabi.deadlock.core;


public final class Edge {
		
	private final Point[] pts;
	private final Vertex start;
	private final Vertex end;
	
	/*
	 * both vs could be null (stand-alone loop) or both could be a vertex (shared with other edges)
	 */
	private final boolean loop;
	
	private final double length;
	
	private boolean removed = false;
	
	public Edge(Vertex start, Vertex end, Point... pts) {
		this.start = start;
		this.end = end;
		loop = (start == end);
		this.pts = pts;
		
		double l = 0.0;
		for (int i = 0; i < pts.length-1;i++) {
			Point a = pts[i];
			Point b = pts[i+1];
			l += Point.dist(a, b);
		}
		length = l;
		
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
	
	public int size() {
		if (removed) {
			throw new IllegalStateException();
		}
		return pts.length;
	}
	
	public double length() {
		if (removed) {
			throw new IllegalStateException();
		}
		return length;
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
	
	public IntersectionInfo travel(int index, double param, double dist) throws TravelException {
		
		if (Point.doubleEquals(dist, 0.0)) {
			return new IntersectionInfo(this, index, param);
		}
		
		double distanceToTravel = dist;
		
		if (dist > 0.0) {
			
			DPoint a = pts[index].toDPoint();
			DPoint b = pts[index+1].toDPoint();
			
			DPoint c = Point.point(a, b, param);
			double distanceToEndOfSegment = Point.dist(c, b);
			if (Point.doubleEquals(distanceToTravel, distanceToEndOfSegment)) {
				
				if (index == size()-2) {
					throw new TravelException();
				} else {
					return new IntersectionInfo(this, index+1, 0.0);
				}
				
			} else if (distanceToTravel < distanceToEndOfSegment) {
				
				double newParam = Point.travel(a, b, param, distanceToTravel);
				
				return new IntersectionInfo(this, index, newParam);
				
			} else {
				
				if (index == size()-2) {
					throw new TravelException();
				} else {
					return travel(index+1, 0.0, distanceToTravel-distanceToEndOfSegment);
				}
				
			}
			
			
		} else {
			
			DPoint a = pts[index].toDPoint();
			DPoint b = pts[index+1].toDPoint();
			
			DPoint c = Point.point(a, b, param);
			double distanceToBeginningOfSegment = Point.dist(c, a);
			if (Point.doubleEquals(distanceToTravel, distanceToBeginningOfSegment)) {
				
				return new IntersectionInfo(this, index, 0.0);
				
			} else if (distanceToTravel < distanceToBeginningOfSegment) {
				
				double newParam = Point.travel(a, b, param, -distanceToTravel);
				
				return new IntersectionInfo(this, index, newParam);
				
			} else {
				
				if (index == 0) {
					throw new TravelException();
				} else {
					return travel(index-1, 1.0, -(distanceToTravel-distanceToBeginningOfSegment));
				}
				
			}
			
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
