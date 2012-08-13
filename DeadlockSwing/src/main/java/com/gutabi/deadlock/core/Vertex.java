package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;


public class Vertex {
	
	private final Point p;
	
	private final List<Edge> eds = new ArrayList<Edge>();
	
	private VertexType type;
	
	private boolean removed = false;
	
	public Vertex(Point p) {
		this.p = p;
	}
	
	public Vertex copy() {
		if (removed) {
			throw new IllegalStateException();
		}
		Vertex c = new Vertex(p);
		c.type = type;
		
		/*
		 * not copying edge right now
		 * not needed for painting
		 * besides, this would get into a nasty mutually-recursive problem of copies not referring to the correct objects
		 */
		
		return c;
	}
	
	public String toString() {
		return "V " + p;
	}
	
	public void add(Edge ed) {
		assert ed != null;
		if (removed) {
			throw new IllegalStateException("vertex has been removed");
		}
		if (!(ed.getStart() == this && ed.getEnd() == this)) {
			assert !eds.contains(ed);
		}
		eds.add(ed);
	}
	
	public void removeEdge(Edge ed) {
		if (removed) {
			throw new IllegalStateException();
		}
		assert eds.contains(ed);
		eds.remove(ed);
	}
	
	public List<Edge> getEdges() {
		if (removed) {
			throw new IllegalStateException();
		}
		return eds;
	}
	
	public Point getPoint() {
		if (removed) {
			throw new IllegalStateException();
		}
		return p;
	}
	
	public VertexType getType() {
		return type;
	}
	
	public void setType(VertexType type) {
		this.type = type;
	}
	
	public void remove() {
		if (removed) {
			throw new IllegalStateException();
		}
		assert eds.size() == 0;
		removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void check() {
		assert !isRemoved();
		assert getPoint() != null;
		
		int edgeCount = getEdges().size();
		
		/*
		 * edgeCount cannot be 0, why have some free-floating vertex?
		 */
		assert edgeCount != 0;
		
		/*
		 * edgeCount cannot be 2, edges should just be merged
		 */
		assert edgeCount != 2;
		
		int count;
		for (Edge e : getEdges()) {
			
			assert !e.isRemoved();
			
			count = 0;
			for (Edge f : getEdges()) {
				if (e == f) {
					count++;
				}
			}
			if (e.getStart() == this && e.getEnd() == this) {
				/*
				 * loop with one vertex, so count of edges is 2 for this edge
				 */
				assert count == 2;
			} else {
				/*
				 * otherwise, all edges in v should be unique
				 */
				assert count == 1;
			}
		}
		
	}
	
}
