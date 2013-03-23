package com.gutabi.deadlock.world.graph;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;

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
	
	public double goalGPPPCombo(int curPathIndex, double curPathParam, boolean pathForward, GraphPosition goalGP, GraphPosition nextBoundGP) {
		
		EdgePosition goalEP = (EdgePosition)goalGP;
		
		if (pathForward ? this.getCombo() < goalEP.getCombo() : this.getCombo() > goalEP.getCombo()) {
			// same direction as edge
			int retIndex = pathForward ? (DMath.equals(curPathParam, 0.0) ? curPathIndex : curPathIndex) : DMath.equals(curPathParam, 0.0) ? curPathIndex-1 : curPathIndex;
			double retParam = pathForward ? (DMath.equals(curPathParam, 0.0) ? goalEP.getParam() : goalEP.getParam()) : DMath.equals(curPathParam, 0.0) ? goalEP.getParam() : goalEP.getParam();
			
			return retIndex+retParam;
			
		} else {
			int retIndex = pathForward ? (DMath.equals(curPathParam, 0.0) ? curPathIndex : curPathIndex) : DMath.equals(curPathParam, 0.0) ? curPathIndex-1 : curPathIndex;
			double retParam = pathForward ? (DMath.equals(curPathParam, 0.0) ? 1-goalEP.getParam() : 1-goalEP.getParam()) : DMath.equals(curPathParam, 0.0) ? 1-goalEP.getParam() : 1-goalEP.getParam();
			
			return retIndex+retParam;
		}
		
	}
	
//	public static double gpppComboX(int curPathIndex, double curPathParam, GraphPosition curGraphPosition, boolean forward, EdgePosition goal, EdgePosition nextBoundGP) {
//		
//		if (curGraphPosition instanceof EdgePosition) {
//			EdgePosition curPosE = (EdgePosition)curGraphPosition;
//			
//			if (forward ? curPosE.getCombo() < goal.getCombo() : curPosE.getCombo() > goal.getCombo()) {
//				// same direction as edge
//				int retIndex = forward ? (DMath.equals(curPathParam, 0.0) ? curPathIndex : curPathIndex) : DMath.equals(curPathParam, 0.0) ? curPathIndex-1 : curPathIndex;
//				double retParam = forward ? (DMath.equals(curPathParam, 0.0) ? goal.getParam() : goal.getParam()) : DMath.equals(curPathParam, 0.0) ? goal.getParam() : goal.getParam();
//				
//				return retIndex+retParam;
//				
//			} else {
//				int retIndex = forward ? (DMath.equals(curPathParam, 0.0) ? curPathIndex : curPathIndex) : DMath.equals(curPathParam, 0.0) ? curPathIndex-1 : curPathIndex;
//				double retParam = forward ? (DMath.equals(curPathParam, 0.0) ? 1-goal.getParam() : 1-goal.getParam()) : DMath.equals(curPathParam, 0.0) ? 1-goal.getParam() : 1-goal.getParam();
//				
//				return retIndex+retParam;
//			}
//			
//		} else {
//			assert curGraphPosition instanceof VertexPosition;
//			
//			Edge nextBoundEdge = (Edge)nextBoundGP.entity;
//			
//			Vertex curVertex = ((VertexPosition)curGraphPosition).v;
//			Vertex nextBoundRefVertex = nextBoundEdge.getReferenceVertex(nextBoundGP.axis);
//			Vertex nextBoundOtherVertex = nextBoundEdge.getOtherVertex(nextBoundGP.axis);
//			
//			if (forward ? curVertex == nextBoundRefVertex : curVertex == nextBoundOtherVertex) {
//				// same direction as edge
//				int retIndex = forward ? (DMath.equals(curPathParam, 0.0) ? curPathIndex : curPathIndex) : DMath.equals(curPathParam, 0.0) ? curPathIndex-1 : curPathIndex;
//				double retParam = forward ? (DMath.equals(curPathParam, 0.0) ? goal.getParam() : goal.getParam()) : DMath.equals(curPathParam, 0.0) ? goal.getParam() : goal.getParam();
//				
//				return retIndex+retParam;
//				
//			} else {
//				assert forward ? curVertex == nextBoundOtherVertex : curVertex == nextBoundRefVertex;
//				int retIndex = forward ? (DMath.equals(curPathParam, 0.0) ? curPathIndex : curPathIndex) : DMath.equals(curPathParam, 0.0) ? curPathIndex-1 : curPathIndex;
//				double retParam = forward ? (DMath.equals(curPathParam, 0.0) ? 1-goal.getParam() : 1-goal.getParam()) : DMath.equals(curPathParam, 0.0) ? 1-goal.getParam() : 1-goal.getParam();
//				
//				return retIndex+retParam;
//			}
//		}
//		
//	}
	
}
