package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;

public abstract class GraphPosition {
	
	public final Point p;
	public final GraphEntity entity;
	public final Axis axis;
	
	private Map<GraphPosition, Double> distMap;
	
	static Logger logger = Logger.getLogger(GraphPosition.class);
	
	public GraphPosition(Point p, GraphEntity e, Axis a) {
		this.p = p;
		this.entity = e;
		this.axis = a;
		
		distMap = new HashMap<GraphPosition, Double>();
	}
	
	public abstract double distanceToConnectedVertex(Vertex v);
	
	
	int hashedCount;
	int unhashedCount;
	
	public double distanceTo(GraphPosition p) {
		
		Double hashedDist = distMap.get(p);
		
		if (hashedDist != null) {
			hashedCount++;
			return hashedDist;
		} else {
			unhashedCount++;
		}
		
		if (entity == p.entity && axis == p.axis) {
			
			List<Vertex> vs = entity.getVertices(axis);
			
			double toHash = Math.abs(distanceToConnectedVertex(vs.get(0)) - p.distanceToConnectedVertex(vs.get(0)));
			
			distMap.put(p, toHash);
			
			return toHash;
			
		} else {
			
			double bestDist = Double.POSITIVE_INFINITY;
			for (Vertex v : entity.getVertices(axis)) {
				for (Vertex w : p.entity.getVertices(p.axis)) {
					double dist = 0.0;
					dist += distanceToConnectedVertex(v);
					dist += MODEL.world.distanceBetweenVertices(v, w);
					dist += p.distanceToConnectedVertex(w);
					if (dist < bestDist) {
						bestDist = dist;
					}
				}
			}
			
			assert bestDist >= 0.0;
			
			distMap.put(p, bestDist);
			
			return bestDist;
			
		}
		
	}
	
	public abstract boolean isBound();
	
	public abstract GraphPosition nextBoundToward(GraphPosition goal);
	
	public GraphPosition travelTo(GraphPosition p, double distance) {
		
		if (DMath.equals(distance, 0.0)) {
			return this;
		}
		
		double distanceToP = distanceTo(p);
		assert DMath.lessThanEquals(distance, distanceToP);
		
		/*
		 * figure out the combinations that would make easier computing
		 */
		d;
		
		if (entity == p.entity && axis == p.axis) {
			
			List<Vertex> vs = entity.getVertices(axis);
			Vertex ref = vs.get(0);
			Vertex other = vs.get(1);
			
			/*
			 * if they are next to each other, then we can do less work
			 */
			if (this instanceof VertexPosition) {
				if (p instanceof VertexPosition) {
					
				} else {
					
				}
			} else {
				if (p instanceof VertexPosition) {
					
				} else {
					
				}
			}
			
			double signedDistance = distanceToConnectedVertex(vs.get(0));
			double signedDistanceP = p.distanceToConnectedVertex(vs.get(0));
			
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
			for (Vertex v : entity.getVertices(axis)) {
				for (Vertex w : p.entity.getVertices(p.axis)) {
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
				
				return ((Edge)p.entity).travelFromConnectedVertex(bestVertexP, distance-(distanceToConnectedVertex(bestVertex) + MODEL.world.distanceBetweenVertices(bestVertex, bestVertexP)));
				
			} else if (DMath.equals(distance, distanceToConnectedVertex(bestVertex))) {
				
				return new VertexPosition(bestVertex);
				
			} else if (DMath.greaterThan(distance, distanceToConnectedVertex(bestVertex))) {
				
				return ((Edge)entity).travelFromConnectedVertex(bestVertex, distance-(distanceToConnectedVertex(bestVertex)));
				
			} else {
				
				return ((EdgePosition)this).travelToConnectedVertex(bestVertex, distance);
				
			}
			
		}
		
	}
	
}
