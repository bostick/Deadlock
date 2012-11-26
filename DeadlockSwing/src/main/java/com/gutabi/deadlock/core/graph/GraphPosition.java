package com.gutabi.deadlock.core.graph;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;

public abstract class GraphPosition {
	
	public final Point p;
	public final Entity entity;
	public final Axis axis;
	
	static Logger logger = Logger.getLogger(GraphPosition.class);
	
	public GraphPosition(Point p, Entity e, Axis a) {
		this.p = p;
		this.entity = e;
		this.axis = a;
	}
	
//	public abstract double distanceToConnectedVertex(Vertex v);
	
	
//	private Map<GraphPosition, Double> distMap = new HashMap<GraphPosition, Double>();
//	int hashedCount;
//	int unhashedCount;
	
//	public static double distanceToX(GraphPosition a, GraphPosition b) {
//		
//		assert !a.equals(b);
//		
//		if (a and b are neighbors) {
//			
//			
//			
//		} else if (a.entity == b.entity && a.axis == b.axis) {
//			
//			Vertex ref = a.entity.getReferenceVertex(a.axis);
//			
//			double dist = Math.abs(a.distanceToConnectedVertex(ref) - b.distanceToConnectedVertex(ref));
//			
//			return dist;
//			
//		} else {
//			
//			double bestDist = Double.POSITIVE_INFINITY;
//			
//			Vertex v0 = a.entity.getReferenceVertex(a.axis);
//			Vertex v1 = a.entity.getOtherVertex(a.axis);
//			Vertex w0 = b.entity.getReferenceVertex(b.axis);
//			Vertex w1 = b.entity.getOtherVertex(b.axis);
//			
//			double distanceToConnectedVertexV0 = a.distanceToConnectedVertex(v0);
//			double distanceToConnectedVertexV1 = a.distanceToConnectedVertex(v1);
//			double pDistanceToConnectedVertexW0 = b.distanceToConnectedVertex(w0);
//			double pDistanceToConnectedVertexW1 = b.distanceToConnectedVertex(w1);
//			
//			double dist = distanceToConnectedVertexV0;
//			dist += MODEL.world.distanceBetweenVertices(v0, w0);
//			dist += pDistanceToConnectedVertexW0;
//			if (dist < bestDist) {
//				bestDist = dist;
//			}
//			
//			dist = distanceToConnectedVertexV0;
//			dist += MODEL.world.distanceBetweenVertices(v0, w1);
//			dist += pDistanceToConnectedVertexW1;
//			if (dist < bestDist) {
//				bestDist = dist;
//			}
//			
//			dist = distanceToConnectedVertexV1;
//			dist += MODEL.world.distanceBetweenVertices(v1, w0);
//			dist += pDistanceToConnectedVertexW0;
//			if (dist < bestDist) {
//				bestDist = dist;
//			}
//			
//			dist = distanceToConnectedVertexV1;
//			dist += MODEL.world.distanceBetweenVertices(v1, w1);
//			dist += pDistanceToConnectedVertexW1;
//			if (dist < bestDist) {
//				bestDist = dist;
//			}
//			
//			assert bestDist >= 0.0;
//			
//			return bestDist;
//			
//		}
//		
//	}
	
	public abstract boolean isBound();
	
//	public abstract GraphPosition nextBoundToward(GraphPosition goal);
	
	
	
