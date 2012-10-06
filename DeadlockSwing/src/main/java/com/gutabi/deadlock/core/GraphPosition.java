package com.gutabi.deadlock.core;

public abstract class GraphPosition extends Position {

	GraphPosition(Point p) {
		super(p);
	}
	
	public abstract double distanceTo(GraphPosition b);
	
	public abstract GraphPosition nextBoundToward(GraphPosition goal);
	
	public GraphPosition travel(GraphPosition p, double distance) {
		if (p instanceof Vertex) {
			return travelToV((Vertex)p, distance);
		} else {
			return travelToE((EdgePosition)p, distance);
		}
	}
	
	private GraphPosition travelToV(Vertex p, double distance) {
		
		if (this instanceof Vertex) {
			
			Vertex v = (Vertex)this;
			
			Edge e = Vertex.commonEdge(v, p);
			
			return v.travel(e, p, distance);
			
		} else {
			
			EdgePosition ep = (EdgePosition)this;
			
			assert ep.getEdge().getStart() == p || ep.getEdge().getEnd() == p;
			assert !ep.getEdge().isLoop();
			
			return ep.travel(p, distance);
		}
		
	}
	
	private GraphPosition travelToE(EdgePosition p, double distance) {
		
		if (this instanceof Vertex) {
			
			Vertex v = (Vertex)this;
			
			assert p.getEdge().getStart() == v || p.getEdge().getEnd() == v;
			assert !p.getEdge().isLoop();
			
			return v.travel(p.getEdge(), (p.getEdge().getStart() == v) ? p.getEdge().getEnd() : p.getEdge().getStart(), distance);
			
		} else {
			
			EdgePosition ep = (EdgePosition)this;
			
			assert p.getEdge() == ep.getEdge();
			
			if (ep.getIndex() < p.getIndex() || (ep.getIndex() == p.getIndex() && DMath.lessThan(ep.getParam(), p.getParam()))) {
				// ep -> p is same direction as edge
				return ep.travel(ep.getEdge().getEnd(), distance);
			} else {
				return ep.travel(ep.getEdge().getStart(), distance);
			}
		}
		
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof GraphPosition)) {
			throw new IllegalArgumentException();
		} else {
			if (this instanceof Vertex) {
				return ((Vertex)this).equalsP((GraphPosition)o);
			} else {
				return ((EdgePosition)this).equalsP((GraphPosition)o);
			}
		}
	}
	
}
