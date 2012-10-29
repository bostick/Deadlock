package com.gutabi.deadlock.core.graph;

@SuppressWarnings("serial")
public class SharedVerticesException extends RuntimeException {
	
	public Vertex v1;
	public Vertex v2;
	
	public SharedVerticesException(Vertex v1, Vertex v2) {
		this.v1 = v1;
		this.v2 = v2;
	}
	
}
