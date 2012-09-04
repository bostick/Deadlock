package com.gutabi.deadlock.core;

public class VertexPosition extends Position {

	private final Vertex v;
	
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
	public Position travel(Connector c, Vertex dest, double dist) {
		if (!(v.getEdges().contains(c) || v.getHubs().contains(c))) {
			throw new IllegalArgumentException();
		}
		if (DMath.doubleEquals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		if (c instanceof Edge) {
			Edge e = (Edge)c;
			
			double totalEdgeLength = e.getTotalLength();
			if (DMath.doubleEquals(dist, totalEdgeLength) || dist > totalEdgeLength) {
				throw new TravelException();
			}
			
			if (v == e.getStart()) {
				assert dest == e.getEnd();
				return EdgePosition.travelFromStart(e, dist);
			} else {
				assert v == e.getEnd();
				assert dest == e.getStart();
				return EdgePosition.travelFromEnd(e, dist);
			}
			
		} else {
			assert false;
			return null;
//			Hub h = (Hub)c;
//			
//			double totalHubLength = Point.distance(p, dest.getPoint());
//			if (DMath.doubleEquals(dist, totalHubLength) || dist > totalHubLength) {
//				throw new TravelException();
//			}
//			
//			travel;
		}
		
	}
	

//	private Position travelForward(Edge e, double dist) throws TravelException {
//		
//		int index = 0;
//		double param = 0.0;
//		double distanceToTravel = dist;
//		
//		while (true) {
//			Point a = e.getPoint(index);
//			Point b = e.getPoint(index+1);
//			
//			Point c = Point.point(a, b, param);
//			double distanceToEndOfSegment = Point.distance(c, b);
//			
//			if (DMath.doubleEquals(distanceToTravel, distanceToEndOfSegment)) {
//				return new EdgePosition(e, index+1, 0.0, e.getEnd());
//			} else if (distanceToTravel < distanceToEndOfSegment) {
//				double newParam = Point.travelForward(a, b, param, distanceToTravel);
//				return new EdgePosition(e, index, newParam, e.getEnd());
//			} else {
//				index++;
//				param = 0.0;
//				distanceToTravel -= distanceToEndOfSegment;
//			}
//		}
//	}
//	
//	private Position travelBackward(Edge e, double dist) throws TravelException {
//		
//		int index = e.size()-2;
//		double param = 1.0;
//		double distanceToTravel = dist;
//		
//		while (true) {
//			Point a = e.getPoint(index);
//			Point b = e.getPoint(index+1);
//			
//			Point c = Point.point(a, b, param);
//			double distanceToStartOfSegment = Point.distance(c, a);
//			
//			if (DMath.doubleEquals(distanceToTravel, distanceToStartOfSegment)) {
//				return new EdgePosition(e, index, 0.0, e.getStart());
//			} else if (distanceToTravel < distanceToStartOfSegment) {
//				double newParam = Point.travelBackward(a, b, param, distanceToTravel);
//				return new EdgePosition(e, index, newParam, e.getStart());
//			} else {
//				index--;
//				param = 1.0;
//				distanceToTravel -= distanceToStartOfSegment;
//			}
//		}
//		
//	}
	

}
