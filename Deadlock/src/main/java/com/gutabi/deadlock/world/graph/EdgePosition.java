package com.gutabi.deadlock.world.graph;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;

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
	
	public GraphPosition travelToNeighbor(GraphPosition p, double distance) {
		
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

}