	public GraphPosition travelToNeighbor(GraphPosition p, double distance) {
		
		if (DMath.equals(distance, 0.0)) {
			return this;
		}
		
		assert !equals(p);
//		assert DMath.lessThanEquals(distance, GraphPosition.distanceTo(this, p));
		assert p.isBound();
		
		if (this instanceof VertexPosition) {
//			VertexPosition vv = (VertexPosition)this;
			
			if (p instanceof VertexPosition) {
				assert false;
				return null;
			} else {
				EdgePosition pe = (EdgePosition)p;
				
//				assert !(pe.entity instanceof Road && ((Road)pe.entity).isLoop());
				
				if (pe.getIndex() == 1) {
//					assert pe.isBound();
//					assert pe.getIndex() == 1;
					
					return ((Edge)pe.entity).travelFromReferenceVertex(pe.axis, distance);
							
					
				} else {
//					assert vv.v == pe.entity.getOtherVertex(pe.axis);
//					assert pe.isBound();
					assert pe.getIndex() == ((Edge)pe.entity).pointCount()-2;
					
					return ((Edge)pe.entity).travelFromOtherVertex(pe.axis, distance);
				}
			}
		} else {
			EdgePosition ee = (EdgePosition)this;
			
			if (p instanceof VertexPosition) {
//				VertexPosition pv = (VertexPosition)p;
				
//				assert !(ee.entity instanceof Road && ((Road)ee.entity).isLoop());
				
				if (DMath.lessThan(0.0, ee.getCombo()) && DMath.lessThanEquals(ee.getCombo(), 1.0)) {
//					assert ee.isBound();
//					assert ee.getIndex() == 1;
					
					return ee.travelToReferenceVertex(ee.axis, distance);
					
				} else {
//					assert pv.v == ee.entity.getOtherVertex(ee.axis);
//					assert ee.isBound();
					assert ee.getIndex() == ((Edge)ee.entity).pointCount()-2;
					
					return ee.travelToOtherVertex(ee.axis, distance);
				}
				
			} else {
				EdgePosition pe = (EdgePosition)p;
				
				assert ee.entity == pe.entity;
				assert ee.axis == pe.axis;
				
				if (ee.getCombo() < pe.getCombo()) {
					return ee.travelToOtherVertex(ee.axis, distance);
				} else {
					return ee.travelToReferenceVertex(ee.axis, distance);
				}
				
			}
		}
		
//		if (this and p are neighbors) {
//			
//			
//			
//		}
//		else if (entity == p.entity && axis == p.axis) {
//			
//			Vertex ref = entity.getReferenceVertex(axis);
//			Vertex other = entity.getOtherVertex(axis);
//			
//			double signedDistance = distanceToConnectedVertex(ref);
//			double signedDistanceP = p.distanceToConnectedVertex(ref);
//			
//			assert DMath.lessThanEquals(distance, Math.abs(signedDistance - signedDistanceP));
//			
//			if (DMath.equals(Math.abs(signedDistance - signedDistanceP), distance)) {
//				
//				return p;
//				
//			} else {
//				
//				if (signedDistance > signedDistanceP) {
//					return ((EdgePosition)this).travelToConnectedVertex(ref, distance);
//				} else {
//					return ((EdgePosition)this).travelToConnectedVertex(other, distance);
//				}
//				
//			}
//			
//		}
		
//		else {
//			
//			Vertex bestVertex = null;
//			Vertex bestVertexP = null;
//			double bestDist = Double.POSITIVE_INFINITY;
//			
//			Vertex v0 = entity.getReferenceVertex(axis);
//			Vertex v1 = entity.getOtherVertex(axis);
//			Vertex w0 = p.entity.getReferenceVertex(p.axis);
//			Vertex w1 = p.entity.getOtherVertex(p.axis);
//			
//			double distanceToConnectedVertexV0 = distanceToConnectedVertex(v0);
//			double distanceToConnectedVertexV1 = distanceToConnectedVertex(v1);
//			double pDistanceToConnectedVertexW0 = p.distanceToConnectedVertex(w0);
//			double pDistanceToConnectedVertexW1 = p.distanceToConnectedVertex(w1);
//			
//			double dist = distanceToConnectedVertexV0;
//			dist += MODEL.world.distanceBetweenVertices(v0, w0);
//			dist += pDistanceToConnectedVertexW0;
//			if (dist < bestDist) {
//				bestDist = dist;
//				bestVertex = v0;
//				bestVertexP = w0;
//			}
//			
//			dist = distanceToConnectedVertexV0;
//			dist += MODEL.world.distanceBetweenVertices(v0, w1);
//			dist += pDistanceToConnectedVertexW1;
//			if (dist < bestDist) {
//				bestDist = dist;
//				bestVertex = v0;
//				bestVertexP = w1;
//			}
//			
//			dist = distanceToConnectedVertexV1;
//			dist += MODEL.world.distanceBetweenVertices(v1, w0);
//			dist += pDistanceToConnectedVertexW0;
//			if (dist < bestDist) {
//				bestDist = dist;
//				bestVertex = v1;
//				bestVertexP = w0;
//			}
//			
//			dist = distanceToConnectedVertexV1;
//			dist += MODEL.world.distanceBetweenVertices(v1, w1);
//			dist += pDistanceToConnectedVertexW1;
//			if (dist < bestDist) {
//				bestDist = dist;
//				bestVertex = v1;
//				bestVertexP = w1;
//			}
//			
//			assert DMath.lessThanEquals(distance, bestDist);
//			
//			if (DMath.equals(distance, bestDist)) {
//				
//				return p;
//				
//			} else if (DMath.equals(distance, distanceToConnectedVertex(bestVertex) + MODEL.world.distanceBetweenVertices(bestVertex, bestVertexP))) {
//				
//				return new VertexPosition(bestVertexP);
//				
//			} else if (DMath.greaterThan(distance, distanceToConnectedVertex(bestVertex) + MODEL.world.distanceBetweenVertices(bestVertex, bestVertexP))) {
//				
//				return ((Edge)p.entity).travelFromConnectedVertex(bestVertexP, distance-(distanceToConnectedVertex(bestVertex) + MODEL.world.distanceBetweenVertices(bestVertex, bestVertexP)));
//				
//			} else if (DMath.equals(distance, distanceToConnectedVertex(bestVertex))) {
//				
//				return new VertexPosition(bestVertex);
//				
//			} else if (DMath.greaterThan(distance, distanceToConnectedVertex(bestVertex))) {
//				
//				return ((Edge)entity).travelFromConnectedVertex(bestVertex, distance-(distanceToConnectedVertex(bestVertex)));
//				
//			} else {
//				
//				return ((EdgePosition)this).travelToConnectedVertex(bestVertex, distance);
//				
//			}
//			
//		}
		
	}
	
}
