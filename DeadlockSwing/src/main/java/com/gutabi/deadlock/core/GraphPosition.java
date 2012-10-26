package com.gutabi.deadlock.core;

import org.apache.log4j.Logger;

public abstract class GraphPosition {
	
	public final Point p;
	
	static Logger logger = Logger.getLogger(GraphPosition.class);
	
	GraphPosition(Point p) {
		this.p = p;
	}
	
	public abstract Entity getEntity();
	
	public abstract double distanceTo(GraphPosition b);
	
	public abstract boolean isBound();
	
	public abstract GraphPosition nextBoundToward(GraphPosition goal);
	
	public abstract GraphPosition floor();
	
	public abstract GraphPosition ceiling();
	
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
				return ep.travel(new VertexPosition(ep.getEdge().getEnd()), distance);
			} else {
				return ep.travel(new VertexPosition(ep.getEdge().getStart()), distance);
			}
		}
		
	}
	
	/**
	 * returns:
	 * 0 if a, b are along the direction of the edge
	 * 1 if b, a are along the direction of the edge
	 * 
	 * exception otherwise
	 */
	public static int direction(GraphPosition a, GraphPosition b) {
		assert !a.equals(b);
		
		if (a instanceof EdgePosition) {
			EdgePosition aa = (EdgePosition)a;
			
			if (b instanceof EdgePosition) {
				EdgePosition bb = (EdgePosition)b;
				
				assert aa.e == bb.e;
				
				return (aa.index+aa.param < bb.index+bb.param)?0:1; 
				
			} else {
				VertexPosition bb = (VertexPosition)b;
				
				assert aa.e.start == bb.v || aa.e.end == bb.v;
				
				return (bb.v == aa.e.end)?0:1;
				
			}
		} else {
			VertexPosition aa = (VertexPosition)a;
			
			EdgePosition bb = (EdgePosition)b;
			
			assert bb.e.start == aa.v || bb.e.end == aa.v;
			
			return (aa.v == bb.e.start)?0:1;
		}
	}
	
}
