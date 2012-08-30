package com.gutabi.deadlock.core;

import java.util.Arrays;
import java.util.List;

public final class Edge extends Driveable {
		
	private final Point[] pts;
	private final double[] segmentLengths;
	private final Vertex start;
	private final Vertex end;
	
	private final double totalLength;
	
	private final boolean standalone;
	private final boolean loop;
	
	private boolean removed = false;
	
	public Edge(Vertex start, Vertex end, List<Point> pts) {
		this.start = start;
		this.end = end;
		loop = (start == end);
		standalone = (loop) ? start == null : false;
		this.pts = pts.toArray(new Point[0]);
		
		segmentLengths = new double[this.pts.length-1];
		
		double length;
		double l = 0.0;
		for (int i = 0; i < segmentLengths.length; i++) {
			length = Point.distance(this.pts[i], this.pts[i+1]);
			segmentLengths[i] = length;
			l += length;
		}
		totalLength = l;
		
		check();
	}
	
	public Edge copy() {
		if (removed) {
			throw new IllegalStateException();
		}
		return new Edge(start, end, Arrays.asList(pts));
	}
	
	public String toString() {
		return "E n=" + pts.length + " " + start + " " + end;
	}
	
	public boolean isStandAlone() {
		return standalone;
	}
	
	public boolean isLoop() {
		return loop;
	}
	
	public int size() {
//		if (removed) {
//			throw new IllegalStateException();
//		}
		return pts.length;
	}
	
	public double getTotalLength() {
		return totalLength;
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
//		if (removed) {
//			throw new IllegalStateException();
//		}
		if (i >= 0) {
			return pts[i];
		} else {
			return pts[i + pts.length];
		}
	}
	
	public double getSegmentLength(int i) {
		if (removed) {
			throw new IllegalStateException();
		}
		return segmentLengths[i];
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
	
	public static boolean haveExactlyOneSharedIntersection(Edge a, Edge b) {
		Vertex as = a.getStart();
		Vertex ae = a.getEnd();
		Vertex bs = b.getStart();
		Vertex be = b.getEnd();
		if (as == bs) {
			return (ae != be);
		} else if (as == be) {
			return (ae != bs);
		} else if (ae == bs) {
			return (as != be);
		} else if (ae == be) {
			return (as != bs);
		} else {
			return false;
		}
	}
	
	public static boolean haveTwoSharedIntersections(Edge a, Edge b) {
		Vertex as = a.getStart();
		Vertex ae = a.getEnd();
		Vertex bs = b.getStart();
		Vertex be = b.getEnd();
		if (as == bs) {
			return (ae == be);
		} else if (as == be) {
			return (ae == bs);
		} else if (ae == bs) {
			return (as == be);
		} else if (ae == be) {
			return (as == bs);
		} else {
			return false;
		}
	}
	
	/**
	 * returns the single shared intersection between edges a and b
	 */
	public static Vertex sharedIntersection(Edge a, Edge b) throws SharedVerticesException {
		assert a != b;
		Vertex as = a.getStart();
		Vertex ae = a.getEnd();
		Vertex bs = b.getStart();
		Vertex be = b.getEnd();
		if (as == bs) {
			if (ae == be) {
				throw new SharedVerticesException(as, ae);
			}
			return as;
		} else if (as == be) {
			if (ae == bs) {
				throw new SharedVerticesException(as, ae);
			}
			return as;
		} else if (ae == bs) {
			if (as == be) {
				throw new SharedVerticesException(as, ae);
			}
			return ae;
		} else if (ae == be) {
			if (as == bs) {
				throw new SharedVerticesException(as, ae);
			}
			return be;
		} else {
			return null;
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
			assert p.isInteger();
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
