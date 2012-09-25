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
		} else if (p instanceof EdgePosition) {
			return travelToE((EdgePosition)p, distance);
		} else {
//			return travelToV(((SinkedPosition)p).getSink(), distance);
			throw new AssertionError();
		}
	}
	
	private GraphPosition travelToV(Vertex p, double distance) {
		
		if (this instanceof Vertex) {
			
			Vertex v = (Vertex)this;
			
			Connector c = Vertex.commonConnector(v, p);
			
			return v.travel(c, p, distance);
			
		} else if (this instanceof EdgePosition) {
			
			EdgePosition ep = (EdgePosition)this;
			
			assert ep.getEdge().getStart() == p || ep.getEdge().getEnd() == p;
			assert !ep.getEdge().isLoop();
			assert ep.getDest() == p;
			
			return ep.travel(p, distance);
		} else {
//			assert this instanceof SinkedPosition;
//			throw new IllegalStateException();
			throw new AssertionError();
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
			
			assert ep.getDest() == p.getDest();
			
			return ep.travel(ep.getDest(), distance);
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
			} else if (this instanceof EdgePosition) {
				return ((EdgePosition)this).equalsP((GraphPosition)o);
			} else {
//				return ((SinkedPosition)this).equalsP((Position)o);
				throw new AssertionError();
			}
		}
	}
	
}
