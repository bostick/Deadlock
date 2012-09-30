package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.List;

public class Vertex extends GraphPosition implements Driveable {
	
	private final int hash;
	
	protected final List<Edge> eds = new ArrayList<Edge>();
	
	protected boolean removed = false;
	
	int graphID;
	
	public Vertex(Point p) {
		super(p);
		
		int h = 17;
		h = 37 * h + p.hashCode();
		hash = h;
		
	}
	
	public Driveable getDriveable() {
		return this;
	}
	
	public int hashCode() {
		return hash;
	}
	
	public boolean isBound() {
		return true;
	}
	
	public GraphPosition nextBoundToward(GraphPosition goal) {
		
		if (goal instanceof EdgePosition) {
			EdgePosition ge = (EdgePosition)goal;
			
			if (this == ge.getEdge().getEnd()) {
				return EdgePosition.nextBoundfromEnd(ge.getEdge());
			} else {
				return EdgePosition.nextBoundfromStart(ge.getEdge());
			}
			
		} else {
			Vertex gv = (Vertex)goal;
			
			if (gv == this) {
				throw new IllegalArgumentException();
			}
			
			Edge e = (Edge)Vertex.commonConnector(this, gv);
			
			if (this == e.getEnd()) {
				return EdgePosition.nextBoundfromEnd(e);
			} else {
				return EdgePosition.nextBoundfromStart(e);
			}
			
		}
		
	}
	
	public boolean equalsP(GraphPosition o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Vertex)) {
			return false;
		} else {
			Vertex b = (Vertex)o;
			return (p.equals(b.p));
		}
	}
	
	public String toString() {
		return "V " + p;
	}
	
	
	
	/**
	 * the specific way to travel
	 */
	public GraphPosition travel(Connector c, Vertex dest, double dist) {
		if (!(eds.contains(c))) {
			throw new IllegalArgumentException();
		}
		if (DMath.equals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		Edge e = (Edge)c;
		
		double totalEdgeLength = e.getTotalLength();
		if (DMath.equals(dist, totalEdgeLength)) {
			return dest;
		} else if (dist > totalEdgeLength) {
			throw new TravelException();
		}
		
		if (this == e.getStart()) {
			assert dest == e.getEnd();
			return EdgePosition.travelFromStart(e, dist);
		} else {
			assert this == e.getEnd();
			assert dest == e.getStart();
			return EdgePosition.travelFromEnd(e, dist);
		}
		
	}
	
	public double distanceTo(GraphPosition b) {
		if (b instanceof Vertex) {
			Vertex bb = (Vertex)b;
			
			double dist = MODEL.world.graph.distanceBetweenVertices(this, bb);
			
			assert DMath.greaterThanEquals(dist, 0.0);
			
			return dist;
		} else {
			EdgePosition bb = (EdgePosition)b;
			
			double bbStartPath = MODEL.world.graph.distanceBetweenVertices(this, bb.getEdge().getStart());
			double bbEndPath = MODEL.world.graph.distanceBetweenVertices(this, bb.getEdge().getEnd());
			
			double dist = Math.min(bbStartPath + bb.distanceToStartOfEdge(), bbEndPath + bb.distanceToEndOfEdge());
			
			assert DMath.greaterThanEquals(dist, 0.0);
			
			return dist;
		}
	}
	
	
	
	public static List<Connector> commonConnectors(Vertex a, Vertex b) {
		
		if (a == b) {
			throw new IllegalArgumentException();
		}
		
		List<Connector> common = new ArrayList<Connector>();
		for (Edge e1 : a.eds) {
			for (Edge e2 : b.eds) {
				if (e1 == e2) {
					common.add(e1);
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
