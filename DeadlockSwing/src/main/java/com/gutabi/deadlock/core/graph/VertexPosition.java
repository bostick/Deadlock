package com.gutabi.deadlock.core.graph;

import java.util.ArrayList;

import com.gutabi.deadlock.core.Entity;

public class VertexPosition extends GraphPosition {
	
	public final Vertex v;
	
	private final int hash;
	
	public VertexPosition(Vertex v) {
		super(v.p);
		this.v = v;
		
		int h = 17;
		h = 37 * h + v.hashCode();
		hash = h;
		
		vs = new ArrayList<Vertex>();
		vs.add(v);
		
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
	
	/**
	 * the specific way to travel
	 */
//	public GraphPosition travel(Road e, Vertex dest, double dist) {
//		
//		if (!(v.roads.contains(e))) {
//			throw new IllegalArgumentException();
//		}
//		if (DMath.equals(dist, 0.0)) {
//			return this;
//		}
//		if (dist < 0.0) {
//			throw new IllegalArgumentException();
//		}
//		
//		double totalRoadLength = e.getTotalLength();
//		if (DMath.equals(dist, totalRoadLength)) {
//			return new VertexPosition(dest);
//		} else if (dist > totalRoadLength) {
//			throw new IllegalArgumentException();
//		}
//		
//		if (v == e.start) {
//			assert dest == e.end;
//			return RoadPosition.travelFromStart(e, dist);
//		} else {
//			assert v == e.end;
//			assert dest == e.start;
//			return RoadPosition.travelFromEnd(e, dist);
//		}
//		
//	}
	
	public double distanceToConnectedVertex(Vertex v) {
		assert vs.contains(v);
		return 0.0;
	}
	
//	
//	public GraphPosition travelToConnectedVertex(Vertex v, double dist) {
//		throw new IllegalArgumentException();
//	}
	
//	public GraphPosition travelFromConnectedVertex(Vertex v, double dist) {
//		throw new IllegalArgumentException();
//	}
	
//	public double distanceTo(GraphPosition b) {
//		if (b instanceof VertexPosition) {
//			VertexPosition bb = (VertexPosition)b;
//			
//			double dist = MODEL.world.distanceBetweenVertices(v, bb.v);
//			
//			assert DMath.greaterThanEquals(dist, 0.0);
//			
//			return dist;
//		} else {
//			RoadPosition bb = (RoadPosition)b;
//			
//			double bbStartPath = MODEL.world.distanceBetweenVertices(v, bb.r.start);
//			double bbEndPath = MODEL.world.distanceBetweenVertices(v, bb.r.end);
//			
//			double dist = Math.min(bbStartPath + bb.distanceToStartOfRoad(), bbEndPath + bb.distanceToEndOfRoad());
//			
//			assert DMath.greaterThanEquals(dist, 0.0);
//			
//			return dist;
//		}
//	}
	
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
//			VertexPosition gv = (VertexPosition)goal;
//			
//			if (gv == this) {
//				throw new IllegalArgumentException();
//			}
//			
//			Edge e = Vertex.commonEdge(v, gv.v);
//			
//			if (v == e.end) {
//				return EdgePosition.nextBoundfromEnd(e);
//			} else {
//				return EdgePosition.nextBoundfromStart(e);
//			}
			throw new IllegalArgumentException();
			
		}
		
	}

}
