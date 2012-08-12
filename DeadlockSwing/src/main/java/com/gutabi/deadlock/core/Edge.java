package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.core.DMath.doubleEquals;

import java.util.Comparator;


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
	
	public double distToEndOfEdge(Position pos) {
		assert pos.edge == this;
		
		//Point point = pos.point;
		double l = 0.0;
//		Point a = pts[index];
//		Point b = pts[index+1];
		l += Point.dist(pos.point, pos.segEnd);
		for (int i = pos.index+1; i < size()-1; i++) {
			Point a = getPoint(i);
			Point b = getPoint(i+1);
			l += Point.dist(a, b);
		}
		return l;
	}
	
	public double distToStartOfEdge(Position pos) {
		assert pos.edge == this;
		
		double l = 0.0;
//		Point a = pts[index];
//		Point b = pts[index+1];
		l += Point.dist(pos.point, pos.segStart);
		for (int i = pos.index-1; i >= 0; i--) {
			Point a = getPoint(i);
			Point b = getPoint(i+1);
			l += Point.dist(a, b);
		}
		return l;
	}
	
	public double distToPosition(Position a, Position b) {
		assert a.edge == this;
		assert b.edge == this;
		
		if (a.index == b.index) {
			return Point.dist(a.point, b.point);
		}
		
		if (posComparator.compare(a, b) == -1) {
			
			double l = 0.0;
			l += Point.dist(a.point, a.segEnd);
			for (int i = a.index+1; i < b.index; i++) {
				Point aa = getPoint(i);
				Point bb = getPoint(i+1);
				l += Point.dist(aa, bb);
			}
			l += Point.dist(b.segStart, b.point);
			return l;
			
		} else {
			
			double l = 0.0;
			l += Point.dist(a.point, a.segStart);
			for (int i = a.index-1; i > b.index; i--) {
				Point aa = getPoint(i);
				Point bb = getPoint(i+1);
				l += Point.dist(aa, bb);
			}
			l += Point.dist(b.segEnd, b.point);
			return l;
			
		}
		
	}
	
	public Position travelForward(Position pos, double dist) throws TravelException {
		assert pos.edge == this;
		
		if (dist == 0.0) {
			return pos;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		int index = pos.index;
		double param = pos.param;
		
		double distanceToTravel = dist;
		
		Point a = getPoint(index);
		Point b = getPoint(index+1);
		
		Point c = Point.point(a, b, param);
		double distanceToEndOfSegment = Point.dist(c, b);
		if (doubleEquals(distanceToTravel, distanceToEndOfSegment)) {
			
			if (index == size()-2) {
				throw new TravelException();
			} else {
				return new Position(this, index+1, 0.0);
			}
			
		} else if (distanceToTravel < distanceToEndOfSegment) {
			
			double newParam = Point.travelForward(a, b, param, distanceToTravel);
			
			return new Position(this, index, newParam);
			
		} else {
			
			if (index == size()-2) {
				throw new TravelException();
			} else {
				return travelForward(new Position(this, index+1, 0.0), distanceToTravel-distanceToEndOfSegment);
			}
			
		}
		
	}
	
	public Position travelBackward(Position pos, double dist) throws TravelException {
		assert pos.edge == this;
		
		if (dist == 0.0) {
			return pos;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		int index = pos.index;
		double param = pos.param;
		
		double distanceToTravel = dist;
		
		Point a = getPoint(index);
		Point b = getPoint(index+1);
		
		Point c = Point.point(a, b, param);
		double distanceToBeginningOfSegment = Point.dist(c, a);
		if (doubleEquals(distanceToTravel, distanceToBeginningOfSegment)) {
			
			return new Position(this, index, 0.0);
			
		} else if (distanceToTravel < distanceToBeginningOfSegment) {
			
			double newParam = Point.travelBackward(a, b, param, distanceToTravel);
			
			return new Position(this, index, newParam);
			
		} else {
			
			if (index == 0) {
				throw new TravelException();
			} else {
				return travelBackward(new Position(this, index-1, 1.0), distanceToTravel-distanceToBeginningOfSegment);
			}
			
		}
		
	}
	
	public Position middle(Position a, Position b) {
		assert a.edge == this;
		assert b.edge == this;
		
		double d = distToPosition(a, b);
		d = d/2;
		
		try {
			if (posComparator.compare(a, b) == -1) {
				return a.travelForward(d);
			} else {
				return a.travelBackward(d);
			}
		} catch (TravelException e) {
			throw new AssertionError();
		}
		
	}
	
	class PositionComparator implements Comparator<Position> {
		@Override
		public int compare(Position a, Position b) {
			if (a.edge != Edge.this || b.edge != Edge.this) {
				throw new IllegalArgumentException();
			}
			if (a.index < b.index) {
				return -1;
			} else if (a.index > b.index) {
				return 1;
			} else if (a.param < b.param) {
				return -1;
			} else if (a.param > b.param) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	public Comparator<Position> posComparator = new PositionComparator();
	
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
