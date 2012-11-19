package com.gutabi.deadlock.core.graph;

import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;

public abstract class Edge extends GraphEntity {
	
	public int id;
	
	public abstract int pointCount();
	
	public abstract double getTotalLength(Vertex a, Vertex b);
	
	public abstract EdgeDirection getDirection(Axis a);
	
	public abstract void setDirection(Axis a, EdgeDirection dir);
	
	public abstract void enterDistancesMatrix(double[][] distances);
	
	public abstract GraphPosition travelFromConnectedVertex(Vertex v, double dist);
	
	public abstract void paintBorders(Graphics2D g2);
	
	
	public abstract Entity decorationsHitTest(Point p);
	
	public abstract Entity decorationsBestHitTest(Shape s);
	
	
	public abstract void paintDecorations(Graphics2D g2);

}
