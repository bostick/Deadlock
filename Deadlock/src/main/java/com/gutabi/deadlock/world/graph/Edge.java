package com.gutabi.deadlock.world.graph;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.SweepableShape;
import com.gutabi.deadlock.ui.RenderingContext;

public abstract class Edge extends Entity {
	
	public int id;
	
	public abstract int pointCount();
	
	public abstract double getTotalLength(Vertex a, Vertex b);
	
	public abstract Direction getDirection(Axis a);
	
	public abstract void setDirection(Axis a, Direction dir);
	
	
	public abstract SweepableShape getShape();
	
	
	public abstract Vertex getReferenceVertex(Axis a);
	
	public abstract Vertex getOtherVertex(Axis a);
	
	
	public abstract void enterDistancesMatrix(double[][] distances);
	
	public abstract boolean canTravelFromTo(Vertex a, Vertex b);
	
	public abstract GraphPosition travelFromReferenceVertex(Axis a, double dist);
	
	public abstract GraphPosition travelFromOtherVertex(Axis a, double dist);
	
	
	public abstract Entity decorationsHitTest(Point p);
	
	public abstract Entity decorationsIntersect(Shape e);
	
	
	public abstract String toFileString();
	
	public abstract void paintBorders(RenderingContext ctxt);
	
	public abstract void paint(RenderingContext ctxt);
	
	public abstract void paintDecorations(RenderingContext ctxt);

}
