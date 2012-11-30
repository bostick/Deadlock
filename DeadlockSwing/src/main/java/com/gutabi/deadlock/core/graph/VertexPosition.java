package com.gutabi.deadlock.core.graph;

import com.gutabi.deadlock.core.Entity;

public class VertexPosition extends GraphPosition {
	
	public final Vertex v;
	
	private int hash;
	
	public VertexPosition(Vertex v) {
		super(v.p, v);
		this.v = v;
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + v.hashCode();
			hash = h;
		}
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
		} else if (!(o instanceof VertexPosition)) {
			return false;
		} else {
			VertexPosition b = (VertexPosition)o;
			return (v == b.v);
		}
	}

}
