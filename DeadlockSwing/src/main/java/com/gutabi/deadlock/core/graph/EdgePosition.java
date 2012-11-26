package com.gutabi.deadlock.core.graph;

import com.gutabi.deadlock.core.Point;

public abstract class EdgePosition extends GraphPosition {
	
	public abstract GraphPosition travelToReferenceVertex(Axis a, double dist);
	
	public abstract GraphPosition travelToOtherVertex(Axis a, double dist);
	
	public EdgePosition(Point p, Edge e, Axis a) {
		super(p, e, a);
	}
	
	public abstract int getIndex();
	
	public abstract double getParam();
	
	public abstract double getCombo();

}
