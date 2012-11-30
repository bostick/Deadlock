package com.gutabi.deadlock.core.graph;

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

}
