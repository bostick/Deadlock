package com.gutabi.deadlock.core.graph;

import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Sweepable;

public abstract class Edge implements Entity, Sweepable {
	
	public int id;
	
	public abstract GraphPosition travelFromConnectedVertex(Vertex v, double dist);
	
	public abstract double getTotalLength(Vertex a, Vertex b);
	
	public abstract void enterDistancesMatrix(double[][] distances);
	
	public abstract void paintBorders(Graphics2D g2);
	
	
	public abstract Entity decorationsHitTest(Point p);
	
	public abstract void paintDecorations(Graphics2D g2);

}
