package com.brentonbostick.capsloc.world.graph;

import com.brentonbostick.capsloc.Entity;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;

public abstract class Edge extends Entity {
	
	public int id;
	
	public abstract int pointCount();
	
	public abstract double getTotalLength(Vertex a, Vertex b);
	
	public abstract Direction getDirection(Axis a);
	
	public abstract void setDirection(Axis a, Direction dir);
	
	
	public abstract Vertex getReferenceVertex(Axis a);
	
	public abstract Vertex getOtherVertex(Axis a);
	
	
	public abstract void enterDistancesMatrix(double[][] distances);
	
	public abstract boolean canTravelFromTo(Vertex a, Vertex b);
	
	public abstract GraphPosition travelFromReferenceVertex(Axis a, double dist);
	
	public abstract GraphPosition travelFromOtherVertex(Axis a, double dist);
	
	
	public abstract Entity decorationsHitTest(Point p);
	
	public abstract Entity decorationsIntersect(Object e);
	
	
//	public abstract String toFileString();
	
	public abstract void paintBorders(RenderingContext ctxt);
	
	public abstract void paint_panel(RenderingContext ctxt);
	
	public abstract void paint_preview(RenderingContext ctxt);
	
	public abstract void paintDecorations(RenderingContext ctxt);

}
