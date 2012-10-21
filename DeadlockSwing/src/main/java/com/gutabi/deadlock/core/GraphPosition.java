package com.gutabi.deadlock.core;

import org.apache.log4j.Logger;

public abstract class GraphPosition extends Position {
	
	public final Graph graph;
	
	static Logger logger = Logger.getLogger(GraphPosition.class);
	
	GraphPosition(Point p, Graph graph) {
		super(p);
		this.graph = graph;
	}
	
	public abstract double distanceTo(GraphPosition b);
	
	public abstract GraphPosition nextBoundToward(GraphPosition goal);
	
	public GraphPosition travel(GraphPosition p, double distance) {
		if (p instanceof VertexPosition) {
			return travelToV((VertexPosition)p, distance);
		} else {
			return travelToE((EdgePosition)p, distance);
		}
	}
	
	private GraphPosition travelToV(VertexPosition p, double distance) {
		
		logger.debug("travelToV");
		
		if (this instanceof VertexPosition) {
			
			Vertex v = ((VertexPosition)this).getVertex();
			
			Edge e = Vertex.commonEdge(v, p.getVertex());
			
			GraphPosition traveled = ((VertexPosition)this).travel(e, p.getVertex(), distance);
			
			logger.debug("done travelToV");
			
			return traveled;
			
		} else {
			
			EdgePosition ep = (EdgePosition)this;
			
			assert ep.getEdge().getStart() == p.getVertex() || ep.getEdge().getEnd() == p.getVertex();
			assert !ep.getEdge().isLoop();
			
			GraphPosition traveled = ep.travel(p, distance);
			
			logger.debug("done travelToV");
			
			return traveled;
		}
		
	}
	
	private GraphPosition travelToE(EdgePosition p, double distance) {
		
		if (this instanceof VertexPosition) {
			
			VertexPosition vp = (VertexPosition)this;
			
			assert p.getEdge().getStart() == vp.getVertex() || p.getEdge().getEnd() == vp.getVertex();
			assert !p.getEdge().isLoop();
			
			return vp.travel(p.getEdge(), (p.getEdge().getStart() == vp.getVertex()) ? p.getEdge().getEnd() : p.getEdge().getStart(), distance);
			
		} else {
			
			EdgePosition ep = (EdgePosition)this;
			
			assert p.getEdge() == ep.getEdge();
			
			if (ep.getIndex() < p.getIndex() || (ep.getIndex() == p.getIndex() && DMath.lessThan(ep.getParam(), p.getParam()))) {
				// ep -> p is same direction as edge
				return ep.travel(new VertexPosition(ep.getEdge().getEnd(), graph), distance);
			} else {
				return ep.travel(new VertexPosition(ep.getEdge().getStart(), graph), distance);
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
			if (this instanceof VertexPosition) {
				return ((VertexPosition)this).equalsP((GraphPosition)o);
			} else {
				return ((EdgePosition)this).equalsP((GraphPosition)o);
			}
		}
	}
	
}
