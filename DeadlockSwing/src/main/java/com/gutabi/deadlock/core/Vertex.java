package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

public class Vertex extends Driveable {
	
	private final Point p;
	
	private final int hash;
	
	private final List<Edge> eds = new ArrayList<Edge>();
	
	private boolean removed = false;
	
	int graphID;
	
	public Vertex(Point p) {
		this.p = p;
		
		int h = 17;
		h = 37 * h + p.hashCode();
		hash = h;
		
	}
	
	public int hashCode() {
		return hash;
	}
	
	public Vertex copy() {
		if (removed) {
			throw new IllegalStateException();
		}
		Vertex c = new Vertex(p);
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
	
	public static List<Edge> commonEdges(Vertex a, Vertex b) {
		
		List<Edge> eds1 = a.eds;
		List<Edge> eds2 = b.eds;
		
		List<Edge> common = new ArrayList<Edge>();
		for (Edge e1 : eds1) {
			for (Edge e2 : eds2) {
				if (e1 == e2) {
					common.add(e1);
				}
			}
		}
		
		return common;
	}
	
	public void addEdge(Edge ed) {
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
		return p;
	}
	
	public void remove() {
		assert !removed;
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
				 * loop with one intersection, so count of edges is 2 for this edge
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
