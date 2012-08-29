package com.gutabi.deadlock.core;

public class EdgePosition extends Position {
	
	public final Edge e;
	public final int index;
	public final double param;
	
	public final Point segStart;
	public final Point segEnd;
	
	private double distanceToStartOfEdge = -1;
	private double distanceToEndOfEdge = -1;
	
	public EdgePosition(Edge e, int index, double param, Position prevPos, Edge prevDirEdge, int prevDir) {
		super(Point.point(e.getPoint(index), e.getPoint(index+1), param), e, prevPos, prevDirEdge, prevDir);
		
		if (index < 0 || index >= e.size()-1) {
			throw new IllegalArgumentException();
		}
		if (param < 0.0 || param >= 1.0) {
			throw new IllegalArgumentException();
		}
		if (index == 0 && DMath.doubleEquals(param, 0.0) && !e.isStandAlone()) {
			throw new IllegalArgumentException();
		}
		
		this.e = e;
		this.index = index;
		this.param = param;
		
		this.segStart = e.getPoint(index);
		this.segEnd = e.getPoint(index+1);
		
		double l;
		l = 0.0;
		l += Point.distance(p, segStart);
		for (int i = index-1; i >= 0; i--) {
			l += e.getSegmentLength(i);
		}
		distanceToStartOfEdge = l;
		
		distanceToEndOfEdge = e.getTotalLength() - distanceToStartOfEdge;
	}
	
	public Edge getEdge() {
		return e;
	}
	
	public int getIndex() {
		return index;
	}
	
