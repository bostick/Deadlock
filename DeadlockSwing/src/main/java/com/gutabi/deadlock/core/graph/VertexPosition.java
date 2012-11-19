package com.gutabi.deadlock.core.graph;

import com.gutabi.deadlock.core.Entity;

public class VertexPosition extends GraphPosition {
	
	public final Vertex v;
	
	private final int hash;
	
	public VertexPosition(Vertex v) {
		super(v.p, v, Axis.NONE);
		this.v = v;
		
		int h = 17;
		h = 37 * h + v.hashCode();
		hash = h;
		
	}
	
	public int hashCode() {
		return hash;
	}
	
	public Entity getEntity() {
		return v;
	}
	
	public boolean isBound() {
		return true;
	}
	
	public String toString() {
		return v.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof GraphPosition)) {
			throw new IllegalArgumentException();
		} else if (!(o instanceof VertexPosition)) {
			return false;
		} else {
			VertexPosition b = (VertexPosition)o;
			return (v == b.v);
		}
	}
	
	public double distanceToConnectedVertex(Vertex v) {
		assert entity.getVertices(axis).contains(v);
		return 0.0;
	}
	
	public GraphPosition floor() {
		return this;
	}
	
	public GraphPosition ceiling() {
		return this;
	}
	
	public GraphPosition nextBoundToward(GraphPosition goal) {
		
		if (goal instanceof RoadPosition) {
			RoadPosition ge = (RoadPosition)goal;
			
			if (v == ge.r.end) {
				return RoadPosition.nextBoundfromEnd(ge.r);
			} else {
				return RoadPosition.nextBoundfromStart(ge.r);
			}
			
		} else if (goal instanceof MergerPosition) {
			MergerPosition me = (MergerPosition)goal;
			
			if (v == me.m.top) {
				return new VertexPosition(me.m.bottom);
			} else if (v == me.m.left) {
				return new VertexPosition(me.m.right);
			} else if (v == me.m.right) {
				return new VertexPosition(me.m.left);
			} else {
				assert v == me.m.bottom;
				return new VertexPosition(me.m.top);
			}
			
		} else {
			throw new IllegalArgumentException();
			
		}
		
	}

}
