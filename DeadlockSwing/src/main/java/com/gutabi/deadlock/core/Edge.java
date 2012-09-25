package com.gutabi.deadlock.core;


import java.util.Arrays;
import java.util.List;

public final class Edge implements Connector {
		
	private final Point[] pts;
	private final double[] cumulativeDistancesFromStart;
	private final Vertex start;
	private final Vertex end;
	
	private final double totalLength;
	
	private final boolean standalone;
	private final boolean loop;
	
	// the index for the last segment that is within the radius of the start vertex
//	private int startIndex;
	// the index for the first segment that is within the radius of the last vertex
//	private int endIndex;
	
	private boolean removed = false;
	
	private final int hash;
	
	int graphID;
	
	public Edge(Vertex start, Vertex end, List<Point> pts) {
		this.start = start;
		this.end = end;
		loop = (start == end);
		standalone = (loop) ? start == null : false;
		this.pts = pts.toArray(new Point[0]);
		
		cumulativeDistancesFromStart = new double[this.pts.length];
		
		double length;
		double l = 0.0;
		for (int i = 0; i < this.pts.length; i++) {
			if (i == 0) {
				cumulativeDistancesFromStart[i] = 0;
			} else {
				Point a  = this.pts[i-1];
				Point b = this.pts[i];
				length = Point.distance(a, b);
				l += length;
				cumulativeDistancesFromStart[i] = cumulativeDistancesFromStart[i-1] + length;
			}
		}
		
		totalLength = l;
		
		int h = 17;
		if (start != null) {
			h = 37 * h + start.hashCode();
		}
		if (end != null) {
			h = 37 * h + end.hashCode();
		}
		h = 37 * h + pts.hashCode();
		hash = h;
		
		check();
	}
	
//	public String toString() {
//		return "E " + hash + " " + start + " " + end + " n=" + pts.length;
//	}
	
	public int hashCode() {
		return hash;
	}
	
	public Edge copy() {
		if (removed) {
			throw new IllegalStateException();
		}
		return new Edge(start, end, Arrays.asList(pts));
	}
	
	public boolean isStandAlone() {
		return standalone;
	}
	
	public boolean isLoop() {
		return loop;
	}
	
	public int size() {
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
		if (i >= 0) {
			return pts[i];
		} else {
			return pts[i + pts.length];
		}
	}
	
//	public double getSegmentLengthX(int i) {
//		if (removed) {
//			throw new IllegalStateException();
//		}
//		return segmentLengths[i];
//	}
	
	/**
	 * segment index i
	 */
	public double getDistanceFromStart(int i) {
		if (removed) {
			throw new IllegalStateException();
		}
		return cumulativeDistancesFromStart[i];
	}
	
	public double getDistanceFromEnd(int i) {
		if (removed) {
			throw new IllegalStateException();
		}
		return totalLength - cumulativeDistancesFromStart[i];
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
				if (p.equals(q)) {
					count++;
				}
			}
			if (loop && (i == 0 || i == pts.length-1)) {
				assert count == 2;
			} else {
				assert count == 1;
			}
			
			if (i < pts.length-1) {
				Point q = pts[i+1];
				if (Math.abs(p.getX() - q.getX()) < 1.0E-3) {
					assert DMath.equals(p.getX(), q.getX());
				}
				if (Math.abs(p.getY() - q.getY()) < 1.0E-3) {
					assert DMath.equals(p.getY(), q.getY());
				}
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