	public double getParam() {
		return param;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof EdgePosition)) {
			return false;
		} else {
			EdgePosition b = (EdgePosition)o;
			return (e == b.e) && (index == b.index) && (param == b.param);
		}
	}
	
	public double distanceToEndOfEdge() {
		return distanceToEndOfEdge;
	}
	
	public double distanceToStartOfEdge() {
		return distanceToStartOfEdge;
	}
	
	protected double distanceForward(EdgePosition a) {
		if (a.index == index) {
			return Point.distance(p, a.p);
		}
		double l = 0.0;
		l += Point.distance(p, segEnd);
		for (int i = index+1; i < a.index; i++) {
			l += e.getSegmentLength(i);
		}
		l += Point.distance(a.segStart, a.p);
		return l;
	}
	
	protected double distanceBackward(EdgePosition a) {
		if (a.index == index) {
			return Point.distance(a.p, p);
		}
		double l = 0.0;
		l += Point.distance(p, segStart);
		for (int i = index-1; i > a.index; i--) {
			l += e.getSegmentLength(i);
		}
		l += Point.distance(a.segEnd, a.p);
		return l;
	}
	
	public double distanceToV(VertexPosition a) {
		
		Vertex v = a.getVertex();
		
		if (v == e.getStart() || v == e.getEnd()) {
			
			if (e.isLoop()) {
				double fDist = distanceToEndOfEdge();
				double bDist = distanceToStartOfEdge();
				return Math.min(fDist, bDist);
			} else {
				if (v == e.getEnd()) {
					return distanceToEndOfEdge();
				} else {
					return distanceToStartOfEdge();
				}
			}
			
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}
	
	public double distanceToE(EdgePosition a) {
		
		if (e == a.e) {
			if (e.isLoop()) {
				
				switch (Position.COMPARATOR.compare(this, a)) {
				case -1: {
					double fDist = distanceForward(a);
					double bDist = distanceToStartOfEdge() + a.distanceToEndOfEdge();
					return Math.min(fDist, bDist);
				}
				case 1: {
					double fDist = distanceToEndOfEdge() + a.distanceToStartOfEdge();
					double bDist = distanceBackward(a);
					return Math.min(fDist, bDist);
				}
				default:
					return 0.0;
				}
				
			} else {
				
				switch (Position.COMPARATOR.compare(this, a)) {
				case -1: {
					return distanceForward(a);
				}
				case 1: {
					return distanceBackward(a);
				}
				default:
					return 0.0;
				}
				
			}
		} else if (Edge.haveExactlyOneSharedIntersection(e, a.e)) {
			
			Vertex v = Edge.sharedIntersection(e, a.e);
			
			if (e.isLoop()) {
				double fDist = distanceToEndOfEdge();
				double bDist = distanceToStartOfEdge();
				if (fDist < bDist) {
					return fDist + new VertexPosition(v, this, e, 1).distanceTo(a);
				} else {
					return bDist + new VertexPosition(v, this, e, -1).distanceTo(a);
				}
			} else {
				if (v == e.getEnd()) {
					return distanceToEndOfEdge() + new VertexPosition(v, this, e, 1).distanceTo(a);
				} else {
					return distanceToStartOfEdge() + new VertexPosition(v, this, e, -1).distanceTo(a);
				}
			}
		} else if (Edge.haveTwoSharedIntersections(e, a.e)) {
			
			Vertex v1 = e.getEnd();
			Vertex v2 = e.getStart();
			
			double fDist = distanceToEndOfEdge() + new VertexPosition(v1, this, e, 1).distanceTo(a);
			double bDist = distanceToStartOfEdge() + new VertexPosition(v2, this, e, -1).distanceTo(a);
			return Math.min(fDist, bDist);
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}
	
	/**
	 * the specific way to travel
	 */
	public Position travel(int dir, double dist) {
		
		if (DMath.doubleEquals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		if (dir == 1) {
			
			double distToEndOfEdge = distanceToEndOfEdge();
			if (DMath.doubleEquals(dist, distToEndOfEdge)) {
				return new VertexPosition(e.getEnd(), this, e, 1);
			} else if (dist > distToEndOfEdge) {
				throw new TravelException();
			}
			
			return travelForward(dist);
			
		} else if (dir == -1) {
			
			double distToStartOfEdge = distanceToStartOfEdge();
			if (DMath.doubleEquals(dist, distToStartOfEdge)) {
				return new VertexPosition(e.getStart(), this, e, -1);
			} else if (dist > distToStartOfEdge) {
				throw new TravelException();
			}
			
			return travelBackward(dist);
			
		} else {
			throw new IllegalArgumentException();
		}
		
	}
	
	private Position travelForward(double dist) throws TravelException {
		
		int index = getIndex();
		double param = getParam();
		double distanceToTravel = dist;
		
		while (true) {
			Point a = e.getPoint(index);
			Point b = e.getPoint(index+1);
			
			Point c = Point.point(a, b, param);
			double distanceToEndOfSegment = Point.distance(c, b);
			
			if (DMath.doubleEquals(distanceToTravel, distanceToEndOfSegment)) {
				return new EdgePosition(e, index+1, 0.0, this, e, 1);
			} else if (distanceToTravel < distanceToEndOfSegment) {
				double newParam = Point.travelForward(a, b, param, distanceToTravel);
				return new EdgePosition(e, index, newParam, this, e, 1);
			} else {
				index++;
				param = 0.0;
				distanceToTravel -= distanceToEndOfSegment;
			}
		}
	}
	
	private Position travelBackward(double dist) throws TravelException {
		
		int index = getIndex();
		double param = getParam();
		double distanceToTravel = dist;
		
		while (true) {
			Point a = e.getPoint(index);
			Point b = e.getPoint(index+1);
			
			Point c = Point.point(a, b, param);
			double distanceToStartOfSegment = Point.distance(c, a);
			
			if (DMath.doubleEquals(distanceToTravel, distanceToStartOfSegment)) {
				return new EdgePosition(e, index, 0.0, this, e, -1);
			} else if (distanceToTravel < distanceToStartOfSegment) {
				double newParam = Point.travelBackward(a, b, param, distanceToTravel);
				return new EdgePosition(e, index, newParam, this, e, -1);
			} else {
				index--;
				param = 1.0;
				distanceToTravel -= distanceToStartOfSegment;
			}
		}
		
	}
	
}
