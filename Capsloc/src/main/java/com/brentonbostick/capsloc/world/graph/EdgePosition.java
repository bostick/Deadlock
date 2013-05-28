package com.brentonbostick.capsloc.world.graph;

import com.brentonbostick.capsloc.math.DMath;
import com.brentonbostick.capsloc.math.Point;

public abstract class EdgePosition extends GraphPosition {
	
	public final Axis axis;
	
	public abstract GraphPosition travelToReferenceVertex(Axis a, double dist);
	
	public abstract GraphPosition travelToOtherVertex(Axis a, double dist);
	
	public abstract GraphPosition nextBoundTowardReferenceVertex();
	
	public abstract GraphPosition nextBoundTowardOtherVertex();
	
	public EdgePosition(Point p, Edge e, Axis axis) {
		super(p, e);
		this.axis = axis;
	}
	
	public abstract int getIndex();
	
	public abstract double getParam();
	
	public abstract double getCombo();
	
	public GraphPosition approachNeighbor(GraphPosition p, double distance) {
		
		if (DMath.equals(distance, 0.0)) {
			return this;
		}
		
		assert !equals(p);
		assert p.isBound();
		
		if (p instanceof VertexPosition) {
			
			if (((VertexPosition)p).v == ((Edge)entity).getReferenceVertex(axis)) {
				
				return travelToReferenceVertex(axis, distance);
				
			} else {
				assert ((VertexPosition)p).v == ((Edge)entity).getOtherVertex(axis);
				
				return travelToOtherVertex(axis, distance);
			}
			
		} else {
			EdgePosition pe = (EdgePosition)p;
			
			assert entity == pe.entity;
			assert axis == pe.axis;
			
			if (getCombo() < pe.getCombo()) {
				return travelToOtherVertex(axis, distance);
			} else {
				return travelToReferenceVertex(axis, distance);
			}
			
		}
		
	}
	
	public double goalGPPPCombo(int curPathIndex, double curPathParam, boolean pathForward, GraphPosition goalGP) {
		
		EdgePosition goalEP = (EdgePosition)goalGP;
		
		if (pathForward ? this.getCombo() < goalEP.getCombo() : this.getCombo() > goalEP.getCombo()) {
			// same direction as edge
			int retIndex = pathForward ? (DMath.equals(curPathParam, 0.0) ? curPathIndex : curPathIndex) : DMath.equals(curPathParam, 0.0) ? curPathIndex-1 : curPathIndex;
			double retParam = goalEP.getParam();
			
//			GraphPositionPathPosition ret = new GraphPositionPathPosition(debugPath, retIndex, retParam);
//			double distToRet = debugPos.lengthTo(ret);
//			assert DMath.equals(distToRet, debugDist);
			
			return retIndex+retParam;
			
		} else {
			int retIndex = pathForward ? (DMath.equals(curPathParam, 0.0) ? curPathIndex : curPathIndex) : DMath.equals(curPathParam, 0.0) ? curPathIndex-1 : curPathIndex;
			double retParam = pathForward ? (DMath.equals(curPathParam, 0.0) ? 1-goalEP.getParam() : 1-goalEP.getParam()) : DMath.equals(curPathParam, 0.0) ? 1-goalEP.getParam() : 1-goalEP.getParam();
			
//			GraphPositionPathPosition ret = new GraphPositionPathPosition(debugPath, retIndex, retParam);
//			double distToRet = debugPos.lengthTo(ret);
//			assert DMath.equals(distToRet, debugDist);
			
			return retIndex+retParam;
		}
		
	}
	
}
