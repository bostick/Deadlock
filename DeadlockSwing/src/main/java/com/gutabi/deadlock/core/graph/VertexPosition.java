package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;

public class VertexPosition extends GraphPosition {
	
	public final Vertex v;
	
//	public final List<StopSign> events;
	
	public VertexPosition(Vertex v) {
		super(v.p);
		this.v = v;
		
//		events = new ArrayList<StopSign>();
	}
	
	public Entity getEntity() {
		return v;
	}
	
	public boolean isBound() {
		return true;
	}
	
//	public List<StopSign> getEvents() {
//		return events;
//	}
	
	public String toString() {
		return v.toString();
	}
	
	public boolean equalsP(GraphPosition o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof VertexPosition)) {
			return false;
		} else {
			VertexPosition b = (VertexPosition)o;
			return (b.v == v);
		}
	}
	
	/**
	 * the specific way to travel
	 */
	public GraphPosition travel(Edge e, Vertex dest, double dist) {
		
		if (!(v.eds.contains(e))) {
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
			throw new IllegalArgumentException();
		}
		
		if (v == e.start) {
			assert dest == e.end;
			return EdgePosition.travelFromStart(e, dist);
		} else {
			assert v == e.end;
			assert dest == e.start;
			return EdgePosition.travelFromEnd(e, dist);
		}
		
	}
	
	public double distanceTo(GraphPosition b) {
		if (b instanceof VertexPosition) {
			VertexPosition bb = (VertexPosition)b;
			
			double dist = MODEL.world.distanceBetweenVertices(v, bb.v);
			
			assert DMath.greaterThanEquals(dist, 0.0);
			
			return dist;
		} else {
			EdgePosition bb = (EdgePosition)b;
			
			double bbStartPath = MODEL.world.distanceBetweenVertices(v, bb.e.start);
			double bbEndPath = MODEL.world.distanceBetweenVertices(v, bb.e.end);
			
			double dist = Math.min(bbStartPath + bb.distanceToStartOfEdge(), bbEndPath + bb.distanceToEndOfEdge());
			
			assert DMath.greaterThanEquals(dist, 0.0);
			
			return dist;
		}
	}
	
	public GraphPosition floor() {
		return this;
	}
	
	public GraphPosition ceiling() {
		return this;
	}
	
	public GraphPosition nextBoundToward(GraphPosition goal) {
		
		if (goal instanceof EdgePosition) {
			EdgePosition ge = (EdgePosition)goal;
			
			if (v == ge.e.end) {
				return EdgePosition.nextBoundfromEnd(ge.e);
			} else {
				return EdgePosition.nextBoundfromStart(ge.e);
			}
			
		} else {
			VertexPosition gv = (VertexPosition)goal;
			
			if (gv == this) {
				throw new IllegalArgumentException();
			}
			
			Edge e = Vertex.commonEdge(v, gv.v);
			
			if (v == e.end) {
				return EdgePosition.nextBoundfromEnd(e);
			} else {
				return EdgePosition.nextBoundfromStart(e);
			}
			
		}
		
	}

}
