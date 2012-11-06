package com.gutabi.deadlock.core.graph;

import com.gutabi.deadlock.core.Point;

public abstract class EdgePosition extends GraphPosition {
	
	public abstract GraphPosition travelToConnectedVertex(Vertex v, double dist);
	
	public EdgePosition(Point p) {
		super(p);
	}
	
	public abstract int getIndex();
	
	public abstract double getParam();
	
//	public double distanceTo(EdgePosition p) {
//		
//		if (getEntity() == p.getEntity() && vs.equals(p.vs)) {
//			
//			return Math.abs(distanceToConnectedVertex(vs.get(0)) - p.distanceToConnectedVertex(p.vs.get(0)));
//			
//		} else {
//			
//			double bestDist = Double.POSITIVE_INFINITY;
//			for (Vertex v : vs) {
//				for (Vertex w : p.vs) {
//					double dist = distanceToConnectedVertex(v) + MODEL.world.distanceBetweenVertices(v, w) + p.distanceToConnectedVertex(w);
//					if (dist < bestDist) {
//						bestDist = dist;
//					}
//				}
//			}
//			
//			return bestDist;
//			
//		}
//		
//	}

}
