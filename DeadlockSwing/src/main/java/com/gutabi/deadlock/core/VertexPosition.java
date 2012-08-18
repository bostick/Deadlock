package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.core.DMath.doubleEquals;

public class VertexPosition extends Position {
	
	public final Vertex v;
	
//	private final int index;
//	private final double param;
	
//	private final VertexPositionType type;
	
//	public VertexPosition(Vertex v) {
//		this(v, (v.getEdges().size() == 1) ? v.getEdges().get(0) : badEdge());
//	}
	
//	public VertexPosition(Vertex v, Edge e) {
//		this(v, e, (e.isLoop()) ? badVertexPositionType() : (v == e.getStart()) ? VertexPositionType.START : VertexPositionType.END);
//	}
//	
//	private static VertexPositionType badVertexPositionType() {
//		throw new IllegalArgumentException();
//	}
//	
//	private static Edge badEdge() {
//		throw new IllegalArgumentException();
//	}
	
	public VertexPosition(Vertex v) {
		super(v.getPoint());
		this.v = v;
		
//		this.type = type;
		
//		if (type == null) {
//			index = -1;
//			param = -1;
//		} else if (type == VertexPositionType.START) {
//			assert v == e.getStart();
//			index = 0;
//			param = 0.0;
//		} else {
//			assert v == e.getEnd();
//			index = e.size()-2;
//			param = 1.0;
//		}
		
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
	
//	public int getIndex() {
//		return index;
//	}
//	
//	public double getParam() {
//		return param;
//	}
//	
//	public VertexPositionType getType() {
//		return type;
//	}
	
	protected double distanceToV(VertexPosition a) {
		
		Vertex v = a.getVertex();
		
		if (!(v == this.v || Edge.commonEdge(v, this.v) != null)) {
			return Double.POSITIVE_INFINITY;
		}
		
		if (v == this.v) {
			for (Edge e : v.getEdges()) {
				if (e.isLoop()) {
					throw new IllegalArgumentException();
				}
			}
			
			return 0;
			
		} else {
			
			Edge e = Edge.commonEdge(v, this.v);
			
			if (e.isLoop()) {
				throw new IllegalArgumentException();
			}
			
			return e.getTotalLength();
		}
		
	}
	
	protected double distanceToE(EdgePosition a) {
		
		if (a.getEdge().isLoop()) {
			throw new IllegalArgumentException();
		}
		
		return a.distanceToV(this);
	}
	
	public Position travel(Edge e, double dist) {
		
		if (e.isLoop()) {
			throw new IllegalArgumentException();
		}
		
		if (doubleEquals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		double totalEdgeLength = e.getTotalLength();
		if (DMath.doubleEquals(dist, totalEdgeLength) || dist > totalEdgeLength) {
			throw new TravelException();
		}
		
		assert v == e.getStart() || v == e.getEnd();
		
		if (v == e.getStart()) {
			return travelForward(e, dist);
		} else {
			return travelBackward(e, dist);
		}
		
	}
	
	private Position travelForward(Edge e, double dist) throws TravelException {
		
		assert v == e.getStart();
		
		double totalLength = e.getTotalLength();
		if (DMath.doubleEquals(dist, totalLength)) {
			return new VertexPosition(e.getEnd());
		} else if (dist > totalLength) {
			throw new TravelException();
		}
		
		int index = 0;
		double param = 0.0;
		double distanceToTravel = dist;
		
		while (true) {
			Point a = e.getPoint(index);
			Point b = e.getPoint(index+1);
			
			Point c = Point.point(a, b, param);
			double distanceToEndOfSegment = Point.dist(c, b);
			
			if (doubleEquals(distanceToTravel, distanceToEndOfSegment)) {
				return new EdgePosition(e, index+1, 0.0);
			} else if (distanceToTravel < distanceToEndOfSegment) {
				double newParam = Point.travelForward(a, b, param, distanceToTravel);
				return new EdgePosition(e, index, newParam);
			} else {
				index++;
				param = 0.0;
				distanceToTravel -= distanceToEndOfSegment;
			}
		}
	}
	
	private Position travelBackward(Edge e, double dist) throws TravelException {
		
		assert v == e.getEnd();
		
		double totalLength = e.getTotalLength();
		if (DMath.doubleEquals(dist, totalLength)) {
			return new VertexPosition(e.getStart());
		} else if (dist > totalLength) {
			throw new TravelException();
		}
		
		int index = e.size()-2;
		double param = 1.0;
		double distanceToTravel = dist;
		
		while (true) {
			Point a = e.getPoint(index);
			Point b = e.getPoint(index+1);
			
			Point c = Point.point(a, b, param);
			double distanceToStartOfSegment = Point.dist(c, a);
			
			if (doubleEquals(distanceToTravel, distanceToStartOfSegment)) {
				return new EdgePosition(e, index, 0.0);
			} else if (distanceToTravel < distanceToStartOfSegment) {
				double newParam = Point.travelBackward(a, b, param, distanceToTravel);
				return new EdgePosition(e, index, newParam);
			} else {
				index--;
				param = 1.0;
				distanceToTravel -= distanceToStartOfSegment;
			}
		}
		
	}
	
	protected Position travelE(EdgePosition a, double dist) {
		
		double actual = distanceToE(a);
		
		assert DMath.doubleEquals(dist, actual) || dist < actual;
		
		Edge e = a.getEdge();
		
		return travel(e, dist);
		
	}
	
	protected Position travelV(VertexPosition a, double dist) {
		
		Vertex v = a.getVertex();
		
		double actual = distanceTo(a);
		
		if (DMath.doubleEquals(dist, actual)) {
			return a;
		} else {
			assert dist < actual;
			
			Edge e = Edge.commonEdge(v, this.v);
			
			return travel(e, dist);
			
		}
		
	}
	
}
