package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

public class Hub implements Connector {
	
	private final Point p;
	
	private final List<Vertex> vertices = new ArrayList<Vertex>();
	
	public Hub(Point p) {
		this.p = p;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public List<Vertex> getVertices() {
		return vertices;
	}
	
}
