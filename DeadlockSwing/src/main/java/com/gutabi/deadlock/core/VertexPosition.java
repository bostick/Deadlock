package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

public class VertexPosition extends GraphPosition {
	
	private final Vertex v;
	
	public VertexPosition(Vertex v) {
		super(v.getPoint());
		this.v = v;
	}
	
	public Vertex getVertex() {
		return v;
	}
	
	public Hilitable getHilitable() {
		return v;
	}
	
	public boolean isBound() {
		return true;
	}
	
	public boolean equalsP(GraphPosition o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof VertexPosition)) {
			return false;
		} else {
			VertexPosition b = (VertexPosition)o;
			return (b.getVertex() == v);
		}
	}
	
	/**
	 * the specific way to travel
	 */
	public GraphPosition travel(Edge e, Vertex dest, double dist) {
		
		logger.debug("travel");
		
		if (!(v.getEdges().contains(e))) {
			throw new IllegalArgumentException();
		}
		if (DMath.equals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		double totalEdgeLength = e.getTotalLength();
		if (DMath.equals(dist, totalEdgeLength)) {
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
		
	}
	
	public double distanceTo(GraphPosition b) {
		if (b instanceof VertexPosition) {
			VertexPosition bb = (VertexPosition)b;
			
			double dist = MODEL.world.graph.distanceBetweenVertices(v, bb.v);
			
			assert DMath.greaterThanEquals(dist, 0.0);
			
			return dist;
		} else {
			EdgePosition bb = (EdgePosition)b;
			
			double bbStartPath = MODEL.world.graph.distanceBetweenVertices(v, bb.getEdge().getStart());
			double bbEndPath = MODEL.world.graph.distanceBetweenVertices(v, bb.getEdge().getEnd());
			
			double dist = Math.min(bbStartPath + bb.distanceToStartOfEdge(), bbEndPath + bb.distanceToEndOfEdge());
			
			assert DMath.greaterThanEquals(dist, 0.0);
			
			return dist;
		}
	}
	
	public GraphPosition nextBoundToward(GraphPosition goal) {
		
		if (goal instanceof EdgePosition) {
			EdgePosition ge = (EdgePosition)goal;
			
			if (v == ge.getEdge().getEnd()) {
				return EdgePosition.nextBoundfromEnd(ge.getEdge());
			} else {
				return EdgePosition.nextBoundfromStart(ge.getEdge());
			}
			
		} else {
			VertexPosition gv = (VertexPosition)goal;
			
			if (gv == this) {
				throw new IllegalArgumentException();
			}
			
			Edge e = Vertex.commonEdge(v, gv.getVertex());
			
			if (v == e.getEnd()) {
				return EdgePosition.nextBoundfromEnd(e);
			} else {
				return EdgePosition.nextBoundfromStart(e);
			}
			
		}
		
	}

}
