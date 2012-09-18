package com.gutabi.deadlock.core;

public abstract class Position {
	
	public abstract Point getPoint();
	
	public abstract Driveable getDriveable();
	
	public abstract double distanceTo(Position b);
	
	public Position travel(Position p, double distance) {
		if (p instanceof Vertex) {
			return travelToV((Vertex)p, distance);
		} else {
			return travelToE((EdgePosition)p, distance);
		}
	}
	
	private Position travelToV(Vertex p, double distance) {
		
		if (this instanceof Vertex) {
			
			Vertex v = (Vertex)this;
			
			Connector c = Vertex.commonConnector(v, p);
			
			return v.travel(c, p, distance);
			
		} else {
			
			EdgePosition ep = (EdgePosition)this;
			
			assert ep.getEdge().getStart() == p || ep.getEdge().getEnd() == p;
			assert !ep.getEdge().isLoop();
			assert ep.getDest() == p;
			
			return ep.travel(p, distance);
		}
		
	}
	
	private Position travelToE(EdgePosition p, double distance) {
		
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

}
