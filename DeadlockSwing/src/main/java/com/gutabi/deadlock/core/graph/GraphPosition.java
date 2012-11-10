package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;

public abstract class GraphPosition {
	
	public final Point p;
	public final Entity e;
	public final Axis a;
	
	/**
	 * vs should be set so that:
	 * vs.get(0) is the "reference" vertex
	 * vs.get(1) is the "other" vertex
	 */
	protected List<Vertex> vs;
	
	static Logger logger = Logger.getLogger(GraphPosition.class);
	
	public GraphPosition(Point p, Entity e, Axis a) {
		this.p = p;
		this.e = e;
		this.a = a;
	}
	
//	public abstract Entity getEntity();
//	
//	public abstract Axis getAxis();
	
//	public abstract GraphPosition travelFromConnectedVertex(Vertex v, double dist);
	
	public abstract double distanceToConnectedVertex(Vertex v);
	
	public double distanceTo(GraphPosition p) {
		
		if (e == p.e && a == p.a) {
			
			return Math.abs(distanceToConnectedVertex(vs.get(0)) - p.distanceToConnectedVertex(p.vs.get(0)));
			
		} else {
			
			double bestDist = Double.POSITIVE_INFINITY;
			for (Vertex v : vs) {
				for (Vertex w : p.vs) {
					double dist = 0.0;
					assert dist >= 0.0;
					dist += distanceToConnectedVertex(v);
					assert dist >= 0.0;
					dist += MODEL.world.distanceBetweenVertices(v, w);
					assert dist >= 0.0;
					dist += p.distanceToConnectedVertex(w);
					assert dist >= 0.0;
					if (dist < bestDist) {
						bestDist = dist;
					}
				}
			}
			
			assert bestDist >= 0.0;
			return bestDist;
			
		}
		
	}
	
	public abstract boolean isBound();
	
	public abstract GraphPosition nextBoundToward(GraphPosition goal);
	
	public abstract GraphPosition floor();
	
	public abstract GraphPosition ceiling();
	
	public GraphPosition travelTo(GraphPosition p, double distance) {
		
		double distanceToP = distanceTo(p);
		assert DMath.lessThanEquals(distance, distanceToP);
		
		if (DMath.equals(distance, 0.0)) {
			return this;
		}
		
		if (e == p.e && a == p.a) {
			
			double signedDistance = distanceToConnectedVertex(vs.get(0));
			double signedDistanceP = p.distanceToConnectedVertex(p.vs.get(0));
			
			assert DMath.lessThanEquals(distance, Math.abs(signedDistance - signedDistanceP));
			
			if (DMath.equals(Math.abs(signedDistance - signedDistanceP), distance)) {
				
				return p;
				
			} else {
				
				if (signedDistance > signedDistanceP) {
					return ((EdgePosition)this).travelToConnectedVertex(vs.get(0), distance);
				} else {
					return ((EdgePosition)this).travelToConnectedVertex(vs.get(1), distance);
				}
				
			}
			
		} else {
			
			Vertex bestVertex = null;
			Vertex bestVertexP = null;
			double bestDist = Double.POSITIVE_INFINITY;
			for (Vertex v : vs) {
				for (Vertex w : p.vs) {
					double dist = distanceToConnectedVertex(v) + MODEL.world.distanceBetweenVertices(v, w) + p.distanceToConnectedVertex(w);
					if (dist < bestDist) {
						bestVertex = v;
						bestVertexP = w;
						bestDist = dist;
					}
				}
			}
			
			assert DMath.lessThanEquals(distance, bestDist);
			
			if (DMath.equals(distance, bestDist)) {
				
				return p;
				
			} else if (DMath.equals(distance, distanceToConnectedVertex(bestVertex) + MODEL.world.distanceBetweenVertices(bestVertex, bestVertexP))) {
				
				return new VertexPosition(bestVertexP);
				
			} else if (DMath.greaterThan(distance, distanceToConnectedVertex(bestVertex) + MODEL.world.distanceBetweenVertices(bestVertex, bestVertexP))) {
				
				return ((Edge)p.e).travelFromConnectedVertex(bestVertexP, distance-(distanceToConnectedVertex(bestVertex) + MODEL.world.distanceBetweenVertices(bestVertex, bestVertexP)));
				
			} else if (DMath.equals(distance, distanceToConnectedVertex(bestVertex))) {
				
				return new VertexPosition(bestVertex);
				
			} else if (DMath.greaterThan(distance, distanceToConnectedVertex(bestVertex))) {
				
				return ((Edge)e).travelFromConnectedVertex(bestVertex, distance-(distanceToConnectedVertex(bestVertex)));
				
			} else {
				
				return ((EdgePosition)this).travelToConnectedVertex(bestVertex, distance);
				
			}
			
		}
		
	}
	
//	private GraphPosition travelToV(VertexPosition p, double distance) {
//		
//		if (this instanceof VertexPosition) {
//			
////			Vertex v = ((VertexPosition)this).v;
////			
////			Edge e = Vertex.commonEdge(v, p.v);
////			
////			GraphPosition traveled = ((VertexPosition)this).travel(e, p.v, distance);
////			
////			return traveled;
//			
//			throw new IllegalArgumentException();
//			
//		} else {
//			
//			RoadPosition ep = (RoadPosition)this;
//			
//			assert ep.r.start == p.v || ep.r.end == p.v;
//			assert !ep.r.isLoop();
//			
//			GraphPosition traveled = ep.travel(p, distance);
//			
//			return traveled;
//		}
//		
//	}
	
//	private GraphPosition travelToE(RoadPosition p, double distance) {
//		
//		if (this instanceof VertexPosition) {
//			
//			VertexPosition vp = (VertexPosition)this;
//			
//			assert p.r.start == vp.v || p.r.end == vp.v;
//			assert !p.r.isLoop();
//			
//			return vp.travel(p.r, (p.r.start == vp.v) ? p.r.end : p.r.start, distance);
//			
//		} else {
//			
//			RoadPosition ep = (RoadPosition)this;
//			
//			assert p.r == ep.r;
//			
//			if (ep.index < p.index || (ep.index == p.index && DMath.lessThan(ep.param, p.param))) {
//				// ep -> p is same direction as edge
//				return ep.travel(new VertexPosition(ep.r.end), distance);
//			} else {
//				return ep.travel(new VertexPosition(ep.r.start), distance);
//			}
//		}
//		
//	}
	
}
