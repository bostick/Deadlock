package com.gutabi.deadlock.core;


public class Position {
	
	public final Edge edge;
	public final int index;
	public final double param;
	public final Point point;
	
	public final Point segStart;
	public final Point segEnd;
	
	public Position(Edge e, int index, double param) {
		this.edge = e;
		this.index = index;
		this.param = param;
		
		segStart = e.getPoint(index);
		segEnd = e.getPoint(index+1);
		this.point = Point.point(segStart, segEnd, param);
		
	}
	
	public Position(Segment si, double param) {
		this(si.edge, si.index, param);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Position)) {
			return false;
		} else {
			Position b = (Position)o;
			return (edge == b.edge) && (index == b.index) && (param == b.param);
		}
	}
	
	public static Position middle(Position a, Position b) {
		return a.edge.middle(a, b);
	}
	
	public double distToEndOfEdge() {
		return edge.distToEndOfEdge(this);
	}
	
	public double distToStartOfEdge() {
		return edge.distToStartOfEdge(this);
	}
	
	public Position travelForward(double dist) throws TravelException {
		return edge.travelForward(this, dist);
	}
	
	public Position travelBackward(double dist) throws TravelException {
		return edge.travelBackward(this, dist);
	}
}
