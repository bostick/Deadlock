package com.gutabi.deadlock.core;

public class VertexPosition extends Position {

	private final Vertex v;
	
	public VertexPosition(Vertex v) {
		super(v.getPoint(), v);
		this.v = v;
	}
	
	public String toString() {
		return v.toString();
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
	
	public Vertex getVertex() {
		return v;
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
			if (DMath.doubleEquals(dist, totalEdgeLength)) {
				return new VertexPosition(dest);
			} else if (dist > totalEdgeLength) {
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

}
