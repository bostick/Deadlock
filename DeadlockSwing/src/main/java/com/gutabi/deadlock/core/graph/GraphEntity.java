package com.gutabi.deadlock.core.graph;

import com.gutabi.deadlock.core.Entity;

public abstract class GraphEntity extends Entity {
	
	/**
	 * vs should be set so that:
	 * vs.get(0) is the "reference" vertex
	 * vs.get(1) is the "other" vertex
	 */
	//public abstract List<Vertex> getVertices(Axis a);
	//public Vertex getReferenceVertex
	public abstract Vertex getReferenceVertex(Axis a);
	
	public abstract Vertex getOtherVertex(Axis a);
	
}
