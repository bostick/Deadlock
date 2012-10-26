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
		
		if (this instanceof VertexPosition) {
			
			Vertex v = ((VertexPosition)this).v;
			
			Edge e = Vertex.commonEdge(v, p.v);
			
			GraphPosition traveled = ((VertexPosition)this).travel(e, p.v, distance);
			
			return traveled;
			
		} else {
			
			EdgePosition ep = (EdgePosition)this;
			
			assert ep.e.start == p.v || ep.e.end == p.v;
			assert !ep.e.isLoop();
			
			GraphPosition traveled = ep.travel(p, distance);
			
			return traveled;
		}
		
	}
	
	private GraphPosition travelToE(EdgePosition p, double distance) {
		
		if (this instanceof VertexPosition) {
			
			VertexPosition vp = (VertexPosition)this;
			
			assert p.e.start == vp.v || p.e.end == vp.v;
			assert !p.e.isLoop();
			
			return vp.travel(p.e, (p.e.start == vp.v) ? p.e.end : p.e.start, distance);
			
		} else {
			
			EdgePosition ep = (EdgePosition)this;
			
			assert p.e == ep.e;
			
			if (ep.index < p.index || (ep.index == p.index && DMath.lessThan(ep.param, p.param))) {
				// ep -> p is same direction as edge
				return ep.travel(new VertexPosition(ep.e.end), distance);
			} else {
				return ep.travel(new VertexPosition(ep.e.start), distance);
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
