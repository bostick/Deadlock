package com.gutabi.deadlock.core;

public class EdgePosition extends Position {
	
	private final Edge e;
	private final int index;
	private final double param;
	
	private final int dir;
	
	public final Point segStart;
	public final Point segEnd;
	
	private double distanceToStartOfEdge = -1;
	private double distanceToEndOfEdge = -1;
	
	public EdgePosition(Edge e, int index, double param) {
		this(e, index, param, 0);
	}
	
	public EdgePosition(Edge e, int index, double param, int dir) {
		super(Point.point(e.getPoint(index), e.getPoint(index+1), param), e);
		
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
		
		this.dir = dir;
		
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
	
	public int getDir() {
		return dir;
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
				return new VertexPosition(e.getEnd());
			} else if (dist > distToEndOfEdge) {
				throw new TravelException();
			}
			
			return travelForward(dist);
			
		} else if (dir == -1) {
			
			double distToStartOfEdge = distanceToStartOfEdge();
			if (DMath.doubleEquals(dist, distToStartOfEdge)) {
				return new VertexPosition(e.getStart());
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
				return new EdgePosition(e, index+1, 0.0, 1);
			} else if (distanceToTravel < distanceToEndOfSegment) {
				double newParam = Point.travelForward(a, b, param, distanceToTravel);
				return new EdgePosition(e, index, newParam, 1);
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
				return new EdgePosition(e, index, 0.0, -1);
			} else if (distanceToTravel < distanceToStartOfSegment) {
				double newParam = Point.travelBackward(a, b, param, distanceToTravel);
				return new EdgePosition(e, index, newParam, -1);
			} else {
				index--;
				param = 1.0;
				distanceToTravel -= distanceToStartOfSegment;
			}
		}
		
	}
	
}
