package com.gutabi.deadlock.core;

public class VertexPosition extends Position {

	public final Vertex v;
	
	public VertexPosition(Vertex v) {
		super(v.getPoint(), v);
		this.v = v;
	}
	
	public Vertex getVertex() {
		return v;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof VertexPosition)) {
			return false;
		} else {
			VertexPosition b = (VertexPosition)o;
			return (v == b.v);
		}
	}
	
	/**
	 * the specific way to travel
	 */
	public Position travel(Edge e, int dir, double dist) {

		if (DMath.doubleEquals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		double totalEdgeLength = e.getTotalLength();
		if (DMath.doubleEquals(dist, totalEdgeLength) || dist > totalEdgeLength) {
			throw new TravelException();
		}
		
		if (v == e.getStart() || v == e.getEnd()) {
			if (dir == 1) {
				return travelForward(e, dist);
			} else if (dir == -1) {
				return travelBackward(e, dist);
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			throw new TravelException();
		}
		
	}
	

	private Position travelForward(Edge e, double dist) throws TravelException {
		
		int index = 0;
		double param = 0.0;
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
	
	private Position travelBackward(Edge e, double dist) throws TravelException {
		
		int index = e.size()-2;
		double param = 1.0;
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
