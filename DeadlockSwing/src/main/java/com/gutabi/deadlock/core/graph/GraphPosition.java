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
	
	static Logger logger = Logger.getLogger(GraphPosition.class);
	
	public GraphPosition(Point p, GraphEntity e, Axis a) {
		this.p = p;
		this.entity = e;
		this.axis = a;
	}
	
	public abstract double distanceToConnectedVertex(Vertex v);
	
	
//	private Map<GraphPosition, Double> distMap = new HashMap<GraphPosition, Double>();
//	int hashedCount;
//	int unhashedCount;
	
	public static double distanceTo(GraphPosition a, GraphPosition b) {
		
//		Double hashedDist = distMap.get(p);
		
//		if (hashedDist != null) {
//			hashedCount++;
//			return hashedDist;
//		} else {
//			unhashedCount++;
//		}
		
		assert !equals(p);
		
		if (this and p are neighbors) {
			
			
			
		} else if (entity == p.entity && axis == p.axis) {
			
//			List<Vertex> vs =entity.getVertices(axis);
			Vertex ref = entity.getReferenceVertex(axis);
			
			double dist = Math.abs(distanceToConnectedVertex(ref) - p.distanceToConnectedVertex(ref));
			
//			distMap.put(p, dist);
			
			return dist;
			
		} else {
			
			double bestDist = Double.POSITIVE_INFINITY;
			
			Vertex v0 = entity.getReferenceVertex(axis);
			Vertex v1 = entity.getOtherVertex(axis);
			Vertex w0 = p.entity.getReferenceVertex(p.axis);
			Vertex w1 = p.entity.getOtherVertex(p.axis);
			
			double distanceToConnectedVertexV0 = distanceToConnectedVertex(v0);
			double distanceToConnectedVertexV1 = distanceToConnectedVertex(v1);
			double pDistanceToConnectedVertexW0 = p.distanceToConnectedVertex(w0);
			double pDistanceToConnectedVertexW1 = p.distanceToConnectedVertex(w1);
			
			double dist = distanceToConnectedVertexV0;
			dist += MODEL.world.distanceBetweenVertices(v0, w0);
			dist += pDistanceToConnectedVertexW0;
			if (dist < bestDist) {
				bestDist = dist;
			}
			
			dist = distanceToConnectedVertexV0;
			dist += MODEL.world.distanceBetweenVertices(v0, w1);
			dist += pDistanceToConnectedVertexW1;
			if (dist < bestDist) {
				bestDist = dist;
			}
			
			dist = distanceToConnectedVertexV1;
			dist += MODEL.world.distanceBetweenVertices(v1, w0);
			dist += pDistanceToConnectedVertexW0;
			if (dist < bestDist) {
				bestDist = dist;
			}
			
			dist = distanceToConnectedVertexV1;
			dist += MODEL.world.distanceBetweenVertices(v1, w1);
			dist += pDistanceToConnectedVertexW1;
			if (dist < bestDist) {
				bestDist = dist;
			}
			
			assert bestDist >= 0.0;
			
//			distMap.put(p, bestDist);
			
			return bestDist;
			
		}
		
	}
	
	public abstract boolean isBound();
	
	public abstract GraphPosition nextBoundToward(GraphPosition goal);
	
	public GraphPosition travelTo(GraphPosition p, double distance) {
		
		if (DMath.equals(distance, 0.0)) {
			return this;
		}
		
		assert !equals(p);
		assert DMath.lessThanEquals(distance, GraphPosition.distanceTo(this, p));
		
		if (this and p are neighbors) {
			
			
			
		} else if (entity == p.entity && axis == p.axis) {
			
			Vertex ref = entity.getReferenceVertex(axis);
			Vertex other = entity.getOtherVertex(axis);
			
			double signedDistance = distanceToConnectedVertex(ref);
			double signedDistanceP = p.distanceToConnectedVertex(ref);
			
			assert DMath.lessThanEquals(distance, Math.abs(signedDistance - signedDistanceP));
			
			if (DMath.equals(Math.abs(signedDistance - signedDistanceP), distance)) {
				
				return p;
				
			} else {
				
				if (signedDistance > signedDistanceP) {
					return ((EdgePosition)this).travelToConnectedVertex(ref, distance);
				} else {
					return ((EdgePosition)this).travelToConnectedVertex(other, distance);
				}
				
			}
			
		} else {
			
			Vertex bestVertex = null;
			Vertex bestVertexP = null;
			double bestDist = Double.POSITIVE_INFINITY;
			
			Vertex v0 = entity.getReferenceVertex(axis);
			Vertex v1 = entity.getOtherVertex(axis);
			Vertex w0 = p.entity.getReferenceVertex(p.axis);
			Vertex w1 = p.entity.getOtherVertex(p.axis);
			
			double distanceToConnectedVertexV0 = distanceToConnectedVertex(v0);
			double distanceToConnectedVertexV1 = distanceToConnectedVertex(v1);
			double pDistanceToConnectedVertexW0 = p.distanceToConnectedVertex(w0);
			double pDistanceToConnectedVertexW1 = p.distanceToConnectedVertex(w1);
			
			double dist = distanceToConnectedVertexV0;
			dist += MODEL.world.distanceBetweenVertices(v0, w0);
			dist += pDistanceToConnectedVertexW0;
			if (dist < bestDist) {
				bestDist = dist;
				bestVertex = v0;
				bestVertexP = w0;
			}
			
			dist = distanceToConnectedVertexV0;
			dist += MODEL.world.distanceBetweenVertices(v0, w1);
			dist += pDistanceToConnectedVertexW1;
			if (dist < bestDist) {
				bestDist = dist;
				bestVertex = v0;
				bestVertexP = w1;
			}
			
			dist = distanceToConnectedVertexV1;
			dist += MODEL.world.distanceBetweenVertices(v1, w0);
			dist += pDistanceToConnectedVertexW0;
			if (dist < bestDist) {
				bestDist = dist;
				bestVertex = v1;
				bestVertexP = w0;
			}
			
			dist = distanceToConnectedVertexV1;
			dist += MODEL.world.distanceBetweenVertices(v1, w1);
			dist += pDistanceToConnectedVertexW1;
			if (dist < bestDist) {
				bestDist = dist;
				bestVertex = v1;
				bestVertexP = w1;
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
