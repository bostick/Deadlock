package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

public class Vertex implements Driveable {
	
	private final Point p;
	
	private final int hash;
	
	protected final List<Edge> eds = new ArrayList<Edge>();
	protected final List<Hub> hubs = new ArrayList<Hub>();
	
	protected boolean removed = false;
	
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
	
	public static List<Connector> commonConnectors(Vertex a, Vertex b) {
		
//		List<Connector> eds1 = a.cons;
//		List<Connector> eds2 = b.cons;
		
		List<Connector> common = new ArrayList<Connector>();
		for (Edge e1 : a.eds) {
			for (Edge e2 : b.eds) {
				if (e1 == e2) {
					common.add(e1);
				}
			}
		}
		for (Hub h1 : a.hubs) {
			for (Hub h2 : b.hubs) {
				if (h1 == h2) {
					common.add(h1);
				}
			}
		}
		return common;
	}
	
	public static Connector commonConnector(Vertex a, Vertex b) {
		
		List<Connector> common = commonConnectors(a, b);
		
		if (common.size() != 1) {
			throw new IllegalArgumentException();
		} else {
			return common.get(0);
		}
		
	}
	
	public void addEdge(Edge e) {
		assert e != null;
		if (removed) {
			throw new IllegalStateException("vertex has been removed");
		}
//		if (!(con.getStart() == this && con.getEnd() == this)) {
//			assert !cons.contains(con);
//		}
		eds.add(e);
	}
	
	public void removeEdge(Edge e) {
		if (removed) {
			throw new IllegalStateException();
		}
		assert eds.contains(e);
		eds.remove(e);
	}
	
	public List<Edge> getEdges() {
		if (removed) {
			throw new IllegalStateException();
		}
		return eds;
	}
	
	public void addHub(Hub h) {
		if (removed) {
			throw new IllegalStateException("vertex has been removed");
		}
		hubs.add(h);
	}
	
	public List<Hub> getHubs() {
		if (removed) {
			throw new IllegalStateException();
		}
		return hubs;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public void remove() {
		assert !removed;
		assert eds.size() == 0;
		assert hubs.size() == 0;
		removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void check() {
		assert !isRemoved();
		assert getPoint() != null;
		
		int edgeCount = eds.size();
		
		assert edgeCount != 0;
		
		/*
		 * edgeCount cannot be 2, edges should just be merged
		 */
		assert edgeCount != 2;
		
		int count;
		for (Edge e : eds) {
			
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
